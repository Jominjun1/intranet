import axios from 'axios'

const api = axios.create({
    baseURL: import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080',
    withCredentials: true,
})


api.interceptors.request.use(config => {
        // 쿠키 기반 인증 - withCredentials로 쿠키 자동 전송
        config.withCredentials = true
        return config
    },error => Promise.reject(error)
)
let isRefreshing = false
let failedQueue = []

function processQueue(error , token = null){
    failedQueue.forEach(prom =>{
        if(error){
            prom.reject(error)
        }else{
            prom.resolve(token)
        }
    })
    failedQueue=[]
}

api.interceptors.response.use(
    res => res,
    async (error) => {
        const originalRequest = error.config

        if (error.response?.status === 401 && !originalRequest._retry) {
            originalRequest._retry = true

            if (isRefreshing) {
                return new Promise((resolve, reject) => {
                    failedQueue.push({ resolve, reject })
                }).then(token => {
                    originalRequest.headers.Authorization = `Bearer ${token}`
                    return api(originalRequest)
                })
            }

            isRefreshing = true
            try {
                const refreshToken = sessionStorage.getItem('refreshToken')
                const res = await api.post('/user/refresh', { refreshToken })

                const newAccessToken = res.data.accessToken
                sessionStorage.setItem('jwt_token', newAccessToken)

                processQueue(null, newAccessToken)
                originalRequest.headers.Authorization = `Bearer ${newAccessToken}`
                return api(originalRequest)
            } catch (err) {
                processQueue(err, null)
                sessionStorage.clear()
                window.location.href = '/login' // or router.push('/login')
                return Promise.reject(err)
            } finally {
                isRefreshing = false
            }
        }

        return Promise.reject(error)
    }
)

export default api
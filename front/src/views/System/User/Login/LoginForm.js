import axios from 'axios'

export default {
    async findID(findIdData) {
        return axios.get('/user/findID', { params: findIdData })
    },
    async login(loginData) {
        return axios.post('/user/login', loginData)
    },

    async findPassword(findPasswordData) {
        return axios.put('/user/findPassword', findPasswordData)
    }
}
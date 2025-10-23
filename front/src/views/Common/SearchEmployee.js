import axios from 'axios'

export default {
    // 사용자 목록 조회
    async fetchAllUsers() {
        const response = await axios.get('/Admin/all-user')
        return response.data.body || []
    },

    // 부서 목록 조회
    async getDeptList() {
        const response = await axios.get('/user/getDeptList')
        if (response.data && response.data.body) {
            return Array.isArray(response.data.body) ? response.data.body : []
        } else {
            return Array.isArray(response.data) ? response.data : []
        }
    },

    // 사용자 수정/삭제
    async updateUser(userId, userData) {
        return axios.put(`/Admin/update/${userId}`, userData)
    },

    // 비밀번호 변경
    async changePassword(loginId, passwordData) {
        return axios.put(`/Admin/changePassword/${loginId}`, passwordData)
    },

    // 사용자 생성
    async createUser(userData) {
        return axios.post('/Admin/createUser', userData)
    }
}

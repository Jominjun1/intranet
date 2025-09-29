import axios from 'axios'

export default {
    // 사용자 등록
    async createUser(user) {
        return axios.post('/Admin/createUser', user)
    },

    // 사용자 정보 수정
    async updateUser(userId, userForm) {
        return axios.put(`/Admin/update/${userId}`, userForm)
    },

    // 비밀번호 변경
    async changePWD(loginId, passwordObj) {
        return axios.put(`/Admin/changePassword/${loginId}`, passwordObj)
    },

    // 전체 사용자 조회
    async fetchAllUsers() {
        const res = await axios.get('/Admin/all-user')
        return res.data.body || []
    },

    // 사용자 삭제 (stat N으로 변경)
    async deleteUser(userId, user) {
        return axios.put(`/Admin/update/${userId}`, { ...user, user_stat: 'N' })
    },

    // 부서 목록 조회
    async fetchDeptList() {
        const res = await axios.get('/user/getDeptList')
        return res.data?.body || []
    }
}

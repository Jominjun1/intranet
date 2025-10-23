import axios from 'axios'

export default {
    // 프로젝트 검색
    async searchProjects(params) {
        const res = await axios.get('/project/searchAll', { params })
        return res.data.body ?? []
    },

    // 부서 목록 조회
    async getDeptList() {
        const response = await axios.get('/user/getDeptList')
        if (response.data && response.data.body) {
            return Array.isArray(response.data.body) ? response.data.body : []
        } else {
            return Array.isArray(response.data) ? response.data : []
        }
    }
}

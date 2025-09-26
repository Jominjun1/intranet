import axios from 'axios'

export default {
    async getDepts() {
        const res = await axios.get('/user/getDeptList')
        if (res.data && typeof res.data === 'object' && res.data.body !== undefined) {
            return res.data.body
        }
        return Array.isArray(res.data) ? res.data : []
    },

    async createDept(dept) {
        return axios.post('/Admin/createDept', dept)
    },

    async updateDept(deptCode, deptData) {
        return axios.put(`/Admin/changeDept/${deptCode}`, deptData)
    },

    async deleteDept(deptCode, deptName) {
        return axios.put(`/Admin/changeDept/${deptCode}`, {
            dept: deptName,
            status: 'N'
        })
    }
}
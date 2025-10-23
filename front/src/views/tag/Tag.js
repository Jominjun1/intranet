import axios from 'axios'

// 태그 리스트 조회
export default {
    async getTagList(params) {

        const res = await axios.get('/tags/getTagList', {params})
        return res.data.body ?? res.data
    },

    // 처리단계
    async getProcStep(ordNo) {
        const res = await axios.get(`/tags/proc_step_${ordNo}`)
        return res.data.body ?? res.data
    },

    async updateProcStep(ordNo, data) {
        return axios.put(`/tags/update_proc_step_${ordNo}`, data)
    },

    // 세팅정보
    async getSettingInfo(ordNo) {
        const res = await axios.get(`/tags/setting_info_${ordNo}`)
        let data = res.data.body ?? res.data
        return Array.isArray(data) ? data : [data]
    },

    async updateSetting(ordNo, data) {
        return axios.put(`/tags/update_setting_${ordNo}`, data)
    },

    // 버전 이력
    async getVersionHistory(ordNo) {
        const res = await axios.get(`/tags/version-history/${ordNo}`)
        return res.data.body ?? res.data ?? []
    },

    // 공통정보
    async getCommonHistory(ordNo) {
        const res = await axios.get(`/tags/common_history_${ordNo}`)
        let data = res.data.body ?? res.data
        return Array.isArray(data) ? data : [data]
    },

    async updateCommon(ordNo, data) {
        return axios.put(`/tags/update_common_${ordNo}`, data)
    },

    // AS 이력
    async getAsHistory(ordNo) {
        const res = await axios.get(`/tags/prod_as_${ordNo}`)
        const data = res.data.body ?? res.data
        return Array.isArray(data) ? data : [data]
    },

    async saveAs(ordNo, formData, isEdit = false) {
        if (isEdit) {
            return axios.put(`/tags/update_${ordNo}_as`, formData)
        } else {
            return axios.post(`/tags/${ordNo}_as`, formData)
        }
    },


    async deleteAs(asId) {
        return axios.delete(`/tags/delete_as/${asId}`)
    }
}
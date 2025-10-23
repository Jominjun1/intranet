import axios from 'axios'

export default {
    // 태그 목록 조회
    async getTagList(params) {
        const res = await axios.get('/tags/getTagList', { params })
        let responseData = res.data
        if (responseData && typeof responseData === 'object' && responseData.body !== undefined) {
            responseData = responseData.body
        }
        return Array.isArray(responseData) ? responseData : []
    },

    // 처리단계 조회
    async getProcStep(ordNo) {
        const res = await axios.get(`/tags/proc_step_${ordNo}`)
        return res.data.body || res.data
    },

    // 처리단계 수정
    async updateProcStep(ordNo, data) {
        return axios.put(`/tags/update_proc_step_${ordNo}`, data)
    },

    // 설정정보 조회
    async getSettingInfo(ordNo) {
        const res = await axios.get(`/tags/setting_info_${ordNo}`)
        let data = res.data.body || res.data
        return Array.isArray(data) ? data : [data]
    },

    // 설정정보 수정
    async updateSetting(ordNo, data) {
        return axios.put(`/tags/update_setting_${ordNo}`, data)
    },

    // 버전 이력 조회
    async getVersionHistory(ordNo) {
        const res = await axios.get(`/tags/version-history/${ordNo}`)
        return res.data.body || res.data || []
    },

    // 버전 수정
    async updateVersion(ordNo, data) {
        return axios.put(`/tags/update_version_${ordNo}`, data)
    },

    // 공통정보 조회
    async getCommonHistory(ordNo) {
        const res = await axios.get(`/tags/common_history_${ordNo}`)
        let data = res.data.body || res.data
        return Array.isArray(data) ? data : [data]
    },

    // 공통정보 수정
    async updateCommon(ordNo, data) {
        return axios.put(`/tags/update_common_${ordNo}`, data)
    },

    // AS 이력 조회
    async getAsHistory(ordNo) {
        const res = await axios.get(`/tags/prod_as_${ordNo}`)
        const data = res.data.body || res.data
        return Array.isArray(data) ? data : [data]
    },

    // AS 생성
    async createAs(ordNo, formData) {
        return axios.post(`/tags/${ordNo}_as`, formData)
    },

    // AS 수정
    async updateAs(ordNo, formData) {
        return axios.put(`/tags/update_${ordNo}_as`, formData)
    },

    // AS 삭제
    async deleteAs(asId) {
        return axios.delete(`/tags/delete_as/${asId}`)
    },

    // 태그번호 검색
    async searchTagNumbers(query) {
        const res = await axios.get('/tags/tag-numbers', { params: { query } })
        return res.data.body || res.data || []
    }
}

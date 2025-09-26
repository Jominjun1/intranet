import axios from 'axios'

export default {
    async fetchLogs(params) {
        try {
            const response = await axios.get('/Log/getLog', { params })
            let data = response.data

            if (data && typeof data === 'object' && data.body !== undefined) {
                data = data.body
            }

            return Array.isArray(data) ? data : []
        } catch (error) {
            console.error('API fetchLogs error:', error)
            throw error
        }
    }
}
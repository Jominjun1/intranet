import axios from 'axios'

export default {
    // 태그번호 검색
    async searchTagNumbers(query) {
        const res = await axios.get('/tags/tag-numbers', { params: { query } })
        return res.data.body || res.data || []
    }
}

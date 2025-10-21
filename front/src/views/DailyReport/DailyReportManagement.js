import axios from 'axios';

export default {

    // dailyReportManagement

    async fetchReportsByDate(date) {
        const res = await axios.get('/daily/list', {params: {date}});
        return res.data;
    },

    async createReport(payload) {
        return axios.post('/daily/create', payload);
    },

    async updateReport(id, payload) {
        return axios.put(`/daily/update/${id}`, payload);
    },

    async deleteReport(id){
        return axios.put(`/daily/delete/${id}`);
    }


    // effortManagement

}

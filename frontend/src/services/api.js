import axios from 'axios' //Axios omogoca Reactu posiljanje zahtev na backend (GET, POST, PUT, DELETE)

const api = axios.create({
    baseURL: 'http://localhost:8080/api/stroski', //sem React posilja zahteve, to je naslov backenda
})

export default api
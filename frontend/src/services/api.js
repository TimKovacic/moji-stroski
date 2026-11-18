import axios from 'axios' //Axios omogoca Reactu posiljanje zahtev na backend (GET, POST, PUT, DELETE)

//povezava na stroske
const api = axios.create({
    baseURL: 'http://localhost:8080/api/stroski', //sem React posilja zahteve, to je naslov backenda
})

// Povezava za proračune
export const proracunApi = axios.create({
    baseURL: 'http://localhost:8080/api/proracuni',
})

export default api
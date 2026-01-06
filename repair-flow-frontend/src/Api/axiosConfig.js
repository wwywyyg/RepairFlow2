import axios from "axios";

const API_BASE = import.meta.env.VITE_API_BASE_URL || "http://localhost:8080";
// create an axios instance
const api = axios.create({
    baseURL: API_BASE,
    timeout: 5000,
    headers: {
        "Content-Type": "application/json",
    },
});

// set the token in the request header
api.interceptors.request.use((config) => {
    const token = localStorage.getItem("token");
    if (token) {
        // for spring boot security jwt token header
        config.headers['Authorization'] = `Bearer ${token}`;
    }
    return config;
    },
        (error) => {
        return Promise.reject(error);
    }   
);

//Response Interceptor deal with global error
api.interceptors.response.use(
    (response) => response,
    (error) => {

        if (error.response && error.response.status === 401) {
            console.warn("Token expired, redirecting to login...");
            localStorage.removeItem("token");
            // window.location.href = "/login";
        }

        if(!error.response){
            console.error("Network Error",error.message);
        }

        return Promise.reject(error);
    }
)

export default api;
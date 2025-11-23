import axios from 'axios';

const api = axios.create({
  baseURL: 'http://localhost:8080', // Spring Boot
});

// 请求拦截器：带上 token（后面登录成功会保存）
api.interceptors.request.use(config => {
  const token = localStorage.getItem('rf_token');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

export default api;
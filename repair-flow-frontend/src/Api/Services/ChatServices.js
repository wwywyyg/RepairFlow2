import api from "../axiosConfig";

export const fetchHistory = async (ticketId) => {
  const res = await api.get(`/tickets/${ticketId}/messages`, { params: { page: 0, size: 50 } });
  // Spring Page: res.data.content 是数组
  return res.data.content || [];
};


export const uploadChatImage = async (ticketId, file) => {
  const form = new FormData();
  form.append("file", file);

  const res = await api.post(`/tickets/${ticketId}/messages/image`, form, {
    headers: { "Content-Type": "multipart/form-data" },
  });

  // ✅ ApiResponse<Map> => res.data.data.url
  const path = res.data?.data?.url; // "/uploads/xxx.jpg"
  if (!path) throw new Error("upload api did not return url");

  const base = (api.defaults.baseURL || "http://localhost:8080").replace(/\/$/, "");
  return path.startsWith("http") ? path : `${base}${path}`;
};
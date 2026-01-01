import api from "../axiosConfig";

// common APIs
// register
export const registerUser = async (userData) => {
    const response = await api.post("/auth/register", userData);
    return response.data;
}

//login
export const loginUser = async (credentials) => {
    const response = await api.post("/auth/login", credentials);
    return response.data;
}

// read user info
export const getCurrentUser = async () => {
    const response = await api.get("/auth/user/me");
    return response.data;
}


// admin APIs
// admin get all users
export const adminGetAllUsers = async (page = 0, size = 10) => {
    const params = { page, size };
    return await api.get("/user/list", { params });
}

// admin update user role
export const adminUpdateUserRole = async (userId, {isActive, role}) => {
    return await api.patch(`/user/update/${userId}`,{isActive, role});
}

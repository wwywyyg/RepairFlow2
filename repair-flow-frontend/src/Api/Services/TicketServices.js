import api from "../axiosConfig";

const TICKETS_CUSTOMER_URL = "/auth/customer/tickets";
const TICKETS_EMPLOYEE_URL = "/auth/employee/tickets";
const TICKETS_ADMIN_URL = "/auth/admin";

// Customer  APIs

// read one customer ticket
export const getOneCustomerTicket = async (ticketId) => {
    return await api.get(`${TICKETS_CUSTOMER_URL}/${ticketId}`);
}

// read all customer own tickets
export const getALlCustomerTickets = async (page = 0, size = 10,status=null) => {
    const params = { page, size,status };
    return await api.get(TICKETS_CUSTOMER_URL, { params });
}

// approve qouote for ticket
export const approveCustomerTicketQuote = async (ticketId) => {
    return await api.post(`${TICKETS_CUSTOMER_URL}/${ticketId}/approve-quote`);
}

// customer confirm paid
export const confirmCustomerTicketPaid = async (ticketId) => {
    return await api.post(`${TICKETS_CUSTOMER_URL}/${ticketId}/confirm`);
}

// customer mark delivered
export const markCustomerTicketDelivered = async (ticketId) => {
    return await api.post(`${TICKETS_CUSTOMER_URL}/${ticketId}/mark-delivered`);
}

// customer create ticket
export const customerCreateTicket = async (ticketInfo) => {
    return await api.post(TICKETS_CUSTOMER_URL, ticketInfo);
}





// employee APIs

// read one employee ticket
export const getOneEmployeeTicket = async (ticketId) => {
    return await api.get(`${TICKETS_EMPLOYEE_URL}/${ticketId}`);
}

// read all available tickets
export const getAllAvailableTickets = async (page = 0, size = 10,status=null) => {
    const params = { page, size,status };
    return await api.get(`${TICKETS_EMPLOYEE_URL}/available`, { params });
}


// read all employee own tickets
export const getAllEmployeeTickets = async (page = 0, size = 10,status=null) => {
    const params = { page, size,status };
    return await api.get(`${TICKETS_EMPLOYEE_URL}/own-tickets`, { params });
}

// set quote for ticket
export const employeeSetQuote = async (ticketId, quoteAmount) => {
    return await api.put(`${TICKETS_EMPLOYEE_URL}/${ticketId}/quote`, { quoteAmount });
}

// update ticket status
export const employeeUpdateStatus = async (ticketId, status) => {
    return await api.put(`${TICKETS_EMPLOYEE_URL}/${ticketId}/status`, { status });
}

// claim ticket
export const employeeClaimTicket = async (ticketId) => {
    return await api.post(`${TICKETS_EMPLOYEE_URL}/${ticketId}/claim`);
}




// common APIs 

// get all device categories data
export const fetchDevicesCategories = async () => {
    return await api.get("/meta/device-categories");
}

// get all issue types data
export const fetchIssueTypes = (deviceCategoryId) => {
  return api.get("/meta/issue-types", {
    params: deviceCategoryId ? { deviceCategoryId } : {},
  });
};


// Admin
export const adminGetAllTickets = async (page = 0, size = 10,status=null) => {
    const params = { page, size,status };
    return await api.get(`${TICKETS_ADMIN_URL}/ticket/list-all`, { params });
}

// read one ticket 
export const adminGetOneTicket = async (ticketId) => {
    return await api.get(`${TICKETS_ADMIN_URL}/ticket/${ticketId}`);
}
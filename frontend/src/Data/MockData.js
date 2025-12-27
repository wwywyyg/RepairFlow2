// src/Data/MockData.js

/**
 * ==========================================
 * 1. 模拟用户数据 (Users by Role)
 * ==========================================
 */
export const MOCK_USERS = {
  customer: {
    id: 10,
    role: "customer",
    name: "Alice Customer",
    email: "alice@example.com",
    avatar: "https://ui-avatars.com/api/?name=Alice+Customer&background=random"
  },
  employee: {
    id: 20,
    role: "employee",
    name: "Bob Technician",
    email: "bob@repair.com",
    avatar: "https://ui-avatars.com/api/?name=Bob+Tech&background=0D8ABC&color=fff"
  },
  admin: {
    id: 30,
    role: "admin",
    name: "Charlie Admin",
    email: "admin@repair.com",
    avatar: "https://ui-avatars.com/api/?name=Charlie+Admin&background=000&color=fff"
  }
};

/**
 * ==========================================
 * 2. 模拟工单列表 (Ticket List)
 * 涵盖了：不同状态、不同分配情况、不同设备类型
 * ==========================================
 */
export const MOCK_TICKETS = [
  // --- 场景 1: 刚创建，未分配 (Open & Unassigned) ---
  // 测试点：Employee 工单池应该看到它，StatusProgress 显示第一步
  {
    id: 101,
    title: "iPhone 13 Screen Crack",
    description: "Dropped on concrete. Screen shattered, touch still works.",
    deviceCategoryId: "Phone",
    issueTypeId: "Screen Replacement",
    status: "PENDING", // 对应 Ticket Open
    quoteAmount: null, // 还没报价
    paid: false,
    customer: MOCK_USERS.customer,
    employee: null, // 未分配
    createdAt: "2025-12-24T09:00:00",
    updatedAt: "2025-12-24T09:00:00"
  },

  // --- 场景 2: 已分配，准备中 (Assigned & Preparation) ---
  // 测试点：Sidebar 里的 "My Tasks" 应该显示 (如果是 Bob 登录)
  {
    id: 102,
    title: "MacBook Pro Battery",
    description: "Battery service recommended warning. Shuts down at 20%.",
    deviceCategoryId: "Laptop",
    issueTypeId: "Battery",
    status: "DEVICE_RECEIVED", // 对应 Preparation 阶段
    quoteAmount: 199.00,
    paid: false,
    customer: { id: 11, name: "David Guest", email: "david@test.com" },
    employee: MOCK_USERS.employee, // 分配给了 Bob
    createdAt: "2025-12-20T14:30:00",
    updatedAt: "2025-12-21T10:00:00"
  },

  // --- 场景 3: 维修进行中 (In Progress) ---
  // 测试点：进度条走到中间，聊天室里应该有沟通记录
  {
    id: 103,
    title: "PS5 HDMI Port",
    description: "No signal on TV. Port looks loose.",
    deviceCategoryId: "Console",
    issueTypeId: "Hardware Fix",
    status: "IN_PROGRESS", // 对应 In Repair
    quoteAmount: 120.00,
    paid: true, // 已付款
    customer: MOCK_USERS.customer, // Alice 的工单
    employee: MOCK_USERS.employee,
    createdAt: "2025-12-18T11:00:00",
    updatedAt: "2025-12-19T16:20:00"
  },

  // --- 场景 4: 已修复，待确认 (Ready for Confirmation) ---
  // 测试点：进度条应该在 Repair 完成状态
  {
    id: 104,
    title: "iPad Air Charging Issue",
    description: "Not charging even with new cable.",
    deviceCategoryId: "Tablet",
    issueTypeId: "Charging Port",
    status: "READY_FOR_CONFIRMATION",
    quoteAmount: 85.00,
    paid: true,
    customer: { id: 12, name: "Eve User", email: "eve@test.com" },
    employee: MOCK_USERS.employee,
    createdAt: "2025-12-15T09:15:00",
    updatedAt: "2025-12-20T11:00:00"
  },

  // --- 场景 5: 已完工交付 (Completed) ---
  // 测试点：进度条全绿，应该在 History 列表里
  {
    id: 105,
    title: "Gaming PC Cleanup",
    description: "Dust cleanup and thermal paste application.",
    deviceCategoryId: "Computer",
    issueTypeId: "Maintenance",
    status: "DELIVERED", // 对应 Completed
    quoteAmount: 50.00,
    paid: true,
    customer: MOCK_USERS.customer,
    employee: { id: 21, name: "Other Tech", email: "other@repair.com" }, // 别的员工修的
    createdAt: "2025-12-01T10:00:00",
    updatedAt: "2025-12-05T14:00:00"
  },

  // --- 场景 6: 边缘测试 - 长文本描述 ---
  // 测试点：检查卡片会不会被撑爆，布局是否正常
  {
    id: 106,
    title: "Legacy Device Investigation",
    description: "This is a very very very long description to test the UI layout stability. I want to see if the card expands correctly or if the text gets cut off. The device makes a strange noise and smells like burning plastic.",
    deviceCategoryId: "Other",
    issueTypeId: "Unknown",
    status: "PENDING",
    quoteAmount: 0.00,
    paid: false,
    customer: MOCK_USERS.customer,
    employee: null,
    createdAt: "2025-12-25T08:00:00",
    updatedAt: "2025-12-25T08:00:00"
  },

  // --- 场景 7: 边缘测试 - 刚报价未付款 ---
  // 测试点：StatusProgress 在 Step 2，Badge 显示 UNPAID
  {
    id: 107,
    title: "Water Damage Switch",
    description: "Spilled juice on it.",
    deviceCategoryId: "Console",
    issueTypeId: "Water Damage",
    status: "QUOTED",
    quoteAmount: 250.00,
    paid: false, // 未付款
    customer: { id: 13, name: "Frank", email: "frank@test.com" },
    employee: MOCK_USERS.employee,
    createdAt: "2025-12-22T13:00:00",
    updatedAt: "2025-12-23T09:30:00"
  }
];
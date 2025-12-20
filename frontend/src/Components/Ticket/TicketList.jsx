import React from "react";
import {Row, Container} from 'react-bootstrap'
import TicketCard from "./TicketCard";

const TicketList = () =>{

const MOCK_TICKETS = [
  // 1. 场景：已完成的工单 (DELIVERED, 绿色)
  {
    "id": 101,
    "title": "MacBook Screen Repair",
    "description": "Screen flickering and shows vertical lines after drop.",
    "deviceCategoryId": 501,
    "issueTypeId": 50105,
    "status": "DELIVERED", 
    "quoteAmount": 450.00,
    "paid": true,
    "customer": {
        "id": 14,
        "name": "Alice Wang",
        "email": "alice@test.com"
    },
    "employee": {
        "id": 15,
        "name": "Tech Mike",
        "email": "mike@test.com"
    },
    "createdAt": "2025-12-01T09:00:00.000000",
    "updatedAt": "2025-12-05T16:00:00.000000"
  },

  // 2. 场景：正在维修中 (IN_PROGRESS, 黄色)
  {
    "id": 102,
    "title": "PS5 Overheating",
    "description": "Console shuts down randomly when playing graphic intensive games. Fan noise is very loud.",
    "deviceCategoryId": 502,
    "issueTypeId": 50201,
    "status": "IN_PROGRESS",
    "quoteAmount": 120.00,
    "paid": false,
    "customer": {
        "id": 20,
        "name": "Bob Gamer",
        "email": "bob@test.com"
    },
    "employee": {
        "id": 15,
        "name": "Tech Mike",
        "email": "mike@test.com"
    },
    "createdAt": "2025-12-11T10:30:00.000000",
    "updatedAt": "2025-12-12T08:15:00.000000"
  },

  // 3. 场景：刚创建，还没人接手 (OPEN, 蓝色, Employee 为 null)
  // 用于测试 "Unassigned" 显示逻辑
  {
    "id": 103,
    "title": "iPhone Battery Drain",
    "description": "Battery health is at 70%, drains completely in 2 hours.",
    "deviceCategoryId": 501,
    "issueTypeId": 50102,
    "status": "OPEN", 
    "quoteAmount": 0.00,
    "paid": false,
    "customer": {
        "id": 22,
        "name": "Charlie Day",
        "email": "charlie@test.com"
    },
    "employee": null, // 测试空值
    "createdAt": "2025-12-15T14:20:00.000000",
    "updatedAt": "2025-12-15T14:20:00.000000"
  },

  // 4. 场景：长文本描述 (测试文本截断效果)
  {
    "id": 104,
    "title": "Custom PC Water Damage",
    "description": "Spilled coffee on the tower. Tried to dry it but now it won't turn on. Motherboard lights flash once then stop. Need data recovery if possible.",
    "deviceCategoryId": 503,
    "issueTypeId": 50309,
    "status": "OPEN",
    "quoteAmount": 0.00,
    "paid": false,
    "customer": {
        "id": 30,
        "name": "David Smith",
        "email": "david@test.com"
    },
    "employee": null,
    "createdAt": "2025-12-16T09:00:00.000000",
    "updatedAt": "2025-12-16T09:00:00.000000"
  },

  // 5. 场景：已取消 (CANCELLED, 红色)
  {
    "id": 105,
    "title": "Old Tablet Fix",
    "description": "Screen cracked.",
    "deviceCategoryId": 501,
    "issueTypeId": 50101,
    "status": "CANCELLED",
    "quoteAmount": 300.00, // 假如报价太贵客户取消了
    "paid": false,
    "customer": {
        "id": 33,
        "name": "Eve Polastri",
        "email": "eve@test.com"
    },
    "employee": {
        "id": 16,
        "name": "Manager Sarah",
        "email": "sarah@test.com"
    },
    "createdAt": "2025-12-01T00:00:00.000000",
    "updatedAt": "2025-12-02T10:00:00.000000"
  }
];



    return(
        <Container fluid>
            <h3 className="mb-4 text-secondary">Ticket Pool</h3>
            {/* for search Bar */}

            <Row>
                {MOCK_TICKETS.map((ticket) => (<TicketCard key={ticket.id} ticket={ticket}/>))}
            </Row>
        </Container>
    )
}

export default TicketList;
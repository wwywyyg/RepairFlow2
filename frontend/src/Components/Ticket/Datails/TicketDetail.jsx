import React from "react";
import {Container,Row,Col,Card,Button,Badge} from 'react-bootstrap'
import {useParams, NavLink} from 'react-router-dom'
import StatusProgress from "./StatusProgress";
import TicketInfo from "./TicketInfo";
import ChatRoom from "./ChatRoom";
import { MOCK_TICKETS } from "../../../Data/MockData";
const TicketDetail = () =>{
    
    // const TICKET_DATA = {
    //     id: 101, // 防止 undefined
    //     title: "MacBook Screen Repair",
    //     status: "DEVICE_RECEIVED", // 可以改这个测试进度条
    //     description: "Screen flickering and shows vertical lines after drop.",
    //     deviceCategoryId: "Laptop (501)",
    //     issueTypeId: "Screen (50105)",
    //     quoteAmount: 450.00,
    //     paid: false,
    //     createdAt: "2025-12-01T09:00:00",
    //     customer: {
    //     name: "Alice Wang",
    //     email: "alice@test.com"
    //     },
    //     employee: {
    //     name: "Tech Mike",
    //     email: "mike@repair.com"
    //     }
    // };

    const { ticketId } = useParams();
    const foundTicket = MOCK_TICKETS.find(t => t.id === parseInt(ticketId));
    if (!foundTicket) {
        return <div className="p-5 text-center">Ticket Not Found</div>;
    }

    const TICKET_DATA = foundTicket;

    return(
        <Container fluid className="p-4">
            {/* top navigation return button */}
            <div className="mb-3">
                <NavLink to="/dashboard/tickets" className="text-decoration-none text-secondary fw-bold"  >
                    &larr; Back to List
                </NavLink>
            </div>

            {/* page main body */}
            <Card className="shadow-sm border border-secondary border-dashed rounded" style={{minHeight:'80vh'}}>

            {/* header */}
                <Card.Header className="bg-light py-3 border-bottom">
                    <div className="d-flex justify-content-between align-items-center">
                        <h4 className="m-0 fw-bold text-dark">
                            #{TICKET_DATA.id} - {TICKET_DATA.title}
                        </h4>
                        <Badge bg="warning" text="dark" className="fs-6 px-3 py-2">
                            {TICKET_DATA.status}
                        </Badge>
                    </div>
                </Card.Header>

                <Card.Body className="d-flex flex-column gap-3">
                {/* Zone A  --  Status Progress */}
                <div className="p-0 m-0 bg-light border border-secondary border-dashed rounded text-center">
                    <StatusProgress status = {TICKET_DATA.status}/>
                </div>
                <Row className="flex-grow-1 g-2">
                    {/* ticket Info */}
                    <Col lg = {3} md = {12}>
                        <div className="h-100 p-0 bg-light  bg-light border border-success border-dashed rounded text-center d-flex flex-column justify-content-center">
                            <TicketInfo ticket={TICKET_DATA}/>
                        </div>
                    </Col>


                    {/* ChatRoom  */}
                    <Col lg={9} md={12}>
                        <div className="h-100 p-0 bg-light border border-success border-dashed rounded text-center d-flex flex-column justify-content-center">
                            <ChatRoom ticketId={TICKET_DATA.id}/>
                        </div>
                    </Col>
                </Row>

                </Card.Body>    


            </Card>


        </Container>
    )



}

export default TicketDetail;
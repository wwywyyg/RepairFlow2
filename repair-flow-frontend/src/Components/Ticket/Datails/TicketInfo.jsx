import React from "react";
import {Card,ListGroup, Badge} from 'react-bootstrap'

const TicketInfo = ({ticket}) =>{





    if(!ticket) return null;
    return(
        <Card className="shadow-sm border-0 h-100">
            <Card.Header className="bg-white fw-bold py-3 border-bottom-0">
                Ticket Details
            </Card.Header>
            <hr className="my-0" />
            <ListGroup variant="flush">
                {/* Customer Section  */}
                <ListGroup.Item className="py-3">
                    <small className="text-muted d-block mb-1 fw-bold">Customer</small>
                    <div className="fw-bold text-dark">{ticket.customer?.name || 'Unknown'}</div>
                    {/* <div className="small text-secondary">{ticket.customer?.email}</div> */}
                </ListGroup.Item>

                {/* Employee Section */}
                <ListGroup.Item className="py-3">
                    <small className="text-muted d-block mb-1 fw-bold">Technician</small>
                    {ticket.employee?(<>
                        <div className="fw-bold text-dark">{ticket.employee.name}</div>
                        {/* <div className="small text-secondary">{ticket.employee.email}</div> */}
                    </>) :(
                        <span className="text-warning fst-italic">Not Assigned Yet</span>
                    )
                    }
                </ListGroup.Item>

                {/* Device & Issue Section */}
                <ListGroup.Item className="py-3">
                    <small className="text-muted d-block mb-1 fw-bold">Device Info</small>
                    <div className="d-flex justify-content-between mb-1">
                        <span>Category:</span>
                        <span className="fw-bold">{ticket.deviceCategoryId}</span>
                    </div>

                    <div className="d-flex justify-content-between mb-1">
                        <span>Issue Type:</span>
                        <span className="fw-bold">{ticket.issueTypeId}</span>
                    </div>
                </ListGroup.Item>
                    
                {/* Financials Section */}
                <ListGroup.Item className="py-3 bg-light">
                    <div className="d-flex justify-content-between align-items-center mb-2">
                        <small className="text-muted">Quote Amount:</small>
                        <span className="fs-5 fw-bold text-primary">{ticket.quoteAmount?.toFixed(2)}</span>
                    </div>
                    <div className="d-flex justify-content-between align-items-center">
                        <small className="text-muted">Payment Status</small>
                        {ticket.paid?(
                            <Badge bg="success">Paid</Badge>
                            ):(<Badge bg="secondary">Unpaid</Badge>)
                        }
                    </div>
                </ListGroup.Item>
                
                {/* Time stamps */}
                <ListGroup.Item className="py-3">
                    <small className="text-muted d-block mb-1 fw-bold">Created At:</small>
                    <div className="small text-secondary">
                        {new Date(ticket.createdAt).toLocaleDateString()}
                    </div>
                    <small className="text-muted d-block mb-1 fw-bold">Updated At:</small>
                    <div className="small text-secondary">
                        {new Date(ticket.updatedAt).toLocaleDateString()}
                    </div>
                </ListGroup.Item>
            </ListGroup>

        </Card>
    )
}

export default TicketInfo;
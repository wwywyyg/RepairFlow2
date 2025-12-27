import React from "react";
import {Card, Button, Badge, Col} from 'react-bootstrap';
import {NavLink} from 'react-router-dom';


const TicketCard = ({ticket}) =>{

    const TEST_TICKET = {
      "id": 10,
      "title": "Phone Screen Crack", 
      "description": "battery die after 1 hour usage.this is a longgggggggggggggggggggggggggggggggggggggggg description",
      "deviceCategoryId": 501,
      "issueTypeId": 50105,
      "status": "DELIVERED", 
      "quoteAmount": 199.99,
      "paid": true,
      "customer": {
          "id": 14,
          "name": "Zhang San",
          "email": "customer2@test.com"
      },
      "employee": {
          "id": 15,
          "name": "Li Si",
          "email": "employee1@test.com"
      },
      "createdAt": "2025-12-10T00:11:55.162773",
      "updatedAt": "2025-12-10T00:19:14.227807"
  };


    // State color
    const getStatusVariant = (status) => {
        switch (status) {
        case 'OPEN': return 'primary';      // blue
        case 'IN_PROGRESS': return 'warning'; // yellow
        case 'DELIVERED': return 'success';   // green
        case 'CANCELLED': return 'danger';    // red
        default: return 'secondary';          // gray
        }
    };

    // reformat Date
    const formatDate = (dateString) => {
        return new Date(dateString).toLocaleDateString();
    };



    return(
        <Col md={8} lg={4} className="mb-4">
            <Card className="h-100 shadow-sm hover-shadow ">

                {/* header Id and Status */}
                <Card.Header className="d-flex justify-content-between align-items-center bg-secondary">
                    <span className="text-muted small fs-6 fw-bold">#{ticket.id}</span>
                    <Badge className="fs-6" bg={getStatusVariant(ticket.status)}>
                        {ticket.status}
                    </Badge>
                </Card.Header>


                <Card.Body className="bg-light">
                {/* title */}
                    <Card.Title className="fw-blod">{ticket.title}</Card.Title>

                {/* descritpion */}
                    <Card.Text className="text-secondary text-truncate fw-bold" style={{maxWidth:'100%'}}>
                        Description : {ticket.description}
                    </Card.Text>

                {/* Device category */}
                    <Card.Text className="text-secondary text-truncate fw-bold" style={{maxWidth:'100%'}}>
                        Category : {ticket.deviceCategoryId}
                    </Card.Text>

                {/* issueType  */}
                    <Card.Text className="text-secondary text-truncate fw-bold" style={{maxWidth:'100%'}}>
                        Issus Type : {ticket.issueTypeId}
                    </Card.Text>

                {/* Quote */}
                    <Card.Text className="text-secondary text-truncate fw-bold" style={{maxWidth:'100%'}}>
                        Quote : {ticket.quoteAmount}
                    </Card.Text>

                {/* Pay status */}
                    <Card.Text className="text-secondary text-truncate fw-bold" style={{maxWidth:'100%'}}>
                        Pay : {ticket.paid? 'Paid' : 'Not PayS'}
                    </Card.Text>
                </Card.Body>


                 <hr className="my-0" />

                {/* get customer and employee information  */}
                <div className="small text-muted bg-light">
                    <div className="d-flex mb-1 justify-content-start px-2 py-2">
                        <span className="px-2 fw-bold text-secondary">Customer: </span>
                        <span className="fw-blod text-dark">{ticket.customer?.name}</span>
                    </div>
                    <div className="d-flex justify-content-start mb-1a">
                        <span className="px-3 fw-bold text-secondary">Assign to: </span>
                        <span className="fw-blod text-dark">{ticket.employee?.name || 'Unassigned'} </span>
                    </div>
                </div>

                <Card.Footer className="bg-light border-top-0 d-flex justify-content-between align-items-center">
                <small className="text-muted">{formatDate(ticket.createdAt)}</small>

                <NavLink to={`/dashboard/tickets/${ticket.id}`} end>
                    <Button variant="outline-primary" size="sm">View Details &rarr;</Button>
                </NavLink>
            </Card.Footer>
            </Card>

            

            

           
        </Col>
    )
}


export default TicketCard;
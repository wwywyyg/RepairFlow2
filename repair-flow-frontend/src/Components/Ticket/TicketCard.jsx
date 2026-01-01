import React , {useState}from "react";
import {Card, Button, Badge, Col} from 'react-bootstrap';
import {NavLink} from 'react-router-dom';
import { useAuth } from "../../Context/AuthContext";
import { employeeClaimTicket } from "../../Api/Services/TicketServices";
import '../../index.css';

const TicketCard = ({ticket , onClaimSuccess}) =>{
    const {user} = useAuth();
    const currentRole = user?.role?.toLowerCase();
    const [showFullDesc, setShowFullDesc] = useState(false);    

    // State color
    const getStatusVariant = (status) => {
        switch (status) {
            case 'PENDING': return 'secondary';           
            case 'ASSIGNED': return 'info';                
            case 'QUOTED': return 'warning';               
            case 'AWAITING_DEVICE': return 'light';        
            case 'DEVICE_RECEIVED': return 'info';        
            case 'IN_PROGRESS': return 'primary';          
            case 'READY_FOR_CONFIRMATION': return 'success'; 
            case 'PAID': return 'success';                 
            case 'SHIPPED': return 'dark';                
            case 'DELIVERED': return 'dark';               
            default: return 'secondary';
        }
    };



    // reformat Date
    const formatDate = (dateString) => {
        return new Date(dateString).toLocaleDateString();
    };

    const formatStatusText = (status) => {
        if(!status) return '';
        return status.replace(/_/g, ' '); 
    }

    const handleClaimTicket = async () => {
        const confirmed = window.confirm("Are you sure you want to claim this ticket?");
        if(!confirmed) return;
        try{
            const response = await employeeClaimTicket(ticket.id);
            const updatedTicket = response.data.data;

            onClaimSuccess?.(updatedTicket);
            
        }catch(error){
            console.error("Error claiming ticket:", error);
            alert("Error claiming ticket: " + error.message);
        }
    }



    return(
        <Col md={8} lg={4} className="mb-4">
            <Card className="h-100 shadow-sm hover-shadow ">

                {/* header Id and Status */}
                <Card.Header className="d-flex justify-content-between align-items-center bg-light">
                    <span className="text-muted small fs-6 fw-bold">#{ticket.id}</span>
                    <Badge className="fs-6" bg={getStatusVariant(ticket.status)}>
                        {formatStatusText(ticket.status) || ticket.status}
                    </Badge>
                </Card.Header>


                <Card.Body className="bg-white">
                {/* title */}
                    <Card.Title className="fw-blod">{ticket.title}</Card.Title>

                {/* descritpion */}
                    <Card.Text className={"text-secondary fw-bold" + (!showFullDesc? " desc-clamp-2":"")} style={{maxWidth:'100%'}}>
                        Description : {ticket.description}
                    </Card.Text>
                    {ticket.description && ticket.description.length > 60 && (
                        <Button variant="link" size="sm" className="px-0 desc-toggle-btn" onClick={()=> setShowFullDesc((prev) => !prev)}>
                            {showFullDesc ? 'Show Less' : 'Show More'}
                        </Button>
                    )}

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
                        Pay : {ticket.paid? 'Paid' : 'Not Payed'}
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

                { !(currentRole === 'employee' && ticket.employee === null) && (
                <NavLink to={`/dashboard/tickets/${ticket.id}`} end>
                    <Button variant="primary" size="sm">View Details &rarr;</Button>
                </NavLink>)
                }
                {/* employee claim button */}
                {currentRole === "employee" && ticket.employee === null && (
                    <Button variant="primary" size="sm" onClick={handleClaimTicket}>Claim Ticket</Button>
                )}
                
            </Card.Footer>
            </Card>
           
        </Col>
    )
}


export default TicketCard;
import React ,{useState,useEffect} from "react";
import {Container,Row,Col,Card,Button,Badge,Spinner,Alert} from 'react-bootstrap'
import {useParams, NavLink} from 'react-router-dom'
import StatusProgress from "./StatusProgress";
import TicketInfo from "./TicketInfo";
import ChatRoom from "./ChatRoom";
import { useAuth } from "../../../Context/AuthContext";
import { getOneCustomerTicket,getOneEmployeeTicket,adminGetOneTicket } from "../../../Api/Services/TicketServices";
import TicketUpdateModal from "./TicketUpdateModal";
const TicketDetail = () =>{
    const { ticketId } = useParams();
    const {user} = useAuth();
    

    const [ticket,setTicket] = useState(null);
    const [loading,setLoading] = useState(true);
    const [error,setError] = useState(null);

    // pop window coontrol
    const [showUpdate, setShowUpdate] = useState(false);


    useEffect(() => {
        const fetchTicket = async () => {
            setLoading(true);

            try{
                let response;

                if(user.role === "CUSTOMER"){
                    response = await getOneCustomerTicket(ticketId);
                }else if(user.role === "EMPLOYEE"){
                    response = await getOneEmployeeTicket(ticketId);
                }else if(user.role === "ADMIN"){
                    response = await adminGetOneTicket(ticketId);
                }else{ // need add admin later
                    throw new Error("Unknown user role");
                } 

                setTicket(response.data.data);
                setError(null);
            }catch(error){
                console.error("Error fetching ticket:", error);
                setError(error.message);
            }finally{
                setLoading(false);
            }
        };

        if(ticketId && user){
            fetchTicket();
        }
    }, [ticketId,user]);

    if (loading) return <Container className="p-5 text-center"><Spinner animation="border" /></Container>;
    if (error) return <Container className="p-5 text-center"><Alert variant="danger">{error}</Alert></Container>;
    if (!ticket) return null;



    const TICKET_DATA = ticket;
    

    const handleTicketSave = (updatedTicket) => {
        setTicket(updatedTicket);
    };

    return(
        <Container fluid className="p-4">
            {/* top navigation return button */}
            <div className="mb-3 d-flex justify-content-between align-items-center">
                <NavLink to="/dashboard/tickets" className="text-decoration-none text-white btn btn-secondary fw-bold"  >
                    &larr; Back to List
                </NavLink>
                <Button variant="primary" onClick={()=> setShowUpdate(true)}>
                    Update Ticket
                </Button>
            </div>

            {/* Update ticket modal component */}
            <TicketUpdateModal show={showUpdate} onClose={() => setShowUpdate(false)} ticket={TICKET_DATA} role={user.role} onSave={handleTicketSave} />

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
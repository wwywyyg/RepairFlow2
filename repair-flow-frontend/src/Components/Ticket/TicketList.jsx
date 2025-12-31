import React, {useState,useEffect} from "react";
import {Row, Container,Spinner,Alert} from 'react-bootstrap'
import TicketCard from "./TicketCard";
import { useAuth } from "../../Context/AuthContext";
import { getALlCustomerTickets,getAllAvailableTickets,getAllEmployeeTickets } from "../../Api/Services/TicketServices";


const TicketList = ({type}) =>{
    const { user } = useAuth();
    const [tickets, setTickets] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    
    useEffect(()=> {
        const fetchTickets = async () => {
            if(!user) return;
            setLoading(true);
            setTickets([]);
            setError(null);

            try{ 
                let response;
                if(type === "POOL"){
                    if(user.role === "CUSTOMER"){
                        response = await getALlCustomerTickets(0,50);
                    }else if(user.role === "EMPLOYEE"){
                        response = await getAllEmployeeTickets(0,50);
                    }
                }else if(type === "AVAILABLE"){
                    response = await getAllAvailableTickets(0,50);
                }// need add admin later

                // data parsing
                if(response && response.data && response.data.data && Array.isArray(response.data.data.content)){
                    setTickets(response.data.data.content);
                }else{
                    setTickets([]);
                    console.error("Error fetching tickets:", response);
                }
                    
            }catch(error){
                console.error("Error fetching tickets:", error);
                setError(error.message);
            }finally{
                setLoading(false);
            }
        };
        fetchTickets();
    },[type,user]);

    if (loading) return <Container className="text-center mt-5"><Spinner animation="border" /></Container>;
    if (error) return <Container className="mt-5"><Alert variant="danger">{error}</Alert></Container>;


    const handleClaimSuccess = (updatedTicket) => {
        if(type === "AVAILABLE"){
            setTickets(prev => prev.filter((ticket) => ticket.id !== updatedTicket.id));
        }else if(type === "POOL" && user?.role === "EMPLOYEE"){
            setTickets((prev) => prev.map((ticket) => ticket.id === updatedTicket.id ? updatedTicket : ticket));
        }
    }
    return(
        <Container fluid>
            {/* <h3 className="mb-3 text-secondary">Ticket Pool</h3> */}
            {/* for search Bar */}

            <Row>
                {tickets.map((ticket) => (<TicketCard key={ticket.id} ticket={ticket} onClaimSuccess={handleClaimSuccess}/>))}
            </Row>
        </Container>
    )
}

export default TicketList;
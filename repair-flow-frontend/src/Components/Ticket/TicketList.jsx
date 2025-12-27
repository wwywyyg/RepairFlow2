import React from "react";
import {Row, Container} from 'react-bootstrap'
import TicketCard from "./TicketCard";
import { MOCK_TICKETS } from "../../Data/MockData"; '../../Data/MockData.js';
const TicketList = () =>{


  const displayTickets = MOCK_TICKETS;

    return(
        <Container fluid>
            {/* <h3 className="mb-3 text-secondary">Ticket Pool</h3> */}
            {/* for search Bar */}

            <Row>
                {displayTickets.map((ticket) => (<TicketCard key={ticket.id} ticket={ticket}/>))}
            </Row>
        </Container>
    )
}

export default TicketList;
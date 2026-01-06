import React, { useState, useEffect, useCallback, useRef } from "react";
import { Container, Row, Col, Card, Button, Badge, Spinner, Alert } from "react-bootstrap";
import { useParams, NavLink } from "react-router-dom";
import StatusProgress from "./StatusProgress";
import TicketInfo from "./TicketInfo";
import ChatRoom from "./ChatRoom";
import { useAuth } from "../../../Context/AuthContext";
import { getOneCustomerTicket, getOneEmployeeTicket, adminGetOneTicket } from "../../../Api/Services/TicketServices";
import TicketUpdateModal from "./TicketUpdateModal";

const TicketDetail = () => {
  const { ticketId } = useParams();
  const { user } = useAuth();

  const [ticket, setTicket] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  // 只用于“后台刷新锁”，不影响 UI
  const refreshingRef = useRef(false);

  const [showUpdate, setShowUpdate] = useState(false);

  const fetchTicket = useCallback(
    async ({ showLoading } = { showLoading: true }) => {
      if (!ticketId || !user?.role) return;

      if (!showLoading) {
        if (refreshingRef.current) return; // 防抖：系统消息连发时不重复请求
        refreshingRef.current = true;
      } else {
        setLoading(true);
      }

      try {
        let response;
        if (user.role === "CUSTOMER") response = await getOneCustomerTicket(ticketId);
        else if (user.role === "EMPLOYEE") response = await getOneEmployeeTicket(ticketId);
        else if (user.role === "ADMIN") response = await adminGetOneTicket(ticketId);
        else throw new Error("Unknown user role");

        setTicket(response.data.data);
        setError(null);
      } catch (e) {
        console.error("Error fetching ticket:", e);
        setError(e.message);
      } finally {
        if (showLoading) {
          setLoading(false);
        } else {
          refreshingRef.current = false;
        }
      }
    },
    [ticketId, user?.role]
  );

  // 首次加载
  useEffect(() => {
    fetchTicket({ showLoading: true });
  }, [fetchTicket]);

  // ✅ 关键：给 ChatRoom 的回调必须是稳定引用
  const handleSystemMessage = useCallback(() => {
    // 系统消息到达 -> 静默刷新 ticket（不显示整页 loading）
    fetchTicket({ showLoading: false });
  }, [fetchTicket]);

  const handleTicketSave = async () => {
    setShowUpdate(false);
    await fetchTicket({ showLoading: true });
  };

  if (loading) return <Container className="p-5 text-center"><Spinner animation="border" /></Container>;
  if (error) return <Container className="p-5 text-center"><Alert variant="danger">{error}</Alert></Container>;
  if (!ticket) return null;

  const TICKET_LIST_PATH_BY_ROLE = {
  ADMIN: "/dashboard/all-tickets",
  EMPLOYEE: "/dashboard/tickets",
  CUSTOMER: "/dashboard/tickets",
  };
  const backPath = TICKET_LIST_PATH_BY_ROLE[user?.role] || "/dashboard";
  
//   const backPath = user?.role.toLowerCase() === "admin" ? "/dashboard/all-tickets" : "/dashboard/tickets"; 

  const TICKET_DATA = ticket;

  return (
    <Container fluid className="p-4">
      <div className="mb-3 d-flex justify-content-between align-items-center">
        <NavLink to={`${backPath}`} className="text-decoration-none text-white btn btn-secondary fw-bold">
          &larr; Back to List
        </NavLink>
        <Button variant="primary" onClick={() => setShowUpdate(true)}>
          Update Ticket
        </Button>
      </div>

      <TicketUpdateModal
        show={showUpdate}
        onClose={() => setShowUpdate(false)}
        ticket={TICKET_DATA}
        role={user.role}
        onSave={handleTicketSave}
      />

      <Card className="shadow-sm border border-secondary border-dashed rounded" style={{ minHeight: "80vh" }}>
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
          <div className="p-0 m-0 bg-light border border-secondary border-dashed rounded text-center">
            <StatusProgress status={TICKET_DATA.status} />
          </div>

          <Row className="flex-grow-1 g-2">
            <Col lg={3} md={12}>
              <div className="h-100 p-0 bg-light border border-success border-dashed rounded text-center d-flex flex-column justify-content-center">
                <TicketInfo ticket={TICKET_DATA} />
              </div>
            </Col>

            <Col lg={9} md={12}>
              <div className="h-100 p-0 bg-light border border-success border-dashed rounded text-center d-flex flex-column justify-content-center">
                <ChatRoom ticketId={TICKET_DATA.id} onSystemMessage={handleSystemMessage} />
              </div>
            </Col>
          </Row>
        </Card.Body>
      </Card>
    </Container>
  );
};

export default TicketDetail;

// Components/Admin/UserCard.jsx
import React, { useState } from "react";
import { Card, Badge, Col, Button } from "react-bootstrap";
import UserManageModal from "./UserManageModal";
import { useAuth } from "../../Context/AuthContext";

const UserCard = ({ user, onUserUpdated }) => {
  const { user: currentUser } = useAuth();
  const [showManage, setShowManage] = useState(false);

  const fullName =
    `${user.firstName || ""} ${user.lastName || ""}`.trim() || "(No Name)";

  const formatDateTime = (dt) => {
    if (!dt) return "-";
    return new Date(dt).toLocaleString();
  };

  const getRoleVariant = (role) => {
    switch (role) {
      case "ADMIN":
        return "danger";
      case "EMPLOYEE":
        return "primary";
      case "CUSTOMER":
      default:
        return "secondary";
    }
  };

  const getActiveVariant = (active) => (active ? "success" : "secondary");
  const getActiveText = (active) => (active ? "Active" : "Inactive");

  const isSelf = currentUser && currentUser.id === user.id;

  return (
    <>
      <Col md={6} lg={4} className="mb-4">
        <Card className="h-100 shadow-sm">
          <Card.Header className="d-flex justify-content-between align-items-center bg-light">
            <span className="text-muted small fw-bold">#{user.id}</span>

            <div className="d-flex gap-2 align-items-center">
              <Badge bg={getRoleVariant(user.role)}>{user.role}</Badge>
              <Badge bg={getActiveVariant(user.active)}>
                {getActiveText(user.active)}
              </Badge>
            </div>
          </Card.Header>

          <Card.Body className="bg-white">
            <Card.Title className="fw-bold mb-2">{fullName}</Card.Title>

            <Card.Text className="mb-1">
              <span className="fw-semibold text-secondary">Email: </span>
              <span className="text-dark">{user.email}</span>
            </Card.Text>

            <Card.Text className="mb-1">
              <span className="fw-semibold text-secondary">Phone: </span>
              <span className="text-dark">{user.phone || "-"}</span>
            </Card.Text>
          </Card.Body>

          <Card.Footer className="bg-light d-flex justify-content-between align-items-center small text-muted">
            <div>
              <div>Created: {formatDateTime(user.createdAt)}</div>
              <div>Updated: {formatDateTime(user.updatedAt)}</div>
            </div>

            <Button
              variant="outline-primary"
              size="sm"
              onClick={() => setShowManage(true)}
            >
              {isSelf ? "View / Edit" : "Manage"}
            </Button>
          </Card.Footer>
        </Card>
      </Col>

      <UserManageModal
        show={showManage}
        onClose={() => setShowManage(false)}
        user={user}
        onUpdated={onUserUpdated}
      />
    </>
  );
};

export default UserCard;

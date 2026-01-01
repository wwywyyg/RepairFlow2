// Components/Admin/AdminUserList.jsx
import React, { useState, useEffect } from "react";
import { Container, Row, Spinner, Alert } from "react-bootstrap";
import { useAuth } from "../../Context/AuthContext";
import { adminGetAllUsers } from "../../Api/Services/AuthServices";
import UserCard from "./UserCard";

const AdminUserList = () => {
  const { user } = useAuth();

  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    const fetchUsers = async () => {
      if (!user || user.role !== "ADMIN") return;

      setLoading(true);
      setError(null);
      setUsers([]);

      try {
        const res = await adminGetAllUsers(0, 50);
        const data = res.data?.data;

        if (data && Array.isArray(data.content)) {
          setUsers(data.content);
        } else {
          setUsers([]);
          console.error("Unexpected user list response:", res);
          setError("Unexpected response while fetching users.");
        }
      } catch (err) {
        console.error("Error fetching users:", err);
        setError(
          err.response?.data?.message ||
            err.message ||
            "Failed to fetch users."
        );
      } finally {
        setLoading(false);
      }
    };

    fetchUsers();
  }, [user]);

  if (loading) {
    return (
      <Container className="text-center mt-5">
        <Spinner animation="border" />
      </Container>
    );
  }

  if (error) {
    return (
      <Container className="mt-5">
        <Alert variant="danger">{error}</Alert>
      </Container>
    );
  }
  const handleUserUpdated = (updatedUser) => {
  setUsers((prev) =>
    prev.map((u) => (u.id === updatedUser.id ? updatedUser : u))
  );
};

  return (
    <Container fluid className="py-3">
      {/* <h3 className="mb-3 text-secondary">All Users</h3> */}

      <Row>
        {users.length === 0 ? (
          <p className="text-muted text-center">No users found.</p>
        ) : (
          users.map((u) => <UserCard key={u.id} user={u} onUserUpdated={handleUserUpdated} />)
        )}
      </Row>
    </Container>
  );
};

export default AdminUserList;

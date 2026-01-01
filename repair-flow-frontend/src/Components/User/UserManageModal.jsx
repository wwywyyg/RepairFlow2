import React, { useState, useEffect } from "react";
import { Modal, Form, Button, Badge } from "react-bootstrap";
import { useAuth } from "../../Context/AuthContext";
import { adminUpdateUserRole } from "../../Api/Services/AuthServices"

const ROLE_OPTIONS = ["CUSTOMER", "EMPLOYEE"];

const ManageUserModal = ({ show, user, onClose, onUpdated }) => {
  const { user: currentUser } = useAuth();
  const isSelf = currentUser && user && currentUser.id === user.id; // ⭐

  const [role, setRole] = useState(user?.role ?? "CUSTOMER");
  const [active, setActive] = useState(user?.active ?? true);
  const [submitting, setSubmitting] = useState(false);

  useEffect(() => {
    if (user) {
      setRole(user.role);
      setActive(user.active);
    }
  }, [user]);

  if (!user) return null;

  const handleSubmit = async (e) => {
    e.preventDefault();

    if (isSelf) {
      alert("Admin cannot change their own role or status.");
      return;
    }

    try {
      setSubmitting(true);
      const res = await adminUpdateUserRole(user.id, {
        isActive: active,
        role,
      });
      const updated = res.data?.data;
      onUpdated?.(updated);
      onClose();
    } catch (err) {
      console.error("Error updating user:", err);
      alert(err.response?.data?.message || err.message || "Update failed");
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <Modal show={show} onHide={onClose} centered backdrop="static">
      <Modal.Header closeButton>
        <div className="d-flex flex-column">
          <Modal.Title>Manage User #{user.id}</Modal.Title>
          <small className="text-muted">
            {user.firstName} {user.lastName} ·{" "}
            <Badge bg="secondary">{user.email}</Badge>
            {isSelf && <Badge bg="info" className="ms-2">This is you</Badge>}
          </small>
        </div>
      </Modal.Header>

      <Modal.Body>
        <Form onSubmit={handleSubmit}>
          {/* Phone 只读 */}
          <Form.Group className="mb-3">
            <Form.Label>Phone</Form.Label>
            <Form.Control value={user.phone || ""} disabled />
          </Form.Group>

          {/* Role：自己时禁用 */}
          <Form.Group className="mb-3">
            <Form.Label>Role</Form.Label>
            <Form.Select
              value={role}
              onChange={(e) => setRole(e.target.value)}
              disabled={isSelf}
            >
              {ROLE_OPTIONS.map((r) => (
                <option key={r} value={r}>
                  {r}
                </option>
              ))}
            </Form.Select>
            {isSelf && (
              <Form.Text className="text-muted">
                You cannot change your own role.
              </Form.Text>
            )}
          </Form.Group>

          {/* Active：自己时也禁用 */}
          <Form.Group className="mb-3">
            <Form.Label>Status</Form.Label>
            <Form.Select
              value={active ? "true" : "false"}
              onChange={(e) => setActive(e.target.value === "true")}
              disabled={isSelf}
            >
              <option value="true">Active</option>
              <option value="false">Inactive</option>
            </Form.Select>
            {isSelf && (
              <Form.Text className="text-muted">
                You cannot disable your own account.
              </Form.Text>
            )}
          </Form.Group>

          <div className="d-flex justify-content-end">
            <Button
              variant="secondary"
              className="me-2"
              onClick={onClose}
              disabled={submitting}
            >
              Cancel
            </Button>
            <Button
              type="submit"
              variant="primary"
              disabled={submitting || isSelf}
            >
              {submitting ? "Saving..." : "Save Changes"}
            </Button>
          </div>
        </Form>
      </Modal.Body>
    </Modal>
  );
};

export default ManageUserModal;

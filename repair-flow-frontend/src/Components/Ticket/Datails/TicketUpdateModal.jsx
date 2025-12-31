import React, { useState, useEffect } from "react";
import { Modal, Form, Button, Badge } from "react-bootstrap";
import { approveCustomerTicketQuote,confirmCustomerTicketPaid,markCustomerTicketDelivered,
        employeeSetQuote, employeeUpdateStatus
        } from "../../../Api/Services/TicketServices";   
const STATUS_OPTIONS = [
  "PENDING",
  "ASSIGNED",
  "QUOTED",
  "AWAITING_DEVICE",
  "DEVICE_RECEIVED",
  "IN_PROGRESS",
  "READY_FOR_CONFIRMATION",
  "PAID",
  "SHIPPED",
  "DELIVERED",
];


const EDITABLE_FIELDS_BY_ROLE = {
  CUSTOMER: [],
  EMPLOYEE: ["status", "quoteAmount"],
  ADMIN: ["status", "quoteAmount"],
};

const TicketUpdateModal = ({ show, onClose, ticket, role, onSave }) => {
  if (!ticket) return null;

  
  let userRole = typeof role === "string" ? role : role.role;
   userRole = userRole?.toUpperCase().replace(/^ROLE_/, "");

  const editableFields = EDITABLE_FIELDS_BY_ROLE[userRole] || [];
  const isEditable = (field) => editableFields.includes(field);

  const [formData, setFormData] = useState(ticket);

  useEffect(() => {
    setFormData(ticket);
  }, [ticket, show]);

  const handleChange = (field, value) => {
    setFormData((prev) => ({
      ...prev,
      [field]: value,
    }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try{
        let updatedTicket = ticket;

        if(userRole === "EMPLOYEE" || userRole === "ADMIN"){
            const promise=[];
            let lastResponse = null;
            
            // if  quoteAmount  change, call employeeSetQuote
            const originalQuoteAmount = ticket.quoteAmount ?? null;
            const newQuoteAmount = formData.quoteAmount ?? null;
            if(originalQuoteAmount !== newQuoteAmount){
                const p = employeeSetQuote(ticket.id,newQuoteAmount).then((response )=> {lastResponse = response});
                promise.push(p);
            }
            
            // if  status  change, call employeeUpdateStatus
            if(formData.status && formData.status !== ticket.status){
                const p = employeeUpdateStatus(ticket.id,formData.status).then((response )=> {lastResponse = response});
                promise.push(p);
            }
            if(promise.length > 0){
                await Promise.all(promise);
                updatedTicket = lastResponse.data.data;
            }else{
                updatedTicket = {...ticket,...formData};
            }
        }
        onSave?.(updatedTicket);
        onClose();
    }catch(error){
        console.error("Error updating ticket:", error);
        alert("Error updating ticket: " + error.message);
    }
  };

  const handleApproveQuote = async () => {
    const confirmed = window.confirm("Confirm to approve this quote?");
    if (!confirmed) return;

    try{
        const response = await approveCustomerTicketQuote(ticket.id);
        const updatedTicket = response.data.data;
        onSave?.(updatedTicket);
        onClose();
    }catch(error){
        console.error("Error approving quote:", error);
        alert("Error approving quote: " + error.message);
    }
    
  };

//   mark delivered
  const handleMarkDelivered = async () => {
    const confirmed = window.confirm("Mark this ticket as delivered?");
    if (!confirmed) return;

    try{
        const response = await markCustomerTicketDelivered(ticket.id);
        const updatedTicket = response.data.data;
        onSave?.(updatedTicket);
        onClose();
    }catch(error){
        console.error("Error marking delivered:", error);
        alert("Error marking delivered: " + error.message);
    }
  };
  
  // mark paid
  const handleConfirmPaid = async () => {
    const confirmed = window.confirm("Confirm this ticket as paid?");
    if (!confirmed) return;

    try{
        const response = await confirmCustomerTicketPaid(ticket.id);
        const updatedTicket = response.data.data;
        onSave?.(updatedTicket);
        onClose();
    }catch(error){
        console.error("Error marking paid:", error);
        alert("Error marking paid: " + error.message);
    }
  };

  return (
    <Modal show={show} onHide={onClose} centered backdrop="static">
      <Modal.Header closeButton>
        <div className="d-flex flex-column">
          <Modal.Title>Update Ticket #{ticket.id}</Modal.Title>
          <small className="text-muted">
            Current Role:{" "}
            <Badge bg="info" text="dark" className="fs-6">
              {userRole}
            </Badge>
          </small>
        </div>
      </Modal.Header>

      <Modal.Body>
        <Form onSubmit={handleSubmit}>
          {/* Title */}
          <Form.Group className="mb-3">
            <Form.Label>Title</Form.Label>
            <div className="border rounded px-3 py-2 bg-light">
              {formData.title}
            </div>
          </Form.Group>

          {/* Description */}
          <Form.Group className="mb-3">
            <Form.Label>Description</Form.Label>
            <div className="border rounded px-3 py-2 bg-light text-start">
              {formData.description}
            </div>
          </Form.Group>

          {/* Device Category */}
          <Form.Group className="mb-3">
            <Form.Label>Device Category</Form.Label>
            <div className="border rounded px-3 py-2 bg-light">
              {formData.deviceCategoryId}
            </div>
          </Form.Group>

          {/* Issue Type */}
          <Form.Group className="mb-3">
            <Form.Label>Issue Type</Form.Label>
            <div className="border rounded px-3 py-2 bg-light">
              {formData.issueTypeId}
            </div>
          </Form.Group>

          {/* Status */}
          <Form.Group className="mb-3">
            <Form.Label>Status</Form.Label>
            {isEditable("status") ? (
              <Form.Select
                value={formData.status}
                onChange={(e) => handleChange("status", e.target.value)}
              >
                {STATUS_OPTIONS.map((s) => (
                  <option key={s} value={s}>
                    {s}
                  </option>
                ))}
              </Form.Select>
            ) : (
              <div className="border rounded px-3 py-2 bg-light">
                {formData.status}
              </div>
            )}
          </Form.Group>

          {/* Quote Amount */}
          <Form.Group className="mb-3">
            <Form.Label>Quote Amount</Form.Label>
            {isEditable("quoteAmount") ? (
              <Form.Control
                type="number"
                min="0"
                value={formData.quoteAmount ?? ""}
                onChange={(e) =>
                  handleChange(
                    "quoteAmount",
                    e.target.value === "" ? null : Number(e.target.value)
                  )
                }
              />
            ) : (
              <div className="border rounded px-3 py-2 bg-light">
                {formData.quoteAmount == null
                  ? "Not Quoted"
                  : `$${formData.quoteAmount}`}
              </div>
            )}
          </Form.Group>

          {/* Payment Status */}
          <Form.Group className="mb-3">
            <Form.Label>Payment Status</Form.Label>
            <div className="border rounded px-3 py-2 bg-light">
              {formData.paid ? "Paid" : "Not Paid"}
            </div>
          </Form.Group>

          {/* Actions 区：Approve Quote / Mark Delivered */}
          {userRole === "CUSTOMER" && (
            <div className="mt-4 border-top pt-3">
              <h6 className="mb-3">Actions</h6>

              {/* Approve Quote */}
              {ticket.status === "QUOTED" && (
                <Button
                  variant="primary"
                  className="me-2"
                  onClick={handleApproveQuote}
                >
                  Approve Quote
                </Button>
              )}
                {/* Mark as Delivered */}
              {ticket.status === "READY_FOR_CONFIRMATION" && (
                <Button variant="primary" onClick={handleConfirmPaid}>
                  Mark as Paid
                </Button>
              )}
              {/* Mark as Paid */}
                {ticket.status === "SHIPPED" && (
                <Button variant="primary" onClick={handleMarkDelivered}>
                  Mark as Delivered
                </Button>
              )}
            </div>
          )}

          {/* Save Changes */}
          {userRole != "CUSTOMER" &&(
             <div className="d-flex justify-content-end mt-3">
                <Button variant="secondary" className="me-2" onClick={onClose}>
                Cancel
                </Button>
                <Button type="submit" variant="primary">
                Save Changes
                </Button>
            </div>
          )
        }
        </Form>
      </Modal.Body>
    </Modal>
  );
};

export default TicketUpdateModal;

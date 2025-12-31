import React, { useState, useEffect } from "react";
import { Container, Card, Form, Button, Alert } from "react-bootstrap";
import { useNavigate } from "react-router-dom";
import { customerCreateTicket } from "../Api/Services/TicketServices";
import { fetchDevicesCategories,fetchIssueTypes } from "../Api/Services/TicketServices";



const CreateTicketPage = () => {
  const navigate = useNavigate();

  const [formData, setFormData] = useState({
    title: "",
    description: "",
    deviceCategoryId: "",
    issueTypeId: "",
  });

  // dropdown option
  const [categories, setCategories] = useState([]);
  const [issueTypes, setIssueTypes] = useState([]);


  // loading status
  const [optionsLoading, setOptionsLoading] = useState(false);
  const [issueLoading, setIssueLoading] = useState(false);
  const [optionsError, setOptionsError] = useState(null);



  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState(null);

  useEffect(() => {
    const loadCategories = async () => {
      setOptionsLoading(true);
      setOptionsError(null);

      try{
         const res = await fetchDevicesCategories();
         const list = res.data?.data || [];
         setCategories(list);
      }catch(error){
        console.error("Error fetching device categories:", error);
        setOptionsError(error.response?.data?.message || error.message || "Failed to load categories.");
      }finally{
        setOptionsLoading(false);
      }
    };

    loadCategories();
  }, []);

  useEffect(() => {
    const loadIssueTypes = async () => {
      const categoryId = formData.deviceCategoryId;
      if(!categoryId){
        setIssueTypes([])
        setFormData((prev) => ({
          ...prev,
          issueTypeId: "",
        }));
        return;
      }

      setIssueLoading(true);
      try{
        const res = await fetchIssueTypes(categoryId);
        const list = res.data?.data || [];
        setIssueTypes(list);

        setFormData((prev) => ({
          ...prev,
          issueTypeId: list.some((it) => String(it.id) === String(prev.issueTypeId))
            ? prev.issueTypeId
            : "",
        }));
      }catch(error){
        console.error("Error fetching issue types:", error);
        setOptionsError(error.response?.data?.message || error.message || "Failed to load issue types.");
      }finally{
        setIssueLoading(false);
      }
    };

    loadIssueTypes();
  }, [formData.deviceCategoryId]);


  // handle input change

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData((prev) => ({
      ...prev,
      [name]: value,
    }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError(null);

    // check if all required fields are filled
    if (
      !formData.title.trim() ||
      !formData.description.trim() ||
      !formData.deviceCategoryId ||
      !formData.issueTypeId
    ) {
      setError("Please fill in all required fields.");
      return;
    }

    const ticketInfo = {
      title: formData.title.trim(),
      description: formData.description.trim(),
      deviceCategoryId: Number(formData.deviceCategoryId),
      issueTypeId: Number(formData.issueTypeId),
    };

    try {
      setSubmitting(true);
      const res = await customerCreateTicket(ticketInfo);
      const newTicket = res.data.data;

      // create successfully, navigate to My Tickets
      if(newTicket?.id){
        navigate(`/dashboard/tickets/${newTicket.id}`);
      }else{
        navigate(`/dashboard/tickets`);
      }
      
    } catch (err) {
      console.error("Error creating ticket:", err);
      setError(err.response?.data?.message || err.message || "Create failed");
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <Container className="py-4">
      <Card className="shadow-sm mx-auto" style={{ maxWidth: "720px" }}>
        <Card.Header className="bg-light">
          <h4 className="mb-0">Create New Ticket</h4>
        </Card.Header>

        <Card.Body>
          {error && <Alert variant="danger">{error}</Alert>}

          <Form onSubmit={handleSubmit}>
            {/* Title */}
            <Form.Group className="mb-3">
              <Form.Label>Title</Form.Label>
              <Form.Control
                type="text"
                name="title"
                maxLength={128}
                value={formData.title}
                onChange={handleChange}
                placeholder="e.g. iPhone 13 screen cracked"
                required
              />
            </Form.Group>

            {/* Description */}
            <Form.Group className="mb-3">
              <Form.Label>Description</Form.Label>
              <Form.Control
                as="textarea"
                rows={3}
                name="description"
                value={formData.description}
                onChange={handleChange}
                placeholder="Describe what happened to your device..."
                required
              />
            </Form.Group>

            {/* Device Category */}
            <Form.Group className="mb-3">
              <Form.Label>Device Category</Form.Label>
              <Form.Select
                name="deviceCategoryId"
                value={formData.deviceCategoryId}
                onChange={handleChange}
                required
                disabled={optionsLoading}
              >
                <option value="">Select category...</option>
                {categories.map((cat) => (
                  <option key={cat.id} value={cat.id}>
                    {cat.name}
                  </option>
                ))}
              </Form.Select>
            </Form.Group>

            {/* Issue Type */}
            <Form.Group className="mb-3">
              <Form.Label>Issue Type</Form.Label>
              <Form.Select
                name="issueTypeId"
                value={formData.issueTypeId}
                onChange={handleChange}
                required
              >
                <option value="">Select issue type...</option>
                {issueTypes.map((issue) => (
                  <option key={issue.id} value={issue.id}>
                    {issue.name}
                  </option>
                ))}
              </Form.Select>
            </Form.Group>

            <div className="d-flex justify-content-end">
              <Button
                type="submit"
                variant="primary"
                disabled={submitting}
              >
                {submitting ? "Creating..." : "Create Ticket"}
              </Button>
            </div>
          </Form>
        </Card.Body>
      </Card>
    </Container>
  );
};

export default CreateTicketPage;

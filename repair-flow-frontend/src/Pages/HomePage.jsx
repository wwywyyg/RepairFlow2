import React from 'react';
import { Container,Row,Col,Button,Navbar,Nav,Image } from 'react-bootstrap';
import { Link } from 'react-router-dom';

const HomePage = () => {
    return(
        <>
        {/* Top Navigation Bar  */}
        <Navbar bg="white" expand = "lg" className="shadow-sm sticky-top">
            <Container>
                {/* Logo */}
                <Navbar.Brand href="/" className="fw-bold text-primary fs-3">
                    RepairFlow
                </Navbar.Brand> 

                <Navbar.Toggle aria-controls="basic-navbar-nav"/>
                <Navbar.Collapse id="basic-navbar-nav">
                    <Nav className="ms-auto">
                        <Link to="/login">
                            <Button variant="primary" className="px-4 fw-bold">Register / Login</Button>
                        </Link>
                    </Nav>
                </Navbar.Collapse>

            </Container>
        </Navbar>

        {/* Hero Section */}
        <section className="py-5 bg-light border-bottom">
            <Container>
                <Row className="align-items-center min-vh-50">
                    <Col lg={6} className="py-5">
                        <h1 className="display-4 fw-bold text-dark mb-4">
                            Easy Post Ticket<br/>
                            <span className="text-primary">Manage Repairs with Confidence</span>
                        </h1>
                        <p className="lead text-muted mb-4">
                            An all-in-one repair workflow for small electronics service teams.
                        </p>
                        <div className="d-flex gap-3">
                            <Link to="/login">
                                <Button variant = "primary" size="lg" className="px-4 shadow-sm">LET'S START</Button>
                            </Link>
                            <Button variant="outline-primary" size="lg" className="px-4">CONTRACT US</Button>
                        </div>
                    </Col> 

                    {/* right side Image */}
                    <Col lg={6} className="text-center ">
                    <Image src="images/right_image_3.png" fluid className="rounded-3" alt="Reapir Service Illurstration"/>
                    </Col>
                </Row>
            </Container>
        </section>
        
        {/* Function Features */}
        <section className="py-4 bg-white">
            <Container>
                <Row className="text-center g-4">
                {/* feature 1 , post ticket */}
                    <Col md={4}>
                        <div className="p-3">
                            <div className="mb-3">
                                <Image src="images/function_1_1_1.png" fluid className="rounded-3" alt="Post ticket image"/>
                            </div>
                            <h3 className="fw-bold">Create Tickets in Seconds</h3>
                            <p className="text-muted mt-3">Enter device details and issue description to create a repair ticket instantly. Tasks are assigned automatically.</p>
                        </div>
                    </Col>

                {/* feature 2, Direct Communication  */}
                    <Col md={4}>
                        <div className="p-3">
                            <div className="mb-3">
                                <Image src="images/function_2_2_2.png" fluid className="rounded-3" alt=" Direct Communication image"/>
                            </div>
                            <h3 className="fw-bold">Clear, Direct Communication</h3>
                            <p className="text-muted mt-3">Built-in real-time chat lets technicians and customers communicate directly, with images and status updates.</p>
                        </div>
                    </Col>

                {/* feature 3 Track Every Repair */}
                    <Col md={4}>
                        <div className="p-3 ">
                            <div className="mb-3">
                                <Image src="images/function_3_3_3.png" fluid className="rounded-3" alt="Track Every Repair image"/>
                            </div>
                            <h3 className="fw-bold">Track Every Repair</h3>
                            <p className="text-muted mt-3">Track every stage of the repair process — from quote pending to in progress and completed — all updated in real time.</p>
                        </div>
                    </Col>
                </Row>
            </Container>
        </section>
        </>
    )
}

export default HomePage;
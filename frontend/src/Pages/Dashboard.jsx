import React from "react";
import {Container , Row,Col} from "react-bootstrap"
import { Outlet } from "react-router-dom";
import SideBar from "../Components/SideBar";





const DashBoard = () => {

    const mockRole = 'customer';
    const roleTextMap  = {
        admin: 'Admin Online',
        employee: 'Employee Online',
        customer: 'Customer Online'
    }
    

    return(
        <>
        <Container fluid className="p-0">
            <Row className="g-0">

                {/* left: sidebar  2 of 5 */}
                <Col lg={2} md={3} className="d-none d-md-block">
                    <SideBar/>
                </Col>

                {/* right: main content(outlet) */}
                <Col lg={10} md={9} className="bg-light min-vh-100">
                {/* simple header */}
                <div className="bg-white shadow-sm p-3 mb-4 d-flex justify-content-between align-items-center h-auto" >
                    <h5 className="m-0 text-secondary">Working Station</h5>
                    <span className="badge bg-primary fs-6">{roleTextMap[mockRole] ?? "Unknow Role"}</span>
                </div>

                <div className="px-4">
                    <Outlet/>
                </div>

                </Col>
            </Row>
        </Container>
        </>
    )
}

export default DashBoard;
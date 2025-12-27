import React from "react";
import { Nav } from "react-bootstrap";
import {Link, NavLink} from 'react-router-dom'
import { MOCK_USERS } from "../Data/MockData";

const MOCK_ROLE = MOCK_USERS.customer.role;


const SideBar = () => {
    return(
        <>
            <div className="bg-dark border-end vh-100 d-flex flex-column p-3" >
                <h4 className="text-primary fw-bold mb-4 px-3">RepairFlow</h4>

                <Nav className="flex-column gap-3">
                {/* anyone can see  */}
                <Nav.Item>        
                    <NavLink  to="/dashboard" end  
                    className={({isActive}) => `nav-link fs-6 text-decoration-none ${isActive ? "text-white fw-semibold" : "text-secondary"}` }> 
                    Dashboard Main Page
                    </NavLink>    
                </Nav.Item>


                {/* for customer */}
                {MOCK_ROLE === 'customer' && (
                    <Nav.Item>
                        <NavLink  to="/dashboard/customer/create-ticket" end 
                        className={({isActive}) => `nav-link fs-5 text-decoration-none px-3 py-2 rounded ${isActive ? "text-white fw-semibold" : "text-secondary"}` }>
                            Create New Ticket
                        </NavLink>  
                        
                        <NavLink  to="/dashboard/tickets" end 
                        className={({isActive}) => `nav-link fs-5 text-decoration-none px-3 py-2 rounded ${isActive ? "text-white fw-semibold" : "text-secondary"}`}>
                            My Tickets
                        </NavLink>
                    </Nav.Item>
                )}


                {/* for employee */}
                {MOCK_ROLE === 'employee' &&(
                    <Nav.Item>
                        <NavLink  to="/dashboard/employee/available-tickets" end 
                        className={({isActive}) => `nav-link fs-5 text-decoration-none px-3 py-2 rounded ${isActive ? "text-white fw-semibold" : "text-secondary"}`}>
                            Available Tickets
                        </NavLink>
                        
                        <NavLink  to="/dashboard/tickets" end 
                        className={({isActive}) => `nav-link fs-5 text-decoration-none px-3 py-2 rounded ${isActive ? "text-white fw-semibold" : "text-secondary"}`}>
                            My Tickets
                        </NavLink>
                    </Nav.Item>
                )}

                {/* for admin */}
                {MOCK_ROLE === 'admin'&&(
                    <Nav.Item>
                        <NavLink  to="/dashboard/tickets" end 
                        className={({isActive}) => `nav-link fs-5 text-decoration-none px-3 py-2 rounded ${isActive ? "text-white fw-semibold" : "text-secondary"}`}>
                            All Tickets
                        </NavLink>

                        <NavLink  to="/dashboard/admin/all-users" end 
                        className={({isActive}) => `nav-link fs-5 text-decoration-none px-3 py-2 rounded ${isActive ? "text-white fw-semibold" : "text-secondary"}`}>
                            All Users
                        </NavLink>
                    </Nav.Item>
                )}
                </Nav>

                {/* log out button */}  
                <div className="mt-auto border-top pt-3 px-4">
                    <NavLink as={Link} to="/" className="text-danger fs-5 text-decoration-none px-3 py-2 rounded" >
                        Log Out
                    </NavLink>
                </div>
            </div>

        </>
    )

}

export default SideBar;
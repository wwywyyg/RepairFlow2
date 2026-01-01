import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
//page components
import HomePage from "./Pages/HomePage";
import AuthPage from "./Pages/AuthPage";
import Dashboard from "./Pages/Dashboard"
import CreateTicketPage from './Pages/CreateTicketPage';

// business components
import TicketList from './Components/Ticket/TicketList';
import TicketDetail from './Components/Ticket/Datails/TicketDetail';


function App() {

  return (
    <BrowserRouter>
      <Routes>
        {/* To Home page */}
        <Route path="/" element={<HomePage/>} />  

        {/* to Auth Page */}
        <Route path="/login" element={<AuthPage/>} />

        {/* to DashBoard */}
        <Route path="/dashboard" element={<Dashboard/>}>
            <Route index element={
              <div className='d-flex justify-content-center align-items-center h-100'>
                <div className='text-center'>
                  <h2 className='mb-3'>Welcome Back!!</h2>
                  <p className='text-muted'>Select an item from the sidebar to start.</p>
                </div>
              </div>
            }/>
          {/* ticket list    | SideBar  my-tickets */}
          <Route path='tickets' element={<TicketList type="POOL"/>}/>
          <Route path="available-tickets" element={<TicketList type="AVAILABLE"/>}/>
          <Route path="all-tickets" element={<TicketList type="ADMIN"/>}/>
          {/* customer create ticket */}
          <Route path="customer/create-ticket" element={<CreateTicketPage/>}/>
            
          {/* ticket details | ticket Details */}
          <Route path='tickets/:ticketId' element={<TicketDetail/>}/>
        </Route>
      </Routes>
    </BrowserRouter>
  );
}

export default App

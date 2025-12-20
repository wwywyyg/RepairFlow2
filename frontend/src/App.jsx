import { BrowserRouter, Routes, Route } from 'react-router-dom';
// import HomePage from "./Pages/HomePage";
// import AuthPage from "./Pages/AuthPage";
// import Dashboard from "./Pages/Dashboard"
import TicketCard from "./Components/Ticket/TicketCard"
function App() {

  return (
    <BrowserRouter>
      <TicketCard/>

      {/* <Routes> */}
        {/* To Home page */}
        {/* <Route path="/" element={<HomePage/>} />   */}

        {/* to Auth Page */}
        {/* <Route path="/login" element={<AuthPage/>} /> */}

        {/* to DashBoard */}
        {/* <Route path="/dashboard" element={<Dashboard/>}> */}
          {/* <Route index element={ */}
            {/* <div className="d-flex justify-content-center align-items-center h-100"> */}
              {/* <h2 className="text-center">Welcome Back!!</h2> */}
            {/* </div> */}
          {/* }/> */}
        {/* </Route> */}
        



      {/* </Routes> */}
    </BrowserRouter>
  );
}

export default App

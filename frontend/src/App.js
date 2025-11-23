import React from 'react';
import RegisterPage from './Pages/RegisterPage';
import LoginPage from './Pages/LoginPage';

function Home() {
  const userJson = localStorage.getItem('rf_user');
  let user = null;

    if (userJson) {
    try {
      user = JSON.parse(userJson);
    } catch (e) {
      console.error('Failed to parse rf_user from localStorage:', userJson, e);
      user = null;
    }
  }

  return (
    <div className="container mt-5">
      <h3>RepairFlow Home</h3>
      {user ? (
        <>
          <p>Welcome, {user.firstName} ({user.email})</p>
          <button
            className="btn btn-outline-danger"
            onClick={() => {
              localStorage.removeItem('rf_token');
              localStorage.removeItem('rf_user');
              window.location.href = '/login';
            }}
          >
            Logout
          </button>
        </>
      ) : (
        <p>
          Please <a href="/login">login</a> or <a href="/register">register</a>.
        </p>
      )}
    </div>
  );
}

export default function App() {
  const path = window.location.pathname;
  if (path === '/login') return <LoginPage />;
  if (path === '/register') return <RegisterPage />;
  return <Home />;
}
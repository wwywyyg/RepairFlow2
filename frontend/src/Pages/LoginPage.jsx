import React, { useState } from 'react';
import { login } from '../api/authApi';

export default function LoginPage() {
  const [form, setForm] = useState({ email: '', password: '' });
  const [error, setError] = useState('');

  const handleChange = (e) => {
    setForm(prev => ({ ...prev, [e.target.name]: e.target.value }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    try {
      const res = await login(form);
      console.log('login response:', res.data);
      // 这里假设你的返回格式是 ResponseMessage<UserLoginResponse>
      const data = res.data.data;
      const token = data.token;
      const user = data.user;

    if (!token || !user) {
      throw new Error('No token or user in response');
    }

      
      localStorage.setItem('rf_token', token);
      localStorage.setItem('rf_user', JSON.stringify(user));

      // 登录成功后跳到一个简单首页
      window.location.href = '/';
    } catch (err) {
      const msg = err.response?.data?.message || 'Login failed';
      setError(msg);
    }
  };

  return (
    <div className="container mt-5">
      <div className="row justify-content-center">
        <div className="col-md-4">
          <h3 className="mb-3 text-center">Login</h3>
          {error && <div className="alert alert-danger">{error}</div>}

          <form onSubmit={handleSubmit}>
            <div className="mb-3">
              <label className="form-label">Email</label>
              <input
                type="email"
                className="form-control"
                name="email"
                value={form.email}
                onChange={handleChange}
                required
              />
            </div>
            <div className="mb-3">
              <label className="form-label">Password</label>
              <input
                type="password"
                className="form-control"
                name="password"
                value={form.password}
                onChange={handleChange}
                required
              />
            </div>
            <button className="btn btn-primary w-100" type="submit">
              Login
            </button>
          </form>

          <p className="mt-3 text-center">
            No account? <a href="/register">Register</a>
          </p>
          <p className="mt-3 text-center">
            <a href="/home">Back to Home</a>
          </p>
        </div>
      </div>
    </div>
  );
}
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.Map" %>
<%
    String email = (String) request.getAttribute("email");
    if (email == null) email = "";

    @SuppressWarnings("unchecked")
    Map<String, String> errors = (Map<String, String>) request.getAttribute("errors");
    if (errors == null) errors = new java.util.HashMap<>();

    String generalError = errors.get("general");
    String emailError = errors.get("email");
    String passwordError = errors.get("password");
%>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Sign In &#8212; WealthFocus</title>
  <link rel="stylesheet" href="<%= request.getContextPath() %>/static/css/main.css">
  <style>
    body {
      display: flex;
      align-items: center;
      justify-content: center;
      min-height: 100vh;
    }
    .auth-card {
      width: 100%;
      max-width: 420px;
      padding: 40px;
      background: var(--surface);
      border: 1px solid var(--border);
      border-radius: var(--radius);
      box-shadow: var(--shadow-lg);
    }
    .auth-header { text-align: center; margin-bottom: 32px; }
    .auth-header .auth-logo {
      width: 48px; height: 48px;
      background: var(--accent-btn);
      border-radius: 12px;
      display: inline-flex; align-items: center; justify-content: center;
      margin-bottom: 16px;
    }
    .auth-header .auth-logo::after {
      content: '';
      width: 20px; height: 20px;
      background: #fff;
      border-radius: 3px;
      transform: rotate(45deg);
    }
    .auth-header h1 {
      font-size: 24px; font-weight: 700; color: var(--text);
      margin: 0 0 4px;
    }
    .auth-header p {
      font-size: 14px; color: var(--text-muted); margin: 0;
    }
    .auth-error {
      background: var(--red-soft);
      border: 1px solid rgba(239,68,68,0.2);
      color: var(--red);
      padding: 10px 14px;
      border-radius: var(--radius-xs);
      margin-bottom: 20px;
      font-size: 13px;
    }
    .auth-field { margin-bottom: 20px; }
    .auth-field label {
      display: block; font-size: 13px; font-weight: 500;
      color: var(--text); margin-bottom: 6px;
    }
    .auth-field input {
      width: 100%; height: 44px; padding: 0 14px;
      background: #f9f8f5; border: 1px solid var(--border);
      border-radius: var(--radius-xs);
      font-size: 14px; color: var(--text); outline: none;
    }
    .auth-field input:focus { border-color: #a3a097; }
    .auth-field .field-error {
      color: var(--red); font-size: 12px; margin-top: 4px;
    }
    .auth-submit {
      width: 100%; height: 44px;
      background: var(--accent-btn); color: #fff;
      border: none; border-radius: var(--radius-xs);
      font-size: 15px; font-weight: 600;
      cursor: pointer; transition: background 0.15s;
    }
    .auth-submit:hover { background: #2d2d3d; }
    .auth-footer {
      text-align: center; margin-top: 24px;
      font-size: 14px; color: var(--text-muted);
    }
    .auth-footer a { color: var(--accent-btn); font-weight: 600; }
    .auth-footer a:hover { text-decoration: underline; }
  </style>
</head>
<body>
  <div class="auth-card">
    <div class="auth-header">
      <div class="auth-logo"></div>
      <h1>Welcome Back</h1>
      <p>Sign in to access your financial dashboard</p>
    </div>

    <% if (generalError != null) { %>
      <div class="auth-error"><%= generalError %></div>
    <% } %>

    <form method="post" action="<%= request.getContextPath() %>/login">
      <div class="auth-field">
        <label for="email">Email Address</label>
        <input type="email" id="email" name="email"
               value="<%= email.replace("\"", "&quot;") %>"
               placeholder="Enter your email" required>
        <% if (emailError != null) { %>
          <div class="field-error"><%= emailError %></div>
        <% } %>
      </div>

      <div class="auth-field">
        <label for="password">Password</label>
        <input type="password" id="password" name="password"
               placeholder="Enter your password" required>
        <% if (passwordError != null) { %>
          <div class="field-error"><%= passwordError %></div>
        <% } %>
      </div>

      <button type="submit" class="auth-submit">Sign In</button>
    </form>

    <div class="auth-footer">
      Don't have an account? <a href="<%= request.getContextPath() %>/register">Create one</a>
    </div>
  </div>
</body>
</html>

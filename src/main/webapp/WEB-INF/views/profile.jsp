<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.wealthfocus.model.User" %>
<%@ page import="java.time.format.DateTimeFormatter" %>
<%
    User user = (User) request.getAttribute("user");
    String success = (String) request.getAttribute("success");
    String error = (String) request.getAttribute("error");

    String name = user.getName() != null ? user.getName() : "";
    String email = user.getEmail() != null ? user.getEmail() : "";
    String phone = user.getPhone() != null ? user.getPhone() : "";
    String address = user.getAddress() != null ? user.getAddress() : "";
    boolean hasPhoto = user.getPhotoPath() != null && !user.getPhotoPath().isEmpty();

    DateTimeFormatter fmt = DateTimeFormatter.ofPattern("MMM dd, yyyy");
    String memberSince = user.getCreatedAt() != null ? user.getCreatedAt().format(fmt) : "—";
    String lastUpdated = user.getUpdatedAt() != null ? user.getUpdatedAt().format(fmt) : "—";

    String initials = "";
    if (!name.isEmpty()) {
        String[] parts = name.trim().split("\\s+");
        initials = String.valueOf(parts[0].charAt(0));
        if (parts.length > 1) initials += parts[parts.length - 1].charAt(0);
        initials = initials.toUpperCase();
    }
%>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>My Profile &#8212; WealthFocus</title>
  <link rel="stylesheet" href="<%= request.getContextPath() %>/static/css/main.css">
  <style>
    .profile-grid {
      display: grid;
      grid-template-columns: 300px 1fr;
      gap: 24px;
      margin-bottom: 24px;
    }
    @media (max-width: 900px) { .profile-grid { grid-template-columns: 1fr; } }

    /* Photo card */
    .photo-card {
      background: var(--surface);
      border: 1px solid var(--border);
      border-radius: var(--radius);
      padding: 32px;
      box-shadow: var(--shadow);
      display: flex;
      flex-direction: column;
      align-items: center;
      text-align: center;
    }
    .avatar-wrap {
      position: relative;
      width: 140px; height: 140px;
      margin-bottom: 20px;
      cursor: pointer;
    }
    .avatar-wrap .avatar {
      width: 140px; height: 140px;
      border-radius: 50%;
      object-fit: cover;
      border: 4px solid var(--border-light);
    }
    .avatar-wrap .avatar-placeholder {
      width: 140px; height: 140px;
      border-radius: 50%;
      background: linear-gradient(135deg, #1c1c28 0%, #3a3a4d 100%);
      display: flex;
      align-items: center;
      justify-content: center;
      color: #fff;
      font-size: 42px;
      font-weight: 700;
      letter-spacing: 1px;
      border: 4px solid var(--border-light);
    }
    .avatar-wrap .avatar-overlay {
      position: absolute;
      inset: 0;
      border-radius: 50%;
      background: rgba(0,0,0,0.45);
      display: flex;
      align-items: center;
      justify-content: center;
      opacity: 0;
      transition: opacity 0.2s;
    }
    .avatar-wrap:hover .avatar-overlay { opacity: 1; }
    .avatar-overlay svg { color: #fff; }
    .photo-card .user-name {
      font-size: 20px;
      font-weight: 700;
      color: var(--text);
      margin-bottom: 4px;
    }
    .photo-card .user-email {
      font-size: 13px;
      color: var(--text-muted);
      margin-bottom: 16px;
    }
    .photo-card .photo-hint {
      font-size: 12px;
      color: var(--text-muted);
    }

    /* Details card */
    .details-card {
      background: var(--surface);
      border: 1px solid var(--border);
      border-radius: var(--radius);
      padding: 32px;
      box-shadow: var(--shadow);
    }
    .details-card h2 {
      font-size: 18px;
      font-weight: 700;
      color: var(--text);
      margin-bottom: 24px;
    }
    .profile-form .field-row {
      display: grid;
      grid-template-columns: 1fr 1fr;
      gap: 16px;
      margin-bottom: 16px;
    }
    @media (max-width: 600px) { .profile-form .field-row { grid-template-columns: 1fr; } }
    .profile-form .field-full {
      margin-bottom: 16px;
    }
    .profile-form label {
      display: block;
      font-size: 13px;
      font-weight: 500;
      color: var(--text-secondary);
      margin-bottom: 6px;
    }
    .profile-form input,
    .profile-form textarea {
      width: 100%;
      height: 44px;
      padding: 0 14px;
      background: #f9f8f5;
      border: 1px solid var(--border);
      border-radius: var(--radius-xs);
      font-size: 14px;
      color: var(--text);
      font-family: inherit;
      outline: none;
      transition: border-color 0.15s;
      box-sizing: border-box;
    }
    .profile-form textarea {
      height: 80px;
      padding: 10px 14px;
      resize: vertical;
    }
    .profile-form input:focus,
    .profile-form textarea:focus { border-color: #a3a097; }
    .profile-form .form-actions {
      display: flex;
      gap: 12px;
      margin-top: 24px;
    }

    /* Account info */
    .account-info {
      background: var(--surface);
      border: 1px solid var(--border);
      border-radius: var(--radius);
      padding: 24px 32px;
      box-shadow: var(--shadow);
    }
    .account-info h2 {
      font-size: 16px;
      font-weight: 700;
      color: var(--text);
      margin-bottom: 16px;
    }
    .info-grid {
      display: grid;
      grid-template-columns: repeat(3, 1fr);
      gap: 24px;
    }
    @media (max-width: 700px) { .info-grid { grid-template-columns: 1fr; } }
    .info-item .info-label {
      font-size: 12px;
      color: var(--text-muted);
      text-transform: uppercase;
      letter-spacing: 0.04em;
      margin-bottom: 4px;
    }
    .info-item .info-value {
      font-size: 15px;
      font-weight: 600;
      color: var(--text);
    }

    /* Alerts */
    .alert {
      padding: 12px 16px;
      border-radius: var(--radius-xs);
      font-size: 13px;
      margin-bottom: 20px;
    }
    .alert-success {
      background: var(--green-soft);
      border: 1px solid rgba(34,197,94,0.2);
      color: var(--green);
    }
    .alert-error {
      background: var(--red-soft);
      border: 1px solid rgba(239,68,68,0.2);
      color: var(--red);
    }
  </style>
</head>
<body>
<div class="app">

  <!-- Sidebar (same as dashboard) -->
  <aside class="sidebar">
    <div class="logo"></div>
    <nav>
      <a href="<%= request.getContextPath() %>/" title="Dashboard">
        <svg width="20" height="20" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" viewBox="0 0 24 24"><rect x="3" y="3" width="7" height="7" rx="1"/><rect x="14" y="3" width="7" height="7" rx="1"/><rect x="3" y="14" width="7" height="7" rx="1"/><rect x="14" y="14" width="7" height="7" rx="1"/></svg>
      </a>
      <a href="#" title="Analytics">
        <svg width="20" height="20" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" viewBox="0 0 24 24"><polyline points="22 12 18 12 15 21 9 3 6 12 2 12"/></svg>
      </a>
      <a href="#" title="Wallet">
        <svg width="20" height="20" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" viewBox="0 0 24 24"><rect x="2" y="5" width="20" height="14" rx="2"/><path d="M16 12h.01"/></svg>
      </a>
      <a href="#" title="Reports">
        <svg width="20" height="20" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" viewBox="0 0 24 24"><path d="M14 2H6a2 2 0 00-2 2v16a2 2 0 002 2h12a2 2 0 002-2V8z"/><polyline points="14 2 14 8 20 8"/><line x1="16" y1="13" x2="8" y2="13"/><line x1="16" y1="17" x2="8" y2="17"/></svg>
      </a>
      <a href="<%= request.getContextPath() %>/profile" class="active" title="Profile">
        <svg width="20" height="20" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" viewBox="0 0 24 24"><path d="M20 21v-2a4 4 0 00-4-4H8a4 4 0 00-4 4v2"/><circle cx="12" cy="7" r="4"/></svg>
      </a>
    </nav>
    <div class="sidebar-bottom">
      <form method="post" action="<%= request.getContextPath() %>/logout">
        <button type="submit" title="Sign out">
          <svg width="20" height="20" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" viewBox="0 0 24 24"><path d="M9 21H5a2 2 0 01-2-2V5a2 2 0 012-2h4"/><polyline points="16 17 21 12 16 7"/><line x1="21" y1="12" x2="9" y2="12"/></svg>
        </button>
      </form>
    </div>
  </aside>

  <div class="main">
    <header class="topbar">
      <div class="breadcrumb">Home / <strong>Profile</strong></div>
      <div class="search-wrap">
        <span class="search-icon">&#x2315;</span>
        <input type="text" placeholder="Search..." disabled>
      </div>
      <div class="right">
        <a href="<%= request.getContextPath() %>/" class="btn-primary">Back to Dashboard</a>
      </div>
    </header>

    <main class="content">
      <div class="hero-section">
        <h1 style="font-size:28px;font-weight:800;margin-bottom:4px;">My Profile</h1>
        <p style="font-size:14px;color:var(--text-muted);">Manage your personal information and preferences</p>
      </div>

      <% if (success != null) { %>
        <div class="alert alert-success"><%= success %></div>
      <% } %>
      <% if (error != null) { %>
        <div class="alert alert-error"><%= error %></div>
      <% } %>

      <form method="post" action="<%= request.getContextPath() %>/profile" enctype="multipart/form-data" class="profile-form">

        <div class="profile-grid">
          <!-- Left: Photo -->
          <div class="photo-card">
            <div class="avatar-wrap" onclick="document.getElementById('photoInput').click()">
              <% if (hasPhoto) { %>
                <img class="avatar" src="<%= request.getContextPath() %>/profile/photo?t=<%= System.currentTimeMillis() %>" alt="Profile photo">
              <% } else { %>
                <div class="avatar-placeholder"><%= initials %></div>
              <% } %>
              <div class="avatar-overlay">
                <svg width="28" height="28" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" viewBox="0 0 24 24"><path d="M23 19a2 2 0 01-2 2H3a2 2 0 01-2-2V8a2 2 0 012-2h4l2-3h6l2 3h4a2 2 0 012 2z"/><circle cx="12" cy="13" r="4"/></svg>
              </div>
            </div>
            <div class="user-name"><%= name.replace("<", "&lt;") %></div>
            <div class="user-email"><%= email.replace("<", "&lt;") %></div>
            <input type="file" id="photoInput" name="photo" accept="image/*" style="display:none"
                   onchange="previewPhoto(this)">
            <div class="photo-hint">Click photo to change (max 5 MB)</div>
          </div>

          <!-- Right: Details -->
          <div class="details-card">
            <h2>Personal Information</h2>

            <div class="field-row">
              <div>
                <label for="name">Full Name</label>
                <input type="text" id="name" name="name"
                       value="<%= name.replace("\"", "&quot;") %>"
                       placeholder="Your full name" required>
              </div>
              <div>
                <label for="email">Email Address</label>
                <input type="email" id="email" name="email"
                       value="<%= email.replace("\"", "&quot;") %>"
                       placeholder="your@email.com" required>
              </div>
            </div>

            <div class="field-row">
              <div>
                <label for="phone">Phone Number</label>
                <input type="tel" id="phone" name="phone"
                       value="<%= phone.replace("\"", "&quot;") %>"
                       placeholder="+977 98XXXXXXXX">
              </div>
              <div>
                <label for="address">Address</label>
                <input type="text" id="address" name="address"
                       value="<%= address.replace("\"", "&quot;") %>"
                       placeholder="City, Country">
              </div>
            </div>

            <div class="form-actions">
              <button type="submit" class="btn btn-dark">Save Changes</button>
              <a href="<%= request.getContextPath() %>/" class="btn btn-outline">Cancel</a>
            </div>
          </div>
        </div>

      </form>

      <!-- Account info -->
      <div class="account-info">
        <h2>Account Information</h2>
        <div class="info-grid">
          <div class="info-item">
            <div class="info-label">Member Since</div>
            <div class="info-value"><%= memberSince %></div>
          </div>
          <div class="info-item">
            <div class="info-label">Last Updated</div>
            <div class="info-value"><%= lastUpdated %></div>
          </div>
          <div class="info-item">
            <div class="info-label">Account ID</div>
            <div class="info-value" style="font-size:12px;font-family:monospace;"><%= user.getId() %></div>
          </div>
        </div>
      </div>

    </main>

    <footer>&copy; 2026 WealthFocus. All rights reserved.</footer>
  </div>
</div>

<script>
function previewPhoto(input) {
  if (input.files && input.files[0]) {
    var file = input.files[0];
    if (file.size > 5 * 1024 * 1024) {
      alert('File size must be under 5 MB');
      input.value = '';
      return;
    }
    var reader = new FileReader();
    reader.onload = function(e) {
      var wrap = document.querySelector('.avatar-wrap');
      var existing = wrap.querySelector('.avatar, .avatar-placeholder');
      if (existing) existing.remove();
      var img = document.createElement('img');
      img.className = 'avatar';
      img.src = e.target.result;
      img.alt = 'Profile photo';
      wrap.insertBefore(img, wrap.firstChild);
    };
    reader.readAsDataURL(file);
  }
}
</script>
</body>
</html>

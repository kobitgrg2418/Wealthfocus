<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%
  String today = java.time.LocalDate.now().toString();
  request.setAttribute("today", today);
  String currentPreset = ((com.wealthfocus.util.TimeRangeUtil.Range) request.getAttribute("range")).preset;
  request.setAttribute("currentPreset", currentPreset);
%>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>WealthFocus — Personal Finance Intelligence</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/main.css">
  <script src="https://cdn.jsdelivr.net/npm/chart.js@4.4.1/dist/chart.umd.min.js"></script>
</head>
<body>
<div class="app">
  <aside class="sidebar">
    <div class="logo"></div>
    <nav>
      <a href="${pageContext.request.contextPath}/" class="active" title="Overview">▦</a>
      <a href="#" title="Analytics">▤</a>
      <a href="#" title="Cards">▢</a>
      <a href="#" title="Banks">▣</a>
      <a href="#" title="Loans">▥</a>
      <a href="#" title="Messages">✉</a>
    </nav>
    <a href="#" class="" title="Sign out" style="color:#6b7280;width:44px;height:44px;display:flex;align-items:center;justify-content:center;">⏏</a>
  </aside>

  <div class="main">
    <header class="topbar">
      <div class="search">
        <input type="text" placeholder="Search Transaction..." disabled>
      </div>
      <div class="right">
        <button class="icon-btn" title="Toggle theme">☀</button>
        <button class="icon-btn" title="Notifications">🔔</button>
        <div class="avatar"></div>
      </div>
    </header>

    <main class="content">
      <c:if test="${not empty param.error}">
        <div class="error-banner">${param.error}</div>
      </c:if>

      <p class="greeting">👋 Welcome In, Jhon!</p>
      <h1 class="title">Financial Overview</h1>

      <div class="pills">
        <a class="pill ${currentPreset == 'current-month' ? 'active' : ''}" href="${pageContext.request.contextPath}/?period=current-month">Current Month</a>
        <a class="pill ${currentPreset == 'last-month' ? 'active' : ''}" href="${pageContext.request.contextPath}/?period=last-month">Last Month</a>
        <a class="pill ${currentPreset == 'last-3-months' ? 'active' : ''}" href="${pageContext.request.contextPath}/?period=last-3-months">Last 3 Months</a>
        <a class="pill ${currentPreset == 'last-6-months' ? 'active' : ''}" href="${pageContext.request.contextPath}/?period=last-6-months">Last 6 Months</a>
        <a class="pill ${currentPreset == 'current-year' ? 'active' : ''}" href="${pageContext.request.contextPath}/?period=current-year">Current Year</a>
      </div>

      <!-- Hero: Balance + Score -->
      <div class="hero-grid">
        <div class="card">
          <h2><span class="badge">💰</span> My Balance</h2>
          <div class="balance-inner">
            <div class="credit-card">
              <div class="row">
                <div class="brand">CreditLynx</div>
                <div class="chip"></div>
              </div>
              <div class="number">8763 2736 9873 0329</div>
              <div class="meta">
                <div>
                  <div class="label">Card Holder</div>
                  <div>JHON DOE</div>
                </div>
                <div style="text-align: right;">
                  <div class="label">Expires</div>
                  <div>10/28</div>
                </div>
                <div style="display:flex;gap:2px;">
                  <span style="display:inline-block;width:18px;height:18px;border-radius:50%;background:rgba(239,68,68,0.85);"></span>
                  <span style="display:inline-block;width:18px;height:18px;border-radius:50%;background:rgba(251,191,36,0.85);margin-left:-6px;"></span>
                </div>
              </div>
            </div>
            <div class="balance-summary">
              <div class="header">
                <div>
                  <div class="label">Net Savings</div>
                  <div class="amount">${netSavings}</div>
                </div>
                <div class="legend">
                  <span><span class="dot income"></span>Income</span>
                  <span><span class="dot expense"></span>Expense</span>
                </div>
              </div>
              <canvas id="monthlyChart"></canvas>
            </div>
          </div>
        </div>

        <div class="card score-card">
          <div class="top">
            <div>
              <div class="num">${score}</div>
              <div class="label">${scoreLabel}</div>
            </div>
            <div class="trend">+6 ↗</div>
          </div>
          <canvas id="scoreGauge" data-score="${score}" style="width:200px;height:200px;margin:8px auto 0;display:block;"></canvas>
          <div style="font-size: 11px; color: var(--muted); margin-top: 8px;">Updated just now</div>
        </div>
      </div>

      <!-- Stat tiles -->
      <div class="stats">
        <div class="card stat">
          <div class="label">Total Income</div>
          <div class="value pos">${totalIncome}</div>
        </div>
        <div class="card stat">
          <div class="label">Total Expenses</div>
          <div class="value neg">${totalExpenses}</div>
        </div>
        <div class="card stat">
          <div class="label">Net Savings</div>
          <div class="value ${netSavingsNonNegative ? 'pos' : 'neg'}">${netSavings}</div>
        </div>
      </div>

      <!-- Income & Expense managers -->
      <div class="two-col">
        <!-- INCOME -->
        <div class="card">
          <h2 style="justify-content: space-between; display: flex;">
            <span>Income</span>
          </h2>
          <form class="inline" method="post" action="${pageContext.request.contextPath}/income/add">
            <input type="hidden" name="period" value="${currentPreset}">
            <div class="row">
              <div>
                <label>Amount (रू)</label>
                <input type="number" step="0.01" min="0.01" name="amount" required>
              </div>
              <div>
                <label>Source</label>
                <input type="text" name="source" required maxlength="255">
              </div>
              <div>
                <label>Date</label>
                <input type="date" name="date" value="${today}" required>
              </div>
              <div style="display:flex;align-items:end;">
                <button class="btn" type="submit">+ Add Income</button>
              </div>
            </div>
          </form>
          <c:choose>
            <c:when test="${empty incomes}">
              <div class="empty">No income entries for this period</div>
            </c:when>
            <c:otherwise>
              <c:forEach var="i" items="${incomes}">
                <div class="entry">
                  <div>
                    <div class="amount income">रू <fmt:formatNumber value="${i.amount}" minFractionDigits="2" maxFractionDigits="2"/></div>
                    <div class="desc">${i.source}</div>
                    <div class="meta">${i.date}</div>
                  </div>
                  <form method="post" action="${pageContext.request.contextPath}/income/delete" onsubmit="return confirm('Delete this income entry?');">
                    <input type="hidden" name="id" value="${i.id}">
                    <input type="hidden" name="period" value="${currentPreset}">
                    <button class="btn danger sm" type="submit">Delete</button>
                  </form>
                </div>
              </c:forEach>
            </c:otherwise>
          </c:choose>
        </div>

        <!-- EXPENSES -->
        <div class="card">
          <h2 style="justify-content: space-between; display: flex;">
            <span>Expenses</span>
          </h2>
          <form class="inline" method="post" action="${pageContext.request.contextPath}/expense/add">
            <input type="hidden" name="period" value="${currentPreset}">
            <div class="row">
              <div>
                <label>Amount (रू)</label>
                <input type="number" step="0.01" min="0.01" name="amount" required>
              </div>
              <div>
                <label>Description</label>
                <input type="text" name="description" required maxlength="255">
              </div>
              <div>
                <label>Category</label>
                <select name="categoryId" required>
                  <option value="">Select...</option>
                  <c:forEach var="cat" items="${categories}">
                    <option value="${cat.id}">${cat.name}</option>
                  </c:forEach>
                </select>
              </div>
              <div>
                <label>Date</label>
                <input type="date" name="date" value="${today}" required>
              </div>
              <div style="display:flex;align-items:end; grid-column: 1 / -1;">
                <button class="btn" type="submit">+ Add Expense</button>
              </div>
            </div>
          </form>
          <c:choose>
            <c:when test="${empty expenses}">
              <div class="empty">No expense entries for this period</div>
            </c:when>
            <c:otherwise>
              <c:forEach var="e" items="${expenses}">
                <div class="entry">
                  <div>
                    <div class="amount expense">-रू <fmt:formatNumber value="${e.amount}" minFractionDigits="2" maxFractionDigits="2"/></div>
                    <div class="desc">${e.description}</div>
                    <div class="meta">${e.categoryName} • ${e.date}</div>
                  </div>
                  <form method="post" action="${pageContext.request.contextPath}/expense/delete" onsubmit="return confirm('Delete this expense entry?');">
                    <input type="hidden" name="id" value="${e.id}">
                    <input type="hidden" name="period" value="${currentPreset}">
                    <button class="btn danger sm" type="submit">Delete</button>
                  </form>
                </div>
              </c:forEach>
            </c:otherwise>
          </c:choose>
        </div>
      </div>

      <!-- Category Analytics -->
      <div class="card" style="margin-top: 20px;">
        <h2>Category Breakdown</h2>
        <c:choose>
          <c:when test="${empty expenses}">
            <div class="empty">No expense data available for this period</div>
          </c:when>
          <c:otherwise>
            <div class="chart-grid">
              <div class="chart-box"><canvas id="categoryDonut"></canvas></div>
              <div class="chart-box"><canvas id="categoryBar"></canvas></div>
            </div>
          </c:otherwise>
        </c:choose>
      </div>

      <!-- AI Investment Advisor -->
      <div class="card" style="margin-top: 20px;">
        <h2 style="display:flex;justify-content:space-between;align-items:center;">
          <span><span class="badge">✨</span> AI Investment Recommendations</span>
          <button id="adviceBtn" class="btn" ${netSavingsPositive ? '' : 'disabled'}>Get Advice</button>
        </h2>
        <c:if test="${not netSavingsPositive}">
          <div class="error-banner" style="background: rgba(251,191,36,0.1); border-color: rgba(251,191,36,0.3); color: var(--amber);">
            <strong>Note:</strong> Recommendations are available when you have positive net savings. Focus on income / expenses first.
          </div>
        </c:if>
        <div id="adviceList">
          <div class="empty">Click "Get Advice" to receive personalized investment ideas based on your savings.</div>
        </div>
      </div>

      <footer>© 2026 WealthFocus · Java + JSP + Servlet · Powered by Tomcat &amp; MySQL</footer>
    </main>
  </div>
</div>

<script>
  window.WEALTHFOCUS_DATA = {
    monthlyIncome: ${monthlyIncomeJson},
    monthlyExpense: ${monthlyExpenseJson},
    categoryChart: ${categoryChartJson}
  };
</script>
<script src="${pageContext.request.contextPath}/static/js/app.js"></script>
</body>
</html>

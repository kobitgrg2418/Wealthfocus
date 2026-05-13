<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%
  String today = java.time.LocalDate.now().toString();
  request.setAttribute("today", today);
  String currentPreset = ((com.wealthfocus.util.TimeRangeUtil.Range) request.getAttribute("range")).preset;
  request.setAttribute("currentPreset", currentPreset);

  int incomeCount = 0;
  int expenseCount = 0;
  if (request.getAttribute("incomes") != null) {
    incomeCount = ((java.util.List<?>) request.getAttribute("incomes")).size();
  }
  if (request.getAttribute("expenses") != null) {
    expenseCount = ((java.util.List<?>) request.getAttribute("expenses")).size();
  }
  request.setAttribute("entryCount", incomeCount + expenseCount);
%>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>WealthFocus &#8212; Portfolio</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/main.css">
  <script src="https://cdn.jsdelivr.net/npm/chart.js@4.4.1/dist/chart.umd.min.js"></script>
</head>
<body>
<div class="app">

  <!-- ── Sidebar ── -->
  <aside class="sidebar">
    <div class="logo"></div>
    <nav>
      <a href="${pageContext.request.contextPath}/" class="active" title="Dashboard">
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
      <a href="${pageContext.request.contextPath}/profile" title="Profile">
        <svg width="20" height="20" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" viewBox="0 0 24 24"><path d="M20 21v-2a4 4 0 00-4-4H8a4 4 0 00-4 4v2"/><circle cx="12" cy="7" r="4"/></svg>
      </a>
    </nav>
    <div class="sidebar-bottom">
      <form method="post" action="${pageContext.request.contextPath}/logout">
        <button type="submit" title="Sign out">
          <svg width="20" height="20" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" viewBox="0 0 24 24"><path d="M9 21H5a2 2 0 01-2-2V5a2 2 0 012-2h4"/><polyline points="16 17 21 12 16 7"/><line x1="21" y1="12" x2="9" y2="12"/></svg>
        </button>
      </form>
    </div>
  </aside>

  <!-- ── Main area ── -->
  <div class="main">

    <!-- Top bar -->
    <header class="topbar">
      <div class="breadcrumb">Home / <strong>Portfolio</strong></div>
      <div class="search-wrap">
        <span class="search-icon">&#x2315;</span>
        <input type="text" placeholder="Search transactions..." disabled>
      </div>
      <div class="right">
        <button class="icon-btn" title="Notifications">
          <svg width="18" height="18" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" viewBox="0 0 24 24"><path d="M18 8A6 6 0 006 8c0 7-3 9-3 9h18s-3-2-3-9"/><path d="M13.73 21a2 2 0 01-3.46 0"/></svg>
        </button>
        <a href="#manage-section" class="btn-primary">Manage Finances</a>
      </div>
    </header>

    <!-- Content -->
    <main class="content">

      <c:if test="${not empty param.error}">
        <div class="error-banner">${param.error}</div>
      </c:if>

      <!-- Hero: Balance -->
      <div class="hero-section">
        <div class="balance-display">
          <span class="amount">${netSavings}</span>
          <span class="currency">Rs</span>
        </div>
        <div class="pnl">
          Net Savings:
          <c:choose>
            <c:when test="${netSavingsNonNegative}">
              <span class="positive">${netSavings}</span>
            </c:when>
            <c:otherwise>
              <span class="negative">${netSavings}</span>
            </c:otherwise>
          </c:choose>
        </div>
      </div>

      <!-- Period pills -->
      <div class="period-pills">
        <a class="period-pill ${currentPreset == 'current-month' ? 'active' : ''}"
           href="${pageContext.request.contextPath}/?period=current-month" title="Current Month">M</a>
        <a class="period-pill ${currentPreset == 'last-month' ? 'active' : ''}"
           href="${pageContext.request.contextPath}/?period=last-month" title="Last Month">LM</a>
        <a class="period-pill ${currentPreset == 'last-3-months' ? 'active' : ''}"
           href="${pageContext.request.contextPath}/?period=last-3-months" title="Last 3 Months">Q</a>
        <a class="period-pill ${currentPreset == 'last-6-months' ? 'active' : ''}"
           href="${pageContext.request.contextPath}/?period=last-6-months" title="Last 6 Months">H</a>
        <a class="period-pill ${currentPreset == 'current-year' ? 'active' : ''}"
           href="${pageContext.request.contextPath}/?period=current-year" title="Current Year">Y</a>
      </div>

      <!-- Main grid: Monthly chart + Category breakdown -->
      <div class="main-grid">
        <div class="card">
          <div class="chart-area">
            <canvas id="monthlyChart"></canvas>
          </div>
        </div>
        <div class="card">
          <div class="card-header">
            <div>
              <div class="card-title">Spending Structure</div>
              <div class="card-subtitle">Category breakdown of expenses</div>
            </div>
          </div>
          <c:choose>
            <c:when test="${empty expenses}">
              <div class="empty">No expense data for this period</div>
            </c:when>
            <c:otherwise>
              <div style="height:180px;">
                <canvas id="categoryRadar"></canvas>
              </div>
              <div class="radar-legend" id="radarLegend"></div>
            </c:otherwise>
          </c:choose>
          <div class="target-bar">
            <div class="target-ring"></div>
            <div class="target-text"><strong>${score}</strong> / 850 &mdash; ${scoreLabel}</div>
          </div>
        </div>
      </div>

      <!-- Stat strip: Income / Expenses / Savings -->
      <div class="stat-strip">
        <div class="stat-card">
          <div>
            <div class="stat-label">Total Income</div>
            <div class="stat-value pos">${totalIncome}</div>
          </div>
          <div class="stat-change up">&#9650;</div>
          <canvas class="stat-sparkline" id="sparkIncome"></canvas>
        </div>
        <div class="stat-card">
          <div>
            <div class="stat-label">Total Expenses</div>
            <div class="stat-value neg">${totalExpenses}</div>
          </div>
          <div class="stat-change down">&#9660;</div>
          <canvas class="stat-sparkline" id="sparkExpense"></canvas>
        </div>
        <div class="stat-card">
          <div>
            <div class="stat-label">Net Savings</div>
            <div class="stat-value ${netSavingsNonNegative ? 'pos' : 'neg'}">${netSavings}</div>
          </div>
          <div class="stat-change ${netSavingsNonNegative ? 'up' : 'down'}">${netSavingsNonNegative ? '&#9650;' : '&#9660;'}</div>
          <canvas class="stat-sparkline" id="sparkNet"></canvas>
        </div>
      </div>

      <!-- Bottom grid: Performance, Health Gauge, Recent -->
      <div class="bottom-grid">

        <!-- Performance -->
        <div class="card">
          <div class="card-header">
            <div class="card-title">Performance</div>
          </div>
          <div class="perf-metrics">
            <div class="perf-metric">
              <div class="perf-value">${totalIncome} <span class="arrow">&#8593;</span></div>
              <div class="perf-label">Income</div>
            </div>
            <div class="perf-metric">
              <div class="perf-value">${totalExpenses} <span class="arrow" style="color:var(--red)">&#8595;</span></div>
              <div class="perf-label">Expenses</div>
            </div>
          </div>
          <div class="perf-section" onclick="toggleSection('incomeSection')">
            <div class="section-left">
              <span class="section-icon" style="color:var(--green);">&#9679;</span>
              Income Sources
            </div>
            <span class="chevron">&#9662;</span>
          </div>
          <div class="perf-section" onclick="toggleSection('expenseSection')">
            <div class="section-left">
              <span class="section-icon" style="color:var(--red);">&#9679;</span>
              Expense Categories
            </div>
            <span class="chevron">&#9662;</span>
          </div>
        </div>

        <!-- Health / Score gauge -->
        <div class="card">
          <div class="card-header">
            <div class="card-title">Health</div>
            <div class="card-link">Explore Insights &#8599;</div>
          </div>
          <div class="gauge-container">
            <div class="gauge-canvas-wrap">
              <canvas id="scoreGauge" data-score="${score}"></canvas>
              <div class="gauge-center">
                <div class="gauge-value">${score}</div>
                <div class="gauge-label">${scoreLabel}</div>
              </div>
            </div>
            <div class="gauge-scale">
              <span>350</span>
              <span>850</span>
            </div>
          </div>
        </div>

        <!-- Recent entries -->
        <div class="card">
          <div class="entries-header">
            <div style="display:flex;align-items:center;">
              <div class="card-title">Entries</div>
              <span class="entry-count">${entryCount}</span>
            </div>
            <div class="entry-tabs">
              <button class="entry-tab active" onclick="showEntries('all', this)">All</button>
              <button class="entry-tab" onclick="showEntries('income', this)">Income</button>
              <button class="entry-tab" onclick="showEntries('expense', this)">Expense</button>
            </div>
          </div>

          <div id="entriesList">
            <c:forEach var="i" items="${incomes}" end="2">
              <div class="entry-item" data-type="income">
                <div class="entry-left">
                  <div class="entry-icon income-icon">&#8593;</div>
                  <div>
                    <div class="entry-name">${i.source}</div>
                    <div class="entry-desc-sub">Income</div>
                  </div>
                </div>
                <div class="entry-right">
                  <div class="entry-amount pos">+<fmt:formatNumber value="${i.amount}" type="number" minFractionDigits="2" maxFractionDigits="2"/> Rs</div>
                  <div class="entry-date">${i.date}</div>
                </div>
              </div>
            </c:forEach>
            <c:forEach var="e" items="${expenses}" end="2">
              <div class="entry-item" data-type="expense">
                <div class="entry-left">
                  <div class="entry-icon expense-icon">&#8595;</div>
                  <div>
                    <div class="entry-name">${e.description}</div>
                    <div class="entry-desc-sub">${e.categoryName}</div>
                  </div>
                </div>
                <div class="entry-right">
                  <div class="entry-amount neg">-<fmt:formatNumber value="${e.amount}" type="number" minFractionDigits="2" maxFractionDigits="2"/> Rs</div>
                  <div class="entry-date">${e.date}</div>
                </div>
              </div>
            </c:forEach>
            <c:if test="${empty incomes and empty expenses}">
              <div class="empty">No entries for this period</div>
            </c:if>
          </div>
        </div>
      </div>

      <!-- AI Investment Recommendations -->
      <div class="card advice-section">
        <div class="card-header">
          <div class="card-title">AI Investment Recommendations</div>
          <button id="adviceBtn" class="btn btn-dark" ${netSavingsPositive ? '' : 'disabled'}>Get Advice</button>
        </div>
        <c:if test="${not netSavingsPositive}">
          <div class="error-banner" style="background:rgba(245,158,11,0.08);border-color:rgba(245,158,11,0.2);color:var(--amber);">
            Recommendations are available when you have positive net savings.
          </div>
        </c:if>
        <div id="adviceList">
          <div class="empty">Click &ldquo;Get Advice&rdquo; to receive personalized investment ideas based on your savings.</div>
        </div>
      </div>

      <!-- Manage section: Income & Expense forms -->
      <div id="manage-section">
        <!-- Income toggle -->
        <div class="section-toggle" onclick="toggleManage('incomeManage', this)">
          <div class="toggle-left">
            <span class="toggle-icon income-bg">&#8593;</span>
            Manage Income
          </div>
          <span class="chevron">&#9662;</span>
        </div>
        <div class="section-content" id="incomeManage">
          <div class="form-card">
            <form method="post" action="${pageContext.request.contextPath}/income/add">
              <input type="hidden" name="period" value="${currentPreset}">
              <div class="form-grid">
                <div class="form-group">
                  <label>Amount (Rs)</label>
                  <input type="number" step="0.01" min="0.01" name="amount" required>
                </div>
                <div class="form-group">
                  <label>Source</label>
                  <input type="text" name="source" required maxlength="255">
                </div>
                <div class="form-group">
                  <label>Date</label>
                  <input type="date" name="date" value="${today}" required>
                </div>
                <div class="form-group" style="display:flex;align-items:end;">
                  <button class="btn btn-dark" type="submit">+ Add Income</button>
                </div>
              </div>
            </form>
            <div class="entry-list">
              <c:choose>
                <c:when test="${empty incomes}">
                  <div class="empty">No income entries for this period</div>
                </c:when>
                <c:otherwise>
                  <c:forEach var="i" items="${incomes}">
                    <div class="entry-row">
                      <div class="entry-info">
                        <div class="amount income">+<fmt:formatNumber value="${i.amount}" type="number" minFractionDigits="2" maxFractionDigits="2"/> Rs</div>
                        <div class="desc">${i.source}</div>
                        <div class="meta">${i.date}</div>
                      </div>
                      <form method="post" action="${pageContext.request.contextPath}/income/delete" onsubmit="return confirm('Delete this income entry?');">
                        <input type="hidden" name="id" value="${i.id}">
                        <input type="hidden" name="period" value="${currentPreset}">
                        <button class="btn-danger" type="submit">Delete</button>
                      </form>
                    </div>
                  </c:forEach>
                </c:otherwise>
              </c:choose>
            </div>
          </div>
        </div>

        <!-- Expense toggle -->
        <div class="section-toggle" onclick="toggleManage('expenseManage', this)">
          <div class="toggle-left">
            <span class="toggle-icon expense-bg">&#8595;</span>
            Manage Expenses
          </div>
          <span class="chevron">&#9662;</span>
        </div>
        <div class="section-content" id="expenseManage">
          <div class="form-card">
            <form method="post" action="${pageContext.request.contextPath}/expense/add">
              <input type="hidden" name="period" value="${currentPreset}">
              <div class="form-grid">
                <div class="form-group">
                  <label>Amount (Rs)</label>
                  <input type="number" step="0.01" min="0.01" name="amount" required>
                </div>
                <div class="form-group">
                  <label>Description</label>
                  <input type="text" name="description" required maxlength="255">
                </div>
                <div class="form-group">
                  <label>Category</label>
                  <select name="categoryId" required>
                    <option value="">Select...</option>
                    <c:forEach var="cat" items="${categories}">
                      <option value="${cat.id}">${cat.name}</option>
                    </c:forEach>
                  </select>
                </div>
                <div class="form-group">
                  <label>Date</label>
                  <input type="date" name="date" value="${today}" required>
                </div>
                <div class="form-group" style="display:flex;align-items:end; grid-column: 1 / -1;">
                  <button class="btn btn-dark" type="submit">+ Add Expense</button>
                </div>
              </div>
            </form>
            <div class="entry-list">
              <c:choose>
                <c:when test="${empty expenses}">
                  <div class="empty">No expense entries for this period</div>
                </c:when>
                <c:otherwise>
                  <c:forEach var="e" items="${expenses}">
                    <div class="entry-row">
                      <div class="entry-info">
                        <div class="amount expense">-<fmt:formatNumber value="${e.amount}" type="number" minFractionDigits="2" maxFractionDigits="2"/> Rs</div>
                        <div class="desc">${e.description}</div>
                        <div class="meta">${e.categoryName} &bull; ${e.date}</div>
                      </div>
                      <form method="post" action="${pageContext.request.contextPath}/expense/delete" onsubmit="return confirm('Delete this expense entry?');">
                        <input type="hidden" name="id" value="${e.id}">
                        <input type="hidden" name="period" value="${currentPreset}">
                        <button class="btn-danger" type="submit">Delete</button>
                      </form>
                    </div>
                  </c:forEach>
                </c:otherwise>
              </c:choose>
            </div>
          </div>
        </div>
      </div>

      <footer>&copy; 2026 WealthFocus &middot; Java + JSP + Servlet</footer>
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

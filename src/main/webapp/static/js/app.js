(function () {
  const data = window.WEALTHFOCUS_DATA;
  if (!data) return;

  const months = ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec'];
  const fmtNPR = (v) => 'Rs ' + Number(v).toLocaleString('en-IN', { minimumFractionDigits: 2, maximumFractionDigits: 2 });

  const chartFont = { family: "'Inter', sans-serif" };
  const gridColor = '#e5e2dc';
  const tickColor = '#9ca3af';

  // ── Monthly bar chart (income green, expense red) ──
  const monthlyEl = document.getElementById('monthlyChart');
  if (monthlyEl) {
    new Chart(monthlyEl, {
      type: 'bar',
      data: {
        labels: months,
        datasets: [
          {
            label: 'Income',
            data: data.monthlyIncome,
            backgroundColor: 'rgba(34, 197, 94, 0.75)',
            hoverBackgroundColor: 'rgba(34, 197, 94, 0.9)',
            borderRadius: 4,
            barPercentage: 0.5,
            categoryPercentage: 0.7,
          },
          {
            label: 'Expense',
            data: data.monthlyExpense,
            backgroundColor: 'rgba(239, 68, 68, 0.65)',
            hoverBackgroundColor: 'rgba(239, 68, 68, 0.85)',
            borderRadius: 4,
            barPercentage: 0.5,
            categoryPercentage: 0.7,
          },
        ],
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        plugins: {
          legend: {
            position: 'top',
            align: 'end',
            labels: {
              color: tickColor,
              font: { ...chartFont, size: 11 },
              boxWidth: 12,
              boxHeight: 12,
              borderRadius: 3,
              useBorderRadius: true,
              padding: 16,
            },
          },
          tooltip: {
            backgroundColor: '#1c1c28',
            titleColor: '#fff',
            bodyColor: '#e5e7eb',
            cornerRadius: 8,
            padding: 10,
            callbacks: { label: (c) => c.dataset.label + ': ' + fmtNPR(c.parsed.y) },
          },
        },
        scales: {
          x: {
            grid: { display: false },
            ticks: { color: tickColor, font: { ...chartFont, size: 11 } },
            border: { display: false },
          },
          y: {
            grid: { color: gridColor, drawBorder: false },
            ticks: {
              color: tickColor,
              font: { ...chartFont, size: 10 },
              callback: (v) => (v >= 1000 ? (v / 1000).toFixed(0) + 'k' : v),
            },
            border: { display: false },
          },
        },
      },
    });
  }

  // ── Category radar/doughnut chart ──
  const radarEl = document.getElementById('categoryRadar');
  const legendBox = document.getElementById('radarLegend');
  if (radarEl && Object.keys(data.categoryChart).length > 0) {
    const labels = Object.keys(data.categoryChart);
    const values = Object.values(data.categoryChart);
    const colors = ['#22c55e', '#facc15', '#f59e0b', '#ef4444', '#8b5cf6', '#06b6d4', '#ec4899'];

    new Chart(radarEl, {
      type: 'radar',
      data: {
        labels: labels,
        datasets: [{
          data: values,
          backgroundColor: 'rgba(34, 197, 94, 0.15)',
          borderColor: '#22c55e',
          borderWidth: 2,
          pointBackgroundColor: '#22c55e',
          pointRadius: 4,
          pointHoverRadius: 6,
        }],
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        plugins: { legend: { display: false } },
        scales: {
          r: {
            grid: { color: '#e5e2dc' },
            angleLines: { color: '#e5e2dc' },
            ticks: { display: false },
            pointLabels: {
              color: '#6b7280',
              font: { ...chartFont, size: 10, weight: '600' },
            },
          },
        },
      },
    });

    if (legendBox) {
      const maxVal = Math.max(...values);
      const lineColors = ['#22c55e', '#f59e0b', '#ef4444'];
      const legendLabels = ['Highest', 'Average', 'Lowest'];
      const legendVals = [Math.max(...values), values.reduce((a, b) => a + b, 0) / values.length, Math.min(...values)];

      legendBox.innerHTML = legendVals.map((v, i) =>
        '<div class="radar-legend-item">' +
          '<div class="legend-left">' +
            '<span class="legend-line" style="background:' + lineColors[i] + ';"></span>' +
            legendLabels[i] +
          '</div>' +
          '<div class="legend-value">' + fmtNPR(v) + '</div>' +
        '</div>'
      ).join('');
    }
  }

  // ── Health score gauge (half-circle) ──
  const gaugeEl = document.getElementById('scoreGauge');
  if (gaugeEl) {
    const score = Number(gaugeEl.dataset.score) || 350;
    const min = 350, max = 850;
    const pct = Math.max(0, Math.min(1, (score - min) / (max - min)));
    const ctx = gaugeEl.getContext('2d');
    const dpr = window.devicePixelRatio || 1;
    const w = 200, h = 120;
    gaugeEl.width = w * dpr;
    gaugeEl.height = h * dpr;
    gaugeEl.style.width = w + 'px';
    gaugeEl.style.height = h + 'px';
    ctx.scale(dpr, dpr);

    const cx = w / 2, cy = h - 4;
    const r = 80;
    const startAngle = Math.PI;
    const endAngle = 2 * Math.PI;

    // Track
    ctx.beginPath();
    ctx.arc(cx, cy, r, startAngle, endAngle);
    ctx.lineWidth = 14;
    ctx.lineCap = 'round';
    ctx.strokeStyle = '#e5e2dc';
    ctx.stroke();

    // Gradient fill
    const grad = ctx.createLinearGradient(cx - r, 0, cx + r, 0);
    grad.addColorStop(0, '#ef4444');
    grad.addColorStop(0.3, '#f59e0b');
    grad.addColorStop(0.55, '#facc15');
    grad.addColorStop(1, '#22c55e');

    ctx.beginPath();
    ctx.arc(cx, cy, r, startAngle, startAngle + (endAngle - startAngle) * pct);
    ctx.lineWidth = 14;
    ctx.lineCap = 'round';
    ctx.strokeStyle = grad;
    ctx.stroke();

    // Needle dot
    const needleAngle = startAngle + (endAngle - startAngle) * pct;
    const nx = cx + r * Math.cos(needleAngle);
    const ny = cy + r * Math.sin(needleAngle);
    ctx.beginPath();
    ctx.arc(nx, ny, 6, 0, Math.PI * 2);
    ctx.fillStyle = '#fff';
    ctx.fill();
    ctx.lineWidth = 3;
    ctx.strokeStyle = pct > 0.5 ? '#22c55e' : pct > 0.3 ? '#f59e0b' : '#ef4444';
    ctx.stroke();
  }

  // ── Sparkline charts for stat cards ──
  function drawSparkline(id, dataArr, color) {
    const el = document.getElementById(id);
    if (!el || !dataArr || dataArr.length === 0) return;
    new Chart(el, {
      type: 'line',
      data: {
        labels: months,
        datasets: [{
          data: dataArr,
          borderColor: color,
          borderWidth: 2,
          fill: true,
          backgroundColor: color.replace(')', ', 0.08)').replace('rgb', 'rgba'),
          pointRadius: 0,
          tension: 0.4,
        }],
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        plugins: { legend: { display: false }, tooltip: { enabled: false } },
        scales: {
          x: { display: false },
          y: { display: false },
        },
      },
    });
  }

  drawSparkline('sparkIncome', data.monthlyIncome, 'rgb(34, 197, 94)');
  drawSparkline('sparkExpense', data.monthlyExpense, 'rgb(239, 68, 68)');

  // Net sparkline (income - expense per month)
  const netMonthly = data.monthlyIncome.map((inc, i) => inc - (data.monthlyExpense[i] || 0));
  drawSparkline('sparkNet', netMonthly, netMonthly.reduce((a, b) => a + b, 0) >= 0 ? 'rgb(34, 197, 94)' : 'rgb(239, 68, 68)');

  // ── Get advice button ──
  const adviceBtn = document.getElementById('adviceBtn');
  const adviceContainer = document.getElementById('adviceList');
  if (adviceBtn && adviceContainer) {
    adviceBtn.addEventListener('click', async () => {
      adviceBtn.disabled = true;
      adviceBtn.textContent = 'Analyzing...';
      try {
        const period = new URLSearchParams(window.location.search).get('period') || '';
        const res = await fetch('advice' + (period ? '?period=' + period : ''));
        const recs = await res.json();
        if (!recs || recs.length === 0) {
          adviceContainer.innerHTML = '<div class="empty">Add positive savings to see recommendations.</div>';
          return;
        }
        adviceContainer.innerHTML = recs.map((r) =>
          '<div class="recommendation">' +
            '<div class="head">' +
              '<div class="title">' + escapeHtml(r.title) + '</div>' +
              '<span class="risk-badge risk-' + r.riskLevel + '">' + r.riskLevel.toUpperCase() + ' RISK</span>' +
            '</div>' +
            '<div style="color:var(--text-secondary);font-size:13px;margin-bottom:8px;">' + escapeHtml(r.description) + '</div>' +
            (r.suggestedAmount ? '<div class="suggested-amount">Suggested: ' + fmtNPR(r.suggestedAmount) + '</div>' : '') +
            '<div class="reasoning"><strong>Reasoning:</strong> ' + escapeHtml(r.reasoning) + '</div>' +
          '</div>'
        ).join('');
      } catch (e) {
        adviceContainer.innerHTML = '<div class="error-banner">Failed to load advice: ' + escapeHtml(String(e.message || e)) + '</div>';
      } finally {
        adviceBtn.disabled = false;
        adviceBtn.textContent = 'Get Advice';
      }
    });
  }

  function escapeHtml(s) {
    return String(s).replace(/[&<>"']/g, (c) => ({
      '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#39;'
    }[c]));
  }

  // ── Entry tabs filter ──
  window.showEntries = function (type, btn) {
    document.querySelectorAll('.entry-tab').forEach((t) => t.classList.remove('active'));
    btn.classList.add('active');
    document.querySelectorAll('#entriesList .entry-item').forEach((el) => {
      if (type === 'all') { el.style.display = ''; return; }
      el.style.display = el.dataset.type === type ? '' : 'none';
    });
  };

  // ── Toggle manage sections ──
  window.toggleManage = function (id, toggleEl) {
    const content = document.getElementById(id);
    if (!content) return;
    const isOpen = content.classList.contains('open');
    content.classList.toggle('open');
    toggleEl.classList.toggle('open');
  };

  // ── Scroll toggle for perf sections ──
  window.toggleSection = function (id) {
    const section = document.getElementById(id);
    if (!section) {
      const manage = document.getElementById(id === 'incomeSection' ? 'incomeManage' : 'expenseManage');
      const toggle = manage ? manage.previousElementSibling : null;
      if (manage && toggle) {
        manage.classList.add('open');
        toggle.classList.add('open');
        manage.scrollIntoView({ behavior: 'smooth', block: 'center' });
      }
    }
  };
})();

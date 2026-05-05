(function () {
  const data = window.WEALTHFOCUS_DATA;
  if (!data) return;

  const months = ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec'];
  const fmtNPR = (v) => 'रू ' + Number(v).toLocaleString('en-IN', { minimumFractionDigits: 2, maximumFractionDigits: 2 });

  // Monthly bar chart - Income vs Expense
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
            backgroundColor: 'rgba(94, 234, 212, 0.85)',
            borderRadius: 4,
            barPercentage: 0.6,
            categoryPercentage: 0.7,
          },
          {
            label: 'Expense',
            data: data.monthlyExpense,
            backgroundColor: 'rgba(251, 113, 133, 0.85)',
            borderRadius: 4,
            barPercentage: 0.6,
            categoryPercentage: 0.7,
          },
        ],
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        plugins: {
          legend: { display: false },
          tooltip: { callbacks: { label: (c) => c.dataset.label + ': ' + fmtNPR(c.parsed.y) } },
        },
        scales: {
          x: { grid: { display: false }, ticks: { color: '#9ca3af', font: { size: 10 } } },
          y: { grid: { color: 'rgba(42,43,47,0.5)' }, ticks: { color: '#9ca3af', font: { size: 10 } } },
        },
      },
    });
  }

  // Score gauge (hexagonal)
  const scoreEl = document.getElementById('scoreGauge');
  if (scoreEl) {
    const score = Number(scoreEl.dataset.score) || 350;
    const angle = ((score - 350) / 500) * 270 - 135;
    const ctx = scoreEl.getContext('2d');
    const w = scoreEl.width = 200;
    const h = scoreEl.height = 200;
    ctx.clearRect(0, 0, w, h);
    const cx = w / 2, cy = h / 2;
    function hexPoints(r) {
      const pts = [];
      for (let i = 0; i < 6; i++) {
        const a = (Math.PI / 3) * i - Math.PI / 2;
        pts.push([cx + r * Math.cos(a), cy + r * Math.sin(a)]);
      }
      return pts;
    }
    function drawHex(r, color, lw) {
      const pts = hexPoints(r);
      ctx.strokeStyle = color; ctx.lineWidth = lw;
      ctx.beginPath();
      pts.forEach((p, i) => i === 0 ? ctx.moveTo(p[0], p[1]) : ctx.lineTo(p[0], p[1]));
      ctx.closePath(); ctx.stroke();
    }
    for (let i = 0; i < 6; i++) {
      drawHex(85 - i * 8, `rgba(94, 234, 212, ${0.08 + i * 0.04})`, 1);
    }
    const grad = ctx.createLinearGradient(0, 0, w, h);
    grad.addColorStop(0, '#5eead4');
    grad.addColorStop(1, '#0d9488');
    drawHex(85, grad, 2);
    // needle
    ctx.strokeStyle = '#5eead4';
    ctx.lineWidth = 3;
    ctx.lineCap = 'round';
    ctx.beginPath();
    ctx.moveTo(cx, cy);
    ctx.lineTo(cx + Math.cos(angle * Math.PI / 180) * 75, cy + Math.sin(angle * Math.PI / 180) * 75);
    ctx.stroke();
    ctx.fillStyle = '#5eead4';
    ctx.beginPath();
    ctx.arc(cx, cy, 6, 0, Math.PI * 2);
    ctx.fill();
  }

  // Category donut chart
  const donutEl = document.getElementById('categoryDonut');
  if (donutEl && Object.keys(data.categoryChart).length > 0) {
    const palette = ['#5eead4', '#22d3ee', '#818cf8', '#f472b6', '#fb923c', '#facc15', '#a3e635'];
    new Chart(donutEl, {
      type: 'doughnut',
      data: {
        labels: Object.keys(data.categoryChart),
        datasets: [{
          data: Object.values(data.categoryChart),
          backgroundColor: palette,
          borderColor: '#141416',
          borderWidth: 2,
        }],
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        cutout: '55%',
        plugins: {
          legend: { position: 'right', labels: { color: '#9ca3af', font: { size: 11 } } },
          tooltip: { callbacks: { label: (c) => c.label + ': ' + fmtNPR(c.parsed) } },
        },
      },
    });
  }

  // Category bar chart
  const catBarEl = document.getElementById('categoryBar');
  if (catBarEl && Object.keys(data.categoryChart).length > 0) {
    new Chart(catBarEl, {
      type: 'bar',
      data: {
        labels: Object.keys(data.categoryChart),
        datasets: [{
          label: 'Amount',
          data: Object.values(data.categoryChart),
          backgroundColor: '#5eead4',
          borderRadius: 6,
        }],
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        plugins: {
          legend: { display: false },
          tooltip: { callbacks: { label: (c) => fmtNPR(c.parsed.y) } },
        },
        scales: {
          x: { grid: { display: false }, ticks: { color: '#9ca3af', font: { size: 10 } } },
          y: { grid: { color: 'rgba(42,43,47,0.5)' }, ticks: { color: '#9ca3af', font: { size: 10 } } },
        },
      },
    });
  }

  // Get advice button
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
        adviceContainer.innerHTML = recs.map((r) => `
          <div class="recommendation">
            <div class="head">
              <div class="title">${escapeHtml(r.title)}</div>
              <span class="risk-badge risk-${r.riskLevel}">${r.riskLevel.toUpperCase()} RISK</span>
            </div>
            <div style="color: var(--text); font-size: 13px; margin-bottom: 8px;">${escapeHtml(r.description)}</div>
            ${r.suggestedAmount ? `<div class="suggested-amount">Suggested: ${fmtNPR(r.suggestedAmount)}</div>` : ''}
            <div class="reasoning"><strong>Reasoning:</strong> ${escapeHtml(r.reasoning)}</div>
          </div>
        `).join('');
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
})();

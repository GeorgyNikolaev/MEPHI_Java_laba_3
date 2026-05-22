const API = "/api/missions";

const $ = (id) => document.getElementById(id);

let selectedMissionId = null;
let selectedFile = null;

function toast(message, type = "ok") {
  const area = $("toastArea");
  const el = document.createElement("div");
  el.className = `toast ${type === "ok" ? "ok" : "err"}`;
  el.textContent = message;
  area.appendChild(el);
  setTimeout(() => {
    el.style.opacity = "0";
    el.style.transform = "translateY(6px)";
    el.style.transition = "opacity 0.3s ease, transform 0.3s ease";
    setTimeout(() => el.remove(), 320);
  }, 4200);
}

async function fetchJson(url, options = {}) {
  const res = await fetch(url, options);
  const text = await res.text();
  let body = null;
  if (text) {
    try {
      body = JSON.parse(text);
    } catch {
      body = text;
    }
  }
  if (!res.ok) {
    const msg = body?.message || res.statusText || "Ошибка запроса";
    throw new Error(msg);
  }
  return body;
}

function setEmptyStateVisible(visible) {
  $("emptyState").classList.toggle("visible", visible);
}

function renderMissions(rows) {
  const tbody = $("missionsBody");
  tbody.innerHTML = "";
  setEmptyStateVisible(!rows.length);
  for (const m of rows) {
    const tr = document.createElement("tr");
    tr.dataset.id = String(m.id);
    if (selectedMissionId === m.id) tr.classList.add("selected");
    tr.innerHTML = `
      <td><strong>${escapeHtml(m.missionCode)}</strong></td>
      <td>${escapeHtml(m.date)}</td>
      <td>${escapeHtml(m.location)}</td>
      <td><span class="pill">${escapeHtml(m.outcome)}</span></td>
      <td>${escapeHtml(m.sourceFilename || "—")}</td>
      <td class="actions">
        <button type="button" class="btn ghost sm" data-action="detail">Подробнее</button>
        <button type="button" class="btn danger sm" data-action="delete">Удалить</button>
      </td>`;
    tr.addEventListener("click", (e) => {
      const btn = e.target.closest("button");
      if (btn?.dataset.action === "delete") {
        e.stopPropagation();
        deleteMission(m.id);
        return;
      }
      if (btn?.dataset.action === "detail") {
        e.stopPropagation();
        openDetail(m.id);
        return;
      }
      selectMission(m.id, tr);
    });
    tbody.appendChild(tr);
  }
  $("reportBtn").disabled = selectedMissionId == null;
  $("loadReportsBtn").disabled = selectedMissionId == null;
}

function selectMission(id, tr) {
  selectedMissionId = id;
  document.querySelectorAll(".missions-table tbody tr").forEach((r) => r.classList.remove("selected"));
  if (tr) tr.classList.add("selected");
  $("reportBtn").disabled = false;
  $("loadReportsBtn").disabled = false;
  $("reportOutput").hidden = true;
}

function escapeHtml(s) {
  return String(s)
    .replace(/&/g, "&amp;")
    .replace(/</g, "&lt;")
    .replace(/>/g, "&gt;")
    .replace(/"/g, "&quot;");
}

async function loadMissions() {
  try {
    const data = await fetchJson(API);
    renderMissions(data);
  } catch (e) {
    toast(e.message, "err");
  }
}

async function deleteMission(id) {
  if (!confirm("Удалить миссию и все отчёты?")) return;
  try {
    await fetchJson(`${API}/${id}`, { method: "DELETE" });
    toast("Миссия удалена");
    if (selectedMissionId === id) {
      selectedMissionId = null;
      $("detailPanel").hidden = true;
    }
    await loadMissions();
  } catch (e) {
    toast(e.message, "err");
  }
}

async function openDetail(id) {
  try {
    const d = await fetchJson(`${API}/${id}`);
    selectMission(id, document.querySelector(`tr[data-id="${id}"]`));
    const html = `
      <div class="detail-grid">
        <div>
          <h4>Основное</h4>
          <p><strong>Код:</strong> ${escapeHtml(d.missionCode)}</p>
          <p><strong>Дата:</strong> ${escapeHtml(d.date)}</p>
          <p><strong>Локация:</strong> ${escapeHtml(d.location)}</p>
          <p><strong>Исход:</strong> ${escapeHtml(d.outcome)}</p>
          <p><strong>Ущерб:</strong> ${d.damageCost != null ? escapeHtml(d.damageCost) : "—"}</p>
        </div>
        <div>
          <h4>Проклятие</h4>
          <p>${d.curse ? escapeHtml(d.curse.name) + " (" + escapeHtml(d.curse.threatLevel) + ")" : "—"}</p>
          <h4>Маги</h4>
          <p>${d.sorcerers?.length ? d.sorcerers.map((s) => escapeHtml(s.name) + " — " + escapeHtml(s.rank)).join("<br/>") : "—"}</p>
        </div>
      </div>
      <div style="margin-top:1rem">
        <h4>Техники</h4>
        <p>${d.techniques?.length ? d.techniques.map((t) =>
      `${escapeHtml(t.techniqueName)} (${escapeHtml(t.techniqueType)}) — ${t.ownerName ? escapeHtml(t.ownerName) : "—"} / урон: ${t.damage ?? "—"}`
    ).join("<br/>") : "—"}</p>
      </div>`;
    $("detailContent").innerHTML = html;
    $("detailPanel").hidden = false;
  } catch (e) {
    toast(e.message, "err");
  }
}

async function uploadFile() {
  if (!selectedFile) return;
  const fd = new FormData();
  fd.append("file", selectedFile);
  try {
    const res = await fetch(API, { method: "POST", body: fd });
    const text = await res.text();
    let body = text ? JSON.parse(text) : null;
    if (!res.ok) throw new Error(body?.message || res.statusText);
    toast("Миссия сохранена");
    selectedFile = null;
    $("fileName").textContent = "";
    $("uploadBtn").disabled = true;
    $("fileInput").value = "";
    await loadMissions();
  } catch (e) {
    toast(e.message, "err");
  }
}

async function createReport() {
  if (!selectedMissionId) return;
  const reportType = $("reportType").value;
  try {
    const body = await fetchJson(`${API}/${selectedMissionId}/reports`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ reportType }),
    });
    $("reportText").textContent = body.content;
    $("reportOutput").hidden = false;
    toast("Отчёт создан и сохранён");
    await loadReportList();
  } catch (e) {
    toast(e.message, "err");
  }
}

async function loadReportList() {
  if (!selectedMissionId) return;
  try {
    const list = await fetchJson(`${API}/${selectedMissionId}/reports`);
    const ul = $("reportList");
    ul.innerHTML = "";
    for (const r of list) {
      const li = document.createElement("li");
      li.textContent = `${r.reportType} · ${r.createdAt}`;
      li.title = "Нажмите, чтобы показать текст";
      li.addEventListener("click", () => {
        $("reportText").textContent = r.content;
        $("reportOutput").hidden = false;
        $("reportOutput").scrollIntoView({ behavior: "smooth", block: "nearest" });
      });
      ul.appendChild(li);
    }
  } catch (e) {
    toast(e.message, "err");
  }
}

function wireUpload() {
  const input = $("fileInput");
  const drop = $("dropZone");
  input.addEventListener("change", () => {
    selectedFile = input.files?.[0] || null;
    $("fileName").textContent = selectedFile ? selectedFile.name : "";
    $("uploadBtn").disabled = !selectedFile;
  });
  ["dragenter", "dragover"].forEach((ev) => {
    drop.addEventListener(ev, (e) => {
      e.preventDefault();
      drop.classList.add("dragover");
    });
  });
  ["dragleave", "drop"].forEach((ev) => {
    drop.addEventListener(ev, (e) => {
      e.preventDefault();
      drop.classList.remove("dragover");
    });
  });
  drop.addEventListener("drop", (e) => {
    const f = e.dataTransfer.files?.[0];
    if (f) {
      selectedFile = f;
      $("fileName").textContent = f.name;
      $("uploadBtn").disabled = false;
    }
  });
  $("uploadBtn").addEventListener("click", uploadFile);
}

$("refreshBtn").addEventListener("click", loadMissions);
$("reportBtn").addEventListener("click", createReport);
$("loadReportsBtn").addEventListener("click", loadReportList);
$("closeDetailBtn").addEventListener("click", () => {
  $("detailPanel").hidden = true;
});

wireUpload();
loadMissions();

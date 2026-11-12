<template>
  <div class="admin">
    <!-- ===== 顶栏 ===== -->
    <header class="topbar">
      <div class="tb-brand">
        <span class="seal">斗</span>
        <div class="tb-txt">
          <b>斗气大陆 · 后台管理</b>
          <i>门 户 数 据 中 枢</i>
        </div>
      </div>
      <div class="tb-right">
        <span class="online"><i></i>数据中枢已连接</span>
        <div class="who"><span class="av">{{ initial }}</span>{{ displayName }}</div>
        <button class="tb-btn" type="button" @click="$emit('back-home')">返回门户</button>
        <button class="tb-btn danger" type="button" @click="doLogout">退出登录</button>
      </div>
    </header>

    <div class="body">
      <!-- ===== 侧栏 ===== -->
      <aside class="side-nav">
        <div class="side-cap">数 据 管 理</div>
        <button class="side-item" :class="{ on: current === 'overview' }" type="button" @click="goOverview">
          <span class="si-ic">览</span><span class="si-txt">总览</span>
        </button>
        <button
          v-for="r in RESOURCES"
          :key="r.key"
          class="side-item"
          :class="{ on: current === r.key }"
          type="button"
          @click="select(r.key)"
        >
          <span class="si-ic">{{ r.icon }}</span>
          <span class="si-txt">{{ r.label }}</span>
          <em>{{ countOf(r.key) }}</em>
        </button>
        <div class="side-foot">
          共 {{ RESOURCES.length }} 类数据<br />
          保存后刷新门户即可生效
        </div>
      </aside>

      <!-- ===== 主区 ===== -->
      <main class="main">
        <!-- 总览 -->
        <section v-if="current === 'overview'" class="overview">
          <div class="page-head">
            <div>
              <h1>数据总览</h1>
              <p>管理门户首页与各榜单页面展示的全部内容，点击进入对应模块进行增删改查。</p>
            </div>
          </div>
          <div class="ov-grid">
            <button v-for="r in RESOURCES" :key="r.key" class="ov-card" type="button" @click="select(r.key)">
              <span class="ov-ic">{{ r.icon }}</span>
              <b>{{ r.label }}</b>
              <span class="ov-count">{{ countOf(r.key) }} <i>条</i></span>
              <p>{{ r.desc }}</p>
              <span class="ov-go">进入管理 →</span>
            </button>
          </div>
          <div class="notice">
            <span class="spark"></span>
            说明：本后台只管理门户展示数据，前台斗者账号与登录注册流程不受影响；改动写入数据库后，刷新前台首页即可看到最新内容。
          </div>
        </section>

        <!-- 数据表格 -->
        <section v-else class="dataset">
          <div class="page-head">
            <div>
              <h1>{{ res.label }}</h1>
              <p>{{ res.desc }}</p>
            </div>
            <span class="count-badge">共 {{ rows.length }} 条</span>
          </div>

          <div class="toolbar">
            <div class="search">
              <svg viewBox="0 0 24 24"><circle cx="11" cy="11" r="7"/><path d="m20 20-3.2-3.2"/></svg>
              <input v-model="kw" type="text" placeholder="按名称搜索……" @keyup.enter="loadRows" />
            </div>
            <button class="btn ghost" type="button" @click="loadRows">搜 寻</button>
            <button class="btn ghost" type="button" @click="kw = ''; loadRows()">重 置</button>
            <button class="btn primary" type="button" @click="openCreate">＋ 新增{{ res.label }}</button>
          </div>

          <div class="table-wrap">
            <table>
              <thead>
                <tr>
                  <th class="idx">#</th>
                  <th v-for="c in res.columns" :key="c.key" :style="{ minWidth: (c.width || 120) + 'px' }">{{ c.label }}</th>
                  <th class="ops">操作</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="(row, i) in rows" :key="row.id">
                  <td class="idx">{{ i + 1 }}</td>
                  <td v-for="c in res.columns" :key="c.key" :title="String(row[c.key] === null || row[c.key] === undefined ? '' : row[c.key])">
                    <span class="cell">{{ display(row[c.key]) }}</span>
                  </td>
                  <td class="ops">
                    <button class="mini" type="button" @click="openEdit(row)">编辑</button>
                    <button class="mini danger" type="button" @click="deleting = row">删除</button>
                  </td>
                </tr>
                <tr v-if="!rows.length">
                  <td class="empty" :colspan="res.columns.length + 2">{{ loading ? '正在载入卷轴……' : '暂无数据，点「新增」开始录入' }}</td>
                </tr>
              </tbody>
            </table>
          </div>
        </section>
      </main>
    </div>

    <!-- ===== 新增 / 编辑弹窗 ===== -->
    <div v-if="showForm" class="mask" @click.self="showForm = false">
      <div class="dialog">
        <h3><span class="spark"></span>{{ editing ? '编辑' : '新增' }} · {{ res.label }}</h3>
        <form @submit.prevent="submitForm">
          <label v-for="c in editable" :key="c.key" class="fld">
            <span>{{ c.label }} <i v-if="c.required">*</i></span>
            <textarea v-if="c.type === 'textarea'" v-model="form[c.key]" rows="3" :placeholder="'请输入' + c.label"></textarea>
            <input v-else v-model="form[c.key]" :type="c.type === 'number' ? 'number' : 'text'" :placeholder="'请输入' + c.label" />
          </label>
          <div v-if="formError" class="dlg-err">{{ formError }}</div>
          <div class="dlg-ops">
            <button class="btn ghost" type="button" @click="showForm = false">取消</button>
            <button class="btn primary" type="submit" :disabled="saving">{{ saving ? '保存中……' : '保 存' }}</button>
          </div>
        </form>
      </div>
    </div>

    <!-- ===== 删除确认 ===== -->
    <div v-if="deleting" class="mask" @click.self="deleting = null">
      <div class="dialog small">
        <h3><span class="spark"></span>删除确认</h3>
        <p class="confirm-txt">确定要删除「{{ deleting[res.primary] }}」吗？<br />删除后门户首页将不再展示这条数据。</p>
        <div class="dlg-ops">
          <button class="btn ghost" type="button" @click="deleting = null">取消</button>
          <button class="btn danger" type="button" @click="doDelete">确认删除</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { RESOURCES, findResource, editableColumns, emptyForm } from '../admin/resources'
import { adminRequest } from '../admin/api'
import { showToast } from '../composables/useToast'

const props = defineProps({
  admin: { type: Object, default: null }
})
const emit = defineEmits(['unauthorized', 'logout', 'back-home'])

const current = ref('overview')
const rows = ref([])
const counts = ref({})
const kw = ref('')
const loading = ref(false)
const saving = ref(false)
const showForm = ref(false)
const editing = ref(false)
const form = ref({})
const formError = ref('')
const deleting = ref(null)

const res = computed(() => findResource(current.value) || RESOURCES[0])
const editable = computed(() => editableColumns(res.value))
const displayName = computed(() => {
  const a = props.admin || {}
  return a.nickname || a.username || '管理员'
})
const initial = computed(() => displayName.value.charAt(0))

function countOf (key) {
  const v = counts.value[key]
  return v === undefined ? '—' : v
}

function display (v) {
  if (v === null || v === undefined || v === '') return '——'
  const text = String(v).replace('T', ' ')
  return text.length > 60 ? text.slice(0, 60) + '…' : text
}

/** 统一处理登录失效：后端返回 code 401 时退回登录页 */
function unauthorized (r) {
  if (r && r.code === 401) {
    emit('unauthorized')
    return true
  }
  return false
}

/** 页面挂载 / 刷新时先确认会话有效 */
async function checkSession () {
  const r = await adminRequest('/check')
  if (unauthorized(r)) return false
  if (r.code !== 0) {
    showToast(r.msg || '会话校验失败', '✕')
    return false // 后端未启动等异常时不再批量发请求
  }
  return true
}

/** 总览：并发拉取 7 个模块的记录数 */
async function loadCounts () {
  const results = await Promise.all(RESOURCES.map(r => adminRequest('/data/' + r.key)))
  if (results.some(r => r && r.code === 401)) {
    emit('unauthorized')
    return
  }
  const next = {}
  results.forEach((r, i) => {
    if (r && r.code === 0) next[RESOURCES[i].key] = (r.data || []).length
  })
  counts.value = next
}

async function loadRows () {
  loading.value = true
  const q = kw.value.trim() ? '?kw=' + encodeURIComponent(kw.value.trim()) : ''
  const r = await adminRequest('/data/' + res.value.key + q)
  loading.value = false
  if (unauthorized(r)) return
  if (r.code !== 0) {
    showToast(r.msg || '数据加载失败', '✕')
    return
  }
  rows.value = r.data || []
  counts.value = Object.assign({}, counts.value, { [res.value.key]: rows.value.length })
}

function goOverview () {
  current.value = 'overview'
  kw.value = ''
  loadCounts()
}

function select (key) {
  current.value = key
  kw.value = ''
  loadRows()
}

function openCreate () {
  form.value = emptyForm(res.value)
  editing.value = false
  formError.value = ''
  showForm.value = true
}

function openEdit (row) {
  form.value = Object.assign({}, row)
  editing.value = true
  formError.value = ''
  showForm.value = true
}

async function submitForm () {
  const missing = editable.value.find(c => c.required && String(form.value[c.key] === null || form.value[c.key] === undefined ? '' : form.value[c.key]).trim() === '')
  if (missing) {
    formError.value = '请填写「' + missing.label + '」'
    return
  }
  formError.value = ''

  // 只提交可编辑字段，避免覆盖数据库维护的创建/更新时间
  const payload = {}
  editable.value.forEach(c => {
    let v = form.value[c.key]
    if (c.type === 'number') v = (v === '' || v === null || v === undefined) ? null : Number(v)
    payload[c.key] = v
  })
  if (editing.value) payload.id = form.value.id

  saving.value = true
  const r = await adminRequest('/data/' + res.value.key, {
    method: editing.value ? 'PUT' : 'POST',
    body: JSON.stringify(payload)
  })
  saving.value = false
  if (unauthorized(r)) return
  if (r.code !== 0) {
    showToast(r.msg || '保存失败', '✕')
    return
  }
  showForm.value = false
  showToast(editing.value ? '修改已保存，刷新门户即可生效' : '新增成功，刷新门户即可生效', '✦')
  loadRows()
}

async function doDelete () {
  const row = deleting.value
  if (!row) return
  const r = await adminRequest('/data/' + res.value.key + '/' + row.id, { method: 'DELETE' })
  deleting.value = null
  if (unauthorized(r)) return
  if (r.code !== 0) {
    showToast(r.msg || '删除失败', '✕')
    return
  }
  showToast('记录已删除', '✦')
  loadRows()
}

async function doLogout () {
  await adminRequest('/logout', { method: 'POST' })
  emit('logout')
}

onMounted(async () => {
  const ok = await checkSession()
  if (ok) loadCounts()
})
</script>

<style scoped>
.admin{
  position:relative;z-index:2;height:100vh;display:flex;flex-direction:column;overflow:hidden;
  background:linear-gradient(180deg, rgba(9,7,17,.86), rgba(6,5,12,.94));
}
/* 顶栏 */
.topbar{
  flex:none;height:64px;display:flex;align-items:center;justify-content:space-between;gap:16px;
  padding:0 22px;border-bottom:1px solid var(--line);
  background:rgba(11,8,21,.9);backdrop-filter:blur(16px);
}
.tb-brand{display:flex;align-items:center;gap:12px;}
.tb-brand .seal{
  width:38px;height:38px;border-radius:11px;display:grid;place-items:center;flex:none;
  font-family:var(--serif);font-size:20px;font-weight:700;color:#140a22;
  background:linear-gradient(140deg,var(--rune),var(--fire-1));box-shadow:0 0 20px var(--glow);
}
.tb-txt{display:flex;flex-direction:column;gap:3px;}
.tb-txt b{font-family:var(--serif);font-size:17px;letter-spacing:.12em;color:var(--text);}
.tb-txt i{font-style:normal;font-size:10px;letter-spacing:.28em;color:var(--muted);}
.tb-right{display:flex;align-items:center;gap:12px;}
.online{font-size:11.5px;color:#7fd8a8;letter-spacing:.06em;display:flex;align-items:center;gap:6px;}
.online i{width:6px;height:6px;border-radius:50%;background:#5ad68f;box-shadow:0 0 9px #5ad68f;}
.who{display:flex;align-items:center;gap:8px;font-size:12.5px;color:var(--text);padding:5px 12px 5px 5px;border:1px solid var(--line);border-radius:99px;}
.who .av{width:25px;height:25px;border-radius:50%;display:grid;place-items:center;font-family:var(--serif);font-size:13px;color:#140a22;background:linear-gradient(140deg,var(--rune),var(--fire-1));}
.tb-btn{
  height:32px;padding:0 14px;border-radius:9px;cursor:pointer;white-space:nowrap;
  font-family:var(--sans);font-size:12px;letter-spacing:.06em;color:var(--rune);
  background:rgba(255,217,138,.07);border:1px solid var(--line-strong);transition:.25s;
}
.tb-btn:hover{background:rgba(255,217,138,.18);transform:translateY(-1px);}
.tb-btn.danger{color:#ff9aa5;border-color:rgba(255,120,135,.28);background:rgba(255,90,110,.07);}
.tb-btn.danger:hover{background:rgba(255,90,110,.16);}

/* 主体 */
.body{flex:1;display:flex;min-height:0;}
.side-nav{
  width:216px;flex:none;padding:20px 12px;overflow-y:auto;border-right:1px solid var(--line);
  background:rgba(12,9,22,.62);display:flex;flex-direction:column;gap:4px;
}
.side-cap{font-size:10.5px;letter-spacing:.28em;color:var(--muted);padding:0 12px 12px;}
.side-item{
  display:flex;align-items:center;gap:10px;width:100%;height:42px;padding:0 11px;border-radius:11px;
  cursor:pointer;text-align:left;color:#b8b0c8;background:none;border:1px solid transparent;transition:.22s;
}
.side-item:hover{background:rgba(255,255,255,.04);color:var(--text);}
.side-item.on{color:var(--rune);background:rgba(255,217,138,.08);border-color:var(--line);}
.si-ic{
  width:26px;height:26px;flex:none;border-radius:8px;display:grid;place-items:center;
  font-family:var(--serif);font-size:13px;color:var(--rune);
  background:rgba(255,217,138,.08);border:1px solid var(--line);
}
.side-item.on .si-ic{background:linear-gradient(140deg,var(--rune),var(--fire-1));color:#140a22;border-color:transparent;}
.si-txt{flex:1;font-size:13px;letter-spacing:.06em;}
.side-item em{font-style:normal;font-size:11px;color:var(--muted);}
.side-foot{margin-top:auto;padding:14px 12px 4px;font-size:11px;line-height:1.9;color:#615b74;border-top:1px solid rgba(255,255,255,.06);}

.main{flex:1;min-width:0;overflow:auto;padding:26px 30px 44px;}
.page-head{display:flex;align-items:flex-start;justify-content:space-between;gap:18px;margin-bottom:22px;}
.page-head h1{font-family:var(--serif);font-size:26px;letter-spacing:.14em;font-weight:700;color:var(--rune);}
.page-head p{margin-top:9px;font-size:12.5px;line-height:1.8;color:var(--muted);letter-spacing:.04em;}
.count-badge{flex:none;font-size:12px;color:var(--rune);padding:7px 14px;border-radius:99px;background:rgba(255,217,138,.07);border:1px solid var(--line);}

/* 总览卡片 */
.ov-grid{display:grid;grid-template-columns:repeat(auto-fill,minmax(230px,1fr));gap:16px;}
.ov-card{
  position:relative;text-align:left;padding:20px;border-radius:16px;cursor:pointer;overflow:hidden;
  color:var(--text);background:linear-gradient(165deg,rgba(24,19,38,.86),rgba(10,8,18,.94));
  border:1px solid var(--line);transition:transform .25s, border-color .25s, box-shadow .3s;
}
.ov-card:hover{transform:translateY(-5px);border-color:var(--line-strong);box-shadow:0 26px 54px -28px var(--glow);}
.ov-ic{width:40px;height:40px;border-radius:12px;display:grid;place-items:center;font-family:var(--serif);font-size:19px;color:#140a22;background:linear-gradient(140deg,var(--rune),var(--fire-1));box-shadow:0 0 18px var(--glow);}
.ov-card b{display:block;font-family:var(--serif);font-size:17px;letter-spacing:.1em;margin-top:14px;}
.ov-count{display:block;font-family:var(--serif);font-size:27px;color:var(--rune);margin-top:8px;}
.ov-count i{font-style:normal;font-size:12px;color:var(--muted);margin-left:4px;}
.ov-card p{font-size:11.5px;line-height:1.8;color:var(--muted);margin-top:8px;min-height:42px;}
.ov-go{font-size:11.5px;color:var(--rune);opacity:.75;letter-spacing:.08em;}
.notice{display:flex;align-items:flex-start;gap:10px;margin-top:24px;padding:16px 18px;border-radius:14px;font-size:12px;line-height:1.9;color:#a9a1bc;background:rgba(255,217,138,.05);border:1px solid var(--line);}
.notice .spark{width:8px;height:8px;flex:none;margin-top:7px;transform:rotate(45deg);background:linear-gradient(135deg,var(--fire-1),var(--rune));box-shadow:0 0 12px var(--glow);}

/* 工具栏 */
.toolbar{display:flex;align-items:center;gap:10px;flex-wrap:wrap;margin-bottom:16px;}
.search{display:flex;align-items:center;gap:9px;height:38px;min-width:220px;flex:1;max-width:340px;padding:0 13px;border-radius:10px;background:rgba(255,255,255,.04);border:1px solid var(--line);transition:.25s;}
.search:focus-within{border-color:var(--fire-1);box-shadow:0 0 0 3px var(--glow-soft);}
.search svg{width:15px;height:15px;flex:none;stroke:var(--muted);fill:none;stroke-width:1.7;}
.search:focus-within svg{stroke:var(--fire-1);}
.search input{flex:1;min-width:0;background:none;border:0;outline:none;color:var(--text);font-size:13px;font-family:var(--sans);}
.search input::placeholder{color:#605a72;}
.btn{height:38px;padding:0 17px;border-radius:10px;cursor:pointer;white-space:nowrap;font-family:var(--sans);font-size:12.5px;letter-spacing:.08em;transition:.25s;}
.btn.ghost{color:var(--rune);background:rgba(255,217,138,.06);border:1px solid var(--line-strong);}
.btn.ghost:hover{background:rgba(255,217,138,.16);}
.btn.primary{color:#fff;border:0;background:linear-gradient(135deg,var(--fire-1),var(--fire-2));box-shadow:0 10px 26px -12px var(--glow);}
.btn.primary:hover{transform:translateY(-2px);}
.btn.danger{color:#fff;border:0;background:linear-gradient(135deg,#e2506a,#a51f3c);}
.btn:disabled{cursor:wait;opacity:.65;transform:none;}

/* 表格 */
.table-wrap{overflow:auto;border:1px solid var(--line);border-radius:14px;background:rgba(12,9,22,.6);}
table{border-collapse:collapse;width:100%;min-width:max-content;font-size:12.5px;}
thead th{
  position:sticky;top:0;z-index:1;text-align:left;padding:13px 14px;white-space:nowrap;
  font-weight:400;letter-spacing:.1em;color:var(--rune);font-size:11.5px;
  background:rgba(24,19,38,.97);border-bottom:1px solid var(--line-strong);
}
tbody td{padding:12px 14px;color:#cfc7dd;border-bottom:1px solid rgba(255,255,255,.05);vertical-align:middle;}
tbody tr:hover td{background:rgba(255,255,255,.025);}
tbody tr:last-child td{border-bottom:0;}
.cell{display:block;max-width:300px;overflow:hidden;text-overflow:ellipsis;white-space:nowrap;}
.idx{color:#6f6982;width:46px;text-align:center;}
.ops{white-space:nowrap;}
.mini{height:27px;padding:0 11px;margin-right:6px;border-radius:7px;cursor:pointer;font-size:11.5px;color:var(--rune);background:rgba(255,217,138,.07);border:1px solid var(--line);transition:.2s;}
.mini:hover{background:rgba(255,217,138,.2);}
.mini.danger{color:#ff9aa5;border-color:rgba(255,120,135,.25);background:rgba(255,90,110,.06);}
.mini.danger:hover{background:rgba(255,90,110,.18);}
.empty{text-align:center;padding:38px 0 !important;color:var(--muted);font-size:12.5px;}

/* 弹窗 */
.mask{position:fixed;inset:0;z-index:15;display:grid;place-items:center;padding:20px;background:rgba(4,3,9,.78);backdrop-filter:blur(6px);}
.dialog{
  width:100%;max-width:520px;max-height:86vh;overflow-y:auto;padding:26px;border-radius:18px;
  background:linear-gradient(165deg,rgba(26,21,42,.98),rgba(11,9,20,.99));
  border:1px solid var(--line-strong);box-shadow:0 40px 90px -30px #000,0 0 70px -30px var(--glow);
}
.dialog.small{max-width:400px;}
.dialog h3{display:flex;align-items:center;gap:9px;font-family:var(--serif);font-size:20px;letter-spacing:.1em;color:var(--rune);margin-bottom:20px;}
.dialog h3 .spark{width:8px;height:8px;flex:none;transform:rotate(45deg);background:linear-gradient(135deg,var(--fire-1),var(--rune));box-shadow:0 0 12px var(--glow);}
.fld{display:block;margin-bottom:14px;}
.fld > span{display:block;font-size:12px;color:#b7aecb;letter-spacing:.06em;margin-bottom:7px;}
.fld > span i{font-style:normal;color:#ff7b8c;}
.fld input,.fld textarea{
  width:100%;padding:10px 12px;border-radius:10px;outline:none;resize:vertical;
  font-family:var(--sans);font-size:13px;line-height:1.7;color:var(--text);
  background:rgba(255,255,255,.04);border:1px solid rgba(255,255,255,.1);transition:.25s;
}
.fld input:focus,.fld textarea:focus{border-color:var(--fire-1);box-shadow:0 0 0 3px var(--glow-soft);}
.fld input::placeholder,.fld textarea::placeholder{color:#5f5973;}
.confirm-txt{font-size:13px;line-height:2;color:#c3bbd3;margin-bottom:8px;}
.dlg-err{font-size:12px;color:#ff7b8c;margin:2px 0 12px;}
.dlg-ops{display:flex;justify-content:flex-end;gap:10px;margin-top:20px;}

@media (max-width:900px){
  .side-nav{width:64px;padding:16px 8px;}
  .side-cap,.si-txt,.side-item em,.side-foot{display:none;}
  .side-item{justify-content:center;padding:0;}
  .main{padding:20px 16px 36px;}
  .online{display:none;}
}
@media (max-width:640px){
  .tb-txt i,.who{display:none;}
  .tb-btn{padding:0 10px;font-size:11px;}
}
</style>

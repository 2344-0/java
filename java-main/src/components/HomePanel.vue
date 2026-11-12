<template>
  <div class="home">
    <!-- ===== 导航栏 ===== -->
    <header class="nav">
      <div class="nav-inner">
        <a class="brand" href="javascript:void(0)" @click="goHome">
          <span class="seal">斗</span>
          <span class="brand-text"><b>斗破苍穹</b><i>斗气大陆</i></span>
        </a>
        <nav class="nav-links">
          <a
            v-for="(l, i) in navLinks"
            :key="l"
            :class="{ on: activeNav === i }"
            :data-cat="navPages[i]"
            @click="onNav(i)"
          >{{ l }}</a>
        </nav>
        <div class="nav-right">
          <div class="nav-user" title="斗者信息" @click="showUserInfo">
            <span class="av">{{ initial }}</span>
            <span><b>{{ username || '萧炎' }}</b> <span class="lv">斗之气三段</span></span>
          </div>
          <button class="admin-entry" type="button" title="进入后台管理系统" @click="$emit('goto-admin')">
            <svg viewBox="0 0 24 24"><rect x="3" y="4" width="18" height="16" rx="2"/><path d="M3 9h18M9 9v11"/></svg>
            后台管理
          </button>
          <div class="themes">
            <i
              v-for="opt in themeOpts"
              :key="opt.key"
              :data-t="opt.key"
              :class="{ on: theme === opt.key }"
              :title="opt.name"
              @click="pickTheme(opt.key)"
            ></i>
          </div>
        </div>
      </div>
    </header>

    <div class="wrap">
      <template v-if="page === 'home'">
      <!-- ===== 英雄区 + 搜索栏 ===== -->
      <section class="hero">
        <div class="hero-rings"></div>
        <h1>斗 气 大 陆</h1>
        <p class="sub">手托莲台焚万法 · 尺镇八荒入长河　<b>莫欺少年穷</b></p>
        <form class="search" autocomplete="off" @submit.prevent="doSearch(searchInput)">
          <svg viewBox="0 0 24 24"><circle cx="11" cy="11" r="7"/><path d="m20 20-3.2-3.2"/></svg>
          <input v-model="searchInput" type="text" placeholder="搜寻 异火 · 功法 · 斗者 · 宗门……" />
          <button type="submit">搜 寻</button>
        </form>
        <div class="hot">
          热门：
          <a
            v-for="q in hotWords"
            :key="q"
            @click="searchInput = q; doSearch(q)"
          >{{ q }}</a>
        </div>
      </section>

      <!-- ===== 快捷分类 ===== -->
      <section class="section">
        <div class="section-head"><h2><span class="spark"></span>修行之所</h2></div>
        <div class="cats">
          <a
            v-for="c in cats"
            :key="c.name"
            class="cat"
            @click="browse(c.name)"
          >
            <span class="ic"><svg viewBox="0 0 24 24" v-html="c.icon"></svg></span>
            <b>{{ c.name }}</b>
            <span>{{ c.desc }}</span>
          </a>
        </div>
      </section>

      <!-- ===== 异火图鉴 ===== -->
      <section class="section">
        <div class="section-head">
          <h2><span class="spark"></span>异火图鉴</h2>
          <a class="more" @click="resetFire">显示全部异火</a>
        </div>
        <div class="fire-grid" ref="fireGrid">
          <article
            v-for="f in fires"
            :key="f.name"
            class="card fire-card"
            :style="{ display: f.hidden ? 'none' : '' }"
          >
            <img class="fc-img" :src="f.image" :alt="f.name" loading="lazy" @error="onImgError" />
            <span class="rank">{{ f.rank }}</span>
            <h3>{{ f.name }}</h3><span class="lv">{{ f.lv }}</span>
            <p>{{ f.desc }}</p>
          </article>
          <div class="empty" :style="{ display: fireEmpty ? 'block' : 'none' }">未寻得此异火，换个咒印再试 ——</div>
        </div>
      </section>

      <!-- ===== 检索 · 丹药坊（仅搜索且有命中时出现） ===== -->
      <section class="section" v-if="searchInput && searchPills.length">
        <div class="section-head">
          <h2><span class="spark"></span>检索 · 丹药坊</h2>
          <a class="more" @click="resetFire">清除检索</a>
        </div>
        <div class="pill-results">
          <article v-for="p in searchPills" :key="p.name" class="pr-card">
            <span class="pr-grade">{{ p.grade }}</span>
            <h4>{{ p.name }}</h4>
            <p class="pr-eff">{{ p.effect }}</p>
            <p class="pr-mat">主材 · {{ p.material || '——' }}</p>
          </article>
        </div>
      </section>

      <!-- ===== 双栏：功法榜 + 斗者动态 ===== -->
      <section class="section">
        <div class="section-head"><h2><span class="spark"></span>斗气风云</h2></div>
        <div class="cols">
          <div class="card">
            <div class="section-head" style="margin-bottom:8px;"><h2 style="font-size:18px;">热门功法</h2><a class="more">查看全部</a></div>
            <div class="list">
              <div v-for="(g, idx) in gongfa" :key="g.name" class="item">
                <span class="no">{{ cnNum[idx] }}</span>
                <div><b>{{ g.name }}</b><span>{{ g.desc }}</span></div>
                <span class="hotv">🔥 {{ g.hot }}</span>
              </div>
            </div>
          </div>
          <div class="card">
            <div class="section-head" style="margin-bottom:8px;"><h2 style="font-size:18px;">斗者动态</h2><a class="more">进入宗门</a></div>
            <div class="feed">
              <div v-for="f in feed" :key="f.name + f.time" class="item">
                <span class="av">{{ f.name.charAt(0) }}</span>
                <div class="txt">
                  <b>{{ f.name }}</b> <time>{{ f.time }}</time>
                  <p>{{ f.text }}</p>
                </div>
              </div>
            </div>
          </div>
        </div>
      </section>

      <div class="foot">
        斗破苍穹 · 斗气大陆门户　|　手托莲台焚万法，尺倚身旁镇八荒<br />
        <a class="link" @click="$emit('logout')">退出斗气</a>
      </div>
      </template>

      <!-- ===== 子页面：异火榜 / 功法阁 / 丹药坊 / 斗气榜 / 宗门 ===== -->
      <FireRankPanel v-else-if="page === 'fire'" />
      <GongfaRankPanel v-else-if="page === 'gongfa'" />
      <PillPanel v-else-if="page === 'pill'" />
      <DouqiRankPanel v-else-if="page === 'douqi'" />
      <SectPanel v-else-if="page === 'sect'" />
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, nextTick, onMounted } from 'vue'
import { theme, themeMeta, setTheme } from '../composables/useTheme'
import { showToast } from '../composables/useToast'
import FireRankPanel from './FireRankPanel.vue'
import GongfaRankPanel from './GongfaRankPanel.vue'
import PillPanel from './PillPanel.vue'
import DouqiRankPanel from './DouqiRankPanel.vue'
import SectPanel from './SectPanel.vue'

// 后端首页接口地址（与后端 springBootTest 端口 8081 保持一致）
const API_HOME = 'http://localhost:8081/api/home'
const API_SEARCH = 'http://localhost:8081/api/home/search'

const props = defineProps({
  username: { type: String, default: '' }
})
const emit = defineEmits(['logout', 'goto-admin'])

const initial = computed(() => (props.username ? props.username.charAt(0) : '斗'))

/* ===== 导航 ===== */
const navLinks = ['首页', '异火榜', '功法阁', '丹药坊', '斗气榜', '宗门']
// 每个导航项对应的子页面：首页 / 异火榜 / 功法阁 / 丹药坊 为真实页面，斗气榜/宗门暂未展开
const navPages = ['home', 'fire', 'gongfa', 'pill', 'douqi', 'sect']
const activeNav = ref(0)
const page = ref('home')

function onNav (i) {
  activeNav.value = i
  const p = navPages[i]
  if (p === 'home') {
    page.value = 'home'
    goTop()
  } else {
    page.value = p
    goTop()
  }
}
// 品牌点击：回到首页
function goHome () {
  page.value = 'home'
  activeNav.value = 0
  goTop()
}
function goTop () { window.scrollTo({ top: 0, behavior: 'smooth' }) }
function showUserInfo () {
  toast('斗者 · ' + (props.username || '萧炎') + '　|　斗之气三段')
}

/* ===== 主题 ===== */
const themeOpts = [
  { key: 'yaohuo', name: '净莲妖火' },
  { key: 'qinglian', name: '青莲地心火' },
  { key: 'yanyan', name: '陨落心炎' }
]
function pickTheme (key) {
  setTheme(key)
  toast('异火已换 · ' + themeMeta[key].name)
}

/* ===== 搜索 ===== */
const searchInput = ref('')
const fireGrid = ref(null)

/* ===== 快捷分类 ===== */
const cats = [
  { name: '异火榜', desc: '天地奇火', icon: '<path d="M12 3c1 3-2 4-2 7a3 3 0 0 0 6 0c0-1-.5-2-1-2.5C16 9 18 12 18 15a6 6 0 1 1-12 0c0-4 3-6 4-8 .6-1 1.4-2.2 2-4Z"/>' },
  { name: '功法阁', desc: '焚决斗技', icon: '<path d="M12 3v18M5 8l7-5 7 5M5 8v8l7 5 7-5V8"/>' },
  { name: '丹药坊', desc: '凝丹炼药', icon: '<path d="M4.5 12a7.5 7.5 0 0 1 15 0c0 2-1 3-1 5a5 5 0 0 1-6 5 5 5 0 0 1-6-5c0-2-1-3-1-5Z"/><path d="M9 12h6"/>' },
  { name: '斗气榜', desc: '段位晋升', icon: '<path d="M12 2 4 6v6c0 5 3.5 8 8 10 4.5-2 8-5 8-10V6Z"/><path d="m9 12 2 2 4-4"/>' },
  { name: '宗门', desc: '迦南星陨', icon: '<path d="M4 21V9l8-5 8 5v12M9 21v-6h6v6"/>' },
  { name: '拍卖行', desc: '天材地宝', icon: '<circle cx="12" cy="12" r="8"/><path d="M12 8v8M9 10h6M9 14h6"/>' }
]
function browse (cat) {
  const map = { '异火榜': 'fire', '功法阁': 'gongfa', '丹药坊': 'pill' }
  if (map[cat]) {
    page.value = map[cat]
    activeNav.value = navLinks.indexOf(cat)
    goTop()
  } else {
    toast('此卷轴尚未展开 · ' + cat, '✦')
  }
}

/* ===== 异火图鉴（数据来自后端 /api/home） ===== */
const fires = ref([])
const fireEmpty = ref(false)
const searchPills = ref([])
const homeLoading = ref(false)

// 把后端异火字段映射为前端卡片所需结构
function mapFire (f) {
  return { name: f.name, rank: f.rankName, lv: f.level, desc: f.description, image: f.image, hidden: false }
}

// 异火配图加载失败时的兜底：隐藏破图，避免影响版面
function onImgError (e) {
  const el = e && e.target
  if (el) el.style.visibility = 'hidden'
}

// 首页展示：一次拉取热词、异火、功法、动态
function loadHome () {
  homeLoading.value = true
  fetch(API_HOME)
    .then(r => {
      if (!r.ok) throw new Error('HTTP ' + r.status)
      return r.json()
    })
    .then(res => {
      homeLoading.value = false
      if (res && res.code === 0 && res.data) {
        const d = res.data
        fires.value = (d.fires || []).map(mapFire)
        gongfa.value = (d.gongfa || []).map(g => ({
          name: g.name, desc: g.description, hot: formatHot(g.hotValue)
        }))
        feed.value = d.feed || []
        hotWords.value = d.hotWords || []
        fireEmpty.value = fires.value.length === 0
      } else {
        toast(res && res.msg ? res.msg : '首页数据加载失败', '✕')
      }
    })
    .catch(() => {
      homeLoading.value = false
      toast('后端服务未启动，首页数据加载失败（http://localhost:8081）', '✕')
    })
}

onMounted(loadHome)

// 搜索：走后端 /api/home/search，同时命中异火 / 功法 / 斗者
function doSearch (kw) {
  kw = (kw || '').trim()
  if (!kw) {
    fires.value.forEach(c => { c.hidden = false })
    fireEmpty.value = false
    searchPills.value = []
    toast('已显示全部异火')
    return
  }
  fetch(API_SEARCH + '?kw=' + encodeURIComponent(kw))
    .then(r => {
      if (!r.ok) throw new Error('HTTP ' + r.status)
      return r.json()
    })
    .then(res => {
      if (!(res && res.code === 0 && res.data)) {
        toast(res && res.msg ? res.msg : '搜索失败，请稍后再试', '✕')
        return
      }
      const d = res.data
      const hitNames = new Set((d.fires || []).map(f => f.name))
      fires.value.forEach(c => { c.hidden = !hitNames.has(c.name) })
      fireEmpty.value = hitNames.size === 0
      searchPills.value = d.pills || []
      // 汇总提示：异火 / 功法 / 丹药 / 斗者 各命中几条
      const parts = []
      if ((d.fires || []).length) parts.push((d.fires).length + ' 道异火')
      if ((d.gongfa || []).length) parts.push((d.gongfa).length + ' 部功法')
      if ((d.pills || []).length) parts.push((d.pills).length + ' 味丹药')
      if ((d.users || []).length) parts.push((d.users).length + ' 位斗者')
      toast(d.total > 0 ? ('寻得 ' + parts.join('、') + '：' + kw) : ('未寻得「' + kw + '」，换个咒印再试'))
      nextTick(() => {
        if (fireGrid.value) fireGrid.value.scrollIntoView({ behavior: 'smooth', block: 'center' })
      })
    })
    .catch(() => toast('网络异常，搜索失败（http://localhost:8081）', '✕'))
}
function resetFire () {
  searchInput.value = ''
  doSearch('')
}

const hotWords = ref([])

/* ===== 功法榜 + 动态（数据来自后端 /api/home） ===== */
const cnNum = ['壹', '贰', '叁', '肆', '伍']
const gongfa = ref([])
const feed = ref([])

// 热度值格式化：98000 -> 9.8w
function formatHot (v) {
  const n = Number(v) || 0
  if (n >= 10000) {
    const w = n / 10000
    return (Math.round(w * 10) / 10) + 'w'
  }
  return String(n)
}

function toast (m, ic) { showToast(m, ic) }
</script>

<style scoped>
/* 首页门户样式由 global.css 中 .home 命名空间类提供（全局）。
   这里仅处理滚动：登录/注册页 body 为 overflow:hidden，
   首页门户内容较多需要独立滚动。 */
.home{
  position:relative;z-index:2;
  height:100vh;
  overflow-y:auto;
  overflow-x:hidden;
  scrollbar-width:thin;
  scrollbar-color:rgba(255,217,138,.28) transparent;
}
.home::-webkit-scrollbar{width:8px;}
.home::-webkit-scrollbar-thumb{background:rgba(255,217,138,.22);border-radius:4px;}
.home::-webkit-scrollbar-track{background:transparent;}

/* 导航栏 · 后台管理入口按钮 */
.admin-entry{
  display:flex;align-items:center;gap:7px;height:34px;padding:0 14px;border-radius:99px;
  cursor:pointer;white-space:nowrap;font-family:var(--sans);font-size:12.5px;letter-spacing:.08em;
  color:var(--rune);background:rgba(255,217,138,.07);border:1px solid var(--line-strong);
  transition:background .25s, box-shadow .25s, transform .25s;
}
.admin-entry svg{width:14px;height:14px;flex:none;stroke:currentColor;fill:none;stroke-width:1.7;}
.admin-entry:hover{background:rgba(255,217,138,.18);box-shadow:0 0 16px -4px var(--glow);transform:translateY(-1px);}
@media (max-width:640px){.admin-entry{padding:0 10px;font-size:11.5px;}}

/* 首页搜索 · 丹药坊命中结果 */
.home .pill-results{display:grid;grid-template-columns:repeat(3,1fr);gap:14px;}
.home .pr-card{position:relative;border-radius:16px;padding:16px 18px;background:linear-gradient(165deg, rgba(24,19,38,.82), rgba(10,8,18,.9));border:1px solid var(--line);box-shadow:0 24px 60px -30px rgba(0,0,0,.9);transition:transform .25s, box-shadow .3s, border-color .3s;overflow:hidden;}
.home .pr-card:hover{transform:translateY(-4px);border-color:var(--line-strong);box-shadow:0 30px 60px -28px var(--glow);}
.home .pr-grade{font-family:var(--serif);font-size:12px;letter-spacing:.06em;color:#140a22;padding:4px 11px;border-radius:99px;background:linear-gradient(140deg,var(--rune),var(--fire-1));box-shadow:0 0 16px -3px var(--glow);}
.home .pr-card h4{font-family:var(--serif);font-size:18px;font-weight:700;letter-spacing:.08em;margin:12px 0 0;}
.home .pr-eff{font-size:12.5px;line-height:1.8;color:var(--rune);margin:8px 0 0;font-family:var(--serif);}
.home .pr-mat{font-size:11.5px;line-height:1.7;color:#cfc6e2;margin:6px 0 0;}
@media (max-width:1024px){.home .pill-results{grid-template-columns:repeat(2,1fr);}}
@media (max-width:640px){.home .pill-results{grid-template-columns:1fr;}}
</style>

<template>
  <div class="home">
    <!-- ===== 导航栏 ===== -->
    <header class="nav">
      <div class="nav-inner">
        <a class="brand" href="javascript:void(0)" @click="goTop">
          <span class="seal">斗</span>
          <span class="brand-text"><b>斗破苍穹</b><i>斗气大陆</i></span>
        </a>
        <nav class="nav-links">
          <a
            v-for="(l, i) in navLinks"
            :key="l"
            :class="{ on: activeNav === i }"
            :data-cat="navCats[i]"
            @click="onNav(i)"
          >{{ l }}</a>
        </nav>
        <div class="nav-right">
          <div class="nav-user" title="斗者信息" @click="showUserInfo">
            <span class="av">{{ initial }}</span>
            <span><b>{{ username || '萧炎' }}</b> <span class="lv">斗之气三段</span></span>
          </div>
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
            <span class="rank">{{ f.rank }}</span>
            <svg class="flame" viewBox="0 0 24 24" fill="none" stroke="var(--fire-1)" stroke-width="1.4"><path d="M12 3c1 3-2 4-2 7a3 3 0 0 0 6 0c0-1-.5-2-1-2.5C16 9 18 12 18 15a6 6 0 1 1-12 0c0-4 3-6 4-8 .6-1 1.4-2.2 2-4Z"/></svg>
            <h3>{{ f.name }}</h3><span class="lv">{{ f.lv }}</span>
            <p>{{ f.desc }}</p>
          </article>
          <div class="empty" :style="{ display: fireEmpty ? 'block' : 'none' }">未寻得此异火，换个咒印再试 ——</div>
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
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, nextTick } from 'vue'
import { theme, themeMeta, setTheme } from '../composables/useTheme'
import { showToast } from '../composables/useToast'

const props = defineProps({
  username: { type: String, default: '' }
})
const emit = defineEmits(['logout'])

const initial = computed(() => (props.username ? props.username.charAt(0) : '斗'))

/* ===== 导航 ===== */
const navLinks = ['首页', '异火榜', '功法阁', '丹药坊', '斗气榜', '宗门']
const navCats = ['', '异火', '功法', '丹药', '斗气', '宗门']
const activeNav = ref(0)
function onNav (i) {
  activeNav.value = i
  const c = navCats[i]
  if (c) {
    searchInput.value = c
    doSearch(c)
    toast('前往 · ' + navLinks[i])
  } else {
    goTop()
  }
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
  searchInput.value = cat
  doSearch(cat)
  toast('浏览 · ' + cat)
}

/* ===== 异火图鉴 ===== */
const fires = reactive([
  { name: '净莲妖火', rank: '异火榜 · 第三', lv: '榜三 · 灵智化生', desc: '呈莲花之形，可化人形，焚尽万物而灵智自生，乃异火中罕见具魂者。', kw: '净莲妖火 妖火 莲台', hidden: false },
  { name: '青莲地心火', rank: '异火榜 · 第十九', lv: '榜十九 · 地心所孕', desc: '生于地心熔岩，莲台模样，可炼体亦可炼丹，温润而炽烈。', kw: '青莲地心火 青莲 地心', hidden: false },
  { name: '陨落心炎', rank: '异火榜 · 第十四', lv: '榜十四 · 助修炼', desc: '潜伏体内助长斗气，修炼事半功倍，然反噬亦烈，需以心智制之。', kw: '陨落心炎 心炎 修炼', hidden: false },
  { name: '骨灵冷火', rank: '异火榜 · 第十一', lv: '榜十一 · 冰火相济', desc: '韩枫师徒所掌之异火，与焚决相融可成冰火双重，诡谲非常。', kw: '骨灵冷火 骨灵 冷火', hidden: false },
  { name: '海心焰', rank: '异火榜 · 第十五', lv: '榜十五 · 深海洋流', desc: '生于万丈深海之底，呈幽蓝之色，炽热中藏冰凉，炼药之上品。', kw: '海心焰 海心 海洋', hidden: false },
  { name: '三千焱炎火', rank: '异火榜 · 第九', lv: '榜九 · 星辰不灭', desc: '又称三千星空焱炎，具不灭之体，受创可自星空重生，威能滔天。', kw: '三千焱炎火 三千 星空', hidden: false },
  { name: '九幽金祖火', rank: '异火榜 · 第六', lv: '榜六 · 金芒镇幽', desc: '幽冥之中生出金芒，可焚魂炼魄，与火山石焰融合可衍新生异火。', kw: '九幽金祖火 九幽 金祖', hidden: false },
  { name: '陀舍古帝烛火', rank: '异火榜 · 第一', lv: '榜一 · 万火之祖', desc: '异火榜魁首，相传为万火之始，集千家异火于一身，可焚天煮海。', kw: '陀舍古帝烛火 陀舍 古帝 帝火', hidden: false }
])
const fireEmpty = ref(false)

function doSearch (kw) {
  kw = (kw || '').trim()
  if (!kw) {
    fires.forEach(c => { c.hidden = false })
    fireEmpty.value = false
    toast('已显示全部异火')
    return
  }
  let hit = 0
  fires.forEach(c => {
    const hay = (c.kw || '') + ' ' + c.name
    const ok = hay.toLowerCase().indexOf(kw.toLowerCase()) > -1
    c.hidden = !ok
    if (ok) hit++
  })
  fireEmpty.value = hit === 0
  toast(hit ? ('寻得 ' + hit + ' 道异火：' + kw) : ('未寻得「' + kw + '」，换个咒印再试'))
  nextTick(() => {
    if (fireGrid.value) fireGrid.value.scrollIntoView({ behavior: 'smooth', block: 'center' })
  })
}
function resetFire () {
  searchInput.value = ''
  doSearch('')
}

const hotWords = ['净莲妖火', '焚决', '玄重尺', '药老', '萧炎']

/* ===== 功法榜 + 动态 ===== */
const cnNum = ['壹', '贰', '叁', '肆', '伍']
const gongfa = [
  { name: '焚决', desc: '可吞噬异火进阶的至强功法', hot: '9.8w' },
  { name: '九转斗者决', desc: '稳固根基，斗气绵长不绝', hot: '6.2w' },
  { name: '八极崩', desc: '近身斗技，一击叠浪八重', hot: '5.5w' },
  { name: '焰分噬浪尺', desc: '玄重尺所施，尺出焰浪滔天', hot: '4.9w' },
  { name: '三千雷动', desc: '身法斗技，雷影瞬息千里', hot: '4.1w' }
]
const feed = [
  { name: '药老', time: '片刻前', text: '新得一味「龙血芝」，可助斗王突破瓶颈，欲求者速来丹药坊。' },
  { name: '萧炎', time: '半个时辰前', text: '于迦南学院秘境收服青莲地心火，斗气大涨，叩谢诸位相助。' },
  { name: '美杜莎', time: '今晨', text: '蛇人族与迦南结盟，异火共享事宜已在宗门议定。' },
  { name: '薰儿', time: '昨日', text: '古族藏书阁新添《异火通鉴》，已向诸位斗者开放查阅。' }
]

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
</style>

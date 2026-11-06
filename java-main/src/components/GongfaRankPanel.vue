<template>
  <div class="rank-page gongfa-rank">
    <section class="section">
      <div class="section-head">
        <h2><span class="spark"></span>功法榜 · 焚决斗技录</h2>
        <span class="count">共 {{ gongfas.length }} 部功法</span>
      </div>
      <p class="lead">斗气大陆功法斗技无数，以修习者众、威能盛者为尊。榜上以热度定名次。</p>

      <ol class="rank-list">
        <li
          v-for="(g, i) in gongfas"
          :key="g.name"
          class="rank-item"
          :class="{ top: i < 3 }"
          @click="openDetail(g, i)"
        >
          <div class="rank-no">
            <svg class="sword" viewBox="0 0 24 24" fill="none" stroke="var(--rune)" stroke-width="1.4"><path d="M12 3v18M5 8l7-5 7 5M5 8v8l7 5 7-5V8"/></svg>
            <b>{{ i + 1 }}</b>
          </div>
          <div class="r-card">
            <div class="rb-head">
              <span class="rb-rank">{{ cnNum[i] }} 甲</span>
              <h3>{{ g.name }}</h3>
              <span class="hotv">🔥 {{ formatHot(g.hotValue) }}</span>
            </div>
            <p class="rb-desc">{{ g.description }}</p>
            <div class="heat">
              <span class="heat-bar" :style="{ width: heatWidth(g.hotValue) }"></span>
            </div>
          </div>
        </li>
      </ol>

      <div v-if="loading" class="state">功法榜加载中……</div>
      <div v-if="!loading && gongfas.length === 0" class="state">功法榜暂无记录</div>
    </section>

    <DetailModal :open="!!selected" :item="selected || {}" @close="selected = null" />
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { showToast } from '../composables/useToast'
import DetailModal from './DetailModal.vue'

const API = 'http://localhost:8081/api/rank/gongfas'

const gongfas = ref([])
const loading = ref(true)
const selected = ref(null)
const cnNum = ['壹', '贰', '叁', '肆', '伍', '陆', '柒', '捌', '玖', '拾']

function openDetail (g, i) {
  selected.value = {
    lead: '功法',
    rank: '功法榜 · 第 ' + cnNum[i] + ' 甲',
    title: g.name,
    tag: formatHot(g.hotValue),
    desc: g.description,
    rows: [
      { label: '名次', value: '第 ' + cnNum[i] + ' 甲' },
      { label: '热度', value: formatHot(g.hotValue) }
    ]
  }
}

const maxHot = computed(() => {
  let m = 1
  for (const g of gongfas.value) m = Math.max(m, Number(g.hotValue) || 0)
  return m
})

function heatWidth (v) {
  const n = Number(v) || 0
  return Math.max(6, Math.round((n / maxHot.value) * 100)) + '%'
}

function formatHot (v) {
  const n = Number(v) || 0
  if (n >= 10000) {
    const w = n / 10000
    return (Math.round(w * 10) / 10) + 'w'
  }
  return String(n)
}

onMounted(load)

function load () {
  loading.value = true
  fetch(API)
    .then(r => r.json())
    .then(res => {
      loading.value = false
      if (res && res.code === 0 && res.data) {
        gongfas.value = res.data
      } else {
        showToast(res && res.msg ? res.msg : '功法榜加载失败', '✕')
      }
    })
    .catch(() => {
      loading.value = false
      showToast('后端服务未启动（http://localhost:8081）', '✕')
    })
}
</script>

<style scoped>
.rank-page{position:relative;z-index:2;}
.lead{font-family:var(--serif);font-size:14px;letter-spacing:.14em;color:#b9b0cc;margin:-6px 0 26px;text-shadow:0 2px 14px rgba(0,0,0,.7);}
.count{font-size:12px;color:var(--muted);letter-spacing:.1em;}

.rank-list{list-style:none;display:flex;flex-direction:column;gap:14px;padding:0;margin:0;}
.rank-item{display:flex;gap:16px;align-items:stretch;cursor:pointer;}

.rank-no{
  position:relative;flex:none;width:64px;height:64px;border-radius:16px;display:grid;place-items:center;
  background:linear-gradient(160deg, rgba(255,217,138,.12), rgba(169,123,255,.16));
  border:1px solid var(--line);box-shadow:0 0 24px -8px var(--glow);
}
.rank-no .sword{position:absolute;inset:0;width:100%;height:100%;opacity:.16;filter:drop-shadow(0 0 10px var(--glow));}
.rank-no b{
  position:relative;font-family:var(--serif);font-size:30px;font-weight:700;line-height:1;
  background:linear-gradient(178deg,#fff8e8,var(--rune) 55%,var(--fire-1));
  -webkit-background-clip:text;background-clip:text;color:transparent;filter:drop-shadow(0 0 12px var(--glow-soft));
}
.rank-item.top .rank-no{border-color:var(--line-strong);background:linear-gradient(160deg, rgba(255,217,138,.22), rgba(169,123,255,.28));box-shadow:0 0 30px -6px var(--glow);}

.r-card{position:relative;flex:1;border-radius:16px;padding:16px 20px;
  background:linear-gradient(165deg, rgba(24,19,38,.82), rgba(10,8,18,.9));
  border:1px solid var(--line);box-shadow:0 24px 60px -30px rgba(0,0,0,.9);
  transition:transform .25s, box-shadow .3s, border-color .3s;overflow:hidden;}
.r-card::before,.r-card::after{content:"";position:absolute;width:18px;height:18px;pointer-events:none;border:1.5px solid var(--rune);opacity:.5;}
.r-card::before{top:-1px;left:-1px;border-right:0;border-bottom:0;border-radius:16px 0 0 0;}
.r-card::after{bottom:-1px;right:-1px;border-left:0;border-top:0;border-radius:0 0 16px 0;}
.rank-item:hover .r-card{transform:translateY(-3px);border-color:var(--line-strong);box-shadow:0 30px 60px -28px var(--glow);}

.rb-head{display:flex;align-items:center;gap:12px;flex-wrap:wrap;}
.rb-rank{font-size:11.5px;letter-spacing:.14em;color:var(--rune);padding:3px 10px;border-radius:99px;border:1px solid var(--line-strong);background:rgba(255,217,138,.06);}
.rb-head h3{font-family:var(--serif);font-size:20px;font-weight:700;letter-spacing:.1em;margin:0;}
.rb-head .hotv{margin-left:auto;font-size:12.5px;color:var(--muted);letter-spacing:.06em;}
.rb-desc{font-size:12.5px;line-height:1.9;color:#a59dc0;margin:10px 0 0;}

.heat{margin-top:12px;height:6px;border-radius:4px;background:rgba(255,255,255,.08);overflow:hidden;}
.heat-bar{display:block;height:100%;border-radius:4px;background:linear-gradient(90deg,var(--fire-1),var(--rune));box-shadow:0 0 12px var(--glow);transition:width .6s cubic-bezier(.2,.8,.25,1);}

.state{text-align:center;color:var(--muted);font-family:var(--serif);letter-spacing:.2em;padding:30px 0;}
</style>

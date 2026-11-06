<template>
  <div class="rank-page douqi-rank">
    <section class="section">
      <div class="section-head">
        <h2><span class="spark"></span>斗气榜 · 晋升之路</h2>
        <span class="count">共 {{ realms.length }} 重境界</span>
      </div>
      <p class="lead">斗气大陆以斗气为尊，凡人由斗之气始，循序而进，直抵斗帝之巅。榜上以晋升之序列境，下为起点，上为绝巅。</p>

      <ol class="rank-list">
        <li
          v-for="(r, i) in realms"
          :key="r.name"
          class="rank-item"
          :class="{ top: i === realms.length - 1 }"
          @click="openDetail(r, i)"
        >
          <div class="rank-no">
            <svg class="aura" viewBox="0 0 24 24" fill="none" stroke="var(--rune)" stroke-width="1.4"><path d="M4 20 9 9l4 5 3-7 4 13"/></svg>
            <b>{{ i + 1 }}</b>
          </div>
          <div class="r-card">
            <div class="rb-head">
              <span class="rb-rank">第 {{ cnNum[i] }} 境</span>
              <h3>{{ r.name }}</h3>
              <span class="lv">{{ r.subLevel }}</span>
            </div>
            <p class="rb-desc">{{ r.description }}</p>
            <span class="tier">{{ r.tierDesc }}</span>
          </div>
        </li>
      </ol>

      <div v-if="loading" class="state">斗气榜加载中……</div>
      <div v-if="!loading && realms.length === 0" class="state">斗气榜暂无记录</div>
    </section>

    <DetailModal :open="!!selected" :item="selected || {}" @close="selected = null" />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { showToast } from '../composables/useToast'
import DetailModal from './DetailModal.vue'

const API = 'http://localhost:8081/api/rank/douqis'

const realms = ref([])
const loading = ref(true)
const selected = ref(null)
const cnNum = ['一', '二', '三', '四', '五', '六', '七', '八', '九', '十', '十一', '十二', '十三', '十四', '十五']

function openDetail (r, i) {
  selected.value = {
    lead: '斗气境界',
    rank: '第 ' + cnNum[i] + ' 境',
    title: r.name,
    tag: r.subLevel,
    tag2: r.tierDesc,
    desc: r.description,
    rows: [{ label: '阶位特征', value: r.tierDesc }]
  }
}

onMounted(load)

function load () {
  loading.value = true
  fetch(API)
    .then(r => r.json())
    .then(res => {
      loading.value = false
      if (res && res.code === 0 && res.data) {
        realms.value = res.data
      } else {
        showToast(res && res.msg ? res.msg : '斗气榜加载失败', '✕')
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
.rank-no .aura{position:absolute;inset:0;width:100%;height:100%;opacity:.16;filter:drop-shadow(0 0 10px var(--glow));}
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
.rb-head .lv{font-size:11px;letter-spacing:.12em;color:#cfc6e2;padding:4px 11px;border-radius:99px;border:1px solid rgba(255,255,255,.1);background:rgba(14,10,24,.55);}
.rb-desc{font-size:12.5px;line-height:1.9;color:#a59dc0;margin:10px 0 0;}
.tier{display:inline-block;margin-top:10px;font-family:var(--serif);font-size:12px;letter-spacing:.1em;color:var(--cyan);}

.state{text-align:center;color:var(--muted);font-family:var(--serif);letter-spacing:.2em;padding:30px 0;}
</style>

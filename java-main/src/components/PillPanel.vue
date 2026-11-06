<template>
  <div class="rank-page pill-shop">
    <section class="section">
      <div class="section-head">
        <h2><span class="spark"></span>丹药坊 · 丹榜奇珍录</h2>
        <span class="count">共 {{ pills.length }} 味丹药</span>
      </div>
      <p class="lead">丹药品阶自一品至九品宝丹，品越高则威能愈盛。坊中丹药以丹榜名次列序，供诸位斗者采撷。</p>

      <div class="pill-grid">
        <article
          v-for="(p, i) in pills"
          :key="p.name"
          class="pill-card"
          :class="{ top: i < 3 }"
          @click="openDetail(p)"
        >
          <span class="pill-rank">丹榜 · 第 {{ cnNum[i] }}</span>
          <div class="pill-top">
            <span class="pill-no">{{ i + 1 }}</span>
            <div class="pill-title">
              <h3>{{ p.name }}</h3>
              <span v-if="p.alias" class="alias">别称 · {{ p.alias }}</span>
            </div>
            <span class="grade">{{ p.grade }}</span>
          </div>
          <span class="effect-label">功效</span>
          <p class="effect">{{ p.effect }}</p>
          <span class="effect-label">主材</span>
          <p class="material">{{ p.material || '——' }}</p>
          <p class="desc">{{ p.description }}</p>
          <button class="refine" @click.stop="refine(p.name)">入 炉 炼 制</button>
        </article>
      </div>

      <div v-if="loading" class="state">丹药坊加载中……</div>
      <div v-if="!loading && pills.length === 0" class="state">丹药坊暂无丹药</div>
    </section>

    <DetailModal :open="!!selected" :item="selected || {}" @close="selected = null" />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { showToast } from '../composables/useToast'
import DetailModal from './DetailModal.vue'

const API = 'http://localhost:8081/api/rank/pills'

const pills = ref([])
const loading = ref(true)
const selected = ref(null)
const cnNum = ['壹', '贰', '叁', '肆', '伍', '陆', '柒', '捌', '玖', '拾']

function refine (name) {
  showToast('「' + name + '」需高阶炼药师方可入炉，阁下可先寻药老相助', '✦')
}

function openDetail (p) {
  selected.value = {
    lead: '丹药',
    rank: p.rankName,
    title: p.name,
    tag: p.grade,
    tag2: p.alias || '',
    desc: p.description,
    rows: [
      { label: '功效', value: p.effect },
      { label: '主材', value: p.material }
    ]
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
        pills.value = res.data
      } else {
        showToast(res && res.msg ? res.msg : '丹药坊加载失败', '✕')
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

.pill-grid{display:grid;grid-template-columns:repeat(3,1fr);gap:16px;}
.pill-card{position:relative;border-radius:18px;padding:20px 20px 18px;display:flex;flex-direction:column;cursor:pointer;
  background:linear-gradient(165deg, rgba(24,19,38,.82), rgba(10,8,18,.9));
  border:1px solid var(--line);box-shadow:0 24px 60px -30px rgba(0,0,0,.9);
  transition:transform .25s, box-shadow .3s, border-color .3s;overflow:hidden;}
.pill-card::before,.pill-card::after{content:"";position:absolute;width:20px;height:20px;pointer-events:none;border:1.5px solid var(--rune);opacity:.5;}
.pill-card::before{top:-1px;left:-1px;border-right:0;border-bottom:0;border-radius:18px 0 0 0;}
.pill-card::after{bottom:-1px;right:-1px;border-left:0;border-top:0;border-radius:0 0 18px 0;}
.pill-card:hover{transform:translateY(-6px);border-color:var(--line-strong);box-shadow:0 34px 70px -28px var(--glow);}
.pill-card.top{border-color:var(--line-strong);box-shadow:0 30px 70px -30px var(--glow);}

.pill-rank{position:absolute;top:16px;right:16px;font-size:11px;letter-spacing:.14em;color:var(--rune);
  padding:3px 10px;border-radius:99px;border:1px solid var(--line-strong);background:rgba(255,217,138,.06);}
.pill-top{display:flex;align-items:center;gap:12px;}
.pill-no{width:40px;height:40px;flex:none;border-radius:12px;display:grid;place-items:center;font-family:var(--serif);font-size:20px;font-weight:700;color:var(--rune);
  background:rgba(255,217,138,.07);border:1px solid var(--line);}
.pill-title h3{font-family:var(--serif);font-size:19px;font-weight:700;letter-spacing:.08em;margin:0;}
.pill-title .alias{display:block;font-size:11px;color:var(--muted);margin-top:3px;letter-spacing:.06em;}
.grade{margin-left:auto;font-family:var(--serif);font-size:13px;letter-spacing:.08em;color:#140a22;padding:5px 12px;border-radius:99px;
  background:linear-gradient(140deg,var(--rune),var(--fire-1));box-shadow:0 0 16px -3px var(--glow);white-space:nowrap;}

.effect-label{font-size:10.5px;letter-spacing:.2em;color:var(--muted);margin:14px 0 4px;}
.effect{font-size:13px;line-height:1.7;color:var(--rune);margin:0;font-family:var(--serif);}
.material{font-size:12px;line-height:1.7;color:#cfc6e2;margin:0;}
.desc{font-size:12px;line-height:1.85;color:#a59dc0;margin:12px 0 16px;flex:1;}

.refine{align-self:flex-start;height:40px;padding:0 20px;border:0;border-radius:99px;cursor:pointer;font-family:var(--serif);
  font-size:13px;letter-spacing:.24em;text-indent:.24em;color:#fff;
  background:linear-gradient(135deg,var(--fire-1),var(--fire-2));box-shadow:0 10px 24px -12px var(--glow), inset 0 1px 0 rgba(255,255,255,.3);
  transition:transform .18s, box-shadow .3s;}
.refine:hover{transform:translateY(-2px);box-shadow:0 16px 34px -12px var(--glow), inset 0 1px 0 rgba(255,255,255,.4);}

.state{text-align:center;color:var(--muted);font-family:var(--serif);letter-spacing:.2em;padding:30px 0;}

@media (max-width:1024px){.pill-grid{grid-template-columns:repeat(2,1fr);}}
@media (max-width:640px){.pill-grid{grid-template-columns:1fr;}}
</style>

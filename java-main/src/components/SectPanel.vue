<template>
  <div class="rank-page sect-shop">
    <section class="section">
      <div class="section-head">
        <h2><span class="spark"></span>宗门 · 势力榜</h2>
        <span class="count">共 {{ sects.length }} 方势力</span>
      </div>
      <p class="lead">斗气大陆群雄并起，宗门林立。势力榜以底蕴与威望定名次，强者云集，霸主争锋。</p>

      <div class="sect-grid">
        <article
          v-for="(s, i) in sects"
          :key="s.name"
          class="sect-card"
          :class="{ top: i < 3 }"
          @click="openDetail(s)"
        >
          <span class="sect-rank">势力榜 · 第 {{ cnNum[i] }}</span>
          <div class="sect-top">
            <span class="sect-no">{{ i + 1 }}</span>
            <div class="sect-title">
              <h3>{{ s.name }}</h3>
              <span v-if="s.alias" class="alias">别称 · {{ s.alias }}</span>
            </div>
            <span class="grade">{{ s.grade }}</span>
          </div>
          <span v-if="s.leader" class="leader">掌权 · {{ s.leader }}</span>
          <p class="desc">{{ s.description }}</p>
        </article>
      </div>

      <div v-if="loading" class="state">宗门加载中……</div>
      <div v-if="!loading && sects.length === 0" class="state">宗门暂无势力</div>
    </section>

    <DetailModal :open="!!selected" :item="selected || {}" @close="selected = null" />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { showToast } from '../composables/useToast'
import DetailModal from './DetailModal.vue'

const API = 'http://localhost:8081/api/rank/sects'

const sects = ref([])
const loading = ref(true)
const selected = ref(null)
const cnNum = ['壹', '贰', '叁', '肆', '伍', '陆', '柒', '捌', '玖', '拾']

function openDetail (s) {
  selected.value = {
    lead: '势力',
    rank: s.rankName,
    title: s.name,
    tag: s.grade,
    tag2: s.alias || '',
    desc: s.description,
    rows: [{ label: '掌权者', value: s.leader }]
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
        sects.value = res.data
      } else {
        showToast(res && res.msg ? res.msg : '宗门加载失败', '✕')
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

.sect-grid{display:grid;grid-template-columns:repeat(3,1fr);gap:16px;}
.sect-card{position:relative;border-radius:18px;padding:20px 20px 18px;display:flex;flex-direction:column;cursor:pointer;
  background:linear-gradient(165deg, rgba(24,19,38,.82), rgba(10,8,18,.9));
  border:1px solid var(--line);box-shadow:0 24px 60px -30px rgba(0,0,0,.9);
  transition:transform .25s, box-shadow .3s, border-color .3s;overflow:hidden;}
.sect-card::before,.sect-card::after{content:"";position:absolute;width:20px;height:20px;pointer-events:none;border:1.5px solid var(--rune);opacity:.5;}
.sect-card::before{top:-1px;left:-1px;border-right:0;border-bottom:0;border-radius:18px 0 0 0;}
.sect-card::after{bottom:-1px;right:-1px;border-left:0;border-top:0;border-radius:0 0 18px 0;}
.sect-card:hover{transform:translateY(-6px);border-color:var(--line-strong);box-shadow:0 34px 70px -28px var(--glow);}
.sect-card.top{border-color:var(--line-strong);box-shadow:0 30px 70px -30px var(--glow);}

.sect-rank{position:absolute;top:16px;right:16px;font-size:11px;letter-spacing:.14em;color:var(--rune);
  padding:3px 10px;border-radius:99px;border:1px solid var(--line-strong);background:rgba(255,217,138,.06);}
.sect-top{display:flex;align-items:center;gap:12px;}
.sect-no{width:40px;height:40px;flex:none;border-radius:12px;display:grid;place-items:center;font-family:var(--serif);font-size:20px;font-weight:700;color:var(--rune);
  background:rgba(255,217,138,.07);border:1px solid var(--line);}
.sect-title h3{font-family:var(--serif);font-size:19px;font-weight:700;letter-spacing:.08em;margin:0;}
.sect-title .alias{display:block;font-size:11px;color:var(--muted);margin-top:3px;letter-spacing:.06em;}
.grade{margin-left:auto;font-family:var(--serif);font-size:13px;letter-spacing:.06em;color:#140a22;padding:5px 12px;border-radius:99px;
  background:linear-gradient(140deg,var(--rune),var(--fire-1));box-shadow:0 0 16px -3px var(--glow);white-space:nowrap;}

.leader{font-size:11.5px;letter-spacing:.08em;color:var(--cyan);margin-top:14px;}
.desc{font-size:12px;line-height:1.85;color:#a59dc0;margin:8px 0 0;flex:1;}

.state{text-align:center;color:var(--muted);font-family:var(--serif);letter-spacing:.2em;padding:30px 0;}

@media (max-width:1024px){.sect-grid{grid-template-columns:repeat(2,1fr);}}
@media (max-width:640px){.sect-grid{grid-template-columns:1fr;}}
</style>

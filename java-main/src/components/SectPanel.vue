<template>
  <div class="rank-page sect-shop">
    <!-- ===== 我的宗门 ===== -->
    <section class="mine-section">
      <div class="mine-card" :class="{ joined: !!joinedSect }">
        <div class="mine-emblem">{{ joinedSect ? joinedSect.charAt(0) : '宗' }}</div>
        <div class="mine-info">
          <span class="mine-label">{{ joinedSect ? '你所归属的宗门' : '尚无宗门' }}</span>
          <h2 v-if="joinedSect">{{ joinedSect }}</h2>
          <p v-else>斗气大陆宗门林立，择一而归，方有立足之地。</p>
          <div v-if="joinedSect" class="mine-meta">
            <span class="rank-chip">{{ currentRank.name }}</span>
            <span>贡献 {{ contribution }}</span>
            <span>{{ mySectInfo.grade || '一流' }}势力</span>
            <span>掌权 · {{ mySectInfo.leader || '—' }}</span>
            <span>同门 {{ memberCounts[joinedSect] || 0 }} 人</span>
          </div>
        </div>
        <button v-if="joinedSect" class="leave-btn" :disabled="acting" @click="onLeave">
          退出宗门
        </button>
        <span v-else class="mine-hint">↓ 从下方势力榜择一而归</span>
      </div>
    </section>

    <!-- ===== 势力榜 ===== -->
    <section class="section">
      <div class="section-head">
        <h2><span class="spark"></span>宗门 · 势力榜</h2>
        <span class="count">共 {{ sects.length }} 方势力</span>
      </div>
      <p class="lead">斗气大陆群雄并起，宗门林立。势力榜以底蕴与威望定名次，强者云集，霸主争锋。择一宗门加入，与同门共修。</p>

      <div class="sect-grid">
        <article
          v-for="(s, i) in sects"
          :key="s.name"
          class="sect-card"
          :class="{ top: i < 3, joined: s.name === joinedSect }"
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
          <div class="card-foot">
            <span class="member-badge">同门 {{ memberCounts[s.name] || 0 }} 人</span>
            <button v-if="s.name === joinedSect" class="joined-tag" disabled>已加入</button>
            <button v-else class="join-btn" :disabled="acting || !username" @click.stop="onJoin(s)">
              {{ username ? '加入宗门' : '请先登录' }}
            </button>
          </div>
        </article>
      </div>

      <div v-if="loading" class="state">宗门加载中……</div>
      <div v-if="!loading && sects.length === 0" class="state">宗门暂无势力</div>
    </section>

    <!-- ===== 宗门深度玩法：职位 / 委派 / 宝库（加入后解锁） ===== -->
    <SectGuildPanel v-if="joinedSect" :username="username" :sect-info="mySectInfo" />

    <DetailModal :open="!!selected" :item="selected || {}" @close="selected = null" />
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { showToast } from '../composables/useToast'
import DetailModal from './DetailModal.vue'
import SectGuildPanel from './SectGuildPanel.vue'
import { API_BASE } from '../api-config'
import {
  joinedSect, memberCounts, acting, loadMySect, loadMembers, joinSect, leaveSect
} from '../composables/useSect'
import { contribution, currentRank } from '../composables/useSectGuild'

const props = defineProps({
  username: { type: String, default: '' }
})

const API = API_BASE + '/api/rank/sects'

const sects = ref([])
const loading = ref(true)
const selected = ref(null)
const cnNum = ['壹', '贰', '叁', '肆', '伍', '陆', '柒', '捌', '玖', '拾']

// 当前所属宗门的详情（来自势力榜）
const mySectInfo = computed(() => sects.value.find(s => s.name === joinedSect.value) || {})

function openDetail (s) {
  selected.value = {
    lead: '势力',
    rank: s.rankName,
    title: s.name,
    tag: s.grade,
    tag2: s.alias || '',
    desc: s.description,
    rows: [
      { label: '掌权者', value: s.leader },
      { label: '同门人数', value: (memberCounts.value[s.name] || 0) + ' 人' }
    ]
  }
}

async function onJoin (s) {
  if (!props.username) {
    showToast('请先登录再加入宗门', '✕')
    return
  }
  await joinSect(props.username, s.name)
}

async function onLeave () {
  await leaveSect(props.username)
}

onMounted(() => {
  load()
  loadMembers()
  loadMySect(props.username)
})

// 登录用户变化时（如重新登录），重新读取所属宗门
watch(() => props.username, (u) => { loadMySect(u) })

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
      showToast(`后端服务未启动（${API_BASE}）`, '✕')
    })
}
</script>

<style scoped>
.rank-page{position:relative;z-index:2;}
.lead{font-family:var(--serif);font-size:14px;letter-spacing:.14em;color:#b9b0cc;margin:-6px 0 26px;text-shadow:0 2px 14px rgba(0,0,0,.7);}
.count{font-size:12px;color:var(--muted);letter-spacing:.1em;}

/* ===== 我的宗门 ===== */
.mine-section{margin-bottom:26px;}
.mine-card{position:relative;display:flex;align-items:center;gap:20px;padding:22px 26px;border-radius:18px;overflow:hidden;
  background:linear-gradient(120deg, rgba(24,19,38,.85), rgba(10,8,18,.92));
  border:1px dashed var(--line-strong);box-shadow:0 24px 60px -32px rgba(0,0,0,.9);}
.mine-card.joined{border-style:solid;border-color:var(--rune);box-shadow:0 24px 70px -28px var(--glow);}
.mine-card.joined::after{content:"";position:absolute;inset:0;pointer-events:none;
  background:radial-gradient(circle at 12% 50%, rgba(255,217,138,.12), transparent 45%);}
.mine-emblem{width:64px;height:64px;flex:none;display:grid;place-items:center;border-radius:16px;
  font-family:var(--serif);font-size:30px;font-weight:700;color:var(--rune);
  background:rgba(255,217,138,.08);border:1px solid var(--line-strong);}
.mine-card.joined .mine-emblem{background:linear-gradient(140deg,var(--rune),var(--fire-1));color:#140a22;
  box-shadow:0 0 24px -4px var(--glow);}
.mine-info{flex:1;min-width:0;}
.mine-label{font-size:11px;letter-spacing:.18em;color:var(--muted);}
.mine-info h2{font-family:var(--serif);font-size:24px;letter-spacing:.12em;margin:4px 0 0;color:var(--rune);}
.mine-info p{font-family:var(--serif);font-size:13px;color:#a59dc0;margin:8px 0 0;letter-spacing:.08em;}
.mine-meta{display:flex;flex-wrap:wrap;gap:10px;margin-top:10px;}
.mine-meta span{font-size:11px;letter-spacing:.08em;color:var(--cyan);padding:3px 10px;border-radius:99px;
  border:1px solid var(--line);background:rgba(157,243,255,.05);}
.mine-meta .rank-chip{color:var(--rune);border-color:var(--line-strong);background:rgba(255,217,138,.08);font-family:var(--serif);}
.mine-hint{font-size:12px;letter-spacing:.1em;color:var(--muted);font-family:var(--serif);}
.leave-btn{flex:none;padding:9px 18px;border-radius:10px;cursor:pointer;font-size:12px;letter-spacing:.1em;
  font-family:var(--serif);color:#e8c9c9;background:rgba(120,20,30,.18);border:1px solid rgba(200,90,90,.45);
  transition:all .2s;}
.leave-btn:hover:not(:disabled){background:rgba(160,30,40,.3);border-color:rgba(230,110,110,.7);}
.leave-btn:disabled{opacity:.5;cursor:not-allowed;}

/* ===== 势力榜 ===== */
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
.sect-card.joined{border-color:var(--rune);}

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

.card-foot{display:flex;align-items:center;justify-content:space-between;gap:10px;margin-top:16px;
  padding-top:14px;border-top:1px solid var(--line);}
.member-badge{font-size:11px;letter-spacing:.08em;color:var(--muted);}
.join-btn{flex:none;padding:7px 16px;border-radius:9px;cursor:pointer;font-size:12px;letter-spacing:.1em;
  font-family:var(--serif);color:#140a22;background:linear-gradient(140deg,var(--rune),var(--fire-1));
  border:0;box-shadow:0 0 16px -4px var(--glow);transition:transform .2s, filter .2s;}
.join-btn:hover:not(:disabled){transform:translateY(-2px);filter:brightness(1.1);}
.join-btn:disabled{opacity:.5;cursor:not-allowed;}
.joined-tag{flex:none;padding:7px 14px;border-radius:9px;font-size:12px;letter-spacing:.1em;font-family:var(--serif);
  color:var(--rune);background:rgba(255,217,138,.08);border:1px solid var(--line-strong);cursor:default;}

.state{text-align:center;color:var(--muted);font-family:var(--serif);letter-spacing:.2em;padding:30px 0;}

@media (max-width:1024px){.sect-grid{grid-template-columns:repeat(2,1fr);}}
@media (max-width:640px){.sect-grid{grid-template-columns:1fr;}.mine-card{flex-wrap:wrap;}.leave-btn{width:100%;}}
</style>

<template>
  <div class="rank-page fire-rank">
    <section class="section">
      <div class="section-head">
        <h2><span class="spark"></span>异火榜 · 天地奇火录</h2>
        <span class="count">共 {{ fires.length }} 道异火</span>
      </div>
      <p class="lead">异火者，天地间孕育之灵焰也。榜上以名次为序，榜一最尊，可焚天煮海。轻触异火，可观其详。</p>

      <ol class="rank-list">
        <li
          v-for="(f, i) in fires"
          :key="f.name"
          class="rank-item"
          :class="{ top: i < 3 }"
          @click="openDetail(f)"
        >
          <div class="rank-no">
            <svg class="flame" viewBox="0 0 24 24" fill="none" stroke="var(--rune)" stroke-width="1.4"><path d="M12 3c1 3-2 4-2 7a3 3 0 0 0 6 0c0-1-.5-2-1-2.5C16 9 18 12 18 15a6 6 0 1 1-12 0c0-4 3-6 4-8 .6-1 1.4-2.2 2-4Z"/></svg>
            <b>{{ i + 1 }}</b>
          </div>
          <div class="r-card">
            <div class="rb-media">
              <img class="rb-img" :src="f.image" :alt="f.name" loading="lazy"
                   @error="onImgError" />
              <span class="rb-rank floating">{{ f.rankName }}</span>
            </div>
            <div class="rb-head">
              <h3>{{ f.name }}</h3>
              <span class="lv">{{ f.level }}</span>
            </div>
            <p class="rb-desc">{{ f.description }}</p>
          </div>
        </li>
      </ol>

      <div v-if="loading" class="state">异火榜加载中……</div>
      <div v-if="!loading && fires.length === 0" class="state">异火榜暂无记录</div>
    </section>

    <DetailModal :open="!!selected" :item="selected || {}" @close="selected = null" />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { showToast } from '../composables/useToast'
import DetailModal from './DetailModal.vue'

// 后端排行榜接口（与 springBootTest 端口 8081 保持一致）
const API = 'http://localhost:8081/api/rank/fires'

const fires = ref([])
const loading = ref(true)
const selected = ref(null)

function openDetail (f) {
  selected.value = {
    lead: '异火',
    rank: f.rankName,
    title: f.name,
    tag: f.level,
    desc: f.description,
    img: f.image,
    rows: [{ label: '关键词', value: f.keywords }]
  }
}

// 图片加载失败时的兜底：隐藏破图，避免影响版面
function onImgError (e) {
  const el = e && e.target
  if (el) el.style.visibility = 'hidden'
}

onMounted(load)

function load () {
  loading.value = true
  fetch(API)
    .then(r => r.json())
    .then(res => {
      loading.value = false
      if (res && res.code === 0 && res.data) {
        fires.value = res.data
      } else {
        showToast(res && res.msg ? res.msg : '异火榜加载失败', '✕')
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

/* 左侧名次徽章 */
.rank-no{
  position:relative;flex:none;width:64px;height:64px;border-radius:16px;display:grid;place-items:center;
  background:linear-gradient(160deg, rgba(255,217,138,.12), rgba(169,123,255,.16));
  border:1px solid var(--line);
  box-shadow:0 0 24px -8px var(--glow);
}
.rank-no .flame{position:absolute;inset:0;width:100%;height:100%;opacity:.16;filter:drop-shadow(0 0 10px var(--glow));}
.rank-no b{
  position:relative;font-family:var(--serif);font-size:30px;font-weight:700;line-height:1;
  background:linear-gradient(178deg,#fff8e8,var(--rune) 55%,var(--fire-1));
  -webkit-background-clip:text;background-clip:text;color:transparent;
  filter:drop-shadow(0 0 12px var(--glow-soft));
}
.rank-item.top .rank-no{
  border-color:var(--line-strong);
  background:linear-gradient(160deg, rgba(255,217,138,.22), rgba(169,123,255,.28));
  box-shadow:0 0 30px -6px var(--glow);
}

/* 异火配图 */
.rb-media{position:relative;margin:-16px -20px 14px;border-radius:16px 16px 0 0;overflow:hidden;height:150px;background:radial-gradient(120% 120% at 50% 0%,rgba(169,123,255,.18),rgba(10,8,18,.9));}
.rb-img{width:100%;height:100%;object-fit:cover;display:block;filter:saturate(1.05) contrast(1.02);}
.rb-media::after{content:"";position:absolute;inset:0;background:linear-gradient(180deg,transparent 40%,rgba(10,8,18,.92));pointer-events:none;}
.rb-rank.floating{position:absolute;left:14px;bottom:12px;z-index:2;font-size:11.5px;letter-spacing:.14em;color:var(--rune);padding:3px 10px;border-radius:99px;border:1px solid var(--line-strong);background:rgba(10,8,18,.6);backdrop-filter:blur(4px);}

/* 右侧卡片 */
.r-card{position:relative;flex:1;border-radius:16px;padding:16px 20px;
  background:linear-gradient(165deg, rgba(24,19,38,.82), rgba(10,8,18,.9));
  border:1px solid var(--line);
  box-shadow:0 24px 60px -30px rgba(0,0,0,.9);
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

.state{text-align:center;color:var(--muted);font-family:var(--serif);letter-spacing:.2em;padding:30px 0;}
</style>

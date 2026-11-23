<template>
  <div class="guild-panel">
    <!-- ===== 职位与贡献 ===== -->
    <section class="rank-card">
      <div class="rank-emblem">{{ currentRank.name.charAt(0) }}</div>
      <div class="rank-main">
        <div class="rank-line">
          <span class="rank-title">{{ currentRank.name }}</span>
          <span class="rank-contrib">宗门贡献 <b>{{ contribution }}</b></span>
        </div>
        <p class="rank-desc">{{ currentRank.desc }}</p>
        <div class="rank-bar"><div class="rank-fill" :style="{ width: (rankProgress * 100) + '%' }"></div></div>
        <div class="rank-next">
          <span v-if="nextRank">再得 {{ Math.max(0, nextRank.min - contribution) }} 贡献 · 晋升「{{ nextRank.name }}」</span>
          <span v-else>已至宗门之巅</span>
          <span class="bonus">总增益 ×{{ contributionBonus.toFixed(2) }}</span>
        </div>
      </div>
    </section>

    <!-- ===== 每日委派 ===== -->
    <section class="section">
      <div class="section-head">
        <h2><span class="spark"></span>宗门委派</h2>
        <span class="count">每日重置 · {{ realmLabel }}</span>
      </div>
      <div class="task-grid">
        <article
          v-for="t in TASKS"
          :key="t.id"
          class="task-card"
          :class="{ locked: currentRankIndex < (t.rankMin || 0), done: taskUsed(t.id) >= t.limit }"
        >
          <span class="task-icon">{{ t.icon }}</span>
          <div class="task-body">
            <h4>{{ t.name }} <span class="task-limit">{{ taskUsed(t.id) }}/{{ t.limit }}</span></h4>
            <p>{{ t.desc }}</p>
            <div class="task-rewards">
              <span>贡献 +{{ taskReward(t).contrib }}</span>
              <span v-if="t.qi">斗气 +{{ taskReward(t).qi }}</span>
              <span v-if="t.cost" class="cost">耗气 {{ t.cost }}</span>
            </div>
          </div>
          <button class="task-btn" :disabled="!canDoTask(t)" @click="onTask(t)">
            {{ currentRankIndex < (t.rankMin || 0) ? '未解锁' : (taskUsed(t.id) >= t.limit ? '已完成' : '领命') }}
          </button>
        </article>
      </div>
    </section>

    <!-- ===== 宗门宝库 ===== -->
    <section class="section">
      <div class="section-head">
        <h2><span class="spark"></span>宗门宝库</h2>
        <span class="count">功法参悟 {{ gongfaStack }}/5 层</span>
      </div>
      <div class="shop-grid">
        <article v-for="item in SHOP" :key="item.id" class="shop-card">
          <span class="shop-icon">{{ item.icon }}</span>
          <h4>{{ item.name }}</h4>
          <p>{{ item.desc }}</p>
          <button
            class="shop-btn"
            :disabled="contribution < item.cost || (item.effect.gongfa && gongfaStack >= item.maxStack)"
            @click="onBuy(item)"
          >
            {{ item.cost }} 贡献 · 兑换
          </button>
        </article>
      </div>
    </section>
  </div>
</template>

<script setup>
import { watch } from 'vue'
import { showToast } from '../composables/useToast'
import { realmLabel } from '../composables/useCultivation'
import {
  RANKS, TASKS, SHOP, contribution, currentRank, currentRankIndex, nextRank,
  rankProgress, contributionBonus, gongfaStack, taskUsed, taskReward,
  canDoTask, completeTask, buyItem, loadGuild
} from '../composables/useSectGuild'

const props = defineProps({
  username: { type: String, default: '' },
  sectInfo: { type: Object, default: () => ({}) }
})

function onTask (task) {
  const res = completeTask(task)
  if (!res.ok) {
    showToast(res.msg, '✕')
    return
  }
  const extra = res.reward.qi ? '，斗气 +' + res.reward.qi : ''
  showToast('完成「' + task.name + '」· 贡献 +' + res.reward.contrib + extra, '✦')
}

function onBuy (item) {
  const res = buyItem(item)
  if (!res.ok) {
    showToast(res.msg, '✕')
    return
  }
  showToast('兑换「' + item.name + '」· ' + res.detail, '✦')
}

function reload () {
  loadGuild(props.username, props.sectInfo && props.sectInfo.grade)
}

watch(() => [props.username, props.sectInfo && props.sectInfo.grade], reload, { immediate: true })
</script>

<style scoped>
.guild-panel{margin-top:6px;}
.section{margin-top:30px;}

/* ===== 职位卡 ===== */
.rank-card{position:relative;display:flex;align-items:center;gap:20px;padding:22px 26px;border-radius:18px;overflow:hidden;
  background:linear-gradient(120deg, rgba(30,24,48,.9), rgba(12,9,22,.94));
  border:1px solid var(--line-strong);box-shadow:0 24px 70px -30px var(--glow);}
.rank-card::after{content:"";position:absolute;inset:0;pointer-events:none;
  background:radial-gradient(circle at 8% 50%, rgba(255,217,138,.16), transparent 42%);}
.rank-emblem{width:64px;height:64px;flex:none;display:grid;place-items:center;border-radius:16px;position:relative;z-index:1;
  font-family:var(--serif);font-size:30px;font-weight:700;color:#140a22;
  background:linear-gradient(140deg,var(--rune),var(--fire-1));box-shadow:0 0 26px -4px var(--glow);}
.rank-main{flex:1;min-width:0;position:relative;z-index:1;}
.rank-line{display:flex;align-items:baseline;justify-content:space-between;gap:12px;}
.rank-title{font-family:var(--serif);font-size:24px;font-weight:700;letter-spacing:.12em;color:var(--rune);}
.rank-contrib{font-size:12px;letter-spacing:.1em;color:var(--muted);}
.rank-contrib b{font-family:var(--serif);font-size:22px;color:var(--rune);margin-left:6px;}
.rank-desc{font-family:var(--serif);font-size:12px;color:#a59dc0;margin:6px 0 12px;letter-spacing:.08em;}
.rank-bar{height:7px;border-radius:99px;overflow:hidden;background:rgba(255,255,255,.06);border:1px solid var(--line);}
.rank-fill{height:100%;border-radius:99px;background:linear-gradient(90deg,var(--rune),var(--fire-1));box-shadow:0 0 14px -2px var(--glow);transition:width .4s;}
.rank-next{display:flex;justify-content:space-between;gap:12px;margin-top:9px;font-size:11px;letter-spacing:.06em;color:var(--muted);}
.rank-next .bonus{color:var(--cyan);}

/* ===== 委派任务 ===== */
.task-grid{display:grid;grid-template-columns:repeat(2,1fr);gap:14px;}
.task-card{position:relative;display:flex;align-items:center;gap:14px;padding:16px 16px;border-radius:14px;
  background:linear-gradient(160deg, rgba(24,19,38,.82), rgba(10,8,18,.9));
  border:1px solid var(--line);box-shadow:0 20px 50px -32px rgba(0,0,0,.9);transition:border-color .25s, transform .25s;}
.task-card:hover{border-color:var(--line-strong);transform:translateY(-3px);}
.task-card.locked{opacity:.5;}
.task-card.done{opacity:.62;}
.task-icon{width:44px;height:44px;flex:none;display:grid;place-items:center;border-radius:12px;
  font-family:var(--serif);font-size:20px;color:var(--rune);background:rgba(255,217,138,.07);border:1px solid var(--line);}
.task-body{flex:1;min-width:0;}
.task-body h4{font-family:var(--serif);font-size:15px;letter-spacing:.08em;margin:0;display:flex;align-items:center;gap:8px;}
.task-limit{font-family:system-ui,sans-serif;font-size:10px;letter-spacing:0;color:var(--muted);
  padding:2px 7px;border-radius:99px;border:1px solid var(--line);}
.task-body p{font-size:11px;line-height:1.6;color:#a59dc0;margin:5px 0 7px;}
.task-rewards{display:flex;flex-wrap:wrap;gap:8px;font-size:10.5px;letter-spacing:.04em;}
.task-rewards span{padding:2px 8px;border-radius:99px;color:var(--cyan);background:rgba(157,243,255,.05);border:1px solid var(--line);}
.task-rewards .cost{color:#e8a0a0;background:rgba(200,80,80,.07);}
.task-btn{flex:none;padding:8px 15px;border-radius:9px;cursor:pointer;font-family:var(--serif);font-size:12px;letter-spacing:.1em;
  color:#140a22;border:0;background:linear-gradient(140deg,var(--rune),var(--fire-1));box-shadow:0 0 14px -4px var(--glow);
  transition:transform .2s, filter .2s;}
.task-btn:hover:not(:disabled){transform:translateY(-2px);filter:brightness(1.1);}
.task-btn:disabled{cursor:not-allowed;opacity:.45;filter:grayscale(.4);}

/* ===== 宗门宝库 ===== */
.shop-grid{display:grid;grid-template-columns:repeat(4,1fr);gap:14px;}
.shop-card{position:relative;display:flex;flex-direction:column;align-items:center;text-align:center;padding:20px 14px;border-radius:14px;
  background:linear-gradient(165deg, rgba(24,19,38,.82), rgba(10,8,18,.9));
  border:1px solid var(--line);box-shadow:0 20px 50px -32px rgba(0,0,0,.9);transition:transform .25s, border-color .25s;}
.shop-card:hover{transform:translateY(-5px);border-color:var(--rune);}
.shop-icon{width:48px;height:48px;display:grid;place-items:center;border-radius:13px;
  font-family:var(--serif);font-size:22px;color:#140a22;background:linear-gradient(140deg,var(--rune),var(--fire-1));box-shadow:0 0 18px -4px var(--glow);}
.shop-card h4{font-family:var(--serif);font-size:15px;letter-spacing:.08em;margin:12px 0 0;}
.shop-card p{font-size:11px;line-height:1.7;color:#a59dc0;margin:8px 0 14px;flex:1;}
.shop-btn{width:100%;padding:8px 0;border-radius:9px;cursor:pointer;font-family:var(--serif);font-size:11.5px;letter-spacing:.06em;
  color:#140a22;border:0;background:linear-gradient(140deg,var(--rune),var(--fire-1));transition:filter .2s;}
.shop-btn:hover:not(:disabled){filter:brightness(1.1);}
.shop-btn:disabled{opacity:.45;cursor:not-allowed;filter:grayscale(.4);}

@media (max-width:1024px){.shop-grid{grid-template-columns:repeat(2,1fr);}}
@media (max-width:760px){.task-grid{grid-template-columns:1fr;}.shop-grid{grid-template-columns:1fr 1fr;}
  .rank-card{flex-wrap:wrap;}.rank-contrib b{font-size:18px;}}
</style>

<template>
  <div class="rank-page dice-page">
    <section class="section">
      <!-- 胜者烟花大图：与小丑对称展示 -->
      <transition name="clown-pop">
        <div v-if="showFireworks" :key="'fw' + verdictSeq" class="clown-layer fireworks-layer" aria-hidden="true">
          <img src="/images/fireworks.png" alt="胜者烟花" />
          <span class="clown-word fw-word">胜</span>
        </div>
      </transition>
      <!-- 败者小丑 -->
      <transition name="clown-pop">
        <div v-if="showClown" :key="'clown' + verdictSeq" class="clown-layer" aria-hidden="true">
          <img src="/images/clown.png" alt="败者小丑" />
          <span class="clown-word">败</span>
        </div>
      </transition>
      <div class="section-head">
        <h2><span class="spark"></span>斗气骰坊 · 以气搏运</h2>
        <button class="sfx-toggle" :class="{ off: !sfxEnabled }" @click="onToggleSfx" :title="sfxEnabled ? '点击关闭音效' : '点击开启音效'">
          <svg viewBox="0 0 24 24" v-html="sfxEnabled ? sfxOnIcon : sfxOffIcon"></svg>
          <span>音效 {{ sfxEnabled ? '开' : '关' }}</span>
        </button>
      </div>
      <p class="lead">掷两枚骰子与庄家比点，点数之和大者胜。连胜可得异火加持，赢得越多，彩头越厚 —— 斗气大陆，运气也是实力。</p>

      <!-- 斗气 / 连胜 状态条 -->
      <div class="dice-stats">
        <div class="dstat">
          <span class="dl">斗气存量</span>
          <b class="dv qi">{{ qi }}</b>
        </div>
        <div class="dstat">
          <span class="dl">当前连胜</span>
          <b class="dv">{{ streak }} <i>连</i></b>
        </div>
        <div class="dstat">
          <span class="dl">最高连胜</span>
          <b class="dv">{{ bestStreak }} <i>连</i></b>
        </div>
        <div class="dstat">
          <span class="dl">本局彩头</span>
          <b class="dv mult">×{{ multiplier.toFixed(1) }}</b>
        </div>
      </div>

      <!-- 每日补给 -->
      <div class="daily-supply" :class="{ claimed: claimedToday }">
        <span class="ds-ic">✦</span>
        <div class="ds-txt">
          <b>每日补给</b>
          <small>{{ claimedToday ? '今日补给已领取，明日再来' : '每日可领一次，助你再战斗气大陆' }}</small>
        </div>
        <button class="claim-btn" :disabled="claimedToday" @click="claimDaily">
          {{ claimedToday ? '已领取' : '领 ' + DAILY_AMOUNT + ' 斗气' }}
        </button>
      </div>

      <!-- 赌桌 -->
      <div class="arena">
        <!-- 庄家 -->
        <div class="seat dealer" :class="{ 'seat-winner': !rolling && settled && winner === 'dealer' }">
          <div class="seat-head">
            <span class="av">{{ dealer.charAt(0) }}</span>
            <div><b>{{ dealer }}</b><small>庄家</small></div>
          </div>
          <div class="dice-row">
            <div
              v-for="(v, i) in dealerDice"
              :key="'d' + i"
              class="die"
              :class="{ rolling: rolling, 'die-win': !rolling && settled && winner === 'dealer' }"
            >
              <span class="spark-ring" v-if="!rolling && settled && winner === 'dealer'"></span>
              <span v-for="p in 9" :key="p" class="pip" :class="{ on: pipMap[v].includes(p - 1) }"></span>
            </div>
          </div>
          <div class="sum">共 <b>{{ dealerSum }}</b> 点</div>
        </div>

        <!-- 中间 VS / 赛果 -->
        <div class="mid">
          <div class="versus">斗</div>
          <div v-if="settled" :key="verdictSeq" class="verdict" :class="verdictType">{{ verdictText }}</div>
          <div v-else class="verdict-hint">{{ rolling ? '骰影翻飞…' : '落注开局' }}</div>
          <!-- 胜者光刃：从中央射向胜方 -->
          <span v-if="!rolling && settled && winner" :key="'beam' + verdictSeq" class="win-beam" :class="winner"></span>
        </div>

        <!-- 玩家 -->
        <div class="seat player" :class="{ 'seat-winner': !rolling && settled && winner === 'player' }">
          <div class="seat-head">
            <span class="av">{{ player.charAt(0) }}</span>
            <div><b>{{ player }}</b><small>斗者</small></div>
          </div>
          <div class="dice-row">
            <div
              v-for="(v, i) in playerDice"
              :key="'p' + i"
              class="die"
              :class="{ rolling: rolling, 'die-win': !rolling && settled && winner === 'player' }"
            >
              <span class="spark-ring" v-if="!rolling && settled && winner === 'player'"></span>
              <span v-for="p in 9" :key="p" class="pip" :class="{ on: pipMap[v].includes(p - 1) }"></span>
            </div>
          </div>
          <div class="sum">共 <b>{{ playerSum }}</b> 点</div>
          <span class="crown" v-if="!rolling && settled && winner === 'player'">冠</span>
        </div>
      </div>

      <!-- 押注与控制 -->
      <div class="table-control">
        <div class="bet-area">
          <span class="bet-label">落注</span>
          <button
            v-for="opt in betOptions"
            :key="opt"
            class="chip"
            :class="{ on: bet === opt }"
            :disabled="rolling || qi <= 0"
            @click="pickBet(opt)"
          >{{ opt }}</button>
          <button
            class="chip all"
            :class="{ on: bet === 'all' }"
            :disabled="rolling || qi <= 0"
            @click="pickBet('all')"
          >全押</button>
        </div>
        <div class="action-area">
          <button v-if="qi <= 0" class="roll-btn refill" @click="refill">重修斗气</button>
          <button v-else class="roll-btn" :disabled="rolling" @click="roll">
            {{ rolling ? '掷骰中…' : '掷 骰' }}
          </button>
          <button class="reset-btn" title="重置全部战绩" @click="resetAll">重置</button>
        </div>
      </div>
      <div class="bet-note">本局押注 <b>{{ stake }}</b> 斗气 · 胜可得 <b>{{ potentialGain }}</b> 斗气（含连胜加持）</div>

      <!-- 战绩记录 -->
      <div class="history" v-if="history.length">
        <div class="section-head" style="margin-bottom:10px;"><h2 style="font-size:16px;">近局战绩</h2></div>
        <div class="history-list">
          <div v-for="(h, i) in history" :key="i" class="hrow" :class="h.type">
            <span class="hicon">{{ h.type === 'win' ? '胜' : h.type === 'lose' ? '负' : '和' }}</span>
            <span class="hdetail">你 {{ h.p }} 点 · {{ dealerOf(h) }} {{ h.d }} 点</span>
            <span class="hgain">{{ h.gain > 0 ? '+' : '' }}{{ h.gain }} 斗气</span>
          </div>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup>
import { ref, computed, onBeforeUnmount } from 'vue'
import { showToast } from '../composables/useToast'
import {
  sfxEnabled, toggleSfx, playRoll, playWin, playLose, playDraw, playClaim, playClick
} from '../composables/useSfx'
import { qi as sharedQi, realmBonus, persist as persistShared } from '../composables/useCultivation'

const props = defineProps({
  username: { type: String, default: '' }
})

const player = computed(() => props.username || '萧炎')
const SAVE_KEY = 'doupo_dice_save_v1'
const DAILY_KEY = 'doupo_dice_daily_v1'
const DAILY_AMOUNT = 50

const sfxOnIcon = '<path d="M4 9v6h4l5 4V5L8 9H4Z"/><path d="M16 9a4 4 0 0 1 0 6M18.5 6.5a7.5 7.5 0 0 1 0 11"/>'
const sfxOffIcon = '<path d="M4 9v6h4l5 4V5L8 9H4Z"/><path d="m17 9 5 6M22 9l-5 6"/>'

function onToggleSfx () {
  toggleSfx()
  if (sfxEnabled.value) playClick()
}

// 本地日期 YYYY-MM-DD（按用户所在时区）
function todayKey () {
  const d = new Date()
  const m = String(d.getMonth() + 1).padStart(2, '0')
  const day = String(d.getDate()).padStart(2, '0')
  return d.getFullYear() + '-' + m + '-' + day
}
function getDailyRecord () {
  try { return localStorage.getItem(DAILY_KEY) || '' } catch (e) { return '' }
}
const lastClaimDate = ref(getDailyRecord())
const claimedToday = computed(() => lastClaimDate.value === todayKey())

// 骰子点数 -> 九宫格亮起的格子索引（0~8）
const pipMap = {
  1: [4],
  2: [0, 8],
  3: [0, 4, 8],
  4: [0, 2, 6, 8],
  5: [0, 2, 4, 6, 8],
  6: [0, 2, 3, 5, 6, 8]
}

const dealers = ['魂殿尊老', '药老', '云岚宗主', '美杜莎', '纳兰嫣然', '萧厉', '铁剑老者']

const betOptions = [10, 30, 50]

function loadSave () {
  const fallback = { qi: 100, bestStreak: 0, wins: 0, games: 0, history: [] }
  try {
    const raw = localStorage.getItem(SAVE_KEY)
    if (!raw) return fallback
    const d = JSON.parse(raw)
    return {
      qi: Math.max(0, Number(d.qi) || 0),
      bestStreak: Number(d.bestStreak) || 0,
      wins: Number(d.wins) || 0,
      games: Number(d.games) || 0,
      history: Array.isArray(d.history) ? d.history.slice(0, 8) : []
    }
  } catch (e) {
    return fallback
  }
}

const saved = loadSave()
// 斗气改为共享状态：与修炼阁打坐互通（原本地存档斗气已在 useCultivation 中迁移）
const qi = sharedQi
const bestStreak = ref(saved.bestStreak)
const wins = ref(saved.wins)
const games = ref(saved.games)
const history = ref(saved.history)

const streak = ref(0)
const bet = ref(10)
const rolling = ref(false)
const settled = ref(false)
const verdictType = ref('')
const verdictText = ref('')
const verdictSeq = ref(0) // 每次结算自增，用于重放赛果/光刃动画
const winner = ref('') // 本局胜方：'player' | 'dealer' | ''
const dealer = ref(dealers[Math.floor(Math.random() * dealers.length)])

// ===== 结算特效：胜者烟花图 / 败者小丑图 =====
const showFireworks = ref(false) // 胜者烟花大图
const showClown = ref(false) // 败者小丑
let fxTimer = null

function launchCelebration (isWin) {
  clearTimeout(fxTimer)
  showClown.value = false
  showFireworks.value = false
  if (isWin) {
    // 玩家胜：全屏烟花大图，和小丑对称展示
    showFireworks.value = true
    fxTimer = setTimeout(() => { showFireworks.value = false }, 3200)
  } else {
    // 玩家败：展示小丑，持续 3.2s
    showClown.value = true
    fxTimer = setTimeout(() => { showClown.value = false }, 3200)
  }
}

onBeforeUnmount(() => clearTimeout(fxTimer))

const playerDice = ref([3, 5])
const dealerDice = ref([2, 4])

const playerSum = computed(() => playerDice.value[0] + playerDice.value[1])
const dealerSum = computed(() => dealerDice.value[0] + dealerDice.value[1])

const stake = computed(() => {
  if (qi.value <= 0) return 0
  return bet.value === 'all' ? qi.value : Math.min(Number(bet.value), qi.value)
})

// 彩头倍率 = 连胜倍率 × 境界加成（修为越高，赌运越盛）
const multiplier = computed(() => streakMultiplier(streak.value) * realmBonus.value)
function streakMultiplier (s) {
  if (s >= 4) return 3
  if (s === 3) return 2
  if (s === 2) return 1.5
  if (s === 1) return 1.2
  return 1
}
const potentialGain = computed(() => Math.round(stake.value * multiplier.value))

function randDie () { return 1 + Math.floor(Math.random() * 6) }

function pickBet (opt) {
  if (bet.value !== opt) playClick()
  bet.value = opt
}

function claimDaily () {
  if (claimedToday.value) return
  qi.value += DAILY_AMOUNT
  lastClaimDate.value = todayKey()
  try { localStorage.setItem(DAILY_KEY, lastClaimDate.value) } catch (e) {}
  playClaim()
  persist()
  showToast('领取每日补给，斗气 +' + DAILY_AMOUNT, '✦')
}

function persist () {
  persistShared()
  try {
    localStorage.setItem(SAVE_KEY, JSON.stringify({
      bestStreak: bestStreak.value,
      wins: wins.value,
      games: games.value,
      history: history.value
    }))
  } catch (e) {
    showToast('本地存档失败，本次战绩可能不会保留', '✕')
  }
}

function roll () {
  if (rolling.value || qi.value <= 0) return
  playRoll()
  rolling.value = true
  settled.value = false
  verdictText.value = ''
  winner.value = ''
  clearTimeout(fxTimer)
  showFireworks.value = false
  showClown.value = false
  dealer.value = dealers[Math.floor(Math.random() * dealers.length)]

  const finalP = [randDie(), randDie()]
  const finalD = [randDie(), randDie()]
  let ticks = 0
  const timer = setInterval(() => {
    playerDice.value = [randDie(), randDie()]
    dealerDice.value = [randDie(), randDie()]
    ticks++
    if (ticks >= 12) {
      clearInterval(timer)
      playerDice.value = finalP
      dealerDice.value = finalD
      settle(finalP[0] + finalP[1], finalD[0] + finalD[1])
    }
  }, 70)
}

function settle (p, d) {
  rolling.value = false
  settled.value = true
  verdictSeq.value++
  games.value++

  let gain = 0
  let type = 'draw'
  if (p > d) {
    gain = potentialGain.value
    qi.value += gain
    wins.value++
    streak.value++
    if (streak.value > bestStreak.value) bestStreak.value = streak.value
    type = 'win'
    winner.value = 'player'
    verdictType.value = 'win'
    verdictText.value = '胜 · 得 ' + gain + ' 斗气'
    playWin()
    launchCelebration(true) // 胜者放烟花
    showToast('骰运昌隆，胜！获得 ' + gain + ' 斗气', '✦')
  } else if (p < d) {
    gain = -stake.value
    qi.value = Math.max(0, qi.value - stake.value)
    streak.value = 0
    type = 'lose'
    winner.value = 'dealer'
    verdictType.value = 'lose'
    verdictText.value = '负 · 失 ' + stake.value + ' 斗气'
    playLose()
    launchCelebration(false) // 败者放小丑
    showToast('此局失利，损失 ' + stake.value + ' 斗气', '✕')
  } else {
    verdictType.value = 'draw'
    verdictText.value = '和 · 退还押注'
    playDraw()
    clearTimeout(fxTimer)
    showFireworks.value = false
    showClown.value = false
    showToast('平局，斗气如数退还', '✦')
  }

  history.value.unshift({ type, p, d, gain, dealer: dealer.value })
  if (history.value.length > 8) history.value = history.value.slice(0, 8)
  persist()
}

function dealerOf (h) { return h.dealer || '庄家' }

function refill () {
  qi.value = 100
  streak.value = 0
  settled.value = false
  winner.value = ''
  verdictText.value = ''
  clearTimeout(fxTimer)
  showFireworks.value = false
  showClown.value = false
  playClaim()
  persist()
  showToast('斗气重修圆满，再战一局！', '✦')
}

function resetAll () {
  playClick()
  qi.value = 100
  bestStreak.value = 0
  wins.value = 0
  games.value = 0
  streak.value = 0
  history.value = []
  settled.value = false
  winner.value = ''
  verdictText.value = ''
  clearTimeout(fxTimer)
  showFireworks.value = false
  showClown.value = false
  persist()
  showToast('战绩已重置', '✦')
}
</script>

<style scoped>
.dice-page{position:relative;z-index:2;}
.section{position:relative;}
.lead{font-family:var(--serif);font-size:14px;letter-spacing:.12em;color:#b9b0cc;margin:-6px 0 22px;text-shadow:0 2px 14px rgba(0,0,0,.7);}

/* ===== 胜者烟花大图：与小丑对称 ===== */
.fireworks-layer img{width:min(560px,92vw);filter:drop-shadow(0 0 40px rgba(255,190,80,.55)) saturate(1.15);
  animation:fw-bloom 3.2s ease-out both;}
@keyframes fw-bloom{0%{opacity:0;transform:translate(-50%,-50%) rotate(-6deg) scale(.55)}
  22%{opacity:1;transform:translate(-50%,-50%) rotate(0) scale(1.06)}
  40%{transform:translate(-50%,-50%) rotate(2deg) scale(1)}
  85%{opacity:1}
  100%{opacity:0;transform:translate(-50%,-50%) scale(1.05)}}
.fw-word{color:var(--rune);text-shadow:0 0 34px var(--glow), 0 0 14px var(--rune), 0 6px 20px rgba(0,0,0,.9);
  animation:crown-drop .55s cubic-bezier(.2,1.6,.4,1) both, crown-float 3s ease-in-out .55s infinite;}

/* ===== 败者小丑 ===== */
.clown-layer{position:absolute;inset:-30px -20px;z-index:22;pointer-events:none;overflow:hidden;}
.clown-layer img{position:absolute;left:50%;top:50%;width:min(360px,62vw);height:auto;transform:translate(-50%,-50%) rotate(-8deg);
  filter:drop-shadow(0 20px 40px rgba(0,0,0,.8)) saturate(1.1);
  animation:clown-shake .5s ease-in-out infinite alternate;}
@keyframes clown-shake{0%{transform:translate(-50%,-50%) rotate(-9deg) scale(1)}100%{transform:translate(-48%,-52%) rotate(-5deg) scale(1.04)}}
.clown-word{position:absolute;left:50%;top:16%;transform:translateX(-50%) rotate(-12deg);
  font-family:var(--serif);font-size:72px;font-weight:700;color:#ff5a6e;
  text-shadow:0 0 30px rgba(255,90,110,.8), 0 6px 20px rgba(0,0,0,.9);
  animation:crown-drop .5s cubic-bezier(.2,1.6,.4,1) both;}
.clown-pop-enter-active{transition:opacity .3s ease;}
.clown-pop-leave-active{transition:opacity .4s ease;}
.clown-pop-enter-from,.clown-pop-leave-to{opacity:0;}
/* 音效开关 */
.sfx-toggle{display:flex;align-items:center;gap:7px;height:32px;padding:0 13px;border-radius:99px;cursor:pointer;
  font-family:var(--serif);font-size:12.5px;letter-spacing:.08em;color:var(--rune);
  background:rgba(255,217,138,.07);border:1px solid var(--line-strong);transition:background .25s, box-shadow .25s;}
.sfx-toggle svg{width:15px;height:15px;flex:none;stroke:currentColor;fill:none;stroke-width:1.7;stroke-linejoin:round;stroke-linecap:round;}
.sfx-toggle:hover{background:rgba(255,217,138,.16);box-shadow:0 0 16px -4px var(--glow);}
.sfx-toggle.off{color:var(--muted);border-color:var(--line);}

/* 每日补给 */
.daily-supply{display:flex;align-items:center;gap:14px;margin-bottom:18px;padding:12px 16px;border-radius:14px;
  background:linear-gradient(100deg, rgba(255,217,138,.10), rgba(90,50,180,.08) 55%, rgba(10,8,18,.4));
  border:1px solid var(--line-strong);box-shadow:0 16px 40px -28px var(--glow);}
.daily-supply.claimed{opacity:.62;filter:saturate(.5);}
.ds-ic{width:38px;height:38px;flex:none;border-radius:11px;display:grid;place-items:center;font-size:20px;color:#140a22;
  background:linear-gradient(140deg,var(--rune),var(--fire-1));box-shadow:0 0 18px -4px var(--glow);}
.ds-txt{flex:1;min-width:0;}
.ds-txt b{display:block;font-family:var(--serif);font-size:14.5px;letter-spacing:.1em;color:var(--rune);}
.ds-txt small{display:block;font-size:11.5px;color:var(--muted);margin-top:3px;letter-spacing:.04em;}
.claim-btn{height:36px;flex:none;padding:0 18px;border:0;border-radius:9px;cursor:pointer;white-space:nowrap;
  font-family:var(--serif);font-size:13.5px;font-weight:700;letter-spacing:.06em;color:#140a22;
  background:linear-gradient(140deg,var(--rune),var(--fire-1));box-shadow:0 0 20px -5px var(--glow);transition:.22s;}
.claim-btn:hover:not(:disabled){transform:translateY(-2px);box-shadow:0 0 28px -4px var(--glow);}
.claim-btn:disabled{cursor:default;color:var(--muted);background:rgba(255,255,255,.05);box-shadow:none;}

/* 状态条 */
.dice-stats{display:grid;grid-template-columns:repeat(4,1fr);gap:14px;margin-bottom:20px;}
.dstat{position:relative;border-radius:14px;padding:14px 18px;
  background:linear-gradient(160deg, rgba(24,19,38,.82), rgba(10,8,18,.9));
  border:1px solid var(--line);box-shadow:0 20px 44px -28px rgba(0,0,0,.9);}
.dstat .dl{display:block;font-size:11.5px;letter-spacing:.14em;color:var(--muted);}
.dstat .dv{display:block;margin-top:6px;font-family:var(--serif);font-size:26px;font-weight:700;color:var(--rune);line-height:1;}
.dstat .dv.qi{color:#fff;text-shadow:0 0 18px var(--glow);}
.dstat .dv.mult{color:var(--cyan);}
.dstat .dv i{font-size:12px;font-style:normal;color:var(--muted);}

/* 赌桌 */
.arena{display:grid;grid-template-columns:1fr 120px 1fr;gap:16px;align-items:center;
  border-radius:20px;padding:24px;
  background:radial-gradient(120% 90% at 50% 0%, rgba(90,50,180,.12), transparent 60%), linear-gradient(165deg, rgba(20,16,32,.8), rgba(8,6,15,.92));
  border:1px solid var(--line);box-shadow:0 40px 80px -40px rgba(0,0,0,.9), inset 0 0 60px -30px var(--glow);}
.seat{position:relative;border-radius:16px;padding:18px;text-align:center;
  background:linear-gradient(160deg, rgba(30,24,48,.6), rgba(12,9,22,.7));border:1px solid var(--line);}
.seat.player{border-color:var(--line-strong);box-shadow:0 0 30px -14px var(--glow);}
.seat-head{display:flex;align-items:center;gap:10px;justify-content:center;margin-bottom:16px;}
.seat-head .av{width:36px;height:36px;flex:none;border-radius:50%;display:grid;place-items:center;
  font-family:var(--serif);font-size:17px;font-weight:700;color:#140a22;
  background:linear-gradient(140deg,var(--rune),var(--fire-1));box-shadow:0 0 16px -3px var(--glow);}
.seat-head b{display:block;font-family:var(--serif);font-size:15px;letter-spacing:.08em;}
.seat-head small{display:block;font-size:10.5px;letter-spacing:.2em;color:var(--muted);margin-top:2px;}
.dice-row{display:flex;gap:14px;justify-content:center;}
.die{width:64px;height:64px;border-radius:14px;padding:9px;display:grid;grid-template-columns:repeat(3,1fr);grid-template-rows:repeat(3,1fr);gap:3px;
  background:linear-gradient(145deg,#fbf4e6,#e4d8c2);border:1px solid #cbb994;
  box-shadow:0 10px 22px -10px rgba(0,0,0,.9), inset 0 2px 4px rgba(255,255,255,.8);transition:box-shadow .3s, border-color .3s;}
.die.die-win{position:relative;z-index:1;box-shadow:0 0 26px -4px var(--glow), 0 10px 22px -10px rgba(0,0,0,.9);border-color:var(--rune);
  animation:die-triumph .6s cubic-bezier(.2,1.5,.4,1) both;}
@keyframes die-triumph{0%{transform:scale(1)}35%{transform:scale(1.14) rotate(-4deg)}70%{transform:scale(.97) rotate(2deg)}100%{transform:scale(1) rotate(0)}}
/* 胜者骰子：旋转符文光环 + 迸射星火 */
.spark-ring{position:absolute;inset:-7px;border-radius:18px;pointer-events:none;z-index:2;
  border:1.5px solid var(--rune);border-left-color:transparent;border-right-color:transparent;
  box-shadow:0 0 14px -2px var(--glow), inset 0 0 10px -4px var(--glow);
  animation:ring-spin 1.6s linear infinite, ring-flash .7s ease-out both;}
@keyframes ring-spin{to{transform:rotate(360deg)}}
@keyframes ring-flash{0%{opacity:0;transform:scale(.6)}40%{opacity:1}100%{opacity:1;transform:scale(1)}}
.spark-ring::before,.spark-ring::after{content:"";position:absolute;top:50%;left:50%;width:5px;height:5px;margin:-2.5px;border-radius:50%;
  background:var(--rune);box-shadow:0 0 8px 2px var(--glow);}
.spark-ring::before{animation:spark-out 1s ease-out infinite .15s;}
.spark-ring::after{animation:spark-out 1s ease-out infinite .65s;}
@keyframes spark-out{0%{opacity:.9;transform:translate(0,0) scale(1)}100%{opacity:0;transform:translate(30px,-30px) scale(.2)}}
.die.rolling{animation:tumble .25s linear infinite;}
@keyframes tumble{0%{transform:rotate(-8deg) translateY(0)}50%{transform:rotate(8deg) translateY(-4px)}100%{transform:rotate(-8deg) translateY(0)}}
.pip{border-radius:50%;background:transparent;}
.pip.on{background:radial-gradient(circle at 35% 30%, #6d4bb8, #2a1656);box-shadow:inset 0 -1px 2px rgba(0,0,0,.5);}
.die-win .pip.on{background:radial-gradient(circle at 35% 30%, #ffcf6b, #b4711d);box-shadow:inset 0 -1px 2px rgba(0,0,0,.4), 0 0 6px var(--rune);}
/* 胜方座位：金色呼吸光晕 */
.seat-winner{animation:seat-glow 1.8s ease-in-out infinite;}
.seat-winner.player{border-color:var(--rune);}
@keyframes seat-glow{0%,100%{box-shadow:0 0 22px -12px var(--glow), inset 0 0 24px -18px var(--glow)}
  50%{box-shadow:0 0 44px -8px var(--glow), inset 0 0 34px -14px var(--glow)}}
/* 玩家胜：冠冕标记 */
.crown{position:absolute;top:-12px;right:-10px;width:34px;height:34px;border-radius:50%;display:grid;place-items:center;z-index:3;
  font-family:var(--serif);font-size:17px;font-weight:700;color:#140a22;
  background:linear-gradient(140deg,var(--rune),var(--fire-1));
  box-shadow:0 0 20px -2px var(--glow), 0 6px 14px -6px #000;
  animation:crown-drop .55s cubic-bezier(.2,1.6,.4,1) both, crown-float 3s ease-in-out .55s infinite;}
@keyframes crown-drop{0%{opacity:0;transform:translateY(-26px) scale(.4) rotate(-40deg)}100%{opacity:1;transform:translateY(0) scale(1) rotate(0)}}
@keyframes crown-float{0%,100%{transform:translateY(0)}50%{transform:translateY(-4px)}}
.sum{margin-top:14px;font-family:var(--serif);font-size:12.5px;letter-spacing:.1em;color:var(--muted);}
.sum b{font-size:19px;color:var(--rune);margin:0 2px;}

.mid{text-align:center;}
.versus{width:52px;height:52px;margin:0 auto 10px;border-radius:50%;display:grid;place-items:center;
  font-family:var(--serif);font-size:24px;font-weight:700;color:#140a22;
  background:linear-gradient(145deg,var(--rune),var(--fire-1));box-shadow:0 0 26px -4px var(--glow);
  animation:float 5s ease-in-out infinite;}
@keyframes float{0%,100%{transform:translateY(0)}50%{transform:translateY(-6px)}}
.verdict{margin-top:6px;font-family:var(--serif);font-size:13px;font-weight:700;letter-spacing:.04em;line-height:1.6;}
.verdict.win{color:var(--rune);text-shadow:0 0 14px var(--glow);
  animation:verdict-stamp .5s cubic-bezier(.2,1.7,.4,1) both, verdict-shine 2.4s ease-in-out .5s infinite;}
@keyframes verdict-stamp{0%{opacity:0;transform:scale(1.7) rotate(-6deg)}60%{opacity:1;transform:scale(.92) rotate(2deg)}100%{opacity:1;transform:scale(1) rotate(0)}}
@keyframes verdict-shine{0%,100%{text-shadow:0 0 12px var(--glow)}50%{text-shadow:0 0 24px var(--rune), 0 0 8px var(--glow)}}
.verdict.lose{color:#ff8a94;animation:verdict-stamp .45s cubic-bezier(.2,1.7,.4,1) both;}
.verdict.draw{color:var(--cyan);animation:verdict-stamp .45s cubic-bezier(.2,1.7,.4,1) both;}
.verdict-hint{margin-top:6px;font-family:var(--serif);font-size:12px;letter-spacing:.1em;color:var(--muted);}
/* 胜者光刃：中央指向胜方的流星 */
.mid{position:relative;}
.win-beam{position:absolute;top:26px;width:96px;height:3px;border-radius:99px;pointer-events:none;z-index:4;
  background:linear-gradient(90deg, transparent, var(--rune) 60%, #fff);
  box-shadow:0 0 14px 2px var(--glow);}
.win-beam.player{right:50%;transform-origin:right center;animation:beam-player .7s ease-out both;}
.win-beam.dealer{left:50%;transform-origin:left center;animation:beam-dealer .7s ease-out both;}
.win-beam::after{content:"";position:absolute;top:50%;width:9px;height:9px;margin-top:-4.5px;border-radius:50%;background:#fff;
  box-shadow:0 0 14px 4px var(--rune);}
.win-beam.player::after{right:-4px;}
.win-beam.dealer::after{left:-4px;}
@keyframes beam-player{0%{opacity:0;transform:scaleX(0)}30%{opacity:1}100%{opacity:0;transform:scaleX(1)}}
@keyframes beam-dealer{0%{opacity:0;transform:scaleX(0)}30%{opacity:1}100%{opacity:0;transform:scaleX(1)}}

/* 押注控制 */
.table-control{display:flex;flex-wrap:wrap;align-items:center;justify-content:space-between;gap:14px;margin-top:20px;}
.bet-area{display:flex;align-items:center;gap:9px;flex-wrap:wrap;}
.bet-label{font-family:var(--serif);font-size:13px;letter-spacing:.1em;color:var(--muted);margin-right:2px;}
.chip{height:36px;min-width:48px;padding:0 14px;border-radius:99px;cursor:pointer;font-family:var(--serif);font-size:14px;
  color:var(--rune);background:rgba(255,217,138,.06);border:1px solid var(--line-strong);transition:.22s;}
.chip:hover:not(:disabled){background:rgba(255,217,138,.16);transform:translateY(-2px);}
.chip.on{color:#140a22;font-weight:700;background:linear-gradient(140deg,var(--rune),var(--fire-1));border-color:transparent;box-shadow:0 0 18px -3px var(--glow);}
.chip.all{min-width:56px;}
.chip:disabled{opacity:.4;cursor:not-allowed;}
.action-area{display:flex;align-items:center;gap:10px;}
.roll-btn{height:40px;min-width:118px;padding:0 22px;border:0;border-radius:10px;cursor:pointer;
  font-family:var(--serif);font-size:15px;font-weight:700;letter-spacing:.12em;color:#140a22;
  background:linear-gradient(140deg,var(--rune),var(--fire-1));box-shadow:0 0 24px -6px var(--glow);transition:.25s;}
.roll-btn:hover:not(:disabled){transform:translateY(-2px);box-shadow:0 0 32px -4px var(--glow);}
.roll-btn:disabled{opacity:.55;cursor:not-allowed;}
.roll-btn.refill{background:linear-gradient(140deg,var(--cyan),#2a8bff);color:#04121c;}
.reset-btn{height:40px;padding:0 14px;border-radius:10px;cursor:pointer;font-family:var(--serif);font-size:12.5px;letter-spacing:.08em;
  color:var(--muted);background:rgba(255,255,255,.03);border:1px solid var(--line);transition:.22s;}
.reset-btn:hover{color:var(--text);border-color:var(--line-strong);}
.bet-note{margin-top:12px;font-size:11.5px;letter-spacing:.06em;color:var(--muted);font-family:var(--serif);}
.bet-note b{color:var(--rune);font-weight:400;}

/* 战绩 */
.history{margin-top:26px;}
.history-list{display:grid;grid-template-columns:repeat(2,1fr);gap:8px;}
.hrow{display:flex;align-items:center;gap:10px;padding:9px 12px;border-radius:10px;
  background:linear-gradient(160deg, rgba(24,19,38,.7), rgba(10,8,18,.8));border:1px solid var(--line);font-size:11.5px;}
.hrow .hicon{width:24px;height:24px;flex:none;border-radius:6px;display:grid;place-items:center;font-family:var(--serif);font-size:13px;font-weight:700;color:#140a22;}
.hrow.win .hicon{background:linear-gradient(140deg,var(--rune),var(--fire-1));}
.hrow.lose .hicon{background:linear-gradient(140deg,#ff9aa4,#c62f45);color:#fff;}
.hrow.draw .hicon{background:linear-gradient(140deg,var(--cyan),#2a8bff);color:#04121c;}
.hrow .hdetail{flex:1;color:#b9b0cc;font-family:var(--serif);letter-spacing:.04em;}
.hrow .hgain{font-family:var(--serif);font-weight:700;white-space:nowrap;}
.hrow.win .hgain{color:var(--rune);}
.hrow.lose .hgain{color:#ff8a94;}
.hrow.draw .hgain{color:var(--cyan);}

@media (max-width:900px){
  .arena{grid-template-columns:1fr;gap:14px;}
  .mid{padding:6px 0;}
  .versus{margin-bottom:4px;}
  .dice-stats{grid-template-columns:repeat(2,1fr);}
  .history-list{grid-template-columns:1fr;}
  .section-head{align-items:center;}
  .daily-supply{gap:10px;padding:10px 12px;}
  .ds-txt small{display:none;}
}
</style>

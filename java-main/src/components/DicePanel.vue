<template>
  <div class="rank-page dice-page">
    <section class="section">
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
        <div class="seat dealer">
          <div class="seat-head">
            <span class="av">{{ dealer.charAt(0) }}</span>
            <div><b>{{ dealer }}</b><small>庄家</small></div>
          </div>
          <div class="dice-row">
            <div
              v-for="(v, i) in dealerDice"
              :key="'d' + i"
              class="die"
              :class="{ rolling: rolling, 'die-win': !rolling && settled && dealerSum > playerSum }"
            >
              <span v-for="p in 9" :key="p" class="pip" :class="{ on: pipMap[v].includes(p - 1) }"></span>
            </div>
          </div>
          <div class="sum">共 <b>{{ dealerSum }}</b> 点</div>
        </div>

        <!-- 中间 VS / 赛果 -->
        <div class="mid">
          <div class="versus">斗</div>
          <div v-if="settled" class="verdict" :class="verdictType">{{ verdictText }}</div>
          <div v-else class="verdict-hint">{{ rolling ? '骰影翻飞…' : '落注开局' }}</div>
        </div>

        <!-- 玩家 -->
        <div class="seat player">
          <div class="seat-head">
            <span class="av">{{ player.charAt(0) }}</span>
            <div><b>{{ player }}</b><small>斗者</small></div>
          </div>
          <div class="dice-row">
            <div
              v-for="(v, i) in playerDice"
              :key="'p' + i"
              class="die"
              :class="{ rolling: rolling, 'die-win': !rolling && settled && playerSum > dealerSum }"
            >
              <span v-for="p in 9" :key="p" class="pip" :class="{ on: pipMap[v].includes(p - 1) }"></span>
            </div>
          </div>
          <div class="sum">共 <b>{{ playerSum }}</b> 点</div>
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
import { ref, computed } from 'vue'
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
const dealer = ref(dealers[Math.floor(Math.random() * dealers.length)])

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
    verdictType.value = 'win'
    verdictText.value = '胜 · 得 ' + gain + ' 斗气'
    playWin()
    showToast('骰运昌隆，胜！获得 ' + gain + ' 斗气', '✦')
  } else if (p < d) {
    gain = -stake.value
    qi.value = Math.max(0, qi.value - stake.value)
    streak.value = 0
    type = 'lose'
    verdictType.value = 'lose'
    verdictText.value = '负 · 失 ' + stake.value + ' 斗气'
    playLose()
    showToast('此局失利，损失 ' + stake.value + ' 斗气', '✕')
  } else {
    verdictType.value = 'draw'
    verdictText.value = '和 · 退还押注'
    playDraw()
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
  verdictText.value = ''
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
  verdictText.value = ''
  persist()
  showToast('战绩已重置', '✦')
}
</script>

<style scoped>
.dice-page{position:relative;z-index:2;}
.lead{font-family:var(--serif);font-size:14px;letter-spacing:.12em;color:#b9b0cc;margin:-6px 0 22px;text-shadow:0 2px 14px rgba(0,0,0,.7);}
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
.die.die-win{box-shadow:0 0 26px -4px var(--glow), 0 10px 22px -10px rgba(0,0,0,.9);border-color:var(--rune);}
.die.rolling{animation:tumble .25s linear infinite;}
@keyframes tumble{0%{transform:rotate(-8deg) translateY(0)}50%{transform:rotate(8deg) translateY(-4px)}100%{transform:rotate(-8deg) translateY(0)}}
.pip{border-radius:50%;background:transparent;}
.pip.on{background:radial-gradient(circle at 35% 30%, #6d4bb8, #2a1656);box-shadow:inset 0 -1px 2px rgba(0,0,0,.5);}
.sum{margin-top:14px;font-family:var(--serif);font-size:12.5px;letter-spacing:.1em;color:var(--muted);}
.sum b{font-size:19px;color:var(--rune);margin:0 2px;}

.mid{text-align:center;}
.versus{width:52px;height:52px;margin:0 auto 10px;border-radius:50%;display:grid;place-items:center;
  font-family:var(--serif);font-size:24px;font-weight:700;color:#140a22;
  background:linear-gradient(145deg,var(--rune),var(--fire-1));box-shadow:0 0 26px -4px var(--glow);
  animation:float 5s ease-in-out infinite;}
@keyframes float{0%,100%{transform:translateY(0)}50%{transform:translateY(-6px)}}
.verdict{margin-top:6px;font-family:var(--serif);font-size:13px;font-weight:700;letter-spacing:.04em;line-height:1.6;}
.verdict.win{color:var(--rune);text-shadow:0 0 14px var(--glow);}
.verdict.lose{color:#ff8a94;}
.verdict.draw{color:var(--cyan);}
.verdict-hint{margin-top:6px;font-family:var(--serif);font-size:12px;letter-spacing:.1em;color:var(--muted);}

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

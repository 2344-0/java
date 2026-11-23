import { ref, computed } from 'vue'

// ============================================================
// 斗破苍穹 · 修炼体系（严格原著设定）
// 斗之气（十段）→ 斗者 → 斗师 → 大斗师 → 斗灵 → 斗王 → 斗皇
//   → 斗宗 → 斗尊 → 斗圣 → 斗帝（各大境界九星）
// 斗气为跨组件共享状态：修炼阁打坐积累，掷骰坊押注消耗，
// 境界越高，掷骰彩头加成越厚。
// ============================================================

export const REALMS = [
  { key: 'douqi', name: '斗之气', unit: '段', max: 10, ability: '感应天地斗气，淬炼己身', color: '#b9b0cc' },
  { key: 'douzhe', name: '斗者', unit: '星', max: 9, ability: '凝聚斗之气旋，踏入修炼之途', color: '#9df3ff' },
  { key: 'doushi', name: '斗师', unit: '星', max: 9, ability: '斗气外放，凝甲护身', color: '#7ee8ff' },
  { key: 'dadoushi', name: '大斗师', unit: '星', max: 9, ability: '斗气化铠，短暂御空', color: '#6ad0ff' },
  { key: 'douling', name: '斗灵', unit: '星', max: 9, ability: '斗气凝灵，灵体随身', color: '#a97bff' },
  { key: 'douwang', name: '斗王', unit: '星', max: 9, ability: '凝聚斗气化翼，翱翔天地', color: '#c08aff' },
  { key: 'douhuang', name: '斗皇', unit: '星', max: 9, ability: '斗气如皇，一击摧城', color: '#ff8ae0' },
  { key: 'douzong', name: '斗宗', unit: '星', max: 9, ability: '凌空踏虚，掌控空间', color: '#ff7b9c' },
  { key: 'dunzun', name: '斗尊', unit: '星', max: 9, ability: '空间穿梭，万法不侵', color: '#ff9a5a' },
  { key: 'dousheng', name: '斗圣', unit: '星', max: 9, ability: '言出法随，毁天灭地', color: '#ffd166' },
  { key: 'doudi', name: '斗帝', unit: '星', max: 9, ability: '万火之尊，唯我独尊', color: '#fff3c4' }
]

// 异火加持（异火榜名次取自原著），提升打坐速度
export const FIRE_OPTIONS = [
  { key: 'none', name: '未炼化异火', rank: '——', mul: 1, desc: '凭自身苦修，稳扎稳打' },
  { key: 'qinglian', name: '青莲地心火', rank: '异火榜 · 第19', mul: 1.3, desc: '生于地心，淬炼经脉' },
  { key: 'yunluo', name: '陨落心炎', rank: '异火榜 · 第14', mul: 1.6, desc: '心火焚身，修炼加速' },
  { key: 'sanyan', name: '三千焱炎火', rank: '异火榜 · 第9', mul: 2, desc: '星空之火，吞噬斗气' },
  { key: 'jinglian', name: '净莲妖火', rank: '异火榜 · 第3', mul: 2.6, desc: '万火妖莲，净化身心' },
  { key: 'gudi', name: '陀舍古帝火', rank: '异火榜 · 第1', mul: 3.2, desc: '万火之祖，焚天煮海' }
]

const STORE_KEY = 'doupo_cultivation_v1'
const DICE_KEY = 'doupo_dice_save_v1'

function clamp (v, min, max) { return Math.max(min, Math.min(max, v)) }

function loadState () {
  const state = { qi: 100, realmIndex: 0, level: 1, fire: 'none' }
  try {
    const raw = localStorage.getItem(STORE_KEY)
    if (raw) {
      const d = JSON.parse(raw)
      state.qi = Math.max(0, Number(d.qi) || 0)
      state.realmIndex = clamp(Number(d.realmIndex) || 0, 0, REALMS.length - 1)
      state.level = clamp(Number(d.level) || 1, 1, REALMS[state.realmIndex].max)
      state.fire = FIRE_OPTIONS.some(f => f.key === d.fire) ? d.fire : 'none'
      return state
    }
    // 迁移掷骰坊旧存档中的斗气，避免打通后斗气重置
    const oldRaw = localStorage.getItem(DICE_KEY)
    if (oldRaw) {
      const old = JSON.parse(oldRaw)
      if (old && old.qi != null) state.qi = Math.max(0, Number(old.qi) || 0)
    }
  } catch (e) { /* 使用默认值 */ }
  return state
}

const initial = loadState()

// ---------- 跨组件共享的单例状态 ----------
export const qi = ref(initial.qi)
export const realmIndex = ref(initial.realmIndex)
export const level = ref(initial.level)
export const fire = ref(initial.fire)
export const meditating = ref(false)
export const lastBreakthrough = ref(null)

export function persist () {
  try {
    localStorage.setItem(STORE_KEY, JSON.stringify({
      qi: qi.value, realmIndex: realmIndex.value, level: level.value, fire: fire.value
    }))
  } catch (e) { /* 隐私模式下忽略 */ }
}

// ---------- 派生数据 ----------
export const currentRealm = computed(() => REALMS[realmIndex.value])
export const realmLabel = computed(() => currentRealm.value.name + level.value + currentRealm.value.unit)
export const currentFire = computed(() => FIRE_OPTIONS.find(f => f.key === fire.value) || FIRE_OPTIONS[0])

// 突破所需斗气：随大境界与星级递增
export const breakthroughCost = computed(() =>
  Math.round(80 + realmIndex.value * 120 + (level.value - 1) * 30)
)

// 打坐每次（每秒）获得的斗气
export const meditateGain = computed(() => {
  const base = 6 + realmIndex.value * 5
  return Math.round(base * currentFire.value.mul)
})

// 当前境界进度（0~1），用于进度条
export const realmProgress = computed(() => {
  const cost = breakthroughCost.value
  return clamp(qi.value / cost, 0, 1)
})
export const canBreakthrough = computed(() =>
  qi.value >= breakthroughCost.value && !(realmIndex.value === REALMS.length - 1 && level.value === REALMS[realmIndex.value].max)
)
export const isMaxRealm = computed(() =>
  realmIndex.value === REALMS.length - 1 && level.value === REALMS[realmIndex.value].max
)

// 境界加成：掷骰坊彩头随境界提升（每个大境界 +15%）
export const realmBonus = computed(() => 1 + realmIndex.value * 0.15)

// 功法：焚决随境界进化（原著焚决可吞噬异火不断进阶）
export const gongfaName = computed(() => {
  const tier = ['初阶', '中阶', '高阶', '宗师', '尊者', '圣阶', '帝阶'][Math.min(6, Math.floor(realmIndex.value / 2))]
  return '焚决 · ' + tier
})

// ---------- 行为 ----------
let meditateTimer = null
export function startMeditate () {
  if (meditating.value) return
  meditating.value = true
  meditateTimer = setInterval(() => {
    qi.value += meditateGain.value
    persist()
  }, 1000)
}
export function stopMeditate () {
  meditating.value = false
  if (meditateTimer) clearInterval(meditateTimer)
  meditateTimer = null
}
export function toggleMeditate () {
  meditating.value ? stopMeditate() : startMeditate()
}

export function selectFire (key) {
  fire.value = key
  persist()
}

// 突破：消耗斗气，升星/升段；本境圆满则进入下一境界
export function breakthrough () {
  if (!canBreakthrough.value) return null
  qi.value -= breakthroughCost.value
  const from = realmLabel.value
  if (level.value < currentRealm.value.max) {
    level.value++
  } else {
    realmIndex.value++
    level.value = 1
  }
  lastBreakthrough.value = { from, to: realmLabel.value }
  persist()
  return lastBreakthrough.value
}

// 供掷骰坊等操作斗气后统一存档
export function syncQi () { persist() }

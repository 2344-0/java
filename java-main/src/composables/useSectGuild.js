// ============================================================
// 宗门玩法：职位晋升 · 每日委派 · 宗门宝库（斗破苍穹原著设定）
// ------------------------------------------------------------
// 与修炼系统联动：
//   · 任务贡献受「职位倍率 × 功法加成 × 境界加成 × 宗门底蕴」叠加
//   · 任务与宝库奖励的斗气直接进入共享斗气（修炼阁/掷骰坊通用）
// 存档 key：doupo_sect_guild_v1（按用户名隔离，跨天自动重置任务）
// ============================================================
import { ref, computed } from 'vue'
import { qi, realmBonus, persist as persistCultivation } from './useCultivation.js'

const STORE_KEY = 'doupo_sect_guild_v1'

function clamp (v, min, max) { return Math.max(min, Math.min(max, v)) }

function todayStr () {
  const d = new Date()
  return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')}`
}

/** 宗门职位体系：贡献达标自动晋升，职位越高任务收益越厚 */
export const RANKS = [
  { name: '外门弟子', min: 0, bonus: 1.0, desc: '初入宗门，杂役修行' },
  { name: '内门弟子', min: 100, bonus: 1.1, desc: '入室弟子，得传功法' },
  { name: '核心弟子', min: 300, bonus: 1.25, desc: '宗门中坚，重点栽培' },
  { name: '执法长老', min: 700, bonus: 1.45, desc: '执掌门规，威震一方' },
  { name: '宗门长老', min: 1500, bonus: 1.7, desc: '德高望重，共议宗务' },
  { name: '副宗主', min: 3000, bonus: 2.0, desc: '一人之下，统摄群雄' },
  { name: '宗主', min: 6000, bonus: 2.5, desc: '执掌宗门，号令天下' }
]

/** 每日宗门委派：次数每日重置，高级任务需晋升职位解锁 */
export const TASKS = [
  { id: 'herb', name: '采集灵药', icon: '药', desc: '深入山林采集天材地宝', contrib: 30, qi: 0, cost: 0, limit: 2, rankMin: 0 },
  { id: 'hunt', name: '猎杀魔兽', icon: '兽', desc: '斩杀妖兽，历练自身', contrib: 50, qi: 60, cost: 0, limit: 2, rankMin: 0 },
  { id: 'patrol', name: '巡逻领地', icon: '巡', desc: '巡视宗门势力范围', contrib: 20, qi: 30, cost: 0, limit: 3, rankMin: 0 },
  { id: 'guard', name: '守护山门', icon: '守', desc: '镇守宗门要地，抵御外敌', contrib: 40, qi: 40, cost: 0, limit: 1, rankMin: 1 },
  { id: 'insight', name: '参悟斗技', icon: '悟', desc: '于演武场参悟宗门斗技', contrib: 60, qi: 0, cost: 120, limit: 1, rankMin: 2 },
  { id: 'escort', name: '押送物资', icon: '镖', desc: '护送宗门物资往来各城', contrib: 45, qi: 50, cost: 0, limit: 2, rankMin: 0 }
]

/** 宗门宝库：贡献兑换斗气与永久功法增益 */
export const SHOP = [
  { id: 'qiju', name: '聚气丹', icon: '丹', desc: '浓郁斗气凝丹，服之增益百缕斗气', cost: 50, effect: { qi: 120 } },
  { id: 'douling', name: '斗灵丹', icon: '灵', desc: '高阶丹药，蕴含庞大斗气', cost: 120, effect: { qi: 350 } },
  { id: 'ningqi', name: '凝气符', icon: '符', desc: '记载凝气之法的符箓，斗气大增', cost: 400, effect: { qi: 800 } },
  { id: 'gongfa', name: '宗门功法', icon: '功', desc: '宗门传承功法，永久提升任务贡献 10%，最多参悟五层', cost: 200, effect: { gongfa: 1 }, maxStack: 5 }
]

/** 宗门底蕴：超级 > 一流 > 二流，影响任务贡献 */
const GRADE_BONUS = { '超级': 1.2, '一流': 1.1, '二流': 1.0 }

function blankState () {
  return { contribution: 0, taskProgress: {}, gongfaStack: 0, taskDate: todayStr() }
}

function loadAll () {
  try {
    const raw = localStorage.getItem(STORE_KEY)
    if (raw) {
      const all = JSON.parse(raw)
      return all && typeof all === 'object' ? all : {}
    }
  } catch (e) { /* 使用空存档 */ }
  return {}
}

function saveAll (all) {
  try { localStorage.setItem(STORE_KEY, JSON.stringify(all)) } catch (e) { /* 隐私模式忽略 */ }
}

// ---------- 单例共享状态 ----------
const currentUser = ref('guest')
const contribution = ref(0)
const taskProgress = ref({})
const gongfaStack = ref(0)
const taskDate = ref(todayStr())
const sectGradeBonus = ref(1)

function persistGuild () {
  const all = loadAll()
  all[currentUser.value] = {
    contribution: contribution.value,
    taskProgress: taskProgress.value,
    gongfaStack: gongfaStack.value,
    taskDate: taskDate.value
  }
  saveAll(all)
}

/** 载入指定斗者的宗门玩法存档；grade 为宗门等级（超级/一流/二流） */
export function loadGuild (username, grade) {
  currentUser.value = (username && username.trim()) || 'guest'
  sectGradeBonus.value = GRADE_BONUS[grade] || 1
  const data = loadAll()[currentUser.value] || blankState()
  contribution.value = Math.max(0, Number(data.contribution) || 0)
  taskProgress.value = (data.taskProgress && typeof data.taskProgress === 'object') ? data.taskProgress : {}
  gongfaStack.value = clamp(Number(data.gongfaStack) || 0, 0, 5)
  taskDate.value = data.taskDate || todayStr()
  if (taskDate.value !== todayStr()) {
    taskProgress.value = {}
    taskDate.value = todayStr()
    persistGuild()
  }
}

function ensureToday () {
  if (taskDate.value !== todayStr()) {
    taskProgress.value = {}
    taskDate.value = todayStr()
  }
}

// ---------- 派生数据 ----------
export const currentRankIndex = computed(() => {
  let idx = 0
  for (let i = 0; i < RANKS.length; i++) {
    if (contribution.value >= RANKS[i].min) idx = i
  }
  return idx
})
export const currentRank = computed(() => RANKS[currentRankIndex.value])
export const nextRank = computed(() => RANKS[currentRankIndex.value + 1] || null)
export const rankProgress = computed(() => {
  const cur = currentRank.value.min
  const nxt = nextRank.value ? nextRank.value.min : cur
  return nxt > cur ? clamp((contribution.value - cur) / (nxt - cur), 0, 1) : 1
})

/** 任务贡献总加成：职位 × 功法 × 境界 × 宗门底蕴 */
export const contributionBonus = computed(() =>
  currentRank.value.bonus * (1 + gongfaStack.value * 0.1) * realmBonus.value * sectGradeBonus.value
)

export function taskUsed (taskId) {
  ensureToday()
  return taskProgress.value[taskId] || 0
}

export function taskReward (task) {
  return {
    contrib: Math.round(task.contrib * contributionBonus.value),
    qi: Math.round(task.qi * currentRank.value.bonus)
  }
}

export function canDoTask (task) {
  ensureToday()
  if (currentRankIndex.value < (task.rankMin || 0)) return false
  if (taskUsed(task.id) >= task.limit) return false
  if (task.cost > qi.value) return false
  return true
}

/** 完成委派：发放贡献与斗气，写入存档 */
export function completeTask (task) {
  ensureToday()
  if (currentRankIndex.value < (task.rankMin || 0)) return { ok: false, msg: '职位不足，此委派尚未解锁' }
  if (taskUsed(task.id) >= task.limit) return { ok: false, msg: '今日此委派次数已用尽' }
  if (task.cost > qi.value) return { ok: false, msg: '斗气不足，无法完成' }

  const reward = taskReward(task)
  qi.value = qi.value - task.cost + reward.qi
  contribution.value += reward.contrib
  taskProgress.value[task.id] = taskUsed(task.id) + 1
  persistCultivation()
  persistGuild()
  return { ok: true, reward }
}

/** 宝库兑换：扣除贡献，发放斗气或功法增益 */
export function buyItem (item) {
  if (contribution.value < item.cost) return { ok: false, msg: '宗门贡献不足' }
  if (item.effect.gongfa && gongfaStack.value >= item.maxStack) return { ok: false, msg: '此功法已参悟至圆满' }

  contribution.value -= item.cost
  const detail = []
  if (item.effect.qi) {
    qi.value += item.effect.qi
    detail.push('斗气 +' + item.effect.qi)
    persistCultivation()
  }
  if (item.effect.gongfa) {
    gongfaStack.value += item.effect.gongfa
    detail.push('贡献增益 +' + Math.round(item.effect.gongfa * 10) + '%')
  }
  persistGuild()
  return { ok: true, detail: detail.join('，') }
}

export { contribution, gongfaStack, taskDate, sectGradeBonus }

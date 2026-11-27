import { ref, watch } from 'vue'

const SAVE_KEY = 'doupo_avatar_save_v1'

// 默认形象（古风玄幻审美，男女模板各一套预设值）
export const defaultAvatar = {
  gender: 'female',
  skin: '#f7d9bd',
  faceWidth: 52,      // 脸宽半径
  chinWidth: 18,      // 下巴宽度（越小越尖）
  eyeSize: 1,         // 眼睛缩放
  eyeSpacing: 22,     // 单眼中心到脸中线的距离
  eyeColor: '#5b3a8c',
  eyeStyle: 'almond', // almond 杏眼 / phoenix 丹凤 / round 圆眼
  browStyle: 'soft',  // soft 弯眉 / sharp 剑眉 / flat 平眉
  browColor: '#3a2b33',
  noseStyle: 'small', // small 秀气 / straight 挺直
  mouthStyle: 'smile',// smile 微笑 / calm 淡然 / charming 朱唇
  blush: true,
  hairStyle: 'long',  // long 长发 / ponytail 马尾 / short 短发 / bun 发髻 / twin 双髻
  hairColor: '#2b1b3d',
  robeColor: '#6d4bb8',
  accessory: 'hairpin', // none / hairpin 玉簪 / flower 花钿
  mark: 'none'        // none / red 花钿 / gold 金纹
}

const malePreset = {
  gender: 'male',
  skin: '#f0cfae',
  faceWidth: 56,
  chinWidth: 24,
  eyeSize: 0.95,
  eyeSpacing: 23,
  eyeColor: '#3b2a4a',
  eyeStyle: 'phoenix',
  browStyle: 'sharp',
  browColor: '#2a2026',
  noseStyle: 'straight',
  mouthStyle: 'calm',
  blush: false,
  hairStyle: 'ponytail',
  hairColor: '#1f1526',
  robeColor: '#3a2b6e',
  accessory: 'none',
  mark: 'none'
}

export const genderPresets = { female: { ...defaultAvatar }, male: { ...malePreset } }

function load () {
  try {
    const raw = localStorage.getItem(SAVE_KEY)
    if (!raw) return { ...defaultAvatar }
    const d = JSON.parse(raw)
    return { ...defaultAvatar, ...d }
  } catch (e) {
    return { ...defaultAvatar }
  }
}

export const avatar = ref(load())

watch(avatar, (v) => {
  try { localStorage.setItem(SAVE_KEY, JSON.stringify(v)) } catch (e) {}
}, { deep: true })

export function saveAvatar () {
  try { localStorage.setItem(SAVE_KEY, JSON.stringify(avatar.value)) } catch (e) {}
}

export function resetAvatar (gender) {
  avatar.value = { ...(genderPresets[gender] || defaultAvatar) }
}

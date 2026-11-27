<template>
  <svg class="avatar-figure" :viewBox="`0 0 ${W} ${H}`" xmlns="http://www.w3.org/2000/svg" role="img" aria-label="斗者形象">
    <defs>
      <!-- 脸部立体渐变 -->
      <linearGradient :id="gid('skin')" x1="0" y1="0" x2="1" y2="1">
        <stop offset="0%" :stop-color="lighten(cfg.skin, 18)" />
        <stop offset="55%" :stop-color="cfg.skin" />
        <stop offset="100%" :stop-color="darken(cfg.skin, 16)" />
      </linearGradient>
      <!-- 脸部柔光 -->
      <radialGradient :id="gid('faceGlow')" cx="50%" cy="42%" r="60%">
        <stop offset="0%" stop-color="#fff" stop-opacity=".28" />
        <stop offset="100%" stop-color="#fff" stop-opacity="0" />
      </radialGradient>
      <!-- 头发渐变 -->
      <linearGradient :id="gid('hair')" x1="0" y1="0" x2="0.9" y2="1">
        <stop offset="0%" :stop-color="lighten(cfg.hairColor, 26)" />
        <stop offset="45%" :stop-color="cfg.hairColor" />
        <stop offset="100%" :stop-color="darken(cfg.hairColor, 24)" />
      </linearGradient>
      <!-- 衣袍渐变 -->
      <linearGradient :id="gid('robe')" x1="0" y1="0" x2="1" y2="1">
        <stop offset="0%" :stop-color="lighten(cfg.robeColor, 22)" />
        <stop offset="100%" :stop-color="darken(cfg.robeColor, 20)" />
      </linearGradient>
      <linearGradient :id="gid('robeDark')" x1="0" y1="0" x2="0" y2="1">
        <stop offset="0%" :stop-color="darken(cfg.robeColor, 34)" />
        <stop offset="100%" :stop-color="darken(cfg.robeColor, 8)" />
      </linearGradient>
      <!-- 眼睛高光 -->
      <radialGradient :id="gid('iris')" cx="42%" cy="38%" r="62%">
        <stop offset="0%" :stop-color="lighten(cfg.eyeColor, 40)" />
        <stop offset="55%" :stop-color="cfg.eyeColor" />
        <stop offset="100%" :stop-color="darken(cfg.eyeColor, 26)" />
      </radialGradient>
      <!-- 光晕 -->
      <radialGradient :id="gid('halo')" cx="50%" cy="50%" r="50%">
        <stop offset="0%" stop-color="#a97bff" stop-opacity=".34" />
        <stop offset="100%" stop-color="#a97bff" stop-opacity="0" />
      </radialGradient>
      <clipPath :id="gid('faceClip')">
        <path :d="facePath" />
      </clipPath>
    </defs>

    <!-- 背后光晕 + 法阵 -->
    <circle :cx="cx" cy="200" r="150" :fill="`url(#${gid('halo')})`" />
    <g opacity=".5" stroke="#ffd98a" fill="none" stroke-width="1">
      <circle :cx="cx" cy="196" r="128" stroke-dasharray="3 9" opacity=".5" />
      <circle :cx="cx" cy="196" r="112" opacity=".25" />
    </g>
    <g :fill="cfg.robeColor" opacity=".55">
      <circle :cx="cx" cy="68" r="2.5" />
      <circle cx="46" cy="150" r="2" />
      <circle cx="314" cy="150" r="2" />
      <circle cx="70" cy="300" r="2" />
      <circle cx="290" cy="300" r="2" />
    </g>

    <!-- 衣袍 / 肩颈 -->
    <g>
      <!-- 后领 -->
      <path :d="`M ${cx - 96} ${H} Q ${cx - 88} 300 ${cx - 52} 282 L ${cx + 52} 282 Q ${cx + 88} 300 ${cx + 96} ${H} Z`" :fill="`url(#${gid('robeDark')})`" />
      <!-- 脖颈 -->
      <path :d="`M ${cx - 17} 232 L ${cx - 17} 272 Q ${cx} 288 ${cx + 17} 272 L ${cx + 17} 232 Z`" :fill="darken(cfg.skin, 10)" />
      <path :d="`M ${cx - 17} 232 L ${cx - 17} 258 Q ${cx} 270 ${cx + 17} 258 L ${cx + 17} 232 Z`" :fill="darken(cfg.skin, 22)" opacity=".45" />
      <!-- 主袍 -->
      <path :d="`M ${cx - 92} ${H} Q ${cx - 84} 298 ${cx - 50} 280 Q ${cx - 24} 300 ${cx} 306 Q ${cx + 24} 300 ${cx + 50} 280 Q ${cx + 84} 298 ${cx + 92} ${H} Z`" :fill="`url(#${gid('robe')})`" />
      <!-- 交领 -->
      <path :d="`M ${cx - 50} 280 Q ${cx - 18} 300 ${cx} 306 L ${cx - 6} 330 Q ${cx - 40} 316 ${cx - 66} 292 Z`" fill="#fff" opacity=".82" />
      <path :d="`M ${cx + 50} 280 Q ${cx + 18} 300 ${cx} 306 L ${cx + 6} 330 Q ${cx + 40} 316 ${cx + 66} 292 Z`" fill="#fff" opacity=".82" />
      <!-- 腰带 -->
      <path :d="`M ${cx - 78} 344 Q ${cx} 356 ${cx + 78} 344 L ${cx + 76} 360 Q ${cx} 372 ${cx - 76} 360 Z`" :fill="darken(cfg.robeColor, 32)" />
      <path :d="`M ${cx - 20} 347 L ${cx + 20} 347 L ${cx + 16} 366 L ${cx - 16} 366 Z`" fill="#ffd98a" opacity=".85" />
      <!-- 衣纹 -->
      <g stroke="#fff" stroke-opacity=".14" stroke-width="2" fill="none">
        <path :d="`M ${cx - 60} 300 Q ${cx - 72} 340 ${cx - 68} ${H}`" />
        <path :d="`M ${cx + 60} 300 Q ${cx + 72} 340 ${cx + 68} ${H}`" />
      </g>
    </g>

    <!-- 后层头发（在脸后面） -->
    <g v-if="cfg.hairStyle === 'long' || cfg.hairStyle === 'twin'">
      <path :d="`M ${cx - 60} 132 Q ${cx - 96} 210 ${cx - 78} 330 Q ${cx - 50} 348 ${cx - 44} 300 Q ${cx - 58} 210 ${cx - 48} 150 Z`" :fill="`url(#${gid('hair')})`" />
      <path :d="`M ${cx + 60} 132 Q ${cx + 96} 210 ${cx + 78} 330 Q ${cx + 50} 348 ${cx + 44} 300 Q ${cx + 58} 210 ${cx + 48} 150 Z`" :fill="`url(#${gid('hair')})`" />
    </g>

    <!-- 脸型 -->
    <path :d="facePath" :fill="`url(#${gid('skin')})`" />
    <path :d="facePath" :fill="`url(#${gid('faceGlow')})`" />
    <!-- 耳 -->
    <ellipse :cx="cx - faceW - 2" cy="176" rx="9" ry="15" :fill="darken(cfg.skin, 8)" />
    <ellipse :cx="cx + faceW + 2" cy="176" rx="9" ry="15" :fill="darken(cfg.skin, 8)" />

    <!-- 五官（裁剪在脸内） -->
    <g :clip-path="`url(#${gid('faceClip')})`">
      <!-- 腮红 -->
      <g v-if="cfg.blush" :fill="cfg.gender === 'male' ? '#d98b7a' : '#f28f9b'" opacity=".28">
        <ellipse :cx="cx - eyeSp + 4" cy="204" rx="17" ry="9" />
        <ellipse :cx="cx + eyeSp - 4" cy="204" rx="17" ry="9" />
      </g>
      <!-- 眉 -->
      <g :fill="cfg.browColor">
        <path :d="browPath(-1)" />
        <path :d="browPath(1)" />
      </g>
      <!-- 眼 -->
      <g>
        <g v-for="side in [-1, 1]" :key="side">
          <ellipse :cx="cx + side * eyeSp" cy="186" :rx="eyeRX" :ry="eyeRY" fill="#fff" />
          <ellipse :cx="cx + side * eyeSp" cy="186" :rx="eyeRX * .74" :ry="eyeRY * .96" :fill="`url(#${gid('iris')})`" />
          <ellipse :cx="cx + side * eyeSp" cy="186" rx="3.4" ry="4.6" fill="#160f22" />
          <circle :cx="cx + side * eyeSp - 2.6" cy="182" r="2" fill="#fff" />
          <circle :cx="cx + side * eyeSp + 2.4" cy="189" r="1" fill="#fff" opacity=".7" />
          <!-- 上眼睑线 -->
          <path :d="lidPath(side)" stroke="#2a1c2e" stroke-width="2.2" fill="none" stroke-linecap="round" />
          <!-- 睫毛 -->
          <path v-if="cfg.gender === 'female'" :d="lashPath(side)" stroke="#2a1c2e" stroke-width="1.6" fill="none" stroke-linecap="round" />
        </g>
      </g>
      <!-- 鼻 -->
      <g :stroke="darken(cfg.skin, 26)" stroke-width="1.8" fill="none" stroke-linecap="round" opacity=".8">
        <path v-if="cfg.noseStyle === 'straight'" :d="`M ${cx} 178 L ${cx - 2} 200 Q ${cx} 205 ${cx + 4} 202`" />
        <path v-else :d="`M ${cx} 182 Q ${cx - 5} 198 ${cx + 2} 200`" />
      </g>
      <ellipse v-if="cfg.noseStyle === 'straight'" :cx="cx + 2" cy="204" rx="3" ry="1.8" :fill="darken(cfg.skin, 30)" opacity=".5" />
      <!-- 嘴 -->
      <path :d="mouthPath" :fill="cfg.mouthStyle === 'charming' ? '#c62f45' : 'none'"
            :stroke="cfg.mouthStyle === 'charming' ? darken('#c62f45', 18) : darken(cfg.skin, 40)"
            stroke-width="2.2" stroke-linecap="round" />
      <path v-if="cfg.mouthStyle === 'smile'" :d="`M ${cx - 6} 230 Q ${cx} 236 ${cx + 6} 230`" stroke="#fff" stroke-opacity=".5" stroke-width="1.2" fill="none" />
      <!-- 额间标记 -->
      <g v-if="cfg.mark !== 'none'">
        <path v-if="cfg.mark === 'red'" :d="`M ${cx} 156 l 5 6 -5 6 -5 -6 Z`" fill="#e0364f" opacity=".9" />
        <g v-else stroke="#ffd98a" stroke-width="1.6" fill="none" opacity=".9">
          <path :d="`M ${cx} 154 l 5 8 -5 8 -5 -8 Z`" />
          <circle :cx="cx" cy="162" r="1.6" fill="#ffd98a" />
        </g>
      </g>
    </g>

    <!-- 前层头发 / 刘海 -->
    <g :fill="`url(#${gid('hair')})`">
      <!-- 主刘海 -->
      <path :d="bangsPath" />
      <!-- 发丝高光 -->
      <path :d="bangsHighlight" fill="none" :stroke="lighten(cfg.hairColor, 38)" stroke-width="1.5" opacity=".4" />
      <!-- 两侧鬓发 -->
      <path :d="`M ${cx - faceW - 6} 146 Q ${cx - faceW - 16} 196 ${cx - faceW - 4} 236 Q ${cx - faceW + 2} 208 ${cx - faceW + 4} 166 Z`" />
      <path :d="`M ${cx + faceW + 6} 146 Q ${cx + faceW + 16} 196 ${cx + faceW + 4} 236 Q ${cx + faceW - 2} 208 ${cx + faceW - 4} 166 Z`" />
    </g>

    <!-- 马尾 / 发髻 / 双髻 -->
    <g v-if="cfg.hairStyle === 'ponytail'" :fill="`url(#${gid('hair')})`">
      <path :d="`M ${cx + 40} 118 Q ${cx + 96} 130 ${cx + 92} 214 Q ${cx + 76} 250 ${cx + 60} 236 Q ${cx + 82} 178 ${cx + 44} 146 Z`" />
    </g>
    <g v-if="cfg.hairStyle === 'bun'" :fill="`url(#${gid('hair')})`">
      <ellipse :cx="cx" cy="86" rx="30" ry="26" />
      <path d="" />
    </g>
    <g v-if="cfg.hairStyle === 'twin'" :fill="`url(#${gid('hair')})`">
      <circle :cx="cx - 66" cy="112" r="26" />
      <circle :cx="cx + 66" cy="112" r="26" />
    </g>

    <!-- 头饰 -->
    <g v-if="cfg.accessory === 'hairpin'">
      <path :d="`M ${cx + 34} 118 L ${cx + 74} 96`" stroke="#ffd98a" stroke-width="3" stroke-linecap="round" />
      <circle :cx="cx + 75" cy="95" r="5" fill="#ffd98a" />
      <circle :cx="cx + 75" cy="95" r="2" fill="#fff" />
    </g>
    <g v-if="cfg.accessory === 'flower'">
      <g :transform="`translate(${cx - 44}, 132)`" fill="#f28f9b">
        <ellipse cx="0" cy="-8" rx="5" ry="9" />
        <ellipse cx="0" cy="-8" rx="5" ry="9" transform="rotate(72)" />
        <ellipse cx="0" cy="-8" rx="5" ry="9" transform="rotate(144)" />
        <ellipse cx="0" cy="-8" rx="5" ry="9" transform="rotate(216)" />
        <ellipse cx="0" cy="-8" rx="5" ry="9" transform="rotate(288)" />
        <circle cx="0" cy="0" r="4" fill="#ffd98a" />
      </g>
    </g>
  </svg>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  config: { type: Object, required: true },
  instanceId: { type: String, default: 'main' }
})

const W = 360
const H = 400
const cx = W / 2
const uid = computed(() => props.instanceId)
function gid (name) { return `${name}-${uid.value}` }

const cfg = computed(() => props.config)
const faceW = computed(() => cfg.value.faceWidth)
const eyeSp = computed(() => cfg.value.eyeSpacing)
const eyeRX = computed(() => 13 * cfg.value.eyeSize)
const eyeRY = computed(() => (cfg.value.eyeStyle === 'round' ? 11 : cfg.value.eyeStyle === 'phoenix' ? 6.5 : 8.5) * cfg.value.eyeSize)

// 脸型：柔和鹅蛋脸 + 收窄尖下巴，额头略窄、颧骨最宽、下颌内收
const facePath = computed(() => {
  const w = faceW.value
  const chin = cfg.value.chinWidth
  const top = 112
  const cheekY = 168
  const jawY = 212
  const bottom = 252
  // 下巴收窄控制：chin 越小越尖（控制点越靠中线）
  const chinCtl = 8 + chin * 0.55
  return `M ${cx - w + 6} ${top + 12}
    C ${cx - w} ${top - 4}, ${cx - w - 2} ${cheekY - 24}, ${cx - w + 2} ${cheekY}
    C ${cx - w + 5} ${jawY - 4}, ${cx - chinCtl} ${jawY + 14}, ${cx} ${bottom}
    C ${cx + chinCtl} ${jawY + 14}, ${cx + w - 5} ${jawY - 4}, ${cx + w - 2} ${cheekY}
    C ${cx + w + 2} ${cheekY - 24}, ${cx + w} ${top - 4}, ${cx + w - 6} ${top + 12}
    Q ${cx} ${top - 8}, ${cx - w + 6} ${top + 12} Z`
})

function browPath (side) {
  const x = cx + side * eyeSp
  const y = 166
  const style = cfg.value.browStyle
  let d
  if (style === 'sharp') {
    d = `M ${x - side * 16} ${y + 4} Q ${x} ${y - 8} ${x + side * 16} ${y - 2} L ${x + side * 15} ${y + 2} Q ${x} ${y - 3} ${x - side * 15} ${y + 7} Z`
  } else if (style === 'flat') {
    d = `M ${x - side * 16} ${y + 1} Q ${x} ${y - 3} ${x + side * 16} ${y + 1} L ${x + side * 16} ${y + 5} Q ${x} ${y + 1} ${x - side * 16} ${y + 5} Z`
  } else {
    d = `M ${x - side * 16} ${y + 4} Q ${x} ${y - 7} ${x + side * 16} ${y + 2} Q ${x} ${y - 1} ${x - side * 16} ${y + 7} Z`
  }
  return d
}

function lidPath (side) {
  const x = cx + side * eyeSp
  const rx = eyeRX.value
  const ry = eyeRY.value
  const lift = cfg.value.eyeStyle === 'phoenix' ? 2 : 0
  return `M ${x - rx} ${186 - lift} Q ${x} ${186 - ry - 3 - lift} ${x + rx} ${186 - lift}`
}
function lashPath (side) {
  const x = cx + side * eyeSp
  const rx = eyeRX.value
  return `M ${x + side * rx} ${184} l ${side * 6} -4 M ${x + side * (rx - 4)} ${181} l ${side * 5} -5`
}

const mouthPath = computed(() => {
  const y = 234
  const style = cfg.value.mouthStyle
  if (style === 'charming') {
    return `M ${cx - 9} ${y} Q ${cx - 3} ${y + 3} ${cx} ${y + 6} Q ${cx + 3} ${y + 3} ${cx + 9} ${y} Q ${cx} ${y + 2} ${cx - 9} ${y} Z`
  }
  if (style === 'calm') {
    return `M ${cx - 7} ${y + 2} Q ${cx} ${y + 1} ${cx + 7} ${y + 2}`
  }
  return `M ${cx - 9} ${y + 1} Q ${cx} ${y + 9} ${cx + 9} ${y + 1}`
})

const bangsPath = computed(() => {
  const w = faceW.value
  const style = cfg.value.hairStyle
  const top = 118
  if (style === 'short') {
    return `M ${cx - w - 2} ${top + 6} Q ${cx - w - 4} ${top - 42} ${cx} ${top - 44} Q ${cx + w + 4} ${top - 42} ${cx + w + 2} ${top + 6}
      Q ${cx + w - 10} ${top - 16} ${cx + 18} ${top - 14} Q ${cx + 8} ${top + 8} ${cx - 2} ${top - 12}
      Q ${cx - 22} ${top + 10} ${cx - w + 6} ${top - 14} Z`
  }
  if (style === 'bun') {
    return `M ${cx - w - 2} ${top + 6} Q ${cx - w - 4} ${top - 40} ${cx} ${top - 42} Q ${cx + w + 4} ${top - 40} ${cx + w + 2} ${top + 6}
      Q ${cx + w - 12} ${top - 14} ${cx + 12} ${top - 12} Q ${cx} ${top + 6} ${cx - 14} ${top - 12}
      Q ${cx - w + 10} ${top - 14} ${cx - w - 2} ${top + 6} Z`
  }
  // 长发/马尾/双髻：中分长刘海，露出额头
  return `M ${cx - w - 2} ${top + 10} Q ${cx - w - 4} ${top - 46} ${cx} ${top - 48} Q ${cx + w + 4} ${top - 46} ${cx + w + 2} ${top + 10}
    Q ${cx + w - 6} ${top - 18} ${cx + 28} ${top - 16} Q ${cx + 16} ${top + 6} ${cx + 6} ${top - 6}
    Q ${cx + 2} ${top - 12} ${cx - 2} ${top - 8} Q ${cx - 16} ${top + 8} ${cx - 28} ${top - 16}
    Q ${cx - w + 6} ${top - 18} ${cx - w - 2} ${top + 10} Z`
})

const bangsHighlight = computed(() => {
  const w = faceW.value
  return `M ${cx - w + 12} 96 Q ${cx - 20} 80 ${cx + 4} 92 M ${cx + 12} 90 Q ${cx + 34} 82 ${cx + 46} 98`
})

// 颜色工具：hex 明暗调整
function hexToRgb (hex) {
  const h = hex.replace('#', '')
  const n = parseInt(h.length === 3 ? h.split('').map(c => c + c).join('') : h, 16)
  return { r: (n >> 16) & 255, g: (n >> 8) & 255, b: n & 255 }
}
function rgbToHex (r, g, b) {
  return '#' + [r, g, b].map(v => Math.max(0, Math.min(255, Math.round(v))).toString(16).padStart(2, '0')).join('')
}
function lighten (hex, amt) {
  const { r, g, b } = hexToRgb(hex)
  return rgbToHex(r + (255 - r) * amt / 100, g + (255 - g) * amt / 100, b + (255 - b) * amt / 100)
}
function darken (hex, amt) {
  const { r, g, b } = hexToRgb(hex)
  return rgbToHex(r * (1 - amt / 100), g * (1 - amt / 100), b * (1 - amt / 100))
}
</script>

<style scoped>
.avatar-figure{display:block;width:100%;height:100%;overflow:visible;filter:drop-shadow(0 18px 34px rgba(0,0,0,.55));}
</style>

<template>
  <canvas ref="cv" id="particles"></canvas>
</template>

<script setup>
import { onMounted, onBeforeUnmount, ref, watch } from 'vue'
import { theme, getFireRGB } from '../composables/useTheme'

const cv = ref(null)
let ctx, W = 0, H = 0
const DPR = Math.min(window.devicePixelRatio || 1, 2)
let embers = [], petals = [], fireRGB = getFireRGB()
let rafId = null, t = 0

function newEmber (init) {
  return {
    x: Math.random() * W, y: init ? Math.random() * H : H + Math.random() * 60,
    r: 0.7 + Math.random() * 2, vy: 0.16 + Math.random() * 0.5, ph: Math.random() * Math.PI * 2,
    amp: 0.25 + Math.random() * 0.8, life: 0, max: 320 + Math.random() * 380, a: 0.22 + Math.random() * 0.55
  }
}
function newPetal (init) {
  return {
    x: Math.random() * W, y: init ? Math.random() * H : -40 - Math.random() * 80,
    s: 5 + Math.random() * 8, vy: 0.35 + Math.random() * 0.6, ph: Math.random() * Math.PI * 2,
    rot: Math.random() * Math.PI * 2, vr: (Math.random() - 0.5) * 0.02, amp: 0.5 + Math.random() * 1.1, a: 0.16 + Math.random() * 0.3
  }
}

function resize () {
  W = window.innerWidth; H = window.innerHeight
  cv.value.width = W * DPR; cv.value.height = H * DPR
  cv.value.style.width = W + 'px'; cv.value.style.height = H + 'px'
  ctx.setTransform(DPR, 0, 0, DPR, 0, 0)
  const nE = Math.round(Math.min(110, Math.max(40, W / 14)))
  const nP = Math.round(Math.min(26, Math.max(10, W / 60)))
  embers = []; petals = []
  for (let i = 0; i < nE; i++) embers.push(newEmber(true))
  for (let j = 0; j < nP; j++) petals.push(newPetal(true))
}

function loop () {
  t += 0.016
  ctx.clearRect(0, 0, W, H)
  ctx.globalCompositeOperation = 'lighter'
  for (let i = 0; i < embers.length; i++) {
    const p = embers[i]
    p.life++; p.y -= p.vy; p.ph += 0.012; p.x += Math.sin(p.ph) * p.amp * 0.5
    const fade = 1 - Math.abs(p.life / p.max - 0.5) * 2
    const alpha = Math.max(0, p.a * fade)
    const g = ctx.createRadialGradient(p.x, p.y, 0, p.x, p.y, p.r * 4)
    g.addColorStop(0, `rgba(${Math.min(255, fireRGB[0] + 60)},${Math.min(255, fireRGB[1] + 40)},${fireRGB[2]},${alpha})`)
    g.addColorStop(0.45, `rgba(${fireRGB[0]},${fireRGB[1]},${fireRGB[2]},${alpha * 0.32})`)
    g.addColorStop(1, `rgba(${fireRGB[0]},${fireRGB[1]},${fireRGB[2]},0)`)
    ctx.fillStyle = g
    ctx.beginPath(); ctx.arc(p.x, p.y, p.r * 4, 0, Math.PI * 2); ctx.fill()
    if (p.y < -50 || p.life > p.max) embers[i] = newEmber(false)
  }
  ctx.globalCompositeOperation = 'source-over'
  for (let k = 0; k < petals.length; k++) {
    const q = petals[k]
    q.ph += 0.01; q.y += q.vy; q.x += Math.sin(q.ph) * q.amp; q.rot += q.vr
    const edge = Math.min(q.y / (H * 0.25), 1, (H - q.y) / (H * 0.25), 1)
    const pa = Math.max(0, q.a * Math.max(0, edge))
    ctx.save()
    ctx.translate(q.x, q.y); ctx.rotate(q.rot); ctx.scale(1, 0.62)
    const pg = ctx.createLinearGradient(-q.s, -q.s, q.s, q.s)
    pg.addColorStop(0, `rgba(${fireRGB[0]},${fireRGB[1]},${fireRGB[2]},${pa})`)
    pg.addColorStop(1, `rgba(255,217,138,${pa * 0.75})`)
    ctx.fillStyle = pg
    ctx.beginPath()
    ctx.moveTo(0, -q.s)
    ctx.bezierCurveTo(q.s * 0.9, -q.s * 0.5, q.s * 0.7, q.s * 0.6, 0, q.s)
    ctx.bezierCurveTo(-q.s * 0.7, q.s * 0.6, -q.s * 0.9, -q.s * 0.5, 0, -q.s)
    ctx.fill()
    ctx.restore()
    if (q.y > H + 60) petals[k] = newPetal(false)
  }
  rafId = requestAnimationFrame(loop)
}

watch(theme, () => { fireRGB = getFireRGB() })

onMounted(() => {
  ctx = cv.value.getContext('2d')
  resize()
  window.addEventListener('resize', resize)
  rafId = requestAnimationFrame(loop)
})
onBeforeUnmount(() => {
  cancelAnimationFrame(rafId)
  window.removeEventListener('resize', resize)
})
</script>

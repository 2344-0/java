import { ref } from 'vue'

// 骰坊音效：使用 Web Audio API 实时合成，无需外部音频文件
const SFX_KEY = 'doupo_dice_sfx'
function loadEnabled () {
  try {
    const v = localStorage.getItem(SFX_KEY)
    return v === null ? true : v === '1'
  } catch (e) {
    return true
  }
}

export const sfxEnabled = ref(loadEnabled())

export function setSfxEnabled (v) {
  sfxEnabled.value = !!v
  try { localStorage.setItem(SFX_KEY, sfxEnabled.value ? '1' : '0') } catch (e) {}
}

export function toggleSfx () { setSfxEnabled(!sfxEnabled.value) }

let ctx = null
function getCtx () {
  if (typeof window === 'undefined') return null
  const AC = window.AudioContext || window.webkitAudioContext
  if (!AC) return null
  if (!ctx) ctx = new AC()
  if (ctx.state === 'suspended') ctx.resume()
  return ctx
}

// 单个音（可带滑音）
function tone (c, freq, start, dur, type = 'sine', vol = 0.12, endFreq) {
  const osc = c.createOscillator()
  const gain = c.createGain()
  osc.type = type
  osc.frequency.setValueAtTime(freq, start)
  if (endFreq) osc.frequency.exponentialRampToValueAtTime(endFreq, start + dur)
  gain.gain.setValueAtTime(0.0001, start)
  gain.gain.exponentialRampToValueAtTime(vol, start + 0.012)
  gain.gain.exponentialRampToValueAtTime(0.0001, start + dur)
  osc.connect(gain).connect(c.destination)
  osc.start(start)
  osc.stop(start + dur + 0.03)
}

// 噪声：模拟骰子碰撞
function noise (c, start, dur, vol = 0.06) {
  const len = Math.floor(c.sampleRate * dur)
  const buf = c.createBuffer(1, len, c.sampleRate)
  const data = buf.getChannelData(0)
  for (let i = 0; i < len; i++) data[i] = Math.random() * 2 - 1
  const src = c.createBufferSource()
  src.buffer = buf
  const gain = c.createGain()
  gain.gain.setValueAtTime(vol, start)
  gain.gain.exponentialRampToValueAtTime(0.0001, start + dur)
  src.connect(gain).connect(c.destination)
  src.start(start)
}

function ready () { return sfxEnabled.value ? getCtx() : null }

export function playRoll () {
  const c = ready(); if (!c) return
  const t = c.currentTime
  for (let i = 0; i < 6; i++) {
    noise(c, t + i * 0.09, 0.05, 0.055)
    tone(c, 180 + Math.random() * 140, t + i * 0.09, 0.05, 'triangle', 0.04)
  }
}

export function playWin () {
  const c = ready(); if (!c) return
  const t = c.currentTime
  ;[523.25, 659.25, 783.99, 1046.5].forEach((f, i) => tone(c, f, t + i * 0.1, 0.24, 'sine', 0.11))
}

export function playLose () {
  const c = ready(); if (!c) return
  const t = c.currentTime
  ;[392, 311.13, 261.63].forEach((f, i) => tone(c, f, t + i * 0.12, 0.3, 'sine', 0.1))
}

export function playDraw () {
  const c = ready(); if (!c) return
  const t = c.currentTime
  tone(c, 440, t, 0.18, 'sine', 0.09)
  tone(c, 440, t + 0.16, 0.24, 'sine', 0.09)
}

export function playClaim () {
  const c = ready(); if (!c) return
  const t = c.currentTime
  tone(c, 880, t, 0.12, 'sine', 0.1)
  tone(c, 1318.5, t + 0.1, 0.32, 'sine', 0.1)
}

export function playClick () {
  const c = ready(); if (!c) return
  tone(c, 620, c.currentTime, 0.05, 'triangle', 0.05)
}

// 突破成功：上行音阶 + 结尾轰鸣，气势十足
export function playBreakthrough () {
  const c = ready(); if (!c) return
  const t = c.currentTime
  ;[392, 523.25, 659.25, 783.99, 1046.5, 1318.5].forEach((f, i) =>
    tone(c, f, t + i * 0.09, 0.3, 'triangle', 0.1)
  )
  tone(c, 130.81, t + 0.5, 0.7, 'sawtooth', 0.08)
  tone(c, 261.63, t + 0.55, 0.6, 'sine', 0.07)
}

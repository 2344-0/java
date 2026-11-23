<template>
  <div class="rank-page cultivation-page">
    <section class="section">
      <div class="section-head">
        <h2><span class="spark"></span>修炼阁 · 焚决炼气</h2>
        <span class="count">斗气存量 <b class="qi-amount">{{ qi }}</b></span>
      </div>
      <p class="lead">斗破苍穹修炼之途：斗之气十段圆满，凝聚斗之气旋晋入斗者；此后斗者至斗帝，每境九星。打坐纳气，异火加持，功法相伴，方登巅峰。</p>

      <!-- 当前境界 主卡 -->
      <div class="realm-hero" :style="{ '--realm-color': currentRealm.color }">
        <div class="rh-glow"></div>
        <div class="rh-main">
          <div class="rh-rings">
            <div class="ring r1"></div><div class="ring r2"></div><div class="ring r3"></div>
            <div class="core" :class="{ active: meditating }">
              <span class="core-text">{{ meditating ? '炼' : '气' }}</span>
            </div>
          </div>
          <div class="rh-info">
            <span class="rh-tag">当 前 境 界</span>
            <h2 class="rh-name">{{ realmLabel }}</h2>
            <p class="rh-ability">{{ currentRealm.ability }}</p>
            <div class="rh-meta">
              <span>功法 · <b>{{ gongfaName }}</b></span>
              <span>异火 · <b>{{ currentFire.name }}</b></span>
            </div>
          </div>
        </div>

        <!-- 突破进度 -->
        <div class="breakthrough">
          <div class="bt-head">
            <span>突破进度</span>
            <b>{{ qi }} / {{ breakthroughCost }}</b>
          </div>
          <div class="bt-bar"><div class="bt-fill" :style="{ width: (realmProgress * 100) + '%' }"></div></div>
          <div class="bt-actions">
            <button class="meditate-btn" :class="{ active: meditating }" @click="onMeditate">
              <span class="mb-ic">{{ meditating ? '止' : '坐' }}</span>
              {{ meditating ? '收功' : '打坐修炼' }}
            </button>
            <button class="break-btn" :disabled="!canBreakthrough" @click="onBreakthrough">
              {{ isMaxRealm ? '已至斗帝巅峰' : '突 破' }}
            </button>
          </div>
          <div class="bt-hint">
            <template v-if="meditating">打坐中 · 每秒纳气 <b>+{{ meditateGain }}</b></template>
            <template v-else-if="canBreakthrough">斗气已满，可冲击下一境界！</template>
            <template v-else>还需 <b>{{ breakthroughCost - qi }}</b> 斗气方可突破</template>
          </div>
        </div>
      </div>

      <!-- 突破特效横幅 -->
      <transition name="bt-burst">
        <div v-if="showBurst" class="burst-banner">
          <span class="burst-star">✦</span>
          <div><b>突破成功！</b><span>{{ lastBreakthrough.from }} → {{ lastBreakthrough.to }}</span></div>
          <span class="burst-star">✦</span>
        </div>
      </transition>

      <!-- 异火加持 -->
      <div class="section-block">
        <div class="section-head" style="margin-bottom:14px;">
          <h2 style="font-size:18px;"><span class="spark"></span>异火加持</h2>
          <span class="count">炼化异火可加速纳气</span>
        </div>
        <div class="fire-grid">
          <button
            v-for="f in FIRE_OPTIONS"
            :key="f.key"
            class="fire-option"
            :class="{ on: fire === f.key }"
            @click="onSelectFire(f)"
          >
            <span class="fo-fire">✦</span>
            <b>{{ f.name }}</b>
            <span class="fo-rank">{{ f.rank }}</span>
            <span class="fo-mul">纳气 ×{{ f.mul }}</span>
            <small>{{ f.desc }}</small>
          </button>
        </div>
      </div>

      <!-- 境界阶梯 -->
      <div class="section-block">
        <div class="section-head" style="margin-bottom:14px;">
          <h2 style="font-size:18px;"><span class="spark"></span>修炼之途</h2>
          <span class="count">大境界 {{ realmIndex + 1 }} / {{ REALMS.length }}</span>
        </div>
        <div class="realm-ladder">
          <div
            v-for="(r, i) in REALMS"
            :key="r.key"
            class="ladder-node"
            :class="{ done: i < realmIndex, current: i === realmIndex, locked: i > realmIndex }"
          >
            <span class="ln-dot">{{ i < realmIndex ? '✓' : i === realmIndex ? level : '·' }}</span>
            <span class="ln-name">{{ r.name }}</span>
            <span class="ln-unit">{{ r.unit }}</span>
          </div>
        </div>
      </div>

      <!-- 与掷骰坊联动说明 -->
      <div class="link-note">
        <span class="ln-ic">⚔</span>
        <div>
          <b>斗气相通 · 境界加持</b>
          <p>此处打坐所纳斗气，与「掷骰坊」通用。当前境界为 <b>{{ realmLabel }}</b>，掷骰彩头额外加成 <b>+{{ Math.round((realmBonus - 1) * 100) }}%</b> —— 修为越高，赌运越盛。</p>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup>
import { ref, onBeforeUnmount } from 'vue'
import { showToast } from '../composables/useToast'
import { playClick, playBreakthrough } from '../composables/useSfx'
import {
  REALMS, FIRE_OPTIONS, qi, realmIndex, level, fire,
  meditating, lastBreakthrough, currentRealm, realmLabel, currentFire,
  breakthroughCost, meditateGain, realmProgress, canBreakthrough,
  isMaxRealm, realmBonus, gongfaName,
  toggleMeditate, selectFire, breakthrough
} from '../composables/useCultivation'

const showBurst = ref(false)
let burstTimer = null

function onMeditate () {
  toggleMeditate()
  playClick()
  if (meditating.value) showToast('盘膝打坐，纳天地斗气入体', '✦')
  else showToast('收功，斗气已归入气海', '✦')
}

function onSelectFire (f) {
  if (fire.value === f.key) return
  selectFire(f.key)
  playClick()
  showToast('已炼化 · ' + f.name + '，纳气速度 ×' + f.mul, '✦')
}

function onBreakthrough () {
  const result = breakthrough()
  if (!result) return
  playBreakthrough()
  showBurst.value = true
  clearTimeout(burstTimer)
  burstTimer = setTimeout(() => { showBurst.value = false }, 2600)
  showToast('突破成功！晋入 ' + result.to, '✦')
}

onBeforeUnmount(() => clearTimeout(burstTimer))
</script>

<style scoped>
.cultivation-page{position:relative;z-index:2;}
.lead{font-family:var(--serif);font-size:14px;letter-spacing:.12em;color:#b9b0cc;margin:-6px 0 22px;text-shadow:0 2px 14px rgba(0,0,0,.7);}
.count{font-size:12px;color:var(--muted);letter-spacing:.1em;}
.count .qi-amount{color:var(--rune);font-size:15px;font-family:var(--serif);}

/* 境界主卡 */
.realm-hero{position:relative;overflow:hidden;border-radius:20px;padding:26px;margin-bottom:22px;
  background:radial-gradient(110% 120% at 18% 30%, color-mix(in srgb, var(--realm-color) 16%, transparent), transparent 60%), linear-gradient(160deg, rgba(24,19,38,.86), rgba(8,6,15,.94));
  border:1px solid var(--line-strong);box-shadow:0 40px 80px -40px rgba(0,0,0,.9), 0 0 60px -30px var(--glow);}
.realm-hero::before,.realm-hero::after{content:"";position:absolute;width:26px;height:26px;pointer-events:none;border:1.5px solid var(--rune);opacity:.55;}
.realm-hero::before{top:-1px;left:-1px;border-right:0;border-bottom:0;border-radius:20px 0 0 0;}
.realm-hero::after{bottom:-1px;right:-1px;border-left:0;border-top:0;border-radius:0 0 20px 0;}
.rh-glow{position:absolute;left:8%;top:50%;width:280px;height:280px;transform:translateY(-50%);border-radius:50%;
  background:radial-gradient(circle, color-mix(in srgb, var(--realm-color) 30%, transparent), transparent 65%);filter:blur(40px);opacity:.7;}
.rh-main{position:relative;display:flex;align-items:center;gap:28px;}

/* 气旋 */
.rh-rings{position:relative;width:130px;height:130px;flex:none;display:grid;place-items:center;}
.ring{position:absolute;border-radius:50%;border:1px solid var(--realm-color);opacity:.5;}
.r1{inset:0;border-style:dashed;animation:spin 18s linear infinite;}
.r2{inset:14px;border-width:2px;opacity:.7;animation:spin 12s linear infinite reverse;}
.r3{inset:28px;border-style:dotted;animation:spin 8s linear infinite;}
.core{width:56px;height:56px;border-radius:50%;display:grid;place-items:center;z-index:1;
  background:radial-gradient(circle at 35% 30%, #fff8e8, var(--realm-color) 75%);
  box-shadow:0 0 30px -4px var(--realm-color), inset 0 0 12px rgba(255,255,255,.7);transition:transform .3s;}
.core.active{animation:breath 1.6s ease-in-out infinite;}
.core-text{font-family:var(--serif);font-size:26px;font-weight:700;color:#140a22;}
@keyframes spin{to{transform:rotate(360deg)}}
@keyframes breath{0%,100%{transform:scale(1)}50%{transform:scale(1.14)}}

.rh-info{flex:1;min-width:0;}
.rh-tag{font-size:11px;letter-spacing:.32em;color:var(--muted);}
.rh-name{font-family:var(--serif);font-size:42px;font-weight:700;letter-spacing:.1em;margin:4px 0 6px;line-height:1.1;
  color:var(--realm-color);text-shadow:0 0 26px color-mix(in srgb, var(--realm-color) 55%, transparent);}
.rh-ability{font-family:var(--serif);font-size:13px;letter-spacing:.08em;color:#cfc6e2;}
.rh-meta{display:flex;flex-wrap:wrap;gap:16px;margin-top:14px;font-size:12px;color:var(--muted);letter-spacing:.06em;}
.rh-meta b{color:var(--rune);font-weight:400;}

/* 突破进度 */
.breakthrough{position:relative;margin-top:22px;padding-top:20px;border-top:1px solid var(--line);}
.bt-head{display:flex;justify-content:space-between;align-items:baseline;font-size:12.5px;letter-spacing:.1em;color:var(--muted);}
.bt-head b{font-family:var(--serif);font-size:15px;color:var(--rune);}
.bt-bar{height:12px;border-radius:99px;margin:10px 0 16px;overflow:hidden;background:rgba(255,255,255,.06);border:1px solid var(--line);}
.bt-fill{height:100%;border-radius:99px;background:linear-gradient(90deg,var(--fire-2),var(--realm-color),var(--rune));
  box-shadow:0 0 18px -3px var(--glow);transition:width .5s cubic-bezier(.4,0,.2,1);}
.bt-actions{display:flex;gap:12px;flex-wrap:wrap;}
.meditate-btn,.break-btn{height:44px;padding:0 22px;border-radius:11px;cursor:pointer;transition:.25s;
  font-family:var(--serif);font-size:15px;font-weight:700;letter-spacing:.1em;display:flex;align-items:center;gap:9px;}
.meditate-btn{color:var(--text);background:rgba(255,255,255,.05);border:1px solid var(--line-strong);}
.meditate-btn:hover{background:rgba(255,255,255,.1);transform:translateY(-2px);}
.meditate-btn.active{color:#140a22;background:linear-gradient(140deg,var(--cyan),#2a8bff);border-color:transparent;box-shadow:0 0 24px -6px var(--cyan);}
.mb-ic{width:24px;height:24px;border-radius:7px;display:grid;place-items:center;font-size:13px;background:rgba(255,255,255,.12);}
.break-btn{border:0;color:#140a22;background:linear-gradient(140deg,var(--rune),var(--realm-color));box-shadow:0 0 26px -6px var(--realm-color);}
.break-btn:hover:not(:disabled){transform:translateY(-2px);box-shadow:0 0 34px -4px var(--realm-color);}
.break-btn:disabled{opacity:.4;cursor:not-allowed;box-shadow:none;}
.bt-hint{margin-top:12px;font-family:var(--serif);font-size:12px;letter-spacing:.06em;color:var(--muted);}
.bt-hint b{color:var(--rune);font-weight:400;}

/* 突破横幅 */
.burst-banner{position:fixed;left:50%;top:38%;transform:translateX(-50%);z-index:99;display:flex;align-items:center;gap:18px;
  padding:18px 34px;border-radius:16px;text-align:center;pointer-events:none;
  background:linear-gradient(140deg, rgba(40,28,70,.96), rgba(12,9,22,.98));border:1px solid var(--rune);
  box-shadow:0 0 70px -10px var(--glow), 0 30px 80px -30px #000;}
.burst-banner div{display:flex;flex-direction:column;gap:6px;}
.burst-banner b{font-family:var(--serif);font-size:26px;letter-spacing:.14em;color:var(--rune);text-shadow:0 0 20px var(--glow);}
.burst-banner span:not(.burst-star){font-family:var(--serif);font-size:14px;color:#cfc6e2;letter-spacing:.08em;}
.burst-star{font-size:26px;color:var(--rune);animation:twinkle 1s ease-in-out infinite alternate;}
@keyframes twinkle{to{opacity:.35;transform:scale(.8)}}
.bt-burst-enter-active,.bt-burst-leave-active{transition:opacity .4s, transform .4s;}
.bt-burst-enter-from,.bt-burst-leave-to{opacity:0;transform:translateX(-50%) scale(.85);}

/* 通用区块 */
.section-block{margin-top:26px;}

/* 异火 */
.fire-grid{display:grid;grid-template-columns:repeat(3,1fr);gap:12px;}
.fire-option{position:relative;text-align:left;border-radius:14px;padding:15px 16px;cursor:pointer;color:var(--text);
  background:linear-gradient(160deg, rgba(24,19,38,.78), rgba(10,8,18,.9));border:1px solid var(--line);transition:.25s;}
.fire-option:hover{transform:translateY(-4px);border-color:var(--line-strong);box-shadow:0 24px 50px -28px var(--glow);}
.fire-option.on{border-color:var(--rune);box-shadow:0 0 26px -8px var(--glow);background:linear-gradient(160deg, rgba(60,40,100,.55), rgba(10,8,18,.9));}
.fo-fire{position:absolute;right:14px;top:12px;font-size:18px;color:var(--rune);opacity:.55;}
.fire-option.on .fo-fire{opacity:1;text-shadow:0 0 14px var(--glow);}
.fire-option b{display:block;font-family:var(--serif);font-size:15.5px;letter-spacing:.06em;}
.fo-rank{display:block;font-size:10.5px;letter-spacing:.1em;color:var(--muted);margin-top:4px;}
.fo-mul{display:inline-block;margin-top:10px;font-family:var(--serif);font-size:12px;color:#140a22;padding:3px 10px;border-radius:99px;
  background:linear-gradient(140deg,var(--rune),var(--fire-1));}
.fire-option small{display:block;margin-top:9px;font-size:11px;color:#a59dc0;line-height:1.6;}

/* 境界阶梯 */
.realm-ladder{display:flex;flex-wrap:wrap;gap:8px;}
.ladder-node{position:relative;display:flex;align-items:center;gap:7px;padding:8px 12px;border-radius:10px;
  background:rgba(255,255,255,.03);border:1px solid var(--line);font-family:var(--serif);font-size:12.5px;transition:.25s;}
.ladder-node::after{content:"›";position:absolute;right:-9px;color:var(--muted);font-size:14px;z-index:1;}
.ladder-node:last-child::after{display:none;}
.ln-dot{width:22px;height:22px;flex:none;border-radius:50%;display:grid;place-items:center;font-size:12px;
  background:rgba(255,255,255,.06);color:var(--muted);}
.ladder-node.done{border-color:var(--line-strong);}
.ladder-node.done .ln-dot{background:linear-gradient(140deg,var(--rune),var(--fire-1));color:#140a22;}
.ladder-node.current{border-color:var(--rune);background:rgba(255,217,138,.1);box-shadow:0 0 20px -8px var(--glow);}
.ladder-node.current .ln-dot{background:linear-gradient(140deg,var(--rune),#fff);color:#140a22;font-weight:700;}
.ln-name{color:var(--text);}
.ladder-node.locked{opacity:.42;}
.ln-unit{font-size:10px;color:var(--muted);}

/* 联动说明 */
.link-note{display:flex;align-items:center;gap:14px;margin-top:26px;padding:16px 18px;border-radius:14px;
  background:linear-gradient(100deg, rgba(90,50,180,.12), rgba(10,8,18,.4));border:1px solid var(--line);}
.ln-ic{width:40px;height:40px;flex:none;border-radius:11px;display:grid;place-items:center;font-size:20px;color:#140a22;
  background:linear-gradient(140deg,var(--rune),var(--fire-1));box-shadow:0 0 18px -4px var(--glow);}
.link-note b{font-family:var(--serif);font-size:14px;letter-spacing:.08em;color:var(--rune);}
.link-note p{margin-top:5px;font-size:12px;color:#b9b0cc;line-height:1.7;font-family:var(--serif);}
.link-note p b{font-size:12px;}

@media (max-width:900px){
  .rh-main{flex-direction:column;text-align:center;gap:16px;}
  .rh-name{font-size:34px;}
  .rh-meta{justify-content:center;}
  .fire-grid{grid-template-columns:repeat(2,1fr);}
}
@media (max-width:560px){.fire-grid{grid-template-columns:1fr;}}
</style>

<template>
  <div class="rank-page forge-page">
    <section class="section">
      <div class="section-head">
        <h2><span class="spark"></span>塑颜阁 · 自塑仙姿</h2>
        <div class="forge-actions">
          <button class="fbtn" @click="randomize"><span>✦</span> 随机塑造</button>
          <button class="fbtn ghost" @click="doReset">重置</button>
          <button class="fbtn save" @click="doSave">保存形象</button>
        </div>
      </div>
      <p class="lead">眉目由心，容颜自塑。调整下方法度，即刻显化你的斗者形貌 —— 绝非呆板木偶，一颦一笑皆由你定。</p>

      <div class="forge-body">
        <!-- 预览 -->
        <div class="preview-wrap">
          <div class="preview-stage">
            <div class="preview-ring"></div>
            <AvatarFigure :config="avatar" instance-id="forge" />
          </div>
          <div class="preview-meta">
            <b>{{ genderName }}</b> · {{ hairName }} · {{ eyeName }}
          </div>
          <div class="preview-seal">塑</div>
        </div>

        <!-- 控制面板 -->
        <div class="controls">
          <!-- 基础 -->
          <div class="cgroup">
            <div class="cg-title"><span>壹</span> 根基</div>
            <div class="crow">
              <span class="clabel">形貌</span>
              <div class="seg">
                <button :class="{ on: avatar.gender === 'female' }" @click="setGender('female')">仙姿</button>
                <button :class="{ on: avatar.gender === 'male' }" @click="setGender('male')">英姿</button>
              </div>
            </div>
            <div class="crow">
              <span class="clabel">肤色</span>
              <div class="swatches">
                <button
                  v-for="c in skins" :key="c"
                  class="swatch skin" :class="{ on: avatar.skin === c }"
                  :style="{ background: c }" :title="c" @click="avatar.skin = c"
                ></button>
              </div>
            </div>
          </div>

          <!-- 脸型 -->
          <div class="cgroup">
            <div class="cg-title"><span>贰</span> 轮廓</div>
            <div class="crow slider-row">
              <span class="clabel">脸宽</span>
              <input type="range" v-model.number="avatar.faceWidth" min="42" max="64" step="1" />
              <i class="cval">{{ avatar.faceWidth }}</i>
            </div>
            <div class="crow slider-row">
              <span class="clabel">下巴</span>
              <input type="range" v-model.number="avatar.chinWidth" min="8" max="34" step="1" />
              <i class="cval">{{ avatar.chinWidth }}</i>
            </div>
            <div class="crow">
              <span class="clabel">腮红</span>
              <button class="toggle" :class="{ on: avatar.blush }" @click="avatar.blush = !avatar.blush">
                {{ avatar.blush ? '有' : '无' }}
              </button>
            </div>
          </div>

          <!-- 眉眼 -->
          <div class="cgroup">
            <div class="cg-title"><span>叁</span> 眉眼</div>
            <div class="crow">
              <span class="clabel">眼形</span>
              <div class="seg small">
                <button v-for="o in eyeStyles" :key="o.v" :class="{ on: avatar.eyeStyle === o.v }" @click="avatar.eyeStyle = o.v">{{ o.n }}</button>
              </div>
            </div>
            <div class="crow slider-row">
              <span class="clabel">眼大</span>
              <input type="range" v-model.number="avatar.eyeSize" min="0.75" max="1.3" step="0.05" />
              <i class="cval">{{ avatar.eyeSize.toFixed(2) }}</i>
            </div>
            <div class="crow slider-row">
              <span class="clabel">眼距</span>
              <input type="range" v-model.number="avatar.eyeSpacing" min="17" max="28" step="1" />
              <i class="cval">{{ avatar.eyeSpacing }}</i>
            </div>
            <div class="crow">
              <span class="clabel">瞳色</span>
              <div class="swatches">
                <button v-for="c in eyeColors" :key="c" class="swatch" :class="{ on: avatar.eyeColor === c }" :style="{ background: c }" @click="avatar.eyeColor = c"></button>
              </div>
            </div>
            <div class="crow">
              <span class="clabel">眉形</span>
              <div class="seg small">
                <button v-for="o in browStyles" :key="o.v" :class="{ on: avatar.browStyle === o.v }" @click="avatar.browStyle = o.v">{{ o.n }}</button>
              </div>
            </div>
          </div>

          <!-- 鼻唇 -->
          <div class="cgroup">
            <div class="cg-title"><span>肆</span> 鼻唇</div>
            <div class="crow">
              <span class="clabel">鼻形</span>
              <div class="seg small">
                <button :class="{ on: avatar.noseStyle === 'small' }" @click="avatar.noseStyle = 'small'">秀气</button>
                <button :class="{ on: avatar.noseStyle === 'straight' }" @click="avatar.noseStyle = 'straight'">挺直</button>
              </div>
            </div>
            <div class="crow">
              <span class="clabel">唇形</span>
              <div class="seg small">
                <button v-for="o in mouthStyles" :key="o.v" :class="{ on: avatar.mouthStyle === o.v }" @click="avatar.mouthStyle = o.v">{{ o.n }}</button>
              </div>
            </div>
          </div>

          <!-- 发饰 -->
          <div class="cgroup">
            <div class="cg-title"><span>伍</span> 发饰</div>
            <div class="crow">
              <span class="clabel">发型</span>
              <div class="seg small wrap">
                <button v-for="o in hairStyles" :key="o.v" :class="{ on: avatar.hairStyle === o.v }" @click="avatar.hairStyle = o.v">{{ o.n }}</button>
              </div>
            </div>
            <div class="crow">
              <span class="clabel">发色</span>
              <div class="swatches">
                <button v-for="c in hairColors" :key="c" class="swatch" :class="{ on: avatar.hairColor === c }" :style="{ background: c }" @click="avatar.hairColor = c"></button>
              </div>
            </div>
            <div class="crow">
              <span class="clabel">头饰</span>
              <div class="seg small">
                <button :class="{ on: avatar.accessory === 'none' }" @click="avatar.accessory = 'none'">无</button>
                <button :class="{ on: avatar.accessory === 'hairpin' }" @click="avatar.accessory = 'hairpin'">玉簪</button>
                <button :class="{ on: avatar.accessory === 'flower' }" @click="avatar.accessory = 'flower'">簪花</button>
              </div>
            </div>
            <div class="crow">
              <span class="clabel">额纹</span>
              <div class="seg small">
                <button :class="{ on: avatar.mark === 'none' }" @click="avatar.mark = 'none'">无</button>
                <button :class="{ on: avatar.mark === 'red' }" @click="avatar.mark = 'red'">朱砂</button>
                <button :class="{ on: avatar.mark === 'gold' }" @click="avatar.mark = 'gold'">金纹</button>
              </div>
            </div>
          </div>

          <!-- 衣袍 -->
          <div class="cgroup">
            <div class="cg-title"><span>陆</span> 衣袍</div>
            <div class="crow">
              <span class="clabel">服色</span>
              <div class="swatches">
                <button v-for="c in robeColors" :key="c" class="swatch" :class="{ on: avatar.robeColor === c }" :style="{ background: c }" @click="avatar.robeColor = c"></button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import AvatarFigure from './AvatarFigure.vue'
import { avatar, saveAvatar, resetAvatar, genderPresets } from '../composables/useAvatar'
import { showToast } from '../composables/useToast'

const skins = ['#fbe3cf', '#f7d9bd', '#f0cfae', '#e6b894', '#cf9b74', '#a9714e']
const hairColors = ['#141018', '#2b1b3d', '#4a2f24', '#7a4a2c', '#b9863f', '#c9c2d6', '#8f4a7a']
const eyeColors = ['#2b1b3d', '#5b3a8c', '#2e6f8e', '#3f8f6e', '#8a5a2b', '#a8344f']
const robeColors = ['#6d4bb8', '#3a2b6e', '#9c2f3e', '#2f6e6a', '#3f5a9c', '#7a4a2c', '#4a4a55']

const eyeStyles = [
  { v: 'almond', n: '杏眼' }, { v: 'phoenix', n: '丹凤' }, { v: 'round', n: '圆眼' }
]
const browStyles = [
  { v: 'soft', n: '弯眉' }, { v: 'sharp', n: '剑眉' }, { v: 'flat', n: '平眉' }
]
const mouthStyles = [
  { v: 'smile', n: '微笑' }, { v: 'calm', n: '淡然' }, { v: 'charming', n: '朱唇' }
]
const hairStyles = [
  { v: 'long', n: '长发' }, { v: 'ponytail', n: '马尾' }, { v: 'short', n: '短发' }, { v: 'bun', n: '发髻' }, { v: 'twin', n: '双髻' }
]

const genderName = computed(() => avatar.value.gender === 'male' ? '英姿' : '仙姿')
const hairName = computed(() => (hairStyles.find(o => o.v === avatar.value.hairStyle) || {}).n || '长发')
const eyeName = computed(() => (eyeStyles.find(o => o.v === avatar.value.eyeStyle) || {}).n || '杏眼')

function pick (arr) { return arr[Math.floor(Math.random() * arr.length)] }
function randomize () {
  const base = { ...genderPresets[avatar.value.gender] }
  avatar.value = {
    ...base,
    skin: pick(skins),
    faceWidth: 44 + Math.floor(Math.random() * 19),
    chinWidth: 10 + Math.floor(Math.random() * 22),
    eyeSize: Math.round((0.8 + Math.random() * 0.5) * 20) / 20,
    eyeSpacing: 18 + Math.floor(Math.random() * 10),
    eyeStyle: pick(eyeStyles).v,
    eyeColor: pick(eyeColors),
    browStyle: pick(browStyles).v,
    noseStyle: Math.random() > 0.5 ? 'small' : 'straight',
    mouthStyle: pick(mouthStyles).v,
    blush: avatar.value.gender === 'female' ? Math.random() > 0.25 : Math.random() > 0.7,
    hairStyle: pick(hairStyles).v,
    hairColor: pick(hairColors),
    robeColor: pick(robeColors),
    accessory: pick(['none', 'hairpin', 'hairpin', 'flower']),
    mark: pick(['none', 'none', 'red', 'gold'])
  }
  showToast('天机重塑，容颜已换', '✦')
}
function setGender (g) {
  if (avatar.value.gender === g) return
  resetAvatar(g)
  showToast(g === 'male' ? '已换英姿·少年英侠' : '已换仙姿·绝代佳人', '✦')
}
function doReset () {
  resetAvatar(avatar.value.gender)
  showToast('容颜已复初貌', '✦')
}
function doSave () {
  saveAvatar()
  showToast('形貌已镌刻于斗气印记', '✦')
}
</script>

<style scoped>
.forge-page{position:relative;z-index:2;}
.lead{font-family:var(--serif);font-size:14px;letter-spacing:.12em;color:#b9b0cc;margin:-6px 0 22px;text-shadow:0 2px 14px rgba(0,0,0,.7);}
.forge-actions{display:flex;gap:10px;flex-wrap:wrap;}
.fbtn{height:34px;padding:0 16px;border:0;border-radius:9px;cursor:pointer;font-family:var(--serif);font-size:13px;letter-spacing:.06em;
  color:#140a22;background:linear-gradient(140deg,var(--rune),var(--fire-1));box-shadow:0 0 20px -6px var(--glow);transition:.22s;}
.fbtn span{font-size:15px;}
.fbtn:hover{transform:translateY(-2px);box-shadow:0 0 28px -4px var(--glow);}
.fbtn.ghost{color:var(--muted);background:rgba(255,255,255,.05);border:1px solid var(--line);box-shadow:none;}
.fbtn.ghost:hover{color:var(--text);border-color:var(--line-strong);}
.fbtn.save{background:linear-gradient(140deg,var(--cyan),#2a8bff);color:#04121c;}

.forge-body{display:grid;grid-template-columns:minmax(280px,400px) 1fr;gap:22px;align-items:start;}
.preview-wrap{position:relative;border-radius:20px;padding:18px;
  background:radial-gradient(120% 90% at 50% 10%, rgba(169,123,255,.2), transparent 62%), linear-gradient(165deg, rgba(24,18,40,.9), rgba(8,6,15,.95));
  border:1px solid var(--line-strong);box-shadow:0 40px 80px -40px rgba(0,0,0,.9), inset 0 0 60px -30px var(--glow);}
.preview-stage{position:relative;width:100%;aspect-ratio:9/10;display:flex;align-items:flex-end;justify-content:center;overflow:hidden;}
.preview-ring{position:absolute;left:50%;top:48%;width:78%;aspect-ratio:1;transform:translate(-50%,-50%);border-radius:50%;
  border:1px dashed rgba(255,217,138,.4);box-shadow:0 0 60px -18px var(--glow), inset 0 0 50px -22px var(--glow);animation:ring-spin 40s linear infinite;}
@keyframes ring-spin{to{transform:translate(-50%,-50%) rotate(360deg)}}
.preview-meta{text-align:center;margin-top:12px;font-family:var(--serif);font-size:13px;letter-spacing:.08em;color:#cfc6e2;}
.preview-meta b{color:var(--rune);}
.preview-seal{position:absolute;top:12px;right:14px;width:34px;height:34px;border-radius:9px;display:grid;place-items:center;
  font-family:var(--serif);font-size:18px;font-weight:700;color:#140a22;background:linear-gradient(140deg,var(--rune),var(--fire-1));box-shadow:0 0 18px -4px var(--glow);}

.controls{display:grid;grid-template-columns:repeat(2,minmax(0,1fr));gap:14px;}
.cgroup{border-radius:16px;padding:16px 18px;
  background:linear-gradient(160deg, rgba(24,19,38,.82), rgba(10,8,18,.9));border:1px solid var(--line);box-shadow:0 20px 44px -30px rgba(0,0,0,.9);}
.cg-title{font-family:var(--serif);font-size:14px;font-weight:700;letter-spacing:.14em;color:var(--rune);margin-bottom:14px;display:flex;align-items:center;gap:8px;}
.cg-title span{width:22px;height:22px;border-radius:6px;display:grid;place-items:center;font-size:12px;color:#140a22;background:linear-gradient(140deg,var(--rune),var(--fire-1));}
.crow{display:flex;align-items:center;gap:10px;margin-bottom:11px;min-height:30px;}
.crow:last-child{margin-bottom:0;}
.clabel{font-family:var(--serif);font-size:12.5px;letter-spacing:.1em;color:var(--muted);width:38px;flex:none;}
.seg{display:flex;gap:6px;flex:1;}
.seg.wrap{flex-wrap:wrap;}
.seg button{flex:1;min-width:38px;height:30px;padding:0 8px;border-radius:8px;cursor:pointer;font-family:var(--serif);font-size:12.5px;
  color:var(--muted);background:rgba(255,255,255,.04);border:1px solid var(--line);transition:.2s;}
.seg button:hover{color:var(--text);border-color:var(--line-strong);}
.seg button.on{color:#140a22;font-weight:700;background:linear-gradient(140deg,var(--rune),var(--fire-1));border-color:transparent;box-shadow:0 0 14px -4px var(--glow);}
.swatches{display:flex;gap:7px;flex-wrap:wrap;flex:1;}
.swatch{width:24px;height:24px;border-radius:50%;cursor:pointer;border:2px solid rgba(255,255,255,.18);transition:.2s;padding:0;}
.swatch:hover{transform:scale(1.15);}
.swatch.on{border-color:var(--rune);box-shadow:0 0 0 2px rgba(255,217,138,.3), 0 0 12px -2px var(--glow);}
.slider-row input[type=range]{flex:1;accent-color:var(--rune);height:4px;cursor:pointer;}
.cval{font-family:var(--serif);font-size:12px;font-style:normal;color:var(--rune);width:30px;text-align:right;}
.toggle{height:28px;min-width:48px;padding:0 12px;border-radius:99px;cursor:pointer;font-family:var(--serif);font-size:12px;color:var(--muted);
  background:rgba(255,255,255,.05);border:1px solid var(--line);transition:.2s;}
.toggle.on{color:#140a22;font-weight:700;background:linear-gradient(140deg,var(--rune),var(--fire-1));border-color:transparent;}

@media (max-width:900px){
  .forge-body{grid-template-columns:1fr;}
  .controls{grid-template-columns:1fr;}
  .preview-stage{max-height:420px;}
  .forge-actions{width:100%;}
}
</style>

<template>
  <div class="home">
    <header class="home-top fx">
      <div class="brand">
        <div class="seal">炎</div>
        <div class="brand-txt">
          <h1>斗气大陆</h1>
          <p>异 火 图 鉴 · 斗 者 殿 堂</p>
        </div>
      </div>

      <div class="user-box">
        <div class="avatar">{{ initial }}</div>
        <div class="u-info">
          <span class="u-name">{{ username || '无名斗者' }}</span>
          <span class="u-rank">{{ rank }}</span>
        </div>
        <button class="logout" type="button" @click="$emit('logout')">退出斗气</button>
      </div>
    </header>

    <section class="welcome card fx d1">
      <h2><span class="spark"></span>欢 迎 归 来，{{ username || '无名斗者' }}</h2>
      <p class="desc">秘钥校验通过，异火认主。愿此间斗气与玄重尺，伴你踏遍大陆。</p>
    </section>

    <section class="grid">
      <div class="card stat fx d2">
        <div class="s-ic">
          <svg viewBox="0 0 24 24"><path d="M12 2.6c2.6 3.4 1.2 5.2.2 7-1.4 2.6-2.8 4.4-1.2 7.6"/><path d="M12 21.4c-2.6-3.4-1.2-5.2-.2-7 1.4-2.6 2.8-4.4 1.2-7.6"/></svg>
        </div>
        <span class="s-label">当前境界</span>
        <b class="s-value">斗者 · 一星</b>
        <p class="s-tip">初入斗气大陆，根基已立</p>
      </div>

      <div class="card stat fx d3">
        <div class="s-ic">
          <svg viewBox="0 0 24 24"><path d="M12 3c1.6 3.2 4.6 4.6 4.6 8a4.6 4.6 0 0 1-9.2 0c0-3.4 3-4.8 4.6-8Z"/></svg>
        </div>
        <span class="s-label">本命异火</span>
        <b class="s-value">青莲地心火</b>
        <p class="s-tip">待觉醒，焚尽万法</p>
      </div>

      <div class="card stat fx d4">
        <div class="s-ic">
          <svg viewBox="0 0 24 24"><rect x="4.4" y="10.4" width="15.2" height="9.6" rx="2.6"/><path d="M8.2 10.4V7.8a3.8 3.8 0 0 1 7.6 0v2.6"/></svg>
        </div>
        <span class="s-label">契约状态</span>
        <b class="s-value">已缔结</b>
        <p class="s-tip">秘钥以密文封存，安稳无虞</p>
      </div>
    </section>

    <footer class="home-foot fx d4">
      <span>斗气大陆 · 登录演示</span>
      <span class="dot">·</span>
      <span>登录耗时 {{ cost }}ms</span>
      <span class="dot">·</span>
      <span>{{ nowText }}</span>
    </footer>
  </div>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  username: { type: String, default: '' }
})
defineEmits(['logout'])

const initial = computed(() => (props.username ? props.username.charAt(0).toUpperCase() : '斗'))

// 按账号长度做个趣味斗气等级，纯演示用
const rank = computed(() => {
  const n = (props.username || '').length
  if (n >= 8) return '斗宗 · 威震一方'
  if (n >= 6) return '斗灵 · 名动一方'
  if (n >= 4) return '斗师 · 小有所成'
  return '斗者 · 初出茅庐'
})

const cost = computed(() => 200 + ((props.username || '').length * 37) % 300)
const nowText = new Date().toLocaleString('zh-CN', { hour12: false })
</script>

<style scoped>
.home{
  position:relative;z-index:2;
  /* 全局 body 为 overflow:hidden，这里固定视口高度并允许内部滚动，避免内容被裁剪 */
  height:100vh;overflow-y:auto;
  padding:clamp(20px,3.4vw,44px) clamp(20px,5vw,72px);
  display:flex;flex-direction:column;gap:26px;
  max-width:1440px;margin:0 auto;
}
/* 顶栏 */
.home-top{display:flex;justify-content:space-between;align-items:center;gap:20px;flex-wrap:wrap;}
.brand{display:flex;align-items:center;gap:16px;}
.seal{
  width:54px;height:54px;border-radius:12px;display:grid;place-items:center;flex:none;
  font-family:var(--serif);font-size:27px;font-weight:700;color:#140a22;
  background:linear-gradient(140deg,var(--rune),var(--fire-1));
  box-shadow:0 0 26px var(--glow), inset 0 0 10px rgba(255,245,220,.55);
  animation:float 6s ease-in-out infinite;
}
@keyframes float{0%,100%{transform:translateY(0)}50%{transform:translateY(-6px)}}
.brand-txt h1{
  font-family:var(--serif);font-size:clamp(24px,2.6vw,32px);letter-spacing:.16em;font-weight:700;
  background:linear-gradient(178deg,#fff8e8 8%,var(--rune) 46%,var(--fire-1) 92%);
  -webkit-background-clip:text;background-clip:text;color:transparent;
  filter:drop-shadow(0 0 18px var(--glow-soft));
}
.brand-txt p{margin-top:6px;font-size:11.5px;color:var(--muted);letter-spacing:.22em;}

.user-box{display:flex;align-items:center;gap:14px;padding:10px 14px;border-radius:16px;
  background:linear-gradient(165deg, rgba(24,19,38,.86), rgba(10,8,18,.92));
  border:1px solid var(--line);box-shadow:0 18px 40px -22px #000, 0 0 40px -22px var(--glow);backdrop-filter:blur(14px);}
.avatar{
  width:42px;height:42px;border-radius:12px;flex:none;display:grid;place-items:center;
  font-family:var(--serif);font-size:19px;font-weight:700;color:#150b23;
  background:linear-gradient(135deg,var(--fire-1),var(--fire-2));box-shadow:0 0 18px var(--glow);
}
.u-info{display:flex;flex-direction:column;gap:3px;min-width:96px;}
.u-name{font-size:14.5px;letter-spacing:.06em;color:var(--text);}
.u-rank{font-size:11px;letter-spacing:.1em;color:var(--rune);opacity:.85;}
.logout{
  flex:none;height:38px;padding:0 16px;border-radius:10px;cursor:pointer;
  font-family:var(--sans);font-size:12.5px;letter-spacing:.08em;
  color:var(--rune);background:rgba(255,217,138,.07);border:1px solid var(--line-strong);transition:.25s;
}
.logout:hover{background:rgba(255,217,138,.18);box-shadow:0 0 16px -3px var(--glow);transform:translateY(-1px);}

/* 通用卡片 */
.card{
  position:relative;padding:26px 28px;border-radius:18px;
  background:linear-gradient(165deg, rgba(24,19,38,.86), rgba(10,8,18,.93));
  border:1px solid var(--line);
  box-shadow:0 34px 70px -34px rgba(0,0,0,.9), 0 0 70px -34px var(--glow);
  backdrop-filter:blur(16px);
}
.card::before,.card::after{content:"";position:absolute;width:22px;height:22px;pointer-events:none;border:1.5px solid var(--rune);opacity:.5;}
.card::before{top:-1px;left:-1px;border-right:0;border-bottom:0;border-radius:18px 0 0 0;}
.card::after{bottom:-1px;right:-1px;border-left:0;border-top:0;border-radius:0 0 18px 0;}

/* 欢迎区 */
.welcome h2{font-family:var(--serif);font-size:clamp(20px,2.3vw,28px);letter-spacing:.12em;font-weight:700;display:flex;align-items:center;gap:10px;}
.welcome h2 .spark{width:9px;height:9px;flex:none;transform:rotate(45deg);background:linear-gradient(135deg,var(--fire-1),var(--rune));box-shadow:0 0 12px var(--glow);}
.welcome .desc{margin-top:10px;font-size:13px;line-height:1.9;color:var(--muted);letter-spacing:.1em;}

/* 三宫格 */
.grid{display:grid;grid-template-columns:repeat(auto-fit,minmax(230px,1fr));gap:20px;}
.stat{display:flex;flex-direction:column;gap:8px;}
.s-ic{width:44px;height:44px;border-radius:12px;display:grid;place-items:center;margin-bottom:6px;
  background:rgba(255,255,255,.04);border:1px solid rgba(255,255,255,.08);}
.s-ic svg{width:22px;height:22px;stroke:var(--fire-1);fill:none;stroke-width:1.6;}
.s-label{font-size:11.5px;letter-spacing:.2em;color:var(--muted);}
.s-value{font-family:var(--serif);font-size:19px;letter-spacing:.08em;color:var(--rune);}
.s-tip{font-size:12px;color:#6f6982;letter-spacing:.06em;}

/* 页脚 */
.home-foot{display:flex;align-items:center;gap:10px;justify-content:center;
  font-size:11.5px;color:#5f5a71;letter-spacing:.12em;margin-top:auto;padding-top:8px;}
.home-foot .dot{opacity:.5;}

@media (max-width:640px){
  .user-box{width:100%;justify-content:space-between;}
  .brand-txt p{display:none;}
}
</style>

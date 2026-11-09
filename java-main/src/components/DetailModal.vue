<template>
  <transition name="dm-fade">
    <div v-if="open" class="dm-mask" @click.self="$emit('close')">
      <div class="dm-card" role="dialog" aria-modal="true">
        <button class="dm-close" @click="$emit('close')" aria-label="关闭">✕</button>
        <img v-if="item.img" class="dm-img" :src="item.img" :alt="item.title" loading="lazy" />
        <span class="dm-lead">{{ item.lead }}</span>
        <div class="dm-rank">{{ item.rank }}</div>
        <h3 class="dm-title">{{ item.title }}</h3>
        <div class="dm-tags">
          <span v-if="item.tag" class="dm-tag">{{ item.tag }}</span>
          <span v-if="item.tag2" class="dm-tag ghost">{{ item.tag2 }}</span>
        </div>
        <p class="dm-desc">{{ item.desc }}</p>
        <dl v-if="item.rows && item.rows.length" class="dm-rows">
          <div v-for="r in item.rows" :key="r.label" class="dm-row">
            <dt>{{ r.label }}</dt>
            <dd>{{ r.value || '——' }}</dd>
          </div>
        </dl>
      </div>
    </div>
  </transition>
</template>

<script setup>
// 通用详情弹窗：供异火榜 / 功法榜 / 丹药坊 / 斗气榜 / 宗门 复用。
// item 结构：{ lead, rank, title, tag, tag2, desc, rows:[{label,value}] }
defineProps({
  open: { type: Boolean, default: false },
  item: { type: Object, default: () => ({}) }
})
defineEmits(['close'])
</script>

<style scoped>
.dm-mask{
  position:fixed;inset:0;z-index:30;display:grid;place-items:center;padding:20px;
  background:rgba(4,3,9,.62);backdrop-filter:blur(6px);
}
.dm-card{
  position:relative;width:min(520px,92vw);max-height:84vh;overflow-y:auto;
  border-radius:22px;padding:34px 32px 30px;
  background:linear-gradient(165deg, rgba(28,22,44,.96), rgba(10,8,18,.98));
  border:1px solid var(--line-strong);
  box-shadow:0 50px 110px -30px #000, 0 0 90px -34px var(--glow);
  animation:dm-rise .35s cubic-bezier(.2,.8,.25,1) both;
}
.dm-card::-webkit-scrollbar{width:8px;}
.dm-card::-webkit-scrollbar-thumb{background:rgba(255,217,138,.22);border-radius:4px;}
@keyframes dm-rise{from{opacity:0;transform:translateY(22px) scale(.97)}to{opacity:1;transform:none}}
.dm-card::before,.dm-card::after{content:"";position:absolute;width:26px;height:26px;pointer-events:none;border:1.5px solid var(--rune);opacity:.6;}
.dm-card::before{top:-1px;left:-1px;border-right:0;border-bottom:0;border-radius:22px 0 0 0;}
.dm-card::after{bottom:-1px;right:-1px;border-left:0;border-top:0;border-radius:0 0 22px 0;}

.dm-close{position:absolute;top:16px;right:16px;width:34px;height:34px;border-radius:10px;cursor:pointer;
  background:rgba(255,255,255,.04);border:1px solid var(--line);color:var(--muted);font-size:15px;line-height:1;
  transition:.25s;}
.dm-close:hover{color:var(--text);border-color:var(--line-strong);box-shadow:0 0 14px -4px var(--glow);}
.dm-img{display:block;width:100%;height:190px;object-fit:cover;border-radius:14px;margin-bottom:18px;filter:saturate(1.05) contrast(1.02);border:1px solid var(--line);box-shadow:0 18px 40px -20px var(--glow);}

.dm-lead{font-family:var(--serif);font-size:12px;letter-spacing:.3em;color:var(--rune);}
.dm-rank{margin-top:8px;display:inline-block;font-size:11.5px;letter-spacing:.14em;color:var(--rune);
  padding:3px 11px;border-radius:99px;border:1px solid var(--line-strong);background:rgba(255,217,138,.06);}
.dm-title{font-family:var(--serif);font-size:30px;font-weight:700;letter-spacing:.1em;margin:14px 0 0;
  background:linear-gradient(178deg,#fff8e8,var(--rune) 50%,var(--fire-1));
  -webkit-background-clip:text;background-clip:text;color:transparent;filter:drop-shadow(0 0 14px var(--glow-soft));}
.dm-tags{display:flex;flex-wrap:wrap;gap:8px;margin-top:14px;}
.dm-tag{font-family:var(--serif);font-size:12.5px;letter-spacing:.06em;color:#140a22;padding:5px 13px;border-radius:99px;
  background:linear-gradient(140deg,var(--rune),var(--fire-1));box-shadow:0 0 16px -3px var(--glow);}
.dm-tag.ghost{color:var(--text);background:rgba(255,255,255,.05);border:1px solid var(--line);box-shadow:none;}
.dm-desc{font-size:13px;line-height:2;color:#bdb4d0;margin:18px 0 0;}
.dm-rows{margin:18px 0 0;border-top:1px solid var(--line);padding-top:14px;}
.dm-row{display:flex;gap:14px;padding:9px 0;border-bottom:1px dashed rgba(255,255,255,.07);}
.dm-row:last-child{border-bottom:0;}
.dm-row dt{flex:none;width:64px;font-size:11.5px;letter-spacing:.16em;color:var(--muted);padding-top:2px;}
.dm-row dd{flex:1;font-size:13px;line-height:1.8;color:var(--text);margin:0;}

.dm-fade-enter-active,.dm-fade-leave-active{transition:opacity .25s;}
.dm-fade-enter-from,.dm-fade-leave-to{opacity:0;}
</style>

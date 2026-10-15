<template>
  <div class="themes">
    <span>异 火</span>
    <i
      v-for="opt in opts"
      :key="opt.key"
      :data-t="opt.key"
      :class="{ on: theme === opt.key }"
      :title="opt.name"
      @click="pick(opt.key)"
    ></i>
  </div>
</template>

<script setup>
import { theme, themeMeta, setTheme } from '../composables/useTheme'
import { showToast } from '../composables/useToast'

const opts = [
  { key: 'yaohuo', name: '净莲妖火' },
  { key: 'qinglian', name: '青莲地心火' },
  { key: 'yanyan', name: '陨落心炎' }
]

function pick (key) {
  setTheme(key)
  showToast('异火已换 · ' + themeMeta[key].name)
}
</script>

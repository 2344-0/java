import { ref } from 'vue'

// 当前异火主题：yaohuo(净莲妖火) / qinglian(青莲地心火) / yanyan(陨落心炎)
export const theme = ref('yaohuo')

export const themeMeta = {
  yaohuo:   { name: '净莲妖火', fire: [169, 123, 255] },
  qinglian: { name: '青莲地心火', fire: [63, 245, 214] },
  yanyan:   { name: '陨落心炎', fire: [255, 122, 26] }
}

export function setTheme (t) {
  if (!themeMeta[t]) return
  theme.value = t
  document.body.setAttribute('data-theme', t)
}

export function getFireRGB () {
  return themeMeta[theme.value].fire
}

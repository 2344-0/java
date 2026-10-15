import { reactive } from 'vue'

export const toastState = reactive({
  show: false,
  text: '',
  icon: '✦'
})

let timer = null

export function showToast (text, icon) {
  toastState.text = text
  toastState.icon = icon || '✦'
  toastState.show = true
  clearTimeout(timer)
  timer = setTimeout(() => { toastState.show = false }, 2600)
}

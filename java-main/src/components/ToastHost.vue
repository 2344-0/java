<template>
  <div id="toast" :class="{ show: toastState.show }">
    <span class="ic">{{ toastState.icon }}</span>
    <span>{{ toastState.text }}</span>
  </div>
</template>

<script setup>
import { toastState } from '../composables/useToast'
</script>

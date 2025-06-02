<template>
  <img v-if="avatarSrc" :src="avatarSrc" alt="头像图片"
       :style="`width: ${prop.size}px; height: ${prop.size}px;`">
  <img v-else src="@/assets/img/steve.png" alt="默认头像"
       :style="`width: ${prop.size}px; height: ${prop.size}px;`">
</template>

<script setup>
import {useAvatar} from "@/utils/useAvatar.js"

const prop = defineProps({
  playerName: {
    type: String,
    required: true
  },
  size: {
    type: Number,
    default: 256
  }
})

const avatarSrc = useAvatar(prop.playerName, prop.size)
</script>

<style scoped>
/* 可选：添加图片圆角或边框样式 */
</style>

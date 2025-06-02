<template>
  <div class="wrapper">
    <br>
    <div v-for="(msg, index) in messageData" :key="index">
      <!-- 玩家消息 -->
      <div v-if="msg.type === 'PLAYER-CHAT-MSG'" class="message"
           :style="doesFoldingDisplay(messageData[index - 1], msg) ? 'padding: 8px 10px 8px 10px; min-height: 0;' : ''">
        <player-avatar :player-name="msg.username" :size="50"
                       v-if="!doesFoldingDisplay(messageData[index - 1], msg)"
                       class="player-avatar"/>
        <div class="message-text mc-font">
          <div class="player-name"
               v-if="!doesFoldingDisplay(messageData[index - 1], msg)">
            {{ msg.username }}
          </div>
          <div class="message-time"
               v-if="!doesFoldingDisplay(messageData[index - 1], msg)">
            {{ parseTimestamp(msg.timestamp) }}
          </div>
          <div class="message-content">
            {{ msg.content }}
          </div>
        </div>
      </div>
      <!-- 玩家登入登出消息 -->
      <div v-else-if="msg.type === 'PLAYER-LOGINOUT-MSG'" class="info">
        <div class="info-time">{{ parseTimestamp(msg.timestamp) }}</div>
        <div class="info-text">
          <player-avatar :player-name="msg.username" :size="16"
                         style="position: relative; top: 4px"/>
        </div>
        <div class="info-text mc-font" style="margin-left: 5px; color: #5d5d5d; font-weight: bold">
          {{ msg.username }}
        </div>
        <div class="info-text" style="margin-left: 5px;"
             :class="msg.content.includes('加入') ? 'info-text-login' : 'info-text-logout'">
          {{ msg.content }}
        </div>
      </div>
    </div>
    <br>
  </div>
</template>

<script setup>
import PlayerAvatar from "@/components/PlayerAvatar.vue";

const prop = defineProps({
  messageData: {
    required: true,
    type: Array,
    default: []
  }
})

// 是否折叠显示
const doesFoldingDisplay = (preMsg, currMsg) => {
  if (preMsg && currMsg) {
    // 都是玩家消息且同一个人且间隔小于5分钟
    return preMsg.type === 'PLAYER-CHAT-MSG' &&
        currMsg.type === 'PLAYER-CHAT-MSG' &&
        preMsg.username === currMsg.username &&
        currMsg.timestamp - preMsg.timestamp < 5 * 60 * 1000
  }
  return false
}

// 时间戳显示时间
const parseTimestamp = (timestamp) => {
  const date = new Date(timestamp)

  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')

  const hours = date.getHours()
  let ampm
  if (hours < 6) {
    ampm = '凌晨'
  } else if (hours < 8) {
    ampm = '清晨'
  } else if (hours < 11) {
    ampm = '上午'
  } else if (hours < 13) {
    ampm = '中午'
  } else if (hours < 18) {
    ampm = '下午'
  } else if (hours < 20) {
    ampm = '傍晚'
  } else if (hours < 22) {
    ampm = '晚上'
  } else {
    ampm = '深夜'
  }
  const formattedHours = String(hours).padStart(2, '0') // 24小时制
  const minutes = String(date.getMinutes()).padStart(2, '0')

  return `${year}-${month}-${day} ${ampm} ${formattedHours}:${minutes}`
}
</script>

<style scoped>
.wrapper {
  width: 100%;
  min-height: 100px;
  background: #f6f6f6;
}

.message {
  width: 90%;
  min-height: 70px;
  position: relative;
  border-radius: 10px;
  margin: 5px auto;
  padding: 10px;
}

.message:hover {
  background: #dbe3d9;
}

.player-avatar {
  position: absolute;
  width: 50px;
  height: 50px;
  filter: drop-shadow(5px 5px 10px rgba(0, 0, 0, 0.2));
}

.message-text {
  margin-left: 65px;
}

.player-name {
  color: #2e612a;
  font-weight: bold;
  font-size: 20px;
  display: inline-block;
  margin: 10px 0;
}

.message-time {
  color: #757575;
  font-size: 15px;
  display: inline-block;
  margin-left: 10px;
}

.info {
  width: 90%;
  position: relative;
  border-radius: 10px;
  margin: 5px auto;
  padding: 0 10px;
}

.info-time {
  font-size: 13px;
  color: #757575;
  display: inline-block;
}

.info-text {
  color: #757575;
  font-size: 13px;
  display: inline-block;
  margin-left: 15px;
}

.info-text-login {
  color: #2e612a;
}

.info-text-logout {
  color: #c94a56;
}

.mc-font {
  font-family: "MCFont", system-ui;
}
</style>
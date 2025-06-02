<template>
  <div class="title">Minecraft 聊天记录浏览器</div>
  <div class="container">
    <p>当前状态：{{ currentStatus }}</p>
    <hr style="margin: 10px 0">
    <button class="upload-btn" @click="btnClick">上传日志文件（.log 或 .gz）</button>
    <button class="upload-btn" @click="refreshView" style="margin-left: 10px">刷新头像</button>
    <br><br>
    <message-wrapper v-if="showMsg" :message-data="responseData"/>
  </div>
  <div class="footer">
    本项目基于协议 GNU General Public License v3.0 开源，前往
    <a href="https://github.com/wifi504/minecraft-chat-history-browser" target="_blank">Github</a> |
    作者B站：<a href="https://space.bilibili.com/335320968" target="_blank">WIFI连接超时</a>
    <br>
    背景来源：LSPHub 主城截图，作者：LSPHub 成员 <a href="https://github.com/pszyfwq" target="_blank">pszyfwq</a>
  </div>
  <div class="bg-img">
    <img src="@/assets/img/background.png" alt="背景图">
  </div>
</template>

<script setup>
import request from "@/utils/request.js";
import {ref} from "vue";
import MessageWrapper from "@/components/MessageWrapper.vue";

const responseData = ref([])
const currentStatus = ref('未选择文件')

const btnClick = async () => {
  // 创建隐藏的文件输入元素
  const input = document.createElement('input');
  input.type = 'file';
  input.multiple = true; // 允许选择多个文件
  input.accept = '.log,.gz'; // 限制文件类型

  // 当用户选择文件后
  input.onchange = async (e) => {
    const files = e.target.files;
    if (files.length === 0) return;

    const formData = new FormData();
    for (let i = 0; i < files.length; i++) {
      formData.append('files', files[i]);
    }

    try {
      const res = await request.post('/chat-history/upload', formData);
      responseData.value = res.data;
      currentStatus.value = `从 ${files.length} 个日志文件中，解析出 ${res.data.length} 条消息`
    } catch (err) {
      responseData.value = err.msg || "上传失败";
      currentStatus.value = '文件上传失败，请重新上传'
    }
  };

  // 触发文件选择对话框
  input.click();
}

const showMsg = ref(true)
// 刷新组件，偶尔头像会错乱
const refreshView = () => {
  showMsg.value = false
  setTimeout(() => {
    showMsg.value = true
  }, 0)
}
</script>

<style scoped>
.container {
  width: 800px;
  min-height: 600px;
  margin: 20px auto;
  padding: 20px;
  border: 1px solid #ccc;
  border-radius: 5px;
  background: rgba(255, 255, 255, .4);
}

.title {
  text-align: center;
  margin-top: 20px;
  color: rgba(33, 87, 29, 0.94);
  font-family: "MCFont", system-ui;
  font-size: 24px;
  font-weight: bold;
  text-shadow: 0 0 10px #ffffff,
  0 0 10px #ffffff,
  0 0 20px #ffffff,
  0 0 30px #ffffff,
  0 0 50px #ffffff;
}

.upload-btn {
  --main-color: #276c14;
  --hover-color: #276c14;
  --active-color: #133f09;
  --main-bg: #e9f1e9;
  --hover-bg: #d1e5d1;
  --active-bg: #bfd3bf;
  padding: 5px 10px;
  border-radius: 5px;
  border: 1px solid var(--main-color);
  background: var(--main-bg);
  color: var(--main-color);
  transform: scale(1);
  transition: all .3s ease;
}

.upload-btn:hover {
  background: var(--hover-bg);
  color: var(--hover-color);
}

.upload-btn:active {
  background: var(--active-bg);
  color: var(--active-color);
  transform: scale(.95);
}

.footer {
  text-align: center;
  font-size: 16px;
  font-weight: bold;
  padding-bottom: 100px;
  color: #276c14;
  font-family: "MCFont", system-ui;
  line-height: 1.8;
  text-shadow: 0 0 10px #ffffff,
  0 0 10px #ffffff,
  0 0 20px #ffffff,
  0 0 30px #ffffff,
  0 0 50px #ffffff;
}

.footer a {
  color: #276c14;
  transition: color .3s ease;
  border-bottom: 2px solid #184d0f;
  padding-bottom: 3px;
}

.footer a:hover {
  color: #a939c2;
}

.footer a:active {
  color: #7b238d;
}

.bg-img {
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  overflow: hidden;
  z-index: -1;
}

.bg-img img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  object-position: center;
}
</style>

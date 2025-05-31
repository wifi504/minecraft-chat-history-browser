<template>
  <h2>Minecraft 聊天记录浏览器</h2>
  <div class="container">
    <p>当前状态：{{ currentStatus }}</p>
    <hr style="margin: 10px 0">
    <button @click="btnClick">上传日志文件（.log / .gz）</button>
    <br><br>
    <message-wrapper :message-data="responseData"/>
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
</script>

<style scoped>
.container {
  width: 800px;
  min-height: 600px;
  margin: 20px auto;
  padding: 20px;
  border: 1px solid #ccc;
  border-radius: 5px;
  background: #ffffff;
}

h2 {
  text-align: center;
  margin-top: 20px;
  color: rgba(33, 87, 29, 0.94);
  font-family: "阿里巴巴普惠体 Heavy", serif;
}
</style>

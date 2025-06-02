import {ref, watchEffect} from "vue"
import request from "@/utils/request.js"

const avatarPromiseCache = new Map()  // 所有组件共享缓存

/**
 * 获取玩家头像 base64 并在请求合并时使用缓存
 * @param {String} playerName 玩家名称
 * @param {Number} size 头像尺寸
 * @returns {Ref<String>} 返回一个响应式 src
 */
export function useAvatar(playerName, size = 256) {
  const src = ref("")

  watchEffect(() => {
    if (!playerName) {
      src.value = ""
      return
    }

    const cacheKey = `${playerName}-${size}`

    if (avatarPromiseCache.has(cacheKey)) {
      avatarPromiseCache.get(cacheKey)
        .then(data => src.value = data)
        .catch(() => src.value = "")
      return
    }

    const promise = new Promise(async (resolve, reject) => {
      try {
        const res = await request.get(`/player-avatar/generate-base64/${playerName}?size=${size}`)
        resolve(res.data)
      } catch (e) {
        avatarPromiseCache.delete(cacheKey)
        // console.error("头像加载失败：", playerName)
        reject(e)
      }
    })

    avatarPromiseCache.set(cacheKey, promise)

    promise.then(data => {
      src.value = data
    }).catch(() => {
      src.value = ""
    })
  })

  return src
}
import axios from 'axios'

// 封装 HTTP Request 请求

// 开发模式自动控制台输出所有请求对象
const dev = false

// 创建 Axios 实例
const instance = axios.create({
  baseURL: 'http://localhost:25566/api', // 配置后端接口前缀
  timeout: 10000 // 超时时间
})

// 请求拦截器
instance.interceptors.request.use(
  (config) => {
    if (dev) {
      console.log('Request ------>')
      console.log(config)
    }

    // 拦截请求携带 Token
    const token = localStorage.getItem('token')
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`
    }
    return config
  },
  (error) => {
    if (dev) {
      console.log('---x---> Request Error')
      console.log(error)
    }
    return Promise.reject(error)
  }
)

// 响应拦截器
instance.interceptors.response.use(
  (response) => {
    if (dev) {
      console.log('<------ Response')
      console.log(response)
    }

    const res = response.data

    // 判断R对象的code是否为200
    if (res.code === 200) {
      return res
    } else {
      return Promise.reject(res)
    }
  },
  (error) => {
    if (dev) {
      console.log('Response Error <---x---')
      console.log(error)
    }

    return Promise.reject({
      code: error.code,
      msg: error.message,
      data: error
    })
  }
)

// 导出封装好的请求方法
export default {
  get(url, params) {
    return instance.get(url, {params})
  },
  post(url, data) {
    return instance.post(url, data)
  },
  put(url, data) {
    return instance.put(url, data)
  },
  delete(url, data) {
    return instance.request({
      url,
      method: 'delete',
      data // 以请求体形式发送
    })
  }
}
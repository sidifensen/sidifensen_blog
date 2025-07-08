import request from "@/utils/Request";

export function login(data){
  return request({
    url: '/user/login',
    method: 'post',
    data: data
  })
}

export function checkCode(){
  return request({
    url: '/user/checkCode',
    method: 'get'
  })
}

export function info(){
  return request({
    url: '/user/info',
    method: 'get'
  })
}


import request from "@/utils/Request";

export function login(data){
  return request({
    url: '/user/login',
    method: 'post',
    data: data
  })
}
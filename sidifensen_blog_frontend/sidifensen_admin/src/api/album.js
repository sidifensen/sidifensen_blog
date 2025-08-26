import request from "@/utils/Request";

// 管理端查看所有用户的相册
export function adminList() {
  return request({
    url: "/album/admin/list",
    method: "get",
  });
}

// 管理端更新相册
export function adminUpdateAlbum(data) {
  return request({
    url: "/album/admin/update",
    method: "put",
    data: data,
  });
}

// 管理端删除相册
export function adminDeleteAlbum(albumId) {
  return request({
    url: `/album/admin/delete/${albumId}`,
    method: "delete",
  });
}

// 管理端搜索相册
export function adminSearchAlbum(data) {
  return request({
    url: "/album/admin/search",
    method: "post",
    data: data,
  });
}

// 管理端查看相册详情
export function adminGetAlbumDetail(albumId) {
  return request({
    url: `/album/admin/get/${albumId}`,
    method: 'get'
  })
}
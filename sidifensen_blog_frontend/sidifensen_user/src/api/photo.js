import request from "@/utils/Request";

// 上传相册图片
export function uploadAlbumPhoto(file, albumId) {
  const formData = new FormData();
  formData.append("file", file);
  formData.append("albumId", albumId);
  return request({
    url: "/photo/uploadAlbum",
    method: "post",
    data: formData,
    headers: { "Content-Type": "multipart/form-data" },
  });
}

// 上传文章图片
export function uploadArticlePhoto(file) {
  const formData = new FormData();
  formData.append("file", file);
  return request({
    url: "/photo/uploadArticle",
    method: "post",
    data: formData,
    headers: { "Content-Type": "multipart/form-data" },
  });
}

// 上传专栏图片
export function uploadColumnPhoto(file) {
  const formData = new FormData();
  formData.append("file", file);
  return request({
    url: "/photo/uploadColumn",
    method: "post",
    data: formData,
    headers: { "Content-Type": "multipart/form-data" },
  });
}

// 修改图片展示状态
export function changeShowStatus(data) {
  return request({
    url: "photo/changeShowStatus",
    method: "put",
    data,
  });
}

// 批量删除图片
export function batchDeletePhoto(photoIds) {
  return request({
    url: "/photo/delete/batch",
    method: "delete",
    data: photoIds,
  });
}

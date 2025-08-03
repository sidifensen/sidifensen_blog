import request from "@/utils/Request";

// 上传图片
export function uploadPhoto(file, albumId) {
  const formData = new FormData();
  formData.append("file", file);
  formData.append("albumId", albumId);
  return request({
    url: "/photo/upload",
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

// 删除图片
export function deletePhoto(photoId) {
  return request({
    url: `/photo/${photoId}`,
    method: "delete",
  });
}

// 批量删除图片
export function batchDeletePhoto(photoIds) {
  return request({
    url: "/photo/batch",
    method: "delete",
    data: photoIds,
  });
}

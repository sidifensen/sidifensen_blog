import request from "@/utils/Request";

// 管理端删除图片
export function adminDeletePhoto(photoId) {
  return request({
    url: `/photo/admin/delete/${photoId}`,
    method: "delete",
  });
}

// 管理端批量删除图片
export function adminDeleteBatchPhoto(data) {
  return request({
    url: `/photo/admin/delete/batch`,
    method: "delete",
    data
  });
}

// 管理端审核图片
export function adminAuditPhoto(data){
  return request({
    url: `/photo/admin/audit`,
    method: "put",
    data
  });
}

// 管理端批量审核图片
export function adminAuditBatchPhoto(data){
  return request({
    url: `/photo/admin/auditBatch`,
    method: "put",
    data
  });
}

// 管理端获取图片列表
export function adminGetPhotoList(){
  return request({
    url: `/photo/admin/list`,
    method: "get",
  });
}

// 管理端搜索图片
export function adminSearchPhoto(data){
  return request({
    url: `/photo/admin/search`,
    method: "post",
    data
  });
}
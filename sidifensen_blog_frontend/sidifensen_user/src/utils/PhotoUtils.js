/**
 * 图片工具类
 * 提供图片相关的工具方法
 */

/**
 * 图片压缩函数 - 优化版本：质量压缩+尺寸调整
 * @param {File} file - 要压缩的图片文件
 * @param {number} quality - 压缩质量，默认为0.6
 * @param {number} maxWidth - 最大宽度，默认为1200
 * @param {number} maxHeight - 最大高度，默认为1200
 * @returns {Promise<File>} - 压缩后的图片文件
 */
export const compressImage = (file, quality = 0.6, maxWidth = 1920, maxHeight = 1080) => {
  return new Promise((resolve) => {
    // 读取文件
    const reader = new FileReader();
    // 将文件读取为base64编码的url
    reader.readAsDataURL(file);
    reader.onload = (e) => {
      const img = new Image();
      img.src = e.target.result;
      img.onload = () => {
        // 计算调整后的尺寸，保持比例
        let width = img.width;
        let height = img.height;

        // 如果图片尺寸超过最大限制，则等比例缩小
        if (width > maxWidth || height > maxHeight) {
          const ratio = Math.min(maxWidth / width, maxHeight / height);
          width = Math.floor(width * ratio);
          height = Math.floor(height * ratio);
        }

        // 创建Canvas进行压缩
        const canvas = document.createElement("canvas");
        canvas.width = width;
        canvas.height = height;

        // 获取Canvas 2D绘图上下文
        const ctx = canvas.getContext("2d");
        // 使用drawImage将图片绘制到Canvas上
        ctx.drawImage(img, 0, 0, width, height);

        // 总是转换为WebP格式以获得更好的压缩效果
        const mimeType = "image/webp";

        // 转换为Blob
        canvas.toBlob(
          (blob) => {
            // 生成新的文件名，更改扩展名为webp
            const nameWithoutExtension = file.name.substring(0, file.name.lastIndexOf("."));
            const fileName = `${nameWithoutExtension}.webp`;
            resolve(new File([blob], fileName, { type: mimeType }));
          },
          mimeType,
          quality
        );
      };
    };
  });
};
import { ElMessage } from "element-plus";
/**
 * 校验图片文件类型和大小
 * @param {File} file - 要校验的图片文件
 * @returns {boolean} - 校验结果
 */
export const validateImageFile = (file) => {
  // 文件类型校验
  const isJPG = file.type === "image/jpg";
  const isJPEG = file.type === "image/jpeg";
  const isPNG = file.type === "image/png";
  const isWEBP = file.type === "image/webp";
  const isGIF = file.type === "image/gif";
  // 文件大小校验（5MB）
  const isLt5M = file.size / 1024 / 1024 < 5;
  if (!isJPG && !isJPEG && !isPNG && !isWEBP && !isGIF) {
    ElMessage.error("上传图片只能是 jpg/jpeg/png/webp/gif 格式!");
    return false
  }
  if (!isLt5M) {
    ElMessage.error("上传图片大小不能超过 5MB!");
    return false
  }
  return true;
};

export default {
  compressImage,
  validateImageFile
};
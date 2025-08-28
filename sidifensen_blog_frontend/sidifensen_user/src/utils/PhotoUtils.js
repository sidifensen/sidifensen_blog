/**
 * 图片工具类
 * 提供图片相关的工具方法
 */

/**
 * 图片压缩函数 - 优化版本：质量压缩+尺寸调整
 * @param {File} file - 要压缩的图片文件
 * @param {number} quality - 压缩质量，默认为0.7
 * @param {number} maxWidth - 最大宽度，默认为1200
 * @param {number} maxHeight - 最大高度，默认为1200
 * @returns {Promise<File>} - 压缩后的图片文件
 */
export const compressImage = (file, quality = 0.7, maxWidth = 1920, maxHeight = 1080) => {
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

export default {
  compressImage
};
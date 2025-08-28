<template>
  <div class="editor-container">
    <EditorHeader></EditorHeader>
    <div ref="divRef" class="editor">
      <div class="aie-container">
        <div class="aie-container-header"></div>
        <div class="editor-content-wrapper">
          <!-- 左侧目录 -->
          <div class="aie-directory">
            <div class="directory-header">
              <h5>目录</h5>
            </div>
            <div class="directory-content" ref="directoryRef">
              <div v-if="!directoryItems.length" class="no-content">暂无内容</div>
              <div v-for="item in directoryItems" :key="item.id">
                <div @click="scrollToHeading(item.id)" :class="['directory-item', `level-${item.level}`, { active: activeHeadingId === item.id }]">
                  {{ item.text }}
                </div>
              </div>
            </div>
          </div>
          <!-- 右侧编辑器内容 -->
          <div class="editor-content">
            <div class="aie-container-main"></div>
            <div class="publish-settings">
              <h3>发布文章设置</h3>
              <div class="tag-setting">
                <label>文章标签</label>
                <el-tag class="tag-item" v-for="tag in tags" :key="tag" closable size="large" effect="plain" @close="deleteTag(tag)">
                  {{ tag }}
                </el-tag>
                <el-button size="small" icon="Plus" style="margin-left: 0px">添加文章标签</el-button>
              </div>
              <div class="cover-setting">
                <label>添加封面</label>
                <el-upload class="uploader" action="" :auto-upload="false" :show-file-list="false" :on-change="handleCoverChange">
                  <img v-if="coverImage" :src="coverImage" class="cover-image" />
                  <el-icon v-else class="avatar-icon"><Plus /></el-icon>
                </el-upload>
                <div class="cover-tip">暂无内容图片，请在正文中添加图片</div>
              </div>
              <div class="description-setting">
                <label>文章摘要</label>
                <div>
                  <el-input v-model="article.description" type="textarea" autosize placeholder="输入文章摘要"  :autosize="{ minRows: 2, maxRows: 4 }" maxlength="256" show-word-limit></el-input>
                  <el-button size="small" style="margin-top: 8px" @click="extractSummary">AI提取摘要</el-button>
                </div>
              </div>
              <div class="setting-item">
                <label>分类专栏</label>
                <el-tag v-for="tag in tags" :key="tag" closable :disable-transitions="false" @close="handleClose(tag)">
                  {{ tag }}
                </el-tag>
                <el-button size="small">+ 新建分类专栏</el-button>
              </div>
              <div class="setting-item">
                <label>文章类型</label>
                <el-radio-group v-model="articleType">
                  <el-radio label="0">原创</el-radio>
                  <el-radio label="1">转载</el-radio>
                </el-radio-group>
              </div>
              <div class="setting-item">
                <label>可见范围</label>
                <el-radio-group v-model="visibility">
                  <el-radio label="0">全部可见</el-radio>
                  <el-radio label="1">仅我可见</el-radio>
                  <el-radio label="2">粉丝可见</el-radio>
                  <el-radio label="3">VIP可见</el-radio>
                </el-radio-group>
              </div>
            </div>
          </div>
        </div>
        <div class="aie-container-footer"></div>
      </div>
    </div>
    <div class="footer">
      <div class="left">
        <div>回到顶部</div>
        <div>文章设置</div>
      </div>
      <div class="right">
        <el-button type="primary" @click="handleClickPublish">发布文章</el-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, nextTick } from "vue";
import EditorHeader from "@/components/EditorHeader.vue";
import { AiEditor } from "aieditor";
import "aieditor/dist/style.css";
import { useDarkStore } from "@/stores/darkStore";
import { watch } from "vue";
import { storeToRefs } from "pinia";
import { ElMessage } from "element-plus";
import { compressImage } from "@/utils/PhotoUtils";
import { getTagList } from "@/api/tag";

const darkStore = useDarkStore();
const { isDark } = storeToRefs(darkStore);

// 创建div引用
const divRef = ref(null);
const directoryRef = ref(null);
// 定义编辑器实例变量
let aiEditor = null;
// 目录项数据
const directoryItems = ref([]);
// 当前激活的标题id
const activeHeadingId = ref(null);

// 监听主题变化
watch(isDark, (newTheme) => {
  if (aiEditor) {
    const themeValue = newTheme ? "dark" : "light";
    aiEditor.changeTheme(themeValue);
  }
});

// 在组件挂载后初始化编辑器
onMounted(() => {
  if (divRef.value) {
    aiEditor = new AiEditor({
      element: divRef.value,
      placeholder: "点击输入内容...",
      theme: isDark.value ? "dark" : "light",
      contentRetention: true, // 开启内容保留
      contentRetentionKey: "editor-content", // 保存本地的key
      htmlPasteConfig: {
        pasteAsText: false, // 粘贴为文本
      },
      content: "",
      draggable: false, // 禁用拖动
      toolbarExcludeKeys: ["subscript", "superscript", "break", "video", "source-code", "printer"], // 排除下标,上标.强制换行,视频,源代码,打印
      onSave: (editor) => {
        ElMessage.success("文档保存成功！");
        return true;
      }, // 保存回调
      // 监听编辑器内容变化，更新目录（使用防抖处理）
      onChange: () => {
        updateDirectoryDebounced();
        return true;
      },
    });

    // 初始化时直接更新目录（不需要防抖）
    nextTick(() => {
      updateDirectory();
    });

    // 设置定期更新目录的定时器（使用防抖处理）
    // 注意：这个定时器会被外部的onUnmounted钩子清理
    const interval = setInterval(() => {
      updateDirectoryDebounced(1000); // 1秒延迟的防抖，避免频繁更新
    }, 10000); // 每10秒触发一次

    // 组件卸载时清除定时器
    onUnmounted(() => {
      clearInterval(interval);
    });
  }
});

// 防抖计时器变量
let directoryUpdateTimer = null;

// 存储标题ID到实际DOM元素的映射
const headingElementsMap = new Map();

// 更新文章目录
// 从编辑器内容中提取所有标题元素(h1-h6)，处理后生成文章目录结构
const updateDirectory = () => {
  if (!aiEditor) return;
  try {
    // 获取编辑器内容
    const content = aiEditor.getHtml();
    // 创建DOMParser实例用于解析HTML字符串
    const parser = new DOMParser();
    // 将HTML字符串解析为DOM文档对象
    const doc = parser.parseFromString(content, "text/html");
    // 获取所有标题元素(h1-h6)
    const headings = doc.querySelectorAll("h1, h2, h3, h4, h5, h6");
    const newItems = [];
    // 清空之前的映射
    headingElementsMap.clear();
    // 遍历所有标题元素，提取标题信息
    headings.forEach((heading, index) => {
      // 提取标题级别(1-6)
      const level = parseInt(heading.tagName.substring(1));
      // 获取标题文本内容并去除首尾空白
      const text = heading.textContent.trim();
      // 为每个标题生成唯一ID
      const id = `heading-${index}`;
      // 为标题元素添加id，便于后续锚点跳转
      heading.id = id;
      // 将标题信息添加到数组中
      newItems.push({ id, text, level, index });
      // 保存标题文本、级别和索引到ID的映射，用于后续查找
      headingElementsMap.set(id, { text, level, index });
    });
    // 更新目录数据
    directoryItems.value = newItems;
  } catch (error) {
    console.error("更新目录时出错:", error);
  }
};

/**
 * 带防抖功能的目录更新函数
 * 防止编辑器内容频繁变化时导致目录更新过于频繁
 * @param {number} delay - 防抖延迟时间(毫秒)，默认为1000ms
 */
const updateDirectoryDebounced = (delay = 1000) => {
  // 清除之前的计时器
  if (directoryUpdateTimer) {
    clearTimeout(directoryUpdateTimer);
  }
  // 设置新的延迟执行
  directoryUpdateTimer = setTimeout(() => {
    updateDirectory();
  }, delay);
};

// 滚动到指定标题
const scrollToHeading = (id) => {
  try {
    // 从映射中获取标题信息
    const headingInfo = headingElementsMap.get(id);
    if (!headingInfo) {
      console.error(`未找到ID为${id}的标题`);
      return;
    }
    // 获取标题文本、级别和索引
    const { text, level, index } = headingInfo;
    // 获取编辑器内容区域
    const contentArea = document.querySelector(".editor-content");
    if (!contentArea) {
      console.error("未找到编辑器内容区域");
      return;
    }
    // 获取所有标题元素
    const allHeadings = Array.from(contentArea.querySelectorAll("h1, h2, h3, h4, h5, h6"));
    let targetElement = null;
    // 索引定位
    if (allHeadings.length > index) {
      targetElement = allHeadings[index];
    }
    // 如果找到目标元素，则滚动到该位置
    if (targetElement) {
      // 更新当前激活的标题ID
      activeHeadingId.value = id;
      // 执行滚动操作
      targetElement.scrollIntoView({ behavior: "smooth", block: "start" });
    } else {
      console.error(`未找到ID为"${id}"或包含文本"${text}"的标题元素`);
    }
  } catch (error) {
    console.error("滚动到标题时出错:", error);
  }
};

// 处理封面文件选择变化
const handleCoverChange = async (file) => {
  // 获取文件对象
  const rawFile = file.raw;
  // 文件类型和大小校验
  const isJPG = rawFile.type === "image/jpeg";
  const isPNG = rawFile.type === "image/png";
  const isJPEG = rawFile.type === "image/jpeg";
  const isWEBP = rawFile.type === "image/webp";
  const isLt5M = rawFile.size / 1024 / 1024 < 5;
  if (!isJPG && !isPNG && !isJPEG && !isWEBP) {
    ElMessage.error("上传封面图片只能是 jpg/png/jpeg/webp 格式!");
    return;
  }
  if (!isLt5M) {
    ElMessage.error("上传封面图片大小不能超过 5MB!");
    return;
  }
  // 压缩图片并设置预览
  const cover = await compressImage(rawFile);
  // 将压缩后的File对象转换为base64编码的URL，以便在img标签中显示
  const reader = new FileReader();
  reader.readAsDataURL(cover);
  reader.onload = () => {
    coverImage.value = reader.result;
    ElMessage.success("封面图片已上传成功");
  };
};

// AI提取摘要
const extractSummary = () => {
  // 这里应该调用实际的AI摘要接口
  // 这里只是一个示例
  ElMessage.success("AI摘要提取中...");
  // 模拟API调用延迟
  setTimeout(() => {
    ElMessage.success("AI摘要提取完成");
  }, 1000);
};

// 处理发布文章
const handleClickPublish = () => {
  // 这里应该调用实际的发布文章接口
  ElMessage.success("文章发布成功!");
};

// 在组件卸载前销毁编辑器实例、移除事件监听器并清理防抖计时器
onUnmounted(() => {
  // 销毁编辑器实例
  aiEditor && aiEditor.destroy();
  // 清理防抖计时器
  if (directoryUpdateTimer) {
    clearTimeout(directoryUpdateTimer);
  }
});

const article = ref({
  tag: "java,vue,springboot",
  title: "",
  description: "",
  content: "",
  coverUrl: "",
  reprintType: 0,
  visibleRange: 0,
});

// 封面图片URL
const coverImage = ref("");

// 所有标签
const allTags = ref([]);
getTagList().then((res) => {
  allTags.value = res.data.data;
  console.log(allTags.value);
});

// 当前标签
const tags = ref(article.value.tag.split(","));
// 删除标签
const deleteTag = (tag) => {
  tags.value = tags.value.filter((item) => item !== tag);
};
</script>

<style lang="scss" scoped>
.editor-container {
  display: flex;
  flex-direction: column;
  background: #f3f4f6;
  width: 100vw;

  // 编辑器
  .editor {
    margin-top: 48px;
    flex: 1;
    padding: 0;
    height: 100%;
    display: flex;
    flex-direction: column;
    // 编辑器头部
    .aie-container-header {
      display: flex;
      position: fixed;
      z-index: 1;
      border: none;
      justify-content: center;
      width: 100vw;
      :deep(aie-header) {
        width: 100vw;
        div {
          display: flex;
          justify-content: center;
        }
      }
    }
    // 编辑器内容包装器
    .editor-content-wrapper {
      padding-top: 68px;
      padding-bottom: 48px;
      display: flex;
      background: var(--el-border-color-lighter);
      // 左侧目录样式
      .aie-directory {
        max-width: 20vw;
        background: var(--el-bg-color);
        border: 1px solid var(--el-border-color);
        border-radius: 4px;
        margin-left: 24px;
        height: calc(100vh - 200px);
        padding: 10px;
        display: flex;
        flex-direction: column;
        .directory-header {
          padding: 12px 16px;
          border-radius: 4px;
          border-bottom: 1px solid var(--el-border-color);
          background: var(--el-bg-color);
          h5 {
            margin: 0;
            font-size: 18px;
            font-weight: 500;
          }
        }
        .directory-content {
          flex: 1;
          overflow-y: auto;
          padding: 8px 0;

          // 目录项样式
          .directory-item {
            display: block;
            padding: 6px 16px;
            color: var(--el-text-color-primary);
            text-decoration: none;
            border-radius: 4px;
            transition: all 0.3s ease;
            cursor: pointer;

            // 不同级别的标题缩进
            &.level-1 {
              padding-left: 16px;
            }
            &.level-2 {
              padding-left: 32px;
            }
            &.level-3 {
              padding-left: 48px;
            }
            &.level-4 {
              padding-left: 64px;
            }
            &.level-5 {
              padding-left: 80px;
            }
            &.level-6 {
              padding-left: 96px;
            }
            &:hover {
              background-color: var(--el-border-color-light);
              color: var(--el-color-primary);
            }
            &.active {
              background-color: var(--el-color-primary-light-9);
              color: var(--el-color-primary);
              font-weight: 500;
            }
          }
          .no-content {
            text-align: center;
            color: #9ca3af;
            padding: 20px;
            font-size: 14px;
          }
          .directory-item {
            display: block;
            padding: 6px 16px;
            color: var(--el-text-color-regular);
            text-decoration: none;
            font-size: 14px;
            line-height: 20px;
            white-space: nowrap;
            overflow: hidden;
            text-overflow: ellipsis;
            cursor: pointer;
            transition: all 0.2s ease;
            &:hover {
              color: var(--el-color-primary);
            }
            // 不同级别标题的缩进
            &.level-1 {
              font-weight: 500;
              padding-left: 16px;
            }
            &.level-2 {
              padding-left: 32px;
            }
            &.level-3 {
              padding-left: 48px;
            }
            &.level-4 {
              padding-left: 64px;
            }
            &.level-5 {
              padding-left: 80px;
            }
            &.level-6 {
              padding-left: 96px;
            }
          }
        }
      }

      // 编辑器内容
      .editor-content {
        height: calc(100vh - 168px);
        overflow-y: auto;
        overflow-x: hidden;
        margin: auto;
        width: 60vw;
        border-radius: 8px;
        .aie-container-main {
          background: var(--el-bg-color);
          border: 1px solid var(--el-border-color);
          min-height: calc(100vh - 100px);
          padding: 16px;
          margin-bottom: 24px;
        }
        // 发布文章设置样式
        .publish-settings {
          padding: 16px;
          background: var(--el-bg-color);
          border: 1px solid var(--el-border-color);
          h3 {
            margin-top: 0;
            margin-bottom: 16px;
            font-size: 18px;
            font-weight: 600;
          }
          label {
            margin-right: 16px;
            font-weight: 500;
            color: var(--el-text-color-primary);
          }
          .tag-item {
            margin-right: 10px;
          }
          // 标签设置
          .tag-setting {
            display: flex;
            align-items: center;
          }
          // 封面设置
          .cover-setting {
            display: flex;
            height: 150px;
            align-items: center;
            // 封面上传样式
            .uploader {
              display: flex;
              flex-direction: column;
              align-items: center;
              justify-content: center;
              margin-bottom: 8px;
              width: 192px;
              height: 108px;
              display: block;
              border: 1px solid var(--el-border-color);
              border-radius: 6px;
              cursor: pointer;
              position: relative;
              overflow: hidden;
              transition: var(--el-transition-duration-fast);
              .cover-image {
                width: 192px;
                height: 108px;
                border-radius: 4px;
              }
              .avatar-icon {
                font-size: 28px;
                color: #8c939d;
                width: 192px;
                height: 108px;
                text-align: center;
              }
            }
            //图片选择
            .cover-tip {
              width: 129px;
              height: 81px;
              color: var(--el-text-color-secondary);
              font-size: 12px;
              margin-left: 16px;
              border: 1px solid var(--el-border-color);
            }
          }
          // 文章摘要设置
          .description-setting {
            display: flex;
            align-items: center;
            height: 100px;
          }
        }
      }
    }
    .aie-container-footer {
      display: none;
    }
  }
  .footer {
    position: fixed;
    bottom: 0px;
    left: 0;
    width: 100%;
    height: 48px;
    background-color: var(--el-bg-color);
    display: flex;
    justify-content: space-evenly;
    align-items: center;
    .right {
      margin-right: 16px;
    }
  }
}

@media screen and (max-width: 1024px) {
  .editor-container {
    .editor {
      .aie-container {
        .editor-content-wrapper {
          padding-top: 0; // 间隙
          .aie-directory {
            display: none; // 隐藏目录
          }
          .editor-content {
            margin-top: 100px;
            height: calc(100vh - 68px);
            width: 100vw; // 编辑文章区域
          }
        }
      }
    }
  }
}

@media screen and (max-width: 768px) {
  .editor-container {
    .editor {
      .aie-container {
        .editor-content-wrapper {
          .editor-content {
            margin-top: 120px;
          }
        }
      }
    }
  }
}
</style>

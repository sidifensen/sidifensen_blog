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
                <a :href="`#${item.id}`" @click.prevent="scrollToHeading(item.id)" :class="['directory-item', `level-${item.level}`]">
                  {{ item.text }}
                </a>
              </div>
            </div>
          </div>
          <!-- 右侧编辑器内容 -->
          <div class="editor-content">
            <div class="aie-container-main"></div>
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

const darkStore = useDarkStore();
const { isDark } = storeToRefs(darkStore);

// 创建div引用
const divRef = ref(null);
const directoryRef = ref(null);
// 定义编辑器实例变量
let aiEditor = null;
// 目录项数据
const directoryItems = ref([]);

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
      // 执行滚动操作
      targetElement.scrollIntoView({ behavior: "smooth", block: "start" });
    } else {
      console.error(`未找到ID为"${id}"或包含文本"${text}"的标题元素`);
    }
  } catch (error) {
    console.error("滚动到标题时出错:", error);
  }
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
</script>

<style lang="scss" scoped>
// 全局样式 - 标题高亮动画
// :global {
//   .highlighted {
//     animation: highlight 2s ease-in-out;
//   }

//   @keyframes highlight {
//     0% {
//       background-color: rgba(255, 215, 0, 0.5);
//     }
//     100% {
//       background-color: transparent;
//     }
//   }
// }

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
        height: calc(100vh - 188px);
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
        overflow: auto;
        margin: auto;
        width: 60vw;
        overflow: auto;
        background: var(--el-bg-color);
        border: 1px solid var(--el-border-color);
        border-radius: 8px;
        .aie-container-main {
          min-height: calc(100vh - 100px);
          padding: 16px;
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

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
            <div ref="directoryRef" class="directory-content">
              <div v-if="!directoryItems.length" class="no-content">暂无内容</div>
              <div v-for="item in directoryItems" :key="item.id">
                <div :class="['directory-item', `level-${item.level}`, { active: activeHeadingId === item.id }]" @click="scrollToHeading(item.id)">
                  {{ item.text }}
                </div>
              </div>
            </div>
          </div>
          <!-- 右侧编辑器内容 -->
          <div class="editor-content">
            <!-- 文章标题区域 -->
            <div class="article-title-container">
              <input v-model="article.title" class="article-title-input" maxlength="50" placeholder="请输入文章标题..." type="text" />
            </div>
            <!-- 文章正文区域 -->
            <div class="aie-container-main"></div>
            <div class="publish-settings">
              <h3>发布文章设置</h3>
              <div class="tag-setting">
                <label>文章标签</label>
                <div class="tag-item-container">
                  <el-tag v-for="tag in tags" :key="tag" class="tag-item" closable effect="plain" size="large" @close="deleteTag(tag)">
                    {{ tag }}
                  </el-tag>
                  <el-button :disabled="tags.length >= 5" class="tag-add-button" icon="Plus" size="small" @click="showTagSelector">添加文章标签 </el-button>
                </div>
                <div v-if="isTagSelectorVisible" class="tag-selector-container">
                  <div class="tag-selector">
                    <div class="tag-selector-header">
                      <h4>标签</h4>
                      <el-icon class="close-icon" @click="closeTagSelector">
                        <Close />
                      </el-icon>
                    </div>
                    <!-- 搜索标签 -->
                    <div class="tag-search-container">
                      <el-input v-model="tagSearchKeyword" :disabled="tags.length >= 5" placeholder="请输入文字搜索" size="small" @input="handleTagSearch" @keyup.enter="addCustomTag">
                        <template #prefix>
                          <el-icon :size="16">
                            <Search />
                          </el-icon>
                        </template>
                      </el-input>
                      <div v-if="isSearchResultVisible && searchResults.length > 0" class="search-result-dropdown">
                        <div v-for="result in searchResults" :key="result.id" class="search-result-item" @click="selectTag(result)">
                          {{ result.name }}
                        </div>
                      </div>
                    </div>
                    <!-- 标签列表 -->
                    <div class="tag-container">
                      <!-- 标签数量限制提示遮盖层 -->
                      <div v-if="tags.length >= 5" class="tag-limit-overlay">
                        <span>最多只能添加5个标签</span>
                      </div>
                      <div class="tag-category-list">
                        <div v-for="category in tagCategories" :key="category" :class="{ active: activeCategory === category }" class="tag-category-item" @click="selectCategory(category)">
                          {{ category }}
                        </div>
                      </div>
                      <div class="tag-list">
                        <div class="available-tags-section">
                          <el-tag v-for="tag in getTagsByCategory(activeCategory)" :key="tag.id" :class="{ 'tag-item-active': tags.includes(tag.name) }" :disabled="tags.length >= 5 && !tags.includes(tag.name)" class="available-tag" size="small" @click="toggleTag(tag)">
                            {{ tag.name }}
                          </el-tag>
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
              <div class="cover-setting">
                <label>添加封面</label>
                <div class="cover-container">
                  <el-upload :auto-upload="true" :http-request="handleCoverUpload" :show-file-list="false" action="" class="uploader" list-type="picture">
                    <img v-if="article.coverUrl || coverImage" :src="article.coverUrl || coverImage" class="cover-image" />
                    <el-icon v-else class="avatar-icon">
                      <Plus />
                    </el-icon>
                  </el-upload>
                  <div class="cover-tip">暂无内容图片，请在正文中添加图片</div>
                </div>
              </div>
              <div class="description-setting">
                <label>文章摘要</label>
                <div class="description-container">
                  <el-input v-model="article.description" :autosize="{ minRows: 2, maxRows: 4 }" class="description-input" maxlength="256" placeholder="输入文章摘要" resize="none" type="textarea"></el-input>
                  <div class="ai-summary-actions">
                    <el-button icon="EditPen" plain round size="small" type="danger" @click="extractSummary">AI提取摘要</el-button>
                    <span v-if="aiQuota !== null" class="ai-quota-text">今日剩余: {{ aiQuota }}/10 次</span>
                  </div>
                </div>
              </div>
              <div class="column-setting">
                <label>分类专栏</label>
                <div class="column-tags-container">
                  <div class="column-item-container">
                    <el-tag v-for="column in columns" :key="column.id" class="column-item" closable effect="plain" size="large" @close="deleteColumn(column)">
                      {{ column.name }}
                    </el-tag>
                    <div class="column-actions">
                      <el-button v-if="!inputVisible" icon="Plus" size="small" @click="showInputColumn" @mouseenter="showColumnListOnHover" @mouseleave="hideColumnListOnLeave">新增专栏 </el-button>
                      <el-input v-if="inputVisible" ref="InputColumnRef" v-model="inputColumn" class="column-input" size="small" @blur="handleColumnInputBlur" @keyup.enter="addNewColumnn" />
                    </div>
                  </div>
                  <div v-if="showColumnDropdown" class="column-dropdown" @mouseenter="handleColumnDropdownEnter" @mouseleave="handleColumnDropdownLeave">
                    <div v-if="columns.length >= 3" class="column-limit-overlay">
                      <span>最多选择三个专栏</span>
                    </div>
                    <div class="column-list">
                      <div v-for="item in allColumns" :key="item.id" :class="{ selected: isColumnSelected(item.id), disabled: columns.length >= 3 && !isColumnSelected(item.id) }" class="column-option" @click="selectColumn(item)">
                        <el-checkbox :checked="isColumnSelected(item.id)" :disabled="columns.length >= 3 && !isColumnSelected(item.id)">
                          <span>{{ item.name }}</span>
                        </el-checkbox>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
              <div class="reprint-type-setting">
                <label>文章类型</label>
                <el-radio-group v-model="article.reprintType">
                  <el-radio :label="0">原创</el-radio>
                  <el-radio :label="1">转载</el-radio>
                </el-radio-group>
              </div>
              <!-- 转载链接输入框 -->
              <div v-if="article.reprintType === 1" class="reprint-url-setting">
                <label>转载链接</label>
                <el-input v-model="article.reprintUrl" placeholder="请输入转载文章的原始链接" clearable style="width: 300px" />
              </div>
              <div class="visible-range-setting">
                <label>可见范围</label>
                <el-radio-group v-model="article.visibleRange">
                  <el-radio :label="0">全部可见</el-radio>
                  <el-radio :label="1">仅我可见</el-radio>
                  <el-radio :label="2">粉丝可见</el-radio>
                  <el-radio :label="3">VIP可见</el-radio>
                </el-radio-group>
              </div>
            </div>
          </div>
          <!-- 右下角回到顶部按钮 -->
          <div class="back-to-top" @click="scrollToTop">
            <el-icon>
              <ArrowUp />
            </el-icon>
          </div>
        </div>
        <div class="aie-container-footer"></div>
      </div>
    </div>
    <div class="footer">
      <div class="left">字数统计: {{ wordCount }}字</div>
      <div class="center">
        <el-button icon="ArrowDown" @click="scrollToArticleSettings">文章设置</el-button>
      </div>
      <div class="right">
        <el-button v-if="!isDraft" :loading="isSavingDraft" :disabled="isPublishing" @click="handleSaveDraft">保存草稿</el-button>
        <el-button type="primary" :loading="isPublishing" :disabled="isSavingDraft" @click="handleClickPublish">发布文章</el-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { nextTick, onMounted, onUnmounted, ref, watch } from "vue";
import { useRoute, useRouter } from "vue-router";
import EditorHeader from "@/components/EditorHeader.vue";
import { AiEditor } from "aieditor";
import "aieditor/dist/style.css";
import { useDarkStore } from "@/stores/darkStore";
import { storeToRefs } from "pinia";
import { compressImage, validateImageFile } from "@/utils/PhotoUtils";
import { getTagList } from "@/api/tag";
import { addColumn, getColumnList } from "@/api/column";
import { uploadArticlePhoto } from "@/api/photo";
import { ArrowUp, Close, Search } from "@element-plus/icons-vue";
import { addArticle, getArticleDetail, saveDraft, updateArticle } from "@/api/article";
import { extractSummary as extractSummaryApi, getAiQuota } from "@/api/ai";

const darkStore = useDarkStore();
const { isDark } = storeToRefs(darkStore);

// 获取路由实例
const route = useRoute();
const router = useRouter();

// 监听页面刷新事件，弹出确认框
const handleBeforeUnload = (e) => {
  // isModified 将在后面定义
  if (isModified?.value) {
    // 现代浏览器标准实现方式
    e.preventDefault();
    e.returnValue = "";
    // 为了兼容不同浏览器，直接返回一个非空字符串
    return "";
  }
  // 如果内容未修改，不阻止页面刷新
  return undefined;
};

// 字数统计
const wordCount = ref(0);

// 滚动到页面顶部
const scrollToTop = () => {
  window.scrollTo({ top: 0, behavior: "smooth" });
};

// 滚动到文章设置区域
const scrollToArticleSettings = () => {
  const articleSettingsElement = document.querySelector(".publish-settings");
  if (articleSettingsElement) {
    articleSettingsElement.scrollIntoView({ behavior: "smooth", block: "start" });
  }
};

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

// 计算字数统计
const countWords = () => {
  if (!aiEditor) return;
  try {
    // 获取编辑器内容
    const content = aiEditor.getHtml();
    // 创建DOMParser实例用于解析HTML字符串
    const parser = new DOMParser();
    // 将HTML字符串解析为DOM文档对象
    const doc = parser.parseFromString(content, "text/html");
    // 获取纯文本内容
    const text = doc.body.textContent || "";
    // 计算纯文本字数（去除多余的空白字符）
    const words = text.replace(/\s+/g, "").length;
    // 更新字数统计
    wordCount.value = words;
  } catch (error) {
    console.error("计算字数时出错:", error);
  }
};

// 文章数据
const article = ref({
  tag: "",
  title: "",
  description: "",
  content: "",
  coverUrl: "",
  reprintType: 0,
  visibleRange: 0,
  columnIds: [],
  reprintUrl: "", // 转载链接
});

// 当前选择的专栏
const columns = ref([]);

// 是否是草稿
const isDraft = ref(false);

// 发布状态管理
const isPublishing = ref(false);
// 保存草稿状态管理
const isSavingDraft = ref(false);

// 根据文章ID获取文章详情并回显数据
const loadArticleDetail = async () => {
  try {
    // 从路由参数中获取articleId
    const articleId = route.query.articleId;
    if (articleId && !isNaN(articleId)) {
      // 调用获取文章详情接口
      const response = await getArticleDetail(articleId);
      const articleData = response.data.data;

      // 填充文章基本信息
      if (articleData) {
        article.value.id = articleData.id || "";
        article.value.title = articleData.title || "";
        article.value.description = articleData.description || "";
        article.value.content = articleData.content || "";
        article.value.coverUrl = articleData.coverUrl || "";
        article.value.reprintType = articleData.reprintType || 0;
        article.value.visibleRange = articleData.visibleRange || 0;
        article.value.columnIds = articleData.columnIds || [];
        article.value.reprintUrl = articleData.reprintUrl || ""; // 转载链接
        isDraft.value = articleData.editStatus === 1;

        // 填充标签
        if (articleData.tags && articleData.tags.length > 0) {
          tags.value = [...articleData.tags]; // 创建新数组避免引用问题
          article.value.tag = articleData.tags.join(",");
        } else if (articleData.tag) {
          // 兼容旧格式
          tags.value = articleData.tag.split(",").filter((tag) => tag.trim() !== "");
          article.value.tag = articleData.tag;
        } else {
          // 如果没有标签数据，初始化为空数组
          tags.value = [];
          article.value.tag = "";
        }

        // 填充专栏
        if (articleData.columns && articleData.columns.length > 0) {
          columns.value = articleData.columns;
          // 同步更新article中的columnIds
          article.value.columnIds = articleData.columns.map((column) => column.id);
        } else if (articleData.columnIds && articleData.columnIds.length > 0 && allColumns.value.length > 0) {
          // 如果只有columnIds，从allColumns中匹配名称
          columns.value = allColumns.value.filter((column) => articleData.columnIds.includes(column.id));
          // 同步更新article中的columnIds
          article.value.columnIds = articleData.columnIds;
        }

        ElMessage.success("文章内容加载成功");
      }
    }
  } catch (error) {
    console.error("加载文章详情失败:", error);
    ElMessage.error("加载文章详情失败，请重试");
  }
};

// 主onMounted钩子 - 合并所有初始化逻辑
onMounted(async () => {
  // 加载用户文章数据
  await loadArticleDetail();

  // 获取AI配额
  await fetchAiQuota();

  // 添加页面刷新事件监听
  window.addEventListener("beforeunload", handleBeforeUnload);

  if (divRef.value) {
    aiEditor = new AiEditor({
      element: divRef.value,
      placeholder: "点击输入内容...",
      theme: isDark.value ? "dark" : "light",
      htmlPasteConfig: {
        pasteAsText: false, // 粘贴为文本
      },
      content: article.value.content, // 绑定文章内容（已提前加载）
      draggable: false, // 禁用拖动
      // 图片上传配置
      image: {
        allowBase64: false,
        defaultSize: 100, // 修改默认尺寸为100%以适应容器宽度
        // 自定义图片上传逻辑
        uploader: (file) => {
          return new Promise((resolve, reject) => {
            uploadArticlePhoto(file)
              .then((response) => {
                // 转换为 aiEditor 要求的格式
                resolve({
                  errorCode: 0,
                  data: {
                    src: response.data.data,
                    alt: "文章图片",
                  },
                });
              })
              .catch((error) => {
                // 处理请求异常情况
                console.error("上传异常:", error);
                if (error.msg || error.message) {
                  reject(new Error(error.msg || error.message));
                } else {
                  reject(new Error("图片上传失败")); // 返回给onError
                }
              });
          });
        },
        // 图片上传事件监听
        uploaderEvent: {
          onUploadBefore: (file) => {
            // 文件类型和大小校验
            if (!validateImageFile(file)) {
              return false;
            }
            ElMessage.info("图片上传中...");
            return true;
          },
          onSuccess: (file, response) => {
            ElMessage.success("图片上传成功");
            return true;
          },
          onFailed: (file, response) => {
            ElMessage.error(response?.msg || "图片上传失败");
          },
          onError: (file, error) => {
            ElMessage.error("上传出错: " + error.message);
          },
        },
        bubbleMenuItems: ["delete"], // 选中图片时的浮动菜单配置, 只显示删除
      },
      // 排除下标,上标.强制换行,视频,源代码,打印,全屏,附件
      toolbarExcludeKeys: ["subscript", "superscript", "break", "video", "source-code", "printer", "fullscreen", "attachment", "ai"],
      onSave: (editor) => {
        ElMessage.success("文档保存成功！");
        return true;
      },
      // 监听编辑器内容变化，更新article.content、目录和字数统计（使用防抖处理）
      onChange: () => {
        updateDirectoryDebounced();
        countWords();
        return true;
      },
    });

    // 初始化时直接更新目录和字数统计（不需要防抖）
    nextTick(() => {
      updateDirectory();
      countWords();
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

// 主onUnmounted钩子 - 合并所有清理逻辑
onUnmounted(() => {
  // 销毁编辑器实例
  aiEditor && aiEditor.destroy();
  // 清理防抖计时器
  if (directoryUpdateTimer) {
    clearTimeout(directoryUpdateTimer);
  }
  // 移除页面刷新事件监听
  window.removeEventListener("beforeunload", handleBeforeUnload);
});

// 标记内容是否已修改(阻止刷新页面)
const isModified = ref(false);

// 监听article变化，更新isModified标志和本地存储
watch(
  () => [article.value.title, article.value.content],
  ([newTitle, newContent]) => {
    // 检查标题是否有实际内容
    const hasTitle = newTitle && newTitle.trim().length > 0;

    // 检查内容是否有实际文本（移除HTML标签后）
    let hasContent = false;
    if (newContent) {
      // 创建临时元素解析HTML
      const tempDiv = document.createElement("div");
      tempDiv.innerHTML = newContent;
      // 获取纯文本并检查是否有实际内容
      const textContent = tempDiv.textContent || tempDiv.innerText || "";
      hasContent = textContent.trim().length > 0;
    }

    // 更新isModified标志
    isModified.value = hasTitle || hasContent;
  },
  { deep: true, immediate: true }
);

// 所有标签 - 后端返回 Map<String, List<Tag>>
const allTags = ref({});
getTagList().then((res) => {
  // 后端返回的是分组的标签数据: { "前沿技术": [{id, category, name}, ...], ... }
  allTags.value = res.data.data;
});

// 标签选择器显示状态
const isTagSelectorVisible = ref(false);
// 标签搜索关键词
const tagSearchKeyword = ref("");
// 搜索结果是否可见
const isSearchResultVisible = ref(false);
// 搜索结果
const searchResults = ref([]);
// 标签分类列表
const tagCategories = ref([]);
// 当前选中的分类
const activeCategory = ref("");

// 监听allTags变化，初始化标签分类
watch(
  allTags,
  (newTags) => {
    if (newTags && typeof newTags === "object") {
      tagCategories.value = Object.keys(newTags);
      if (tagCategories.value.length > 0) {
        activeCategory.value = tagCategories.value[0];
      }
    }
  },
  { immediate: true }
);

// 显示标签选择器
const showTagSelector = () => {
  isTagSelectorVisible.value = true;
};

// 关闭标签选择器
const closeTagSelector = () => {
  isTagSelectorVisible.value = false;
  tagSearchKeyword.value = "";
  isSearchResultVisible.value = false;
  searchResults.value = [];
};

// 处理标签搜索
const handleTagSearch = () => {
  if (!tagSearchKeyword.value.trim()) {
    isSearchResultVisible.value = false;
    searchResults.value = [];
    return;
  }
  const keyword = tagSearchKeyword.value.toLowerCase();
  const results = [];
  // 在所有标签中搜索
  if (allTags.value && typeof allTags.value === "object") {
    Object.values(allTags.value).forEach((tagArray) => {
      tagArray.forEach((tag) => {
        // tag 现在是对象 {id, category, name}
        if (tag.name.toLowerCase().includes(keyword) && !results.find((r) => r.name === tag.name)) {
          results.push(tag);
        }
      });
    });
  }
  searchResults.value = results;
  isSearchResultVisible.value = results.length > 0;
};

// 选择搜索结果中的标签
const selectTag = (tag) => {
  toggleTag(tag);
  tagSearchKeyword.value = "";
  isSearchResultVisible.value = false;
  searchResults.value = [];
};

// 添加自定义标签
const addCustomTag = () => {
  // // 限制最多添加5个标签
  // if (tags.value.length >= 5) {
  //   ElMessage.warning("最多只能添加5个标签");
  //   return;
  // }
  // const customTag = tagSearchKeyword.value.trim();
  // if (customTag && !tags.value.includes(customTag)) {
  //   tags.value.push(customTag);
  //   tagSearchKeyword.value = "";
  //   isSearchResultVisible.value = false;
  //   searchResults.value = [];
  // }
  ElMessage.warning("标签只能由后台管理员添加");
  return;
};

// 选择分类
const selectCategory = (category) => {
  activeCategory.value = category;
};

// 根据分类获取标签
const getTagsByCategory = (category) => {
  if (allTags.value && allTags.value[category]) {
    return allTags.value[category];
  }
  return [];
};

// 当前标签（存储标签名称字符串）
const tags = ref([]);

// 切换标签选中状态
const toggleTag = (tag) => {
  const tagName = tag.name;
  const index = tags.value.indexOf(tagName);
  if (index > -1) {
    tags.value.splice(index, 1);
  } else if (tags.value.length < 5) {
    // 限制最多添加5个标签
    tags.value.push(tagName);
  } else {
    ElMessage.warning("最多只能添加5个标签");
    return;
  }
  // 更新article中的tag值，确保数据同步
  article.value.tag = tags.value.join(",");
};

// 删除标签
const deleteTag = (tag) => {
  tags.value = tags.value.filter((item) => item !== tag);
  // 同步更新article中的tag值，确保数据同步
  article.value.tag = tags.value.join(",");
};

// 封面图片URL
const coverImage = ref("");
// 处理封面图片自动上传
const handleCoverUpload = async (options) => {
  const { file } = options;
  try {
    // 使用工具类校验文件类型和大小
    const validation = validateImageFile(file);
    if (!validation) {
      options.onError && options.onError();
      return;
    }
    // 压缩图片
    const compressedFile = await compressImage(file);
    // 上传到服务器
    ElMessage.info("封面图片上传中...");
    const response = await uploadArticlePhoto(compressedFile);
    // 将服务器返回的URL赋值给coverImage
    coverImage.value = response.data.data;
    article.value.coverUrl = response.data.data;
    // 调用成功回调
    options.onSuccess && options.onSuccess();
    ElMessage.success("封面图片上传成功");
  } catch (error) {
    console.error("封面图片上传失败:", error);
    ElMessage.error("封面图片上传失败，请重试");
    options.onError && options.onError();
  }
};

// AI配额（剩余调用次数）
const aiQuota = ref(null);

// 获取AI配额
const fetchAiQuota = async () => {
  try {
    const response = await getAiQuota();
    aiQuota.value = response.data.data;
  } catch (error) {
    console.error("获取AI配额失败:", error);
  }
};

// AI提取摘要
const extractSummary = async () => {
  try {
    // 获取编辑器内容
    if (!aiEditor) {
      ElMessage.warning("编辑器未初始化");
      return;
    }

    const content = aiEditor.getHtml();

    // 检查内容是否为空
    if (!content || content.trim() === "" || content === "<p></p>") {
      ElMessage.warning("请先输入文章内容");
      return;
    }

    ElMessage.info("AI摘要提取中，请稍候...");

    // 调用后端接口提取摘要
    const response = await extractSummaryApi(content);
    const summary = response.data.data;

    // 将提取的摘要填充到文章摘要输入框
    article.value.description = summary;

    ElMessage.success("AI摘要提取完成");

    // 更新AI配额
    await fetchAiQuota();
  } catch (error) {
    console.error("AI提取摘要失败:", error);
    ElMessage.error(error.response?.data?.msg || "AI提取摘要失败，请重试");
  }
};

// 专栏标签输入框是否显示
const inputVisible = ref(false);
// 专栏输入框引用
const InputColumnRef = ref();
// 输入的专栏
const inputColumn = ref("");
// 用户专栏列表是否显示
const showColumnDropdown = ref(false);

// 鼠标悬停时获取最新专栏列表并显示
const showColumnListOnHover = async () => {
  showColumnDropdown.value = true;
  try {
    const res = await getColumnList();
    allColumns.value = res.data.data
      .sort((a, b) => {
        return a.sort - b.sort;
      })
      .map((item) => {
        return {
          id: item.id,
          name: item.name,
        };
      });
  } catch (error) {
    console.error("获取专栏列表失败:", error);
    ElMessage.error("获取专栏列表失败");
  }
};

// 鼠标移出按钮时隐藏专栏列表
const hideColumnListOnLeave = () => {
  // 添加延迟
  setTimeout(() => {
    // 只有当鼠标没有进入下拉列表区域时才隐藏
    if (!isMouseInDropdown.value) {
      showColumnDropdown.value = false;
    }
  }, 2000);
};

// 标记鼠标是否在下拉列表区域内
const isMouseInDropdown = ref(false);

// 点击时显示专栏输入框和专栏列表
const showInputColumn = () => {
  inputVisible.value = true;
  showColumnDropdown.value = true;
  nextTick(() => {
    InputColumnRef?.value?.input.focus();
  });
};

// 鼠标移入下拉列表区域时更新标记
const handleColumnDropdownEnter = () => {
  showColumnDropdown.value = true;
  isMouseInDropdown.value = true;
};

// 鼠标移出下拉列表区域时更新标记并隐藏列表
const handleColumnDropdownLeave = () => {
  isMouseInDropdown.value = false;
  // 添加延迟
  setTimeout(() => {
    // 同时检查inputVisible状态，如果输入框可见则保持下拉列表显示
    if (!inputVisible.value) {
      showColumnDropdown.value = false;
    }
  }, 400);
};

// 输入框失焦时隐藏下拉列表并添加专栏
const handleColumnInputBlur = () => {
  // 先添加专栏
  addNewColumnn();
  setTimeout(() => {
    showColumnDropdown.value = false;
  }, 500);
};

// 新增专栏
const addNewColumnn = () => {
  if (inputColumn.value) {
    addColumn({
      name: inputColumn.value,
    })
      .then((res) => {
        // 刷新专栏列表
        getColumnList().then((res) => {
          allColumns.value = res.data.data
            .sort((a, b) => {
              return a.sort - b.sort;
            })
            .map((item) => {
              return {
                id: item.id,
                name: item.name,
              };
            });
        });
        ElMessage.success("新增专栏成功");
      })
      .catch(() => {
        ElMessage.error("新增专栏失败");
      });
  }
  inputVisible.value = false;
  inputColumn.value = "";
};

// 用户的专栏列表
const allColumns = ref([]);
getColumnList().then((res) => {
  allColumns.value = res.data.data
    .sort((a, b) => {
      return a.sort - b.sort;
    })
    .map((item) => {
      return {
        id: item.id,
        name: item.name,
      };
    });
});

// 判断专栏是否已选择
const isColumnSelected = (columnId) => {
  return columns.value.some((column) => column.id === columnId);
};

// 选择专栏
const selectColumn = (column) => {
  // 如果已选择，则取消选择
  if (isColumnSelected(column.id)) {
    columns.value = columns.value.filter((item) => item.id !== column.id);
    article.value.columnIds = article.value.columnIds.filter((item) => item !== column.id);
  } else if (columns.value.length < 3) {
    // 如果未选择且未达到上限，则添加选择
    columns.value.push(column);
    article.value.columnIds.push(column.id);
  }
};

// 删除专栏
const deleteColumn = (column) => {
  columns.value = columns.value.filter((item) => item.id !== column.id);
  article.value.columnIds = article.value.columnIds.filter((item) => item !== column.id);
};

// 发布文章
const handleClickPublish = async () => {
  // 防止重复点击
  if (isPublishing.value) {
    return;
  }

  // 基本校验
  if (!article.value.title || !article.value.title.trim()) {
    ElMessage.warning("请输入文章标题");
    return;
  }

  try {
    isPublishing.value = true;

    // 确保在发布前获取最新的编辑器内容
    if (aiEditor) {
      article.value.content = aiEditor.getHtml();
    }

    // 校验文章内容
    if (!article.value.content || article.value.content.trim() === "") {
      ElMessage.warning("请输入文章内容");
      return;
    }

    // 确保标签数据同步到article对象中
    article.value.tag = tags.value.join(",");

    // 根据是否存在文章ID决定调用新增还是更新接口
    const articleId = route.query.articleId;
    if (articleId && !isNaN(articleId)) {
      // 存在文章ID，调用更新接口
      // 设置编辑状态为已发布
      article.value.editStatus = 0;
      await updateArticle(article.value);
      ElMessage.success("文章更新成功!");
    } else {
      // 不存在文章ID，调用新增接口
      // 设置编辑状态为已发布
      article.value.editStatus = 0;
      await addArticle(article.value);
      ElMessage.success("文章发布成功!");
    }

    // 标记内容未修改，避免刷新提示
    isModified.value = false;

    router.push("/creation/articlemanage");
  } catch (error) {
    const articleId = route.query.articleId;
    if (articleId && !isNaN(articleId)) {
      ElMessage.error("文章更新失败!");
    } else {
      ElMessage.error("文章发布失败!");
    }
  } finally {
    isPublishing.value = false;
  }
};

// 保存草稿
const handleSaveDraft = async () => {
  // 防止重复点击
  if (isSavingDraft.value) {
    return;
  }

  // 基本校验
  if (!article.value.title || !article.value.title.trim()) {
    ElMessage.warning("请输入文章标题");
    return;
  }

  try {
    isSavingDraft.value = true;

    // 确保在保存前获取最新的编辑器内容
    if (aiEditor) {
      article.value.content = aiEditor.getHtml();
    }

    // 确保标签数据同步到article对象中
    article.value.tag = tags.value.join(",");

    const response = await saveDraft(article.value);

    // 保存草稿成功后，更新文章ID和路由参数
    if (response.data && response.data.data && response.data.data.id) {
      article.value.id = response.data.data.id;
      // 更新路由参数，确保后续发布时能正确识别为更新操作
      router.replace({
        query: { ...route.query, articleId: response.data.data.id },
      });
    }

    ElMessage.success("草稿保存成功!");
  } catch (error) {
    console.error("草稿保存失败:", error);
    ElMessage.error("草稿保存失败!");
  } finally {
    isSavingDraft.value = false;
  }
};
</script>

<style lang="scss" scoped>
// 编辑器容器样式
.editor-container {
  display: flex;
  flex-direction: column;
  width: 100%;
  background: #f3f4f6;
  // 编辑器
  .editor {
    display: flex;
    flex: 1;
    flex-direction: column;
    height: 100%;
    margin-top: 48px;
    padding: 0;
    // 编辑器头部
    .aie-container-header {
      position: fixed;
      z-index: 999;
      top: 48px;
      display: flex;
      justify-content: center;
      width: 100%;
      border: none;
      :deep(aie-header) {
        border-top: 1px solid var(--el-border-color);
        width: 100%;
        div {
          display: flex;
          justify-content: center;
        }
      }
    }
    // 编辑器内容包装器
    .editor-content-wrapper {
      display: flex;
      padding-top: 68px;
      padding-bottom: 48px;
      background: var(--el-border-color-lighter);
      min-height: calc(100vh - 48px);
      // 左侧目录样式
      .aie-directory {
        position: fixed;
        display: flex;
        flex-direction: column;
        width: 15vw;
        height: calc(100vh - 200px);
        margin-left: 24px;
        padding: 10px;
        background: var(--el-bg-color);
        border: 1px solid var(--el-border-color);
        border-radius: 4px;
        .directory-header {
          padding: 12px 16px;
          background: var(--el-bg-color);
          border-bottom: 1px solid var(--el-border-color);
          border-radius: 4px;
          h5 {
            margin: 0;
            font-size: 18px;
            font-weight: 500;
          }
        }
        .directory-content {
          overflow-y: auto;
          flex: 1;
          padding: 8px 0;
          // 目录项样式
          .directory-item {
            display: block;
            padding: 6px 16px;
            text-decoration: none;
            color: var(--el-text-color-primary);
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
              color: var(--el-color-primary);
              background-color: var(--el-border-color-light);
            }
            &.active {
              font-weight: 500;
              color: var(--el-color-primary);
              background-color: var(--el-color-primary-light-9);
            }
          }
          .no-content {
            padding: 20px;
            font-size: 14px;
            text-align: center;
            color: #9ca3af;
          }
          .directory-item {
            display: block;
            overflow: hidden;
            padding: 6px 16px;
            font-size: 14px;
            line-height: 20px;
            white-space: nowrap;
            text-decoration: none;
            text-overflow: ellipsis;
            color: var(--el-text-color-regular);
            transition: all 0.2s ease;
            cursor: pointer;
            &:hover {
              color: var(--el-color-primary);
            }
            // 不同级别标题的缩进
            &.level-1 {
              padding-left: 16px;
              font-weight: 500;
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
        overflow-x: hidden;
        overflow-y: visible;
        width: 50vw;
        min-height: calc(100vh - 168px);
        margin: auto;
        border-radius: 8px;
        // 文章标题区域样式
        .article-title-container {
          width: 100%;
          .article-title-input {
            box-sizing: border-box;
            width: 100%;
            padding: 28px;
            font-size: 24px;
            color: var(--el-text-color-primary);
            background: var(--el-bg-color);
            border: none;
            border-bottom: 1px solid var(--el-border-color);
            outline: none;
            transition: all 0.3s ease;
            &::placeholder {
              font-weight: 400;
              color: var(--el-text-color-placeholder);
            }
          }
          @media screen and (max-width: 768px) {
            margin-top: 60px;
          }
        }
        // 文章正文区域样式
        .aie-container-main {
          overflow-x: hidden;
          min-height: calc(100vh - 200px);
          margin-bottom: 24px;
          padding: 16px;
          background: var(--el-bg-color);
          border: none;
          :deep(img) {
            width: auto !important;
            max-width: 100% !important;
            height: auto !important;
          }

          // 表格样式 - 简单有效的滚动解决方案
          :deep(table) {
            min-width: 100% !important;
            max-width: 100% !important;
            border-collapse: collapse;
            margin: 16px 0;
            border-radius: 4px;
            display: block;
            overflow-x: auto;
            white-space: nowrap;
            p {
              text-align: center;
            }

            // 自定义滚动条样式，确保可见
            &::-webkit-scrollbar {
              height: 10px;
            }
          }
        }
        // 发布文章设置样式
        .publish-settings {
          padding: 16px;
          padding-bottom: 50px;
          // border: 1px solid var(--el-border-color);
          background: var(--el-bg-color);
          h3 {
            margin-top: 0;
            margin-bottom: 16px;
            font-size: 18px;
            font-weight: 600;
          }
          label {
            display: inline-block;
            margin-right: 16px;
            font-weight: 500;
            color: var(--el-text-color-primary);
            @media screen and (max-width: 768px) {
              width: 40px;
              min-width: 40px;
            }
          }
          .tag-setting {
            position: relative;
            display: flex;
            align-items: center;
            .tag-item-container {
              display: flex;
              flex-wrap: wrap;
              .tag-item {
                margin-right: 10px;
                margin-bottom: 10px;
              }
              .tag-add-button {
                height: 22px;
                margin-bottom: 10px;
                margin-left: 0px;
              }
            }
            // 标签选择器
            .tag-selector-container {
              position: absolute;
              z-index: 2000;
              top: 100%;
              left: 0;
              overflow: hidden;
              width: 400px;
              max-height: 400px;
              margin-top: 8px;
              padding: 16px;
              background: var(--el-bg-color);
              border: 1px solid var(--el-border-color);
              border-radius: 6px;
              box-shadow: 0 4px 16px rgba(0, 0, 0, 0.15);
              @media screen and (max-width: 768px) {
                width: 80vw;
                max-height: 60vh;
              }
              .tag-selector {
                width: 100%;
                // 头部样式
                .tag-selector-header {
                  display: flex;
                  align-items: center;
                  justify-content: space-between;
                  margin-bottom: 16px;
                  h4 {
                    margin: 0;
                    font-size: 16px;
                    font-weight: 500;
                  }
                  .close-icon {
                    color: var(--el-text-color-secondary);
                    cursor: pointer;
                    &:hover {
                      color: var(--el-text-color-primary);
                    }
                  }
                }
                // 搜索容器样式
                .tag-search-container {
                  position: relative;
                  margin-bottom: 16px;
                  .el-input {
                    width: 100%;
                  }
                }
                // 搜索结果下拉框样式
                .search-result-dropdown {
                  position: absolute;
                  z-index: 1000;
                  top: 100%;
                  right: 0;
                  left: 0;
                  overflow-y: auto;
                  max-height: 200px;
                  background: var(--el-bg-color);
                  border: 1px solid var(--el-border-color);
                  border-radius: 4px;
                  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
                  .search-result-item {
                    padding: 8px 16px;
                    transition: background-color 0.2s;
                    cursor: pointer;
                    &:hover {
                      background-color: var(--el-border-color-light);
                    }
                  }
                }
                /* 标签容器样式 */
                .tag-container {
                  position: relative;
                  display: flex;
                  height: 300px;
                  gap: 20px;
                  /* 标签数量限制遮盖层 */
                  .tag-limit-overlay {
                    position: absolute;
                    z-index: 10;
                    top: 0;
                    right: 0;
                    bottom: 0;
                    left: 0;
                    display: flex;
                    align-items: center;
                    justify-content: center;
                    background-color: rgba(0, 0, 0, 0.5);
                    pointer-events: none;
                    span {
                      font-size: 14px;
                      font-weight: 500;
                      color: white;
                    }
                  }
                  /* 左侧分类列表样式 */
                  .tag-category-list {
                    overflow: auto;
                    flex-shrink: 0;
                    width: 100px;
                    padding-right: 10px;
                    .tag-category-item {
                      margin-bottom: 5px;
                      padding: 5px 15px;
                      border-radius: 4px;
                      transition: all 0.2s;
                      cursor: pointer;
                      &:hover {
                        background-color: var(--el-border-color-light);
                      }
                      &.active {
                        color: white;
                        background-color: var(--el-color-primary);
                      }
                    }
                  }
                  /* 右侧标签列表样式 */
                  .tag-list {
                    overflow-y: auto;
                    flex: 1;
                    /* 可用标签区域样式 */
                    .available-tags-section {
                      display: flex;
                      flex-wrap: wrap;
                      gap: 8px;
                      .available-tag {
                        transition: all 0.2s;
                        cursor: pointer;
                        &.tag-item-active {
                          color: white;
                          background-color: var(--el-color-primary);
                        }
                      }
                    }
                  }
                }
              }
            }
          }
          // 封面设置
          .cover-setting {
            display: flex;
            align-items: center;
            margin-bottom: 10px;
            .cover-container {
              display: flex;
              flex-wrap: wrap;
              // 封面上传样式
              .uploader {
                position: relative;
                display: flex;
                display: block;
                overflow: hidden;
                align-items: center;
                flex-direction: column;
                justify-content: center;
                width: 192px;
                height: 108px;
                margin-bottom: 8px;
                border: 1px solid var(--el-border-color);
                border-radius: 6px;
                transition: var(--el-transition-duration-fast);
                cursor: pointer;
                .cover-image {
                  width: 192px;
                  height: 108px;
                  border-radius: 4px;
                }
                .avatar-icon {
                  width: 192px;
                  height: 108px;
                  font-size: 28px;
                  text-align: center;
                  color: #8c939d;
                }
              }
              //图片选择
              .cover-tip {
                display: flex;
                align-items: center;
                justify-content: center;
                width: 129px;
                height: 81px;
                margin-left: 16px;
                font-size: 12px;
                color: var(--el-text-color-secondary);
                border: 1px solid var(--el-border-color);
              }
            }
          }
          // 文章摘要设置
          .description-setting {
            display: flex;
            align-items: center;
            margin-bottom: 16px;
            .description-container {
              display: flex;
              flex-wrap: wrap;
              .description-input {
                :deep(.el-textarea__inner) {
                  box-sizing: border-box;
                }
              }
              // AI摘要操作区域
              .ai-summary-actions {
                display: flex;
                align-items: center;
                gap: 12px;
                margin-top: 8px;
                // AI配额文字
                .ai-quota-text {
                  font-size: 13px;
                  color: var(--el-text-color-regular);
                  white-space: nowrap;
                }
              }
            }
          }
          // 分类专栏设置
          .column-setting {
            display: flex;
            align-items: center;
            margin-bottom: 16px;
            .column-tags-container {
              position: relative;
              display: flex;
              align-items: center;
              flex-wrap: wrap;
              .column-item-container {
                display: flex;
                flex-wrap: wrap;
                .column-item {
                  margin-right: 10px;
                  margin-bottom: 8px;
                }
                .column-actions {
                  display: flex;
                  align-items: center;
                  margin-bottom: 8px;
                  .column-input {
                    width: 87.78px;
                    height: 35.78px;
                  }
                }
              }
              .column-dropdown {
                position: absolute;
                z-index: 1000;
                top: calc(100% + 4px);
                left: 0;
                overflow-y: auto;
                min-width: 180px;
                max-height: 150px;
                background: var(--el-bg-color);
                border: 1px solid var(--el-border-color);
                border-radius: 4px;
                box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
                .column-limit-overlay {
                  position: absolute;
                  z-index: 10;
                  top: 0;
                  right: 0;
                  bottom: 0;
                  left: 0;
                  display: flex;
                  align-items: center;
                  justify-content: center;
                  font-size: 14px;
                  color: var(--el-text-color-secondary);
                  background: rgba(255, 255, 255, 0.8);
                }
                // 用户已有的专栏列表
                .column-list {
                  position: relative;
                  z-index: 5;
                  align-items: center;
                  max-height: 200px;
                  padding: 8px;
                  gap: 2px;
                  .column-option {
                    display: flex;
                    align-items: center;
                    padding: 4px;
                    border-radius: 4px;
                    transition: background-color 0.2s;
                    cursor: pointer;
                    &:hover {
                      background-color: var(--el-border-color-light);
                    }
                    &.selected {
                      background-color: var(--el-color-primary-light-9);
                    }
                    &.disabled {
                      cursor: not-allowed;
                      opacity: 0.6;
                      &:hover {
                        background-color: transparent;
                      }
                    }
                  }
                }
              }
            }
          }
          // 文章类型设置
          .reprint-type-setting {
            display: flex;
            margin-bottom: 16px;
            :deep(.el-radio) {
              width: auto; // 留足够宽度
            }
          }
          // 转载链接设置
          .reprint-url-setting {
            display: flex;
            align-items: center;
            margin-bottom: 16px;
            margin-left: 20px; // 缩进显示层级关系
            label {
              margin-right: 16px;
              color: var(--el-text-color-regular);
            }
          }
          // 可见范围设置
          .visible-range-setting {
            display: flex;
            margin-bottom: 16px;
            :deep(.el-radio) {
              width: auto; // 留足够宽度
            }
          }
        }
      }
      // 回到顶部按钮
      .back-to-top {
        position: fixed;
        right: 22px;
        bottom: 80px;
        display: flex;
        align-items: center;
        justify-content: center;
        width: 60px;
        height: 60px;
        font-size: 30px;
        backdrop-filter: blur(2px);
        background-color: color-mix(in srgb, var(--el-bg-color) 50%, transparent);
        border: 1px solid var(--el-border-color);
        border-radius: 50%;
        cursor: pointer;
        @media screen and (max-width: 768px) {
          width: 50px;
          height: 50px;
          font-size: 24px;
        }

        &:hover {
          background: var(--el-color-primary);
          color: white;
          transform: translateY(-2px);
          box-shadow: 0 4px 16px 0 rgba(0, 0, 0, 0.15);
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
    display: flex;
    align-items: center;
    justify-content: space-evenly;
    width: 100%;
    height: 48px;
    background-color: var(--el-bg-color);
    .left {
      font-size: 12px;
      padding: 5px;
    }
    .center {
      margin-right: 5px;
    }
    .right {
      display: flex;
      margin-right: 5px;
      :deep(.el-button) {
        margin-left: 5px;
      }
    }
  }
}
@media screen and (max-width: 1024px) {
  .editor-container {
    .editor {
      .aie-container {
        .editor-content-wrapper {
          max-width: 100%;
          padding-top: 0; // 间隙
          .aie-directory {
            display: none; // 隐藏目录
          }
          .editor-content {
            overflow-x: hidden !important;
            box-sizing: border-box;
            width: 100%; // 编辑文章区域
            min-height: calc(100vh - 68px);
            margin-top: 100px;
            padding: 0 15px;
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
            overflow-x: hidden !important;
            box-sizing: border-box !important;
            width: 100% !important;
            min-height: calc(100vh - 68px);
            margin-top: 120px;
            padding: 0 10px;
          }
        }
      }
    }
  }
}
</style>

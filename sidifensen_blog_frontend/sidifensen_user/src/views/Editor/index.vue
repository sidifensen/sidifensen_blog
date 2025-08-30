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
            <!-- 文章标题区域 -->
            <div class="article-title-container">
              <input 
                v-model="article.title" 
                type="text" 
                class="article-title-input" 
                placeholder="请输入文章标题..." 
                maxlength="50"
              />
            </div>
            <!-- 文章正文区域 -->
            <div class="aie-container-main"></div>
            <div class="publish-settings">
              <h3>发布文章设置</h3>
              <div class="tag-setting">
                <label>文章标签</label>
                <div class="tag-item-container">
                  <el-tag class="tag-item" v-for="tag in tags" :key="tag" closable size="large" effect="plain" @close="deleteTag(tag)">
                    {{ tag }}
                  </el-tag>
                  <el-button class="tag-add-button" size="small" icon="Plus" @click="showTagSelector">添加文章标签</el-button>
                </div>
                <div v-if="isTagSelectorVisible" class="tag-selector-container">
                  <div class="tag-selector">
                    <div class="tag-selector-header">
                      <h4>标签</h4>
                      <el-icon class="close-icon" @click="closeTagSelector"><Close /></el-icon>
                    </div>
                    <!-- 搜索标签 -->
                    <div class="tag-search-container">
                      <el-input v-model="tagSearchKeyword" placeholder="请输入文字搜索" size="small" @input="handleTagSearch" @keyup.enter="addCustomTag">
                        <template #prefix>
                          <el-icon :size="16"><Search /></el-icon>
                        </template>
                      </el-input>
                      <div v-if="isSearchResultVisible && searchResults.length > 0" class="search-result-dropdown">
                        <div v-for="result in searchResults" :key="result" class="search-result-item" @click="selectTag(result)">
                          {{ result }}
                        </div>
                      </div>
                    </div>
                    <!-- 标签列表 -->
                    <div class="tag-container">
                      <div class="tag-category-list">
                        <div v-for="category in tagCategories" :key="category" class="tag-category-item" :class="{ active: activeCategory === category }" @click="selectCategory(category)">
                          {{ category }}
                        </div>
                      </div>
                      <div class="tag-list">
                        <div class="available-tags-section">
                          <el-tag v-for="tag in getTagsByCategory(activeCategory)" :key="tag" class="available-tag" :class="{ 'tag-item-active': tags.includes(tag) }" size="small" @click="toggleTag(tag)">
                            {{ tag }}
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
                  <el-upload class="uploader" action="" :auto-upload="true" :show-file-list="false" list-type="picture" :http-request="handleCoverUpload">
                    <img v-if="article.coverUrl || coverImage" :src="article.coverUrl || coverImage" class="cover-image" />
                    <el-icon v-else class="avatar-icon"><Plus /></el-icon>
                  </el-upload>
                  <div class="cover-tip">暂无内容图片，请在正文中添加图片</div>
                </div>
              </div>
              <div class="description-setting">
                <label>文章摘要</label>
                <div class="description-container">
                  <el-input v-model="article.description" type="textarea" resize="none" placeholder="输入文章摘要" :autosize="{ minRows: 2, maxRows: 4 }" maxlength="256"></el-input>
                  <el-button size="small" type="danger" icon="EditPen" plain round style="margin-top: 8px" @click="extractSummary">AI提取摘要</el-button>
                </div>
              </div>
              <div class="column-setting">
                <label>分类专栏</label>
                <div class="column-tags-container">
                  <div class="column-item-container">
                    <el-tag class="column-item" v-for="column in columns" :key="column.id" size="large" closable effect="plain" @close="deleteColumn(column)">
                      {{ column.name }}
                    </el-tag>
                    <div class="column-actions">
                      <el-button v-if="!inputVisible" size="small" icon="Plus" @mouseenter="showColumnListOnHover" @mouseleave="hideColumnListOnLeave" @click="showInputColumn">新增专栏 </el-button>
                      <el-input v-if="inputVisible" ref="InputColumnRef" class="column-input" v-model="inputColumn" size="small" @keyup.enter="addNewColumnn" @blur="handleColumnInputBlur" />
                    </div>
                  </div>
                  <div v-if="showColumnDropdown" class="column-dropdown" @mouseenter="handleColumnDropdownEnter" @mouseleave="handleColumnDropdownLeave">
                    <div v-if="columns.length >= 3" class="column-limit-overlay">
                      <span>最多选择三个专栏</span>
                    </div>
                    <div class="column-list">
                      <div v-for="item in allColumns" :key="item.id" class="column-option" :class="{ selected: isColumnSelected(item.id), disabled: columns.length >= 3 && !isColumnSelected(item.id) }" @click="selectColumn(item)">
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
                <el-radio-group class="reprint-type-radio" v-model="article.reprintType">
                  <el-radio :label="0">原创</el-radio>
                  <el-radio :label="1">转载</el-radio>
                </el-radio-group>
              </div>
              <div class="visible-range-setting">
                <label>可见范围</label>
                <el-radio-group class="visible-range-radio" v-model="article.visibleRange">
                  <div class="visible-range-container">
                    <el-radio :label="0">全部可见</el-radio>
                    <el-radio :label="1">仅我可见</el-radio>
                    <el-radio :label="2">粉丝可见</el-radio>
                    <el-radio :label="3">VIP可见</el-radio>
                  </div>
                </el-radio-group>
              </div>
            </div>
          </div>
          <!-- 右下角回到顶部按钮 -->
          <div class="back-to-top" @click="scrollToTop">
            <el-icon><ArrowUp /></el-icon>
          </div>
        </div>
        <div class="aie-container-footer"></div>
      </div>
    </div>
    <div class="footer">
      <div>字数统计: {{ wordCount }}字</div>
      <div class="center">
        <el-button icon="ArrowDown" @click="scrollToArticleSettings">文章设置</el-button>
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
import { compressImage, validateImageFile } from "@/utils/PhotoUtils";
import { getTagList } from "@/api/tag";
import { addColumn, getColumnList } from "@/api/column";
import { uploadArticlePhoto } from "@/api/photo";
import { ArrowUp, Search, Close } from "@element-plus/icons-vue";
import { addArticle } from "@/api/article";

const darkStore = useDarkStore();
const { isDark } = storeToRefs(darkStore);

// 字数统计
const wordCount = ref(0);

// 滚动到页面顶部
const scrollToTop = () => {
  const contentArea = document.querySelector(".editor-content");
  if (contentArea) {
    contentArea.scrollTo({ top: 0, behavior: "smooth" });
  }
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
      content: article.value.content, // 绑定文章内容
      draggable: false, // 禁用拖动
      // 监听编辑器内容变化，更新article.content
      onChange: (html) => {
        article.value.content = html;
      },
      // 图片上传配置
      image: {
        allowBase64: false,
        defaultSize: 350,
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
            console.log("onSuccess上传成功:", response);
            // {"errorCode": 0,"data": {"src": "", "alt": "文章图片"}
            ElMessage.success("图片上传成功");
            return true;
          },
          onFailed: (file, response) => {
            console.log("onFailed上传失败:", response);
            ElMessage.error(response?.msg || "图片上传失败");
          },
          onError: (file, error) => {
            console.log("onError上传出错:", error);
            ElMessage.error("上传出错: " + error.message);
          },
        },
        // 选中图片时的浮动菜单配置
        bubbleMenuItems: ["AlignLeft", "AlignCenter", "AlignRight", "delete"],
      },
      toolbarExcludeKeys: ["subscript", "superscript", "break", "video", "source-code", "printer", "fullscreen"], // 排除下标,上标.强制换行,视频,源代码,打印,全屏
      onSave: (editor) => {
        ElMessage.success("文档保存成功！");
        return true;
      }, // 保存回调
      // 监听编辑器内容变化，更新目录和字数统计（使用防抖处理）
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

// 在组件卸载前销毁编辑器实例、移除事件监听器并清理防抖计时器
onUnmounted(() => {
  // 销毁编辑器实例
  aiEditor && aiEditor.destroy();
  // 清理防抖计时器
  if (directoryUpdateTimer) {
    clearTimeout(directoryUpdateTimer);
  }
});

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
});

// 所有标签
const allTags = ref([]);
getTagList().then((res) => {
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
        if (tag.toLowerCase().includes(keyword) && !results.includes(tag)) {
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
  const customTag = tagSearchKeyword.value.trim();
  if (customTag && !tags.value.includes(customTag)) {
    tags.value.push(customTag);
    tagSearchKeyword.value = "";
    isSearchResultVisible.value = false;
    searchResults.value = [];
  }
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

// 当前标签
const tags = ref((article.value.tag || "").split(",").filter((tag) => tag.trim() !== ""));

// 切换标签选中状态
const toggleTag = (tag) => {
  const index = tags.value.indexOf(tag);
  if (index > -1) {
    tags.value.splice(index, 1);
  } else {
    tags.value.push(tag);
  }
  // 更新article中的tag值，确保数据同步
  article.value.tag = tags.value.join(",");
  console.log(tags.value);
};

// 删除标签
const deleteTag = (tag) => {
  tags.value = tags.value.filter((item) => item !== tag);
};

// 点击外部关闭搜索结果
const handleDocumentClick = (event) => {
  if (isSearchResultVisible.value && !event.target.closest(".tag-search-container") && !event.target.closest(".search-result-dropdown")) {
    isSearchResultVisible.value = false;
  }
};

// 组件挂载时添加事件监听
onMounted(() => {
  document.addEventListener("click", handleDocumentClick);
});

// 组件卸载时移除事件监听
onUnmounted(() => {
  document.removeEventListener("click", handleDocumentClick);
});

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

// AI提取摘要
const extractSummary = () => {
  ElMessage.success("AI摘要提取中...");
  setTimeout(() => {
    ElMessage.success("AI摘要提取完成");
  }, 1000);
};

// 专栏标签输入框是否显示
const inputVisible = ref(false);
// 专栏输入框引用
const InputColumnRef = ref();
// 输入的专栏
const inputColumn = ref("");
// 用户专栏列表是否显示
const showColumnDropdown = ref(false);
// 选中的专栏
const selectedColumns = ref({});

// 鼠标悬停时只显示专栏列表
const showColumnListOnHover = () => {
  showColumnDropdown.value = true;
};

// 鼠标移出按钮时隐藏专栏列表
const hideColumnListOnLeave = () => {
  // 添加延迟
  setTimeout(() => {
    // 只有当鼠标没有进入下拉列表区域时才隐藏
    if (!isMouseInDropdown.value) {
      showColumnDropdown.value = false;
    }
  }, 1000);
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

// 当前选择的专栏
const columns = ref([]);

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

// 初始化时同步选中状态
onMounted(() => {
  // 初始化selectedColumns状态
  columns.value.forEach((column) => {
    selectedColumns.value[column.id] = true;
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
  selectedColumns.value[column.id] = false;
  article.value.columnIds = article.value.columnIds.filter((item) => item !== column.id);
};

// 发布文章
const handleClickPublish = async () => {
  console.log(article.value);
  addArticle(article.value)
    .then(() => {
      ElMessage.success("文章发布成功!");
    })
    .catch(() => {
      ElMessage.error("文章发布失败!");
    });
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
        width: 20vw;
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
        // 文章标题区域样式
        .article-title-container {
          margin-bottom: 20px;
          .article-title-input {
            width: 100%;
            padding: 12px 16px;
            font-size: 24px;
            font-weight: 600;
            color: var(--el-text-color-primary);
            background: var(--el-bg-color);
            border: 1px solid var(--el-border-color);
            border-radius: 8px;
            outline: none;
            transition: all 0.3s ease;
            &::placeholder {
              color: var(--el-text-color-placeholder);
              font-weight: 400;
            }
            &:focus {
              border-color: var(--el-color-primary);
              box-shadow: 0 0 0 2px rgba(64, 169, 255, 0.2);
            }
            @media screen and (max-width: 768px) {
              font-size: 20px;
              padding: 10px 12px;
            }
          }
        }
        // 文章正文区域样式
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
          padding-bottom: 50px;
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
                margin-left: 0px;
                margin-bottom: 10px;
              }
            }
            // 标签选择器
            .tag-selector-container {
              position: absolute;
              top: 100%;
              left: 0;
              width: 400px;
              max-height: 400px;
              margin-top: 8px;
              border: 1px solid var(--el-border-color);
              border-radius: 6px;
              background: var(--el-bg-color);
              padding: 16px;
              z-index: 2000;
              box-shadow: 0 4px 16px rgba(0, 0, 0, 0.15);
              overflow: hidden;
              @media screen and (max-width: 768px) {
                width: 80vw;
                max-height: 60vh;
              }
              .tag-selector {
                width: 100%;
                // 头部样式
                .tag-selector-header {
                  display: flex;
                  justify-content: space-between;
                  align-items: center;
                  margin-bottom: 16px;
                  h4 {
                    margin: 0;
                    font-size: 16px;
                    font-weight: 500;
                  }
                  .close-icon {
                    cursor: pointer;
                    color: var(--el-text-color-secondary);
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
                  top: 100%;
                  left: 0;
                  right: 0;
                  max-height: 200px;
                  overflow-y: auto;
                  background: var(--el-bg-color);
                  border: 1px solid var(--el-border-color);
                  border-radius: 4px;
                  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
                  z-index: 1000;
                  .search-result-item {
                    padding: 8px 16px;
                    cursor: pointer;
                    transition: background-color 0.2s;
                    &:hover {
                      background-color: var(--el-border-color-light);
                    }
                  }
                }
                // 标签容器样式
                .tag-container {
                  display: flex;
                  gap: 20px;
                  height: 300px;
                  // 左侧分类列表样式
                  .tag-category-list {
                    padding-right: 10px;
                    width: 100px;
                    overflow: auto;
                    flex-shrink: 0;
                    .tag-category-item {
                      padding: 5px 15px;
                      cursor: pointer;
                      border-radius: 4px;
                      margin-bottom: 5px;
                      transition: all 0.2s;
                      &:hover {
                        background-color: var(--el-border-color-light);
                      }
                      &.active {
                        background-color: var(--el-color-primary);
                        color: white;
                      }
                    }
                  }
                  // 右侧标签列表样式
                  .tag-list {
                    flex: 1;
                    overflow-y: auto;
                    // 可用标签区域样式
                    .available-tags-section {
                      display: flex;
                      flex-wrap: wrap;
                      gap: 8px;
                      .available-tag {
                        cursor: pointer;
                        transition: all 0.2s;
                        &.tag-item-active {
                          background-color: var(--el-color-primary);
                          color: white;
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
                display: flex;
                align-items: center;
                justify-content: center;
                width: 129px;
                height: 81px;
                color: var(--el-text-color-secondary);
                font-size: 12px;
                margin-left: 16px;
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
            }
          }
          // 分类专栏设置
          .column-setting {
            display: flex;
            align-items: center;
            margin-bottom: 16px;
            .column-tags-container {
              display: flex;
              flex-wrap: wrap;
              align-items: center;
              position: relative;
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
                top: calc(100% + 4px);
                left: 0;
                min-width: 180px;
                max-height: 150px;
                overflow-y: auto;
                background: var(--el-bg-color);
                border: 1px solid var(--el-border-color);
                border-radius: 4px;
                box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
                z-index: 1000;
                .column-limit-overlay {
                  position: absolute;
                  top: 0;
                  left: 0;
                  right: 0;
                  bottom: 0;
                  background: rgba(255, 255, 255, 0.8);
                  display: flex;
                  align-items: center;
                  justify-content: center;
                  z-index: 10;
                  font-size: 14px;
                  color: var(--el-text-color-secondary);
                }
                // 用户已有的专栏列表
                .column-list {
                  max-height: 200px;
                  padding: 8px;
                  position: relative;
                  z-index: 5;
                  align-items: center;
                  gap: 2px;
                  .column-option {
                    display: flex;
                    align-items: center;
                    padding: 4px;
                    cursor: pointer;
                    transition: background-color 0.2s;
                    border-radius: 4px;
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
            align-items: center;
            margin-bottom: 16px;
            @media screen and (max-width: 768px) {
              .reprint-type-radio {
                display: flex;
                flex-wrap: wrap;
                // flex-direction: column;
              }
            }
          }
          // 可见范围设置
          .visible-range-setting {
            display: flex;
            align-items: center;
            margin-bottom: 16px;
            .visible-range-radio {
              display: flex;
              flex-wrap: wrap;
              .visible-range-container {
              }
            }
          }
        }
      }
      // 回到顶部按钮
      .back-to-top {
        position: fixed;
        width: 60px;
        height: 60px;
        bottom: 80px;
        right: 20px;
        font-size: 30px;
        display: flex;
        align-items: center;
        justify-content: center;
        background: var(--el-bg-color);
        border: 1px solid var(--el-border-color);
        border-radius: 50%;
        cursor: pointer;
        @media screen and (max-width: 768px) {
          width: 50px;
          height: 50px;
          font-size: 24px;
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

<template>
  <div class="column-manage-container">
    <div class="main-content">
      <!-- 筛选区域 -->
      <div class="filter-section">
        <div class="filter-row">
          <div class="filter-item">
            <el-select v-model="selectedYear" placeholder="年份" @change="handleDateFilterChange" class="filter-select">
              <template #prefix>
                <span class="select-prefix">年份:</span>
              </template>
              <el-option label="不限" :value="null"></el-option>
              <el-option v-for="year in availableYears" :key="year" :label="year + '年'" :value="year"></el-option>
            </el-select>
          </div>

          <div class="filter-item">
            <el-select v-model="selectedMonth" placeholder="月份" @change="handleDateFilterChange" class="filter-select">
              <template #prefix>
                <span class="select-prefix">月份:</span>
              </template>
              <el-option label="不限" :value="null"></el-option>
              <el-option v-for="month in 12" :key="month" :label="month + '月'" :value="month"></el-option>
            </el-select>
          </div>

          <div class="filter-item">
            <el-select v-model="showStatus" placeholder="展示状态" @change="handleStatusFilterChange" class="filter-select">
              <template #prefix>
                <span class="select-prefix">状态:</span>
              </template>
              <el-option label="全部" :value="null"></el-option>
              <el-option label="公开" :value="0"></el-option>
              <el-option label="私密" :value="1"></el-option>
            </el-select>
          </div>

          <div class="filter-item">
            <el-select v-model="examineStatus" placeholder="审核状态" @change="handleExamineStatusFilterChange" class="filter-select">
              <template #prefix>
                <span class="select-prefix">审核:</span>
              </template>
              <el-option label="全部" :value="null"></el-option>
              <el-option label="待审核" :value="0"></el-option>
              <el-option label="审核通过" :value="1"></el-option>
              <el-option label="审核未通过" :value="2"></el-option>
            </el-select>
          </div>

          <div class="filter-item">
            <el-input v-model="searchKeyword" placeholder="请输入关键词" @keyup.enter="handleSearch" class="search-input">
              <template #prefix>
                <el-icon>
                  <Search />
                </el-icon>
              </template>
            </el-input>
          </div>

          <div class="filter-item create-button-wrapper">
            <el-button type="primary" :icon="Plus" @click="handleCreateColumn" class="create-column-btn">
              <span class="btn-text">新增专栏</span>
            </el-button>
          </div>
        </div>
      </div>

      <!-- 专栏列表区域 -->
      <div class="column-list-container" ref="listContainer" @scroll="handleScroll">
        <div v-if="loading" class="loading-container">
          <div class="loading-spinner"></div>
          <span>加载中...</span>
        </div>

        <div v-else-if="columns.length === 0" class="empty-container">
          <el-empty description="暂无专栏数据"></el-empty>
        </div>

        <div v-else class="column-cards">
          <el-card v-for="column in columns" :key="column.id" class="column-card">
            <div class="column-card-content">
              <!-- 专栏封面和基本信息 -->
              <div class="column-info">
                <el-image :src="column.coverUrl || ''" alt="专栏封面" class="column-cover" @click="goToColumnDetail(column)">
                  <template #placeholder>
                    <div class="loading-text">加载中...</div>
                  </template>
                  <template #error>
                    <div class="error">
                      <el-icon>
                        <Picture />
                      </el-icon>
                    </div>
                  </template>
                </el-image>

                <div class="column-details">
                  <h4 class="column-title" @click="goToColumnDetail(column)">{{ column.name }}</h4>
                  <el-tooltip v-if="column.description && column.description.length > 50" :content="column.description" placement="top">
                    <p class="column-description">{{ column.description }}</p>
                  </el-tooltip>
                  <p v-else class="column-description">{{ column.description || "暂无描述" }}</p>
                </div>
              </div>

              <!-- 专栏统计信息 -->
              <div class="column-stats">
                <div class="stat-item">
                  <el-icon>
                    <Document />
                  </el-icon>
                  <span>{{ column.articleCount || 0 }} 文章</span>
                </div>
                <div class="stat-item">
                  <el-icon>
                    <Star />
                  </el-icon>
                  <span>{{ column.focusCount || 0 }} 关注</span>
                </div>
                <div class="stat-item">
                  <span class="status-badge" :class="column.showStatus === 0 ? 'public' : 'private'">
                    {{ column.showStatus === 0 ? "公开" : "私密" }}
                  </span>
                </div>
                <div class="stat-item">
                  <span class="examine-badge" :class="getExamineStatusClass(column.examineStatus)">
                    {{ getExamineStatusText(column.examineStatus) }}
                  </span>
                </div>
              </div>

              <!-- 专栏元信息 -->
              <div class="column-meta">
                <span class="column-date">创建时间：{{ formatTime(column.createTime) }}</span>
                <span class="column-sort">排序：{{ column.sort }}</span>
              </div>

              <!-- 专栏操作 -->
              <div class="column-actions">
                <div class="sort-actions">
                  <el-button type="primary" text size="small" @click="handleSortUp(column)" :disabled="sortLoading || isFirstColumn(column.id)">
                    <el-icon><ArrowUp /></el-icon>
                    上移
                  </el-button>
                  <el-button type="primary" text size="small" @click="handleSortDown(column)" :disabled="sortLoading || isLastColumn(column.id)">
                    <el-icon><ArrowDown /></el-icon>
                    下移
                  </el-button>
                </div>
                <div class="operation-actions">
                  <el-button type="success" text @click="handleManageColumn(column)">管理</el-button>
                  <el-button type="primary" text @click="handleEditColumn(column)">编辑专栏</el-button>
                  <el-button type="danger" text @click="handleDeleteColumn(column.id)">删除专栏</el-button>
                </div>
              </div>
            </div>
          </el-card>

          <!-- 加载更多指示器 -->
          <div v-if="loadingMore" class="loading-more">
            <div class="loading-spinner small"></div>
            <span>加载更多...</span>
          </div>
        </div>
      </div>
    </div>

    <!-- 新增专栏对话框 -->
    <el-dialog v-model="createDialogVisible" title="新增专栏" width="600px" :close-on-click-modal="false" :close-on-press-escape="false">
      <el-form ref="createFormRef" :model="createForm" :rules="createFormRules" label-width="100px" class="create-column-form">
        <!-- 专栏名称 -->
        <el-form-item label="专栏名称" prop="name">
          <el-input v-model="createForm.name" placeholder="请输入专栏名称" maxlength="30" show-word-limit />
        </el-form-item>

        <!-- 专栏描述 -->
        <el-form-item label="专栏描述" prop="description">
          <el-input v-model="createForm.description" type="textarea" :rows="4" placeholder="请输入专栏描述（可选）" maxlength="200" show-word-limit resize="none" />
        </el-form-item>

        <!-- 专栏封面 -->
        <el-form-item label="专栏封面" prop="coverUrl">
          <div class="cover-upload-container">
            <el-upload ref="createUploadRef" class="cover-uploader" :action="''" :http-request="handleCreateUploadCover" :show-file-list="false" :before-upload="beforeUploadCover" accept="image/*" drag>
              <div v-if="createForm.coverUrl" class="cover-preview">
                <el-image :src="createForm.coverUrl" class="preview-image">
                  <template #placeholder>
                    <div class="loading-text">加载中...</div>
                  </template>
                  <template #error>
                    <div class="error-placeholder">
                      <el-icon><Picture /></el-icon>
                      <span>加载失败</span>
                    </div>
                  </template>
                </el-image>
                <div class="cover-mask">
                  <div class="cover-actions">
                    <el-button type="primary" size="small" :loading="createUploadLoading">
                      <el-icon><Picture /></el-icon>
                      {{ createUploadLoading ? "上传中..." : "更换封面" }}
                    </el-button>
                    <el-button type="danger" size="small" @click.stop="handleCreateRemoveCover">
                      <el-icon><Delete /></el-icon>
                      删除封面
                    </el-button>
                  </div>
                </div>
              </div>
              <div v-else class="upload-placeholder">
                <el-icon v-if="!createUploadLoading" class="upload-icon"><Plus /></el-icon>
                <div v-if="createUploadLoading" class="uploading">
                  <div class="loading-spinner"></div>
                  <span>上传中...</span>
                </div>
                <div v-else class="upload-text">
                  <div>点击或将图片拖拽到这里上传</div>
                  <div class="upload-tip">支持 JPG、PNG、GIF 格式，文件大小不超过 5MB</div>
                </div>
              </div>
            </el-upload>
          </div>
        </el-form-item>

        <!-- 展示状态 -->
        <el-form-item label="展示状态" prop="showStatus">
          <el-radio-group v-model="createForm.showStatus">
            <el-radio :value="0">
              <el-icon><View /></el-icon>
              公开
            </el-radio>
            <el-radio :value="1">
              <el-icon><Hide /></el-icon>
              私密
            </el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>

      <template #footer>
        <div class="dialog-footer">
          <el-button @click="handleCancelCreate" :disabled="createSubmitting">取消</el-button>
          <el-button type="primary" @click="handleSubmitCreate" :loading="createSubmitting">
            {{ createSubmitting ? "创建中..." : "创建" }}
          </el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 编辑专栏对话框 -->
    <el-dialog v-model="editDialogVisible" title="编辑专栏" width="600px" :close-on-click-modal="false" :close-on-press-escape="false">
      <el-form ref="editFormRef" :model="editForm" :rules="editFormRules" label-width="100px" class="edit-column-form">
        <!-- 专栏名称 -->
        <el-form-item label="专栏名称" prop="name">
          <el-input v-model="editForm.name" placeholder="请输入专栏名称" maxlength="30" show-word-limit />
        </el-form-item>

        <!-- 专栏描述 -->
        <el-form-item label="专栏描述" prop="description">
          <el-input v-model="editForm.description" type="textarea" :rows="4" placeholder="请输入专栏描述（可选）" maxlength="200" show-word-limit resize="none" />
        </el-form-item>

        <!-- 专栏封面 -->
        <el-form-item label="专栏封面" prop="coverUrl">
          <div class="cover-upload-container">
            <el-upload ref="uploadRef" class="cover-uploader" :action="''" :http-request="handleUploadCover" :show-file-list="false" :before-upload="beforeUploadCover" accept="image/*" drag>
              <div v-if="editForm.coverUrl" class="cover-preview">
                <el-image :src="editForm.coverUrl" class="preview-image">
                  <template #placeholder>
                    <div class="loading-text">加载中...</div>
                  </template>
                  <template #error>
                    <div class="error-placeholder">
                      <el-icon><Picture /></el-icon>
                      <span>加载失败</span>
                    </div>
                  </template>
                </el-image>
                <div class="cover-mask">
                  <div class="cover-actions">
                    <el-button type="primary" size="small" :loading="uploadLoading">
                      <el-icon><Picture /></el-icon>
                      {{ uploadLoading ? "上传中..." : "更换封面" }}
                    </el-button>
                    <el-button type="danger" size="small" @click.stop="handleRemoveCover">
                      <el-icon><Delete /></el-icon>
                      删除封面
                    </el-button>
                  </div>
                </div>
              </div>
              <div v-else class="upload-placeholder">
                <el-icon v-if="!uploadLoading" class="upload-icon"><Plus /></el-icon>
                <div v-if="uploadLoading" class="uploading">
                  <div class="loading-spinner"></div>
                  <span>上传中...</span>
                </div>
                <div v-else class="upload-text">
                  <div>点击或将图片拖拽到这里上传</div>
                  <div class="upload-tip">支持 JPG、PNG、GIF 格式，文件大小不超过 5MB</div>
                </div>
              </div>
            </el-upload>
          </div>
        </el-form-item>

        <!-- 展示状态 -->
        <el-form-item label="展示状态" prop="showStatus">
          <el-radio-group v-model="editForm.showStatus">
            <el-radio :value="0">
              <el-icon><View /></el-icon>
              公开
            </el-radio>
            <el-radio :value="1">
              <el-icon><Hide /></el-icon>
              私密
            </el-radio>
          </el-radio-group>
        </el-form-item>

        <!-- 审核状态 -->
        <el-form-item label="审核状态">
          <div class="examine-status-display">
            <span class="examine-badge" :class="getExamineStatusClass(editForm.examineStatus)">
              {{ getExamineStatusText(editForm.examineStatus) }}
            </span>
            <div class="form-tip">
              <el-icon><InfoFilled /></el-icon>
              <span>审核状态由管理员控制，用户无法修改</span>
            </div>
          </div>
        </el-form-item>

        <!-- 排序值 -->
        <el-form-item label="排序值" prop="sort">
          <el-input-number v-model="editForm.sort" :min="0" :max="9999" controls-position="right" placeholder="请输入排序值" class="sort-input" />
          <div class="form-tip">
            <el-icon><InfoFilled /></el-icon>
            <span>数值越小排序越靠前</span>
          </div>
        </el-form-item>
      </el-form>

      <template #footer>
        <div class="dialog-footer">
          <el-button @click="handleCancelEdit" :disabled="editSubmitting">取消</el-button>
          <el-button type="primary" @click="handleSubmitEdit" :loading="editSubmitting">
            {{ editSubmitting ? "保存中..." : "保存" }}
          </el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 专栏文章管理对话框 -->
    <el-dialog v-model="manageDialogVisible" title="专栏文章管理" width="800px" :close-on-click-modal="false">
      <div class="column-manage-container">
        <!-- 专栏信息展示 -->
        <div class="column-info" v-if="currentManageColumn">
          <h3>{{ currentManageColumn.name }}</h3>
          <p>{{ currentManageColumn.description || "暂无描述" }}</p>
          <div class="column-stats">
            <span>共 {{ columnArticles.length }} 篇文章</span>
          </div>
        </div>

        <!-- 文章列表 -->
        <div class="article-list-container">
          <div v-if="articleLoading" class="loading-container">
            <el-skeleton animated :count="3">
              <template #template>
                <div class="article-skeleton">
                  <el-skeleton-item variant="image" style="width: 80px; height: 60px" />
                  <div class="skeleton-content">
                    <el-skeleton-item variant="h3" style="width: 70%" />
                    <el-skeleton-item variant="text" style="width: 100%" />
                  </div>
                </div>
              </template>
            </el-skeleton>
          </div>

          <div v-else-if="columnArticles.length === 0" class="empty-state">
            <el-empty description="该专栏暂无文章" />
          </div>

          <div v-else class="article-list">
            <!-- 拖拽排序提示 -->
            <div class="sort-tip">
              <el-icon><InfoFilled /></el-icon>
              <span>拖拽文章可调整排序，点击保存排序按钮生效</span>
            </div>

            <!-- 可拖拽的文章列表 -->
            <div class="draggable-list">
              <div
                v-for="(article, index) in columnArticles"
                :key="article.id"
                class="article-item"
                :class="{ 'sort-changed': article.sortChanged }"
                draggable="true"
                @dragstart="handleDragStart($event, index)"
                @dragover="handleDragOver($event)"
                @drop="handleDrop($event, index)"
                @dragend="handleDragEnd"
              >
                <!-- 拖拽手柄 -->
                <div class="drag-handle">
                  <el-icon><Rank /></el-icon>
                </div>

                <!-- 排序号 -->
                <div class="sort-number">{{ index + 1 }}</div>

                <!-- 文章封面 -->
                <el-image :src="article.coverUrl" class="article-cover" fit="cover">
                  <template #placeholder>
                    <div class="loading-text">加载中...</div>
                  </template>
                  <template #error>
                    <div class="error-placeholder">
                      <el-icon><Picture /></el-icon>
                    </div>
                  </template>
                </el-image>

                <!-- 文章信息 -->
                <div class="article-info">
                  <h4 class="article-title">{{ article.title }}</h4>
                  <p class="article-description">{{ article.description || "暂无描述" }}</p>
                  <div class="article-meta">
                    <span class="article-date">{{ formatTime(article.createTime) }}</span>
                    <span class="article-stats">{{ article.readCount || 0 }} 阅读</span>
                    <span class="examine-badge" :class="getExamineStatusClass(article.examineStatus)">
                      {{ getExamineStatusText(article.examineStatus) }}
                    </span>
                  </div>
                </div>

                <!-- 操作按钮 -->
                <div class="article-actions">
                  <el-button type="danger" text @click="handleRemoveArticle(article)" :loading="removeLoading === article.id">
                    <el-icon><Delete /></el-icon>
                    移除
                  </el-button>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <template #footer>
        <div class="dialog-footer">
          <el-button @click="handleCancelManage">关闭</el-button>
          <el-button type="primary" @click="handleSaveSort" :loading="articleSortLoading" :disabled="!hasSortChanges">
            {{ articleSortLoading ? "保存中..." : "保存排序" }}
          </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from "vue";
import { useRouter } from "vue-router";
import { Search, Picture, Document, Star, ArrowUp, ArrowDown, View, Hide, InfoFilled, Plus, Delete, Rank } from "@element-plus/icons-vue";
// 移除vuedraggable依赖，使用原生HTML5拖拽API
import { getUserColumnManageList, updateColumn, deleteColumn, getColumnDetail, updateColumnArticleSort, removeArticleFromColumn, addColumn } from "@/api/column";
import { uploadColumnPhoto } from "@/api/photo";
import { useUserStore } from "@/stores/userStore";
import { formatTime } from "@/utils/formatTime";

const router = useRouter();
const userStore = useUserStore();

// 审核状态工具函数
const getExamineStatusText = (status) => {
  switch (status) {
    case 0:
      return "待审核";
    case 1:
      return "审核通过";
    case 2:
      return "审核未通过";
    default:
      return "未知状态";
  }
};

const getExamineStatusClass = (status) => {
  switch (status) {
    case 0:
      return "waiting";
    case 1:
      return "passed";
    case 2:
      return "rejected";
    default:
      return "unknown";
  }
};

// 专栏列表数据
const columns = ref([]);
// 加载状态
const loading = ref(false);
const loadingMore = ref(false);
const sortLoading = ref(false);
// 当前页码
const currentPage = ref(1);
// 页面大小
const pageSize = ref(20);
// 是否还有更多数据
const hasMore = ref(true);
// 搜索关键词
const searchKeyword = ref("");
// 列表容器引用
const listContainer = ref(null);

// 年份月份筛选
const selectedYear = ref(null);
const selectedMonth = ref(null);
const availableYears = ref([]);
const showStatus = ref(null);
const examineStatus = ref(null);

// 编辑专栏相关
const editDialogVisible = ref(false);
const editSubmitting = ref(false);
const editFormRef = ref(null);
const uploadRef = ref(null);
const uploadLoading = ref(false);
const editForm = ref({
  id: null,
  name: "",
  description: "",
  coverUrl: "",
  showStatus: 0,
  examineStatus: 0,
  sort: 1,
});

// 编辑表单验证规则
const editFormRules = {
  name: [
    { required: true, message: "请输入专栏名称", trigger: "blur" },
    { min: 1, max: 50, message: "专栏名称长度应在1-50个字符", trigger: "blur" },
  ],
  description: [{ max: 200, message: "专栏描述不能超过200个字符", trigger: "blur" }],
  showStatus: [{ required: true, message: "请选择展示状态", trigger: "change" }],
  sort: [
    { required: true, message: "请输入排序值", trigger: "blur" },
    { type: "number", min: 1, max: 9999, message: "排序值应在1-9999之间", trigger: "blur" },
  ],
};

// 新增专栏相关
const createDialogVisible = ref(false);
const createSubmitting = ref(false);
const createFormRef = ref(null);
const createUploadRef = ref(null);
const createUploadLoading = ref(false);
const createForm = ref({
  name: "",
  description: "",
  coverUrl: "",
  showStatus: 0,
});

// 新增表单验证规则
const createFormRules = {
  name: [
    { required: true, message: "请输入专栏名称", trigger: "blur" },
    { min: 1, max: 50, message: "专栏名称长度应在1-50个字符", trigger: "blur" },
  ],
  description: [{ max: 200, message: "专栏描述不能超过200个字符", trigger: "blur" }],
  showStatus: [{ required: true, message: "请选择展示状态", trigger: "change" }],
};

// 专栏文章管理相关
const manageDialogVisible = ref(false);
const currentManageColumn = ref(null);
const columnArticles = ref([]);
const articleLoading = ref(false);
const articleSortLoading = ref(false);
const removeLoading = ref(null);
const hasSortChanges = ref(false);
const originalArticleOrder = ref([]);

// 拖拽相关状态
const draggedIndex = ref(null);
const dragOverIndex = ref(null);

// 加载专栏列表
const loadColumns = async (reset = false) => {
  // 如果没有更多数据或者已经在加载中，则不再请求
  if (!hasMore.value || loading.value || loadingMore.value) {
    return;
  }

  // 设置加载状态
  if (reset) {
    loading.value = true;
  } else {
    loadingMore.value = true;
  }

  try {
    // 构建筛选参数
    const filterParams = {};

    // 搜索关键词
    if (searchKeyword.value.trim()) {
      filterParams.keyword = searchKeyword.value.trim();
    }

    // 展示状态筛选
    if (showStatus.value !== null) {
      filterParams.showStatus = showStatus.value;
    }

    // 审核状态筛选
    if (examineStatus.value !== null) {
      filterParams.examineStatus = examineStatus.value;
    }

    // 年份筛选
    if (selectedYear.value) {
      filterParams.year = selectedYear.value;
    }

    // 月份筛选
    if (selectedMonth.value) {
      filterParams.month = selectedMonth.value;
    }

    // 发送请求获取专栏列表
    const res = await getUserColumnManageList(currentPage.value, pageSize.value, filterParams);
    const newColumns = res.data.data.data || [];
    const total = res.data.data.total || 0;

    if (reset) {
      // 初次加载或筛选条件改变时
      columns.value = newColumns;
      // 从专栏数据中提取年份信息并更新筛选选项
      updateDateFiltersFromColumns(newColumns);
    } else {
      // 无限滚动时加载下一页数据
      columns.value = [...columns.value, ...newColumns];
      // 合并新数据后更新筛选选项
      updateDateFiltersFromColumns(columns.value);
    }

    // 判断是否还有更多数据
    hasMore.value = columns.value.length < total;

    // 更新页码
    if (hasMore.value && newColumns.length > 0) {
      currentPage.value++;
    }
  } catch (error) {
    console.error("加载专栏列表失败:", error);
    ElMessage.error("加载专栏列表失败");
  } finally {
    // 重置加载状态
    loading.value = false;
    loadingMore.value = false;
  }
};

// 处理搜索
const handleSearch = () => {
  currentPage.value = 1;
  columns.value = [];
  hasMore.value = true;
  loadColumns(true);
};

// 处理新增专栏
const handleCreateColumn = () => {
  // 重置表单数据
  createForm.value = {
    name: "",
    description: "",
    coverUrl: "",
    showStatus: 0,
  };

  // 显示新增对话框
  createDialogVisible.value = true;

  // 重置表单验证状态
  if (createFormRef.value) {
    createFormRef.value.clearValidate();
  }
};

// 处理状态筛选变化
const handleStatusFilterChange = () => {
  currentPage.value = 1;
  columns.value = [];
  hasMore.value = true;
  loadColumns(true);
};

// 处理审核状态筛选变化
const handleExamineStatusFilterChange = () => {
  currentPage.value = 1;
  columns.value = [];
  hasMore.value = true;
  loadColumns(true);
};

// 处理日期筛选变化
const handleDateFilterChange = () => {
  currentPage.value = 1;
  columns.value = [];
  hasMore.value = true;
  loadColumns(true);
};

// 处理滚动事件 - 自定义无限滚动
const handleScroll = () => {
  // 如果没有列表容器或正在加载中或加载更多中或没有更多内容时,不用加载下一页了
  if (!listContainer.value || loading.value || loadingMore.value || !hasMore.value) {
    return;
  }

  const container = listContainer.value;
  // 当滚动到底部附近时加载更多
  if (container.scrollTop + container.clientHeight >= container.scrollHeight - 100) {
    loadColumns();
  }
};

// 从专栏列表中提取年份选项
const updateDateFiltersFromColumns = (columnList) => {
  if (!columnList || columnList.length === 0) {
    availableYears.value = [];
    return;
  }
  // 提取所有专栏的年份并去重排序
  const years = [
    ...new Set(
      columnList.map((column) => {
        const createTime = new Date(column.createTime);
        return createTime.getFullYear();
      })
    ),
  ].sort((a, b) => b - a);
  availableYears.value = years;
};

// 判断是否是第一个专栏（排序值最小，不能上移）
const isFirstColumn = (columnId) => {
  if (columns.value.length === 0) return true;
  const column = columns.value.find((c) => c.id === columnId);
  if (!column) return true;
  return columns.value.every((c) => c.sort >= column.sort);
};

// 判断是否是最后一个专栏（排序值最大，不能下移）
const isLastColumn = (columnId) => {
  if (columns.value.length === 0) return true;
  const column = columns.value.find((c) => c.id === columnId);
  if (!column) return true;
  return columns.value.every((c) => c.sort <= column.sort);
};

// 处理专栏上移
const handleSortUp = async (column) => {
  if (sortLoading.value || isFirstColumn(column.id)) return;

  try {
    sortLoading.value = true;

    // 找到当前专栏在数组中的位置
    const currentIndex = columns.value.findIndex((c) => c.id === column.id);
    if (currentIndex <= 0) return;

    // 找到上一个专栏
    const prevColumn = columns.value[currentIndex - 1];

    // 交换排序值
    const tempSort = column.sort;
    column.sort = prevColumn.sort;
    prevColumn.sort = tempSort;

    // 更新后端数据
    await updateColumn({
      id: column.id,
      name: column.name,
      description: column.description,
      coverUrl: column.coverUrl,
      showStatus: column.showStatus,
      sort: column.sort,
    });

    await updateColumn({
      id: prevColumn.id,
      name: prevColumn.name,
      description: prevColumn.description,
      coverUrl: prevColumn.coverUrl,
      showStatus: prevColumn.showStatus,
      sort: prevColumn.sort,
    });

    // 交换数组中的位置
    columns.value[currentIndex] = prevColumn;
    columns.value[currentIndex - 1] = column;

    ElMessage.success("专栏排序调整成功");
  } catch (error) {
    console.error("专栏排序失败:", error);
    ElMessage.error("专栏排序失败，请重试");
    // 恢复原始排序
    loadColumns(true);
  } finally {
    sortLoading.value = false;
  }
};

// 处理专栏下移
const handleSortDown = async (column) => {
  if (sortLoading.value || isLastColumn(column.id)) return;

  try {
    sortLoading.value = true;

    // 找到当前专栏在数组中的位置
    const currentIndex = columns.value.findIndex((c) => c.id === column.id);
    if (currentIndex >= columns.value.length - 1) return;

    // 找到下一个专栏
    const nextColumn = columns.value[currentIndex + 1];

    // 交换排序值
    const tempSort = column.sort;
    column.sort = nextColumn.sort;
    nextColumn.sort = tempSort;

    // 更新后端数据
    await updateColumn({
      id: column.id,
      name: column.name,
      description: column.description,
      coverUrl: column.coverUrl,
      showStatus: column.showStatus,
      sort: column.sort,
    });

    await updateColumn({
      id: nextColumn.id,
      name: nextColumn.name,
      description: nextColumn.description,
      coverUrl: nextColumn.coverUrl,
      showStatus: nextColumn.showStatus,
      sort: nextColumn.sort,
    });

    // 交换数组中的位置
    columns.value[currentIndex] = nextColumn;
    columns.value[currentIndex + 1] = column;

    ElMessage.success("专栏排序调整成功");
  } catch (error) {
    console.error("专栏排序失败:", error);
    ElMessage.error("专栏排序失败，请重试");
    // 恢复原始排序
    loadColumns(true);
  } finally {
    sortLoading.value = false;
  }
};

// 上传前检查
const beforeUploadCover = (file) => {
  // 检查文件类型
  const isImage = file.type.startsWith("image/");
  if (!isImage) {
    ElMessage.error("只能上传图片文件！");
    return false;
  }

  // 检查文件大小（5MB）
  const isLt5M = file.size / 1024 / 1024 < 5;
  if (!isLt5M) {
    ElMessage.error("图片大小不能超过 5MB！");
    return false;
  }

  return true;
};

// 处理图片上传
const handleUploadCover = async (options) => {
  const { file } = options;

  try {
    uploadLoading.value = true;

    // 调用专栏上传接口
    const res = await uploadColumnPhoto(file);

    // 更新表单数据
    editForm.value.coverUrl = res.data.data;

    ElMessage.success("封面上传成功");
  } catch (error) {
    console.error("封面上传失败:", error);
    ElMessage.error("封面上传失败，请重试");
  } finally {
    uploadLoading.value = false;
  }
};

// 删除封面
const handleRemoveCover = (event) => {
  event.preventDefault();
  event.stopPropagation();

  editForm.value.coverUrl = "";
  ElMessage.success("封面已删除");
};

// 编辑专栏 - 打开编辑对话框
const handleEditColumn = (column) => {
  // 填充编辑表单数据
  editForm.value = {
    id: column.id,
    name: column.name,
    description: column.description || "",
    coverUrl: column.coverUrl || "",
    showStatus: column.showStatus,
    examineStatus: column.examineStatus,
    sort: column.sort,
  };

  // 显示编辑对话框
  editDialogVisible.value = true;

  // 重置表单验证状态
  if (editFormRef.value) {
    editFormRef.value.clearValidate();
  }
};

// 提交编辑专栏
const handleSubmitEdit = async () => {
  if (!editFormRef.value) return;

  try {
    // 验证表单
    const valid = await editFormRef.value.validate();
    if (!valid) return;

    editSubmitting.value = true;

    // 提交更新请求
    await updateColumn(editForm.value);

    ElMessage.success("专栏更新成功");

    // 关闭对话框
    editDialogVisible.value = false;

    // 重新加载专栏列表
    currentPage.value = 1;
    columns.value = [];
    hasMore.value = true;
    loadColumns(true);
  } catch (error) {
    console.error("更新专栏失败:", error);
    ElMessage.error("更新专栏失败，请重试");
  } finally {
    editSubmitting.value = false;
  }
};

// 取消编辑专栏
const handleCancelEdit = async () => {
  // 检查表单是否有未保存的修改
  const originalColumn = columns.value.find((c) => c.id === editForm.value.id);
  const hasChanges =
    originalColumn &&
    (originalColumn.name !== editForm.value.name || (originalColumn.description || "") !== editForm.value.description || (originalColumn.coverUrl || "") !== editForm.value.coverUrl || originalColumn.showStatus !== editForm.value.showStatus || originalColumn.sort !== editForm.value.sort);

  if (hasChanges) {
    try {
      await ElMessageBox.confirm("您有未保存的修改，确定要放弃吗？", "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning",
      });
    } catch {
      return; // 用户取消了放弃操作，继续编辑
    }
  }

  editDialogVisible.value = false;

  // 重置表单数据
  editForm.value = {
    id: null,
    name: "",
    description: "",
    coverUrl: "",
    showStatus: 0,
    examineStatus: 0,
    sort: 1,
  };

  // 清空表单验证
  if (editFormRef.value) {
    editFormRef.value.clearValidate();
  }
};

// 提交新增专栏
const handleSubmitCreate = async () => {
  if (!createFormRef.value) return;

  try {
    // 验证表单
    const valid = await createFormRef.value.validate();
    if (!valid) return;

    createSubmitting.value = true;

    // 提交创建请求
    await addColumn(createForm.value);

    ElMessage.success("专栏创建成功");

    // 关闭对话框
    createDialogVisible.value = false;

    // 重新加载专栏列表
    currentPage.value = 1;
    columns.value = [];
    hasMore.value = true;
    loadColumns(true);
  } catch (error) {
    console.error("创建专栏失败:", error);
    ElMessage.error("创建专栏失败，请重试");
  } finally {
    createSubmitting.value = false;
  }
};

// 取消新增专栏
const handleCancelCreate = async () => {
  // 检查表单是否有未保存的修改
  const hasChanges = createForm.value.name || createForm.value.description || createForm.value.coverUrl;

  if (hasChanges) {
    try {
      await ElMessageBox.confirm("您有未保存的修改，确定要放弃吗？", "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning",
      });
    } catch {
      return; // 用户取消了放弃操作，继续编辑
    }
  }

  createDialogVisible.value = false;

  // 重置表单数据
  createForm.value = {
    name: "",
    description: "",
    coverUrl: "",
    showStatus: 0,
  };

  // 重置表单验证状态
  if (createFormRef.value) {
    createFormRef.value.clearValidate();
  }
};

// 新增专栏上传封面处理
const handleCreateUploadCover = async (options) => {
  const { file } = options;

  try {
    createUploadLoading.value = true;

    // 上传封面
    const res = await uploadColumnPhoto(file);

    // 更新表单数据
    createForm.value.coverUrl = res.data.data;

    ElMessage.success("封面上传成功");
  } catch (error) {
    console.error("上传封面失败:", error);
    ElMessage.error("上传封面失败，请重试");
  } finally {
    createUploadLoading.value = false;
  }
};

// 新增专栏删除封面
const handleCreateRemoveCover = () => {
  createForm.value.coverUrl = "";
  ElMessage.success("封面已删除");
};

// 删除专栏
const handleDeleteColumn = async (columnId) => {
  try {
    await ElMessageBox.confirm("确定要删除这个专栏吗？此操作不可恢复", "删除专栏", {
      confirmButtonText: "确定",
      cancelButtonText: "取消",
      type: "warning",
    });

    await deleteColumn(columnId);
    ElMessage.success("专栏删除成功");

    // 重新加载专栏列表
    currentPage.value = 1;
    columns.value = [];
    hasMore.value = true;
    loadColumns(true);
  } catch (error) {
    if (error !== "cancel") {
      console.error("删除专栏失败:", error);
      ElMessage.error("删除专栏失败，请重试");
    }
  }
};

// 管理专栏文章
const handleManageColumn = async (column) => {
  currentManageColumn.value = column;
  manageDialogVisible.value = true;
  await loadColumnArticles(column.id);
};

// 加载专栏文章列表
const loadColumnArticles = async (columnId) => {
  try {
    articleLoading.value = true;
    const res = await getColumnDetail(columnId);
    columnArticles.value = res.data.data.articles || [];

    // 保存原始排序，用于检测是否有变化
    originalArticleOrder.value = columnArticles.value.map((article, index) => ({
      articleId: article.id,
      sort: index + 1,
    }));

    hasSortChanges.value = false;
  } catch (error) {
    console.error("获取专栏文章失败:", error);
    ElMessage.error("获取专栏文章失败，请重试");
  } finally {
    articleLoading.value = false;
  }
};

// 原生拖拽事件处理
const handleDragStart = (event, index) => {
  draggedIndex.value = index;
  event.dataTransfer.effectAllowed = "move";
  event.dataTransfer.setData("text/html", event.target);
};

const handleDragOver = (event) => {
  event.preventDefault();
  event.dataTransfer.dropEffect = "move";
};

const handleDrop = (event, targetIndex) => {
  event.preventDefault();

  if (draggedIndex.value === null || draggedIndex.value === targetIndex) {
    return;
  }

  // 执行数组元素交换
  const draggedItem = columnArticles.value[draggedIndex.value];
  const newArticles = [...columnArticles.value];

  // 移除拖拽的元素
  newArticles.splice(draggedIndex.value, 1);
  // 在目标位置插入元素
  newArticles.splice(targetIndex, 0, draggedItem);

  columnArticles.value = newArticles;

  // 检查排序变化
  checkSortChanges();
};

const handleDragEnd = () => {
  draggedIndex.value = null;
  dragOverIndex.value = null;
};

// 检查排序是否有变化
const checkSortChanges = () => {
  // 检查排序是否有变化
  const currentOrder = columnArticles.value.map((article, index) => ({
    articleId: article.id,
    sort: index + 1,
  }));

  // 比较当前排序和原始排序
  hasSortChanges.value = !currentOrder.every((item, index) => {
    const original = originalArticleOrder.value[index];
    return original && original.articleId === item.articleId;
  });

  // 标记排序改变的文章
  columnArticles.value.forEach((article, index) => {
    const originalIndex = originalArticleOrder.value.findIndex((item) => item.articleId === article.id);
    article.sortChanged = originalIndex !== index;
  });
};

// 保存排序
const handleSaveSort = async () => {
  if (!hasSortChanges.value) return;

  try {
    articleSortLoading.value = true;

    // 构建排序数据
    const sortList = columnArticles.value.map((article, index) => ({
      articleId: article.id,
      sort: index + 1,
    }));

    await updateColumnArticleSort(currentManageColumn.value.id, sortList);
    ElMessage.success("文章排序保存成功");

    // 更新原始排序
    originalArticleOrder.value = [...sortList];
    hasSortChanges.value = false;

    // 清除排序变化标记
    columnArticles.value.forEach((article) => {
      article.sortChanged = false;
    });
  } catch (error) {
    console.error("保存排序失败:", error);
    ElMessage.error("保存排序失败，请重试");
  } finally {
    articleSortLoading.value = false;
  }
};

// 从专栏中移除文章
const handleRemoveArticle = async (article) => {
  try {
    await ElMessageBox.confirm(`确定要从专栏中移除文章"${article.title}"吗？`, "移除文章", {
      confirmButtonText: "确定",
      cancelButtonText: "取消",
      type: "warning",
    });

    removeLoading.value = article.id;

    await removeArticleFromColumn(currentManageColumn.value.id, article.id);
    ElMessage.success("文章移除成功");

    // 从列表中移除文章
    const index = columnArticles.value.findIndex((item) => item.id === article.id);
    if (index > -1) {
      columnArticles.value.splice(index, 1);
    }

    // 更新专栏文章数量
    if (currentManageColumn.value.articleCount > 0) {
      currentManageColumn.value.articleCount--;
    }

    // 重新加载主列表中的专栏数据
    const columnIndex = columns.value.findIndex((col) => col.id === currentManageColumn.value.id);
    if (columnIndex > -1 && columns.value[columnIndex].articleCount > 0) {
      columns.value[columnIndex].articleCount--;
    }

    // 更新原始排序数据
    originalArticleOrder.value = columnArticles.value.map((article, index) => ({
      articleId: article.id,
      sort: index + 1,
    }));

    hasSortChanges.value = false;
  } catch (error) {
    if (error !== "cancel") {
      console.error("移除文章失败:", error);
      ElMessage.error("移除文章失败，请重试");
    }
  } finally {
    removeLoading.value = null;
  }
};

// 取消管理
const handleCancelManage = () => {
  if (hasSortChanges.value) {
    ElMessageBox.confirm("您有未保存的排序修改，确定要关闭吗？", "提示", {
      confirmButtonText: "确定",
      cancelButtonText: "取消",
      type: "warning",
    })
      .then(() => {
        manageDialogVisible.value = false;
        resetManageData();
      })
      .catch(() => {
        // 用户取消，不关闭对话框
      });
  } else {
    manageDialogVisible.value = false;
    resetManageData();
  }
};

// 重置管理数据
const resetManageData = () => {
  currentManageColumn.value = null;
  columnArticles.value = [];
  originalArticleOrder.value = [];
  hasSortChanges.value = false;
  removeLoading.value = null;
};

// 跳转到专栏详情页面
const goToColumnDetail = (column) => {
  // 获取专栏作者ID，如果没有则使用当前用户ID
  const userId = column.userId || userStore.user?.id;
  if (!userId) {
    ElMessage.warning("无法获取用户信息");
    return;
  }

  // 跳转到专栏详情页面
  const routeUrl = router.resolve(`/user/${userId}/column/${column.id}`);
  window.open(routeUrl.href, "_blank");
};

// 组件挂载时的处理
onMounted(() => {
  loadColumns(true);
});

// 组件卸载时的处理
onUnmounted(() => {
  // 清理资源
  listContainer.value = null;
});
</script>

<style lang="scss" scoped>
.column-manage-container {
  display: flex;
  flex-direction: column;
  height: calc(100vh - 48px);

  .main-content {
    // 筛选区域
    .filter-section {
      background-color: var(--el-bg-color);
      border-radius: 4px;
      box-shadow: 0 1px 2px rgba(0, 0, 0, 0.1);
      padding: 16px;
      margin-bottom: 16px;

      .filter-row {
        display: flex;
        align-items: center;
        gap: 16px;
        flex-wrap: wrap;

        .filter-item {
          .filter-select {
            width: 180px;
          }

          .search-input {
            width: 220px;
          }

          .select-prefix {
            margin-right: 5px;
            font-size: 14px;
            color: var(--el-text-color-regular);
          }

          // 新增专栏按钮样式
          &.create-button-wrapper {
            margin-left: auto; // 推到右边

            .create-column-btn {
              background: linear-gradient(135deg, var(--el-color-primary), var(--el-color-primary-light-3));
              border: none;
              border-radius: 8px;
              padding: 10px 20px;
              font-weight: 600;
              font-size: 14px;
              box-shadow: 0 4px 12px rgba(64, 158, 255, 0.3);
              transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
              position: relative;
              overflow: hidden;

              &::before {
                content: "";
                position: absolute;
                top: 0;
                left: -100%;
                width: 100%;
                height: 100%;
                background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.3), transparent);
                transition: left 0.6s;
              }

              &:hover {
                transform: translateY(-2px);
                box-shadow: 0 6px 20px rgba(64, 158, 255, 0.4);
                background: linear-gradient(135deg, var(--el-color-primary-light-3), var(--el-color-primary));

                &::before {
                  left: 100%;
                }
              }

              &:active {
                transform: translateY(0);
                box-shadow: 0 2px 8px rgba(64, 158, 255, 0.3);
              }

              .el-icon {
                margin-right: 6px;
                font-size: 16px;
                transition: transform 0.3s ease;
              }

              .btn-text {
                font-weight: 600;
                letter-spacing: 0.5px;
              }

              &:hover .el-icon {
                transform: rotate(90deg);
              }
            }
          }
        }
      }
    }

    // 专栏列表容器
    .column-list-container {
      background-color: var(--el-bg-color);
      border-radius: 4px;
      box-shadow: 0 1px 2px rgba(0, 0, 0, 0.1);
      flex: 1;
      overflow-y: auto;
      position: relative;
      max-height: calc(100vh - 200px);

      // 加载容器
      .loading-container,
      .loading-more {
        display: flex;
        align-items: center;
        justify-content: center;
        padding: 40px;
        color: var(--el-text-color-secondary);

        .loading-spinner {
          margin-right: 10px;
        }
      }

      // 空状态容器
      .empty-container {
        padding: 60px 20px;
        text-align: center;
      }

      // 专栏卡片列表
      .column-cards {
        display: grid;
        grid-template-columns: repeat(auto-fill, minmax(360px, 1fr));
        gap: 16px;
        padding: 16px;

        :deep(.el-card__body) {
          height: 100% !important;
        }

        .column-card {
          transition: all 0.3s ease;

          &:hover {
            transform: translateY(-2px);
            box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
          }

          .column-card-content {
            display: flex;
            flex-direction: column;
            height: 100%;

            // 专栏信息区域
            .column-info {
              display: flex;
              align-items: flex-start;
              gap: 12px;
              padding-bottom: 12px;
              border-bottom: 1px solid var(--el-border-color-light);

              .column-cover {
                width: 160px;
                height: 100px;
                border-radius: 6px;
                flex-shrink: 0;
                cursor: pointer;
                transition: all 0.3s ease;

                &:hover {
                  transform: scale(1.05);
                  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
                }

                .loading-text {
                  display: flex;
                  justify-content: center;
                  align-items: center;
                  width: 100%;
                  height: 100%;
                  font-size: 12px;
                  color: var(--el-text-color-regular);
                  background-color: var(--el-bg-color-page);
                }

                // 错误占位图标样式
                .error {
                  display: flex;
                  justify-content: center;
                  align-items: center;
                  width: 100%;
                  height: 100%;
                  background-color: var(--el-bg-color-page);

                  .el-icon {
                    font-size: 24px;
                    color: var(--el-text-color-placeholder);
                  }
                }
              }

              .column-details {
                flex: 1;
                min-width: 0;

                .column-title {
                  font-size: 16px;
                  font-weight: 600;
                  color: var(--el-text-color-primary);
                  margin: 0 0 8px 0;
                  overflow: hidden;
                  text-overflow: ellipsis;
                  display: -webkit-box;
                  -webkit-line-clamp: 2;
                  -webkit-box-orient: vertical;
                  line-height: 1.4;
                  cursor: pointer;
                  transition: color 0.3s ease;

                  &:hover {
                    color: var(--el-color-primary);
                  }
                }

                .column-description {
                  font-size: 14px;
                  color: var(--el-text-color-regular);
                  margin: 0;
                  line-height: 1.5;
                  overflow: hidden;
                  text-overflow: ellipsis;
                  display: -webkit-box;
                  -webkit-line-clamp: 2;
                  -webkit-box-orient: vertical;
                }
              }
            }

            // 专栏统计信息
            .column-stats {
              display: flex;
              gap: 16px;
              padding: 8px 0;
              border-bottom: 1px solid var(--el-border-color-lighter);
              margin-bottom: 12px;

              .stat-item {
                display: flex;
                align-items: center;
                gap: 4px;
                color: var(--el-text-color-secondary);
                font-size: 13px;

                .el-icon {
                  font-size: 16px;
                }

                .status-badge {
                  padding: 2px 8px;
                  border-radius: 4px;
                  font-size: 12px;
                  font-weight: 500;

                  &.public {
                    background-color: #e8f5e8;
                    color: #52c41a;
                  }

                  &.private {
                    background-color: #fff7e6;
                    color: #fa8c16;
                  }
                }

                .examine-badge {
                  padding: 2px 8px;
                  border-radius: 4px;
                  font-size: 12px;
                  font-weight: 500;

                  &.waiting {
                    background-color: #fff7e6;
                    color: #fa8c16;
                  }

                  &.passed {
                    background-color: #f6ffed;
                    color: #52c41a;
                  }

                  &.rejected {
                    background-color: #fff2f0;
                    color: #ff4d4f;
                  }

                  &.unknown {
                    background-color: #f5f5f5;
                    color: #8c8c8c;
                  }
                }
              }
            }

            // 专栏元信息
            .column-meta {
              display: flex;
              justify-content: space-between;
              align-items: center;
              font-size: 13px;
              color: var(--el-text-color-secondary);
              margin-bottom: 12px;

              .column-sort {
                font-weight: 500;
                color: var(--el-color-primary);
              }
            }

            // 专栏操作
            .column-actions {
              display: flex;
              justify-content: space-between;
              align-items: center;
              // gap: 8px;
              padding-top: 8px;
              border-top: 1px solid var(--el-border-color-lighter);

              .sort-actions {
                display: flex;
                :deep(.el-button) {
                  margin-left: 0;
                }
              }

              .operation-actions {
                display: flex;
                :deep(.el-button) {
                  margin-left: 0;
                }
              }
            }
          }
        }
      }
    }
  }

  // 自定义加载指示器样式
  .loading-spinner {
    width: 20px;
    height: 20px;
    border: 2px solid #f3f3f3;
    border-top: 2px solid var(--el-color-primary);
    border-radius: 50%;
    animation: spin 1s linear infinite;

    // 小尺寸加载指示器
    &.small {
      width: 16px;
      height: 16px;
      border-width: 1px;
      margin-right: 8px;
      display: inline-block;
      vertical-align: middle;
    }
  }

  // 加载动画
  @keyframes spin {
    0% {
      transform: rotate(0deg);
    }

    100% {
      transform: rotate(360deg);
    }
  }
}

// 新增和编辑专栏对话框样式
:deep(.el-dialog) {
  .create-column-form,
  .edit-column-form {
    // 封面上传容器
    .cover-upload-container {
      .cover-uploader {
        width: 100%;

        :deep(.el-upload) {
          width: 100%;
          border: 1px dashed var(--el-border-color);
          border-radius: 6px;
          cursor: pointer;
          position: relative;
          overflow: hidden;
          transition: var(--el-transition-duration-fast);

          &:hover {
            border-color: var(--el-color-primary);
          }
        }

        :deep(.el-upload-dragger) {
          padding: 0;
          width: 100%;
          height: auto;
          background-color: transparent;
          border: none;
        }

        .cover-preview {
          position: relative;
          width: 100%;
          height: 200px;

          .preview-image {
            width: 100%;
            height: 100%;
            border-radius: 6px;
          }

          .loading-text {
            display: flex;
            justify-content: center;
            align-items: center;
            width: 100%;
            height: 100%;
            font-size: 14px;
            color: var(--el-text-color-secondary);
            background-color: var(--el-fill-color-light);
            border-radius: 6px;
          }

          .error-placeholder {
            display: flex;
            flex-direction: column;
            justify-content: center;
            align-items: center;
            width: 100%;
            height: 100%;
            color: var(--el-text-color-placeholder);
            background-color: var(--el-fill-color-light);
            border-radius: 6px;

            .el-icon {
              font-size: 32px;
              margin-bottom: 8px;
            }

            span {
              font-size: 14px;
            }
          }

          .cover-mask {
            position: absolute;
            top: 0;
            left: 0;
            width: 100%;
            height: 100%;
            background-color: rgba(0, 0, 0, 0.5);
            border-radius: 6px;
            display: flex;
            justify-content: center;
            align-items: center;
            opacity: 0;
            transition: opacity 0.3s ease;

            &:hover {
              opacity: 1;
            }

            .cover-actions {
              display: flex;
              gap: 8px;
            }
          }
        }

        .upload-placeholder {
          display: flex;
          flex-direction: column;
          justify-content: center;
          align-items: center;
          height: 200px;
          color: var(--el-text-color-secondary);

          .upload-icon {
            font-size: 48px;
            margin-bottom: 16px;
            color: var(--el-color-primary);
          }

          .uploading {
            display: flex;
            flex-direction: column;
            align-items: center;
            gap: 12px;

            .loading-spinner {
              width: 32px;
              height: 32px;
              border: 3px solid var(--el-border-color);
              border-top: 3px solid var(--el-color-primary);
              border-radius: 50%;
              animation: spin 1s linear infinite;
            }

            span {
              font-size: 14px;
              color: var(--el-color-primary);
            }
          }

          .upload-text {
            text-align: center;

            div:first-child {
              font-size: 16px;
              margin-bottom: 8px;
            }

            .upload-tip {
              font-size: 12px;
              color: var(--el-text-color-placeholder);
            }
          }
        }
      }
    }

    // 排序输入框
    .sort-input {
      width: 200px;
    }

    // 表单提示信息
    .form-tip {
      display: flex;
      align-items: center;
      gap: 4px;
      font-size: 12px;
      color: var(--el-text-color-secondary);

      .el-icon {
        font-size: 14px;
      }
    }

    // 审核状态显示
    .examine-status-display {
      display: flex;
      flex-direction: column;
      gap: 8px;

      .examine-badge {
        padding: 4px 12px;
        border-radius: 4px;
        font-size: 13px;
        font-weight: 500;
        display: inline-block;
        width: fit-content;

        &.waiting {
          background-color: #fff7e6;
          color: #fa8c16;
        }

        &.passed {
          background-color: #f6ffed;
          color: #52c41a;
        }

        &.rejected {
          background-color: #fff2f0;
          color: #ff4d4f;
        }

        &.unknown {
          background-color: #f5f5f5;
          color: #8c8c8c;
        }
      }
    }

    // 单选按钮组样式
    .el-radio-group {
      .el-radio {
        display: flex;
        align-items: center;
        margin-right: 24px;
      }
    }
  }

  // 对话框底部按钮
  .dialog-footer {
    display: flex;
    justify-content: flex-end;
    gap: 12px;
  }
}

// 专栏文章管理对话框样式
:deep(.el-dialog) {
  .column-manage-container {
    max-height: 70vh;
    // 专栏信息展示
    .column-info {
      padding: 20px;
      background: var(--el-bg-color-page);
      border-radius: 8px;
      margin-bottom: 20px;
      border: 1px solid var(--el-border-color);

      h3 {
        font-size: 18px;
        font-weight: 600;
        color: var(--el-text-color-primary);
        margin: 0 0 8px 0;
      }

      p {
        font-size: 14px;
        color: var(--el-text-color-regular);
        margin: 0 0 12px 0;
        line-height: 1.5;
      }

      .column-stats {
        font-size: 13px;
        color: var(--el-text-color-secondary);
      }
    }

    // 文章列表容器
    .article-list-container {
      // max-height: 500px;
      overflow-y: auto;

      // 加载容器
      .loading-container {
        padding: 20px 0;
      }

      // 骨架屏样式
      .article-skeleton {
        display: flex;
        gap: 12px;
        padding: 16px 0;
        border-bottom: 1px solid var(--el-border-color-light);

        .skeleton-content {
          flex: 1;
          display: flex;
          flex-direction: column;
          gap: 8px;
        }
      }

      // 空状态
      .empty-state {
        padding: 60px 0;
        text-align: center;
      }

      // 文章列表
      .article-list {
        // 排序提示
        .sort-tip {
          display: flex;
          align-items: center;
          gap: 8px;
          padding: 12px 16px;
          background: var(--el-color-info-light-9);
          border: 1px solid var(--el-color-info-light-7);
          border-radius: 6px;
          margin-bottom: 16px;
          font-size: 13px;
          color: var(--el-color-info);

          .el-icon {
            font-size: 16px;
          }
        }

        // 可拖拽列表
        .draggable-list {
          .article-item {
            display: flex;
            align-items: center;
            gap: 12px;
            padding: 16px;
            background: var(--el-bg-color-page);
            border: 1px solid var(--el-border-color-light);
            border-radius: 8px;
            margin-bottom: 12px;
            transition: all 0.3s ease;
            cursor: move;

            &:hover {
              border-color: var(--el-color-primary-light-7);
              box-shadow: 0 2px 8px var(--el-color-primary-light-9);
            }

            &.sort-changed {
              border-color: var(--el-color-warning);
              background: var(--el-color-warning-light-9);
            }

            // 拖拽手柄
            .drag-handle {
              color: var(--el-text-color-placeholder);
              cursor: grab;

              &:active {
                cursor: grabbing;
              }

              .el-icon {
                font-size: 18px;
              }
            }

            // 排序号
            .sort-number {
              width: 24px;
              height: 24px;
              background: var(--el-color-primary);
              color: white;
              border-radius: 50%;
              display: flex;
              align-items: center;
              justify-content: center;
              font-size: 12px;
              font-weight: 600;
            }

            // 文章封面
            .article-cover {
              width: 80px;
              height: 60px;
              border-radius: 6px;
              flex-shrink: 0;

              .loading-text {
                display: flex;
                justify-content: center;
                align-items: center;
                width: 100%;
                height: 100%;
                font-size: 12px;
                color: var(--el-text-color-regular);
                background-color: var(--el-bg-color-page);
              }

              .error-placeholder {
                display: flex;
                justify-content: center;
                align-items: center;
                width: 100%;
                height: 100%;
                background-color: var(--el-bg-color-page);

                .el-icon {
                  font-size: 20px;
                  color: var(--el-text-color-placeholder);
                }
              }
            }

            // 文章信息
            .article-info {
              flex: 1;
              min-width: 0;

              .article-title {
                font-size: 16px;
                font-weight: 600;
                color: var(--el-text-color-primary);
                margin: 0 0 8px 0;
                line-height: 1.4;
                display: -webkit-box;
                -webkit-line-clamp: 2;
                -webkit-box-orient: vertical;
                overflow: hidden;
              }

              .article-description {
                font-size: 13px;
                color: var(--el-text-color-regular);
                margin: 0 0 8px 0;
                line-height: 1.4;
                display: -webkit-box;
                -webkit-line-clamp: 2;
                -webkit-box-orient: vertical;
                overflow: hidden;
              }

              // 文章元信息
              .article-meta {
                display: flex;
                align-items: center;
                gap: 12px;
                font-size: 12px;
                color: var(--el-text-color-secondary);

                .article-date {
                  color: var(--el-text-color-secondary);
                }

                .article-stats {
                  color: var(--el-text-color-secondary);
                }

                .examine-badge {
                  padding: 2px 6px;
                  border-radius: 3px;
                  font-size: 11px;
                  font-weight: 500;

                  &.waiting {
                    background-color: #fff7e6;
                    color: #fa8c16;
                  }

                  &.passed {
                    background-color: #f6ffed;
                    color: #52c41a;
                  }

                  &.rejected {
                    background-color: #fff2f0;
                    color: #ff4d4f;
                  }
                }
              }
            }

            // 操作按钮
            .article-actions {
              flex-shrink: 0;
            }
          }
        }
      }
    }
  }
}

// 响应式设计 - 平板端
@media screen and (max-width: 992px) and (min-width: 769px) {
  .column-manage-container {
    .main-content {
      .column-list-container {
        .column-cards {
          grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
        }
      }
    }
  }
}

// 响应式设计 - 移动端
@media screen and (max-width: 768px) {
  .column-manage-container {
    height: calc(100vh);

    .main-content {
      .filter-section {
        padding: 10px;
        margin-bottom: 10px;

        .filter-row {
          flex-direction: column;
          align-items: stretch;
          gap: 10px;

          .filter-item {
            .filter-select,
            .search-input {
              width: 100%;
            }

            // 移动端新增按钮样式
            &.create-button-wrapper {
              margin-left: 0;
              margin-top: 8px;

              .create-column-btn {
                width: 100%;
                padding: 12px 20px;
                font-size: 16px;
                border-radius: 10px;
                box-shadow: 0 3px 8px rgba(64, 158, 255, 0.25);

                &:hover {
                  transform: none; // 移动端不需要上浮效果
                  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.35);
                }

                &:active {
                  transform: scale(0.98);
                }
              }
            }
          }
        }
      }

      .column-list-container {
        max-height: calc(100vh - 60px);

        .column-cards {
          grid-template-columns: 1fr;
          padding: 10px;
          gap: 12px;

          .column-card {
            .column-card-content {
              .column-info {
                .column-cover {
                  width: 80px;
                  height: 50px;
                }

                .column-details {
                  .column-title {
                    font-size: 15px;
                    -webkit-line-clamp: 1;
                  }

                  .column-description {
                    font-size: 13px;
                    -webkit-line-clamp: 1;
                  }
                }
              }

              .column-stats {
                flex-wrap: wrap;
                gap: 8px;

                .stat-item {
                  font-size: 12px;

                  .el-icon {
                    font-size: 14px;
                  }

                  .status-badge {
                    font-size: 11px;
                    padding: 1px 6px;
                  }
                }
              }

              .column-meta {
                flex-direction: column;
                align-items: flex-start;
                gap: 4px;
                font-size: 12px;
              }

              .column-actions {
                flex-direction: column;
                align-items: stretch;
                gap: 8px;

                .sort-actions,
                .operation-actions {
                  justify-content: center;
                }
              }
            }
          }
        }
      }
    }
  }
}
</style>

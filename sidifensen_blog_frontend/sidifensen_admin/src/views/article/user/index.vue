<template>
  <div class="management-container">
    <!-- 用户列表视图 -->
    <div v-if="!showArticles" class="card">
      <div class="card-header">
        <h2 class="card-title">用户文章管理</h2>
        <div class="card-actions">
          <el-input v-model="searchUserKeyword" placeholder="搜索用户名" class="search-input" size="small" clearable @input="handleUserSearch">
            <template #prefix>
              <el-icon><Search /></el-icon>
            </template>
          </el-input>
        </div>
      </div>

      <!-- 用户列表卡片 -->
      <div class="user-list-container" v-loading="userLoading">
        <div v-if="filteredUserList.length === 0" class="empty-container">
          <el-empty description="暂无用户数据"></el-empty>
        </div>
        <div v-else class="user-cards">
          <el-card v-for="user in filteredUserList" :key="user.id" class="user-card" shadow="hover">
            <div class="user-card-content">
              <div class="user-avatar">
                <el-avatar :src="user.avatar" :size="60">
                  <template #default>
                    <el-icon><User /></el-icon>
                  </template>
                </el-avatar>
              </div>
              <div class="user-right-content">
                <div class="user-info">
                  <div class="user-id">用户ID: {{ user.id }}</div>
                  <div class="user-name">
                    <span class="username">{{ user.username }}</span>
                    <span v-if="user.nickname" class="nickname">({{ user.nickname }})</span>
                  </div>
                  <div class="article-count">
                    <span class="article-count-label">文章数量:</span>
                    <span class="article-count-value">{{ user.articleCount || 0 }}</span>
                  </div>
                </div>
                <div class="user-actions">
                  <el-button type="primary" size="default" @click="handleViewUserArticles(user)" :icon="Document" class="view-articles-btn"> 查看文章 </el-button>
                </div>
              </div>
            </div>
          </el-card>
        </div>
      </div>
    </div>

    <!-- 用户文章列表视图 -->
    <ManagementCard
      v-else
      :title="(currentUser?.nickname || currentUser?.username) + '的文章'"
      :showTimeFilter="true"
      :showPagination="true"
      :modelCurrentPage="currentPage"
      :modelPageSize="pageSize"
      :total="total"
      @search="fetchUserArticlesData"
      @timeChange="handleTimeChange"
    >
      <!-- 返回按钮 -->
      <template #filters>
        <el-button @click="handleBackToUsers" :icon="ArrowLeft" size="small" plain>返回用户列表</el-button>
        <ExamineStatusSelect v-model="searchExamineStatus" @change="handleSearch" />
        <KeywordSearch v-model="searchKeyword" placeholder="搜索文章标题" label="" width="160px" :debounce="0" :prefixIcon="Search" @search="handleSearch" />
        <SearchButtons @search="handleSearch" @reset="handleReset" />
      </template>

      <!-- 批量操作按钮 -->
      <template #batch-actions>
        <BatchActions :selectedCount="selectedArticles.length" :showBatchDelete="true" @batchDelete="handleBatchDelete" />
      </template>

      <!-- 桌面端表格视图 -->
      <template #table-view>
        <DataTable
          v-loading="loading"
          :data="paginatedArticleList"
          :show-selection="true"
          :show-cover="true"
          :show-id="true"
          :show-actions="true"
          :has-detail-action="true"
          :has-edit-action="true"
          :has-delete-action="true"
          :actions-width="240"
          @selection-change="handleSelectionChange"
          @detail="handleViewArticle"
          @edit="handleEditArticle"
          @delete="handleDeleteArticle"
        >
          <!-- 文章标题列 -->
          <el-table-column prop="title" label="文章标题" min-width="180">
            <template #default="{ row }">
              <el-tooltip :content="row.title" placement="top-start">
                <div class="article-title">{{ row.title }}</div>
              </el-tooltip>
            </template>
          </el-table-column>
          <!-- 标签列 -->
          <el-table-column prop="tag" label="标签" min-width="150">
            <template #default="{ row }">
              <el-tag v-if="row.tag" size="small" type="info">{{ row.tag }}</el-tag>
              <span v-else class="no-tag">无</span>
            </template>
          </el-table-column>
          <!-- 类型列 -->
          <el-table-column prop="reprintType" label="类型" width="70">
            <template #default="{ row }">
              <el-tag :type="row.reprintType === 0 ? 'success' : 'warning'" size="small">
                {{ row.reprintType === 0 ? '原创' : '转载' }}
              </el-tag>
            </template>
          </el-table-column>
          <!-- 可见范围列 -->
          <el-table-column prop="visibleRange" label="可见范围" width="90">
            <template #default="{ row }">
              <el-tag :type="getVisibleRangeType(row.visibleRange)" size="small">
                {{ getVisibleRangeText(row.visibleRange) }}
              </el-tag>
            </template>
          </el-table-column>
          <!-- 编辑状态列 -->
          <el-table-column prop="editStatus" label="编辑状态" width="80">
            <template #default="{ row }">
              <el-tag :type="getEditStatusType(row.editStatus)" size="small">
                {{ getEditStatusText(row.editStatus) }}
              </el-tag>
            </template>
          </el-table-column>
          <!-- 审核状态列 -->
          <el-table-column prop="examineStatus" label="审核状态" width="80">
            <template #default="{ row }">
              <StatusBadge :status="row.examineStatus" :statusMap="examineStatusMap" />
            </template>
          </el-table-column>
          <!-- 阅读量列 -->
          <el-table-column prop="readCount" label="阅读量" width="70" />
          <!-- 点赞量列 -->
          <el-table-column prop="likeCount" label="点赞量" width="70" />
          <!-- 评论数列 -->
          <el-table-column prop="commentCount" label="评论数" width="70" />
          <!-- 收藏量列 -->
          <el-table-column prop="collectCount" label="收藏量" width="70" />
          <!-- 创建时间列 -->
          <el-table-column prop="createTime" label="创建时间" sortable width="110" />
          <!-- 更新时间列 -->
          <el-table-column prop="updateTime" label="更新时间" sortable width="110" />
        </DataTable>
      </template>

      <!-- 移动端卡片视图 -->
      <template #card-view>
        <MobileCardList
          :data="paginatedArticleList"
          :selectedItems="selectedArticles"
          showSelection
          showMeta
          :hasDetailAction="true"
          :hasEditAction="true"
          :hasDeleteAction="true"
          @select="handleMobileSelect"
          @detail="handleViewArticle"
          @edit="handleEditArticle"
          @delete="handleDeleteArticle"
        >
          <!-- 自定义卡片内容 -->
          <template #custom="{ item }">
            <div class="mobile-meta">
              <el-tag :type="item.reprintType === 0 ? 'success' : 'warning'" size="small">{{ item.reprintType === 0 ? '原创' : '转载' }}</el-tag>
              <StatusBadge :status="item.examineStatus" :statusMap="examineStatusMap" />
            </div>
            <div class="mobile-stats">
              <span
                ><el-icon><View /></el-icon> {{ item.readCount || 0 }}</span
              >
              <span><svg-icon name="like" width="12px" height="12px" /> {{ item.likeCount || 0 }}</span>
              <span
                ><el-icon><ChatDotRound /></el-icon> {{ item.commentCount || 0 }}</span
              >
            </div>
            <div class="mobile-time">创建: {{ item.createTime }}</div>
          </template>
        </MobileCardList>
      </template>
    </ManagementCard>

    <!-- 文章详情对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="90%"
      class="article-detail-dialog"
      :close-on-click-modal="false"
      :close-on-press-escape="true"
      draggable
      align-center
      @close="handleDialogClose"
    >
      <div v-if="currentArticle" class="article-detail" v-loading="detailLoading">
        <!-- 文章基本信息 -->
        <div class="article-info-section">
          <div class="article-detail-header">
            <!-- 左侧：文章信息 -->
            <div class="article-detail-info">
              <div class="article-title-section">
                <h2 class="article-title-detail">{{ currentArticle?.title || '无标题' }}</h2>
                <div class="article-id-detail">#{{ currentArticle?.id || 'N/A' }}</div>
              </div>

              <div class="article-author-section">
                <el-icon class="author-icon"><User /></el-icon>
                <span class="author-name-detail">{{ currentUser?.nickname || currentUser?.username || '未知作者' }}</span>
              </div>

              <div class="article-description-detail" v-if="currentArticle && currentArticle.description && currentArticle.description.trim()">
                <el-icon class="desc-icon"><Document /></el-icon>
                <span>{{ currentArticle.description }}</span>
              </div>
              <div v-else-if="currentArticle" class="article-description-detail no-description">
                <el-icon class="desc-icon"><Document /></el-icon>
                <span>暂无描述</span>
              </div>

              <!-- 状态标签 -->
              <div class="article-badges-detail">
                <div class="badge-group">
                  <span class="badge-label">文章标签:</span>
                  <el-tag v-if="currentArticle && currentArticle.tag" type="info" size="small">{{ currentArticle.tag }}</el-tag>
                  <span v-else class="no-data">无标签</span>
                </div>

                <div class="badge-group">
                  <span class="badge-label">所属专栏:</span>
                  <template v-if="currentArticle && currentArticle.columns && currentArticle.columns.length > 0">
                    <el-tag v-for="column in currentArticle.columns" :key="column.id" type="primary" size="small" class="column-tag">{{ column.name }}</el-tag>
                  </template>
                  <span v-else class="no-data">无专栏</span>
                </div>

                <div class="badge-group">
                  <span class="badge-label">文章状态:</span>
                  <el-tag :type="(currentArticle?.reprintType || 0) === 0 ? 'success' : 'warning'" size="small">
                    {{ (currentArticle?.reprintType || 0) === 0 ? '原创' : '转载' }}
                  </el-tag>
                  <el-tag :type="getVisibleRangeType(currentArticle?.visibleRange || 0)" size="small">
                    {{ getVisibleRangeText(currentArticle?.visibleRange || 0) }}
                  </el-tag>
                  <el-tag :type="getEditStatusType(currentArticle?.editStatus || 0)" size="small">
                    {{ getEditStatusText(currentArticle?.editStatus || 0) }}
                  </el-tag>
                </div>

                <!-- 转载链接 -->
                <div v-if="(currentArticle?.reprintType || 0) === 1 && currentArticle?.reprintUrl" class="badge-group reprint-url-group">
                  <span class="badge-label">转载链接:</span>
                  <a :href="currentArticle.reprintUrl" target="_blank" rel="noopener noreferrer" class="reprint-url-link">
                    {{ currentArticle.reprintUrl }}
                    <el-icon class="external-link-icon"><Top /></el-icon>
                  </a>
                </div>

                <div class="badge-group">
                  <span class="badge-label">审核状态:</span>
                  <StatusBadge :status="currentArticle?.examineStatus || 0" :statusMap="examineStatusMap" />
                </div>
              </div>
            </div>

            <!-- 右侧：文章封面 -->
            <div class="article-cover-detail">
              <el-image
                v-if="currentArticle && currentArticle.coverUrl"
                :src="currentArticle.coverUrl"
                class="detail-cover-img"
                :preview-src-list="[currentArticle.coverUrl]"
                fit="cover"
                preview-teleported
              />
              <div v-else class="no-cover-detail">
                <el-icon class="cover-icon"><Picture /></el-icon>
                <span>暂无封面</span>
              </div>
            </div>
          </div>

          <!-- 底部：统计数据和时间信息 -->
          <div class="article-stats-detail">
            <div class="stats-group">
              <div class="stat-item">
                <el-icon class="stat-icon"><View /></el-icon>
                <span class="stat-label">阅读</span>
                <span class="stat-value">{{ currentArticle?.readCount || 0 }}</span>
              </div>
              <div class="stat-item">
                <svg-icon name="like" width="16px" height="16px" color="var(--text-muted)" />
                <span class="stat-label">点赞</span>
                <span class="stat-value">{{ currentArticle?.likeCount || 0 }}</span>
              </div>
              <div class="stat-item">
                <el-icon class="stat-icon"><ChatDotRound /></el-icon>
                <span class="stat-label">评论</span>
                <span class="stat-value">{{ currentArticle?.commentCount || 0 }}</span>
              </div>
              <div class="stat-item">
                <el-icon class="stat-icon"><Star /></el-icon>
                <span class="stat-label">收藏</span>
                <span class="stat-value">{{ currentArticle?.collectCount || 0 }}</span>
              </div>
              <div class="stat-item time-stat-item">
                <el-icon class="stat-icon"><Clock /></el-icon>
                <span class="stat-label">创建:</span>
                <span class="stat-value">{{ currentArticle?.createTime || '未知' }}</span>
              </div>
              <div class="stat-item time-stat-item">
                <el-icon class="stat-icon"><Refresh /></el-icon>
                <span class="stat-label">更新:</span>
                <span class="stat-value">{{ currentArticle?.updateTime || '未知' }}</span>
              </div>
            </div>
          </div>
        </div>

        <!-- 文章内容 -->
        <div class="article-content-section">
          <div class="content-header">
            <h3>文章内容</h3>
            <el-divider />
          </div>
          <div class="article-content" v-html="currentArticle?.content || '暂无内容'"></div>
        </div>
      </div>

      <!-- 加载状态 -->
      <div v-else class="loading-container">
        <el-empty description="正在加载文章详情..." />
      </div>

      <template #footer>
        <div class="dialog-footer">
          <el-button @click="dialogVisible = false" :icon="Close">关闭</el-button>
          <el-button type="primary" @click="handleAuditArticle(currentArticle?.id)" :icon="Check" :disabled="!currentArticle || (currentArticle?.examineStatus || 0) === 1"> 审核通过 </el-button>
          <el-button type="warning" @click="handleRejectArticle(currentArticle?.id)" :icon="Close" :disabled="!currentArticle || (currentArticle?.examineStatus || 0) === 2"> 审核拒绝 </el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 修改文章对话框 -->
    <el-dialog v-model="editDialogVisible" title="修改文章" width="600px" class="edit-article-dialog" :close-on-click-modal="false" :close-on-press-escape="true" @close="handleEditDialogClose">
      <el-form ref="editFormRef" :model="editForm" :rules="editRules" label-width="100px" v-loading="editLoading">
        <el-form-item label="文章标题" prop="title">
          <el-input v-model="editForm.title" placeholder="请输入文章标题" maxlength="50" show-word-limit clearable />
        </el-form-item>

        <el-form-item label="文章描述" prop="description">
          <el-input v-model="editForm.description" type="textarea" :rows="3" placeholder="请输入文章描述" maxlength="255" show-word-limit clearable />
        </el-form-item>

        <el-form-item label="文章标签" prop="tag">
          <el-input v-model="editForm.tag" placeholder="请输入文章标签" clearable maxlength="255" show-word-limit />
        </el-form-item>

        <el-form-item label="文章类型" prop="reprintType">
          <el-radio-group v-model="editForm.reprintType">
            <el-radio :value="0">原创</el-radio>
            <el-radio :value="1">转载</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item
          v-if="editForm.reprintType === 1"
          label="转载链接"
          prop="reprintUrl"
          :rules="[
            { required: true, message: '请输入转载链接', trigger: 'blur' },
            { type: 'url', message: '请输入有效的URL地址', trigger: 'blur' },
          ]"
        >
          <el-input v-model="editForm.reprintUrl" placeholder="请输入原文链接" clearable />
        </el-form-item>

        <el-form-item label="可见范围" prop="visibleRange">
          <el-select v-model="editForm.visibleRange" placeholder="请选择可见范围" style="width: 100%">
            <el-option label="全部可见" :value="0" />
            <el-option label="仅我可见" :value="1" />
            <el-option label="粉丝可见" :value="2" />
            <el-option label="VIP可见" :value="3" />
          </el-select>
        </el-form-item>

        <el-form-item label="编辑状态" prop="editStatus">
          <el-select v-model="editForm.editStatus" placeholder="请选择编辑状态" style="width: 100%">
            <el-option label="已发布" :value="0" />
            <el-option label="草稿箱" :value="1" />
            <el-option label="回收站" :value="2" />
          </el-select>
        </el-form-item>
      </el-form>

      <template #footer>
        <div class="dialog-footer">
          <el-button @click="editDialogVisible = false" :icon="Close">取消</el-button>
          <el-button type="primary" @click="handleUpdateArticle" :loading="editLoading" :icon="Check"> 确认修改 </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, computed } from 'vue'
import { Delete, Close, Check, View, Calendar, Picture, User, Document, Star, ChatDotRound, Collection, Clock, Refresh, Search, ArrowLeft, Top, Edit } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getUserListWithArticleCount } from '@/api/user'
import {
  adminGetArticlesByUserId,
  adminDeleteArticle,
  adminDeleteBatchArticle,
  adminExamineArticle,
  adminExamineBatchArticle,
  adminSearchArticle,
  adminGetArticle,
  adminUpdateArticle,
} from '@/api/article'
import ManagementCard from '@/components/management/ManagementCard.vue'
import DataTable from '@/components/data/DataTable.vue'
import MobileCardList from '@/components/data/MobileCardList.vue'
import BatchActions from '@/components/actions/BatchActions.vue'
import SearchButtons from '@/components/search/SearchButtons.vue'
import ExamineStatusSelect from '@/components/search/ExamineStatusSelect.vue'
import KeywordSearch from '@/components/search/KeywordSearch.vue'
import StatusBadge from '@/components/common/StatusBadge.vue'

// 视图状态
const showArticles = ref(false)
const currentUser = ref(null)

// 用户列表数据
const userList = ref([])
const userLoading = ref(false)
const searchUserKeyword = ref('')

// 过滤后的用户列表
const filteredUserList = computed(() => {
  if (!searchUserKeyword.value) return userList.value
  const keyword = searchUserKeyword.value.toLowerCase()
  return userList.value.filter((user) => user.username.toLowerCase().includes(keyword) || user.nickname?.toLowerCase().includes(keyword))
})

// 审核状态映射
const examineStatusMap = {
  0: { text: '待审核', type: 'danger' },
  1: { text: '已审核', type: 'success' },
  2: { text: '未通过', type: 'warning' },
}

// 文章列表数据
const articleList = ref([])
const paginatedArticleList = ref([])
const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const dialogVisible = ref(false)
const dialogTitle = ref('文章详情')
const currentArticle = ref(null)
const detailLoading = ref(false)

// 搜索条件
const searchExamineStatus = ref('')
const searchKeyword = ref('')
const searchCreateTimeStart = ref(null)
const searchCreateTimeEnd = ref(null)

// 选中的文章
const selectedArticles = ref([])

// 批量操作加载状态
const batchAuditLoading = ref(false)
const batchRejectLoading = ref(false)
const batchDeleteLoading = ref(false)

// 修改文章相关状态
const editDialogVisible = ref(false)
const editLoading = ref(false)
const editFormRef = ref(null)
const editForm = ref({
  id: null,
  title: '',
  description: '',
  tag: '',
  reprintType: 0,
  reprintUrl: '',
  visibleRange: 0,
  editStatus: 0,
})

// 表单验证规则
const editRules = {
  title: [
    { required: true, message: '请输入文章标题', trigger: 'blur' },
    { min: 1, max: 50, message: '标题长度应在1-50个字符之间', trigger: 'blur' },
  ],
  description: [{ max: 255, message: '描述长度不能超过255个字符', trigger: 'blur' }],
  tag: [{ max: 255, message: '标签长度不能超过255个字符', trigger: 'blur' }],
}

// 获取用户列表
const getUsers = async () => {
  userLoading.value = true
  try {
    const res = await getUserListWithArticleCount()
    userList.value = res.data
  } catch (error) {
    ElMessage.error('获取用户列表失败')
  } finally {
    userLoading.value = false
  }
}

// 处理用户搜索
const handleUserSearch = () => {
  // 搜索逻辑已在computed中处理
}

// 查看用户文章
const handleViewUserArticles = async (user) => {
  currentUser.value = user
  showArticles.value = true
  currentPage.value = 1
  await fetchUserArticlesData()
}

// 返回用户列表
const handleBackToUsers = () => {
  showArticles.value = false
  currentUser.value = null
  articleList.value = []
  paginatedArticleList.value = []
  currentPage.value = 1
  total.value = 0
  // 重置搜索条件
  searchExamineStatus.value = ''
  searchKeyword.value = ''
  searchCreateTimeStart.value = null
  searchCreateTimeEnd.value = null
  selectedArticles.value = []
}

// 时间筛选变化
const handleTimeChange = ({ startTime, endTime }) => {
  searchCreateTimeStart.value = startTime
  searchCreateTimeEnd.value = endTime
}

const hasSearchConditions = () => !!(searchExamineStatus.value || searchKeyword.value || searchCreateTimeStart.value || searchCreateTimeEnd.value)

const buildSearchPayload = () => ({
  pageNum: currentPage.value,
  pageSize: pageSize.value,
  userId: currentUser.value?.id,
  examineStatus: searchExamineStatus.value ? parseInt(searchExamineStatus.value, 10) : undefined,
  keyword: searchKeyword.value || undefined,
  createTimeStart: searchCreateTimeStart.value || undefined,
  createTimeEnd: searchCreateTimeEnd.value || undefined,
})

const applyPageData = (pageData) => {
  articleList.value = pageData?.data || []
  paginatedArticleList.value = articleList.value
  total.value = Number(pageData?.total || 0)
  selectedArticles.value = []
}

const fetchUserArticlesData = async () => {
  if (!currentUser.value) return
  loading.value = true
  try {
    let pageData = null
    if (hasSearchConditions()) {
      const res = await adminSearchArticle(buildSearchPayload())
      pageData = res.data
    } else {
      const res = await adminGetArticlesByUserId(currentUser.value.id, {
        pageNum: currentPage.value,
        pageSize: pageSize.value,
      })
      pageData = res.data
    }
    applyPageData(pageData)
  } catch (error) {
    ElMessage.error(hasSearchConditions() ? '搜索文章失败' : '获取用户文章列表失败')
  } finally {
    loading.value = false
  }
}

// 处理搜索
const handleSearch = () => {
  currentPage.value = 1
  fetchUserArticlesData()
}

// 重置处理
const handleReset = () => {
  searchExamineStatus.value = ''
  searchKeyword.value = ''
  searchCreateTimeStart.value = null
  searchCreateTimeEnd.value = null
  handleSearch()
}

// 智能刷新列表
const refreshArticleList = async (deletedCount = 0) => {
  if (!currentUser.value) return
  if (deletedCount > 0 && currentPage.value > 1 && articleList.value.length <= deletedCount) {
    currentPage.value -= 1
  }
  await fetchUserArticlesData()
}

// 表格多选
const handleSelectionChange = (articles) => {
  selectedArticles.value = articles
}

// 检查文章是否被选中
const isArticleSelected = (articleId) => {
  return selectedArticles.value.some((article) => article.id === articleId)
}

// 移动端选择处理
const handleMobileSelect = (article) => {
  const index = selectedArticles.value.findIndex((item) => item.id === article.id)
  if (index > -1) {
    selectedArticles.value.splice(index, 1)
  } else {
    selectedArticles.value.push(article)
  }
}

// 对话框关闭处理
const handleDialogClose = () => {
  currentArticle.value = null
  detailLoading.value = false
}

// 查看文章详情
const handleViewArticle = async (articleId) => {
  try {
    detailLoading.value = true
    currentArticle.value = null
    dialogTitle.value = '文章详情'

    const res = await adminGetArticle(articleId)

    if (res && res.data) {
      currentArticle.value = res.data
      dialogVisible.value = true
    } else {
      throw new Error('文章数据为空或格式错误')
    }
  } catch (error) {
    ElMessage.error('获取文章详情失败: ' + (error.message || '未知错误'))
    dialogVisible.value = false
  } finally {
    detailLoading.value = false
  }
}

// 处理单个文章审核
const handleAuditArticle = async (articleId) => {
  try {
    await adminExamineArticle({ articleId: articleId, examineStatus: 1 })
    ElMessage.success('审核成功')
    await refreshArticleList()
    if (dialogVisible.value) {
      dialogVisible.value = false
    }
  } catch (error) {
    ElMessage.error('审核失败')
  }
}

// 处理批量审核
const handleBatchAudit = () => {
  ElMessageBox.confirm(`确定要审核通过选中的 ${selectedArticles.value.length} 篇文章吗？`, '确认', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'info',
  })
    .then(async () => {
      batchAuditLoading.value = true
      try {
        const data = selectedArticles.value.map((article) => ({
          articleId: article.id,
          examineStatus: 1,
        }))
        await adminExamineBatchArticle(data)
        ElMessage.success('批量审核成功')
        await refreshArticleList()
      } catch (error) {
        ElMessage.error('批量审核失败')
      } finally {
        batchAuditLoading.value = false
      }
    })
    .catch(() => {
      ElMessage.info('审核已取消')
    })
}

// 处理单个文章拒绝
const handleRejectArticle = async (articleId) => {
  try {
    await adminExamineArticle({ articleId: articleId, examineStatus: 2 })
    ElMessage.success('拒绝成功')
    await refreshArticleList()
    if (dialogVisible.value) {
      dialogVisible.value = false
    }
  } catch (error) {
    ElMessage.error('拒绝失败')
  }
}

// 处理批量拒绝
const handleBatchReject = () => {
  ElMessageBox.confirm(`确定要拒绝选中的 ${selectedArticles.value.length} 篇文章吗？`, '确认', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  })
    .then(async () => {
      batchRejectLoading.value = true
      try {
        const data = selectedArticles.value.map((article) => ({
          articleId: article.id,
          examineStatus: 2,
        }))
        await adminExamineBatchArticle(data)
        ElMessage.success('批量拒绝成功')
        await refreshArticleList()
      } catch (error) {
        ElMessage.error('批量拒绝失败')
      } finally {
        batchRejectLoading.value = false
      }
    })
    .catch(() => {
      ElMessage.info('拒绝已取消')
    })
}

// 处理删除单个文章
const handleDeleteArticle = (articleId) => {
  ElMessageBox.confirm('确定要删除该文章吗？', '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  })
    .then(async () => {
      try {
        await adminDeleteArticle(articleId)
        ElMessage.success('删除成功')
        await refreshArticleList()
        if (dialogVisible.value) {
          dialogVisible.value = false
        }
      } catch (error) {
        ElMessage.error('删除失败')
      }
    })
    .catch(() => {
      ElMessage.info('删除已取消')
    })
}

// 处理批量删除
const handleBatchDelete = () => {
  ElMessageBox.confirm(`确定要删除选中的 ${selectedArticles.value.length} 篇文章吗？`, '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  })
    .then(async () => {
      batchDeleteLoading.value = true
      try {
        const articleIds = selectedArticles.value.map((article) => article.id)
        await adminDeleteBatchArticle(articleIds)
        ElMessage.success('批量删除成功')
        await refreshArticleList()
      } catch (error) {
        ElMessage.error('批量删除失败')
      } finally {
        batchDeleteLoading.value = false
      }
    })
    .catch(() => {
      ElMessage.info('删除已取消')
    })
}

// 获取可见范围文本
const getVisibleRangeText = (visibleRange) => {
  const texts = { 0: '全部可见', 1: '仅我可见', 2: '粉丝可见', 3: 'VIP可见' }
  return texts[visibleRange] || '未知'
}

// 获取可见范围标签类型
const getVisibleRangeType = (visibleRange) => {
  const types = { 0: 'success', 1: 'info', 2: 'warning', 3: 'danger' }
  return types[visibleRange] || 'info'
}

// 获取编辑状态文本
const getEditStatusText = (editStatus) => {
  const texts = { 0: '已发布', 1: '草稿箱', 2: '回收站' }
  return texts[editStatus] || '未知'
}

// 获取编辑状态标签类型
const getEditStatusType = (editStatus) => {
  const types = { 0: 'success', 1: 'warning', 2: 'danger' }
  return types[editStatus] || 'info'
}

// 处理修改文章
const handleEditArticle = async (article) => {
  try {
    editForm.value = {
      id: article.id,
      title: article.title || '',
      description: article.description || '',
      tag: article.tag || '',
      reprintType: article.reprintType || 0,
      reprintUrl: article.reprintUrl || '',
      visibleRange: article.visibleRange || 0,
      editStatus: article.editStatus || 0,
    }
    editDialogVisible.value = true
  } catch (error) {
    console.error('修改文章出错:', error)
    ElMessage.error('修改文章失败')
  }
}

// 修改对话框关闭处理
const handleEditDialogClose = () => {
  if (editFormRef.value) {
    editFormRef.value.resetFields()
  }
  editForm.value = {
    id: null,
    title: '',
    description: '',
    tag: '',
    reprintType: 0,
    reprintUrl: '',
    visibleRange: 0,
    editStatus: 0,
  }
}

// 处理更新文章
const handleUpdateArticle = async () => {
  if (!editFormRef.value) return

  try {
    const valid = await editFormRef.value.validate()
    if (!valid) return

    editLoading.value = true

    const updateData = {
      id: editForm.value.id,
      title: editForm.value.title,
      description: editForm.value.description,
      tag: editForm.value.tag,
      reprintType: editForm.value.reprintType,
      reprintUrl: editForm.value.reprintUrl,
      visibleRange: editForm.value.visibleRange,
      editStatus: editForm.value.editStatus,
    }

    await adminUpdateArticle(updateData)
    ElMessage.success('修改文章成功')
    editDialogVisible.value = false
    await refreshArticleList()
  } catch (error) {
    ElMessage.error('修改文章失败：' + (error.message || '未知错误'))
  } finally {
    editLoading.value = false
  }
}

// 初始化
onMounted(() => {
  getUsers()
})
</script>

<style lang="scss" scoped>
// 用户列表视图 - 保留原有样式
.management-container {
  height: 100%;
  box-sizing: border-box;
  position: relative;

  .card {
    height: 100%;
    padding: 20px;
    overflow: hidden;
    display: flex;
    flex-direction: column;
    border-radius: 12px;
    box-shadow: 0 4px 20px var(--shadow-card);
    transition: all 0.3s ease;

    &:hover {
      box-shadow: 0 8px 30px var(--shadow-hover);
    }

    .card-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: 10px 10px 0 10px;

      .card-title {
        font-size: 20px;
        font-weight: 600;
        margin: 0;
        display: flex;
        align-items: center;

        &::before {
          content: '';
          display: inline-block;
          width: 4px;
          height: 20px;
          background-color: var(--admin-primary);
          border-radius: 2px;
          margin-right: 10px;
        }
      }

      .card-actions {
        display: flex;
        align-items: center;
        gap: 10px;

        .search-input {
          width: 240px;
          border-radius: 8px;
        }
      }
    }
  }

  // 用户列表
  .user-list-container {
    flex: 1;
    margin-top: 16px;
    overflow-y: auto;

    .empty-container {
      padding: 60px 20px;
      text-align: center;
    }

    .user-cards {
      display: grid;
      grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
      gap: 16px;
      padding: 16px;

      :deep(.el-card__body) {
        padding: 0;
      }

      .user-card {
        transition: all 0.3s ease;
        border-radius: 12px;

        &:hover {
          transform: translateY(-4px);
          box-shadow: 0 8px 25px var(--shadow-hover);
        }

        .user-card-content {
          display: flex;
          gap: 16px;
          padding: 16px;
          min-height: 80px;

          .user-avatar {
            flex-shrink: 0;
            align-self: center;
          }

          .user-right-content {
            flex: 1;
            display: flex;
            flex-direction: column;
            justify-content: space-between;
            min-height: 60px;

            .user-info {
              .user-id {
                font-size: 12px;
                color: var(--text-muted);
                margin-bottom: 4px;
              }

              .user-name {
                font-size: 16px;
                font-weight: 500;
                color: var(--text-regular);
                word-break: break-all;
                line-height: 1.4;
                margin-bottom: 6px;

                .username {
                  color: var(--admin-info);
                  font-weight: 600;
                  display: block;
                  margin-bottom: 2px;
                }

                .nickname {
                  color: var(--text-muted);
                  font-weight: 400;
                  font-size: 14px;
                  display: block;
                }
              }

              .article-count {
                display: flex;
                align-items: center;
                gap: 4px;
                font-size: 13px;

                .article-count-label {
                  color: var(--text-regular);
                  font-weight: 500;
                }

                .article-count-value {
                  background: var(--admin-warning);
                  color: white;
                  padding: 2px 8px;
                  border-radius: 12px;
                  font-weight: 600;
                  font-size: 12px;
                  min-width: 20px;
                  text-align: center;
                }
              }
            }

            .user-actions {
              align-self: center;
              margin-top: 8px;
              width: 100%;
              display: flex;
              justify-content: flex-start;

              .view-articles-btn {
                border-radius: 8px;
                font-weight: 500;
                transition: all 0.3s ease;
                white-space: nowrap;

                &:hover {
                  transform: scale(1.05);
                  box-shadow: 0 4px 12px var(--shadow-hover);
                }
              }
            }
          }
        }
      }
    }
  }
}

// 文章标题样式
.article-title {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  cursor: pointer;

  &:hover {
    color: var(--el-color-primary);
  }
}

.no-tag {
  color: var(--text-muted);
  font-size: 12px;
}

// 移动端元信息
.mobile-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 8px;
  margin: 4px 0;
}

.mobile-stats {
  display: flex;
  gap: 12px;
  font-size: 12px;
  color: var(--text-muted);

  span {
    display: flex;
    align-items: center;
    gap: 2px;
  }
}

.mobile-time {
  font-size: 12px;
  color: var(--text-muted);
}

// 文章详情对话框
:deep(.article-detail-dialog) {
  border-radius: 16px;

  .el-dialog__header {
    background: var(--admin-primary);
    color: white;
    border-radius: 16px 16px 0 0;
    padding: 20px 24px;

    .el-dialog__title {
      font-size: 18px;
      font-weight: 600;
    }
  }

  .el-dialog__body {
    padding: 24px;
    max-height: 80vh;
    overflow-y: auto;
  }
}

.article-detail {
  .article-info-section {
    margin-bottom: 24px;

    .article-detail-header {
      display: flex;
      gap: 24px;
      align-items: flex-start;

      .article-detail-info {
        flex: 1;

        .article-title-section {
          display: flex;
          align-items: center;
          gap: 12px;
          margin-bottom: 16px;

          .article-title-detail {
            margin: 0;
            font-size: 24px;
            font-weight: 700;
            color: var(--text-primary);
            line-height: 1.3;
            flex: 1;
          }

          .article-id-detail {
            background: var(--admin-primary);
            color: white;
            padding: 4px 12px;
            border-radius: 20px;
            font-size: 12px;
            font-weight: 600;
            white-space: nowrap;
          }
        }

        .article-author-section {
          display: flex;
          align-items: center;
          gap: 8px;
          margin-bottom: 12px;
          padding: 8px 12px;
          background-color: var(--el-border-color-light);
          border-radius: 8px;

          .author-icon {
            color: var(--el-text-color-regular);
            font-size: 16px;
          }

          .author-name-detail {
            font-weight: 600;
            color: var(--el-text-color-regular);
          }
        }

        .article-description-detail {
          display: flex;
          align-items: flex-start;
          gap: 8px;
          font-size: 14px;
          color: var(--text-muted);
          line-height: 1.6;
          margin-bottom: 16px;
          padding: 12px;
          background-color: var(--admin-info);
          border-radius: 8px;
          border-left: 4px solid var(--admin-info);

          .desc-icon {
            color: var(--admin-info);
            font-size: 16px;
            margin-top: 2px;
            flex-shrink: 0;
          }

          span {
            flex: 1;
          }

          &.no-description {
            background-color: var(--bg-card);
            border-left-color: var(--border);

            .desc-icon {
              color: var(--border);
            }

            span {
              color: var(--text-muted);
              font-style: italic;
            }
          }
        }

        .article-badges-detail {
          display: flex;
          flex-direction: column;
          gap: 12px;
          margin-bottom: 20px;

          .badge-group {
            display: flex;
            align-items: center;
            gap: 8px;
            flex-wrap: wrap;

            .badge-label {
              font-size: 13px;
              font-weight: 600;
              color: var(--text-primary);
              min-width: 80px;
            }

            .no-data {
              font-size: 12px;
              color: var(--border);
              font-style: italic;
            }

            .el-tag {
              font-size: 11px;
              height: 24px;
              line-height: 22px;
            }

            .column-tag {
              margin-right: 4px;
              margin-bottom: 4px;
            }

            &.reprint-url-group {
              .reprint-url-link {
                color: var(--admin-info);
                text-decoration: none;
                display: flex;
                align-items: center;
                gap: 4px;
                max-width: 400px;
                overflow: hidden;
                text-overflow: ellipsis;
                white-space: nowrap;
                font-size: 13px;

                &:hover {
                  text-decoration: underline;
                }

                .external-link-icon {
                  font-size: 12px;
                  color: var(--admin-info);
                }
              }
            }
          }
        }
      }

      .article-cover-detail {
        flex-shrink: 0;
        width: 640px;
        height: 360px;
        display: flex;
        align-items: center;
        justify-content: center;

        .detail-cover-img {
          width: 100%;
          height: 100%;
          border-radius: 16px;
          cursor: pointer;
          transition: all 0.3s ease;
          box-shadow: 0 6px 20px var(--shadow-card);
          object-fit: cover;

          &:hover {
            transform: scale(1.05);
            box-shadow: 0 12px 32px var(--shadow-hover);
          }
        }

        .no-cover-detail {
          width: 100%;
          height: 100%;
          background: var(--bg-card);
          border: 2px dashed var(--border);
          border-radius: 16px;
          display: flex;
          flex-direction: column;
          align-items: center;
          justify-content: center;
          font-size: 16px;
          color: var(--text-muted);
          gap: 12px;

          .cover-icon {
            font-size: 32px;
            color: var(--border);
          }
        }
      }
    }

    .article-stats-detail {
      border-top: 1px solid var(--border);
      padding-top: 20px;
      margin-top: 20px;

      .stats-group {
        display: flex;
        flex-wrap: wrap;
        gap: 12px;
        justify-content: space-evenly;

        .stat-item {
          display: flex;
          align-items: center;
          gap: 8px;
          padding: 10px 16px;
          background: var(--bg-card);
          border-radius: 12px;
          border: 1px solid var(--border);
          transition: all 0.3s ease;
          flex: 1;
          min-width: 160px;
          max-width: 200px;

          &:hover {
            transform: translateY(-2px);
            box-shadow: 0 4px 12px var(--shadow-card);
          }

          .stat-icon {
            font-size: 16px;
            color: var(--text-muted);
          }

          .stat-label {
            font-size: 12px;
            color: var(--text-muted);
            font-weight: 500;
          }

          .stat-value {
            font-size: 14px;
            font-weight: 700;
            color: var(--text-primary);
            margin-left: auto;
          }

          &.time-stat-item {
            background: var(--admin-info);
            border-color: var(--admin-info);

            .stat-icon {
              color: var(--admin-primary);
            }

            .stat-label {
              color: var(--admin-primary);
              font-weight: 600;
            }

            .stat-value {
              color: var(--admin-primary);
              font-size: 12px;
              font-weight: 600;
            }
          }
        }
      }
    }
  }
}

.article-content-section {
  .content-header {
    margin-bottom: 16px;

    h3 {
      margin: 0;
      font-size: 18px;
      font-weight: 600;
      color: var(--text-primary);
      display: flex;
      align-items: center;
      gap: 8px;

      &::before {
        content: '';
        width: 4px;
        height: 20px;
        background: var(--admin-primary);
        border-radius: 2px;
      }
    }
  }

  .article-content {
    max-height: 69vh;
    overflow-y: auto;
    padding: 20px;
    background-color: var(--bg-card);
    border: 1px solid var(--border);
    border-radius: 12px;
    line-height: 1.8;
    font-size: 15px;
    color: var(--text-primary);

    :deep(img) {
      width: 100%;
      height: auto;
      border-radius: 8px;
      box-shadow: 0 2px 8px var(--shadow-card);
      margin: 8px 0;
    }

    :deep(pre) {
      background-color: var(--bg-page);
      padding: 16px;
      border-radius: 8px;
      overflow-x: auto;
      border-left: 4px solid var(--admin-primary);
      margin: 12px 0;
    }

    :deep(code) {
      background-color: var(--bg-page);
      padding: 2px 6px;
      border-radius: 4px;
      font-size: 14px;
      color: var(--admin-warning);
    }

    :deep(blockquote) {
      border-left: 4px solid var(--admin-primary);
      padding-left: 16px;
      margin: 16px 0;
      color: var(--text-muted);
      font-style: italic;
    }

    :deep(h1, h2, h3, h4, h5, h6) {
      margin: 20px 0 12px 0;
      color: var(--text-primary);
    }

    :deep(p) {
      margin: 12px 0;
    }

    :deep(ul, ol) {
      margin: 12px 0;
      padding-left: 24px;
    }
  }
}

.loading-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 200px;
}

.dialog-footer {
  display: flex;
  justify-content: center;
  gap: 120px;
  padding: 16px 0;

  @media screen and (max-width: 768px) {
    gap: 4px;
  }

  .el-button {
    margin-left: 0px;
    padding: 10px 20px;
    font-weight: 500;
    border-radius: 8px;
    transition: all 0.3s ease;

    &:hover {
      transform: translateY(-2px);
      box-shadow: 0 4px 12px var(--shadow-hover);
    }
  }
}

// 修改文章对话框
:deep(.edit-article-dialog) {
  border-radius: 16px;

  .el-dialog__header {
    background: var(--admin-success);
    color: white;
    border-radius: 16px 16px 0 0;
    padding: 20px 24px;

    .el-dialog__title {
      font-size: 18px;
      font-weight: 600;
    }
  }

  .el-dialog__body {
    padding: 24px;
  }
}

.edit-form {
  .el-form-item__label {
    font-weight: 600;
    color: var(--text-primary);
  }
}

// 响应式
@media screen and (max-width: 768px) {
  .management-container .card {
    padding: 2px;

    .card-header {
      padding: 6px;

      .card-title {
        font-size: 16px;
      }

      .card-actions .search-input {
        width: 140px;
      }
    }
  }

  .user-list-container .user-cards {
    grid-template-columns: 1fr;
    gap: 10px;
    padding: 10px;
    max-width: 400px;
    margin: 0 auto;
  }

  .article-detail {
    .article-info-section {
      .article-detail-header {
        flex-direction: column;
        gap: 16px;
      }

      .article-cover-detail {
        align-self: center;
        width: 100%;
        height: 150px;
      }
    }
  }
}
</style>

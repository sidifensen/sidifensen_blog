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
    <div v-else class="card">
      <div class="card-header">
        <h2 class="card-title">{{ currentUser?.nickname || currentUser?.username }}的文章</h2>
        <div class="card-actions">
          <el-button @click="handleBackToUsers" :icon="ArrowLeft" size="small">返回用户列表</el-button>
          <el-select v-model="searchExamineStatus" placeholder="审核状态" filterable clearable size="small" class="search-input" @change="handleSearch">
            <el-option label="待审核" value="0" />
            <el-option label="审核通过" value="1" />
            <el-option label="审核不通过" value="2" />
            <el-option label="全部" value="" />
          </el-select>
        </div>
      </div>

      <div class="card-second">
        <el-date-picker
          v-model="searchCreateTimeStart"
          type="datetime"
          format="YYYY-MM-DD HH:mm:ss"
          value-format="YYYY-MM-DD HH:mm:ss"
          placeholder="创建时间开始"
          :prefix-icon="Calendar"
          size="small"
          class="search-input"
          clearable
          @change="handleSearch" />
        <el-date-picker
          v-model="searchCreateTimeEnd"
          type="datetime"
          format="YYYY-MM-DD HH:mm:ss"
          value-format="YYYY-MM-DD HH:mm:ss"
          placeholder="创建时间结束"
          :prefix-icon="Calendar"
          size="small"
          class="search-input"
          clearable
          @change="handleSearch" />
      </div>

      <div class="card-third">
        <el-button type="primary" plain round @click="handleBatchAudit" :disabled="selectedArticles.length === 0" :loading="batchAuditLoading"> 批量审核 </el-button>
        <el-button type="warning" plain round @click="handleBatchReject" :disabled="selectedArticles.length === 0" :loading="batchRejectLoading"> 批量拒绝 </el-button>
        <el-button type="danger" plain round @click="handleBatchDelete" :disabled="selectedArticles.length === 0" :loading="batchDeleteLoading"> 批量删除 </el-button>
      </div>

      <!-- 桌面端表格视图 -->
      <div v-if="!isMobileView" class="desktop-view">
        <el-table v-loading="loading" :data="paginatedArticleList" class="table" @selection-change="handleSelectionChange" :row-style="{ height: 'auto' }" :cell-style="{ padding: '8px 0' }">
          <el-table-column type="selection" width="30" />
          <el-table-column prop="coverUrl" label="封面" width="120">
            <template #default="{ row }">
              <div class="article-cover-container">
                <el-image v-if="row.coverUrl" :src="row.coverUrl" class="article-cover" :preview-src-list="[row.coverUrl]" fit="cover" preview-teleported />
                <div v-else class="no-cover">暂无封面</div>
              </div>
            </template>
          </el-table-column>
          <el-table-column prop="id" label="ID" width="60" />
          <el-table-column prop="title" label="文章标题" min-width="180">
            <template #default="{ row }">
              <el-tooltip :content="row.title" placement="top-start">
                <div class="article-title">{{ row.title }}</div>
              </el-tooltip>
            </template>
          </el-table-column>
          <el-table-column prop="tag" label="标签" width="150">
            <template #default="{ row }">
              <el-tag v-if="row.tag" size="small" type="info" class="tag-wrap">
                {{ row.tag }}
              </el-tag>
              <span v-else class="no-tag">无标签</span>
            </template>
          </el-table-column>
          <el-table-column prop="reprintType" label="类型" width="60">
            <template #default="{ row }">
              <el-tag :type="row.reprintType === 0 ? 'success' : 'warning'" size="small">
                {{ row.reprintType === 0 ? "原创" : "转载" }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="visibleRange" label="可见范围" width="80">
            <template #default="{ row }">
              <el-tag :type="getVisibleRangeType(row.visibleRange)" size="small">
                {{ getVisibleRangeText(row.visibleRange) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="editStatus" label="编辑状态" width="80">
            <template #default="{ row }">
              <el-tag :type="getEditStatusType(row.editStatus)" size="small">
                {{ getEditStatusText(row.editStatus) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="examineStatus" label="审核状态" width="80">
            <template #default="{ row }">
              <div class="article-status" :class="row.examineStatus === 0 ? 'status-unaudited' : row.examineStatus === 1 ? 'status-audited' : 'status-rejected'">
                {{ row.examineStatus === 0 ? "待审核" : row.examineStatus === 1 ? "已审核" : "未通过" }}
              </div>
            </template>
          </el-table-column>
          <el-table-column prop="readCount" label="阅读量" width="80" />
          <el-table-column prop="likeCount" label="点赞量" width="80" />
          <el-table-column prop="commentCount" label="评论数" width="80" />
          <el-table-column prop="collectCount" label="收藏量" width="80" />
          <el-table-column prop="createTime" label="创建时间" sortable width="110" />
          <el-table-column prop="updateTime" label="更新时间" sortable width="110" />
          <el-table-column label="操作" width="390">
            <template #default="{ row }">
              <div class="table-actions">
                <el-button type="info" @click="handleViewArticle(row.id)" :icon="View" class="view-button" size="small">查看</el-button>
                <el-button type="success" @click="handleEditArticle(row)" :icon="Edit" class="edit-button" size="small">修改</el-button>
                <el-button type="primary" @click="handleAuditArticle(row.id)" :icon="Check" class="examine-button" size="small">审核</el-button>
                <el-button type="warning" @click="handleRejectArticle(row.id)" :icon="Close" class="reject-button" size="small">拒绝</el-button>
                <el-button type="danger" @click="handleDeleteArticle(row.id)" :icon="Delete" class="delete-button" size="small">删除</el-button>
              </div>
            </template>
          </el-table-column>
        </el-table>
      </div>

      <!-- 移动端卡片视图 -->
      <div v-else class="mobile-view">
        <div class="article-cards">
          <el-card v-for="article in paginatedArticleList" :key="article.id" class="article-card">
            <div class="article-card-content">
              <div class="article-header-section">
                <div class="article-cover-mobile">
                  <el-image v-if="article.coverUrl" :src="article.coverUrl" class="article-cover-img" :preview-src-list="[article.coverUrl]" fit="cover" preview-teleported />
                  <div v-else class="no-cover-mobile">暂无封面</div>
                </div>
                <div class="article-info">
                  <div class="article-header">
                    <div class="article-id">#{{ article.id }}</div>
                    <div class="article-status" :class="article.examineStatus === 0 ? 'status-unaudited' : article.examineStatus === 1 ? 'status-audited' : 'status-rejected'">
                      {{ article.examineStatus === 0 ? "待审核" : article.examineStatus === 1 ? "已审核" : "未通过" }}
                    </div>
                  </div>
                  <div class="article-title-mobile">{{ article.title }}</div>
                  <div class="article-description-mobile" v-if="article.description">{{ article.description }}</div>
                  <div class="article-badges-mobile">
                    <el-tag v-if="article.tag" size="small" type="info">{{ article.tag }}</el-tag>
                  </div>

                  <!-- 状态标签单独一行 -->
                  <div class="article-status-row">
                    <el-tag :type="article.reprintType === 0 ? 'success' : 'warning'" size="small">
                      {{ article.reprintType === 0 ? "原创" : "转载" }}
                    </el-tag>
                    <el-tag :type="getVisibleRangeType(article.visibleRange)" size="small">
                      {{ getVisibleRangeText(article.visibleRange) }}
                    </el-tag>
                    <el-tag :type="getEditStatusType(article.editStatus)" size="small">
                      {{ getEditStatusText(article.editStatus) }}
                    </el-tag>
                  </div>
                  <div class="article-meta">
                    <!-- 统计数据一行显示 -->
                    <div class="stats-row">
                      <div class="meta-item stat-item">
                        <span class="label">阅读:</span>
                        <span>{{ article.readCount || 0 }}</span>
                      </div>
                      <div class="meta-item stat-item">
                        <span class="label">点赞:</span>
                        <span>{{ article.likeCount || 0 }}</span>
                      </div>
                      <div class="meta-item stat-item">
                        <span class="label">评论:</span>
                        <span>{{ article.commentCount || 0 }}</span>
                      </div>
                      <div class="meta-item stat-item">
                        <span class="label">收藏:</span>
                        <span>{{ article.collectCount || 0 }}</span>
                      </div>
                    </div>

                    <!-- 时间信息 -->
                    <div class="time-row">
                      <div class="meta-item time-item">
                        <span class="label">创建:</span>
                        <span>{{ article.createTime }}</span>
                      </div>
                      <div class="meta-item time-item">
                        <span class="label">更新:</span>
                        <span>{{ article.updateTime }}</span>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
              <div class="article-actions">
                <el-button type="info" @click="handleViewArticle(article.id)" :icon="View" class="view-button" size="small">查看</el-button>
                <el-button type="success" @click="handleEditArticle(article)" :icon="Edit" class="edit-button" size="small">修改</el-button>
                <el-button type="primary" @click="handleAuditArticle(article.id)" :icon="Check" class="examine-button" size="small">审核</el-button>
                <el-button type="warning" @click="handleRejectArticle(article.id)" :icon="Close" class="reject-button" size="small">拒绝</el-button>
                <el-button type="danger" @click="handleDeleteArticle(article.id)" :icon="Delete" class="delete-button" size="small">删除</el-button>
              </div>
            </div>
          </el-card>
        </div>
      </div>

      <!-- 分页 -->
      <div class="pagination-container">
        <el-pagination v-model:current-page="currentPage" v-model:page-size="pageSize" :page-sizes="[10, 20, 50, 100]" layout="prev, pager, next, jumper" :total="total" @size-change="handleSizeChange" @current-change="handleCurrentChange" />
      </div>
    </div>

    <!-- 文章详情对话框 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="90%" class="article-detail-dialog" :close-on-click-modal="false" :close-on-press-escape="true" draggable align-center @close="handleDialogClose">
      <div v-if="currentArticle" class="article-detail" v-loading="detailLoading">
        <!-- 文章基本信息 -->
        <div class="article-info-section">
          <div class="article-detail-header">
            <!-- 左侧：文章信息 -->
            <div class="article-detail-info">
              <div class="article-title-section">
                <h2 class="article-title-detail">{{ currentArticle?.title || "无标题" }}</h2>
                <div class="article-id-detail">#{{ currentArticle?.id || "N/A" }}</div>
              </div>

              <div class="article-author-section">
                <el-icon class="author-icon"><User /></el-icon>
                <span class="author-name-detail">{{ currentUser?.nickname || currentUser?.username || "未知作者" }}</span>
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
                    <el-tag v-for="column in currentArticle.columns" :key="column.id" type="primary" size="small" class="column-tag">
                      {{ column.name }}
                    </el-tag>
                  </template>
                  <span v-else class="no-data">无专栏</span>
                </div>

                <div class="badge-group">
                  <span class="badge-label">文章状态:</span>
                  <el-tag :type="(currentArticle?.reprintType || 0) === 0 ? 'success' : 'warning'" size="small">
                    {{ (currentArticle?.reprintType || 0) === 0 ? "原创" : "转载" }}
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
                  <div class="article-status" :class="(currentArticle?.examineStatus || 0) === 0 ? 'status-unaudited' : (currentArticle?.examineStatus || 0) === 1 ? 'status-audited' : 'status-rejected'">
                    {{ (currentArticle?.examineStatus || 0) === 0 ? "待审核" : (currentArticle?.examineStatus || 0) === 1 ? "已审核" : "未通过" }}
                  </div>
                </div>
              </div>
            </div>

            <!-- 右侧：文章封面 -->
            <div class="article-cover-detail">
              <el-image v-if="currentArticle && currentArticle.coverUrl" :src="currentArticle.coverUrl" class="detail-cover-img" :preview-src-list="[currentArticle.coverUrl]" fit="cover" preview-teleported />
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
                <el-icon class="stat-icon"><Star /></el-icon>
                <span class="stat-label">点赞</span>
                <span class="stat-value">{{ currentArticle?.likeCount || 0 }}</span>
              </div>
              <div class="stat-item">
                <el-icon class="stat-icon"><ChatDotRound /></el-icon>
                <span class="stat-label">评论</span>
                <span class="stat-value">{{ currentArticle?.commentCount || 0 }}</span>
              </div>
              <div class="stat-item">
                <el-icon class="stat-icon"><Collection /></el-icon>
                <span class="stat-label">收藏</span>
                <span class="stat-value">{{ currentArticle?.collectCount || 0 }}</span>
              </div>
              <div class="stat-item time-stat-item">
                <el-icon class="stat-icon"><Clock /></el-icon>
                <span class="stat-label">创建:</span>
                <span class="stat-value">{{ currentArticle?.createTime || "未知" }}</span>
              </div>
              <div class="stat-item time-stat-item">
                <el-icon class="stat-icon"><Refresh /></el-icon>
                <span class="stat-label">更新:</span>
                <span class="stat-value">{{ currentArticle?.updateTime || "未知" }}</span>
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

        <!-- 转载链接输入框，仅在选择转载时显示 -->
        <el-form-item
          v-if="editForm.reprintType === 1"
          label="转载链接"
          prop="reprintUrl"
          :rules="[
            { required: true, message: '请输入转载链接', trigger: 'blur' },
            { type: 'url', message: '请输入有效的URL地址', trigger: 'blur' },
          ]">
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
import { ref, onMounted, onUnmounted, computed } from "vue";
import { Delete, Close, Check, View, Calendar, Picture, User, Document, Star, ChatDotRound, Collection, Clock, Refresh, Search, ArrowLeft, Top, Edit } from "@element-plus/icons-vue";
import { ElMessage, ElMessageBox } from "element-plus";
import { getUserList, getUserListWithArticleCount } from "@/api/user";
import { adminGetArticlesByUserId, adminDeleteArticle, adminDeleteBatchArticle, adminExamineArticle, adminExamineBatchArticle, adminSearchArticle, adminGetArticle, adminUpdateArticle } from "@/api/article";

// 视图状态
const showArticles = ref(false);
const currentUser = ref(null);

// 用户列表数据
const userList = ref([]);
const userLoading = ref(false);
const searchUserKeyword = ref("");

// 过滤后的用户列表
const filteredUserList = computed(() => {
  if (!searchUserKeyword.value) return userList.value;
  const keyword = searchUserKeyword.value.toLowerCase();
  return userList.value.filter((user) => user.username.toLowerCase().includes(keyword) || user.nickname?.toLowerCase().includes(keyword));
});

// 文章列表数据
const articleList = ref([]);
const paginatedArticleList = ref([]);
const loading = ref(false);
const currentPage = ref(1);
const pageSize = ref(10);
const total = ref(0);
const dialogVisible = ref(false);
const dialogTitle = ref("文章详情");
const currentArticle = ref(null);
const detailLoading = ref(false);

// 搜索条件
const searchExamineStatus = ref("");
const searchCreateTimeStart = ref(null);
const searchCreateTimeEnd = ref(null);

// 选中的文章
const selectedArticles = ref([]);

// 批量操作加载状态
const batchAuditLoading = ref(false);
const batchRejectLoading = ref(false);
const batchDeleteLoading = ref(false);

// 修改文章相关状态
const editDialogVisible = ref(false);
const editLoading = ref(false);
const editFormRef = ref(null);
const editForm = ref({
  id: null,
  title: "",
  description: "",
  tag: "",
  reprintType: 0,
  reprintUrl: "",
  visibleRange: 0,
  editStatus: 0,
});

// 表单验证规则
const editRules = {
  title: [
    { required: true, message: "请输入文章标题", trigger: "blur" },
    { min: 1, max: 50, message: "标题长度应在1-50个字符之间", trigger: "blur" },
  ],
  description: [{ max: 255, message: "描述长度不能超过255个字符", trigger: "blur" }],
  tag: [{ max: 255, message: "标签长度不能超过255个字符", trigger: "blur" }],
};

// 移动端检测
const isMobileView = ref(false);

// 监听窗口大小变化
const handleResize = () => {
  isMobileView.value = window.innerWidth <= 768;
};

// 获取用户列表
const getUsers = async () => {
  userLoading.value = true;
  try {
    const res = await getUserListWithArticleCount();
    userList.value = res.data.data;
  } catch (error) {
    ElMessage.error("获取用户列表失败");
  } finally {
    userLoading.value = false;
  }
};

// 处理用户搜索
const handleUserSearch = () => {
  // 搜索逻辑已在computed中处理
};

// 查看用户文章
const handleViewUserArticles = async (user) => {
  currentUser.value = user;
  showArticles.value = true;
  await getUserArticles(user.id);
};

// 返回用户列表
const handleBackToUsers = () => {
  showArticles.value = false;
  currentUser.value = null;
  articleList.value = [];
  paginatedArticleList.value = [];
  // 重置搜索条件
  searchExamineStatus.value = "";
  searchCreateTimeStart.value = null;
  searchCreateTimeEnd.value = null;
  selectedArticles.value = [];
};

// 获取用户文章列表
const getUserArticles = async (userId) => {
  loading.value = true;
  try {
    const res = await adminGetArticlesByUserId(userId);
    articleList.value = res.data.data.sort((a, b) => b.id - a.id);
    total.value = articleList.value.length;
    currentPage.value = 1;
    updatePaginatedArticleList();
  } catch (error) {
    ElMessage.error("获取用户文章列表失败");
  } finally {
    loading.value = false;
  }
};

// 更新分页数据
const updatePaginatedArticleList = () => {
  const startIndex = (currentPage.value - 1) * pageSize.value;
  const endIndex = startIndex + pageSize.value;
  paginatedArticleList.value = articleList.value.slice(startIndex, endIndex);
};

// 处理分页大小变化
const handleSizeChange = (size) => {
  pageSize.value = size;
  currentPage.value = 1;
  updatePaginatedArticleList();
};

// 处理当前页码变化
const handleCurrentChange = (current) => {
  currentPage.value = current;
  updatePaginatedArticleList();
};

// 处理搜索
const handleSearch = async () => {
  if (!currentUser.value) return;

  loading.value = true;
  try {
    const res = await adminSearchArticle({
      userId: currentUser.value.id,
      examineStatus: searchExamineStatus.value,
      createTimeStart: searchCreateTimeStart.value,
      createTimeEnd: searchCreateTimeEnd.value,
    });
    articleList.value = res.data.data;
    total.value = articleList.value.length;
    currentPage.value = 1;
    updatePaginatedArticleList();
  } catch (error) {
    ElMessage.error("搜索文章失败");
  } finally {
    loading.value = false;
  }
};

// 智能刷新列表
const refreshArticleList = async () => {
  if (!currentUser.value) return;

  // 检查是否有任何搜索条件
  const hasSearchConditions = searchExamineStatus.value || searchCreateTimeStart.value || searchCreateTimeEnd.value;

  if (hasSearchConditions) {
    await handleSearch();
  } else {
    await getUserArticles(currentUser.value.id);
  }
};

// 表格多选
const handleSelectionChange = (articles) => {
  selectedArticles.value = articles;
};

// 对话框关闭处理
const handleDialogClose = () => {
  currentArticle.value = null;
  detailLoading.value = false;
};

// 查看文章详情
const handleViewArticle = async (articleId) => {
  try {
    detailLoading.value = true;
    currentArticle.value = null;
    dialogTitle.value = "文章详情";

    const res = await adminGetArticle(articleId);

    if (res && res.data && res.data.data) {
      currentArticle.value = res.data.data;
      dialogVisible.value = true;
    } else {
      throw new Error("文章数据为空或格式错误");
    }
  } catch (error) {
    ElMessage.error("获取文章详情失败: " + (error.message || "未知错误"));
    dialogVisible.value = false;
  } finally {
    detailLoading.value = false;
  }
};

// 处理单个文章审核
const handleAuditArticle = async (articleId) => {
  try {
    await adminExamineArticle({ articleId: articleId, examineStatus: 1 });
    ElMessage.success("审核成功");
    await refreshArticleList();
    if (dialogVisible.value) {
      dialogVisible.value = false;
    }
  } catch (error) {
    ElMessage.error("审核失败");
  }
};

// 处理批量审核
const handleBatchAudit = () => {
  ElMessageBox.confirm(`确定要审核通过选中的 ${selectedArticles.value.length} 篇文章吗？`, "确认", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "info",
  })
    .then(async () => {
      batchAuditLoading.value = true;
      try {
        const data = selectedArticles.value.map((article) => ({
          articleId: article.id,
          examineStatus: 1,
        }));
        await adminExamineBatchArticle(data);
        ElMessage.success("批量审核成功");
        await refreshArticleList();
      } catch (error) {
        ElMessage.error("批量审核失败");
      } finally {
        batchAuditLoading.value = false;
      }
    })
    .catch(() => {
      ElMessage.info("审核已取消");
    });
};

// 处理单个文章拒绝
const handleRejectArticle = async (articleId) => {
  try {
    await adminExamineArticle({ articleId: articleId, examineStatus: 2 });
    ElMessage.success("拒绝成功");
    await refreshArticleList();
    if (dialogVisible.value) {
      dialogVisible.value = false;
    }
  } catch (error) {
    ElMessage.error("拒绝失败");
  }
};

// 处理批量拒绝
const handleBatchReject = () => {
  ElMessageBox.confirm(`确定要拒绝选中的 ${selectedArticles.value.length} 篇文章吗？`, "确认", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning",
  })
    .then(async () => {
      batchRejectLoading.value = true;
      try {
        const data = selectedArticles.value.map((article) => ({
          articleId: article.id,
          examineStatus: 2,
        }));
        await adminExamineBatchArticle(data);
        ElMessage.success("批量拒绝成功");
        await refreshArticleList();
      } catch (error) {
        ElMessage.error("批量拒绝失败");
      } finally {
        batchRejectLoading.value = false;
      }
    })
    .catch(() => {
      ElMessage.info("拒绝已取消");
    });
};

// 处理删除单个文章
const handleDeleteArticle = (articleId) => {
  ElMessageBox.confirm("确定要删除该文章吗？", "警告", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning",
  })
    .then(async () => {
      try {
        await adminDeleteArticle(articleId);
        ElMessage.success("删除成功");
        await refreshArticleList();
        if (dialogVisible.value) {
          dialogVisible.value = false;
        }
      } catch (error) {
        ElMessage.error("删除失败");
      }
    })
    .catch(() => {
      ElMessage.info("删除已取消");
    });
};

// 处理批量删除
const handleBatchDelete = () => {
  ElMessageBox.confirm(`确定要删除选中的 ${selectedArticles.value.length} 篇文章吗？`, "警告", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning",
  })
    .then(async () => {
      batchDeleteLoading.value = true;
      try {
        const data = selectedArticles.value.map((article) => ({
          articleId: article.id,
        }));
        await adminDeleteBatchArticle(data);
        ElMessage.success("批量删除成功");
        await refreshArticleList();
      } catch (error) {
        ElMessage.error("批量删除失败");
      } finally {
        batchDeleteLoading.value = false;
      }
    })
    .catch(() => {
      ElMessage.info("删除已取消");
    });
};

// 获取可见范围文本
const getVisibleRangeText = (visibleRange) => {
  const texts = {
    0: "全部可见",
    1: "仅我可见",
    2: "粉丝可见",
    3: "VIP可见",
  };
  return texts[visibleRange] || "未知";
};

// 获取可见范围标签类型
const getVisibleRangeType = (visibleRange) => {
  const types = {
    0: "success",
    1: "info",
    2: "warning",
    3: "danger",
  };
  return types[visibleRange] || "info";
};

// 获取编辑状态文本
const getEditStatusText = (editStatus) => {
  const texts = {
    0: "已发布",
    1: "草稿箱",
    2: "回收站",
  };
  return texts[editStatus] || "未知";
};

// 获取编辑状态标签类型
const getEditStatusType = (editStatus) => {
  const types = {
    0: "success",
    1: "warning",
    2: "danger",
  };
  return types[editStatus] || "info";
};

// 处理修改文章
const handleEditArticle = async (article) => {
  try {
    editForm.value = {
      id: article.id,
      title: article.title || "",
      description: article.description || "",
      tag: article.tag || "",
      reprintType: article.reprintType || 0,
      reprintUrl: article.reprintUrl || "",
      visibleRange: article.visibleRange || 0,
      editStatus: article.editStatus || 0,
    };
    editDialogVisible.value = true;
  } catch (error) {
    console.error("修改文章出错:", error);
    ElMessage.error("修改文章失败");
  }
};

// 修改对话框关闭处理
const handleEditDialogClose = () => {
  if (editFormRef.value) {
    editFormRef.value.resetFields();
  }
  editForm.value = {
    id: null,
    title: "",
    description: "",
    tag: "",
    reprintType: 0,
    reprintUrl: "",
    visibleRange: 0,
    editStatus: 0,
  };
};

// 处理更新文章
const handleUpdateArticle = async () => {
  if (!editFormRef.value) return;

  try {
    // 表单验证
    const valid = await editFormRef.value.validate();
    if (!valid) return;

    editLoading.value = true;

    // 准备更新数据
    const updateData = {
      id: editForm.value.id,
      title: editForm.value.title,
      description: editForm.value.description,
      tag: editForm.value.tag,
      reprintType: editForm.value.reprintType,
      reprintUrl: editForm.value.reprintUrl,
      visibleRange: editForm.value.visibleRange,
      editStatus: editForm.value.editStatus,
    };

    await adminUpdateArticle(updateData);
    ElMessage.success("修改文章成功");
    editDialogVisible.value = false;

    // 刷新列表
    await refreshArticleList();
  } catch (error) {
    ElMessage.error("修改文章失败：" + (error.message || "未知错误"));
  } finally {
    editLoading.value = false;
  }
};

// 初始化
onMounted(() => {
  getUsers();
  handleResize();
  window.addEventListener("resize", handleResize);
});

// 组件卸载时移除监听
onUnmounted(() => {
  window.removeEventListener("resize", handleResize);
});
</script>

<style lang="scss" scoped>
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
    overflow: hidden;
    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
    transition: all 0.3s ease;

    &:hover {
      box-shadow: 0 8px 30px rgba(0, 0, 0, 0.12);
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
          content: "";
          display: inline-block;
          width: 4px;
          height: 20px;
          background-color: #42b983;
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

          :deep(.el-input__wrapper) {
            border-radius: 8px;
            transition: all 0.3s ease;

            &:focus-within {
              box-shadow: 0 0 0 3px rgba(66, 185, 131, 0.2);
              border-color: #42b983;
            }
          }

          :deep(.el-select__wrapper) {
            border-radius: 8px;
            transition: all 0.3s ease;

            &:focus-within {
              box-shadow: 0 0 0 3px rgba(66, 185, 131, 0.2);
              border-color: #42b983;
            }
          }
        }
      }
    }

    .card-second {
      display: flex;
      justify-content: flex-end;
      padding: 5px 5px 0 5px;
      gap: 10px;

      :deep(.el-input__wrapper) {
        border-radius: 8px;
        &:focus-within {
          box-shadow: 0 0 0 3px rgba(66, 185, 131, 0.2);
          border-color: #42b983;
        }
      }
    }

    .card-third {
      display: flex;
      justify-content: flex-end;
      padding: 10px;
      border-bottom: 1px solid var(--el-border-color);
    }
  }

  // 用户列表容器
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
          box-shadow: 0 8px 25px rgba(0, 0, 0, 0.12);
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
                color: #909399;
                margin-bottom: 4px;
              }

              .user-name {
                font-size: 16px;
                font-weight: 500;
                color: #303133;
                word-break: break-all;
                line-height: 1.4;
                margin-bottom: 6px;

                .username {
                  color: #409eff;
                  font-weight: 600;
                  display: block;
                  margin-bottom: 2px;
                }

                .nickname {
                  color: #909399;
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
                  color: #606266;
                  font-weight: 500;
                }

                .article-count-value {
                  background: linear-gradient(45deg, #42b983, #36a970);
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
                  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.3);
                }
              }
            }
          }
        }
      }
    }
  }

  // 桌面端表格视图
  .desktop-view {
    flex: 1;
    display: flex;
    flex-direction: column;
    padding-bottom: 60px; // 为分页容器预留空间

    .table {
      flex: 1;
      display: flex;
      flex-direction: column;
      margin-top: 16px;
      max-height: calc(100vh - 280px); // 增加高度预留，为分页容器留空间

      :deep(.tag-wrap) {
        white-space: normal; // 保持文本换行
        word-break: break-word;
        line-height: 1.4;
        max-width: 130px;
        display: inline-block;
        height: auto;
        min-height: 20px;
      }

      :deep(.el-table__header-wrapper) {
        background-color: var(--el-bg-color);
        th {
          font-weight: 600;
          color: #475569;
        }
      }

      :deep(.el-table__body-wrapper) {
        tr td {
          color: #64748b;
          padding: 12px 0;
          vertical-align: middle;

          .cell {
            display: flex;
            align-items: center;
            justify-content: flex-start;
            min-height: 40px;
          }
        }
      }

      .article-title {
        max-width: 200px;
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
        cursor: pointer;

        &:hover {
          color: #42b983;
        }
      }

      .no-tag {
        color: #999;
        font-size: 12px;
      }

      .article-cover-container {
        display: flex;
        justify-content: center;
        align-items: center;
        height: 100%;

        .article-cover {
          width: 100px;
          height: 60px;
          border-radius: 6px;
          cursor: pointer;
          transition: all 0.3s ease;

          &:hover {
            transform: scale(1.05);
            box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
          }
        }

        .no-cover {
          width: 100px;
          height: 60px;
          background-color: #f5f5f5;
          border: 1px dashed #ddd;
          border-radius: 6px;
          display: flex;
          align-items: center;
          justify-content: center;
          font-size: 12px;
          color: #999;
        }
      }

      .table-actions {
        display: flex;
        align-items: center;
        justify-content: center;
        flex-wrap: wrap;
        height: 100%;
        min-height: 60px;

        // 通用按钮样式
        .view-button,
        .edit-button,
        .examine-button,
        .reject-button,
        .delete-button {
          border-radius: 6px;
          transition: all 0.3s ease;

          &:hover {
            transform: translateY(-2px);
          }
        }

        .view-button {
          background-color: #f0f9ff;
          color: #0369a1;
          border-color: #f0f9ff;

          &:hover {
            background-color: #dbeafe;
            border-color: #dbeafe;
            box-shadow: 0 2px 8px rgba(3, 105, 161, 0.3);
          }
        }

        .edit-button {
          background-color: #f0fdf4;
          color: #15803d;
          border-color: #f0fdf4;

          &:hover {
            background-color: #dcfce7;
            border-color: #dcfce7;
            box-shadow: 0 2px 8px rgba(21, 128, 61, 0.3);
          }
        }

        .examine-button {
          background-color: #e0f2fe;
          color: #0284c7;
          border-color: #e0f2fe;

          &:hover {
            background-color: #bae6fd;
            border-color: #bae6fd;
            box-shadow: 0 2px 8px rgba(2, 132, 199, 0.3);
          }
        }

        .reject-button {
          background-color: #fef3c7;
          color: #d97706;
          border-color: #fef3c7;

          &:hover {
            background-color: #fde68a;
            border-color: #fde68a;
            box-shadow: 0 2px 8px rgba(217, 119, 6, 0.3);
          }
        }

        .delete-button {
          background-color: #fee2e2;
          color: #ef4444;
          border-color: #fee2e2;

          &:hover {
            background-color: #fecaca;
            border-color: #fecaca;
            box-shadow: 0 2px 8px rgba(239, 68, 68, 0.3);
          }
        }
      }
    }
  }

  // 移动端卡片视图
  .mobile-view {
    flex: 1;
    display: flex;
    flex-direction: column;
    margin-top: 16px;
    padding-bottom: 60px; // 为分页容器预留空间
    overflow-y: auto;

    .article-cards {
      display: flex;
      flex-direction: column;
      gap: 12px;
      padding: 10px;

      .article-card {
        transition: all 0.3s ease;
        border-radius: 8px;
        margin-bottom: 12px;

        &:hover {
          transform: translateY(-2px);
          box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
        }

        .article-card-content {
          display: flex;
          flex-direction: column;
          gap: 12px;

          .article-header-section {
            display: flex;
            flex-direction: column;
            gap: 12px;

            .article-cover-mobile {
              width: 100%;
              margin-bottom: 8px;

              .article-cover-img {
                width: 100%;
                height: 120px;
                border-radius: 6px;
                cursor: pointer;
                transition: all 0.3s ease;
                object-fit: cover;

                &:hover {
                  transform: scale(1.02);
                  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
                }
              }

              .no-cover-mobile {
                width: 100%;
                height: 120px;
                background-color: #f5f5f5;
                border: 1px dashed #ddd;
                border-radius: 6px;
                display: flex;
                align-items: center;
                justify-content: center;
                font-size: 12px;
                color: #999;
                text-align: center;
              }
            }

            .article-info {
              width: 100%;
              display: flex;
              flex-direction: column;
              gap: 8px;

              .article-header {
                display: flex;
                justify-content: space-between;
                align-items: center;
                flex-wrap: wrap;
                gap: 8px;

                .article-id {
                  font-size: 12px;
                  color: #666;
                  background-color: #f5f5f5;
                  padding: 2px 6px;
                  border-radius: 4px;
                }
              }

              .article-title-mobile {
                font-size: 16px;
                font-weight: 500;
                color: #333;
                line-height: 1.4;
                margin-bottom: 6px;
              }

              .article-description-mobile {
                font-size: 12px;
                color: #666;
                line-height: 1.4;
                margin-bottom: 8px;
                display: -webkit-box;
                -webkit-line-clamp: 2;
                line-clamp: 2;
                -webkit-box-orient: vertical;
                overflow: hidden;
              }

              .article-badges-mobile {
                display: flex;
                flex-wrap: wrap;
                gap: 4px;
                margin-bottom: 6px;

                .el-tag {
                  font-size: 10px;
                  height: 20px;
                  line-height: 18px;
                }
              }

              // 状态标签单独一行
              .article-status-row {
                display: flex;
                flex-wrap: wrap;
                gap: 6px;
                margin-bottom: 8px;
                justify-content: flex-start;

                .el-tag {
                  font-size: 10px;
                  height: 22px;
                  line-height: 20px;
                  padding: 0 8px;
                  font-weight: 500;
                }
              }

              .article-meta {
                display: flex;
                flex-direction: column;
                gap: 8px;
                margin-top: 8px;

                .meta-item {
                  font-size: 12px;
                  color: #666;
                  display: flex;
                  align-items: center;

                  .label {
                    font-weight: 500;
                    margin-right: 4px;
                  }
                }

                .stats-row {
                  display: flex;
                  flex-wrap: wrap;
                  gap: 6px 8px;

                  .stat-item {
                    background-color: #f8f9fa;
                    padding: 3px 8px;
                    border-radius: 12px;
                    font-size: 11px;
                    color: #555;
                    border: 1px solid #e9ecef;
                    white-space: nowrap;

                    .label {
                      color: #666;
                      margin-right: 2px;
                    }
                  }
                }

                .time-row {
                  display: flex;
                  flex-wrap: wrap;
                  gap: 8px;
                  margin-top: 4px;

                  .time-item {
                    font-size: 11px;
                    color: #888;

                    .label {
                      color: #999;
                    }
                  }
                }
              }
            }
          }

          .article-actions {
            display: flex;
            justify-content: center;
            padding-top: 8px;
            border-top: 1px solid #f0f0f0;

            .el-button {
              font-size: 12px;
              padding: 6px 10px;
              height: auto;
              border-radius: 4px;
              margin-left: 2px;
              flex: 1;
            }
          }
        }
      }
    }
  }

  // 通用的文章状态样式
  .article-status {
    display: inline-block;
    padding: 2px 8px;
    border-radius: 12px;
    font-size: 12px;
    font-weight: 500;

    &.status-unaudited {
      background-color: #fff1f0;
      color: #f56c6c;
    }

    &.status-audited {
      background-color: #f0f9eb;
      color: #67c23a;
    }

    &.status-rejected {
      background-color: #fdf6ec;
      color: #e6a23c;
    }
  }

  .pagination-container {
    display: flex;
    justify-content: flex-end;
    padding: 10px;
    background-color: var(--el-bg-color);
    border-radius: 0 0 12px 12px;
    z-index: 10;
    width: 100%;
    box-sizing: border-box;
    position: absolute;
    bottom: 0;
    left: 0;
  }
}

// 文章详情对话框样式
:deep(.article-detail-dialog) {
  border-radius: 16px;

  .el-dialog__header {
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    color: white;
    border-radius: 16px 16px 0 0;
    padding: 20px 24px;
    position: relative;

    .el-dialog__title {
      font-size: 18px;
      font-weight: 600;
    }

    .el-dialog__headerbtn {
      position: absolute;
      top: 50%;
      right: 20px;
      transform: translateY(-50%);
      width: 32px;
      height: 32px;
      border-radius: 50%;
      background-color: rgba(255, 255, 255, 0.1);
      display: flex;
      align-items: center;
      justify-content: center;
      transition: all 0.3s ease;

      &:hover {
        background-color: rgba(255, 255, 255, 0.2);
        transform: translateY(-50%) scale(1.1);
      }

      .el-dialog__close {
        color: white;
        font-size: 18px;
        font-weight: bold;

        &:hover {
          color: #ff6b6b;
        }
      }
    }
  }

  .el-dialog__body {
    padding: 24px;
    max-height: 80vh;
    overflow-y: auto;
  }

  @media screen and (max-width: 767px) {
    width: 95% !important;

    .el-dialog__body {
      padding: 16px;
    }
  }
}

.article-detail {
  // 文章基本信息区域
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
            color: #2c3e50;
            line-height: 1.3;
            flex: 1;
          }

          .article-id-detail {
            background: linear-gradient(45deg, #667eea, #764ba2);
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
          color: #6c757d;
          line-height: 1.6;
          margin-bottom: 16px;
          padding: 12px;
          background-color: #e3f2fd;
          border-radius: 8px;
          border-left: 4px solid #2196f3;

          .desc-icon {
            color: #2196f3;
            font-size: 16px;
            margin-top: 2px;
            flex-shrink: 0;
          }

          span {
            flex: 1;
          }

          &.no-description {
            background-color: #f8f9fa;
            border-left-color: #adb5bd;

            .desc-icon {
              color: #adb5bd;
            }

            span {
              color: #6c757d;
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
              color: #495057;
              min-width: 80px;
            }

            .no-data {
              font-size: 12px;
              color: #adb5bd;
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

            // 转载链接样式
            &.reprint-url-group {
              .reprint-url-link {
                color: #409eff;
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
                  color: #409eff;
                }
              }
            }
          }
        }
      }

      .article-cover-detail {
        flex-shrink: 0;
        width: 640px; //保持长宽比16:9
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
          box-shadow: 0 6px 20px rgba(0, 0, 0, 0.15);
          object-fit: cover;

          &:hover {
            transform: scale(1.05);
            box-shadow: 0 12px 32px rgba(0, 0, 0, 0.25);
          }
        }

        .no-cover-detail {
          width: 100%;
          height: 100%;
          background: linear-gradient(45deg, #f8f9fa, #e9ecef);
          border: 2px dashed #dee2e6;
          border-radius: 16px;
          display: flex;
          flex-direction: column;
          align-items: center;
          justify-content: center;
          font-size: 16px;
          color: #6c757d;
          gap: 12px;

          .cover-icon {
            font-size: 32px;
            color: #adb5bd;
          }
        }
      }
    }

    // 底部统计数据样式
    .article-stats-detail {
      border-top: 1px solid #e9ecef;
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
          background: linear-gradient(135deg, #f8f9fa, #e9ecef);
          border-radius: 12px;
          border: 1px solid #dee2e6;
          transition: all 0.3s ease;
          flex: 1;
          min-width: 160px;
          max-width: 200px;

          &:hover {
            transform: translateY(-2px);
            box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
          }

          .stat-icon {
            font-size: 16px;
            color: #6c757d;
          }

          .stat-label {
            font-size: 12px;
            color: #6c757d;
            font-weight: 500;
          }

          .stat-value {
            font-size: 14px;
            font-weight: 700;
            color: #495057;
            margin-left: auto;
          }

          &.time-stat-item {
            background: linear-gradient(135deg, #e3f2fd, #bbdefb);
            border-color: #90caf9;

            .stat-icon {
              color: #1976d2;
            }

            .stat-label {
              color: #1976d2;
              font-weight: 600;
            }

            .stat-value {
              color: #0d47a1;
              font-size: 12px;
              font-weight: 600;
            }
          }
        }
      }
    }
  }
}

// 文章内容区域
.article-content-section {
  .content-header {
    margin-bottom: 16px;

    h3 {
      margin: 0;
      font-size: 18px;
      font-weight: 600;
      color: #2c3e50;
      display: flex;
      align-items: center;
      gap: 8px;

      &::before {
        content: "";
        width: 4px;
        height: 20px;
        background: linear-gradient(135deg, #667eea, #764ba2);
        border-radius: 2px;
      }
    }
  }

  .article-content {
    max-height: 69vh;
    overflow-y: auto;
    padding: 20px;
    background-color: #ffffff;
    border: 1px solid #e9ecef;
    border-radius: 12px;
    line-height: 1.8;
    font-size: 15px;
    color: #333;

    :deep(img) {
      width: 100%;
      height: auto;
      border-radius: 8px;
      box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
      margin: 8px 0;
    }

    :deep(pre) {
      background-color: #f8f9fa;
      padding: 16px;
      border-radius: 8px;
      overflow-x: auto;
      border-left: 4px solid #007bff;
      margin: 12px 0;
    }

    :deep(code) {
      background-color: #f8f9fa;
      padding: 2px 6px;
      border-radius: 4px;
      font-size: 14px;
      color: #e83e8c;
    }

    :deep(blockquote) {
      border-left: 4px solid #007bff;
      padding-left: 16px;
      margin: 16px 0;
      color: #6c757d;
      font-style: italic;
    }

    :deep(h1, h2, h3, h4, h5, h6) {
      margin: 20px 0 12px 0;
      color: #2c3e50;
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
      box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
    }
  }
}

// 响应式设计
@media screen and (max-width: 1400px) {
  .management-container {
    .card {
      .card-header {
        .card-actions {
          .search-input {
            width: 180px;
          }
        }
      }
    }
  }
}

@media screen and (max-width: 1220px) {
  .management-container {
    .card {
      .card-header {
        flex-direction: column;
        align-items: flex-start;
        gap: 16px;

        .card-actions {
          width: 100%;
          justify-content: space-between;

          .search-input {
            width: 100%;
          }
        }
      }
    }

    // 中等屏幕用户卡片样式
    .user-list-container {
      .user-cards {
        grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
        gap: 14px;
        padding: 14px;
      }
    }
  }
}

@media screen and (max-width: 768px) {
  .management-container {
    .card {
      padding: 2px;

      .card-header {
        padding: 6px;

        .card-title {
          font-size: 16px;
        }

        .card-actions {
          .search-input {
            width: 100%;
          }
        }
      }

      .table {
        margin-top: 0;
        max-height: calc(100vh - 240px); // 为分页容器预留更多空间
      }

      .pagination-container {
        padding: 4px;

        :deep(.el-pagination) {
          .el-pager {
            display: none;
          }
        }
      }
    }

    // 移动端用户卡片样式
    .user-list-container {
      .user-cards {
        grid-template-columns: 1fr;
        gap: 10px;
        padding: 10px;
        max-width: 400px;
        margin: 0 auto;

        .user-card {
          :deep(.el-card__body) {
            padding: 0;
          }

          .user-card-content {
            flex-direction: column;
            align-items: center;
            text-align: center;
            gap: 12px;
            padding: 16px;

            .user-avatar {
              :deep(.el-avatar) {
                width: 64px !important;
                height: 64px !important;
                font-size: 32px;
              }
            }

            .user-right-content {
              width: 100%;
              align-items: center;

              .user-info {
                .user-id {
                  font-size: 12px;
                  margin-bottom: 6px;
                }

                .user-name {
                  font-size: 14px;
                  word-break: break-all;

                  .username {
                    display: block;
                    margin-bottom: 4px;
                    font-size: 16px;
                  }

                  .nickname {
                    display: block;
                    font-size: 14px;
                  }
                }

                .article-count {
                  justify-content: center;
                  margin-top: 8px;

                  .article-count-label {
                    font-size: 13px;
                  }

                  .article-count-value {
                    font-size: 12px;
                    padding: 2px 10px;
                  }
                }
              }

              .user-actions {
                width: 100%;
                margin-top: 12px;

                .view-articles-btn {
                  width: 100%;
                  font-size: 14px;
                }
              }
            }
          }
        }
      }
    }
  }

  .article-detail {
    .article-info-section {
      .article-detail-header {
        flex-direction: column;
        gap: 16px;

        .article-cover-detail {
          align-self: center;
          width: 100%;
          height: 150px;

          .detail-cover-img {
            width: 100%;
            height: 100%;
          }

          .no-cover-detail {
            width: 100%;
            height: 100%;
          }
        }
      }

      .article-stats-detail {
        .stats-group {
          flex-direction: column;

          .stat-item {
            min-width: auto;
            max-width: none;
          }
        }
      }
    }
  }
}

@media screen and (max-width: 480px) {
  .card-header {
    flex-direction: column;
    align-items: flex-start;

    .card-actions {
      width: 100%;
      justify-content: space-between;

      .search-input {
        width: 100%;
      }
    }
  }

  .table-actions {
    .el-button {
      font-size: 11px;
      padding: 4px 8px;
    }
  }
}

// 修改文章对话框样式
:deep(.edit-article-dialog) {
  border-radius: 16px;

  .el-dialog__header {
    background: linear-gradient(135deg, #10b981 0%, #059669 100%);
    color: white;
    border-radius: 16px 16px 0 0;
    padding: 20px 24px;

    .el-dialog__title {
      font-size: 18px;
      font-weight: 600;
    }

    .el-dialog__headerbtn {
      top: 50%;
      right: 20px;
      transform: translateY(-50%);
      width: 32px;
      height: 32px;
      border-radius: 50%;
      background-color: rgba(255, 255, 255, 0.1);
      display: flex;
      align-items: center;
      justify-content: center;
      transition: all 0.3s ease;

      &:hover {
        background-color: rgba(255, 255, 255, 0.2);
        transform: translateY(-50%) scale(1.1);
      }

      .el-dialog__close {
        color: white;
        font-size: 18px;
        font-weight: bold;

        &:hover {
          color: #ff6b6b;
        }
      }
    }
  }

  .el-dialog__body {
    padding: 24px;

    .el-form {
      .el-form-item {
        margin-bottom: 20px;

        .el-form-item__label {
          font-weight: 600;
          color: #374151;
        }

        .el-input,
        .el-select,
        .el-textarea {
          .el-input__wrapper,
          .el-select__wrapper,
          .el-textarea__inner {
            border-radius: 8px;
            transition: all 0.3s ease;

            &:focus-within {
              box-shadow: 0 0 0 3px rgba(16, 185, 129, 0.2);
              border-color: #10b981;
            }
          }
        }

        .el-radio-group {
          .el-radio {
            margin-right: 20px;

            .el-radio__input.is-checked + .el-radio__label {
              color: #10b981;
            }

            .el-radio__input.is-checked .el-radio__inner {
              background-color: #10b981;
              border-color: #10b981;
            }
          }
        }
      }
    }
  }

  @media screen and (max-width: 767px) {
    width: 95% !important;

    .el-dialog__body {
      padding: 16px;
    }
  }
}
</style>

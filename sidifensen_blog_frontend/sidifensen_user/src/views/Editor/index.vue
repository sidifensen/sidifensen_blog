<template>
  <div class="editor-container">
    <EditorHeader></EditorHeader>
    <div class="editor">
      <div ref="divRef" style="height: 600px" />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue';
import EditorHeader from '@/components/EditorHeader.vue';
import { AiEditor } from 'aieditor';
import 'aieditor/dist/style.css';

// 创建div引用
const divRef = ref(null);
// 定义编辑器实例变量
let aiEditor = null;

// 在组件挂载后初始化编辑器
onMounted(() => {
  if (divRef.value) {
    aiEditor = new AiEditor({
      element: divRef.value,
      placeholder: '点击输入内容...',
      content: 'AiEditor 是一个面向 AI 的开源富文本编辑器。 '
    });
  }
});

// 在组件卸载前销毁编辑器实例
onUnmounted(() => {
  aiEditor && aiEditor.destroy();
});
</script>

<style lang="scss" scoped>
.editor-container {
  height: 100vh;
  display: flex;
  flex-direction: column;

  /* 设置EditorHeader的高度为48px */
  :deep(.pc-menu) {
    height: 48px;
  }

  /* 确保编辑器内容显示在EditorHeader下方并占据剩余空间 */
  .editor {
    margin-top: 48px;
    flex: 1;
  }
}
</style>

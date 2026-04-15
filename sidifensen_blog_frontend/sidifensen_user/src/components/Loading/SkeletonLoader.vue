<template>
  <div v-if="loading" class="skeleton-list">
    <div v-for="i in displayCount" :key="i" class="skeleton-card">
      <div class="skeleton-image"></div>
      <div class="skeleton-body">
        <div class="skeleton-line title"></div>
        <div class="skeleton-line"></div>
        <div class="skeleton-line short"></div>
        <div class="skeleton-line date"></div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  loading: {
    type: Boolean,
    default: true,
  },
  count: {
    type: Number,
    default: 3,
  },
})

const displayCount = computed(() => Math.max(1, props.count))
</script>

<style lang="scss" scoped>
.skeleton-list {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 24px;

  @media (max-width: 992px) {
    grid-template-columns: repeat(2, 1fr);
  }

  @media (max-width: 768px) {
    grid-template-columns: 1fr;
  }
}

.skeleton-card {
  display: flex;
  flex-direction: column;
  background: var(--bg-card);
  border: 1px solid var(--border);
  border-radius: 12px;
  overflow: hidden;
}

.skeleton-image {
  width: 100%;
  height: 140px;
  background: var(--bg-subtle);
  animation: pulse 1.5s ease-in-out infinite;
}

.skeleton-body {
  padding: 16px;
}

.skeleton-line {
  height: 14px;
  background: var(--bg-subtle);
  border-radius: 4px;
  margin-bottom: 10px;
  animation: pulse 1.5s ease-in-out infinite;

  &.title {
    width: 80%;
    height: 18px;
    margin-bottom: 12px;
  }

  &.short {
    width: 60%;
    margin-bottom: 12px;
  }

  &.date {
    width: 40%;
    height: 12px;
    margin-bottom: 0;
    margin-top: 12px;
  }
}

@keyframes pulse {
  0%,
  100% {
    opacity: 1;
  }
  50% {
    opacity: 0.5;
  }
}
</style>

<script setup>
const props = defineProps({
  modelValue: {
    type: String,
    default: ''
  },
  placeholder: {
    type: String,
    default: ''
  },
  disabled: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['update:modelValue', 'click'])

const onInput = (e) => {
  emit('update:modelValue', e.detail.value)
}

const onClick = () => {
  if (!props.disabled) {
    emit('click')
  }
}
</script>

<template>
  <input
    class="custom-input"
    :value="modelValue"
    :placeholder="placeholder"
    :disabled="disabled"
    @input="onInput"
    @click="onClick"
  />
</template>

<style lang="scss" scoped>
.custom-input {
  width: 100%;
  height: 36px;
  padding: 0 12px;
  border: 1px solid var(--border, #e2e8f0);
  border-radius: 18px;
  background: var(--bg-card, #ffffff);
  font-size: 14px;
  color: var(--text-primary, #1e293b);

  &::placeholder {
    color: var(--text-muted, #94a3b8);
  }

  &:disabled {
    opacity: 0.6;
    background: var(--bg-page, #f8fafc);
  }
}
</style>

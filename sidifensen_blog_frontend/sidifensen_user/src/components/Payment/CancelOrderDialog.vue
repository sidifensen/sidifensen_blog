<template>
  <Teleport to="body">
    <div v-if="visible" class="cancel-order-dialog-mask" @click.self="handleClose">
      <div class="cancel-order-dialog">
        <button class="dialog-close" @click="handleClose">
          <svg viewBox="0 0 24 24" width="18" height="18">
            <path
              fill="currentColor"
              d="M19 6.41L17.59 5 12 10.59 6.41 5 5 6.41 10.59 12 5 17.59 6.41 19 12 13.41 17.59 19 19 17.59 13.41 12z"
            />
          </svg>
        </button>

        <div class="dialog-content">
          <div class="dialog-icon">
            <svg viewBox="0 0 24 24" width="32" height="32">
              <path
                fill="currentColor"
                d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm1 15h-2v-2h2v2zm0-4h-2V7h2v6z"
              />
            </svg>
          </div>

          <h3 class="dialog-title">{{ title }}</h3>
          <p class="dialog-message">{{ message }}</p>

          <div class="dialog-actions">
            <button class="btn-cancel" @click="handleClose">取消</button>
            <button class="btn-confirm" @click="handleConfirm" :disabled="confirming">
              {{ confirming ? '取消中...' : '确定' }}
            </button>
          </div>
        </div>
      </div>
    </div>
  </Teleport>
</template>

<script setup>
import { ref } from 'vue'
import { cancelVipOrder } from '@/api/vip'

const props = defineProps({
  visible: {
    type: Boolean,
    default: false,
  },
  orderNo: {
    type: String,
    required: true,
  },
  title: {
    type: String,
    default: '取消订单',
  },
  message: {
    type: String,
    default: '取消后订单将无法恢复，确定要取消吗？',
  },
})

const emit = defineEmits(['update:visible', 'confirmed'])

const confirming = ref(false)

const handleClose = () => {
  if (confirming.value) return
  emit('update:visible', false)
}

const handleConfirm = async () => {
  if (confirming.value) return

  try {
    confirming.value = true
    await cancelVipOrder(props.orderNo)
    emit('confirmed')
    emit('update:visible', false)
  } catch (error) {
    ElMessage.error(error?.msg || '取消订单失败')
  } finally {
    confirming.value = false
  }
}
</script>

<style lang="scss" scoped>
.cancel-order-dialog-mask {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.45);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 10000;
  animation: fadeIn 0.2s ease;

  .cancel-order-dialog {
    position: relative;
    width: 100%;
    max-width: 400px;
    margin: 20px;
    animation: slideIn 0.25s ease;

    .dialog-close {
      position: absolute;
      top: 14px;
      right: 14px;
      width: 28px;
      height: 28px;
      border: none;
      background: transparent;
      color: #94a3b8;
      cursor: pointer;
      border-radius: 50%;
      display: flex;
      align-items: center;
      justify-content: center;
      transition: all 0.2s;

      &:hover {
        background: #f1f5f9;
        color: #475569;
      }
    }

    .dialog-content {
      background: #ffffff;
      border-radius: 16px;
      padding: 32px 28px 24px;
      text-align: center;
      box-shadow: 0 20px 60px rgba(0, 0, 0, 0.15);

      .dialog-icon {
        width: 48px;
        height: 48px;
        margin: 0 auto 16px;
        border-radius: 50%;
        background: #fef3c7;
        color: #d97706;
        display: flex;
        align-items: center;
        justify-content: center;
      }

      .dialog-title {
        margin: 0 0 12px;
        font-size: 18px;
        font-weight: 600;
        color: #1e293b;
      }

      .dialog-message {
        margin: 0 0 28px;
        font-size: 14px;
        line-height: 1.6;
        color: #64748b;
      }

      .dialog-actions {
        display: flex;
        justify-content: center;
        gap: 12px;

        .btn-cancel,
        .btn-confirm {
          min-width: 88px;
          height: 40px;
          border-radius: 10px;
          font-size: 14px;
          font-weight: 500;
          cursor: pointer;
          transition: all 0.2s;
          border: none;

          &:disabled {
            opacity: 0.6;
            cursor: not-allowed;
          }
        }

        .btn-cancel {
          background: #f8fafc;
          color: #475569;
          border: 1px solid #e2e8f0;

          &:hover {
            background: #f1f5f9;
            border-color: #cbd5e1;
          }

          &:active {
            transform: scale(0.98);
          }
        }

        .btn-confirm {
          background: #ef4444;
          color: #ffffff;

          &:hover {
            background: #dc2626;
          }

          &:active {
            transform: scale(0.98);
          }
        }
      }
    }
  }
}

html.dark {
  .cancel-order-dialog-mask {
    .cancel-order-dialog {
      .dialog-close {
        &:hover {
          background: #334155;
          color: #cbd5e1;
        }
      }

      .dialog-content {
        background: #1e293b;

        .dialog-icon {
          background: rgba(254, 243, 199, 0.12);
          color: #fcd34d;
        }

        .dialog-title {
          color: #f1f5f9;
        }

        .dialog-message {
          color: #94a3b8;
        }

        .btn-cancel {
          background: #334155;
          border-color: #334155;
          color: #f1f5f9;

          &:hover {
            background: #475569;
            border-color: #475569;
          }
        }

        .btn-confirm {
          background: #ef4444;

          &:hover {
            background: #dc2626;
          }
        }
      }
    }
  }
}

@keyframes fadeIn {
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
}

@keyframes slideIn {
  from {
    opacity: 0;
    transform: translateY(12px) scale(0.98);
  }
  to {
    opacity: 1;
    transform: translateY(0) scale(1);
  }
}
</style>

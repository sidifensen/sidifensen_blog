import { useDarkStore } from "@/stores/darkStore";
import { storeToRefs } from "pinia";

/**
 * 获取图表主题颜色
 */
export function useChartTheme() {
  const darkStore = useDarkStore();
  const { isDark } = storeToRefs(darkStore);

  return {
    isDark: isDark.value,

    // 图表调色板
    palette: isDark.value
      ? ["#34d399", "#fbbf24", "#94a3b8", "#f87171"]
      : ["#10b981", "#f59e0b", "#64748b", "#ef4444"],

    // 蓝色调色板（用于折线图等）
    bluePalette: isDark.value
      ? ["#60a5fa", "#34d399", "#fbbf24"]
      : ["#3b82f6", "#10b981", "#f59e0b"],

    // 双色调色板
    dualPalette: isDark.value
      ? ["#6ee7b7", "#475569"]
      : ["#7c3aed", "#94a3b8"],

    // 渐变调色板
    gradientPalette: ["#4facfe", "#43e97b"],

    // 文字颜色
    textColor: isDark.value ? "#ffffff" : "#64748b",

    // 主要文字颜色
    textPrimaryColor: isDark.value ? "#e2e8f0" : "#1e293b",

    // 次要文字颜色
    textMutedColor: isDark.value ? "#8ea0b7" : "#64748b",

    // 坐标轴线颜色
    axisLineColor: isDark.value ? "#ffffff" : "#e2e8f0",

    // 网格线颜色
    gridLineColor: isDark.value ? "#ffffff" : "#edf2f7",

    // 描边/边框颜色
    strokeColor: isDark.value ? "#1a1a1a" : "#ffffff",

    // 图表区域填充开始颜色
    areaStartColor: isDark.value
      ? "rgba(148, 163, 184, 0.18)"
      : "rgba(71, 85, 105, 0.14)",

    // 图表区域填充结束颜色
    areaEndColor: isDark.value
      ? "rgba(148, 163, 184, 0.03)"
      : "rgba(71, 85, 105, 0.02)",

    // 图表区域填充（渐变）
    areaFill: isDark.value
      ? "l(270) 0:rgba(96, 165, 250, 0.18) 1:rgba(96, 165, 250, 0.03)"
      : "l(270) 0:rgba(59, 130, 246, 0.18) 1:rgba(59, 130, 246, 0.03)",

    // VIP 统计颜色
    vipPalette: isDark.value
      ? ["#6ee7b7", "#475569"]
      : ["#7c3aed", "#94a3b8"],

    // 用户分布图表颜色
    userDistributionPalette: isDark.value
      ? ["#6ee7b7", "#f59e0b", "#ef4444"]
      : ["#3b82f6", "#f59e0b", "#ef4444"],

    // 雷达图/极坐标图深色模式专用颜色
    radarDarkAxisLine: "#22314a",
    radarDarkGridLine: "#1c2940",
    radarDarkStroke: "#22314a",

    // G2 主题配置
    g2Theme: isDark.value ? { type: "classicDark" } : { type: "light" },

    // 辅助色
    amber: "#f59e0b",
    red: "#ef4444",
    blue: "#60a5fa",
  };
}


import { ElMessage } from 'element-plus';
// 引入disable-devtool库
import DisableDevtool from 'disable-devtool';

// 定义检测器类型常量
const DetectorType = {
  Unknown: -1,
  RegToString: 0,    // 根据正则检测
  DefineId: 1,       // 根据dom id检测
  Size: 2,           // 根据窗口尺寸检测
  DateToString: 3,   // 根据Date.toString 检测
  FuncToString: 4,   // 根据Function.toString 检测
  Debugger: 5,       // 根据断点检测，仅在ios chrome 真机情况下有效
  Performance: 6,    // 根据log大数据性能检测
  DebugLib: 7        // 检测第三方调试工具 eruda 和 vconsole
};

/**
 * 配置并初始化disable-devtool
 * 用于禁用浏览器开发者工具，保护前端代码
 */
export function setupDisableDevtool() {
  DisableDevtool({
    md5: '8ee665ec42804db3fd0bee4793f1266f',                     // 绕过禁用的md5值，留空表示不启用绕过禁用
    url: 'about:blank',            // 关闭页面失败时的跳转页面
    timeOutUrl: 'about:blank',  // 关闭页面超时跳转到警告页面
    tkName: 'youyu',              // 绕过禁用时的url参数名称
    ondevtoolopen: (type, next) => {
      // 开发者面板打开的回调
      // console.log('开发者工具被打开，检测类型:', type);
      // 使用setTimeout确保消息有足够时间显示
      // 使用闭包确保只显示一次提示
      if (!window.devtoolWarned) {
        ElMessage.error('请勿使用开发者工具!');
        window.devtoolWarned = true;
        // 增加延迟时间确保消息有足够时间显示
        setTimeout(() => {
          // 2秒后刷新页面
          setTimeout(() => {
            window.location.reload();
            // 重置警告标志，以便下次打开时再次提示
            window.devtoolWarned = false;
          }, 2000);
          // 尝试关闭窗口(放在刷新后执行，确保消息显示)
          next();
        }, 1000);
      }
    },
    ondevtoolclose: () => {
      // 开发者面板关闭的回调
      // ElMessage.error('开发者工具被关闭！');
      console.log('开发者工具被关闭');
    },
    interval: 100,               // 增加检测频率，提高灵敏度
    disableMenu: true,           // 禁用右键菜单
    stopIntervalTime: 3000,      // 在移动端时取消监视的等待时长，单位ms
    clearIntervalWhenDevOpenTrigger: true,   // 触发后清除定时器，避免重复刷新
    resetOnReload: true,                      // 刷新页面后重置检测状态
    detectors: [                 // 启用的检测器
      DetectorType.RegToString,
      DetectorType.DefineId,
      DetectorType.Size,
      DetectorType.DateToString,
      DetectorType.FuncToString,
      DetectorType.Debugger,
      DetectorType.Performance,
      DetectorType.DebugLib
    ],
    // clearLog: true,              // 每次都清除log
    disableSelect: true,         // 禁用选择文本
    disableCopy: true,           // 禁用复制
    disableCut: true,            // 禁用剪切
    disablePaste: false,         // 不禁用粘贴
    ignore: () => {
      // 开发环境下忽略禁用
      return import.meta.env.DEV;
    },
    disableIframeParents: true, // iframe中禁用所有父窗口
    disableMenuBar: true,       // 禁用浏览器菜单栏中的开发者工具选项
    disableDebuggerKeyword: true, // 禁用debugger关键字
    detectByStack: true,         // 通过调用栈检测开发者工具
    disableF12: true,           // 禁用F12快捷键
    disableCtrlShiftI: true,    // 禁用Ctrl+Shift+I快捷键
  });
}

export default setupDisableDevtool;
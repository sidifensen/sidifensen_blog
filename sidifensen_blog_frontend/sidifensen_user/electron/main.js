import { app, BrowserWindow, globalShortcut, Notification, ipcMain, Tray, Menu, nativeImage } from 'electron';
import path from 'path';
import { fileURLToPath } from 'url';
import { dirname } from 'path';
import electronUpdater from 'electron-updater';

// 获取 __dirname 的 ES 模块等价物
const __filename = fileURLToPath(import.meta.url);
const __dirname = dirname(__filename);

// 配置自动更新
const { autoUpdater } = electronUpdater;
autoUpdater.autoDownload = false;
autoUpdater.autoInstallOnAppQuit = true;

let mainWindow = null;
let tray = null;

// 创建主窗口
function createWindow() {
  mainWindow = new BrowserWindow({
    width: 1200,
    height: 800,
    minWidth: 800,
    minHeight: 600,
    show: false,
    backgroundColor: '#f8fafc',
    webPreferences: {
      preload: path.join(__dirname, 'preload.cjs'),
      contextIsolation: true,
      nodeIntegration: false,
    },
  });

  // 根据环境加载不同 URL
  if (process.env.NODE_ENV === 'development' || process.env.VITE_DEV_SERVER_URL) {
    mainWindow.loadURL(process.env.VITE_DEV_SERVER_URL || 'http://localhost:7000');
    mainWindow.webContents.openDevTools();
  } else {
    // 打包后 asar 结构: app.asar/electron/main.js, app.asar/dist/index.html
    // 使用 app.getAppPath() 获取应用根目录，避免 __dirname 在打包后路径不正确
    const indexPath = path.join(app.getAppPath(), 'dist', 'index.html');
    mainWindow.loadFile(indexPath);
  }

  // 窗口准备好后显示
  mainWindow.once('ready-to-show', () => {
    mainWindow.show();
  });

  // 关闭时最小化到托盘
  mainWindow.on('close', (event) => {
    if (!app.isQuiting) {
      event.preventDefault();
      mainWindow.hide();
    }
  });
}

// 创建系统托盘
function createTray() {
  // 尝试加载图标
  const iconPaths = [
    path.join(__dirname, '../public/icons/256x256.png'),
    path.join(__dirname, '../../public/icons/256x256.png'),
  ];
  let icon = nativeImage.createEmpty();
  for (const iconPath of iconPaths) {
    icon = nativeImage.createFromPath(iconPath);
    if (!icon.isEmpty()) break;
  }
  if (icon.isEmpty()) {
    console.log('Tray icon not found, using empty icon');
  }
  tray = new Tray(icon);

  const contextMenu = Menu.buildFromTemplate([
    { label: '显示', click: () => mainWindow.show() },
    { type: 'separator' },
    { label: '退出', click: () => {
      app.isQuiting = true;
      app.quit();
    }},
  ]);

  tray.setToolTip('Sidifensen Blog');
  tray.setContextMenu(contextMenu);

  tray.on('click', () => {
    mainWindow.show();
  });
}

// 注册全局快捷键
function registerShortcuts() {
  // Ctrl+Q 退出应用
  globalShortcut.register('CommandOrControl+Q', () => {
    app.isQuiting = true;
    app.quit();
  });

  // Ctrl+W 关闭窗口（最小化到托盘）
  globalShortcut.register('CommandOrControl+W', () => {
    mainWindow.close();
  });
}

// 配置自动更新功能
function setupAutoUpdater() {
  autoUpdater.on('update-available', (info) => {
    if (Notification.isSupported()) {
      new Notification({
        title: '发现新版本',
        body: `版本 ${info.version} 可用，是否下载？`,
      }).show();
    }
    autoUpdater.downloadUpdate();
  });

  autoUpdater.on('update-downloaded', () => {
    if (Notification.isSupported()) {
      new Notification({
        title: '下载完成',
        body: '新版本已下载，重启应用即可更新',
      }).show();
    }
  });

  // 检查更新（仅在非开发模式）
  if (!app.isPackaged) {
    autoUpdater.checkForUpdates().catch(console.error);
  }
}

// 显示原生通知
ipcMain.handle('show-notification', async (event, { title, body }) => {
  if (Notification.isSupported()) {
    new Notification({ title, body }).show();
  }
});

// 获取应用版本
ipcMain.handle('get-app-version', () => app.getVersion());

app.whenReady().then(() => {
  createWindow();
  createTray();
  registerShortcuts();
  setupAutoUpdater();

  app.on('activate', () => {
    if (BrowserWindow.getAllWindows().length === 0) {
      createWindow();
    } else {
      mainWindow.show();
    }
  });
});

app.on('window-all-closed', () => {
  if (process.platform !== 'darwin') {
    app.quit();
  }
});

app.on('will-quit', () => {
  globalShortcut.unregisterAll();
});

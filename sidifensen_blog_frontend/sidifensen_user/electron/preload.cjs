const { contextBridge, ipcRenderer } = require('electron');

contextBridge.exposeInMainWorld('electronAPI', {
  showNotification: (options) => ipcRenderer.invoke('show-notification', options),
  getAppVersion: () => ipcRenderer.invoke('get-app-version'),
  platform: process.platform,
});

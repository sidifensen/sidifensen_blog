# sidifensen_blog_frontend

This template should help get you started developing with Vue 3 in Vite.

## Recommended IDE Setup

[VSCode](https://code.visualstudio.com/) + [Volar](https://marketplace.visualstudio.com/items?itemName=Vue.volar) (and disable Vetur).

## Customize configuration

See [Vite Configuration Reference](https://vite.dev/config/).

## Project Setup

```sh
npm install
```

### Compile and Hot-Reload for Development

```sh
npm run dev
```

### Compile and Minify for Production

```sh
npm run build
```

## Electron 桌面应用

### 开发模式

```sh
npm run electron:dev
```

### 打包 Windows exe

```sh
npm run electron:build
```

打包产物位于 `dist-electron/` 目录。

### 打包全平台（Windows/macOS/Linux）

```sh
npm run electron:build:all
```

## Capacitor 原生应用（Android）

### 首次添加 Android 项目

```sh
npx cap add android
```

### 构建并同步

```sh
npm run build:capacitor
npx cap sync android
```

### 打开 Android Studio

```sh
npx cap open android
```

### 在 Android Studio 中构建 APK

```sh
# 进入 android 目录
cd android

# Debug 版本（开发测试用）
./gradlew assembleDebug

# Release 版本（正式发布用）
./gradlew assembleRelease
```

### APK 输出位置

| 版本 | 路径 |
|------|------|
| Debug | `android/app/build/outputs/apk/debug/app-debug.apk` |
| Release | `android/app/build/outputs/apk/release/app-release.apk` |

### 删除 Android 项目

```sh
rm -rf android/
```

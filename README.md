# OneWord

[![CI](https://github.com/YxmCute/OneWord/actions/workflows/ci.yml/badge.svg)](https://github.com/YxmCute/OneWord/actions/workflows/ci.yml)

一个基于 Kotlin Multiplatform + Compose Multiplatform 的三端诗词应用，目标平台为 Android、iOS、macOS。

首页从今日诗词接口获取一句古诗词，支持本地缓存、动态主题取色、桌面端打包，以及 iOS 模拟器容器运行。

## 技术栈

- Kotlin Multiplatform
- Compose Multiplatform
- Ktor Client
- SQLDelight
- Kotlinx Serialization
- XcodeGen

## 当前能力

- Android / iOS / macOS 三端共用业务层与大部分 UI
- 首页展示单句诗词、作者、题目、朝代
- 手动刷新、全文展开、离线缓存回显
- 主题模式切换与自定义主色取色板
- macOS DMG 打包
- iOS Simulator 容器工程与一键重置运行脚本

## 工程结构

- `composeApp/`
  - KMP 共享模块
  - Android 入口
  - macOS Desktop 入口
  - 共享 UI、主题、网络、数据库、状态管理
- `iosApp/`
  - iOS 容器工程定义
  - `project.yml` 通过 XcodeGen 生成 `iosApp.xcodeproj`
- `scripts/`
  - iOS/Xcode 工具脚本

## 环境要求

- JDK 17+
- Android SDK
- Xcode 26+
- iOS Simulator runtime
- macOS（用于 iOS 与 macOS 构建）

## 快速开始

### 1. Android

```bash
cd /Users/koma/AndroidStudioProjects/OneWord
./gradlew :composeApp:assembleDebug
```

APK 输出：

- `composeApp/build/outputs/apk/debug/composeApp-debug.apk`

### 2. macOS Desktop

```bash
cd /Users/koma/AndroidStudioProjects/OneWord
./gradlew :composeApp:packageDmg
```

DMG 输出：

- `composeApp/build/compose/binaries/main/dmg/OneWord-1.0.0.dmg`

### 3. iOS Simulator

推荐使用一键脚本先清理旧调试进程、重启模拟器并重新安装：

```bash
cd /Users/koma/AndroidStudioProjects/OneWord
./scripts/reset_and_run_ios.sh
```

如果只想重新打开 Xcode 工程并清空 `DerivedData`：

```bash
./scripts/open_ios_fresh.sh
```

如果仅走命令行构建并启动模拟器 App：

```bash
./scripts/run_ios_simulator.sh
```

## iOS 说明

- iOS 容器工程由 `iosApp/project.yml` 生成
- 重新生成工程：

```bash
cd /Users/koma/AndroidStudioProjects/OneWord/iosApp
xcodegen generate
```

- 当前项目最低部署版本为 `iOS 13`
- `Info.plist` 已补齐 Compose iOS 所需的 `CADisableMinimumFrameDurationOnPhone`
- iOS 数据库路径已按 SQLDelight Native Driver 要求修正为：
  - `name = oneword.db`
  - `basePath = Application Support`

## 常用脚本

- `scripts/open_ios_fresh.sh`
  - 重生成 Xcode 工程并清理 `DerivedData`
- `scripts/run_ios_simulator.sh`
  - 命令行 build/install/launch iOS 模拟器容器
- `scripts/reset_and_run_ios.sh`
  - 额外清理旧的 `debugserver` / `OneWord` 残留进程，适合 iOS 调试状态异常时使用

## 数据来源

- 今日诗词
- 接入流程：先取 `/token`，再带 `X-User-Token` 请求 `/sentence`

## 测试与验证

已验证过的构建链：

```bash
./gradlew :composeApp:desktopTest
./gradlew :composeApp:assembleDebug
./gradlew :composeApp:packageDmg
./gradlew :composeApp:linkDebugFrameworkIosSimulatorArm64
```

## Git 状态

当前仓库已初始化并有本地提交历史，但还没有配置 GitHub 远程。

如需推送到 GitHub：

```bash
git remote add origin <your-github-repo-url>
git push -u origin main
```

## Release Notes

- [v0.1.0](docs/releases/v0.1.0.md)

## Install

- [Installation Guide](docs/INSTALL.md)

# Install Guide

## Android

1. 下载 Release 附件中的 Android APK。
2. 在 Android 设备上允许安装未知来源应用。
3. 打开 APK 完成安装。

文件名：

- `OneWord-Android-v0.1.0.apk`

## macOS

1. 下载 Release 附件中的 DMG。
2. 打开 DMG。
3. 将 `OneWord.app` 拖入 `Applications`。
4. 首次打开如果被 Gatekeeper 拦截，到 `System Settings -> Privacy & Security` 中允许打开。

文件名：

- `OneWord-macOS-v0.1.0.dmg`

## iOS

当前 iOS 通过 Xcode + Simulator 运行，不提供签名后的发布 IPA。

推荐先重置并运行模拟器容器：

```bash
./scripts/reset_and_run_ios.sh
```

如果只想打开 Xcode 工程并清空 `DerivedData`：

```bash
./scripts/open_ios_fresh.sh
```

如果只想命令行构建并启动模拟器：

```bash
./scripts/run_ios_simulator.sh
```

如果需要导出开发用 IPA：

```bash
TEAM_ID=YOUR_TEAM_ID BUNDLE_ID=com.koma.oneword.iosApp ./scripts/build_ios_ipa.sh
```

说明：

- `reset_and_run_ios.sh`
  - 适合调试残留、黑屏、安装状态异常时使用
- `run_ios_simulator.sh`
  - 适合已经有稳定模拟器环境时快速重跑
- `build_ios_ipa.sh`
  - 适合需要真机开发包时手动归档导出

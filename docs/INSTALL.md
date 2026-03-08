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

当前 iOS 通过 Xcode + Simulator 运行，不提供签名后的 IPA 安装包。

推荐命令：

```bash
cd /Users/koma/AndroidStudioProjects/OneWord
./scripts/reset_and_run_ios.sh
```

如果只想打开 Xcode 工程：

```bash
./scripts/open_ios_fresh.sh
```

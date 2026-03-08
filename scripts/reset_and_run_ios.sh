#!/bin/zsh
# Resets the iOS simulator environment, removes stale debugger processes, rebuilds, reinstalls, and relaunches the app.

set -euo pipefail
ROOT="$(cd "$(dirname "$0")/.." && pwd)"
DEVICE_ID="${DEVICE_ID:-2A0E9031-B775-4952-BB7C-E725DF63BF02}"
BUNDLE_ID="${BUNDLE_ID:-com.koma.oneword.iosApp}"
cd "$ROOT/iosApp"
xcodegen generate >/dev/null
cd "$ROOT"
pkill -9 -f 'OneWord.app/OneWord' || true
pkill -9 -f debugserver || true
rm -rf ~/Library/Developer/Xcode/DerivedData/iosApp-*
xcrun simctl shutdown "$DEVICE_ID" || true
xcrun simctl boot "$DEVICE_ID"
xcrun simctl bootstatus "$DEVICE_ID" -b
xcrun simctl terminate "$DEVICE_ID" "$BUNDLE_ID" || true
xcrun simctl uninstall "$DEVICE_ID" "$BUNDLE_ID" || true
xcodebuild -project iosApp/iosApp.xcodeproj -scheme iosApp -destination "id=$DEVICE_ID" build
APP_PATH=$(find ~/Library/Developer/Xcode/DerivedData/iosApp-* -path '*/Build/Products/Debug-iphonesimulator/OneWord.app' | tail -1)
open -a Simulator
xcrun simctl install "$DEVICE_ID" "$APP_PATH"
xcrun simctl launch "$DEVICE_ID" "$BUNDLE_ID"

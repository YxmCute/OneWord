#!/bin/zsh
# Builds the iOS simulator app, installs it on the configured simulator, and launches it without a full reset.

set -euo pipefail
ROOT="$(cd "$(dirname "$0")/.." && pwd)"
DEVICE_ID="${DEVICE_ID:-2A0E9031-B775-4952-BB7C-E725DF63BF02}"
APP_BUNDLE_ID="${APP_BUNDLE_ID:-com.koma.oneword.iosApp}"
cd "$ROOT"
xcodebuild -project iosApp/iosApp.xcodeproj -scheme iosApp -destination "id=$DEVICE_ID" build
APP_PATH=$(find ~/Library/Developer/Xcode/DerivedData/iosApp-* -path '*/Build/Products/Debug-iphonesimulator/OneWord.app' | tail -1)
open -a Simulator
xcrun simctl boot "$DEVICE_ID" || true
xcrun simctl bootstatus "$DEVICE_ID" -b
xcrun simctl install "$DEVICE_ID" "$APP_PATH"
xcrun simctl launch "$DEVICE_ID" "$APP_BUNDLE_ID"

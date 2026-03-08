#!/bin/zsh
set -euo pipefail
ROOT="/Users/koma/AndroidStudioProjects/OneWord"
cd "$ROOT/iosApp"
xcodegen generate >/dev/null
rm -rf ~/Library/Developer/Xcode/DerivedData/iosApp-*
open "$ROOT/iosApp/iosApp.xcodeproj"

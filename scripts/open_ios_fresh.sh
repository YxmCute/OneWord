#!/bin/zsh
# Regenerates the Xcode project, clears DerivedData, and opens a clean iOS workspace window.

set -euo pipefail
ROOT="$(cd "$(dirname "$0")/.." && pwd)"
cd "$ROOT/iosApp"
xcodegen generate >/dev/null
rm -rf ~/Library/Developer/Xcode/DerivedData/iosApp-*
open "$ROOT/iosApp/iosApp.xcodeproj"

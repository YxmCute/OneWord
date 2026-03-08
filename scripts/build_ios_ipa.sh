#!/usr/bin/env bash
# Archives the iOS container app and exports a development IPA using the provided team and bundle identifiers.
# This is intended for manual development distribution rather than App Store submission.

set -euo pipefail

ROOT_DIR="$(cd "$(dirname "$0")/.." && pwd)"
PROJECT_PATH="$ROOT_DIR/iosApp/iosApp.xcodeproj"
SCHEME="iosApp"
CONFIGURATION="${CONFIGURATION:-Release}"
EXPORT_METHOD="${EXPORT_METHOD:-development}"
TEAM_ID="${TEAM_ID:-}"
BUNDLE_ID="${BUNDLE_ID:-com.koma.oneword.iosApp}"
ARCHIVE_PATH="${ARCHIVE_PATH:-$ROOT_DIR/build/ios/$SCHEME.xcarchive}"
EXPORT_PATH="${EXPORT_PATH:-$ROOT_DIR/build/ios/export-$EXPORT_METHOD}"
TEMP_EXPORT_OPTIONS="$ROOT_DIR/build/ios/ExportOptions.$EXPORT_METHOD.plist"
JAVA_17_HOME="${JAVA_HOME:-$(/usr/libexec/java_home -v 17 2>/dev/null || true)}"

if [[ -z "$TEAM_ID" ]]; then
  echo "TEAM_ID is required."
  echo "Example:"
  echo "  TEAM_ID=ABCDE12345 BUNDLE_ID=com.example.oneword ./scripts/build_ios_ipa.sh"
  exit 1
fi

if [[ -z "$JAVA_17_HOME" ]]; then
  echo "JDK 17 is required."
  echo "Install JDK 17 or run with JAVA_HOME pointing to a JDK 17 installation."
  exit 1
fi

export JAVA_HOME="$JAVA_17_HOME"
export PATH="$JAVA_HOME/bin:$PATH"

mkdir -p "$(dirname "$ARCHIVE_PATH")" "$EXPORT_PATH"

cat > "$TEMP_EXPORT_OPTIONS" <<EOF
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE plist PUBLIC "-//Apple//DTD PLIST 1.0//EN" "http://www.apple.com/DTDs/PropertyList-1.0.dtd">
<plist version="1.0">
<dict>
    <key>method</key>
    <string>$EXPORT_METHOD</string>
    <key>signingStyle</key>
    <string>automatic</string>
    <key>teamID</key>
    <string>$TEAM_ID</string>
    <key>destination</key>
    <string>export</string>
    <key>stripSwiftSymbols</key>
    <true/>
    <key>compileBitcode</key>
    <false/>
</dict>
</plist>
EOF

echo "Archiving $SCHEME"
xcodebuild \
  -project "$PROJECT_PATH" \
  -scheme "$SCHEME" \
  -configuration "$CONFIGURATION" \
  -destination "generic/platform=iOS" \
  -archivePath "$ARCHIVE_PATH" \
  -allowProvisioningUpdates \
  DEVELOPMENT_TEAM="$TEAM_ID" \
  PRODUCT_BUNDLE_IDENTIFIER="$BUNDLE_ID" \
  clean archive

echo "Exporting IPA"
xcodebuild \
  -exportArchive \
  -archivePath "$ARCHIVE_PATH" \
  -exportPath "$EXPORT_PATH" \
  -exportOptionsPlist "$TEMP_EXPORT_OPTIONS" \
  -allowProvisioningUpdates

echo "IPA output:"
find "$EXPORT_PATH" -maxdepth 1 -name "*.ipa" -print

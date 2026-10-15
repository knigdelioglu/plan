#!/usr/bin/env bash
set -euo pipefail

display="${1:-compact}"
case "$display" in
  compact)
    echo "Using the emulator's native phone window"
    ;;
  tablet-portrait)
    adb shell wm size 1200x1920
    adb shell wm density 240
    ;;
  tablet-landscape)
    adb shell wm size 1920x1200
    adb shell wm density 240
    ;;
  *)
    echo "Unknown display: $display" >&2
    exit 2
    ;;
esac

adb shell wm size
adb shell wm density
cd android
./gradlew connectedDebugAndroidTest --stacktrace

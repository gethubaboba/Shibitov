#!/usr/bin/env bash
# Task 13 — Pseudolocales: проверка english XA (accent) и AR XB (RTL)

set -euo pipefail

CALC_PKG="com.example.lab_4_calculator_shibitov_nikolay"
GUESS_PKG="com.example.lab_4_guessnumber_shibitov_nikolay"

echo "=== Task 13: Pseudolocales Test ==="

echo "NOTE: pseudoLocalesEnabled is set in debug buildType (build.gradle)."
echo "Install debug APK on emulator, then switch locale in developer settings."
echo ""

echo "[1] Install Calculator debug APK..."
adb install -r "Lab_4_Calculator_Shibitov_Nikolay/app/build/outputs/apk/debug/app-debug.apk"

echo "[2] Switch to English XA (accented pseudo-locale)..."
adb shell setprop persist.sys.locale en-XA
adb shell stop; adb shell start
sleep 5

adb shell am start -n "$CALC_PKG/.MainActivity"
sleep 2
adb shell screencap /sdcard/task13_en_xa.png
adb pull /sdcard/task13_en_xa.png task13_en_xa.png
echo "  Screenshot: task13_en_xa.png"

echo "[3] Switch to AR XB (RTL pseudo-locale)..."
adb shell setprop persist.sys.locale ar-XB
adb shell stop; adb shell start
sleep 5

adb shell am start -n "$CALC_PKG/.MainActivity"
sleep 2
adb shell screencap /sdcard/task13_ar_xb.png
adb pull /sdcard/task13_ar_xb.png task13_ar_xb.png
echo "  Screenshot: task13_ar_xb.png"

echo "[4] Restore default locale (en-US)..."
adb shell setprop persist.sys.locale en-US
adb shell stop; adb shell start
sleep 5

echo "=== Done. Check screenshots for text truncation and RTL layout issues ==="

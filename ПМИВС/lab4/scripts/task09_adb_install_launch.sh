#!/usr/bin/env bash
# Task 9 — ADB: установка и запуск приложений

set -euo pipefail

CALC_APK="app/build/outputs/apk/debug/app-debug.apk"
GUESS_APK="app/build/outputs/apk/debug/app-debug.apk"
CALC_PKG="com.example.lab_4_calculator_shibitov_nikolay"
GUESS_PKG="com.example.lab_4_guessnumber_shibitov_nikolay"

echo "=== Task 9: ADB Install & Launch ==="

echo "[1] Installing Calculator..."
adb install -r "Lab_4_Calculator_Shibitov_Nikolay/$CALC_APK"

echo "[2] Installing GuessNumber..."
adb install -r "Lab_4_GuessNumber_Shibitov_Nikolay/$GUESS_APK"

echo "[3] Launching Calculator..."
adb shell am start -n "$CALC_PKG/.MainActivity"
sleep 2

echo "[4] Capturing Calculator screenshot..."
adb shell screencap /sdcard/task09_calculator.png
adb pull /sdcard/task09_calculator.png task09_calculator.png

echo "[5] Launching GuessNumber..."
adb shell am start -n "$GUESS_PKG/.MainActivity"
sleep 2

echo "[6] Capturing GuessNumber screenshot..."
adb shell screencap /sdcard/task09_guess.png
adb pull /sdcard/task09_guess.png task09_guess.png

echo "[7] Reading logcat (5 seconds)..."
timeout 5 adb logcat -d -v time | grep -E "ActivityManager|$CALC_PKG|$GUESS_PKG" || true

echo "=== Done. Screenshots: task09_calculator.png, task09_guess.png ==="

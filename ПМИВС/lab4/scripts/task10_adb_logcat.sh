#!/usr/bin/env bash
# Task 10 — ADB: фильтрация logcat по тегу/уровню

set -euo pipefail

CALC_PKG="com.example.lab_4_calculator_shibitov_nikolay"
GUESS_PKG="com.example.lab_4_guessnumber_shibitov_nikolay"

echo "=== Task 10: Logcat Filtering ==="

echo "[1] Clear logcat buffer..."
adb logcat -c

echo "[2] Launch Calculator..."
adb shell am start -n "$CALC_PKG/.MainActivity"
sleep 3

echo "[3] Logcat — WARNING level and above for all tags:"
adb logcat -d *:W | head -50

echo "[4] Logcat — DEBUG level, filter by app package:"
adb logcat -d | grep "$CALC_PKG" | head -30

echo "[5] Logcat — only Errors:"
adb logcat -d *:E | head -20

echo "[6] Save full logcat to file:"
adb logcat -d > task10_logcat_full.txt
echo "    Saved to task10_logcat_full.txt ($(wc -l < task10_logcat_full.txt) lines)"

echo "=== Done ==="

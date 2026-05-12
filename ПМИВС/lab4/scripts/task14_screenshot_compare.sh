#!/usr/bin/env bash
# Task 14 — Screenshot comparison (reference vs actual)

set -euo pipefail

CALC_PKG="com.example.lab_4_calculator_shibitov_nikolay"
REF_DIR="task14_reference"
ACT_DIR="task14_actual"
mkdir -p "$REF_DIR" "$ACT_DIR"

capture() {
    local tag="$1" dir="$2"
    adb shell screencap "/sdcard/sc_${tag}.png"
    adb pull "/sdcard/sc_${tag}.png" "${dir}/sc_${tag}.png"
    echo "  Saved ${dir}/sc_${tag}.png"
}

echo "=== Task 14: Screenshot Comparison ==="

echo "[1] Launch Calculator..."
adb shell am start -n "$CALC_PKG/.MainActivity"
sleep 2

echo "[2] Save reference screenshot (initial state)..."
capture "initial" "$REF_DIR"

echo "[3] Perform calculation: 7 × 6 = 42..."
adb shell input tap 160 710   # btn_7
adb shell input tap 810 640   # btn_multiply
adb shell input tap 490 640   # btn_6
adb shell input tap 810 780   # btn_equals
sleep 1
capture "result_42" "$ACT_DIR"

echo "[4] Clear and re-enter same expression..."
adb shell input tap 160 640   # btn_clear
sleep 1
capture "after_clear" "$ACT_DIR"

echo "[5] Reference vs actual comparison (size check)..."
REF_SIZE=$(stat -c%s "${REF_DIR}/sc_initial.png" 2>/dev/null || echo 0)
ACT_SIZE=$(stat -c%s "${ACT_DIR}/sc_result_42.png" 2>/dev/null || echo 0)
echo "  Reference size: ${REF_SIZE} bytes"
echo "  Actual size:    ${ACT_SIZE} bytes"

if [ "$REF_SIZE" -gt 0 ] && [ "$ACT_SIZE" -gt 0 ]; then
    echo "  Both screenshots captured successfully — visual diff required manually."
else
    echo "  WARNING: One or more screenshots missing."
fi

echo "=== Done ==="

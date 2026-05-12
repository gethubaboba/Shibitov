#!/usr/bin/env bash
# Task 12 — ADB regression: SMS, incoming call, battery, wifi interruptions

set -euo pipefail

CALC_PKG="com.example.lab_4_calculator_shibitov_nikolay"
GUESS_PKG="com.example.lab_4_guessnumber_shibitov_nikolay"
SCREENSHOTS_DIR="task12_screenshots"
mkdir -p "$SCREENSHOTS_DIR"

pull_screenshot() {
    local name="$1"
    adb shell screencap "/sdcard/${name}.png"
    adb pull "/sdcard/${name}.png" "${SCREENSHOTS_DIR}/${name}.png"
}

echo "=== Task 12: ADB Regression Test ==="

# --- Install both APKs ---
echo "[1] Installing apps..."
adb install -r "Lab_4_Calculator_Shibitov_Nikolay/app/build/outputs/apk/debug/app-debug.apk"
adb install -r "Lab_4_GuessNumber_Shibitov_Nikolay/app/build/outputs/apk/debug/app-debug.apk"

# --- Start Calculator and enter expression ---
echo "[2] Launch Calculator, enter 42 + 8..."
adb shell am start -n "$CALC_PKG/.MainActivity"
sleep 2
adb shell input keyevent 10   # '4'
adb shell input keyevent 11   # '2'
# Tap '+' via coordinates (approximate for ConstraintLayout)
adb shell input tap 810 710   # btn_plus region
adb shell input keyevent 17   # '8'
pull_screenshot "01_calc_before_sms"

# --- Send SMS interruption ---
echo "[3] Send SMS to emulator..."
adb emu sms send +79001234567 "Test SMS from regression script"
sleep 2
pull_screenshot "02_calc_sms_arrived"

# --- Dismiss notification, return to calculator ---
adb shell input keyevent KEYCODE_BACK
sleep 1
adb shell am start -n "$CALC_PKG/.MainActivity"
sleep 1
pull_screenshot "03_calc_after_sms"

# --- Incoming GSM call ---
echo "[4] Simulate incoming call..."
adb emu gsm call +79009876543
sleep 3
pull_screenshot "04_call_incoming"

echo "[5] Hang up the call..."
adb emu gsm cancel +79009876543
sleep 2
adb shell am start -n "$CALC_PKG/.MainActivity"
sleep 1
pull_screenshot "05_calc_after_call"

# --- Battery level change ---
echo "[6] Set battery to 15% (low battery warning)..."
adb shell dumpsys battery set level 15
sleep 2
pull_screenshot "06_low_battery"
adb shell dumpsys battery reset

# --- WiFi toggle ---
echo "[7] Toggle WiFi off/on..."
adb shell svc wifi disable
sleep 1
adb shell svc wifi enable
sleep 1
pull_screenshot "07_wifi_restored"

# --- GuessNumber regression ---
echo "[8] Launch GuessNumber, play a move..."
adb shell am start -n "$GUESS_PKG/.MainActivity"
sleep 2
adb shell input tap 540 640   # et_guess
adb shell input text "50"
adb shell input tap 540 740   # btn_guess
sleep 1
pull_screenshot "08_guess_after_move"

# --- Check for crashes ---
echo "[9] Check logcat for fatal errors..."
CRASHES=$(adb logcat -d | grep -c "FATAL EXCEPTION" || true)
if [ "$CRASHES" -gt 0 ]; then
    echo "  FAIL: $CRASHES crash(es) detected!"
else
    echo "  OK: No crashes."
fi

echo "=== Regression done. Screenshots in $SCREENSHOTS_DIR/ ==="

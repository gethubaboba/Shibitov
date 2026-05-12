#!/usr/bin/env bash
# Task 11 — Monkey stress test

set -euo pipefail

CALC_PKG="com.example.lab_4_calculator_shibitov_nikolay"
GUESS_PKG="com.example.lab_4_guessnumber_shibitov_nikolay"
EVENTS=5000
SEED=12345

echo "=== Task 11: Monkey Stress Test ==="

echo "[1] Monkey on Calculator ($EVENTS events, seed=$SEED)..."
adb shell monkey \
    -p "$CALC_PKG" \
    -s $SEED \
    --pct-touch 60 \
    --pct-motion 20 \
    --pct-appswitch 5 \
    --ignore-crashes \
    --ignore-timeouts \
    --ignore-security-exceptions \
    -v $EVENTS \
    2>&1 | tee task11_monkey_calculator.txt

echo ""
echo "[2] Monkey on GuessNumber ($EVENTS events, seed=$SEED)..."
adb shell monkey \
    -p "$GUESS_PKG" \
    -s $SEED \
    --pct-touch 60 \
    --pct-motion 20 \
    --pct-appswitch 5 \
    --ignore-crashes \
    --ignore-timeouts \
    --ignore-security-exceptions \
    -v $EVENTS \
    2>&1 | tee task11_monkey_guess.txt

echo ""
echo "[3] Check for crashes in monkey output..."
if grep -q "CRASH" task11_monkey_calculator.txt; then
    echo "  FAIL: Calculator crashed during Monkey test!"
else
    echo "  OK: Calculator — no crashes."
fi

if grep -q "CRASH" task11_monkey_guess.txt; then
    echo "  FAIL: GuessNumber crashed during Monkey test!"
else
    echo "  OK: GuessNumber — no crashes."
fi

echo "=== Done ==="

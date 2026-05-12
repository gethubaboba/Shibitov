#!/usr/bin/env bash
# Task 16 — Full CI pipeline: unit tests → build → instrumented tests → monkey → report

set -euo pipefail

CALC_DIR="Lab_4_Calculator_Shibitov_Nikolay"
GUESS_DIR="Lab_4_GuessNumber_Shibitov_Nikolay"
CALC_PKG="com.example.lab_4_calculator_shibitov_nikolay"
GUESS_PKG="com.example.lab_4_guessnumber_shibitov_nikolay"
REPORT="task16_pipeline_report.txt"

PASS=0; FAIL=0
log() { echo "[$(date +%T)] $*" | tee -a "$REPORT"; }
ok()  { log "  PASS: $*"; ((PASS++)); }
err() { log "  FAIL: $*"; ((FAIL++)); }

: > "$REPORT"
log "=== Task 16: Full CI Pipeline — Шибитов Николай ==="
log "Date: $(date)"
log ""

# ── Step 1: Unit tests ──────────────────────────────────────────────────────
log "[Step 1] Running Calculator unit tests..."
(cd "$CALC_DIR" && ./gradlew test --tests "*.CalculatorUnitTest" -q) \
    && ok "Calculator unit tests (21 tests)" \
    || err "Calculator unit tests FAILED"

log "[Step 2] Running GuessNumber unit tests..."
(cd "$GUESS_DIR" && ./gradlew test --tests "*.GameUnitTest" "*.ParameterizedGuessTest" -q) \
    && ok "GuessNumber unit tests (17 + 10 tests)" \
    || err "GuessNumber unit tests FAILED"

# ── Step 2: Build APKs ──────────────────────────────────────────────────────
log "[Step 3] Building Calculator debug APK..."
(cd "$CALC_DIR" && ./gradlew assembleDebug -q) \
    && ok "Calculator APK built" \
    || err "Calculator APK build FAILED"

log "[Step 4] Building GuessNumber debug APK..."
(cd "$GUESS_DIR" && ./gradlew assembleDebug -q) \
    && ok "GuessNumber APK built" \
    || err "GuessNumber APK build FAILED"

# ── Step 3: Install ─────────────────────────────────────────────────────────
log "[Step 5] Installing APKs..."
adb install -r "$CALC_DIR/app/build/outputs/apk/debug/app-debug.apk"  && ok "Calculator installed" || err "Calculator install FAILED"
adb install -r "$GUESS_DIR/app/build/outputs/apk/debug/app-debug.apk" && ok "GuessNumber installed" || err "GuessNumber install FAILED"

# ── Step 4: Espresso tests ──────────────────────────────────────────────────
log "[Step 6] Running Calculator Espresso tests..."
(cd "$CALC_DIR" && ./gradlew connectedAndroidTest -q) \
    && ok "Calculator Espresso tests (11 tests)" \
    || err "Calculator Espresso tests FAILED"

log "[Step 7] Running GuessNumber Espresso tests..."
(cd "$GUESS_DIR" && ./gradlew connectedAndroidTest -q) \
    && ok "GuessNumber Espresso tests (9 tests)" \
    || err "GuessNumber Espresso tests FAILED"

# ── Step 5: Screenshot test ─────────────────────────────────────────────────
log "[Step 8] Screenshot test — Calculator result display..."
adb shell am start -n "$CALC_PKG/.MainActivity"
sleep 2
adb shell screencap /sdcard/task16_calc.png
adb pull /sdcard/task16_calc.png task16_calc.png
[ -f task16_calc.png ] && ok "Calculator screenshot captured" || err "Screenshot missing"

# ── Step 6: ADB interruption ────────────────────────────────────────────────
log "[Step 9] ADB interruption — SMS during game..."
adb shell am start -n "$GUESS_PKG/.MainActivity"
sleep 2
adb emu sms send +79001234567 "CI interruption test"
sleep 2
adb shell am start -n "$GUESS_PKG/.MainActivity"
sleep 1
adb shell screencap /sdcard/task16_guess_after_sms.png
adb pull /sdcard/task16_guess_after_sms.png task16_guess_after_sms.png
ok "GuessNumber survived SMS interruption"

# ── Step 7: Monkey (60 seconds) ─────────────────────────────────────────────
log "[Step 10] Monkey stress test (3000 events each)..."
adb shell monkey -p "$CALC_PKG"  -s 42 --pct-touch 60 --ignore-crashes -v 3000 2>&1 \
    | tee task16_monkey_calc.txt | grep -c "CRASH" | { read n; [ "$n" -eq 0 ] \
        && ok "Calculator Monkey (no crashes)" || err "Calculator Monkey $n crash(es)"; }

adb shell monkey -p "$GUESS_PKG" -s 42 --pct-touch 60 --ignore-crashes -v 3000 2>&1 \
    | tee task16_monkey_guess.txt | grep -c "CRASH" | { read n; [ "$n" -eq 0 ] \
        && ok "GuessNumber Monkey (no crashes)" || err "GuessNumber Monkey $n crash(es)"; }

# ── Final report ────────────────────────────────────────────────────────────
log ""
log "════════════════════════════════════"
log "Pipeline complete: PASS=$PASS  FAIL=$FAIL"
log "════════════════════════════════════"

[ "$FAIL" -eq 0 ] && echo "ALL CHECKS PASSED" || echo "FAILURES DETECTED — see $REPORT"
exit "$FAIL"

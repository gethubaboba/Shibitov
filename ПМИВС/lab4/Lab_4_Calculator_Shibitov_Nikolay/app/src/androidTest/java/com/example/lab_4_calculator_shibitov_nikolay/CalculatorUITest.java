package com.example.lab_4_calculator_shibitov_nikolay;

import androidx.test.espresso.accessibility.AccessibilityChecks;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.*;

/**
 * Tasks 3, 4 — Espresso UI-тесты.
 * Task 3: базовые сценарии калькулятора.
 * Task 4: сохранение состояния при повороте экрана (ActivityScenario.recreate).
 */
@RunWith(AndroidJUnit4.class)
public class CalculatorUITest {

    @Rule
    public ActivityScenarioRule<MainActivity> activityRule =
            new ActivityScenarioRule<>(MainActivity.class);

    @BeforeClass
    public static void enableAccessibilityChecks() {
        AccessibilityChecks.enable().setRunChecksFromRootView(true);
    }

    // Task 3 — UI tests

    @Test
    public void testUi_SimpleAddition() {
        onView(withId(R.id.btn_2)).perform(click());
        onView(withId(R.id.btn_plus)).perform(click());
        onView(withId(R.id.btn_3)).perform(click());
        onView(withId(R.id.btn_equals)).perform(click());
        onView(withId(R.id.tv_result)).check(matches(withText("5")));
    }

    @Test
    public void testUi_Subtraction() {
        onView(withId(R.id.btn_9)).perform(click());
        onView(withId(R.id.btn_minus)).perform(click());
        onView(withId(R.id.btn_4)).perform(click());
        onView(withId(R.id.btn_equals)).perform(click());
        onView(withId(R.id.tv_result)).check(matches(withText("5")));
    }

    @Test
    public void testUi_Multiplication() {
        onView(withId(R.id.btn_3)).perform(click());
        onView(withId(R.id.btn_multiply)).perform(click());
        onView(withId(R.id.btn_4)).perform(click());
        onView(withId(R.id.btn_equals)).perform(click());
        onView(withId(R.id.tv_result)).check(matches(withText("12")));
    }

    @Test
    public void testUi_Division() {
        onView(withId(R.id.btn_8)).perform(click());
        onView(withId(R.id.btn_divide)).perform(click());
        onView(withId(R.id.btn_2)).perform(click());
        onView(withId(R.id.btn_equals)).perform(click());
        onView(withId(R.id.tv_result)).check(matches(withText("4")));
    }

    @Test
    public void testUi_DivisionByZero_ShowsError() {
        onView(withId(R.id.btn_5)).perform(click());
        onView(withId(R.id.btn_divide)).perform(click());
        onView(withId(R.id.btn_0)).perform(click());
        onView(withId(R.id.btn_equals)).perform(click());
        onView(withId(R.id.tv_result)).check(matches(withText(
                R.string.error_division_by_zero)));
    }

    @Test
    public void testUi_Clear_ResetsDisplay() {
        onView(withId(R.id.btn_5)).perform(click());
        onView(withId(R.id.btn_clear)).perform(click());
        onView(withId(R.id.tv_result)).check(matches(withText("0")));
    }

    @Test
    public void testUi_Backspace_RemovesLastDigit() {
        onView(withId(R.id.btn_1)).perform(click());
        onView(withId(R.id.btn_2)).perform(click());
        onView(withId(R.id.btn_3)).perform(click());
        onView(withId(R.id.btn_backspace)).perform(click());
        onView(withId(R.id.tv_result)).check(matches(withText("12")));
    }

    @Test
    public void testUi_Negate() {
        onView(withId(R.id.btn_5)).perform(click());
        onView(withId(R.id.btn_negate)).perform(click());
        onView(withId(R.id.tv_result)).check(matches(withText("-5")));
    }

    @Test
    public void testUi_Percent() {
        onView(withId(R.id.btn_5)).perform(click());
        onView(withId(R.id.btn_0)).perform(click());
        onView(withId(R.id.btn_percent)).perform(click());
        onView(withId(R.id.tv_result)).check(matches(withText("0.5")));
    }

    // Task 4 — Rotation state preservation

    @Test
    public void testUi_RotationSavesInput() {
        onView(withId(R.id.btn_1)).perform(click());
        onView(withId(R.id.btn_2)).perform(click());
        onView(withId(R.id.btn_3)).perform(click());

        activityRule.getScenario().recreate();

        onView(withId(R.id.tv_expression)).check(matches(withText("123")));
    }

    @Test
    public void testUi_RotationSavesFullExpression() {
        onView(withId(R.id.btn_4)).perform(click());
        onView(withId(R.id.btn_plus)).perform(click());
        onView(withId(R.id.btn_5)).perform(click());

        activityRule.getScenario().recreate();

        onView(withId(R.id.tv_expression)).check(matches(withText("4 + 5")));
    }
}

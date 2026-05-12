package com.example.lab_4_guessnumber_shibitov_nikolay;

import androidx.test.espresso.accessibility.AccessibilityChecks;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.*;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.*;

/**
 * Tasks 6, 7 — Espresso UI-тесты игры «Угадай число».
 * Task 6: базовые UI-сценарии.
 * Task 7: accessibility-проверки (TalkBack / contentDescription).
 */
@RunWith(AndroidJUnit4.class)
public class GameUITest {

    @Rule
    public ActivityScenarioRule<MainActivity> activityRule =
            new ActivityScenarioRule<>(MainActivity.class);

    @BeforeClass
    public static void enableAccessibilityChecks() {
        AccessibilityChecks.enable().setRunChecksFromRootView(true);
    }

    // Task 6 — UI tests

    @Test
    public void testUi_InitialState_GuessButtonEnabled() {
        onView(withId(R.id.btn_guess)).check(matches(isEnabled()));
    }

    @Test
    public void testUi_InitialState_NewGameButtonDisabled() {
        onView(withId(R.id.btn_new_game)).check(matches(not(isEnabled())));
    }

    @Test
    public void testUi_InvalidInput_ShowsMessage() {
        onView(withId(R.id.et_guess)).perform(typeText("abc"), closeSoftKeyboard());
        onView(withId(R.id.btn_guess)).perform(click());
        onView(withId(R.id.tv_result)).check(matches(withText(containsString("1"))));
    }

    @Test
    public void testUi_OutOfRangeInput_ShowsMessage() {
        onView(withId(R.id.et_guess)).perform(typeText("200"), closeSoftKeyboard());
        onView(withId(R.id.btn_guess)).perform(click());
        onView(withId(R.id.tv_result)).check(matches(withText(containsString("1"))));
    }

    @Test
    public void testUi_AttemptsDisplayUpdates() {
        onView(withId(R.id.et_guess)).perform(typeText("50"), closeSoftKeyboard());
        onView(withId(R.id.btn_guess)).perform(click());
        onView(withId(R.id.tv_attempts)).check(matches(withText(containsString("/ 7"))));
    }

    // Task 7 — Accessibility

    @Test
    public void testUi_Accessibility_GuessButton_HasContentDescription() {
        onView(withId(R.id.btn_guess)).check(matches(
                withContentDescription(R.string.btn_guess)));
    }

    @Test
    public void testUi_Accessibility_NewGameButton_HasContentDescription() {
        onView(withId(R.id.btn_new_game)).check(matches(
                withContentDescription(R.string.btn_new_game)));
    }

    @Test
    public void testUi_Accessibility_ResultText_HasContentDescription() {
        onView(withId(R.id.tv_result)).check(matches(
                withContentDescription(R.string.cd_result)));
    }

    @Test
    public void testUi_Accessibility_AttemptsText_HasContentDescription() {
        onView(withId(R.id.tv_attempts)).check(matches(
                withContentDescription(R.string.cd_attempts)));
    }
}

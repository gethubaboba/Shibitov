package com.example.lab_4_calculator_shibitov_nikolay;

import android.content.Context;
import android.content.res.Configuration;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Locale;

import static org.junit.Assert.assertEquals;

/**
 * Task 15 — Проверка локализации: немецкая строка об ошибке делення на ноль.
 */
@RunWith(AndroidJUnit4.class)
public class CalculatorLocalizationTest {

    @Test
    public void testGermanLocale_divisionByZeroString() {
        Context baseCtx = InstrumentationRegistry.getInstrumentation().getTargetContext();

        Configuration config = new Configuration(baseCtx.getResources().getConfiguration());
        config.setLocale(Locale.GERMAN);
        Context deCtx = baseCtx.createConfigurationContext(config);

        String expected = "Fehler: Division durch Null";
        String actual   = deCtx.getString(R.string.error_division_by_zero);

        assertEquals(expected, actual);
    }

    @Test
    public void testGermanLocale_appName() {
        Context baseCtx = InstrumentationRegistry.getInstrumentation().getTargetContext();
        Configuration config = new Configuration(baseCtx.getResources().getConfiguration());
        config.setLocale(Locale.GERMAN);
        Context deCtx = baseCtx.createConfigurationContext(config);

        assertEquals("Taschenrechner", deCtx.getString(R.string.app_name));
    }

    @Test
    public void testEnglishLocale_divisionByZeroString() {
        Context baseCtx = InstrumentationRegistry.getInstrumentation().getTargetContext();
        Configuration config = new Configuration(baseCtx.getResources().getConfiguration());
        config.setLocale(Locale.ENGLISH);
        Context enCtx = baseCtx.createConfigurationContext(config);

        assertEquals("Error: Division by zero", enCtx.getString(R.string.error_division_by_zero));
    }
}

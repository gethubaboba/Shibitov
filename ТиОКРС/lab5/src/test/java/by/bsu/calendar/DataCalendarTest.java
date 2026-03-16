package by.bsu.calendar;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JUnit 5 тесты для классов Data и Calendar.
 *
 * Разработаны по методике TDD: каждый тест написан ДО реализации
 * соответствующего метода (фаза Red), затем реализован минимальный код
 * для его прохождения (фаза Green).
 */
class DataCalendarTest {

    // =========================================================
    // Цикл 1: Создание Data и валидация
    // =========================================================

    @Test
    void dataCreation_validDate_noException() {
        assertDoesNotThrow(() -> new Data(15, 6, 2026));
    }

    @Test
    void dataCreation_getters_returnCorrectValues() {
        Data d = new Data(7, 3, 2026);
        assertEquals(7,    d.getDay());
        assertEquals(3,    d.getMonth());
        assertEquals(2026, d.getYear());
    }

    @Test
    void dataCreation_dayZero_throwsException() {
        assertThrows(DataException.class, () -> new Data(0, 1, 2026));
    }

    @Test
    void dataCreation_day32_throwsException() {
        assertThrows(DataException.class, () -> new Data(32, 1, 2026));
    }

    @Test
    void dataCreation_monthZero_throwsException() {
        assertThrows(DataException.class, () -> new Data(1, 0, 2026));
    }

    @Test
    void dataCreation_month13_throwsException() {
        assertThrows(DataException.class, () -> new Data(1, 13, 2026));
    }

    @Test
    void dataCreation_feb29_nonLeapYear_throwsException() {
        assertThrows(DataException.class, () -> new Data(29, 2, 2026));
    }

    @Test
    void dataCreation_feb29_leapYear_noException() {
        assertDoesNotThrow(() -> new Data(29, 2, 2024)); // 2024 — високосный
    }

    @Test
    void dataEquals_sameDateDifferentObject_equal() {
        assertEquals(new Data(1, 1, 2026), new Data(1, 1, 2026));
    }

    @Test
    void dataEquals_differentDates_notEqual() {
        assertNotEquals(new Data(1, 1, 2026), new Data(2, 1, 2026));
    }

    // =========================================================
    // Цикл 2: Високосный год и число дней в месяце
    // =========================================================

    @Test
    void isLeapYear_divisibleBy400_true() {
        assertTrue(Data.isLeapYear(2000));
    }

    @Test
    void isLeapYear_divisibleBy100NotBy400_false() {
        assertFalse(Data.isLeapYear(1900));
        assertFalse(Data.isLeapYear(2100));
    }

    @Test
    void isLeapYear_divisibleBy4NotBy100_true() {
        assertTrue(Data.isLeapYear(2024));
        assertTrue(Data.isLeapYear(2028));
    }

    @Test
    void isLeapYear_notDivisibleBy4_false() {
        assertFalse(Data.isLeapYear(2026));
        assertFalse(Data.isLeapYear(2023));
    }

    @Test
    void daysInMonth_january_31() {
        assertEquals(31, Data.daysInMonth(1, 2026));
    }

    @Test
    void daysInMonth_april_30() {
        assertEquals(30, Data.daysInMonth(4, 2026));
    }

    @Test
    void daysInMonth_february_leapYear_29() {
        assertEquals(29, Data.daysInMonth(2, 2024));
    }

    @Test
    void daysInMonth_february_nonLeapYear_28() {
        assertEquals(28, Data.daysInMonth(2, 2026));
    }

    @Test
    void daysInMonth_december_31() {
        assertEquals(31, Data.daysInMonth(12, 2026));
    }

    // =========================================================
    // Цикл 3: День недели (алгоритм Sakamoto)
    // =========================================================

    @Test
    void getDayOfWeek_jan1_2026_isThursday() {
        // 01.01.2026 — четверг (подтверждено независимо)
        assertEquals(4, new Data(1, 1, 2026).getDayOfWeek()); // 4 = Чт
    }

    @Test
    void getDayOfWeek_jan4_2026_isSunday() {
        assertEquals(7, new Data(4, 1, 2026).getDayOfWeek()); // 7 = Вс
    }

    @Test
    void getDayOfWeek_jan5_2026_isMonday() {
        assertEquals(1, new Data(5, 1, 2026).getDayOfWeek()); // 1 = Пн
    }

    @Test
    void getDayOfWeek_jan3_2026_isSaturday() {
        assertEquals(6, new Data(3, 1, 2026).getDayOfWeek()); // 6 = Сб
    }

    @Test
    void getDayOfWeek_feb1_2026_isSunday() {
        // 01.02.2026 — воскресенье
        assertEquals(7, new Data(1, 2, 2026).getDayOfWeek());
    }

    @Test
    void getDayOfWeekName_thursday_correctRussian() {
        assertEquals("Четверг", new Data(1, 1, 2026).getDayOfWeekName());
    }

    @Test
    void getDayOfWeekName_monday_correctRussian() {
        assertEquals("Понедельник", new Data(5, 1, 2026).getDayOfWeekName());
    }

    // =========================================================
    // Цикл 4: Конструктор Calendar и getDaysCount
    // =========================================================

    @Test
    void calendar_january2026_31days() {
        assertEquals(31, new Calendar(1, 2026).getDaysCount());
    }

    @Test
    void calendar_february2026_28days() {
        assertEquals(28, new Calendar(2, 2026).getDaysCount());
    }

    @Test
    void calendar_february2024_29days() {
        assertEquals(29, new Calendar(2, 2024).getDaysCount()); // 2024 високосный
    }

    @Test
    void calendar_april2026_30days() {
        assertEquals(30, new Calendar(4, 2026).getDaysCount());
    }

    @Test
    void calendar_invalidMonth_throwsException() {
        assertThrows(DataException.class, () -> new Calendar(13, 2026));
        assertThrows(DataException.class, () -> new Calendar(0, 2026));
    }

    @Test
    void calendar_getAllDates_firstDayIsFirst() {
        Calendar jan = new Calendar(1, 2026);
        assertEquals(new Data(1, 1, 2026), jan.getAllDates()[0]);
    }

    @Test
    void calendar_getAllDates_lastDayIsCorrect() {
        Calendar jan = new Calendar(1, 2026);
        Data[] dates = jan.getAllDates();
        assertEquals(new Data(31, 1, 2026), dates[dates.length - 1]);
    }

    @Test
    void calendar_monthName_january_isCorrect() {
        assertEquals("Январь", new Calendar(1, 2026).getMonthName());
    }

    // =========================================================
    // Цикл 5: getDayOfWeekForDate
    // =========================================================

    @Test
    void getDayOfWeekForDate_jan1_isThursday() {
        assertEquals(4, new Calendar(1, 2026).getDayOfWeekForDate(1));
    }

    @Test
    void getDayOfWeekForDate_jan5_isMonday() {
        assertEquals(1, new Calendar(1, 2026).getDayOfWeekForDate(5));
    }

    @Test
    void getDayOfWeekForDate_jan31_isSaturday() {
        // 31.01.2026 — суббота
        assertEquals(6, new Calendar(1, 2026).getDayOfWeekForDate(31));
    }

    @Test
    void getDayOfWeekForDate_outOfRange_throwsException() {
        Calendar jan = new Calendar(1, 2026);
        assertThrows(DataException.class, () -> jan.getDayOfWeekForDate(0));
        assertThrows(DataException.class, () -> jan.getDayOfWeekForDate(32));
    }

    // =========================================================
    // Цикл 6: getDatesByDayOfWeek
    // =========================================================

    @Test
    void getDatesByDayOfWeek_thursday_jan2026_correctCount() {
        // Четверги в январе 2026: 1, 8, 15, 22, 29 → 5 дат
        List<Data> thursdays = new Calendar(1, 2026).getDatesByDayOfWeek(4);
        assertEquals(5, thursdays.size());
    }

    @Test
    void getDatesByDayOfWeek_thursday_jan2026_firstIsJan1() {
        List<Data> thursdays = new Calendar(1, 2026).getDatesByDayOfWeek(4);
        assertEquals(new Data(1, 1, 2026), thursdays.get(0));
    }

    @Test
    void getDatesByDayOfWeek_monday_jan2026_4dates() {
        // Понедельники в январе 2026: 5, 12, 19, 26 → 4 даты
        assertEquals(4, new Calendar(1, 2026).getDatesByDayOfWeek(1).size());
    }

    @Test
    void getDatesByDayOfWeek_saturday_jan2026_5dates() {
        // Субботы в январе 2026: 3, 10, 17, 24, 31 → 5 дат
        assertEquals(5, new Calendar(1, 2026).getDatesByDayOfWeek(6).size());
    }

    @Test
    void getDatesByDayOfWeek_sunday_jan2026_4dates() {
        // Воскресенья в январе 2026: 4, 11, 18, 25 → 4 даты
        assertEquals(4, new Calendar(1, 2026).getDatesByDayOfWeek(7).size());
    }

    @Test
    void getDatesByDayOfWeek_invalidDayOfWeek_throwsException() {
        Calendar jan = new Calendar(1, 2026);
        assertThrows(DataException.class, () -> jan.getDatesByDayOfWeek(0));
        assertThrows(DataException.class, () -> jan.getDatesByDayOfWeek(8));
    }

    // =========================================================
    // Цикл 7: Массив Calendar на год
    // =========================================================

    @Test
    void yearArray_2026_12months() {
        Calendar[] year = new Calendar[12];
        for (int m = 1; m <= 12; m++) {
            year[m - 1] = new Calendar(m, 2026);
        }
        assertEquals(12, year.length);
        assertEquals(31, year[0].getDaysCount());   // Январь
        assertEquals(28, year[1].getDaysCount());   // Февраль (не високосный)
        assertEquals(31, year[11].getDaysCount());  // Декабрь
    }

    @Test
    void yearArray_totalDays2026_is365() {
        int total = 0;
        for (int m = 1; m <= 12; m++) {
            total += new Calendar(m, 2026).getDaysCount();
        }
        assertEquals(365, total); // 2026 — не високосный
    }

    @Test
    void yearArray_totalDays2024_is366() {
        int total = 0;
        for (int m = 1; m <= 12; m++) {
            total += new Calendar(m, 2024).getDaysCount();
        }
        assertEquals(366, total); // 2024 — високосный
    }
}

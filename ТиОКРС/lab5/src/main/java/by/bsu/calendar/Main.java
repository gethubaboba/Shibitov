package by.bsu.calendar;

import java.util.List;

/**
 * Демонстрационное приложение для классов Data и Calendar.
 *
 * Демонстрирует:
 *  1. Создание массива из 12 объектов Calendar на 2026 год.
 *  2. Вывод числа дней в каждом месяце.
 *  3. Для января: по дате → день недели.
 *  4. Для января: по дню недели → все даты.
 *  5. Пример обработки исключения (некорректная дата).
 */
public class Main {

    public static void main(String[] args) {

        // ---- 1. Массив из 12 объектов Calendar на 2026 год ----
        final int YEAR = 2026;
        Calendar[] year = new Calendar[12];
        for (int m = 1; m <= 12; m++) {
            year[m - 1] = new Calendar(m, YEAR);
        }

        System.out.println("=== Календарь на " + YEAR + " год ===\n");
        for (Calendar cal : year) {
            System.out.printf("  %-15s %d дней%n", cal.getMonthName(), cal.getDaysCount());
        }

        // ---- 2. Январь: по дате → день недели ----
        Calendar january = year[0];
        System.out.println("\n=== Январь " + YEAR + " — по номеру дня → день недели ===\n");
        System.out.printf("  %-14s | %s%n", "Дата", "День недели");
        System.out.println("  " + "-".repeat(35));
        for (Data d : january.getAllDates()) {
            System.out.printf("  %-14s | %s%n", d, d.getDayOfWeekName());
        }

        // ---- 3. Январь: по дню недели → все даты ----
        System.out.println("\n=== Январь " + YEAR + " — по дню недели → все даты ===\n");
        System.out.printf("  %-15s | %s%n", "День недели", "Даты (числа месяца)");
        System.out.println("  " + "-".repeat(50));
        for (int dow = 1; dow <= 7; dow++) {
            List<Data> dates = january.getDatesByDayOfWeek(dow);
            StringBuilder sb = new StringBuilder();
            for (Data d : dates) {
                if (sb.length() > 0) sb.append(", ");
                sb.append(String.format("%2d", d.getDay()));
            }
            System.out.printf("  %-15s | %s (%d дат)%n",
                    Data.DAY_NAMES[dow], sb, dates.size());
        }

        // ---- 4. Пример обработки исключения ----
        System.out.println("\n=== Пример DataException ===\n");
        try {
            new Data(32, 1, 2026);
        } catch (DataException e) {
            System.out.println("  DataException: " + e.getMessage());
        }
        try {
            new Data(29, 2, 2026);   // 2026 не високосный
        } catch (DataException e) {
            System.out.println("  DataException: " + e.getMessage());
        }
        try {
            new Data(29, 2, 2024);   // 2024 — високосный, ОК
            System.out.println("  29.02.2024 — создан без ошибок (2024 — високосный)");
        } catch (DataException e) {
            System.out.println("  DataException: " + e.getMessage());
        }
    }
}

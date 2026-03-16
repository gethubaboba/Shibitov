package by.bsu.calendar;

/**
 * Класс «Дата» — хранит день, месяц и год.
 *
 * Поддерживает:
 * <ul>
 *   <li>Валидацию при создании (бросает {@link DataException} при ошибке)</li>
 *   <li>Определение високосного года</li>
 *   <li>Вычисление числа дней в месяце</li>
 *   <li>Вычисление дня недели (алгоритм Tomohiko Sakamoto)</li>
 * </ul>
 */
public class Data {

    /** Таблица для алгоритма Sakamoto. */
    private static final int[] SAK = {0, 3, 2, 5, 0, 3, 5, 1, 4, 6, 2, 4};

    /** Названия дней недели: индекс 1 = Понедельник, ..., 7 = Воскресенье. */
    static final String[] DAY_NAMES = {
        "", "Понедельник", "Вторник", "Среда",
        "Четверг", "Пятница", "Суббота", "Воскресенье"
    };

    private final int day;
    private final int month;
    private final int year;

    /**
     * @throws DataException если дата некорректна
     */
    public Data(int day, int month, int year) {
        if (!isValidDate(day, month, year)) {
            throw new DataException(
                    String.format("Некорректная дата: %02d.%02d.%04d", day, month, year));
        }
        this.day   = day;
        this.month = month;
        this.year  = year;
    }

    // ---- геттеры ----

    public int getDay()   { return day; }
    public int getMonth() { return month; }
    public int getYear()  { return year; }

    // ---- статические утилиты ----

    /** Возвращает true, если год високосный. */
    public static boolean isLeapYear(int year) {
        return (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0);
    }

    /** Число дней в месяце с учётом високосных лет. */
    public static int daysInMonth(int month, int year) {
        if (month < 1 || month > 12) {
            throw new DataException("Некорректный месяц: " + month);
        }
        int[] days = {31, isLeapYear(year) ? 29 : 28,
                31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
        return days[month - 1];
    }

    /** Проверяет корректность даты. */
    public static boolean isValidDate(int day, int month, int year) {
        if (year < 1 || month < 1 || month > 12 || day < 1) return false;
        return day <= daysInMonth(month, year);
    }

    // ---- день недели ----

    /**
     * Возвращает день недели: 1 = Понедельник, ..., 7 = Воскресенье.
     *
     * <p>Используется алгоритм Tomohiko Sakamoto (формула Зеллера в оптимизированном виде).
     * Работает корректно для григорианского календаря.
     */
    public int getDayOfWeek() {
        int y = month < 3 ? year - 1 : year;
        int d = (y + y / 4 - y / 100 + y / 400 + SAK[month - 1] + day) % 7;
        // Sakamoto: 0=Вс, 1=Пн, ..., 6=Сб → конвертируем в 1=Пн..7=Вс
        return d == 0 ? 7 : d;
    }

    /** Возвращает название дня недели на русском. */
    public String getDayOfWeekName() {
        return DAY_NAMES[getDayOfWeek()];
    }

    // ---- стандартные методы ----

    @Override
    public String toString() {
        return String.format("%02d.%02d.%04d", day, month, year);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Data)) return false;
        Data d = (Data) o;
        return day == d.day && month == d.month && year == d.year;
    }

    @Override
    public int hashCode() {
        return year * 10000 + month * 100 + day;
    }
}

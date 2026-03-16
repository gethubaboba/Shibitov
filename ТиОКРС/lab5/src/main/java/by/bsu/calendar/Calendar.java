package by.bsu.calendar;

import java.util.ArrayList;
import java.util.List;

/**
 * Класс «Календарь на определённый месяц».
 *
 * Хранит массив объектов {@link Data} — по одному на каждый день месяца.
 * Предоставляет:
 * <ul>
 *   <li>Получение дня недели по дате (по номеру дня)</li>
 *   <li>Получение всех дат месяца с заданным днём недели</li>
 * </ul>
 */
public class Calendar {

    static final String[] MONTH_NAMES = {
        "", "Январь", "Февраль", "Март", "Апрель",
        "Май", "Июнь", "Июль", "Август",
        "Сентябрь", "Октябрь", "Ноябрь", "Декабрь"
    };

    private final int month;
    private final int year;
    private final Data[] dates;   // dates[0] = 1-е число, dates[n-1] = последнее

    /**
     * @param month месяц (1–12)
     * @param year  год (>= 1)
     * @throws DataException при некорректных параметрах
     */
    public Calendar(int month, int year) {
        if (month < 1 || month > 12) {
            throw new DataException("Некорректный месяц: " + month);
        }
        if (year < 1) {
            throw new DataException("Некорректный год: " + year);
        }
        this.month = month;
        this.year  = year;

        int days = Data.daysInMonth(month, year);
        this.dates = new Data[days];
        for (int d = 1; d <= days; d++) {
            dates[d - 1] = new Data(d, month, year);
        }
    }

    // ---- геттеры ----

    public int getMonth()     { return month; }
    public int getYear()      { return year; }
    public int getDaysCount() { return dates.length; }
    public String getMonthName() { return MONTH_NAMES[month]; }

    /** Возвращает копию массива всех дат месяца. */
    public Data[] getAllDates() { return dates.clone(); }

    // ---- основные методы ----

    /**
     * По номеру дня в месяце возвращает день недели (1=Пн..7=Вс).
     *
     * @param day номер дня (1 … getDaysCount())
     * @throws DataException если номер дня выходит за допустимые границы
     */
    public int getDayOfWeekForDate(int day) {
        if (day < 1 || day > dates.length) {
            throw new DataException(
                    "День " + day + " выходит за пределы месяца (" + dates.length + " дней)");
        }
        return dates[day - 1].getDayOfWeek();
    }

    /**
     * Возвращает список всех дат месяца, приходящихся на указанный день недели.
     *
     * @param dayOfWeek день недели (1=Пн..7=Вс)
     * @throws DataException если dayOfWeek вне диапазона [1..7]
     */
    public List<Data> getDatesByDayOfWeek(int dayOfWeek) {
        if (dayOfWeek < 1 || dayOfWeek > 7) {
            throw new DataException("Некорректный день недели: " + dayOfWeek);
        }
        List<Data> result = new ArrayList<>();
        for (Data d : dates) {
            if (d.getDayOfWeek() == dayOfWeek) {
                result.add(d);
            }
        }
        return result;
    }

    @Override
    public String toString() {
        return String.format("%s %d (%d дней)", getMonthName(), year, dates.length);
    }
}

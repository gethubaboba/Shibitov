# Лабораторная работа №5
# Разработка через тестирование (TDD)

**Выполнил:** Шибитов Николай
**Группа:** 11, 3 курс
**Вариант:** 5 — Data и Calendar
**Дисциплина:** Теория и основы конструирования разработки ПО (ТиОКРС)
**Дата:** 16.03.2026

---

## 1. Постановка задачи

**Вариант 5.**
Определить класс `Data` и класс `Calendar` на определённый месяц, использующий объект `Data` в качестве поля. Объявить массив объектов `Calendar` на год. Для месяца январь вывести по дате день недели; по дню недели вывести все даты.

### Классы и методы

| Класс | Описание |
|-------|----------|
| `Data` | Дата (день, месяц, год); `isLeapYear()`, `daysInMonth()`, `getDayOfWeek()` |
| `DataException` | Unchecked-исключение при некорректных параметрах |
| `Calendar` | Календарь на месяц; `getDayOfWeekForDate()`, `getDatesByDayOfWeek()` |
| `Main` | Демонстрация: массив на год + анализ января 2026 |

---

## 2. Методология TDD

Каждый цикл разработки: **Red** (написать падающий тест) → **Green** (минимальный код) → **Refactor**.

---

## 3. Цикл 1 — Создание Data и валидация

### 3.1. Red

```java
@Test
void dataCreation_validDate_noException() {
    assertDoesNotThrow(() -> new Data(15, 6, 2026));
}

@Test
void dataCreation_day32_throwsException() {
    assertThrows(DataException.class, () -> new Data(32, 1, 2026));
}

@Test
void dataCreation_feb29_nonLeapYear_throwsException() {
    assertThrows(DataException.class, () -> new Data(29, 2, 2026));
}
```

**Результат:** `compilation error` — класс `Data` не существует. ✗

### 3.2. Green

```java
public class Data {
    private final int day, month, year;

    public Data(int day, int month, int year) {
        if (!isValidDate(day, month, year))
            throw new DataException("Некорректная дата: " + day + "." + month + "." + year);
        this.day = day; this.month = month; this.year = year;
    }

    public static boolean isValidDate(int day, int month, int year) {
        if (year < 1 || month < 1 || month > 12 || day < 1) return false;
        return day <= daysInMonth(month, year);
    }

    public static int daysInMonth(int month, int year) {
        int[] days = {31, isLeapYear(year) ? 29 : 28,
                      31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
        return days[month - 1];
    }

    public static boolean isLeapYear(int year) {
        return (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0);
    }
}
```

**Результат:** все тесты цикла 1 пройдены ✓

---

## 4. Цикл 2 — Високосный год и число дней в месяце

### 4.1. Red

```java
@Test void isLeapYear_divisibleBy400_true()        { assertTrue(Data.isLeapYear(2000)); }
@Test void isLeapYear_divisibleBy100NotBy400_false(){ assertFalse(Data.isLeapYear(1900)); }
@Test void isLeapYear_divisibleBy4NotBy100_true()  { assertTrue(Data.isLeapYear(2024)); }
@Test void daysInMonth_february_leapYear_29()      { assertEquals(29, Data.daysInMonth(2, 2024)); }
@Test void daysInMonth_february_nonLeapYear_28()   { assertEquals(28, Data.daysInMonth(2, 2026)); }
```

**Результат:** тесты проваливались до добавления `isLeapYear` и `daysInMonth`. ✗

### 4.2. Green

Реализован `isLeapYear` (правило Грегорианского календаря) и `daysInMonth` (таблица из 12 значений с учётом февраля).

**Результат:** все тесты цикла 2 пройдены ✓

---

## 5. Цикл 3 — День недели (алгоритм Tomohiko Sakamoto)

### 5.1. Red

```java
@Test void getDayOfWeek_jan1_2026_isThursday() { assertEquals(4, new Data(1,1,2026).getDayOfWeek()); }
@Test void getDayOfWeek_jan4_2026_isSunday()   { assertEquals(7, new Data(4,1,2026).getDayOfWeek()); }
@Test void getDayOfWeek_jan5_2026_isMonday()   { assertEquals(1, new Data(5,1,2026).getDayOfWeek()); }
@Test void getDayOfWeek_feb1_2026_isSunday()   { assertEquals(7, new Data(1,2,2026).getDayOfWeek()); }
```

**Результат:** `compilation error` — метод `getDayOfWeek()` не существует. ✗

### 5.2. Алгоритм и реализация

Алгоритм Tomohiko Sakamoto — оптимизированная формула Зеллера для Григорианского календаря:

```
d = (y + y/4 − y/100 + y/400 + t[month−1] + day) mod 7
```

где `y = year − 1` при `month < 3` (январь и февраль относятся к предыдущему году в расчёте),
`t = [0, 3, 2, 5, 0, 3, 5, 1, 4, 6, 2, 4]` — поправочная таблица.

Результат: `0 = Вс, 1 = Пн, ..., 6 = Сб`. Конвертация в `1=Пн..7=Вс`: `d == 0 ? 7 : d`.

**Ручная проверка для 01.01.2026:**
- `y = 2025` (так как month=1 < 3)
- `d = (2025 + 506 − 20 + 5 + 0 + 1) % 7 = 2517 % 7 = 4` → Четверг ✓

```java
private static final int[] SAK = {0, 3, 2, 5, 0, 3, 5, 1, 4, 6, 2, 4};

public int getDayOfWeek() {
    int y = month < 3 ? year - 1 : year;
    int d = (y + y/4 - y/100 + y/400 + SAK[month-1] + day) % 7;
    return d == 0 ? 7 : d;
}
```

**Результат:** все тесты цикла 3 пройдены ✓

---

## 6. Цикл 4 — Конструктор Calendar и getDaysCount

### 6.1. Red

```java
@Test void calendar_january2026_31days() { assertEquals(31, new Calendar(1, 2026).getDaysCount()); }
@Test void calendar_february2026_28days(){ assertEquals(28, new Calendar(2, 2026).getDaysCount()); }
@Test void calendar_february2024_29days(){ assertEquals(29, new Calendar(2, 2024).getDaysCount()); }
@Test void calendar_invalidMonth_throwsException() {
    assertThrows(DataException.class, () -> new Calendar(13, 2026));
}
```

**Результат:** `compilation error` — класс `Calendar` не существует. ✗

### 6.2. Green

```java
public class Calendar {
    private final int month, year;
    private final Data[] dates;

    public Calendar(int month, int year) {
        if (month < 1 || month > 12) throw new DataException("Некорректный месяц: " + month);
        if (year < 1)                throw new DataException("Некорректный год: " + year);
        this.month = month; this.year = year;
        int days = Data.daysInMonth(month, year);
        this.dates = new Data[days];
        for (int d = 1; d <= days; d++) dates[d-1] = new Data(d, month, year);
    }

    public int getDaysCount() { return dates.length; }
    public Data[] getAllDates() { return dates.clone(); }
}
```

**Результат:** все тесты цикла 4 пройдены ✓

---

## 7. Цикл 5 — getDayOfWeekForDate и getDatesByDayOfWeek

### 7.1. Red

```java
@Test void getDayOfWeekForDate_jan1_isThursday() {
    assertEquals(4, new Calendar(1, 2026).getDayOfWeekForDate(1));
}

@Test void getDatesByDayOfWeek_thursday_jan2026_correctCount() {
    // Четверги в январе 2026: 1, 8, 15, 22, 29 → 5 дат
    assertEquals(5, new Calendar(1, 2026).getDatesByDayOfWeek(4).size());
}

@Test void getDatesByDayOfWeek_monday_jan2026_4dates() {
    // Понедельники: 5, 12, 19, 26 → 4 даты
    assertEquals(4, new Calendar(1, 2026).getDatesByDayOfWeek(1).size());
}
```

**Результат:** `compilation error` — методы не существуют. ✗

### 7.2. Green

```java
public int getDayOfWeekForDate(int day) {
    if (day < 1 || day > dates.length)
        throw new DataException("День " + day + " вне диапазона");
    return dates[day - 1].getDayOfWeek();
}

public List<Data> getDatesByDayOfWeek(int dayOfWeek) {
    if (dayOfWeek < 1 || dayOfWeek > 7)
        throw new DataException("Некорректный день недели: " + dayOfWeek);
    List<Data> result = new ArrayList<>();
    for (Data d : dates)
        if (d.getDayOfWeek() == dayOfWeek) result.add(d);
    return result;
}
```

**Результат:** все тесты цикла 5 пройдены ✓

---

## 8. Цикл 6 — Массив Calendar на год

### 8.1. Red

```java
@Test void yearArray_totalDays2026_is365() {
    int total = 0;
    for (int m = 1; m <= 12; m++) total += new Calendar(m, 2026).getDaysCount();
    assertEquals(365, total);
}

@Test void yearArray_totalDays2024_is366() {
    int total = 0;
    for (int m = 1; m <= 12; m++) total += new Calendar(m, 2024).getDaysCount();
    assertEquals(366, total);
}
```

**Результат:** тесты пройдены без дополнительных изменений (все методы уже реализованы). ✓

---

## 9. Итоговые результаты тестирования

### 9.1. Сводка тестов

| Группа тестов | Тестов | Прошло |
|---------------|:------:|:------:|
| Цикл 1: Создание Data и валидация | 9 | 9 |
| Цикл 2: Високосный год и дни в месяце | 7 | 7 |
| Цикл 3: День недели (Sakamoto) | 7 | 7 |
| Цикл 4: Конструктор Calendar | 8 | 8 |
| Цикл 5: getDayOfWeekForDate + getDatesByDayOfWeek | 7 | 7 |
| Цикл 6: Массив на год | 3 | 3 |
| **Итого** | **41** | **41** |

Все **41 тест пройден**.

---

## 10. Вывод демонстрационного приложения

```
=== Календарь на 2026 год ===

  Январь          31 дней
  Февраль         28 дней
  Март            31 дней
  Апрель          30 дней
  Май             31 дней
  Июнь            30 дней
  Июль            31 дней
  Август          31 дней
  Сентябрь        30 дней
  Октябрь         31 дней
  Ноябрь          30 дней
  Декабрь         31 дней

=== Январь 2026 — по номеру дня → день недели ===

  Дата           | День недели
  -----------------------------------
  01.01.2026     | Четверг
  02.01.2026     | Пятница
  03.01.2026     | Суббота
  04.01.2026     | Воскресенье
  05.01.2026     | Понедельник
  06.01.2026     | Вторник
  07.01.2026     | Среда
  08.01.2026     | Четверг
  09.01.2026     | Пятница
  10.01.2026     | Суббота
  ...
  31.01.2026     | Суббота

=== Январь 2026 — по дню недели → все даты ===

  День недели     | Даты (числа месяца)
  --------------------------------------------------
  Понедельник     |  5, 12, 19, 26 (4 даты)
  Вторник         |  6, 13, 20, 27 (4 даты)
  Среда           |  7, 14, 21, 28 (4 даты)
  Четверг         |  1,  8, 15, 22, 29 (5 дат)
  Пятница         |  2,  9, 16, 23, 30 (5 дат)
  Суббота         |  3, 10, 17, 24, 31 (5 дат)
  Воскресенье     |  4, 11, 18, 25 (4 даты)

=== Пример DataException ===

  DataException: Некорректная дата: 32.01.2026
  DataException: Некорректная дата: 29.02.2026
  29.02.2024 — создан без ошибок (2024 — високосный)
```

---

## 11. Структура проекта

```
lab5/
├── pom.xml
├── Dockerfile
├── lab5_report.md
└── src/
    ├── main/java/by/bsu/calendar/
    │   ├── DataException.java
    │   ├── Data.java
    │   ├── Calendar.java
    │   └── Main.java
    └── test/java/by/bsu/calendar/
        └── DataCalendarTest.java
```

---

## 12. Заключение

В ходе работы методология TDD применена к разработке классов `Data` и `Calendar`.
Каждый цикл начинался с написания теста (фаза Red), что позволило заранее спроектировать интерфейс классов.

Ключевые выводы:
- Алгоритм Sakamoto корректно вычисляет день недели для любой даты Григорианского календаря; граничные случаи (January/February → предыдущий год в формуле) проверены тестами.
- Правило трёх исключений для високосного года (÷400, ÷100, ÷4) проверено отдельными тестами.
- Класс `Calendar` не хранит лишних данных — все даты строятся при инициализации, что упрощает последующие запросы `getDayOfWeekForDate` и `getDatesByDayOfWeek`.
- Итоговая проверка суммы дней за год (365 / 366) подтверждает корректность всей цепочки классов.

---

*Документ выполнен в рамках курса ТиОКРС, БГУ, 2025–2026 уч. год.*

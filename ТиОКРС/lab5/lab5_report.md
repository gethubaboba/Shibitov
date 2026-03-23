# Лабораторная работа №5
# Разработка через тестирование (TDD)

**Выполнил:** Шибитов Николай
**Группа:** 11, 3 курс
**Вариант:** 8 — Последовательность
**Дисциплина:** Теория и основы конструирования разработки ПО (ТиОКРС)
**Дата:** 23.03.2026

---

## 1. Постановка задачи

**Вариант 8.**
Определить класс «Последовательность» — хранит последовательность целых чисел. Определить несколько конструкторов и методы: тип (убывающая, возрастающая, неубывающая, невозрастающая, геометрическая, арифметическая прогрессия), принадлежность элемента, равны ли две последовательности, максимум, минимум.

### Классы и методы

| Класс | Описание |
|-------|----------|
| `Sequence` | Последовательность целых чисел; методы определения типа, contains, equals, max, min |
| `SequenceException` | Unchecked-исключение при некорректных параметрах |
| `Main` | Демонстрация всех возможностей класса |

---

## 2. Методология TDD

Каждый цикл разработки: **Red** (написать падающий тест) → **Green** (минимальный код) → **Refactor**.

---

## 3. Цикл 1 — Конструктор из массива и валидация

### 3.1. Red

```java
@Test
void constructor_validArray_noException() {
    assertDoesNotThrow(() -> new Sequence(new int[]{1, 2, 3}));
}

@Test
void constructor_emptyArray_throwsException() {
    assertThrows(SequenceException.class, () -> new Sequence(new int[]{}));
}

@Test
void constructor_nullArray_throwsException() {
    assertThrows(SequenceException.class, () -> new Sequence(null));
}
```

**Результат:** `compilation error` — класс `Sequence` не существует. ✗

### 3.2. Green

```java
public class Sequence {
    private final int[] elements;

    public Sequence(int[] elements) {
        if (elements == null || elements.length == 0)
            throw new SequenceException("Последовательность не может быть пустой");
        this.elements = Arrays.copyOf(elements, elements.length);
    }

    public int size()  { return elements.length; }
    public int get(int index) {
        if (index < 0 || index >= elements.length)
            throw new SequenceException("Индекс " + index + " вне диапазона");
        return elements[index];
    }
}
```

**Результат:** все тесты цикла 1 пройдены ✓

---

## 4. Цикл 2 — Конструктор арифметической прогрессии

### 4.1. Red

```java
@Test
void arithmeticConstructor_validParams_correctElements() {
    Sequence s = new Sequence(1, 2, 5); // 1, 3, 5, 7, 9
    assertEquals(5, s.size());
    assertEquals(1, s.get(0));
    assertEquals(9, s.get(4));
}

@Test
void arithmeticConstructor_zeroCount_throwsException() {
    assertThrows(SequenceException.class, () -> new Sequence(1, 2, 0));
}
```

**Результат:** `compilation error` — конструктор `Sequence(int, int, int)` не существует. ✗

### 4.2. Green

```java
public Sequence(int first, int step, int count) {
    if (count <= 0)
        throw new SequenceException("Количество элементов должно быть положительным: " + count);
    this.elements = new int[count];
    for (int i = 0; i < count; i++) elements[i] = first + i * step;
}
```

**Результат:** все тесты цикла 2 пройдены ✓

---

## 5. Цикл 3 — Конструктор геометрической прогрессии

### 5.1. Red

```java
@Test
void geometricConstructor_validParams_correctElements() {
    Sequence s = new Sequence(1, 2.0, 5); // 1, 2, 4, 8, 16
    assertEquals(16, s.get(4));
}

@Test
void geometricConstructor_firstZero_throwsException() {
    assertThrows(SequenceException.class, () -> new Sequence(0, 2.0, 5));
}
```

**Результат:** `compilation error` — конструктор `Sequence(int, double, int)` не существует. ✗

### 5.2. Green

```java
public Sequence(int first, double ratio, int count) {
    if (count <= 0) throw new SequenceException("...");
    if (first == 0) throw new SequenceException("Первый член ГП не может быть 0");
    if (ratio == 0) throw new SequenceException("Знаменатель ГП не может быть 0");
    this.elements = new int[count];
    double current = first;
    for (int i = 0; i < count; i++) { elements[i] = (int) Math.round(current); current *= ratio; }
}
```

**Результат:** все тесты цикла 3 пройдены ✓

---

## 6. Цикл 4 — isIncreasing и isDecreasing

### 6.1. Red

```java
@Test void isIncreasing_strictlyIncreasing_true()  { assertTrue(new Sequence(new int[]{1,3,5,7}).isIncreasing()); }
@Test void isIncreasing_withEqual_false()           { assertFalse(new Sequence(new int[]{1,3,3,7}).isIncreasing()); }
@Test void isDecreasing_strictlyDecreasing_true()  { assertTrue(new Sequence(new int[]{9,5,2,-1}).isDecreasing()); }
@Test void isDecreasing_withEqual_false()          { assertFalse(new Sequence(new int[]{9,5,5,-1}).isDecreasing()); }
```

**Результат:** `compilation error` — методы не существуют. ✗

### 6.2. Green

```java
public boolean isIncreasing() {
    for (int i = 0; i < elements.length - 1; i++)
        if (elements[i] >= elements[i + 1]) return false;
    return true;
}

public boolean isDecreasing() {
    for (int i = 0; i < elements.length - 1; i++)
        if (elements[i] <= elements[i + 1]) return false;
    return true;
}
```

**Результат:** все тесты цикла 4 пройдены ✓

---

## 7. Цикл 5 — isNonDecreasing и isNonIncreasing

### 7.1. Red

```java
@Test void isNonDecreasing_withEqual_true()    { assertTrue(new Sequence(new int[]{1,2,2,3}).isNonDecreasing()); }
@Test void isNonDecreasing_withDecrease_false(){ assertFalse(new Sequence(new int[]{1,3,2}).isNonDecreasing()); }
@Test void isNonIncreasing_withEqual_true()    { assertTrue(new Sequence(new int[]{5,5,3,1}).isNonIncreasing()); }
@Test void isNonIncreasing_withIncrease_false(){ assertFalse(new Sequence(new int[]{5,3,4}).isNonIncreasing()); }
```

**Результат:** `compilation error` — методы не существуют. ✗

### 7.2. Green

```java
public boolean isNonDecreasing() {
    for (int i = 0; i < elements.length - 1; i++)
        if (elements[i] > elements[i + 1]) return false;
    return true;
}

public boolean isNonIncreasing() {
    for (int i = 0; i < elements.length - 1; i++)
        if (elements[i] < elements[i + 1]) return false;
    return true;
}
```

**Результат:** все тесты цикла 5 пройдены ✓

---

## 8. Цикл 6 — isArithmeticProgression

### 8.1. Red

```java
@Test void isAP_validAP_true()    { assertTrue(new Sequence(new int[]{2,5,8,11,14}).isArithmeticProgression()); }
@Test void isAP_notAP_false()     { assertFalse(new Sequence(new int[]{1,2,4,8}).isArithmeticProgression()); }
@Test void isAP_constant_true()   { assertTrue(new Sequence(new int[]{3,3,3}).isArithmeticProgression()); }
@Test void isAP_negativeStep_true(){ assertTrue(new Sequence(new int[]{10,7,4,1}).isArithmeticProgression()); }
```

**Результат:** `compilation error` — метод не существует. ✗

### 8.2. Реализация

Проверяем, что разность между соседними элементами постоянна. Для последовательности длиной ≤ 2 всегда `true` (два любых числа образуют АП).

```java
public boolean isArithmeticProgression() {
    if (elements.length <= 2) return true;
    int diff = elements[1] - elements[0];
    for (int i = 1; i < elements.length - 1; i++)
        if (elements[i + 1] - elements[i] != diff) return false;
    return true;
}
```

**Результат:** все тесты цикла 6 пройдены ✓

---

## 9. Цикл 7 — isGeometricProgression

### 9.1. Red

```java
@Test void isGP_validGP_true()            { assertTrue(new Sequence(new int[]{1,2,4,8,16}).isGeometricProgression()); }
@Test void isGP_fractionalRatio_true()    { assertTrue(new Sequence(new int[]{4,6,9}).isGeometricProgression()); }
@Test void isGP_firstZero_false()         { assertFalse(new Sequence(new int[]{0,0,0}).isGeometricProgression()); }
@Test void isGP_arithmeticNotGeometric_false(){ assertFalse(new Sequence(new int[]{2,4,6,8}).isGeometricProgression()); }
```

**Результат:** `compilation error` — метод не существует. ✗

### 9.2. Алгоритм и реализация

Проверка через целочисленное перемножение: знаменатель ГП равен `a[1]/a[0]` в несократимой дроби `num/den`. Для каждой пары соседних элементов проверяем `a[i+1] * den == a[i] * num` (умножение в `long` для предотвращения переполнения).

**Ручная проверка для {4, 6, 9}:**
- `num = 6, den = 4` → после сокращения: `num = 3, den = 2`
- `i=1`: `9 * 2 == 6 * 3` → `18 == 18` ✓ → ГП с q = 3/2

```java
public boolean isGeometricProgression() {
    if (elements.length <= 1) return true;
    if (elements[0] == 0) return false;
    long num = elements[1], den = elements[0];
    long g = gcd(Math.abs(num), Math.abs(den));
    num /= g; den /= g;
    for (int i = 1; i < elements.length - 1; i++) {
        if (elements[i] == 0) return false;
        if ((long) elements[i + 1] * den != (long) elements[i] * num) return false;
    }
    return true;
}
```

**Результат:** все тесты цикла 7 пройдены ✓

---

## 10. Цикл 8 — contains

### 10.1. Red

```java
@Test void contains_presentElement_true() { assertTrue(new Sequence(new int[]{3,1,4,1,5}).contains(4)); }
@Test void contains_absentElement_false()  { assertFalse(new Sequence(new int[]{3,1,4,1,5}).contains(7)); }
```

**Результат:** `compilation error` — метод не существует. ✗

### 10.2. Green

```java
public boolean contains(int element) {
    for (int e : elements) if (e == element) return true;
    return false;
}
```

**Результат:** все тесты цикла 8 пройдены ✓

---

## 11. Цикл 9 — equals

### 11.1. Red

```java
@Test void equals_identicalSequences_true() {
    assertEquals(new Sequence(new int[]{1,2,3}), new Sequence(new int[]{1,2,3}));
}
@Test void equals_differentElements_false() {
    assertNotEquals(new Sequence(new int[]{1,2,3}), new Sequence(new int[]{1,2,4}));
}
@Test void equals_arithmeticAndArrayConstructors_equal() {
    assertEquals(new Sequence(2, 3, 4), new Sequence(new int[]{2,5,8,11}));
}
```

**Результат:** `assertEquals` возвращал false — метод `equals` не переопределён. ✗

### 11.2. Green

```java
@Override
public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Sequence)) return false;
    return Arrays.equals(elements, ((Sequence) o).elements);
}
```

**Результат:** все тесты цикла 9 пройдены ✓

---

## 12. Цикл 10 — max и min

### 12.1. Red

```java
@Test void max_generalSequence_correctMax() { assertEquals(9, new Sequence(new int[]{3,1,4,1,5,9,2,6}).max()); }
@Test void min_generalSequence_correctMin() { assertEquals(1, new Sequence(new int[]{3,1,4,1,5,9,2,6}).min()); }
@Test void max_negativeNumbers_correctMax() { assertEquals(-1, new Sequence(new int[]{-5,-3,-1,-4}).max()); }
@Test void min_negativeNumbers_correctMin() { assertEquals(-5, new Sequence(new int[]{-5,-3,-1,-4}).min()); }
```

**Результат:** `compilation error` — методы не существуют. ✗

### 12.2. Green

```java
public int max() {
    int max = elements[0];
    for (int e : elements) if (e > max) max = e;
    return max;
}

public int min() {
    int min = elements[0];
    for (int e : elements) if (e < min) min = e;
    return min;
}
```

**Результат:** все тесты цикла 10 пройдены ✓

---

## 13. Итоговые результаты тестирования

### 13.1. Сводка тестов

| Группа тестов | Тестов | Прошло |
|---------------|:------:|:------:|
| Цикл 1: Конструктор из массива и валидация | 7 | 7 |
| Цикл 2: Конструктор арифметической прогрессии | 4 | 4 |
| Цикл 3: Конструктор геометрической прогрессии | 4 | 4 |
| Цикл 4: isIncreasing и isDecreasing | 7 | 7 |
| Цикл 5: isNonDecreasing и isNonIncreasing | 6 | 6 |
| Цикл 6: isArithmeticProgression | 6 | 6 |
| Цикл 7: isGeometricProgression | 7 | 7 |
| Цикл 8: contains | 4 | 4 |
| Цикл 9: equals | 5 | 5 |
| Цикл 10: max и min | 6 | 6 |
| **Итого** | **56** | **56** |

Все **56 тестов пройдены**.

---

## 14. Вывод демонстрационного приложения

```
=== Создание последовательностей ===

s1 (из массива):                   [3, 1, 4, 1, 5, 9, 2, 6]
s2 (АП: a1=2, d=3, n=6):           [2, 5, 8, 11, 14, 17]
s3 (ГП: a1=1, q=2, n=7):           [1, 2, 4, 8, 16, 32, 64]
s4 (убывающая):                    [10, 8, 5, 3, 1]
s5 (невозрастающая с повторами):   [5, 5, 3, 3, 1]

=== Типы последовательностей ===

  s1: [3, 1, 4, 1, 5, 9, 2, 6]      → произвольная
  s2: [2, 5, 8, 11, 14, 17]         → арифметическая прогрессия; возрастающая
  s3: [1, 2, 4, 8, 16, 32, 64]      → геометрическая прогрессия; возрастающая
  s4: [10, 8, 5, 3, 1]              → убывающая
  s5: [5, 5, 3, 3, 1]               → невозрастающая

=== Принадлежность элемента ===

  s1 содержит 9?  true
  s1 содержит 7?  false
  s2 содержит 14? true
  s3 содержит 32? true

=== Сравнение последовательностей ===

  s2 == s2copy (одинаковые)?  true
  s1 == s2 (разные)?          false

=== Максимум и минимум ===

  s1: max=9, min=1
  s2: max=17, min=2
  s3: max=64, min=1
  s4: max=10, min=1

=== Обработка SequenceException ===

  SequenceException: Последовательность не может быть пустой
  SequenceException: Количество элементов должно быть положительным: 0
  SequenceException: Первый член геометрической прогрессии не может быть 0
```

---

## 15. Структура проекта

```
lab5/
├── pom.xml
├── Dockerfile
├── lab5_report.md
└── src/
    ├── main/java/by/bsu/sequence/
    │   ├── SequenceException.java
    │   ├── Sequence.java
    │   └── Main.java
    └── test/java/by/bsu/sequence/
        └── SequenceTest.java
```

---

## 16. Заключение

В ходе работы методология TDD применена к разработке класса `Sequence`.
Каждый цикл начинался с написания падающего теста (фаза Red), что позволило точно спроектировать интерфейс класса до его реализации.

Ключевые выводы:
- Три конструктора покрывают основные способы создания последовательности: произвольный массив, арифметическая и геометрическая прогрессии.
- Проверка ГП через целочисленное перемножение с предварительным сокращением дроби позволяет корректно обрабатывать как целые, так и дробные знаменатели без погрешностей с плавающей точкой.
- Постоянная последовательность одновременно является АП (d=0), неубывающей и невозрастающей — метод `getType()` перечисляет все применимые типы через «; ».
- Защитные копии в конструкторе и `toArray()` гарантируют неизменяемость объекта.

---

*Документ выполнен в рамках курса ТиОКРС, БГУ, 2025–2026 уч. год.*

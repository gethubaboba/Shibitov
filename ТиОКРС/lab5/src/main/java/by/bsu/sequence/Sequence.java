package by.bsu.sequence;

import java.util.Arrays;

/**
 * Класс «Последовательность» — хранит последовательность целых чисел.
 *
 * <p>Предоставляет:
 * <ul>
 *   <li>Несколько конструкторов: из массива, арифметическая прогрессия, геометрическая прогрессия</li>
 *   <li>Определение типа: возрастающая, убывающая, неубывающая, невозрастающая,
 *       арифметическая прогрессия, геометрическая прогрессия</li>
 *   <li>Принадлежность элемента</li>
 *   <li>Сравнение двух последовательностей на равенство</li>
 *   <li>Максимум и минимум</li>
 * </ul>
 */
public class Sequence {

    private final int[] elements;

    // ---- Конструкторы ----

    /**
     * Конструктор из массива целых чисел (создаёт защитную копию).
     *
     * @param elements непустой массив элементов
     * @throws SequenceException если массив null или пустой
     */
    public Sequence(int[] elements) {
        if (elements == null || elements.length == 0) {
            throw new SequenceException("Последовательность не может быть пустой");
        }
        this.elements = Arrays.copyOf(elements, elements.length);
    }

    /**
     * Конструктор арифметической прогрессии: a₁, a₁+d, a₁+2d, …
     *
     * @param first первый член
     * @param step  разность (шаг)
     * @param count количество членов (> 0)
     * @throws SequenceException если count ≤ 0
     */
    public Sequence(int first, int step, int count) {
        if (count <= 0) {
            throw new SequenceException("Количество элементов должно быть положительным: " + count);
        }
        this.elements = new int[count];
        for (int i = 0; i < count; i++) {
            elements[i] = first + i * step;
        }
    }

    /**
     * Конструктор геометрической прогрессии: a₁, a₁·q, a₁·q², …
     *
     * @param first первый член (≠ 0)
     * @param ratio знаменатель (≠ 0)
     * @param count количество членов (> 0)
     * @throws SequenceException если first == 0, ratio == 0 или count ≤ 0
     */
    public Sequence(int first, double ratio, int count) {
        if (count <= 0) {
            throw new SequenceException("Количество элементов должно быть положительным: " + count);
        }
        if (first == 0) {
            throw new SequenceException("Первый член геометрической прогрессии не может быть 0");
        }
        if (ratio == 0) {
            throw new SequenceException("Знаменатель геометрической прогрессии не может быть 0");
        }
        this.elements = new int[count];
        double current = first;
        for (int i = 0; i < count; i++) {
            elements[i] = (int) Math.round(current);
            current *= ratio;
        }
    }

    // ---- Геттеры ----

    /** Число элементов. */
    public int size() { return elements.length; }

    /**
     * Элемент по индексу (0-based).
     *
     * @throws SequenceException если индекс вне диапазона
     */
    public int get(int index) {
        if (index < 0 || index >= elements.length) {
            throw new SequenceException(
                    "Индекс " + index + " вне диапазона [0, " + (elements.length - 1) + "]");
        }
        return elements[index];
    }

    /** Возвращает защитную копию массива. */
    public int[] toArray() { return Arrays.copyOf(elements, elements.length); }

    // ---- Методы определения типа ----

    /**
     * Строго возрастающая: a[i] < a[i+1] для всех i.
     */
    public boolean isIncreasing() {
        for (int i = 0; i < elements.length - 1; i++) {
            if (elements[i] >= elements[i + 1]) return false;
        }
        return true;
    }

    /**
     * Строго убывающая: a[i] > a[i+1] для всех i.
     */
    public boolean isDecreasing() {
        for (int i = 0; i < elements.length - 1; i++) {
            if (elements[i] <= elements[i + 1]) return false;
        }
        return true;
    }

    /**
     * Неубывающая: a[i] ≤ a[i+1] для всех i.
     */
    public boolean isNonDecreasing() {
        for (int i = 0; i < elements.length - 1; i++) {
            if (elements[i] > elements[i + 1]) return false;
        }
        return true;
    }

    /**
     * Невозрастающая: a[i] ≥ a[i+1] для всех i.
     */
    public boolean isNonIncreasing() {
        for (int i = 0; i < elements.length - 1; i++) {
            if (elements[i] < elements[i + 1]) return false;
        }
        return true;
    }

    /**
     * Арифметическая прогрессия: разность между соседними элементами постоянна.
     */
    public boolean isArithmeticProgression() {
        if (elements.length <= 2) return true;
        int diff = elements[1] - elements[0];
        for (int i = 1; i < elements.length - 1; i++) {
            if (elements[i + 1] - elements[i] != diff) return false;
        }
        return true;
    }

    /**
     * Геометрическая прогрессия: отношение соседних элементов постоянно.
     *
     * <p>Проверка через целочисленное перемножение во избежание погрешностей с плавающей точкой:
     * a[i+1]/a[i] = a[1]/a[0] ⟺ a[i+1]·a[0] = a[i]·a[1] (при сокращённой дроби a[1]/a[0]).
     *
     * @return false, если первый элемент равен 0 или хотя бы один промежуточный элемент равен 0
     */
    public boolean isGeometricProgression() {
        if (elements.length <= 1) return true;
        if (elements[0] == 0) return false;

        // Сокращаем дробь a[1]/a[0]
        long num = elements[1];
        long den = elements[0];
        long g = gcd(Math.abs(num), Math.abs(den));
        num /= g;
        den /= g;

        for (int i = 1; i < elements.length - 1; i++) {
            if (elements[i] == 0) return false;
            // Проверяем: elements[i+1] * den == elements[i] * num
            if ((long) elements[i + 1] * den != (long) elements[i] * num) return false;
        }
        return true;
    }

    private static long gcd(long a, long b) {
        return b == 0 ? a : gcd(b, a % b);
    }

    /**
     * Возвращает текстовое описание типов последовательности через «; ».
     * Если ни один тип не подходит — возвращает «произвольная».
     */
    public String getType() {
        StringBuilder sb = new StringBuilder();
        if (isArithmeticProgression()) appendType(sb, "арифметическая прогрессия");
        if (isGeometricProgression())  appendType(sb, "геометрическая прогрессия");
        if (isIncreasing())            appendType(sb, "возрастающая");
        else if (isDecreasing())       appendType(sb, "убывающая");
        else if (isNonDecreasing())    appendType(sb, "неубывающая");
        else if (isNonIncreasing())    appendType(sb, "невозрастающая");
        return sb.length() == 0 ? "произвольная" : sb.toString();
    }

    private static void appendType(StringBuilder sb, String type) {
        if (sb.length() > 0) sb.append("; ");
        sb.append(type);
    }

    // ---- Принадлежность и сравнение ----

    /**
     * Возвращает true, если элемент присутствует в последовательности.
     */
    public boolean contains(int element) {
        for (int e : elements) {
            if (e == element) return true;
        }
        return false;
    }

    // ---- Максимум / минимум ----

    /** Наибольший элемент последовательности. */
    public int max() {
        int max = elements[0];
        for (int e : elements) {
            if (e > max) max = e;
        }
        return max;
    }

    /** Наименьший элемент последовательности. */
    public int min() {
        int min = elements[0];
        for (int e : elements) {
            if (e < min) min = e;
        }
        return min;
    }

    // ---- Стандартные методы ----

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Sequence)) return false;
        return Arrays.equals(elements, ((Sequence) o).elements);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(elements);
    }

    @Override
    public String toString() {
        return Arrays.toString(elements);
    }
}

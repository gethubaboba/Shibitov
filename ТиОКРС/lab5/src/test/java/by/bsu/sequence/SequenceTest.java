package by.bsu.sequence;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JUnit 5 тесты для класса Sequence.
 *
 * Разработаны по методике TDD: каждый тест написан ДО реализации
 * соответствующего метода (фаза Red), затем реализован минимальный код
 * для его прохождения (фаза Green).
 */
class SequenceTest {

    // =========================================================
    // Цикл 1: Конструктор из массива и валидация
    // =========================================================

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

    @Test
    void constructor_singleElement_sizeOne() {
        assertEquals(1, new Sequence(new int[]{42}).size());
    }

    @Test
    void get_validIndex_returnsElement() {
        Sequence s = new Sequence(new int[]{10, 20, 30});
        assertEquals(10, s.get(0));
        assertEquals(20, s.get(1));
        assertEquals(30, s.get(2));
    }

    @Test
    void get_outOfRange_throwsException() {
        Sequence s = new Sequence(new int[]{1, 2, 3});
        assertThrows(SequenceException.class, () -> s.get(-1));
        assertThrows(SequenceException.class, () -> s.get(3));
    }

    @Test
    void toArray_returnsDefensiveCopy() {
        int[] arr = {1, 2, 3};
        Sequence s = new Sequence(arr);
        int[] copy = s.toArray();
        copy[0] = 999;
        assertEquals(1, s.get(0)); // оригинал не изменился
    }

    // =========================================================
    // Цикл 2: Конструктор арифметической прогрессии
    // =========================================================

    @Test
    void arithmeticConstructor_validParams_correctElements() {
        Sequence s = new Sequence(1, 2, 5); // 1, 3, 5, 7, 9
        assertEquals(5, s.size());
        assertEquals(1, s.get(0));
        assertEquals(3, s.get(1));
        assertEquals(9, s.get(4));
    }

    @Test
    void arithmeticConstructor_negativeStep_correctElements() {
        Sequence s = new Sequence(10, -3, 4); // 10, 7, 4, 1
        assertEquals(10, s.get(0));
        assertEquals(7,  s.get(1));
        assertEquals(1,  s.get(3));
    }

    @Test
    void arithmeticConstructor_zeroCount_throwsException() {
        assertThrows(SequenceException.class, () -> new Sequence(1, 2, 0));
    }

    @Test
    void arithmeticConstructor_negativeCount_throwsException() {
        assertThrows(SequenceException.class, () -> new Sequence(1, 2, -1));
    }

    // =========================================================
    // Цикл 3: Конструктор геометрической прогрессии
    // =========================================================

    @Test
    void geometricConstructor_validParams_correctElements() {
        Sequence s = new Sequence(1, 2.0, 5); // 1, 2, 4, 8, 16
        assertEquals(5,  s.size());
        assertEquals(1,  s.get(0));
        assertEquals(2,  s.get(1));
        assertEquals(4,  s.get(2));
        assertEquals(16, s.get(4));
    }

    @Test
    void geometricConstructor_firstZero_throwsException() {
        assertThrows(SequenceException.class, () -> new Sequence(0, 2.0, 5));
    }

    @Test
    void geometricConstructor_ratioZero_throwsException() {
        assertThrows(SequenceException.class, () -> new Sequence(1, 0.0, 5));
    }

    @Test
    void geometricConstructor_zeroCount_throwsException() {
        assertThrows(SequenceException.class, () -> new Sequence(1, 2.0, 0));
    }

    // =========================================================
    // Цикл 4: isIncreasing и isDecreasing
    // =========================================================

    @Test
    void isIncreasing_strictlyIncreasing_true() {
        assertTrue(new Sequence(new int[]{1, 3, 5, 7}).isIncreasing());
    }

    @Test
    void isIncreasing_withEqual_false() {
        assertFalse(new Sequence(new int[]{1, 3, 3, 7}).isIncreasing());
    }

    @Test
    void isIncreasing_decreasing_false() {
        assertFalse(new Sequence(new int[]{5, 3, 1}).isIncreasing());
    }

    @Test
    void isIncreasing_singleElement_true() {
        assertTrue(new Sequence(new int[]{42}).isIncreasing());
    }

    @Test
    void isDecreasing_strictlyDecreasing_true() {
        assertTrue(new Sequence(new int[]{9, 5, 2, -1}).isDecreasing());
    }

    @Test
    void isDecreasing_withEqual_false() {
        assertFalse(new Sequence(new int[]{9, 5, 5, -1}).isDecreasing());
    }

    @Test
    void isDecreasing_increasing_false() {
        assertFalse(new Sequence(new int[]{1, 2, 3}).isDecreasing());
    }

    // =========================================================
    // Цикл 5: isNonDecreasing и isNonIncreasing
    // =========================================================

    @Test
    void isNonDecreasing_withEqual_true() {
        assertTrue(new Sequence(new int[]{1, 2, 2, 3}).isNonDecreasing());
    }

    @Test
    void isNonDecreasing_strictlyIncreasing_true() {
        assertTrue(new Sequence(new int[]{1, 2, 3}).isNonDecreasing());
    }

    @Test
    void isNonDecreasing_withDecrease_false() {
        assertFalse(new Sequence(new int[]{1, 3, 2}).isNonDecreasing());
    }

    @Test
    void isNonIncreasing_withEqual_true() {
        assertTrue(new Sequence(new int[]{5, 5, 3, 3, 1}).isNonIncreasing());
    }

    @Test
    void isNonIncreasing_strictlyDecreasing_true() {
        assertTrue(new Sequence(new int[]{5, 3, 1}).isNonIncreasing());
    }

    @Test
    void isNonIncreasing_withIncrease_false() {
        assertFalse(new Sequence(new int[]{5, 3, 4}).isNonIncreasing());
    }

    // =========================================================
    // Цикл 6: isArithmeticProgression
    // =========================================================

    @Test
    void isAP_validAP_true() {
        assertTrue(new Sequence(new int[]{2, 5, 8, 11, 14}).isArithmeticProgression());
    }

    @Test
    void isAP_constantSequence_true() {
        // Постоянная последовательность — частный случай АП с d=0
        assertTrue(new Sequence(new int[]{3, 3, 3, 3}).isArithmeticProgression());
    }

    @Test
    void isAP_negativeStep_true() {
        assertTrue(new Sequence(new int[]{10, 7, 4, 1}).isArithmeticProgression());
    }

    @Test
    void isAP_twoElements_true() {
        assertTrue(new Sequence(new int[]{5, 10}).isArithmeticProgression());
    }

    @Test
    void isAP_singleElement_true() {
        assertTrue(new Sequence(new int[]{7}).isArithmeticProgression());
    }

    @Test
    void isAP_notAP_false() {
        assertFalse(new Sequence(new int[]{1, 2, 4, 8}).isArithmeticProgression());
    }

    // =========================================================
    // Цикл 7: isGeometricProgression
    // =========================================================

    @Test
    void isGP_validGP_true() {
        assertTrue(new Sequence(new int[]{1, 2, 4, 8, 16}).isGeometricProgression());
    }

    @Test
    void isGP_ratioThree_true() {
        assertTrue(new Sequence(new int[]{2, 6, 18, 54}).isGeometricProgression());
    }

    @Test
    void isGP_fractionalRatio_true() {
        // 4, 6, 9 — ГП с q = 3/2
        assertTrue(new Sequence(new int[]{4, 6, 9}).isGeometricProgression());
    }

    @Test
    void isGP_singleElement_true() {
        assertTrue(new Sequence(new int[]{5}).isGeometricProgression());
    }

    @Test
    void isGP_firstZero_false() {
        assertFalse(new Sequence(new int[]{0, 0, 0}).isGeometricProgression());
    }

    @Test
    void isGP_notGP_false() {
        assertFalse(new Sequence(new int[]{1, 2, 3, 4}).isGeometricProgression());
    }

    @Test
    void isGP_arithmeticNotGeometric_false() {
        assertFalse(new Sequence(new int[]{2, 4, 6, 8}).isGeometricProgression());
    }

    // =========================================================
    // Цикл 8: contains
    // =========================================================

    @Test
    void contains_presentElement_true() {
        assertTrue(new Sequence(new int[]{3, 1, 4, 1, 5}).contains(4));
    }

    @Test
    void contains_absentElement_false() {
        assertFalse(new Sequence(new int[]{3, 1, 4, 1, 5}).contains(7));
    }

    @Test
    void contains_firstElement_true() {
        assertTrue(new Sequence(new int[]{10, 20, 30}).contains(10));
    }

    @Test
    void contains_lastElement_true() {
        assertTrue(new Sequence(new int[]{10, 20, 30}).contains(30));
    }

    // =========================================================
    // Цикл 9: equals
    // =========================================================

    @Test
    void equals_identicalSequences_true() {
        Sequence s1 = new Sequence(new int[]{1, 2, 3});
        Sequence s2 = new Sequence(new int[]{1, 2, 3});
        assertEquals(s1, s2);
    }

    @Test
    void equals_differentElements_false() {
        assertNotEquals(new Sequence(new int[]{1, 2, 3}),
                        new Sequence(new int[]{1, 2, 4}));
    }

    @Test
    void equals_differentLengths_false() {
        assertNotEquals(new Sequence(new int[]{1, 2, 3}),
                        new Sequence(new int[]{1, 2}));
    }

    @Test
    void equals_sameInstance_true() {
        Sequence s = new Sequence(new int[]{5, 10});
        assertEquals(s, s);
    }

    @Test
    void equals_arithmeticAndArrayConstructors_equal() {
        // new Sequence(2, 3, 4) → {2, 5, 8, 11}
        Sequence fromAP  = new Sequence(2, 3, 4);
        Sequence fromArr = new Sequence(new int[]{2, 5, 8, 11});
        assertEquals(fromAP, fromArr);
    }

    // =========================================================
    // Цикл 10: max и min
    // =========================================================

    @Test
    void max_generalSequence_correctMax() {
        assertEquals(9, new Sequence(new int[]{3, 1, 4, 1, 5, 9, 2, 6}).max());
    }

    @Test
    void min_generalSequence_correctMin() {
        assertEquals(1, new Sequence(new int[]{3, 1, 4, 1, 5, 9, 2, 6}).min());
    }

    @Test
    void max_singleElement_returnsThatElement() {
        assertEquals(42, new Sequence(new int[]{42}).max());
    }

    @Test
    void min_singleElement_returnsThatElement() {
        assertEquals(42, new Sequence(new int[]{42}).min());
    }

    @Test
    void max_negativeNumbers_correctMax() {
        assertEquals(-1, new Sequence(new int[]{-5, -3, -1, -4}).max());
    }

    @Test
    void min_negativeNumbers_correctMin() {
        assertEquals(-5, new Sequence(new int[]{-5, -3, -1, -4}).min());
    }
}

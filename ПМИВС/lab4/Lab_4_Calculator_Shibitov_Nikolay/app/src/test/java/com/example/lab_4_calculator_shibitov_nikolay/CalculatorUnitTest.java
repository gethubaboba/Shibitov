package com.example.lab_4_calculator_shibitov_nikolay;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Task 2 — Unit-тесты бизнес-логики калькулятора (JVM, без эмулятора).
 * 21 тест: базовые операции, граничные значения, исключения.
 */
public class CalculatorUnitTest {

    private static final double DELTA = 1e-9;
    private Calculator calc;

    @Before
    public void setUp() {
        calc = new Calculator();
    }

    // --- Addition ---
    @Test public void add_positive()          { assertEquals(5.0,  calc.add(2, 3),     DELTA); }
    @Test public void add_negative()          { assertEquals(-1.0, calc.add(-3, 2),    DELTA); }
    @Test public void add_zeros()             { assertEquals(0.0,  calc.add(0, 0),     DELTA); }
    @Test public void add_largeNumbers()      { assertEquals(2e15, calc.add(1e15,1e15),DELTA); }

    // --- Subtraction ---
    @Test public void sub_positive()          { assertEquals(1.0,  calc.sub(3, 2),     DELTA); }
    @Test public void sub_negative()          { assertEquals(-5.0, calc.sub(-2, 3),    DELTA); }
    @Test public void sub_sameValues()        { assertEquals(0.0,  calc.sub(7, 7),     DELTA); }

    // --- Multiplication ---
    @Test public void mul_positive()          { assertEquals(6.0,  calc.mul(2, 3),     DELTA); }
    @Test public void mul_byZero()            { assertEquals(0.0,  calc.mul(99, 0),    DELTA); }
    @Test public void mul_negatives()         { assertEquals(6.0,  calc.mul(-2, -3),   DELTA); }
    @Test public void mul_fraction()          { assertEquals(0.5,  calc.mul(0.25, 2),  DELTA); }

    // --- Division ---
    @Test public void div_normal()            { assertEquals(2.0,  calc.div(6, 3),     DELTA); }
    @Test public void div_fraction()          { assertEquals(0.5,  calc.div(1, 2),     DELTA); }
    @Test public void div_negatives()         { assertEquals(-3.0, calc.div(-6, 2),    DELTA); }
    @Test public void div_byZero_throws()     {
        assertThrows(ArithmeticException.class, () -> calc.div(5, 0));
    }

    // --- Percentage ---
    @Test public void percentage_100()        { assertEquals(1.0,  calc.percentage(100), DELTA); }
    @Test public void percentage_50()         { assertEquals(0.5,  calc.percentage(50),  DELTA); }

    // --- Negate ---
    @Test public void negate_positive()       { assertEquals(-5.0, calc.negate(5),  DELTA); }
    @Test public void negate_negative()       { assertEquals(3.0,  calc.negate(-3), DELTA); }
    @Test public void negate_zero()           { assertEquals(0.0,  calc.negate(0),  DELTA); }

    // --- evaluate() ---
    @Test public void evaluate_unknownOp_throws() {
        assertThrows(IllegalArgumentException.class,
                () -> calc.evaluate(1, "^", 2));
    }
}

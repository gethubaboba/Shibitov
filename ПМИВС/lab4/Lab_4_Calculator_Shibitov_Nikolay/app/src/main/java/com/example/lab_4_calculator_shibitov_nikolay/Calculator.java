package com.example.lab_4_calculator_shibitov_nikolay;

/**
 * Класс бизнес-логики калькулятора (Java).
 * Не содержит зависимостей от Android SDK — тестируется на JVM без эмулятора.
 */
public class Calculator {

    public double add(double a, double b) {
        return a + b;
    }

    public double sub(double a, double b) {
        return a - b;
    }

    public double mul(double a, double b) {
        return a * b;
    }

    public double div(double a, double b) {
        if (b == 0.0) throw new ArithmeticException("Division by zero");
        return a / b;
    }

    public double percentage(double value) {
        return value / 100.0;
    }

    public double negate(double a) {
        return -a;
    }

    /**
     * Вычисляет выражение "a оператор b".
     * @throws ArithmeticException  при делении на ноль
     * @throws IllegalArgumentException при неизвестном операторе
     */
    public double evaluate(double a, String operator, double b) {
        switch (operator) {
            case "+": return add(a, b);
            case "-": return sub(a, b);
            case "×": return mul(a, b);
            case "÷": return div(a, b);
            default:  throw new IllegalArgumentException("Unknown operator: " + operator);
        }
    }
}

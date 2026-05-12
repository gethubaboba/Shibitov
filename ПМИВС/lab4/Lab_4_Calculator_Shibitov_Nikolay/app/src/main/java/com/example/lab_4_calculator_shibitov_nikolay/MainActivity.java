package com.example.lab_4_calculator_shibitov_nikolay;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

/**
 * Главная Activity калькулятора.
 * Состояние сохраняется при повороте экрана через onSaveInstanceState / onCreate.
 */
public class MainActivity extends AppCompatActivity {

    private TextView tvExpression;
    private TextView tvResult;

    private final Calculator calculator = new Calculator();

    // Состояние, переживающее поворот экрана
    private String expression   = "";
    private String currentInput = "";
    private double firstOperand = 0.0;
    private String currentOperator = "";
    private boolean expectNewInput = false;

    private static final String KEY_EXPRESSION    = "expression";
    private static final String KEY_INPUT         = "currentInput";
    private static final String KEY_FIRST_OPERAND = "firstOperand";
    private static final String KEY_OPERATOR      = "operator";
    private static final String KEY_EXPECT_NEW    = "expectNewInput";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvExpression = findViewById(R.id.tv_expression);
        tvResult     = findViewById(R.id.tv_result);

        if (savedInstanceState != null) {
            expression      = savedInstanceState.getString(KEY_EXPRESSION, "");
            currentInput    = savedInstanceState.getString(KEY_INPUT, "");
            firstOperand    = savedInstanceState.getDouble(KEY_FIRST_OPERAND, 0.0);
            currentOperator = savedInstanceState.getString(KEY_OPERATOR, "");
            expectNewInput  = savedInstanceState.getBoolean(KEY_EXPECT_NEW, false);
            updateDisplay();
        }

        setupButtons();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(KEY_EXPRESSION,    expression);
        outState.putString(KEY_INPUT,         currentInput);
        outState.putDouble(KEY_FIRST_OPERAND, firstOperand);
        outState.putString(KEY_OPERATOR,      currentOperator);
        outState.putBoolean(KEY_EXPECT_NEW,   expectNewInput);
    }

    private void setupButtons() {
        int[] digitIds  = { R.id.btn_0,R.id.btn_1,R.id.btn_2,R.id.btn_3,R.id.btn_4,
                            R.id.btn_5,R.id.btn_6,R.id.btn_7,R.id.btn_8,R.id.btn_9 };
        String[] digits = { "0","1","2","3","4","5","6","7","8","9" };
        for (int i = 0; i < digitIds.length; i++) {
            final String d = digits[i];
            ((Button) findViewById(digitIds[i])).setOnClickListener(v -> onDigit(d));
        }

        ((Button) findViewById(R.id.btn_plus))    .setOnClickListener(v -> onOperator("+"));
        ((Button) findViewById(R.id.btn_minus))   .setOnClickListener(v -> onOperator("-"));
        ((Button) findViewById(R.id.btn_multiply)).setOnClickListener(v -> onOperator("×"));
        ((Button) findViewById(R.id.btn_divide))  .setOnClickListener(v -> onOperator("÷"));
        ((Button) findViewById(R.id.btn_equals))  .setOnClickListener(v -> onEquals());
        ((Button) findViewById(R.id.btn_clear))   .setOnClickListener(v -> onClear());
        ((Button) findViewById(R.id.btn_dot))     .setOnClickListener(v -> onDot());
        ((Button) findViewById(R.id.btn_negate))  .setOnClickListener(v -> onNegate());
        ((Button) findViewById(R.id.btn_percent)) .setOnClickListener(v -> onPercent());
        ((Button) findViewById(R.id.btn_backspace)).setOnClickListener(v -> onBackspace());
    }

    private void onDigit(String digit) {
        if (expectNewInput) { currentInput = ""; expectNewInput = false; }
        currentInput += digit;
        expression   += digit;
        updateDisplay();
    }

    private void onDot() {
        if (expectNewInput) { currentInput = "0"; expectNewInput = false; }
        if (!currentInput.contains(".")) {
            if (currentInput.isEmpty()) currentInput = "0";
            currentInput += ".";
            expression   += ".";
            updateDisplay();
        }
    }

    private void onOperator(String op) {
        if (currentInput.isEmpty() && currentOperator.isEmpty()) return;
        if (!currentInput.isEmpty()) {
            try { firstOperand = Double.parseDouble(currentInput); }
            catch (NumberFormatException e) { return; }
        }
        currentOperator = op;
        expression += " " + op + " ";
        expectNewInput = true;
        currentInput   = "";
        updateDisplay();
    }

    private void onEquals() {
        if (currentOperator.isEmpty() || currentInput.isEmpty()) return;
        double second;
        try { second = Double.parseDouble(currentInput); }
        catch (NumberFormatException e) { return; }
        try {
            double result = calculator.evaluate(firstOperand, currentOperator, second);
            String formatted = (result == (long) result)
                    ? String.valueOf((long) result)
                    : String.valueOf(result);
            expression += " = " + formatted;
            tvExpression.setText(expression);
            tvResult.setText(formatted);
            currentInput    = formatted;
            currentOperator = "";
            firstOperand    = result;
            expression      = formatted;
            expectNewInput  = true;
        } catch (ArithmeticException e) {
            tvResult.setText(getString(R.string.error_division_by_zero));
            expression = ""; currentInput = ""; currentOperator = ""; expectNewInput = true;
        }
    }

    private void onClear() {
        expression = ""; currentInput = ""; currentOperator = "";
        firstOperand = 0.0; expectNewInput = false;
        tvExpression.setText("");
        tvResult.setText("0");
    }

    private void onNegate() {
        try {
            double v = Double.parseDouble(currentInput);
            currentInput = formatResult(calculator.negate(v));
            tvResult.setText(currentInput);
        } catch (NumberFormatException ignored) {}
    }

    private void onPercent() {
        try {
            double v = Double.parseDouble(currentInput);
            currentInput = String.valueOf(calculator.percentage(v));
            tvResult.setText(currentInput);
        } catch (NumberFormatException ignored) {}
    }

    private void onBackspace() {
        if (!currentInput.isEmpty()) {
            currentInput = currentInput.substring(0, currentInput.length() - 1);
            if (!expression.isEmpty()) expression = expression.substring(0, expression.length() - 1);
            updateDisplay();
        }
    }

    private void updateDisplay() {
        tvExpression.setText(expression);
        tvResult.setText(currentInput.isEmpty() ? "0" : currentInput);
    }

    private String formatResult(double v) {
        return (v == (long) v) ? String.valueOf((long) v) : String.valueOf(v);
    }
}

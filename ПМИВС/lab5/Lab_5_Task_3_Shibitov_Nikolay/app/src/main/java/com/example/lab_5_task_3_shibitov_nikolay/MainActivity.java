package com.example.lab_5_task_3_shibitov_nikolay;

import android.app.AlertDialog;
import android.gesture.Gesture;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.gesture.GestureOverlayView;
import android.gesture.Prediction;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Random;

public class MainActivity extends AppCompatActivity
        implements GestureOverlayView.OnGesturePerformedListener {

    private GestureLibrary gestureLibrary;
    private TextView tvPrompt;
    private TextView tvInput;
    private TextView tvResult;
    private StringBuilder inputBuffer = new StringBuilder();
    private int secretNumber;
    private boolean gameActive = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvPrompt = findViewById(R.id.tvPrompt);
        tvInput  = findViewById(R.id.tvInput);
        tvResult = findViewById(R.id.tvResult);

        // Load gesture library from raw resources
        gestureLibrary = GestureLibraries.fromRawResource(this, R.raw.gestures);
        if (!gestureLibrary.load()) {
            Toast.makeText(this, "Не удалось загрузить жесты", Toast.LENGTH_SHORT).show();
            Log.e("Gestures", "Error loading gestures from R.raw.gestures");
        }

        GestureOverlayView gestureOverlayView = findViewById(R.id.gestureOverlay);
        gestureOverlayView.addOnGesturePerformedListener(this);

        Button btnStart = findViewById(R.id.btnStart);
        btnStart.setOnClickListener(v -> startNewGame());

        Button btnClear = findViewById(R.id.btnClear);
        btnClear.setOnClickListener(v -> {
            inputBuffer.setLength(0);
            tvInput.setText("Число: ");
        });

        Button btnCheck = findViewById(R.id.btnCheck);
        btnCheck.setOnClickListener(v -> checkGuess());
    }

    private void startNewGame() {
        secretNumber = new Random().nextInt(100) + 1;
        gameActive = true;
        inputBuffer.setLength(0);
        tvPrompt.setText("Угадайте число от 1 до 100");
        tvInput.setText("Число: ");
        tvResult.setText("");
        Toast.makeText(this, "Игра началась!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onGesturePerformed(GestureOverlayView overlay, Gesture gesture) {
        if (!gameActive) {
            Toast.makeText(this, "Нажмите «Старт»", Toast.LENGTH_SHORT).show();
            return;
        }

        ArrayList<Prediction> predictions = gestureLibrary.recognize(gesture);
        // Снижаем порог до 1.0 для лучшего распознавания
        if (!predictions.isEmpty() && predictions.get(0).score >= 1.0) {
            String name = predictions.get(0).name;
            if (name.matches("[0-9]")) {
                inputBuffer.append(name);
                tvInput.setText("Число: " + inputBuffer);
            }
        } else {
            Toast.makeText(this, "Жест не понятен", Toast.LENGTH_SHORT).show();
        }
    }

    private void checkGuess() {
        if (!gameActive) {
            Toast.makeText(this, "Начните новую игру", Toast.LENGTH_SHORT).show();
            return;
        }
        if (inputBuffer.length() == 0) {
            Toast.makeText(this, "Сначала введите число жестами", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            int guess = Integer.parseInt(inputBuffer.toString());
            inputBuffer.setLength(0);
            tvInput.setText("Число: ");

            if (guess < secretNumber) {
                tvResult.setText(guess + " — слишком мало");
            } else if (guess > secretNumber) {
                tvResult.setText(guess + " — слишком много");
            } else {
                gameActive = false;
                new AlertDialog.Builder(this)
                        .setTitle("Победа!")
                        .setMessage("Вы угадали число " + secretNumber + "!")
                        .setPositiveButton("Играть еще", (d, w) -> startNewGame())
                        .show();
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Ошибка формата", Toast.LENGTH_SHORT).show();
            inputBuffer.setLength(0);
        }
    }
}

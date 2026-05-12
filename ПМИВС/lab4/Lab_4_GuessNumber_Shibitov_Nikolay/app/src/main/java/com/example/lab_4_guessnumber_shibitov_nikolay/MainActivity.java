package com.example.lab_4_guessnumber_shibitov_nikolay;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

public class MainActivity extends AppCompatActivity {

    private GameViewModel viewModel;
    private EditText etGuess;
    private TextView tvResult;
    private TextView tvAttempts;
    private Button   btnGuess;
    private Button   btnNewGame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etGuess    = findViewById(R.id.et_guess);
        tvResult   = findViewById(R.id.tv_result);
        tvAttempts = findViewById(R.id.tv_attempts);
        btnGuess   = findViewById(R.id.btn_guess);
        btnNewGame = findViewById(R.id.btn_new_game);

        viewModel = new ViewModelProvider(this).get(GameViewModel.class);

        viewModel.getResultText().observe(this,   text -> tvResult.setText(text));
        viewModel.getAttemptsText().observe(this, text -> tvAttempts.setText(text));
        viewModel.getIsGameOver().observe(this, gameOver -> {
            btnGuess.setEnabled(!gameOver);
            btnNewGame.setEnabled(gameOver);
        });

        btnGuess.setOnClickListener(v -> {
            String input = etGuess.getText().toString();
            viewModel.makeGuess(input);
            etGuess.setText("");
        });

        btnNewGame.setOnClickListener(v -> {
            viewModel.startNewGame();
            etGuess.setText("");
        });
    }
}

package com.example.lab_4_guessnumber_shibitov_nikolay;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class GameViewModel extends ViewModel {

    private static final int MIN_VALUE   = 1;
    private static final int MAX_VALUE   = 100;
    private static final int MAX_ATTEMPTS = 7;

    private NumberGame game;

    private final MutableLiveData<String> resultText   = new MutableLiveData<>("");
    private final MutableLiveData<String> attemptsText = new MutableLiveData<>("");
    private final MutableLiveData<Boolean> isGameOver  = new MutableLiveData<>(false);

    public GameViewModel() {
        startNewGame();
    }

    public void startNewGame() {
        game = new NumberGame(new RandomNumberGenerator(), MIN_VALUE, MAX_VALUE, MAX_ATTEMPTS);
        resultText.setValue("");
        attemptsText.setValue("0 / " + MAX_ATTEMPTS);
        isGameOver.setValue(false);
    }

    public void makeGuess(String input) {
        GuessResult result = game.guess(input);
        attemptsText.setValue(game.getAttempts() + " / " + MAX_ATTEMPTS);

        switch (result) {
            case TOO_LOW:
                resultText.setValue("Больше!");
                break;
            case TOO_HIGH:
                resultText.setValue("Меньше!");
                break;
            case CORRECT:
                resultText.setValue("Верно! Число: " + game.getSecretNumber()
                        + "  Попытки: " + game.getAttempts());
                isGameOver.setValue(true);
                break;
            case GAME_OVER:
                resultText.setValue("Игра окончена. Загаданное число: " + game.getSecretNumber());
                isGameOver.setValue(true);
                break;
            case INVALID_INPUT:
                resultText.setValue("Введите число от " + MIN_VALUE + " до " + MAX_VALUE);
                break;
        }
    }

    public LiveData<String>  getResultText()   { return resultText; }
    public LiveData<String>  getAttemptsText() { return attemptsText; }
    public LiveData<Boolean> getIsGameOver()   { return isGameOver; }
}

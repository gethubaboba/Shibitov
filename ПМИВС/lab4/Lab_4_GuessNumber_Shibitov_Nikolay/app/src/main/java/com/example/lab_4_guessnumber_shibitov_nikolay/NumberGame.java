package com.example.lab_4_guessnumber_shibitov_nikolay;

/**
 * Бизнес-логика игры «Угадай число».
 * Не содержит зависимостей от Android SDK — тестируется на JVM.
 */
public class NumberGame {

    private final int minValue;
    private final int maxValue;
    private final int maxAttempts;

    private final int secretNumber;
    private int attempts = 0;
    private boolean gameOver = false;

    public NumberGame(NumberGenerator generator, int minValue, int maxValue, int maxAttempts) {
        this.minValue    = minValue;
        this.maxValue    = maxValue;
        this.maxAttempts = maxAttempts;
        this.secretNumber = generator.generate(minValue, maxValue);
    }

    public GuessResult guess(String input) {
        if (gameOver) return GuessResult.GAME_OVER;

        int number;
        try {
            number = Integer.parseInt(input.trim());
        } catch (NumberFormatException e) {
            return GuessResult.INVALID_INPUT;
        }

        if (number < minValue || number > maxValue) {
            return GuessResult.INVALID_INPUT;
        }

        attempts++;

        if (number == secretNumber) {
            gameOver = true;
            return GuessResult.CORRECT;
        }

        if (attempts >= maxAttempts) {
            gameOver = true;
            return GuessResult.GAME_OVER;
        }

        return number < secretNumber ? GuessResult.TOO_LOW : GuessResult.TOO_HIGH;
    }

    public int getAttempts()    { return attempts; }
    public int getMaxAttempts() { return maxAttempts; }
    public boolean isGameOver() { return gameOver; }
    public int getSecretNumber(){ return secretNumber; }
}

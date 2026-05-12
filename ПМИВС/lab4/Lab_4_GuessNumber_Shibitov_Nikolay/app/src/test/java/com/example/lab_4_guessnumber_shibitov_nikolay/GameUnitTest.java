package com.example.lab_4_guessnumber_shibitov_nikolay;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

/**
 * Tasks 5 & 8 — Unit-тесты игровой логики с Mockito.
 * 17 тестов: mock NumberGenerator, stub, spy, verify, edge cases.
 */
public class GameUnitTest {

    @Mock NumberGenerator mockGenerator;

    private NumberGame game;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        when(mockGenerator.generate(anyInt(), anyInt())).thenReturn(42);
        game = new NumberGame(mockGenerator, 1, 100, 7);
    }

    // --- Mockito: verify generator called exactly once during construction ---
    @Test
    public void verify_generatorCalledOnce() {
        verify(mockGenerator, times(1)).generate(1, 100);
    }

    // --- Basic guess results ---
    @Test public void guess_correct()    { assertEquals(GuessResult.CORRECT,  game.guess("42")); }
    @Test public void guess_tooLow()     { assertEquals(GuessResult.TOO_LOW,  game.guess("10")); }
    @Test public void guess_tooHigh()    { assertEquals(GuessResult.TOO_HIGH, game.guess("99")); }

    // --- isGameOver after correct guess ---
    @Test
    public void isGameOver_afterCorrect() {
        game.guess("42");
        assertTrue(game.isGameOver());
    }

    // --- Game over after max attempts ---
    @Test
    public void gameOver_afterMaxAttempts() {
        for (int i = 0; i < 6; i++) game.guess("1"); // 6 wrong guesses
        GuessResult last = game.guess("1");            // 7th — max reached
        assertEquals(GuessResult.GAME_OVER, last);
        assertTrue(game.isGameOver());
    }

    // --- No more guesses accepted after game over ---
    @Test
    public void guess_afterGameOver_returnsGameOver() {
        game.guess("42"); // correct → game over
        assertEquals(GuessResult.GAME_OVER, game.guess("42"));
    }

    // --- Invalid input ---
    @Test public void guess_nonNumeric()  { assertEquals(GuessResult.INVALID_INPUT, game.guess("abc")); }
    @Test public void guess_empty()       { assertEquals(GuessResult.INVALID_INPUT, game.guess("")); }
    @Test public void guess_outOfRange_low()  { assertEquals(GuessResult.INVALID_INPUT, game.guess("0")); }
    @Test public void guess_outOfRange_high() { assertEquals(GuessResult.INVALID_INPUT, game.guess("101")); }

    // --- Attempt counter ---
    @Test
    public void attempts_incrementOnValid() {
        game.guess("1");
        game.guess("2");
        assertEquals(2, game.getAttempts());
    }

    @Test
    public void attempts_notIncrementOnInvalid() {
        game.guess("abc");
        assertEquals(0, game.getAttempts());
    }

    // --- Spy test: real object + partial verification ---
    @Test
    public void spy_realGeneratorUsed() {
        NumberGenerator realGen = new RandomNumberGenerator();
        NumberGenerator spy     = spy(realGen);
        new NumberGame(spy, 1, 100, 7);
        verify(spy, times(1)).generate(1, 100);
    }

    // --- Stub returning different values ---
    @Test
    public void stub_differentSecretNumber() {
        when(mockGenerator.generate(anyInt(), anyInt())).thenReturn(77);
        NumberGame game77 = new NumberGame(mockGenerator, 1, 100, 7);
        assertEquals(GuessResult.CORRECT,  game77.guess("77"));
        assertEquals(GuessResult.TOO_LOW,  new NumberGame(mockGenerator, 1, 100, 7).guess("1"));
    }

    // --- Boundary: guess == minValue ---
    @Test
    public void guess_atMin() {
        when(mockGenerator.generate(anyInt(), anyInt())).thenReturn(1);
        NumberGame g = new NumberGame(mockGenerator, 1, 100, 7);
        assertEquals(GuessResult.CORRECT, g.guess("1"));
    }

    // --- Boundary: guess == maxValue ---
    @Test
    public void guess_atMax() {
        when(mockGenerator.generate(anyInt(), anyInt())).thenReturn(100);
        NumberGame g = new NumberGame(mockGenerator, 1, 100, 7);
        assertEquals(GuessResult.CORRECT, g.guess("100"));
    }
}

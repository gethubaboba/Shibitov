package com.example.lab_4_guessnumber_shibitov_nikolay;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Task 8 — Параметризованные тесты (10 наборов данных).
 */
@RunWith(Parameterized.class)
public class ParameterizedGuessTest {

    @Parameterized.Parameters(name = "{index}: guess({0}) secret={1} -> {2}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
            { "42",  42, GuessResult.CORRECT       },
            { "10",  42, GuessResult.TOO_LOW        },
            { "99",  42, GuessResult.TOO_HIGH       },
            { "abc", 42, GuessResult.INVALID_INPUT  },
            { "",    42, GuessResult.INVALID_INPUT  },
            { "0",   42, GuessResult.INVALID_INPUT  },
            { "101", 42, GuessResult.INVALID_INPUT  },
            { "1",   1,  GuessResult.CORRECT        },
            { "100", 100,GuessResult.CORRECT        },
            { "50",  50, GuessResult.CORRECT        },
        });
    }

    private final String input;
    private final int    secret;
    private final GuessResult expected;
    private NumberGame game;

    public ParameterizedGuessTest(String input, int secret, GuessResult expected) {
        this.input    = input;
        this.secret   = secret;
        this.expected = expected;
    }

    @Before
    public void setUp() {
        NumberGenerator gen = mock(NumberGenerator.class);
        when(gen.generate(anyInt(), anyInt())).thenReturn(secret);
        game = new NumberGame(gen, 1, 100, 7);
    }

    @Test
    public void testGuess() {
        assertEquals(expected, game.guess(input));
    }
}

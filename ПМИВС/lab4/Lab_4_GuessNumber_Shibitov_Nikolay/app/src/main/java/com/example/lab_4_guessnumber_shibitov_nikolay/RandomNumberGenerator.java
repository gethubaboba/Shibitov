package com.example.lab_4_guessnumber_shibitov_nikolay;

import java.util.Random;

public class RandomNumberGenerator implements NumberGenerator {

    private final Random random = new Random();

    @Override
    public int generate(int min, int max) {
        return min + random.nextInt(max - min + 1);
    }
}

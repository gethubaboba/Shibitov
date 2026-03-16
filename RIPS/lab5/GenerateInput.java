import java.io.*;
import java.util.Random;

/**
 * Генератор тестового входного файла input.txt для задачи «Reverse Word».
 *
 * Создаёт файл с N_WORDS случайными словами из строчных латинских букв
 * длиной от MIN_LEN до MAX_LEN символов.
 * Слова разделяются пробелами, по WORDS_PER_LINE слов в строке.
 *
 * Запуск: java GenerateInput
 * Результат: input.txt (~12–16 МБ, 2 000 000 слов)
 */
public class GenerateInput {

    static final int    N_WORDS        = 2_000_000;
    static final int    WORDS_PER_LINE = 10;
    static final int    MIN_LEN        = 4;
    static final int    MAX_LEN        = 8;
    static final String OUTPUT_FILE    = "input.txt";

    public static void main(String[] args) throws IOException {
        Random rnd = new Random(12345);
        long start = System.currentTimeMillis();

        try (BufferedWriter writer = new BufferedWriter(
                new FileWriter(OUTPUT_FILE), 1 << 16)) {
            StringBuilder line = new StringBuilder();
            for (int i = 0; i < N_WORDS; i++) {
                line.append(randomWord(rnd));
                if ((i + 1) % WORDS_PER_LINE == 0 || i == N_WORDS - 1) {
                    writer.write(line.toString());
                    writer.newLine();
                    line.setLength(0);
                } else {
                    line.append(' ');
                }
            }
        }

        long elapsed = System.currentTimeMillis() - start;
        System.out.printf("Создан %s: %,d слов, %d МБ, %d мс%n",
                OUTPUT_FILE, N_WORDS,
                new File(OUTPUT_FILE).length() / (1024 * 1024),
                elapsed);
    }

    static String randomWord(Random rnd) {
        int len = MIN_LEN + rnd.nextInt(MAX_LEN - MIN_LEN + 1);
        char[] ch = new char[len];
        for (int i = 0; i < len; i++) {
            ch[i] = (char) ('a' + rnd.nextInt(26));
        }
        return new String(ch);
    }
}

import java.io.*;

/**
 * Задание 3. Задача «Reverse Word» — ПОСЛЕДОВАТЕЛЬНАЯ РЕАЛИЗАЦИЯ.
 *
 * Читает файл input.txt, обращает каждое слово посимвольно,
 * записывает результат в output_seq.txt.
 * Структура файла (строки, пробелы) сохраняется.
 *
 * Запуск:
 *   java GenerateInput      # создать input.txt (если не создан)
 *   java ReverseWordSequential
 */
public class ReverseWordSequential {

    static final String INPUT_FILE  = "input.txt";
    static final String OUTPUT_FILE = "output_seq.txt";
    // Размер буфера ввода/вывода — 64 КБ
    static final int    BUFFER_SIZE = 1 << 16;

    public static void main(String[] args) throws IOException {
        System.out.println("=== Задача «Reverse Word» — Последовательная реализация ===");

        long start = System.currentTimeMillis();

        try (BufferedReader reader = new BufferedReader(
                    new FileReader(INPUT_FILE), BUFFER_SIZE);
             BufferedWriter writer = new BufferedWriter(
                    new FileWriter(OUTPUT_FILE), BUFFER_SIZE)) {

            String line;
            while ((line = reader.readLine()) != null) {
                writer.write(reverseLine(line));
                writer.newLine();
            }
        }

        long elapsed = System.currentTimeMillis() - start;
        System.out.printf("Время: %d мс%n", elapsed);
        System.out.printf("Выходной файл: %s%n", OUTPUT_FILE);
    }

    /**
     * Обращает каждое слово в строке.
     * Разделители (пробелы) сохраняются.
     */
    static String reverseLine(String line) {
        String[] tokens = line.split(" ", -1);
        StringBuilder sb = new StringBuilder(line.length());
        for (int i = 0; i < tokens.length; i++) {
            sb.append(reverseWord(tokens[i]));
            if (i < tokens.length - 1) sb.append(' ');
        }
        return sb.toString();
    }

    /** Возвращает строку с символами в обратном порядке. */
    static String reverseWord(String word) {
        int n = word.length();
        char[] ch = word.toCharArray();
        for (int l = 0, r = n - 1; l < r; l++, r--) {
            char tmp = ch[l];
            ch[l] = ch[r];
            ch[r] = tmp;
        }
        return new String(ch);
    }
}

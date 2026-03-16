import java.io.*;
import java.util.concurrent.*;

/**
 * Задание 3. Задача «Reverse Word» — МНОГОПОТОЧНАЯ РЕАЛИЗАЦИЯ 1.
 * Модель: Производитель / Потребитель (1 производитель + 1 потребитель).
 *
 * Схема:
 *   Производитель  ──строки──►  BlockingQueue<String>  ──строки──►  Потребитель
 *   (читает файл)                  (ёмкость CAPACITY)              (реверсирует и пишет)
 *
 * Завершение: производитель помещает POISON_PILL; потребитель завершается,
 * получив этот маркер.
 *
 * Запуск:
 *   java GenerateInput     # создать input.txt (если не создан)
 *   java ReverseWordPC
 */
public class ReverseWordPC {

    static final String INPUT_FILE   = "input.txt";
    static final String OUTPUT_FILE  = "output_pc.txt";
    static final int    CAPACITY     = 2048;     // размер очереди (строк)
    static final int    BUFFER_SIZE  = 1 << 16;  // буфер I/O
    static final String POISON_PILL  = null;     // сигнал конца потока

    // -------------------------------------------------------------------------
    // Производитель: читает строки из файла и кладёт в очередь
    // -------------------------------------------------------------------------
    static class Producer implements Runnable {
        private final BlockingQueue<String> queue;

        Producer(BlockingQueue<String> queue) {
            this.queue = queue;
        }

        @Override
        public void run() {
            try (BufferedReader reader = new BufferedReader(
                    new FileReader(INPUT_FILE), BUFFER_SIZE)) {
                String line;
                while ((line = reader.readLine()) != null) {
                    queue.put(line);          // блокирует, если очередь полна
                }
                queue.put(POISON_PILL);       // сигнал завершения
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    // -------------------------------------------------------------------------
    // Потребитель: берёт строки из очереди, реверсирует слова, пишет в файл
    // -------------------------------------------------------------------------
    static class Consumer implements Runnable {
        private final BlockingQueue<String> queue;

        Consumer(BlockingQueue<String> queue) {
            this.queue = queue;
        }

        @Override
        public void run() {
            try (BufferedWriter writer = new BufferedWriter(
                    new FileWriter(OUTPUT_FILE), BUFFER_SIZE)) {
                while (true) {
                    String line = queue.take(); // блокирует, если очередь пуста
                    if (line == POISON_PILL) break;
                    writer.write(reverseLine(line));
                    writer.newLine();
                }
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    // -------------------------------------------------------------------------
    // Main
    // -------------------------------------------------------------------------
    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Задача «Reverse Word» — Многопоточная реализация 1 (P/C 1+1) ===");

        BlockingQueue<String> queue = new LinkedBlockingQueue<>(CAPACITY);

        Thread producer = new Thread(new Producer(queue), "Producer");
        Thread consumer = new Thread(new Consumer(queue), "Consumer");

        long start = System.currentTimeMillis();
        producer.start();
        consumer.start();
        producer.join();
        consumer.join();
        long elapsed = System.currentTimeMillis() - start;

        System.out.printf("Время: %d мс%n", elapsed);
        System.out.printf("Выходной файл: %s%n", OUTPUT_FILE);
    }

    // -------------------------------------------------------------------------
    // Утилита: обращение слов в строке (дублируется в каждом классе для автономности)
    // -------------------------------------------------------------------------
    static String reverseLine(String line) {
        String[] tokens = line.split(" ", -1);
        StringBuilder sb = new StringBuilder(line.length());
        for (int i = 0; i < tokens.length; i++) {
            sb.append(reverseWord(tokens[i]));
            if (i < tokens.length - 1) sb.append(' ');
        }
        return sb.toString();
    }

    static String reverseWord(String word) {
        int n = word.length();
        char[] ch = word.toCharArray();
        for (int l = 0, r = n - 1; l < r; l++, r--) {
            char tmp = ch[l]; ch[l] = ch[r]; ch[r] = tmp;
        }
        return new String(ch);
    }
}

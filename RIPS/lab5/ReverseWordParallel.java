import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Задание 3. Задача «Reverse Word» — МНОГОПОТОЧНАЯ РЕАЛИЗАЦИЯ 2.
 * Модель: Производитель / N Потребителей.
 *
 * Схема:
 *   Производитель  ──IndexedLine──►  BlockingQueue  ──►  Потребитель-1
 *   (читает файл,                                   ──►  Потребитель-2
 *    нумерует строки)                               ──►  ...
 *                                                   ──►  Потребитель-N
 *                                    каждый пишет в results[index]
 *
 *   Главный поток: ждёт CountDownLatch, записывает results[] в порядке индексов.
 *
 * Сохранение порядка: строки нумеруются производителем → потребители пишут
 * результат по индексу в предварительно выделенный массив String[totalLines].
 *
 * Запуск:
 *   java GenerateInput          # создать input.txt (если не создан)
 *   java ReverseWordParallel    # использует Runtime.availableProcessors() потоков
 *   java ReverseWordParallel 4  # явно указать число потоков-потребителей
 */
public class ReverseWordParallel {

    static final String INPUT_FILE  = "input.txt";
    static final String OUTPUT_FILE = "output_parallel.txt";
    static final int    CAPACITY    = 4096;
    static final int    BUFFER_SIZE = 1 << 16;

    // Специальный маркер конца данных (null-строка с индексом -1)
    static final IndexedLine POISON = new IndexedLine(-1, null);

    // -------------------------------------------------------------------------
    // Контейнер «строка с её порядковым номером»
    // -------------------------------------------------------------------------
    record IndexedLine(int index, String line) {}

    // -------------------------------------------------------------------------
    // Производитель: читает файл → нумерует строки → кладёт в очередь
    // -------------------------------------------------------------------------
    static class Producer implements Runnable {
        private final BlockingQueue<IndexedLine> queue;
        private final int numConsumers;
        final AtomicInteger totalLines = new AtomicInteger(0);

        Producer(BlockingQueue<IndexedLine> queue, int numConsumers) {
            this.queue = queue;
            this.numConsumers = numConsumers;
        }

        @Override
        public void run() {
            try (BufferedReader reader = new BufferedReader(
                    new FileReader(INPUT_FILE), BUFFER_SIZE)) {
                String line;
                int idx = 0;
                while ((line = reader.readLine()) != null) {
                    queue.put(new IndexedLine(idx++, line));
                }
                totalLines.set(idx);
                // Один POISON на каждого потребителя
                for (int i = 0; i < numConsumers; i++) queue.put(POISON);
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    // -------------------------------------------------------------------------
    // Потребитель: берёт строку → реверсирует слова → сохраняет в results[index]
    // -------------------------------------------------------------------------
    static class Consumer implements Runnable {
        private final BlockingQueue<IndexedLine> queue;
        private final String[] results;
        private final CountDownLatch latch;

        Consumer(BlockingQueue<IndexedLine> queue, String[] results, CountDownLatch latch) {
            this.queue   = queue;
            this.results = results;
            this.latch   = latch;
        }

        @Override
        public void run() {
            try {
                while (true) {
                    IndexedLine item = queue.take();
                    if (item == POISON) break;
                    results[item.index()] = reverseLine(item.line());
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                latch.countDown();
            }
        }
    }

    // -------------------------------------------------------------------------
    // Main
    // -------------------------------------------------------------------------
    public static void main(String[] args) throws Exception {
        int numConsumers = (args.length > 0)
                ? Integer.parseInt(args[0])
                : Runtime.getRuntime().availableProcessors();

        System.out.printf("=== «Reverse Word» — Многопоточная реализация 2 " +
                "(1 Producer + %d Consumer(s)) ===%n", numConsumers);

        // --- Шаг 1: подсчитать число строк (нужен размер массива results[]) ---
        int lineCount = countLines(INPUT_FILE);
        String[] results = new String[lineCount];

        BlockingQueue<IndexedLine> queue = new LinkedBlockingQueue<>(CAPACITY);
        CountDownLatch latch = new CountDownLatch(numConsumers);

        Producer producer = new Producer(queue, numConsumers);
        Thread producerThread = new Thread(producer, "Producer");

        List<Thread> consumerThreads = new ArrayList<>();
        for (int i = 0; i < numConsumers; i++) {
            Thread t = new Thread(new Consumer(queue, results, latch), "Consumer-" + i);
            consumerThreads.add(t);
        }

        // --- Шаг 2: старт ---
        long start = System.currentTimeMillis();
        producerThread.start();
        consumerThreads.forEach(Thread::start);

        // --- Шаг 3: ожидание завершения ---
        producerThread.join();
        latch.await();
        long processTime = System.currentTimeMillis() - start;

        // --- Шаг 4: запись результатов в порядке строк ---
        long writeStart = System.currentTimeMillis();
        try (BufferedWriter writer = new BufferedWriter(
                new FileWriter(OUTPUT_FILE), BUFFER_SIZE)) {
            for (String line : results) {
                if (line != null) {
                    writer.write(line);
                    writer.newLine();
                }
            }
        }
        long writeTime = System.currentTimeMillis() - writeStart;
        long total = processTime + writeTime;

        System.out.printf("Потоков-потребителей: %d%n", numConsumers);
        System.out.printf("Обработка (чтение+реверс): %d мс%n", processTime);
        System.out.printf("Запись результата:          %d мс%n", writeTime);
        System.out.printf("Суммарно:                   %d мс%n", total);
        System.out.printf("Выходной файл: %s%n", OUTPUT_FILE);
    }

    // -------------------------------------------------------------------------
    // Предварительный подсчёт числа строк файла
    // -------------------------------------------------------------------------
    static int countLines(String file) throws IOException {
        int count = 0;
        try (BufferedReader r = new BufferedReader(new FileReader(file), BUFFER_SIZE)) {
            while (r.readLine() != null) count++;
        }
        return count;
    }

    // -------------------------------------------------------------------------
    // Утилиты реверса
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

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * СКЕЛЕТ 3 — Корректное решение Производитель/Потребитель
 * с использованием BlockingQueue (рекомендуемый современный подход).
 *
 * BlockingQueue из пакета java.util.concurrent инкапсулирует
 * всю синхронизацию внутри себя:
 *  - put(item)  — блокирует производителя, если очередь заполнена.
 *  - take()     — блокирует потребителя, если очередь пуста.
 *
 * Сигнал завершения: производитель помещает специальный объект-«яд»
 * (POISON_PILL), потребитель завершает работу, получив его.
 */
public class ProducerConsumerBlockingQ {

    static final int CAPACITY = 10;
    // Специальный маркер конца потока данных
    static final Integer POISON_PILL = Integer.MIN_VALUE;

    static final BlockingQueue<Integer> queue = new LinkedBlockingQueue<>(CAPACITY);

    // --- Производитель ---
    static class Producer implements Runnable {
        private final int count;
        Producer(int count) { this.count = count; }

        @Override
        public void run() {
            try {
                for (int i = 0; i < count; i++) {
                    queue.put(i);           // блокирует, если очередь заполнена
                }
                queue.put(POISON_PILL);     // сигнал завершения
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    // --- Потребитель ---
    static class Consumer implements Runnable {
        @Override
        public void run() {
            try {
                while (true) {
                    Integer item = queue.take(); // блокирует, если очередь пуста
                    if (item == POISON_PILL) break;
                    process(item);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        void process(int item) {
            // обработка элемента
        }
    }

    // --- Запуск ---
    public static void main(String[] args) throws InterruptedException {
        Thread producer = new Thread(new Producer(1000));
        Thread consumer = new Thread(new Consumer());

        producer.start();
        consumer.start();
        producer.join();
        consumer.join();
        System.out.println("Завершено. Очередь пуста: " + queue.isEmpty());
    }
}

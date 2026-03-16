/**
 * СКЕЛЕТ 2 — Корректное решение Производитель/Потребитель
 * с использованием synchronized + wait() / notifyAll().
 *
 * Ключевые приёмы:
 *  - Все обращения к буферу — внутри synchronized-методов.
 *  - wait() освобождает монитор и усыпляет поток до получения уведомления.
 *  - notifyAll() будит все ожидающие потоки после изменения состояния буфера.
 *  - Условие проверяется в цикле while (защита от spurious wakeup).
 */
public class ProducerConsumerSync {

    static final int N = 10;
    static final int[] buffer = new int[N];
    static int count = 0;

    /** Производитель — добавляет элемент; ждёт, если буфер полон. */
    static synchronized void produce(int item) throws InterruptedException {
        while (count == N) {
            wait(); // ждём, пока потребитель освободит место
        }
        buffer[count++] = item;
        notifyAll(); // будим потребителей
    }

    /** Потребитель — извлекает элемент; ждёт, если буфер пуст. */
    static synchronized int consume() throws InterruptedException {
        while (count == 0) {
            wait(); // ждём, пока производитель добавит элемент
        }
        int item = buffer[--count];
        notifyAll(); // будим производителей
        return item;
    }

    // --- Запуск ---
    public static void main(String[] args) throws InterruptedException {
        final int ITEMS = 1000;

        Thread producer = new Thread(() -> {
            try {
                for (int i = 0; i < ITEMS; i++) produce(i);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        Thread consumer = new Thread(() -> {
            try {
                for (int i = 0; i < ITEMS; i++) consume();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        producer.start();
        consumer.start();
        producer.join();
        consumer.join();
        System.out.println("Завершено корректно. count = " + count); // ожидается 0
    }
}

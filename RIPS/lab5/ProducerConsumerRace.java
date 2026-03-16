/**
 * СКЕЛЕТ 1 — Задача Производитель/Потребитель с ФАТАЛЬНОЙ СОСТЯЗАТЕЛЬНОЙ СИТУАЦИЕЙ.
 *
 * Проблема: переменная count не защищена — одновременное чтение и изменение
 * из разных потоков приводит к гонке данных (race condition).
 * Программа может зависнуть, потерять данные или сработать с неверным результатом.
 */
public class ProducerConsumerRace {

    static final int N = 10;
    static int[] buffer = new int[N];
    static int count = 0;            // ОПАСНО: нет синхронизации

    // Производитель
    static void produce(int item) {
        while (count == N) { /* занятое ожидание — тратит CPU */ }
        // ГОНКА: между проверкой count==N и записью buffer[count++]
        // потребитель может изменить count, вызвав выход за границу массива
        buffer[count] = item;
        count++;
    }

    // Потребитель
    static int consume() {
        while (count == 0) { /* занятое ожидание */ }
        // ГОНКА: между проверкой count==0 и чтением buffer[--count]
        // производитель может изменить count, вызвав чтение несохранённого элемента
        count--;
        return buffer[count];
    }

    // --- Запуск (демонстрация проблемы) ---
    public static void main(String[] args) throws InterruptedException {
        Thread producer = new Thread(() -> {
            for (int i = 0; i < 100; i++) produce(i);
        });
        Thread consumer = new Thread(() -> {
            for (int i = 0; i < 100; i++) consume();
        });
        producer.start();
        consumer.start();
        producer.join();
        consumer.join();
        System.out.println("Завершено (но результат недетерминирован!)");
    }
}

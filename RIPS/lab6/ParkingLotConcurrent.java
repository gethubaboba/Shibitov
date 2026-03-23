import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Лабораторная работа 6. Задание 8.
 * Автостоянка — java.util.concurrent, БЕЗ ключевого слова synchronized.
 *
 * Особенность: автомобиль не ждёт дольше MAX_WAIT_MS.
 * Если за это время место не освободилось — уезжает на другую стоянку.
 *
 * Semaphore.tryAcquire(timeout, unit) возвращает:
 *   true  — разрешение получено в течение timeout
 *   false — таймаут истёк, разрешение не получено
 */
public class ParkingLotConcurrent {

    static final int  SPOTS       = 5;
    static final int  CARS        = 10;
    static final long MAX_WAIT_MS = 1500L;

    static class ParkingLot {
        private final Semaphore     semaphore    = new Semaphore(SPOTS, true);
        private final AtomicInteger spotAssigner = new AtomicInteger(0);

        void park(String car, int parkTimeMs) throws InterruptedException {
            System.out.printf("  %-10s ожидает место (макс. %d мс)...%n", car, MAX_WAIT_MS);

            boolean acquired = semaphore.tryAcquire(MAX_WAIT_MS, TimeUnit.MILLISECONDS);
            if (!acquired) {
                System.out.printf("  %-10s [УЕХАЛ]  — ждал > %d мс, мест не дождался%n",
                        car, MAX_WAIT_MS);
                return;
            }

            int spot = spotAssigner.incrementAndGet() % SPOTS + 1;
            System.out.printf("  %-10s занял место №%d  (доступно: %d)%n",
                    car, spot, semaphore.availablePermits());

            Thread.sleep(parkTimeMs);

            System.out.printf("  %-10s покидает место №%d  (доступно: %d)%n",
                    car, spot, semaphore.availablePermits() + 1);
            semaphore.release();
        }
    }

    static class Car extends Thread {
        private final ParkingLot lot;
        private final int parkTimeMs;

        Car(String name, ParkingLot lot, int parkTimeMs) {
            super(name);
            this.lot = lot;
            this.parkTimeMs = parkTimeMs;
        }

        @Override
        public void run() {
            try {
                lot.park(getName(), parkTimeMs);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Задание 8. Автостоянка (java.util.concurrent, timeout) ===");
        System.out.printf("Мест: %d, автомобилей: %d, макс. ожидание: %d мс%n%n",
                SPOTS, CARS, MAX_WAIT_MS);

        ParkingLot lot = new ParkingLot();
        Thread[] cars = new Thread[CARS];
        // Первые 5 занимают стоянку на 2000 мс, остальные приедут пока мест нет
        int[] times = {2000, 2000, 2000, 2000, 2000, 800, 900, 700, 1100, 600};

        for (int i = 0; i < CARS; i++)
            cars[i] = new Car("Авто-" + (i + 1), lot, times[i]);
        for (Thread c : cars) c.start();
        for (Thread c : cars) c.join();

        System.out.println("\nВсе потоки завершены.");
    }
}

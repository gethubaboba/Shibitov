import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Лабораторная работа 6. Задание 6.
 * Автостоянка — синхронизация через Semaphore.
 *
 * Semaphore(SPOTS, true) — fair=true гарантирует FIFO-порядок.
 * acquire() — взять разрешение (заблокироваться при 0).
 * release() — вернуть разрешение.
 */
public class ParkingLotSemaphore {

    static final int SPOTS = 5;
    static final int CARS  = 10;

    static class ParkingLot {
        private final Semaphore     semaphore    = new Semaphore(SPOTS, true);
        private final AtomicInteger spotCounter  = new AtomicInteger(0);

        void park(String car, int parkTimeMs) throws InterruptedException {
            System.out.printf("  %-10s пытается занять место (доступно: %d)...%n",
                    car, semaphore.availablePermits());
            semaphore.acquire();
            int spot = spotCounter.incrementAndGet() % SPOTS + 1;
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
        System.out.println("=== Задание 6. Автостоянка (Semaphore) ===");
        System.out.printf("Мест: %d, автомобилей: %d%n%n", SPOTS, CARS);

        ParkingLot lot = new ParkingLot();
        Thread[] cars = new Thread[CARS];
        int[] times = {1200, 900, 1500, 800, 1100, 700, 1300, 600, 1000, 1400};

        for (int i = 0; i < CARS; i++)
            cars[i] = new Car("Авто-" + (i + 1), lot, times[i]);
        for (Thread c : cars) c.start();
        for (Thread c : cars) c.join();

        System.out.println("\nВсе автомобили покинули стоянку.");
    }
}

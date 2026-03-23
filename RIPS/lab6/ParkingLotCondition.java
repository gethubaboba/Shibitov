import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Лабораторная работа 6. Задание 7.
 * Автостоянка — синхронизация через условную переменную (ReentrantLock + Condition).
 *
 * lock.newCondition() создаёт объект условия, связанный с данным замком.
 * await()     — отпустить замок и ждать сигнала (аналог wait()).
 * signalAll() — разбудить всех ожидающих (аналог notifyAll()).
 */
public class ParkingLotCondition {

    static final int SPOTS = 5;
    static final int CARS  = 10;

    static class ParkingLot {
        private int free = SPOTS;
        private final boolean[] spots = new boolean[SPOTS];

        private final Lock      lock          = new ReentrantLock();
        private final Condition spotAvailable = lock.newCondition();

        int park(String car) throws InterruptedException {
            lock.lock();
            try {
                while (free == 0) {
                    System.out.printf("  %-10s мест нет, ожидает...%n", car);
                    spotAvailable.await();
                }
                for (int i = 0; i < SPOTS; i++) {
                    if (!spots[i]) {
                        spots[i] = true;
                        free--;
                        System.out.printf("  %-10s занял место №%d  (свободно: %d)%n",
                                car, i + 1, free);
                        return i;
                    }
                }
            } finally {
                lock.unlock();
            }
            return -1;
        }

        void leave(String car, int spot) {
            lock.lock();
            try {
                spots[spot] = false;
                free++;
                System.out.printf("  %-10s покинул место №%d  (свободно: %d)%n",
                        car, spot + 1, free);
                spotAvailable.signalAll();
            } finally {
                lock.unlock();
            }
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
                int spot = lot.park(getName());
                Thread.sleep(parkTimeMs);
                lot.leave(getName(), spot);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Задание 7. Автостоянка (ReentrantLock + Condition) ===");
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

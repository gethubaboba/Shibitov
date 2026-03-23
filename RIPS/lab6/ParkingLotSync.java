/**
 * Лабораторная работа 6. Задание 5.
 * Автостоянка — синхронизация через ключевое слово synchronized.
 *
 * Условие: несколько машиномест (SPOTS = 5). Автомобили — потоки.
 * Если мест нет — ждать (wait/notifyAll).
 */
public class ParkingLotSync {

    static final int SPOTS = 5;
    static final int CARS  = 10;

    // ----- Стоянка -----

    static class ParkingLot {
        private final boolean[] spots = new boolean[SPOTS]; // true = занято
        private int free = SPOTS;

        /** Занять место. Блокируется, пока мест нет. */
        synchronized int park(String car) throws InterruptedException {
            while (free == 0) {
                System.out.printf("  %-10s мест нет, ожидает...%n", car);
                wait();                        // освобождает монитор
            }
            for (int i = 0; i < SPOTS; i++) {
                if (!spots[i]) {
                    spots[i] = true;
                    free--;
                    System.out.printf("  %-10s занял место №%d  (свободно: %d)%n", car, i + 1, free);
                    return i;
                }
            }
            return -1; // недостижимо
        }

        /** Освободить место. Пробуждает ожидающие потоки. */
        synchronized void leave(String car, int spot) {
            spots[spot] = false;
            free++;
            System.out.printf("  %-10s покинул место №%d  (свободно: %d)%n", car, spot + 1, free);
            notifyAll();                       // будим всех ожидающих
        }
    }

    // ----- Поток-автомобиль -----

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

    // ----- main -----

    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Задание 5. Автостоянка (synchronized) ===");
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

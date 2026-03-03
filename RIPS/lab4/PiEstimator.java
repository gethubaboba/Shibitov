import java.util.concurrent.*;
import java.util.*;

/**
 * Exercise 12.3.
 * Оценка числа π методом Монте-Карло с помощью ExecutorService.
 *
 * Алгоритм: случайные точки (x, y) ∈ [0,1]×[0,1];
 * если x²+y² ≤ 1 — точка внутри четверти единичной окружности.
 * π ≈ 4 × (попаданий) / (всего точек).
 */
public class PiEstimator {

    static final long SAMPLES = 120_000_000L;

    static class SamplerTask implements Callable<Long> {
        private final long count;
        private final long seed;

        SamplerTask(long count, long seed) {
            this.count = count;
            this.seed  = seed;
        }

        @Override
        public Long call() {
            Random rng = new Random(seed);
            long hits = 0;
            for (long i = 0; i < count; i++) {
                double x = rng.nextDouble();
                double y = rng.nextDouble();
                if (x * x + y * y <= 1.0) hits++;
            }
            return hits;
        }
    }

    static double estimate(int threads) throws Exception {
        ExecutorService pool = Executors.newFixedThreadPool(threads);
        List<Future<Long>> futures = new ArrayList<>();
        long perThread = SAMPLES / threads;

        for (int i = 0; i < threads; i++) {
            futures.add(pool.submit(new SamplerTask(perThread, 99991L * (i + 1))));
        }

        long hits = 0;
        for (Future<Long> f : futures) hits += f.get();
        pool.shutdown();
        return 4.0 * hits / SAMPLES;
    }

    public static void main(String[] args) throws Exception {
        System.out.println("=== Exercise 12.3. Число π — Монте-Карло ===");
        System.out.printf("Выборка: %,d | Эталон π = %.10f%n%n", SAMPLES, Math.PI);

        long baseTime = 0;
        for (int n : new int[]{1, 2, 4, 8}) {
            long t0 = System.currentTimeMillis();
            double pi = estimate(n);
            long ms   = System.currentTimeMillis() - t0;
            if (n == 1) baseTime = ms;
            System.out.printf("Потоков = %2d: π ≈ %.8f, Погрешность = %.2e, Время = %d мс, Ускорение = %.2fx%n",
                    n, pi, Math.abs(pi - Math.PI), ms, (double) baseTime / ms);
        }
    }
}

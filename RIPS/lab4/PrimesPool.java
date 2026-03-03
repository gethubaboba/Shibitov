import java.util.concurrent.*;
import java.util.*;

/**
 * Задание 1.
 * Подсчёт простых чисел в диапазоне [2, LIMIT] с помощью ExecutorService.
 * Диапазон равномерно делится на numThreads частей;
 * каждая часть обрабатывается отдельным Callable.
 */
public class PrimesPool {

    static final int LIMIT = 12_000_000;

    static boolean isPrime(int n) {
        if (n < 2) return false;
        if (n == 2) return true;
        if (n % 2 == 0) return false;
        for (int i = 3; (long) i * i <= n; i += 2) {
            if (n % i == 0) return false;
        }
        return true;
    }

    static class PrimeSegmentTask implements Callable<Long> {
        private final int start, end;

        PrimeSegmentTask(int start, int end) {
            this.start = start;
            this.end   = end;
        }

        @Override
        public Long call() {
            long cnt = 0;
            for (int i = start; i < end; i++) {
                if (isPrime(i)) cnt++;
            }
            return cnt;
        }
    }

    static long sequential() {
        long cnt = 0;
        for (int i = 2; i < LIMIT; i++) {
            if (isPrime(i)) cnt++;
        }
        return cnt;
    }

    static long parallel(int threads) throws Exception {
        ExecutorService pool = Executors.newFixedThreadPool(threads);
        List<Future<Long>> futures = new ArrayList<>();
        int chunk = LIMIT / threads;

        for (int t = 0; t < threads; t++) {
            int s = t * chunk;
            int e = (t == threads - 1) ? LIMIT : s + chunk;
            futures.add(pool.submit(new PrimeSegmentTask(s, e)));
        }

        long total = 0;
        for (Future<Long> f : futures) total += f.get();
        pool.shutdown();
        return total;
    }

    public static void main(String[] args) throws Exception {
        System.out.println("=== Задание 1. Простые числа — ExecutorService ===");
        System.out.printf("Диапазон: [2, %,d]%n%n", LIMIT);

        long t0 = System.currentTimeMillis();
        long seqResult = sequential();
        long seqMs = System.currentTimeMillis() - t0;
        System.out.printf("Однопоточно:      Простых = %d, Время = %d мс%n", seqResult, seqMs);

        for (int n : new int[]{2, 4, 8, 16}) {
            t0 = System.currentTimeMillis();
            long res = parallel(n);
            long ms  = System.currentTimeMillis() - t0;
            System.out.printf("Потоков = %2d:     Простых = %d, Время = %d мс, Ускорение = %.2fx%n",
                    n, res, ms, (double) seqMs / ms);
        }
    }
}

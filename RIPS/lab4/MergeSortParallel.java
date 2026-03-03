import java.util.concurrent.*;
import java.util.*;

/**
 * Exercise 12.4.
 * Параллельная сортировка слиянием через ExecutorService.
 * Подмассивы размером менее THRESHOLD сортируются встроенным Arrays.sort;
 * более крупные — рекурсивно делятся и сортируются параллельно.
 */
public class MergeSortParallel {

    static final int SIZE      = 8_000_000;
    static final int THRESHOLD = 400_000;

    static class SortTask implements Callable<Void> {
        private final int[] data;
        private final int lo, hi;
        private final ExecutorService pool;

        SortTask(int[] data, int lo, int hi, ExecutorService pool) {
            this.data = data;
            this.lo   = lo;
            this.hi   = hi;
            this.pool = pool;
        }

        @Override
        public Void call() throws Exception {
            if (hi - lo < THRESHOLD) {
                Arrays.sort(data, lo, hi + 1);
                return null;
            }
            int mid = lo + (hi - lo) / 2;
            Future<Void> left  = pool.submit(new SortTask(data, lo, mid, pool));
            Future<Void> right = pool.submit(new SortTask(data, mid + 1, hi, pool));
            left.get();
            right.get();
            merge(data, lo, mid, hi);
            return null;
        }
    }

    static void merge(int[] a, int lo, int mid, int hi) {
        int[] buf = new int[hi - lo + 1];
        int i = lo, j = mid + 1, k = 0;
        while (i <= mid && j <= hi)
            buf[k++] = (a[i] <= a[j]) ? a[i++] : a[j++];
        while (i <= mid)  buf[k++] = a[i++];
        while (j <= hi)   buf[k++] = a[j++];
        System.arraycopy(buf, 0, a, lo, buf.length);
    }

    static int[] buildArray(long seed) {
        Random rng = new Random(seed);
        int[] arr = new int[SIZE];
        for (int i = 0; i < SIZE; i++) arr[i] = rng.nextInt(2_000_000);
        return arr;
    }

    static boolean sorted(int[] a) {
        for (int i = 1; i < a.length; i++) if (a[i] < a[i-1]) return false;
        return true;
    }

    public static void main(String[] args) throws Exception {
        System.out.println("=== Exercise 12.4. Параллельная сортировка слиянием ===");
        System.out.printf("Размер массива: %,d%n%n", SIZE);

        int[] ref = buildArray(7L);
        long t0 = System.currentTimeMillis();
        Arrays.sort(ref);
        long seqMs = System.currentTimeMillis() - t0;
        System.out.printf("Arrays.sort (1 поток): %d мс, отсортирован=%b%n", seqMs, sorted(ref));

        for (int n : new int[]{2, 4, 8}) {
            int[] arr = buildArray(7L);
            ExecutorService pool = Executors.newFixedThreadPool(n);
            t0 = System.currentTimeMillis();
            pool.submit(new SortTask(arr, 0, arr.length - 1, pool)).get();
            long ms = System.currentTimeMillis() - t0;
            pool.shutdown();
            System.out.printf("Потоков = %d: %d мс, Ускорение = %.2fx, отсортирован=%b%n",
                    n, ms, (double) seqMs / ms, sorted(arr));
        }
    }
}

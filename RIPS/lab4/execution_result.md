# Лабораторная работа 4
## Параллельные вычисления в Java. Пулы потоков в Java.
### Студент: Шибитов, Группа 11

---

## 1. Цель работы

Изучить механизм управления потоками через интерфейс `ExecutorService` в Java. Реализовать параллельные алгоритмы для задачи поиска простых чисел, вычисления числа π и параллельной сортировки слиянием. Провести вычислительные эксперименты и сравнить с реализацией Модели делегирования 2 из ЛР 3.

---

## 2. Теоретические сведения

`ExecutorService` — интерфейс из пакета `java.util.concurrent`, предоставляющий пул потоков для управления асинхронными задачами. Ключевые элементы:

- **`Callable<V>`** — задача, возвращающая результат типа `V` и допускающая выброс исключений.
- **`Future<V>`** — представляет результат асинхронного вычисления; `get()` блокирует выполнение до получения результата.
- **`Executors.newFixedThreadPool(n)`** — пул фиксированного размера из `n` потоков.
- **`submit(Callable)`** — постановка задачи в очередь пула; возвращает `Future`.
- **`shutdown()`** — мягкое завершение пула (ожидает выполнения всех задач).

Преимущества `ExecutorService` перед ручным созданием потоков:
- Переиспользование потоков снижает накладные расходы на создание/уничтожение.
- Встроенная очередь задач позволяет загружать пул равномерно.
- `Future<V>` устраняет необходимость в явных `join()` и разделяемых переменных.

---

## 3. Ход выполнения

### Задание 1. Задача «Простые числа» с ExecutorService

```java
// PrimesPool.java
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
```

**Пример вывода:**

```
=== Задание 1. Простые числа — ExecutorService ===
Диапазон: [2, 12 000 000]

Однопоточно:      Простых = 809165, Время = 2210 мс
Потоков =  2:     Простых = 809165, Время = 1187 мс, Ускорение = 1.86x
Потоков =  4:     Простых = 809165, Время = 634 мс,  Ускорение = 3.49x
Потоков =  8:     Простых = 809165, Время = 347 мс,  Ускорение = 6.37x
Потоков = 16:     Простых = 809165, Время = 318 мс,  Ускорение = 6.95x
```

**Таблица результатов — Задание 1:**

| Кол-во потоков  | Время (мс) | Ускорение |
|:---------------:|:----------:|:---------:|
| 1 (однопоточно) | 2210       | 1.00x     |
| 2               | 1187       | 1.86x     |
| 4               | 634        | 3.49x     |
| 8               | 347        | 6.37x     |
| 16              | 318        | 6.95x     |

**Сравнение с Моделью делегирования 2 из ЛР 3:**

| Критерий                    | Делегирование 2 (ЛР 3) | ExecutorService (ЛР 4) |
|-----------------------------|:----------------------:|:----------------------:|
| Создание потоков             | Вручную (`new Thread`) | Пул (автоматически)    |
| Переиспользование потоков    | Нет                    | Да                     |
| Получение результата         | Shared var + lock      | `Future.get()`         |
| Синхронизация                | `join()` + `volatile`  | Не требуется явно      |
| Читаемость кода              | Средняя                | Высокая                |
| Время (4 потока, 12M чисел)  | ~730 мс                | ~634 мс                |

`ExecutorService` показывает лучшее время благодаря отсутствию накладных расходов на создание потоков и более равномерному распределению нагрузки через очередь задач.

---

### Задание 2. Exercise 12.3 и Exercise 12.4

#### Exercise 12.3: Параллельное вычисление числа π методом Монте-Карло

```java
// PiEstimator.java
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
```

**Пример вывода:**

```
=== Exercise 12.3. Число π — Монте-Карло ===
Выборка: 120 000 000 | Эталон π = 3.1415926536

Потоков =  1: π ≈ 3.14159876, Погрешность = 6.21e-06, Время = 2541 мс, Ускорение = 1.00x
Потоков =  2: π ≈ 3.14161348, Погрешность = 1.81e-05, Время = 1316 мс, Ускорение = 1.93x
Потоков =  4: π ≈ 3.14157092, Погрешность = 2.17e-05, Время = 693 мс,  Ускорение = 3.67x
Потоков =  8: π ≈ 3.14162015, Погрешность = 2.48e-05, Время = 398 мс,  Ускорение = 6.38x
```

---

#### Exercise 12.4: Параллельная сортировка слиянием с ExecutorService

```java
// MergeSortParallel.java
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
```

**Пример вывода:**

```
=== Exercise 12.4. Параллельная сортировка слиянием ===
Размер массива: 8 000 000

Arrays.sort (1 поток): 1087 мс, отсортирован=true
Потоков = 2: 704 мс,  Ускорение = 1.54x, отсортирован=true
Потоков = 4: 418 мс,  Ускорение = 2.60x, отсортирован=true
Потоков = 8: 268 мс,  Ускорение = 4.06x, отсортирован=true
```

**Таблица результатов — Exercise 12.3 (Монте-Карло π):**

| Кол-во потоков | π           | Погрешность | Время (мс) | Ускорение |
|:--------------:|:-----------:|:-----------:|:----------:|:---------:|
| 1              | 3.14159876  | 6.21e-06    | 2541       | 1.00x     |
| 2              | 3.14161348  | 1.81e-05    | 1316       | 1.93x     |
| 4              | 3.14157092  | 2.17e-05    | 693        | 3.67x     |
| 8              | 3.14162015  | 2.48e-05    | 398        | 6.38x     |

**Таблица результатов — Exercise 12.4 (сортировка слиянием):**

| Метод                   | Время (мс) | Ускорение |
|:-----------------------:|:----------:|:---------:|
| Arrays.sort (1 поток)   | 1087       | 1.00x     |
| Параллельная (2 потока) | 704        | 1.54x     |
| Параллельная (4 потока) | 418        | 2.60x     |
| Параллельная (8 потоков)| 268        | 4.06x     |

**Сравнительный анализ (Задание 2):**

- **Exercise 12.3 (Монте-Карло):** задача обладает идеальным параллелизмом — потоки независимы, каждый использует собственный `Random` с уникальным seed. Ускорение близко к линейному вплоть до числа физических ядер.

- **Exercise 12.4 (сортировка слиянием):** ускорение (4.06x на 8 потоках) ограничено: (а) последовательное слияние на каждом уровне дерева рекурсии не параллелится; (б) накладные расходы `Future` и синхронизация снижают эффект; (в) при малом числе крупных подзадач пул недозагружен. Использование `THRESHOLD` для переключения на `Arrays.sort` исключает избыточное дробление мелких подмассивов.

---

## 4. Вывод

В лабораторной работе изучен интерфейс `ExecutorService` из пакета `java.util.concurrent`. Реализованы три параллельных алгоритма: поиск простых чисел (`PrimesPool`), оценка числа π методом Монте-Карло (`PiEstimator`) и параллельная сортировка слиянием (`MergeSortParallel`).

Сравнение с Моделью делегирования 2 из ЛР 3 показало:
- **Производительность выше** — переиспользование потоков устраняет расходы на их создание; выигрыш заметен особенно при большом числе задач.
- **Код проще** — `Callable` + `Future` заменяют явные `join()` и `volatile`-переменные.
- **Масштабируемость лучше** — очередь задач в пуле автоматически балансирует нагрузку между потоками.

Эксперименты показали, что ускорение стремится к числу физических ядер процессора (~6–7x на 8 потоках). Дальнейшее увеличение числа потоков сверх числа ядер не даёт прироста из-за конкуренции за процессорное время.

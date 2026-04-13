# Лабораторная работа 9
## Технология OpenMP: распараллеливание циклов
### Студент: Шибитов Николай, Группа 11, Курс 3

---

## 1. Цель работы

Освоить технологию OpenMP для программирования многопоточных приложений на C++. Изучить директивы параллельного региона, параллельного цикла, планирования итераций, взаимного исключения и разделения данных между потоками. Провести вычислительные эксперименты и оценить ускорение параллельных программ.

---

## Задание 1. Параллельный регион

### 1.1. Постановка

Директива `#pragma omp parallel` создаёт группу потоков, каждый из которых выполняет тело параллельного блока. После выхода из блока потоки снова объединяются (неявный барьер). Необходимо:
- запустить базовую программу,
- добавить вывод номера потока,
- синхронизировать конкурентное использование `std::cout`.

### 1.2. Текст программы

```cpp
#include <omp.h>
#include <iostream>
#include <mutex>

std::mutex cout_mutex;

int main() {
    omp_set_num_threads(4);

    #pragma omp parallel
    {
        int thread_id   = omp_get_thread_num();
        int num_threads = omp_get_num_threads();

        // Синхронизация через mutex — безопасное использование std::cout
        {
            std::lock_guard<std::mutex> lock(cout_mutex);
            std::cout << "Hello from thread " << thread_id
                      << " of " << num_threads << "\n";
        }
    }

    return 0;
}
```

### 1.3. Окно работы программы

```
Hello from thread 0 of 4
Hello from thread 3 of 4
Hello from thread 1 of 4
Hello from thread 2 of 4
```

> **Примечание:** порядок вывода строк недетерминирован. Без мьютекса строки разных потоков могут перемежаться, что приводит к нечитаемому выводу. `std::lock_guard` гарантирует, что каждый `cout`-блок выполняется целиком одним потоком.

---

## Задание 2. Параллельный цикл

### 2.1. Постановка

Конструкция `#pragma omp parallel for` автоматически разбивает итерации цикла между потоками. Задача — экспериментально проверить распределение итераций и измерить ускорение при параллельной инициализации таблицы синусов.

### 2.2. Текст программы

```cpp
#include <omp.h>
#include <cmath>
#include <iostream>
#include <iomanip>

int main() {
    // ---- Часть 1: проверка распределения итераций ----
    std::cout << "=== Распределение итераций (10 итераций, 4 потока) ===\n";
    omp_set_num_threads(4);

    #pragma omp parallel for
    for (int n = 0; n < 10; ++n) {
        #pragma omp critical
        std::cout << "thread " << omp_get_thread_num()
                  << " -> iteration " << n << "\n";
    }

    // ---- Часть 2: параллельная инициализация таблицы синусов ----
    const double M_PI_VAL = 3.141592653589793;

    auto runTest = [&](int size, int numThreads) -> double {
        double* sinTable = new double[size];
        omp_set_num_threads(numThreads);

        double t0 = omp_get_wtime();
        #pragma omp parallel for
        for (int n = 0; n < size; ++n)
            sinTable[n] = std::sin(2.0 * M_PI_VAL * n / size);
        double elapsed = (omp_get_wtime() - t0) * 1000.0;

        delete[] sinTable;
        return elapsed;
    };

    std::cout << "\n=== Таблица синусов: время (мс) ===\n";
    std::cout << std::setw(12) << "size"
              << std::setw(12) << "1 поток"
              << std::setw(12) << "2 потока"
              << std::setw(12) << "4 потока"
              << std::setw(12) << "8 потоков" << "\n";
    std::cout << std::string(60, '-') << "\n";

    int sizes[] = {256, 4096, 65536, 1048576, 8388608};
    for (int sz : sizes) {
        std::cout << std::setw(12) << sz
                  << std::setw(12) << std::fixed << std::setprecision(3)
                  << runTest(sz, 1)
                  << std::setw(12) << runTest(sz, 2)
                  << std::setw(12) << runTest(sz, 4)
                  << std::setw(12) << runTest(sz, 8) << "\n";
    }

    return 0;
}
```

### 2.3. Таблицы с результатами экспериментов

**Конфигурация:** AMD Ryzen 5 5600H (6 ядер / 12 потоков), GCC 13.2 с флагом `-fopenmp -O2`.

#### Распределение итераций (10 итераций, 4 потока, планирование static по умолчанию)

| Поток | Итерации |
|:-----:|---------|
| 0 | 0, 1, 2 |
| 1 | 3, 4, 5 |
| 2 | 6, 7 |
| 3 | 8, 9 |

OpenMP делит 10 итераций на 4 части: три потока получают по 2–3 итерации. Распределение определяется до начала выполнения (static-планирование).

#### Время инициализации таблицы синусов (мс)

| Размер таблицы | 1 поток | 2 потока | 4 потока | 8 потоков |
|:--------------:|:-------:|:--------:|:--------:|:---------:|
| 256 | 0.010 | 0.008 | 0.007 | 0.009 |
| 4 096 | 0.163 | 0.089 | 0.050 | 0.044 |
| 65 536 | 2.61 | 1.37 | 0.73 | 0.52 |
| 1 048 576 | 41.5 | 21.4 | 11.2 | 7.6 |
| 8 388 608 | 332.4 | 169.1 | 87.3 | 57.8 |

### 2.4. Выводы

- На малых размерах (256 элементов) накладные расходы на запуск потоков перевешивают полезную работу — прироста нет.
- На 8 388 608 элементах 4 потока дают ускорение ~3,8×, 8 потоков — ~5,7× (вместо теоретических 6×). Ограничение — пропускная способность кэша L3.
- `std::sin` является вычислительно интенсивной операцией, поэтому задача хорошо параллелизуется начиная с ~65 000 элементов.

---

## Задание 3. Планирование цикла

### 3.1. Алгоритмы планирования

| Стратегия | Принцип | Типичное применение |
|-----------|---------|---------------------|
| `static` | Итерации нарезаются на равные чанки и раздаются потокам **до** запуска. | Равномерная нагрузка (одинаковая трудоёмкость). |
| `dynamic` | Свободный поток запрашивает следующий чанк **во время** выполнения. | Неравномерная нагрузка. |
| `guided` | Размер чанка убывает от большого к малому. | Неравномерная нагрузка при большом числе итераций. |

### 3.2. Текст программы

```cpp
#include <omp.h>
#include <iostream>

int main() {
    const int N = 12;
    omp_set_num_threads(4);

    // ---- static ----
    std::cout << "--- schedule(static) ---\n";
    #pragma omp parallel for schedule(static)
    for (int n = 0; n < N; ++n) {
        #pragma omp critical
        std::cout << "  T" << omp_get_thread_num()
                  << " iter=" << n << "\n";
    }

    // ---- dynamic ----
    std::cout << "\n--- schedule(dynamic) ---\n";
    #pragma omp parallel for schedule(dynamic)
    for (int n = 0; n < N; ++n) {
        #pragma omp critical
        std::cout << "  T" << omp_get_thread_num()
                  << " iter=" << n << "\n";
    }

    // ---- guided ----
    std::cout << "\n--- schedule(guided) ---\n";
    #pragma omp parallel for schedule(guided)
    for (int n = 0; n < N; ++n) {
        #pragma omp critical
        std::cout << "  T" << omp_get_thread_num()
                  << " iter=" << n << "\n";
    }

    return 0;
}
```

### 3.3. Наблюдаемое распределение итераций (12 итераций, 4 потока)

#### schedule(static)

| Поток | Итерации |
|:-----:|---------|
| 0 | 0, 1, 2 |
| 1 | 3, 4, 5 |
| 2 | 6, 7, 8 |
| 3 | 9, 10, 11 |

Распределение строго фиксировано и воспроизводится при каждом запуске.

#### schedule(dynamic)

| Поток | Итерации (пример одного запуска) |
|:-----:|----------------------------------|
| 0 | 0, 4, 8, 11 |
| 1 | 1, 5, 9 |
| 2 | 2, 6, 10 |
| 3 | 3, 7 |

Распределение непостоянно — зависит от скорости выполнения каждого потока.

#### schedule(guided)

| Поток | Итерации (пример одного запуска) |
|:-----:|----------------------------------|
| 0 | 0, 1, 2 (чанк 3) |
| 1 | 3, 4, 5 (чанк 3) |
| 2 | 6, 7 (чанк 2) |
| 3 | 8, 9 (чанк 2), 10, 11 (чанк 1+1) |

### 3.4. Выводы

- **`static`** — минимум накладных расходов, лучший выбор при равномерной нагрузке. Распределение детерминировано.
- **`dynamic`** — каждый поток работает, пока есть итерации, независимо от скорости других. Лучшая балансировка при неравномерной трудоёмкости, но высокие накладные расходы на синхронизацию счётчика.
- **`guided`** — начинает с крупных чанков (мало синхронизаций) и уменьшает их к концу (балансировка «хвоста»). Хороший компромисс при большом числе итераций с переменной стоимостью.

---

## Задание 4. Взаимное исключение

### 4.1. Постановка

Продемонстрировать гонку данных без синхронизации, корректное поведение с `atomic` и `critical`. Реализовать поиск минимума в одномерном массиве с измерением производительности.

### 4.2. Текст программы

```cpp
#include <omp.h>
#include <iostream>
#include <iomanip>
#include <vector>
#include <random>
#include <climits>

// ---- Демонстрация atomic ----
void demoAtomic() {
    const int n = 10000, value = 2;
    int counterUnsafe = 0;

    // Без atomic — data race
    #pragma omp parallel for num_threads(4)
    for (int i = 0; i < n; ++i)
        counterUnsafe += value;
    std::cout << "Без atomic (ожидалось 20000): " << counterUnsafe << "\n";

    int counterSafe = 0;
    #pragma omp parallel for num_threads(4)
    for (int i = 0; i < n; ++i) {
        #pragma omp atomic
        counterSafe += value;
    }
    std::cout << "С atomic (ожидалось 20000):   " << counterSafe << "\n";
}

// ---- Поиск минимума с critical ----
int parallelMin(const std::vector<int>& a, int numThreads) {
    int minVal = INT_MAX;
    int size   = (int)a.size();

    #pragma omp parallel for num_threads(numThreads)
    for (int i = 0; i < size; ++i) {
        if (a[i] < minVal) {
            #pragma omp critical
            {
                if (a[i] < minVal)
                    minVal = a[i];
            }
        }
    }
    return minVal;
}

int main() {
    std::cout << "=== Демонстрация atomic ===\n";
    demoAtomic();

    std::cout << "\n=== Поиск минимума в массиве ===\n";
    std::cout << std::setw(14) << "Размер"
              << std::setw(12) << "1 поток"
              << std::setw(12) << "2 потока"
              << std::setw(12) << "4 потока"
              << std::setw(12) << "8 потоков"
              << std::setw(10) << "Мин." << "\n";
    std::cout << std::string(72, '-') << "\n";

    std::mt19937 rng(123);
    std::uniform_int_distribution<int> dist(1, 1'000'000);

    int sizes[] = {10000, 100000, 1000000, 10000000, 100000000};
    for (int sz : sizes) {
        std::vector<int> arr(sz);
        for (int& x : arr) x = dist(rng);
        arr[rng() % sz] = 0;

        auto measure = [&](int t) -> std::pair<double, int> {
            double t0 = omp_get_wtime();
            int res = parallelMin(arr, t);
            return {(omp_get_wtime() - t0) * 1000.0, res};
        };

        auto [ms1, m1] = measure(1);
        auto [ms2, m2] = measure(2);
        auto [ms4, m4] = measure(4);
        auto [ms8, m8] = measure(8);

        std::cout << std::setw(14) << sz
                  << std::setw(12) << std::fixed << std::setprecision(2) << ms1
                  << std::setw(12) << ms2
                  << std::setw(12) << ms4
                  << std::setw(12) << ms8
                  << std::setw(10) << m1 << "\n";
    }

    return 0;
}
```

### 4.3. Таблицы с результатами экспериментов

#### Демонстрация atomic

```
=== Демонстрация atomic ===
Без atomic (ожидалось 20000): 16324
С atomic (ожидалось 20000):   20000
```

Без `atomic` потоки конкурентно читают и записывают общую переменную — часть операций теряется. Результат меняется от запуска к запуску.

#### Поиск минимума (время в мс)

| Размер массива | 1 поток | 2 потока | 4 потока | 8 потоков | Минимум |
|:--------------:|:-------:|:--------:|:--------:|:---------:|:-------:|
| 10 000 | 0.03 | 0.02 | 0.03 | 0.04 | 0 |
| 100 000 | 0.31 | 0.18 | 0.11 | 0.09 | 0 |
| 1 000 000 | 3.1 | 1.6 | 0.9 | 0.7 | 0 |
| 10 000 000 | 30.7 | 15.9 | 8.4 | 6.1 | 0 |
| 100 000 000 | 307 | 156 | 81 | 55 | 0 |

**Ускорение на 8 потоках:** ~5,6× (теоретический предел — 6× из-за числа физических ядер). Ограничение — пропускная способность памяти (memory-bound задача).

---

## Задание 5. Разделение данных между потоками

### 5.1. Постановка

Изучить клаузы `private` / `shared` и директиву `reduction`. Реализовать:
- демонстрацию `private`/`shared`;
- вычисление факториала через `reduction` и `atomic`;
- вычисление числа π методом прямоугольников;
- вычисление скалярного произведения двух векторов.

### 5.2. Текст программы

```cpp
#include <omp.h>
#include <iostream>
#include <iomanip>
#include <vector>
#include <cmath>

// ============================================================
// Демонстрация private/shared
// ============================================================
void demoPrivateShared() {
    int a = 0, b = 0;

    #pragma omp parallel for private(a) shared(b) num_threads(4)
    for (a = 0; a < 50; ++a) {
        #pragma omp atomic
        b += a;
    }
    // b = 0+1+...+49 = 1225
    std::cout << "private(a) shared(b): b = " << b
              << " (ожидалось 1225)\n";
}

// ============================================================
// Факториал
// ============================================================
long long factorialReduction(int number) {
    long long fac = 1;
    #pragma omp parallel for reduction(*:fac) num_threads(4)
    for (int n = 2; n <= number; ++n)
        fac *= n;
    return fac;
}

long long factorialAtomic(int number) {
    long long fac = 1;
    #pragma omp parallel for num_threads(4)
    for (int n = 2; n <= number; ++n) {
        #pragma omp atomic
        fac *= n;
    }
    return fac;
}

// ============================================================
// Число π методом прямоугольников
// ============================================================
double piReduction(long long N, int threads) {
    double sum = 0.0, step = 1.0 / N;
    #pragma omp parallel for reduction(+:sum) num_threads(threads)
    for (long long i = 0; i < N; ++i) {
        double x = (i + 0.5) * step;
        sum += 4.0 / (1.0 + x * x);
    }
    return sum * step;
}

double piAtomic(long long N, int threads) {
    double sum = 0.0, step = 1.0 / N;
    #pragma omp parallel for num_threads(threads)
    for (long long i = 0; i < N; ++i) {
        double x = (i + 0.5) * step;
        double val = 4.0 / (1.0 + x * x);
        #pragma omp atomic
        sum += val;
    }
    return sum * step;
}

double piSerial(long long N) {
    double sum = 0.0, step = 1.0 / N;
    for (long long i = 0; i < N; ++i) {
        double x = (i + 0.5) * step;
        sum += 4.0 / (1.0 + x * x);
    }
    return sum * step;
}

// ============================================================
// Скалярное произведение
// ============================================================
double dotReduction(const std::vector<double>& A,
                    const std::vector<double>& B, int threads) {
    double res = 0.0;
    int n = (int)A.size();
    #pragma omp parallel for reduction(+:res) num_threads(threads)
    for (int i = 0; i < n; ++i)
        res += A[i] * B[i];
    return res;
}

double dotAtomic(const std::vector<double>& A,
                 const std::vector<double>& B, int threads) {
    double res = 0.0;
    int n = (int)A.size();
    #pragma omp parallel for num_threads(threads)
    for (int i = 0; i < n; ++i) {
        #pragma omp atomic
        res += A[i] * B[i];
    }
    return res;
}

int main() {
    // ---- private/shared ----
    std::cout << "=== Демонстрация private/shared ===\n";
    demoPrivateShared();

    // ---- Факториал ----
    std::cout << "\n=== Факториал ===\n";
    for (int n : {5, 10, 12}) {
        std::cout << n << "! = "
                  << factorialReduction(n) << " (reduction), "
                  << factorialAtomic(n)    << " (atomic)\n";
    }

    // ---- Число π ----
    std::cout << "\n=== Вычисление числа π (метод прямоугольников) ===\n";
    std::cout << std::setw(14) << "N"
              << std::setw(18) << "Serial (мс)"
              << std::setw(18) << "Atomic 4T (мс)"
              << std::setw(20) << "Reduction 4T (мс)"
              << std::setw(16) << "π ≈" << "\n";
    std::cout << std::string(86, '-') << "\n";

    for (long long N : {100000LL, 1000000LL, 10000000LL, 100000000LL}) {
        double t0;
        t0 = omp_get_wtime(); double pSer = piSerial(N);   double tSer = (omp_get_wtime()-t0)*1000;
        t0 = omp_get_wtime(); double pAt  = piAtomic(N,4); double tAt  = (omp_get_wtime()-t0)*1000;
        t0 = omp_get_wtime(); double pRed = piReduction(N,4); double tRed = (omp_get_wtime()-t0)*1000;

        std::cout << std::setw(14) << N
                  << std::setw(18) << std::fixed << std::setprecision(2) << tSer
                  << std::setw(18) << tAt
                  << std::setw(20) << tRed
                  << std::setw(16) << std::setprecision(10) << pRed << "\n";
    }

    // ---- Скалярное произведение ----
    std::cout << "\n=== Скалярное произведение ===\n";
    std::cout << std::setw(14) << "N"
              << std::setw(20) << "Atomic 1T (мс)"
              << std::setw(20) << "Atomic 4T (мс)"
              << std::setw(20) << "Reduction 4T (мс)"
              << std::setw(20) << "Reduction 6T (мс)" << "\n";
    std::cout << std::string(94, '-') << "\n";

    for (int sz : {100000, 1000000, 10000000, 100000000}) {
        std::vector<double> A(sz, 1.0), B(sz, 2.0);
        double t0;
        t0 = omp_get_wtime(); dotAtomic(A,B,1);    double ta1=(omp_get_wtime()-t0)*1000;
        t0 = omp_get_wtime(); dotAtomic(A,B,4);    double ta4=(omp_get_wtime()-t0)*1000;
        t0 = omp_get_wtime(); dotReduction(A,B,4); double tr4=(omp_get_wtime()-t0)*1000;
        t0 = omp_get_wtime(); dotReduction(A,B,6); double tr6=(omp_get_wtime()-t0)*1000;

        std::cout << std::setw(14) << sz
                  << std::setw(20) << std::fixed << std::setprecision(2) << ta1
                  << std::setw(20) << ta4
                  << std::setw(20) << tr4
                  << std::setw(20) << tr6 << "\n";
    }

    return 0;
}
```

### 5.3. Таблицы с результатами экспериментов

**Конфигурация:** AMD Ryzen 5 5600H (6 ядер / 12 потоков), GCC 13.2, `-fopenmp -O2`.

#### Факториал

| n | n! | reduction (4T) | atomic (4T) | Корректность |
|:-:|---:|:--------------:|:-----------:|:------------:|
| 5 | 120 | 120 | 120 | ✓ |
| 10 | 3 628 800 | 3 628 800 | 3 628 800 | ✓ |
| 12 | 479 001 600 | 479 001 600 | 479 001 600 | ✓ |

> При `n > 20` значение факториала превышает диапазон `long long` — для больших значений необходима библиотека произвольной точности.

#### Вычисление числа π (4 потока)

| N | Serial (мс) | Atomic 4T (мс) | Reduction 4T (мс) | π ≈ |
|:-:|:-----------:|:--------------:|:-----------------:|:---:|
| 100 000 | 0.55 | 12.8 | 0.17 | 3.1415926536 |
| 1 000 000 | 5.4 | 127.4 | 1.56 | 3.1415926536 |
| 10 000 000 | 54.1 | 1 274 | 14.8 | 3.1415926536 |
| 100 000 000 | 541 | >12 000 | 144 | 3.1415926536 |

> **Ключевое наблюдение:** `atomic`-версия работает в 20–23 раза **медленнее** последовательной! Причина — на каждой из N итераций все 4 потока ждут доступа к одной переменной. `reduction` устраняет конкуренцию: каждый поток ведёт приватный аккумулятор и производит одно слияние в конце.

#### Скалярное произведение

| N | Atomic 1T (мс) | Atomic 4T (мс) | Reduction 4T (мс) | Reduction 6T (мс) |
|:-:|:--------------:|:--------------:|:-----------------:|:-----------------:|
| 100 000 | 1.2 | 5.1 | 0.34 | 0.28 |
| 1 000 000 | 12.1 | 50.9 | 3.2 | 2.5 |
| 10 000 000 | 121 | 510 | 31.4 | 21.7 |
| 100 000 000 | 1 215 | 5 104 | 314 | 210 |

> `Reduction` на 6 потоках ускоряет вычисление ~5,8× по сравнению с последовательным. Версия с `atomic` деградирует: больше потоков → больше конкуренции → медленнее.

---

## Итоговые выводы

1. **OpenMP** — минималистичный инструмент параллелизма: одна директива `#pragma omp parallel for` может дать ускорение в несколько раз без переписывания алгоритма. Требует флага компилятора (`-fopenmp` в GCC/Clang, `/openmp` в MSVC) и заголовочного файла `<omp.h>`.

2. **Параллельный регион** создаёт пул потоков; `std::cout` не является потокобезопасным, поэтому для корректного вывода необходима синхронизация (`mutex` или `#pragma omp critical`).

3. **Эффективность параллельного цикла** зависит от размера данных. Накладные расходы OpenMP (создание потоков, барьеры) оправданы только при достаточно большом числе итераций (~10⁵ и выше для математических функций).

4. **Планирование итераций:**
   - `static` — детерминированное, нулевые накладные расходы на балансировку, лучший выбор при равномерной нагрузке;
   - `dynamic` — адаптивное, устраняет дисбаланс, но требует синхронизации на каждом чанке;
   - `guided` — убывающий чанк, хороший компромисс между накладными расходами и балансировкой.

5. **`reduction` vs `atomic` в цикле:** `atomic` при большом числе итераций порождает сильную конкуренцию потоков и может работать хуже последовательного кода. `reduction` создаёт приватные копии и объединяет их один раз — является предпочтительным способом параллельного накопления.

6. **Ограничение — пропускная способность памяти:** задачи типа «поиск минимума» и «скалярное произведение» являются memory-bound и масштабируются хуже, чем compute-bound задачи (вычисление синуса, π). На 6-ядерном процессоре достигнуто ускорение ~5,8× вместо теоретических 6×.

---

*Дата выполнения: 13.04.2026. Источники: openmp.org, Гергель В.П. «Высокопроизводительные вычисления для многоядерных многопроцессорных систем», Глава 5.*

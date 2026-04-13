// Лабораторная работа 9. Задание 4: Взаимное исключение
// Студент: Шибитов Николай, Группа 11, Курс 3
//
// Компиляция (GCC):   g++ -fopenmp -O2 -o task4 task4_mutual_exclusion.cpp
// Компиляция (MSVC):  cl /openmp /O2 task4_mutual_exclusion.cpp

#include <omp.h>
#include <iostream>
#include <iomanip>
#include <vector>
#include <random>
#include <climits>

// ============================================================
// Демонстрация atomic: счётчик
// ============================================================
void demoAtomic() {
    const int n = 10000, value = 2;

    // Без atomic — data race, результат некорректен
    int counterUnsafe = 0;
    #pragma omp parallel for num_threads(4)
    for (int i = 0; i < n; ++i)
        counterUnsafe += value;
    std::cout << "Без atomic (ожидалось " << n * value
              << "): counter = " << counterUnsafe << "\n";

    // С atomic — атомарная операция, результат всегда корректен
    int counterSafe = 0;
    #pragma omp parallel for num_threads(4)
    for (int i = 0; i < n; ++i) {
        #pragma omp atomic
        counterSafe += value;
    }
    std::cout << "С atomic    (ожидалось " << n * value
              << "): counter = " << counterSafe << "\n";
}

// ============================================================
// Демонстрация critical: поиск максимума
// ============================================================
void demoMax(const std::vector<int>& a) {
    int maxVal = a[0];
    int size   = (int)a.size();

    #pragma omp parallel for num_threads(4)
    for (int i = 1; i < size; ++i) {
        if (a[i] > maxVal) {
            #pragma omp critical
            {
                // Двойная проверка: другой поток мог изменить maxVal
                if (a[i] > maxVal)
                    maxVal = a[i];
            }
        }
    }
    std::cout << "Максимум (critical): " << maxVal << "\n";
}

// ============================================================
// Поиск минимума — эксперимент с разным числом потоков
// ============================================================
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
    // --- Демо atomic ---
    std::cout << "=== Демонстрация atomic ===\n";
    demoAtomic();

    // --- Демо critical (поиск максимума) ---
    std::cout << "\n=== Демонстрация critical (поиск максимума) ===\n";
    std::vector<int> sample = {5, 12, 3, 99, 7, 42, 1, 88};
    demoMax(sample);

    // --- Эксперимент: поиск минимума ---
    std::cout << "\n=== Поиск минимума: время (мс) ===\n";
    std::cout << std::setw(14) << "Размер"
              << std::setw(12) << "1 поток"
              << std::setw(12) << "2 потока"
              << std::setw(12) << "4 потока"
              << std::setw(12) << "6 потоков"
              << std::setw(10) << "Мин." << "\n";
    std::cout << std::string(72, '-') << "\n";

    std::mt19937 rng(123);
    std::uniform_int_distribution<int> dist(1, 1'000'000);

    int sizes[] = {10000, 100000, 1000000, 10000000, 100000000};
    for (int sz : sizes) {
        std::vector<int> arr(sz);
        for (int& x : arr) x = dist(rng);
        arr[rng() % sz] = 0; // гарантированный минимум

        auto measure = [&](int t) -> std::pair<double, int> {
            double t0 = omp_get_wtime();
            int res   = parallelMin(arr, t);
            return {(omp_get_wtime() - t0) * 1000.0, res};
        };

        auto [ms1, m1] = measure(1);
        auto [ms2, m2] = measure(2);
        auto [ms4, m4] = measure(4);
        auto [ms6, m6] = measure(6);

        std::cout << std::setw(14) << sz
                  << std::setw(12) << std::fixed << std::setprecision(2) << ms1
                  << std::setw(12) << ms2
                  << std::setw(12) << ms4
                  << std::setw(12) << ms6
                  << std::setw(10) << m1 << "\n";
    }

    return 0;
}

// Лабораторная работа 9. Задание 3: Планирование цикла
// Студент: Шибитов Николай, Группа 11, Курс 3
//
// Компиляция (GCC):   g++ -fopenmp -O2 -o task3 task3_schedule.cpp
// Компиляция (MSVC):  cl /openmp /O2 task3_schedule.cpp

#include <omp.h>
#include <iostream>
#include <iomanip>
#include <vector>
#include <cmath>

static const int N = 12;
static const int T = 4;

// Вывод итераций с указанием потока
void printIter(int n, int thread) {
    #pragma omp critical
    std::cout << "  T" << thread << " -> iter " << n << "\n";
}

// Замер времени суммирования с разными стратегиями для тяжёлых итераций
double benchSchedule(int schedule_type, int numThreads, int iters) {
    std::vector<double> result(iters, 0.0);
    omp_set_num_threads(numThreads);

    double t0 = omp_get_wtime();

    if (schedule_type == 0) {
        #pragma omp parallel for schedule(static)
        for (int i = 0; i < iters; ++i)
            result[i] = std::sin((double)i) + std::cos((double)i);
    } else if (schedule_type == 1) {
        #pragma omp parallel for schedule(dynamic)
        for (int i = 0; i < iters; ++i)
            result[i] = std::sin((double)i) + std::cos((double)i);
    } else {
        #pragma omp parallel for schedule(guided)
        for (int i = 0; i < iters; ++i)
            result[i] = std::sin((double)i) + std::cos((double)i);
    }

    return (omp_get_wtime() - t0) * 1000.0;
}

int main() {
    omp_set_num_threads(T);

    // --- Распределение итераций: static ---
    std::cout << "=== schedule(static) — " << N
              << " итераций, " << T << " потока ===\n";
    #pragma omp parallel for schedule(static) num_threads(T)
    for (int n = 0; n < N; ++n)
        printIter(n, omp_get_thread_num());

    // --- Распределение итераций: dynamic ---
    std::cout << "\n=== schedule(dynamic) — " << N
              << " итераций, " << T << " потока ===\n";
    #pragma omp parallel for schedule(dynamic) num_threads(T)
    for (int n = 0; n < N; ++n)
        printIter(n, omp_get_thread_num());

    // --- Распределение итераций: guided ---
    std::cout << "\n=== schedule(guided) — " << N
              << " итераций, " << T << " потока ===\n";
    #pragma omp parallel for schedule(guided) num_threads(T)
    for (int n = 0; n < N; ++n)
        printIter(n, omp_get_thread_num());

    // --- Замеры производительности (1M итераций) ---
    const int BIG = 1000000;
    std::cout << "\n=== Производительность (" << BIG << " итераций) ===\n";
    std::cout << std::setw(12) << "Потоки"
              << std::setw(16) << "static (мс)"
              << std::setw(16) << "dynamic (мс)"
              << std::setw(16) << "guided (мс)" << "\n";
    std::cout << std::string(60, '-') << "\n";

    for (int t : {1, 2, 4, 6}) {
        std::cout << std::setw(12) << t
                  << std::setw(16) << std::fixed << std::setprecision(2)
                  << benchSchedule(0, t, BIG)
                  << std::setw(16) << benchSchedule(1, t, BIG)
                  << std::setw(16) << benchSchedule(2, t, BIG) << "\n";
    }

    return 0;
}

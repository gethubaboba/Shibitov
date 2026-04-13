// Лабораторная работа 9. Задание 2: Параллельный цикл
// Студент: Шибитов Николай, Группа 11, Курс 3
//
// Компиляция (GCC):   g++ -fopenmp -O2 -o task2 task2_parallel_for.cpp
// Компиляция (MSVC):  cl /openmp /O2 task2_parallel_for.cpp

#include <omp.h>
#include <cmath>
#include <iostream>
#include <iomanip>
#include <vector>

static const double MY_PI = 3.141592653589793;

// Инициализация таблицы синусов: возвращает время в мс
double initSinTable(int size, int numThreads) {
    std::vector<double> sinTable(size);
    omp_set_num_threads(numThreads);

    double t0 = omp_get_wtime();

    #pragma omp parallel for
    for (int n = 0; n < size; ++n)
        sinTable[n] = std::sin(2.0 * MY_PI * n / size);

    return (omp_get_wtime() - t0) * 1000.0;
}

int main() {
    // --- Часть 1: как распределяются итерации ---
    std::cout << "=== Распределение итераций (10 итераций, 4 потока) ===\n";
    omp_set_num_threads(4);

    #pragma omp parallel for
    for (int n = 0; n < 10; ++n) {
        #pragma omp critical
        std::cout << "  thread " << omp_get_thread_num()
                  << " -> iteration " << n << "\n";
    }

    // --- Часть 2: эквивалентный ручной код ---
    std::cout << "\n=== Эквивалентный ручной код (для понимания) ===\n";
    #pragma omp parallel num_threads(4)
    {
        int this_thread = omp_get_thread_num();
        int num_threads = omp_get_num_threads();
        int my_start = this_thread * 10 / num_threads;
        int my_end   = (this_thread + 1) * 10 / num_threads;

        for (int n = my_start; n < my_end; ++n) {
            #pragma omp critical
            std::cout << "  thread " << this_thread
                      << " -> iteration " << n << "\n";
        }
    }

    // --- Часть 3: таблица синусов, замеры времени ---
    std::cout << "\n=== Таблица синусов: время инициализации (мс) ===\n";
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
                  << initSinTable(sz, 1)
                  << std::setw(12) << initSinTable(sz, 2)
                  << std::setw(12) << initSinTable(sz, 4)
                  << std::setw(12) << initSinTable(sz, 8) << "\n";
    }

    return 0;
}

// Лабораторная работа 9. Задание 1: Параллельный регион
// Студент: Шибитов Николай, Группа 11, Курс 3
//
// Компиляция (GCC):   g++ -fopenmp -O2 -o task1 task1_parallel_region.cpp
// Компиляция (MSVC):  cl /openmp /O2 task1_parallel_region.cpp

#include <omp.h>
#include <iostream>
#include <mutex>

std::mutex cout_mutex;

int main() {
    omp_set_num_threads(4);

    std::cout << "Число потоков: " << 4 << "\n\n";

    // --- Шаг 1: базовая параллельная область ---
    std::cout << "--- Базовый параллельный регион ---\n";
    #pragma omp parallel
    {
        printf("Hello!\n");
    }

    // --- Шаг 2: вывод номера потока (небезопасный, строки могут перемешаться) ---
    std::cout << "\n--- Вывод номера потока (без синхронизации, возможно перемешивание) ---\n";
    #pragma omp parallel
    {
        int id = omp_get_thread_num();
        int total = omp_get_num_threads();
        // Намеренно без синхронизации — демонстрация проблемы
        std::cout << "Thread " << id << " of " << total << "\n";
    }

    // --- Шаг 3: вывод с синхронизацией через mutex ---
    std::cout << "\n--- Вывод номера потока (с синхронизацией через mutex) ---\n";
    #pragma omp parallel
    {
        int id = omp_get_thread_num();
        int total = omp_get_num_threads();

        std::lock_guard<std::mutex> lock(cout_mutex);
        std::cout << "Thread " << id << " of " << total
                  << " | wtime = " << omp_get_wtime() << "\n";
    }

    // --- Шаг 4: вывод с синхронизацией через #pragma omp critical ---
    std::cout << "\n--- Вывод номера потока (с синхронизацией через critical) ---\n";
    #pragma omp parallel
    {
        int id = omp_get_thread_num();
        int total = omp_get_num_threads();

        #pragma omp critical
        {
            std::cout << "Thread " << id << " of " << total << " says Hello!\n";
        }
    }

    return 0;
}

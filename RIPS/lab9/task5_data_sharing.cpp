// Лабораторная работа 9. Задание 5: Разделение данных между потоками
// Студент: Шибитов Николай, Группа 11, Курс 3
//
// Компиляция (GCC):   g++ -fopenmp -O2 -o task5 task5_data_sharing.cpp
// Компиляция (MSVC):  cl /openmp /O2 task5_data_sharing.cpp

#include <omp.h>
#include <iostream>
#include <iomanip>
#include <vector>
#include <cmath>

// ============================================================
// Демонстрация private / shared
// ============================================================
void demoPrivateShared() {
    int a = 0, b = 0;

    // a — private (каждый поток имеет собственную копию)
    // b — shared  (все потоки обращаются к одной переменной)
    #pragma omp parallel for private(a) shared(b) num_threads(4)
    for (a = 0; a < 50; ++a) {
        #pragma omp atomic
        b += a; // b накапливает 0+1+...+49 = 1225
    }

    std::cout << "private(a) shared(b): b = " << b
              << " (ожидалось 1225)\n";
}

// ============================================================
// Факториал с reduction
// ============================================================
long long factorialReduction(int number) {
    long long fac = 1;
    #pragma omp parallel for reduction(*:fac) num_threads(4)
    for (int n = 2; n <= number; ++n)
        fac *= n;
    return fac;
}

// Факториал с atomic (медленнее из-за конкуренции)
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
double piSerial(long long N) {
    double sum = 0.0, step = 1.0 / N;
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
        double x   = (i + 0.5) * step;
        double val = 4.0 / (1.0 + x * x);
        #pragma omp atomic
        sum += val;
    }
    return sum * step;
}

double piReduction(long long N, int threads) {
    double sum = 0.0, step = 1.0 / N;
    #pragma omp parallel for reduction(+:sum) num_threads(threads)
    for (long long i = 0; i < N; ++i) {
        double x = (i + 0.5) * step;
        sum += 4.0 / (1.0 + x * x);
    }
    return sum * step;
}

// ============================================================
// Скалярное произведение
// ============================================================
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

double dotReduction(const std::vector<double>& A,
                    const std::vector<double>& B, int threads) {
    double res = 0.0;
    int n = (int)A.size();
    #pragma omp parallel for reduction(+:res) num_threads(threads)
    for (int i = 0; i < n; ++i)
        res += A[i] * B[i];
    return res;
}

int main() {
    // --- private / shared ---
    std::cout << "=== Демонстрация private/shared ===\n";
    demoPrivateShared();

    // --- Факториал ---
    std::cout << "\n=== Факториал ===\n";
    for (int n : {5, 10, 12}) {
        long long r = factorialReduction(n);
        long long a = factorialAtomic(n);
        std::cout << n << "! = " << r
                  << " (reduction), " << a << " (atomic)\n";
    }

    // --- Число π ---
    std::cout << "\n=== Вычисление числа π (метод прямоугольников) ===\n";
    std::cout << std::setw(14) << "N"
              << std::setw(18) << "Serial (мс)"
              << std::setw(18) << "Atomic 4T (мс)"
              << std::setw(20) << "Reduction 4T (мс)"
              << std::setw(16) << "π ≈" << "\n";
    std::cout << std::string(86, '-') << "\n";

    for (long long N : {100000LL, 1000000LL, 10000000LL, 100000000LL}) {
        double t0;

        t0 = omp_get_wtime();
        double pSer = piSerial(N);
        double tSer = (omp_get_wtime() - t0) * 1000.0;

        t0 = omp_get_wtime();
        double pAt = piAtomic(N, 4);
        double tAt = (omp_get_wtime() - t0) * 1000.0;

        t0 = omp_get_wtime();
        double pRed = piReduction(N, 4);
        double tRed = (omp_get_wtime() - t0) * 1000.0;

        std::cout << std::setw(14) << N
                  << std::setw(18) << std::fixed << std::setprecision(2) << tSer
                  << std::setw(18) << tAt
                  << std::setw(20) << tRed
                  << std::setw(16) << std::setprecision(10) << pRed << "\n";
    }

    // --- Скалярное произведение ---
    std::cout << "\n=== Скалярное произведение (A[i]=1.0, B[i]=2.0, ожидается 2*N) ===\n";
    std::cout << std::setw(14) << "N"
              << std::setw(20) << "Atomic 1T (мс)"
              << std::setw(20) << "Atomic 4T (мс)"
              << std::setw(20) << "Reduction 4T (мс)"
              << std::setw(20) << "Reduction 6T (мс)" << "\n";
    std::cout << std::string(94, '-') << "\n";

    for (int sz : {100000, 1000000, 10000000, 100000000}) {
        std::vector<double> A(sz, 1.0), B(sz, 2.0);
        double t0;

        t0 = omp_get_wtime(); dotAtomic(A, B, 1);    double ta1 = (omp_get_wtime()-t0)*1000;
        t0 = omp_get_wtime(); dotAtomic(A, B, 4);    double ta4 = (omp_get_wtime()-t0)*1000;
        t0 = omp_get_wtime(); dotReduction(A, B, 4); double tr4 = (omp_get_wtime()-t0)*1000;
        t0 = omp_get_wtime(); dotReduction(A, B, 6); double tr6 = (omp_get_wtime()-t0)*1000;

        std::cout << std::setw(14) << sz
                  << std::setw(20) << std::fixed << std::setprecision(2) << ta1
                  << std::setw(20) << ta4
                  << std::setw(20) << tr4
                  << std::setw(20) << tr6 << "\n";
    }

    return 0;
}

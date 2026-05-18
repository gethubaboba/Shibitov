#include <omp.h>

#include <cmath>
#include <iomanip>
#include <iostream>
#include <vector>

double pi_spmd(long long intervals, int thread_count) {
    const double step = 1.0 / static_cast<double>(intervals);
    double global_sum = 0.0;

    omp_set_num_threads(thread_count);

    #pragma omp parallel
    {
        const int rank = omp_get_thread_num();
        const int workers = omp_get_num_threads();
        double local_sum = 0.0;

        for (long long i = rank; i < intervals; i += workers) {
            const double x = (static_cast<double>(i) + 0.5) * step;
            local_sum += 4.0 / (1.0 + x * x);
        }

        #pragma omp atomic
        global_sum += local_sum;
    }

    return global_sum * step;
}

int main() {
    const long long intervals = 50'000'000;
    const std::vector<int> thread_counts = {1, 2, 4, 8};
    const double reference_pi = std::acos(-1.0);

    std::cout << "Lab 10. Task 1. OpenMP SPMD pattern\n";
    std::cout << "Integral formula: pi = integral(0..1) 4 / (1 + x^2) dx\n";
    std::cout << "Intervals: " << intervals << "\n\n";

    std::cout << std::setw(10) << "threads"
              << std::setw(16) << "time, ms"
              << std::setw(20) << "pi"
              << std::setw(16) << "abs error" << '\n';
    std::cout << std::string(62, '-') << '\n';

    for (int threads : thread_counts) {
        const double start = omp_get_wtime();
        const double pi = pi_spmd(intervals, threads);
        const double elapsed_ms = (omp_get_wtime() - start) * 1000.0;

        std::cout << std::setw(10) << threads
                  << std::setw(16) << std::fixed << std::setprecision(3) << elapsed_ms
                  << std::setw(20) << std::setprecision(12) << pi
                  << std::setw(16) << std::scientific << std::setprecision(3)
                  << std::abs(pi - reference_pi)
                  << std::fixed << '\n';
    }

    return 0;
}

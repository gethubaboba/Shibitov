#include <omp.h>

#include <cmath>
#include <iomanip>
#include <iostream>
#include <vector>

double partial_integral(long long begin, long long end, double step) {
    double sum = 0.0;
    for (long long i = begin; i < end; ++i) {
        const double x = (static_cast<double>(i) + 0.5) * step;
        sum += 4.0 / (1.0 + x * x);
    }
    return sum;
}

double divide_and_conquer_sum(long long begin, long long end, double step, long long cutoff) {
    if (end - begin <= cutoff) {
        return partial_integral(begin, end, step);
    }

    const long long middle = begin + (end - begin) / 2;
    double left = 0.0;
    double right = 0.0;

    #pragma omp task shared(left)
    left = divide_and_conquer_sum(begin, middle, step, cutoff);

    #pragma omp task shared(right)
    right = divide_and_conquer_sum(middle, end, step, cutoff);

    #pragma omp taskwait
    return left + right;
}

double pi_divide_and_conquer(long long intervals, int thread_count, long long cutoff) {
    const double step = 1.0 / static_cast<double>(intervals);
    double sum = 0.0;

    omp_set_num_threads(thread_count);

    #pragma omp parallel
    {
        #pragma omp single
        sum = divide_and_conquer_sum(0, intervals, step, cutoff);
    }

    return sum * step;
}

int main() {
    const long long intervals = 50'000'000;
    const std::vector<int> thread_counts = {1, 2, 4, 8};
    const std::vector<long long> cutoffs = {50'000, 500'000, 5'000'000};
    const double reference_pi = std::acos(-1.0);

    std::cout << "Lab 10. Task 3. Divide and conquer with OpenMP tasks\n";
    std::cout << "Intervals: " << intervals << "\n\n";

    std::cout << std::setw(10) << "threads"
              << std::setw(14) << "cutoff"
              << std::setw(16) << "time, ms"
              << std::setw(20) << "pi"
              << std::setw(16) << "abs error" << '\n';
    std::cout << std::string(76, '-') << '\n';

    for (int threads : thread_counts) {
        for (long long cutoff : cutoffs) {
            const double start = omp_get_wtime();
            const double pi = pi_divide_and_conquer(intervals, threads, cutoff);
            const double elapsed_ms = (omp_get_wtime() - start) * 1000.0;

            std::cout << std::setw(10) << threads
                      << std::setw(14) << cutoff
                      << std::setw(16) << std::fixed << std::setprecision(3) << elapsed_ms
                      << std::setw(20) << std::setprecision(12) << pi
                      << std::setw(16) << std::scientific << std::setprecision(3)
                      << std::abs(pi - reference_pi)
                      << std::fixed << '\n';
        }
    }

    return 0;
}

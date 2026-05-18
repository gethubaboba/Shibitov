#include <omp.h>

#include <cmath>
#include <iomanip>
#include <iostream>
#include <string>
#include <vector>

enum class ScheduleKind {
    Static,
    Dynamic,
    Guided
};

double pi_parallel_for(long long intervals, int thread_count, ScheduleKind schedule_kind) {
    const double step = 1.0 / static_cast<double>(intervals);
    double sum = 0.0;

    omp_set_num_threads(thread_count);

    switch (schedule_kind) {
        case ScheduleKind::Static:
            #pragma omp parallel for schedule(static) reduction(+:sum)
            for (long long i = 0; i < intervals; ++i) {
                const double x = (static_cast<double>(i) + 0.5) * step;
                sum += 4.0 / (1.0 + x * x);
            }
            break;
        case ScheduleKind::Dynamic:
            #pragma omp parallel for schedule(dynamic, 4096) reduction(+:sum)
            for (long long i = 0; i < intervals; ++i) {
                const double x = (static_cast<double>(i) + 0.5) * step;
                sum += 4.0 / (1.0 + x * x);
            }
            break;
        case ScheduleKind::Guided:
            #pragma omp parallel for schedule(guided, 4096) reduction(+:sum)
            for (long long i = 0; i < intervals; ++i) {
                const double x = (static_cast<double>(i) + 0.5) * step;
                sum += 4.0 / (1.0 + x * x);
            }
            break;
    }

    return sum * step;
}

std::string name(ScheduleKind schedule_kind) {
    switch (schedule_kind) {
        case ScheduleKind::Static:
            return "static";
        case ScheduleKind::Dynamic:
            return "dynamic";
        case ScheduleKind::Guided:
            return "guided";
    }
    return "unknown";
}

int main() {
    const long long intervals = 50'000'000;
    const std::vector<int> thread_counts = {1, 2, 4, 8};
    const std::vector<ScheduleKind> schedules = {
        ScheduleKind::Static,
        ScheduleKind::Dynamic,
        ScheduleKind::Guided
    };
    const double reference_pi = std::acos(-1.0);

    std::cout << "Lab 10. Task 2. OpenMP parallel for pattern\n";
    std::cout << "Intervals: " << intervals << "\n\n";

    std::cout << std::setw(10) << "schedule"
              << std::setw(10) << "threads"
              << std::setw(16) << "time, ms"
              << std::setw(20) << "pi"
              << std::setw(16) << "abs error" << '\n';
    std::cout << std::string(72, '-') << '\n';

    for (ScheduleKind schedule_kind : schedules) {
        for (int threads : thread_counts) {
            const double start = omp_get_wtime();
            const double pi = pi_parallel_for(intervals, threads, schedule_kind);
            const double elapsed_ms = (omp_get_wtime() - start) * 1000.0;

            std::cout << std::setw(10) << name(schedule_kind)
                      << std::setw(10) << threads
                      << std::setw(16) << std::fixed << std::setprecision(3) << elapsed_ms
                      << std::setw(20) << std::setprecision(12) << pi
                      << std::setw(16) << std::scientific << std::setprecision(3)
                      << std::abs(pi - reference_pi)
                      << std::fixed << '\n';
        }
    }

    return 0;
}

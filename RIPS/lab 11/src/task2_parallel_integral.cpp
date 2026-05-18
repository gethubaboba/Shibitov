#include <chrono>
#include <cmath>
#include <functional>
#include <iomanip>
#include <iostream>
#include <string>
#include <thread>
#include <vector>

using Clock = std::chrono::high_resolution_clock;

struct IntegralTask {
    std::string name;
    std::function<double(double)> function;
    double a;
    double b;
    double exact;
};

double integrate_left_rectangles(
    const std::function<double(double)>& f,
    double a,
    double b,
    int intervals,
    unsigned thread_count
) {
    const double step = (b - a) / static_cast<double>(intervals);
    std::vector<double> partial(thread_count, 0.0);
    std::vector<std::thread> threads;
    threads.reserve(thread_count);

    const int block = intervals / static_cast<int>(thread_count);
    for (unsigned t = 0; t < thread_count; ++t) {
        const int begin = static_cast<int>(t) * block;
        const int end = (t + 1 == thread_count)
            ? intervals
            : begin + block;

        threads.emplace_back([&, begin, end, t] {
            double local = 0.0;
            for (int i = begin; i < end; ++i) {
                const double x = a + static_cast<double>(i) * step;
                local += f(x);
            }
            partial[t] = local;
        });
    }

    for (auto& thread : threads) {
        thread.join();
    }

    double sum = 0.0;
    for (double item : partial) {
        sum += item;
    }
    return sum * step;
}

template <typename Func>
double measure_ms(Func&& func, double& result) {
    const auto start = Clock::now();
    result = func();
    const auto finish = Clock::now();
    return std::chrono::duration<double, std::milli>(finish - start).count();
}

int main() {
    const std::vector<IntegralTask> tasks = {
        {"x^2 on [0,1]", [](double x) { return x * x; }, 0.0, 1.0, 1.0 / 3.0},
        {"sin(x) on [0,pi]", [](double x) { return std::sin(x); }, 0.0, std::acos(-1.0), 2.0},
        {"exp(x) on [0,1]", [](double x) { return std::exp(x); }, 0.0, 1.0, std::exp(1.0) - 1.0}
    };
    const std::vector<int> sizes = {10'000, 100'000, 1'000'000, 5'000'000};
    const std::vector<unsigned> thread_counts = {2, 4};

    std::cout << "Lab 11. Task 2. Parallel definite integral\n";
    std::cout << "Method: left rectangles\n\n";

    std::cout << std::setw(18) << "function"
              << std::setw(12) << "intervals"
              << std::setw(10) << "threads"
              << std::setw(14) << "time, ms"
              << std::setw(16) << "value"
              << std::setw(14) << "abs error" << '\n';
    std::cout << std::string(84, '-') << '\n';

    for (const auto& task : tasks) {
        for (int intervals : sizes) {
            for (unsigned threads : thread_counts) {
                double result = 0.0;
                const double elapsed = measure_ms(
                    [&] {
                        return integrate_left_rectangles(
                            task.function,
                            task.a,
                            task.b,
                            intervals,
                            threads
                        );
                    },
                    result
                );

                std::cout << std::setw(18) << task.name
                          << std::setw(12) << intervals
                          << std::setw(10) << threads
                          << std::setw(14) << std::fixed << std::setprecision(3) << elapsed
                          << std::setw(16) << std::setprecision(8) << result
                          << std::setw(14) << std::scientific << std::setprecision(2)
                          << std::abs(result - task.exact)
                          << std::fixed << '\n';
            }
        }
    }

    return 0;
}

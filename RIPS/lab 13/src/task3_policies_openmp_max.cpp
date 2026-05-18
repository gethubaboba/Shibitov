#include <algorithm>
#include <chrono>
#include <execution>
#include <iomanip>
#include <iostream>
#include <limits>
#include <omp.h>
#include <random>
#include <string>
#include <vector>

using Clock = std::chrono::high_resolution_clock;

std::vector<int> make_data(std::size_t size) {
    std::mt19937 gen(101);
    std::uniform_int_distribution<int> dist(-1'000'000, 1'000'000);

    std::vector<int> data(size);
    std::generate(data.begin(), data.end(), [&] { return dist(gen); });
    if (!data.empty()) {
        data[size * 2 / 3] = 4'000'000;
    }
    return data;
}

int openmp_max(const std::vector<int>& data, int thread_count) {
    int answer = std::numeric_limits<int>::min();
    const int size = static_cast<int>(data.size());

    #pragma omp parallel for num_threads(thread_count) reduction(max:answer)
    for (int i = 0; i < size; ++i) {
        answer = std::max(answer, data[static_cast<std::size_t>(i)]);
    }

    return answer;
}

template <typename Func>
double measure_ms(Func&& func, int& result) {
    const auto start = Clock::now();
    result = func();
    const auto finish = Clock::now();
    return std::chrono::duration<double, std::milli>(finish - start).count();
}

void print_row(const std::string& method, std::size_t size, double elapsed, int result) {
    std::cout << std::setw(18) << method
              << std::setw(12) << size
              << std::setw(14) << std::fixed << std::setprecision(3) << elapsed
              << std::setw(12) << result
              << '\n';
}

int main() {
    const std::vector<std::size_t> sizes = {1'000'000, 5'000'000, 10'000'000};

    std::cout << "Task 3. Standard execution policies and OpenMP maximum search\n";
    std::cout << std::setw(18) << "method"
              << std::setw(12) << "size"
              << std::setw(14) << "time, ms"
              << std::setw(12) << "max"
              << '\n';
    std::cout << std::string(56, '-') << '\n';

    for (std::size_t size : sizes) {
        const auto data = make_data(size);
        int result = 0;

        double elapsed = measure_ms(
            [&] { return *std::max_element(std::execution::seq, data.begin(), data.end()); },
            result
        );
        print_row("std::seq", size, elapsed, result);

        elapsed = measure_ms(
            [&] { return *std::max_element(std::execution::par, data.begin(), data.end()); },
            result
        );
        print_row("std::par", size, elapsed, result);

        elapsed = measure_ms(
            [&] { return *std::max_element(std::execution::par_unseq, data.begin(), data.end()); },
            result
        );
        print_row("std::par_unseq", size, elapsed, result);

        elapsed = measure_ms(
            [&] { return openmp_max(data, 4); },
            result
        );
        print_row("OpenMP 4 th", size, elapsed, result);
    }

    return 0;
}


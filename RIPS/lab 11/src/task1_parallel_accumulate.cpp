#include <algorithm>
#include <chrono>
#include <iomanip>
#include <iostream>
#include <numeric>
#include <thread>
#include <vector>

using Clock = std::chrono::high_resolution_clock;

template <typename Iterator, typename T>
T parallel_accumulate(Iterator first, Iterator last, T init, unsigned thread_count) {
    const auto length = static_cast<std::size_t>(std::distance(first, last));
    if (length == 0) {
        return init;
    }

    thread_count = std::max(1u, std::min<unsigned>(thread_count, static_cast<unsigned>(length)));
    const std::size_t block_size = length / thread_count;

    std::vector<T> partial_results(thread_count);
    std::vector<std::thread> threads;
    threads.reserve(thread_count - 1);

    Iterator block_start = first;
    for (unsigned i = 0; i + 1 < thread_count; ++i) {
        Iterator block_end = std::next(block_start, static_cast<std::ptrdiff_t>(block_size));
        threads.emplace_back([block_start, block_end, &partial_results, i] {
            partial_results[i] = std::accumulate(block_start, block_end, T{});
        });
        block_start = block_end;
    }

    partial_results[thread_count - 1] = std::accumulate(block_start, last, T{});

    for (auto& thread : threads) {
        thread.join();
    }

    return std::accumulate(partial_results.begin(), partial_results.end(), init);
}

template <typename Func>
double measure_ms(Func&& func, double& result) {
    const auto start = Clock::now();
    result = func();
    const auto finish = Clock::now();
    return std::chrono::duration<double, std::milli>(finish - start).count();
}

int main() {
    const std::vector<std::size_t> sizes = {1'000'000, 5'000'000, 10'000'000, 20'000'000};
    const std::vector<unsigned> thread_counts = {1, 2, 4, 8};

    std::cout << "Lab 11. Task 1. Parallel std::accumulate\n";
    std::cout << std::setw(12) << "size"
              << std::setw(10) << "threads"
              << std::setw(14) << "time, ms"
              << std::setw(16) << "sum" << '\n';
    std::cout << std::string(52, '-') << '\n';

    for (std::size_t size : sizes) {
        std::vector<double> data(size);
        std::iota(data.begin(), data.end(), 1.0);

        for (unsigned threads : thread_counts) {
            double result = 0.0;
            const double elapsed = measure_ms(
                [&] { return parallel_accumulate(data.begin(), data.end(), 0.0, threads); },
                result
            );

            std::cout << std::setw(12) << size
                      << std::setw(10) << threads
                      << std::setw(14) << std::fixed << std::setprecision(3) << elapsed
                      << std::setw(16) << std::setprecision(0) << result
                      << '\n';
        }
    }

    return 0;
}

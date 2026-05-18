#include <algorithm>
#include <chrono>
#include <iomanip>
#include <iostream>
#include <limits>
#include <random>
#include <thread>
#include <vector>

using Clock = std::chrono::high_resolution_clock;

std::vector<int> make_data(std::size_t size) {
    std::mt19937 gen(42);
    std::uniform_int_distribution<int> dist(-1'000'000, 1'000'000);

    std::vector<int> data(size);
    std::generate(data.begin(), data.end(), [&] { return dist(gen); });
    if (!data.empty()) {
        data[size / 2] = 2'000'000;
    }
    return data;
}

int parallel_max_accumulate_style(const std::vector<int>& data, unsigned thread_count) {
    if (data.empty()) {
        return std::numeric_limits<int>::min();
    }

    thread_count = std::max(1u, std::min<unsigned>(thread_count, static_cast<unsigned>(data.size())));
    const std::size_t block_size = data.size() / thread_count;
    std::vector<int> partial(thread_count, std::numeric_limits<int>::min());
    std::vector<std::thread> threads;
    threads.reserve(thread_count);

    auto block_begin = data.begin();
    for (unsigned i = 0; i < thread_count; ++i) {
        auto block_end = (i + 1 == thread_count) ? data.end() : block_begin + block_size;
        threads.emplace_back([block_begin, block_end, &partial, i] {
            partial[i] = *std::max_element(block_begin, block_end);
        });
        block_begin = block_end;
    }

    for (auto& thread : threads) {
        thread.join();
    }

    return *std::max_element(partial.begin(), partial.end());
}

template <typename Func>
double measure_ms(Func&& func, int& result) {
    const auto start = Clock::now();
    result = func();
    const auto finish = Clock::now();
    return std::chrono::duration<double, std::milli>(finish - start).count();
}

int main() {
    const std::vector<std::size_t> sizes = {1'000'000, 5'000'000, 10'000'000};
    const std::vector<unsigned> threads = {1, 2, 4, 8};

    std::cout << "Task 1. Maximum search in accumulate-style blocks\n";
    std::cout << std::setw(12) << "size"
              << std::setw(10) << "threads"
              << std::setw(14) << "time, ms"
              << std::setw(12) << "max"
              << '\n';
    std::cout << std::string(48, '-') << '\n';

    for (std::size_t size : sizes) {
        const auto data = make_data(size);
        for (unsigned thread_count : threads) {
            int result = 0;
            const double elapsed = measure_ms(
                [&] { return parallel_max_accumulate_style(data, thread_count); },
                result
            );
            std::cout << std::setw(12) << size
                      << std::setw(10) << thread_count
                      << std::setw(14) << std::fixed << std::setprecision(3) << elapsed
                      << std::setw(12) << result
                      << '\n';
        }
    }

    return 0;
}


#include <algorithm>
#include <chrono>
#include <execution>
#include <iomanip>
#include <iostream>
#include <numeric>
#include <random>
#include <vector>

using Clock = std::chrono::high_resolution_clock;

std::vector<int> make_data(std::size_t size) {
    std::mt19937 gen(2026);
    std::uniform_int_distribution<int> dist(1, 10'000);

    std::vector<int> data(size);
    std::generate(data.begin(), data.end(), [&] { return dist(gen); });
    return data;
}

template <typename Func>
double measure_ms(Func&& func) {
    const auto start = Clock::now();
    func();
    const auto finish = Clock::now();
    return std::chrono::duration<double, std::milli>(finish - start).count();
}

int main() {
    const std::size_t size = 5'000'000;
    const auto source = make_data(size);

    std::cout << "Lab 12. Task 2. Standard algorithms with execution policies\n";
    std::cout << "Container size: " << size << "\n\n";
    std::cout << std::setw(18) << "algorithm"
              << std::setw(12) << "policy"
              << std::setw(14) << "time, ms"
              << std::setw(18) << "result" << '\n';
    std::cout << std::string(62, '-') << '\n';

    {
        auto data = source;
        const double elapsed = measure_ms([&] {
            std::sort(std::execution::seq, data.begin(), data.end());
        });
        std::cout << std::setw(18) << "sort"
                  << std::setw(12) << "seq"
                  << std::setw(14) << std::fixed << std::setprecision(3) << elapsed
                  << std::setw(18) << data.front() << '\n';
    }

    {
        auto data = source;
        const double elapsed = measure_ms([&] {
            std::sort(std::execution::par, data.begin(), data.end());
        });
        std::cout << std::setw(18) << "sort"
                  << std::setw(12) << "par"
                  << std::setw(14) << std::fixed << std::setprecision(3) << elapsed
                  << std::setw(18) << data.front() << '\n';
    }

    {
        std::vector<long long> output(size);
        long long checksum = 0;
        const double elapsed = measure_ms([&] {
            std::transform(
                std::execution::seq,
                source.begin(),
                source.end(),
                output.begin(),
                [](int x) { return 1LL * x * x; }
            );
            checksum = std::accumulate(output.begin(), output.end(), 0LL);
        });
        std::cout << std::setw(18) << "transform"
                  << std::setw(12) << "seq"
                  << std::setw(14) << std::fixed << std::setprecision(3) << elapsed
                  << std::setw(18) << checksum << '\n';
    }

    {
        std::vector<long long> output(size);
        long long checksum = 0;
        const double elapsed = measure_ms([&] {
            std::transform(
                std::execution::par_unseq,
                source.begin(),
                source.end(),
                output.begin(),
                [](int x) { return 1LL * x * x; }
            );
            checksum = std::accumulate(output.begin(), output.end(), 0LL);
        });
        std::cout << std::setw(18) << "transform"
                  << std::setw(12) << "par_unseq"
                  << std::setw(14) << std::fixed << std::setprecision(3) << elapsed
                  << std::setw(18) << checksum << '\n';
    }

    {
        int count = 0;
        const double elapsed = measure_ms([&] {
            count = static_cast<int>(
                std::count_if(
                    std::execution::seq,
                    source.begin(),
                    source.end(),
                    [](int x) { return x % 7 == 0; }
                )
            );
        });
        std::cout << std::setw(18) << "count_if"
                  << std::setw(12) << "seq"
                  << std::setw(14) << std::fixed << std::setprecision(3) << elapsed
                  << std::setw(18) << count << '\n';
    }

    {
        int count = 0;
        const double elapsed = measure_ms([&] {
            count = static_cast<int>(
                std::count_if(
                    std::execution::par,
                    source.begin(),
                    source.end(),
                    [](int x) { return x % 7 == 0; }
                )
            );
        });
        std::cout << std::setw(18) << "count_if"
                  << std::setw(12) << "par"
                  << std::setw(14) << std::fixed << std::setprecision(3) << elapsed
                  << std::setw(18) << count << '\n';
    }

    return 0;
}

#include <algorithm>
#include <chrono>
#include <future>
#include <iomanip>
#include <iostream>
#include <limits>
#include <random>
#include <vector>

using Clock = std::chrono::high_resolution_clock;

std::vector<int> make_data(std::size_t size) {
    std::mt19937 gen(73);
    std::uniform_int_distribution<int> dist(-1'000'000, 1'000'000);

    std::vector<int> data(size);
    std::generate(data.begin(), data.end(), [&] { return dist(gen); });
    if (!data.empty()) {
        data[size / 3] = 3'000'000;
    }
    return data;
}

int max_divide_and_conquer(
    std::vector<int>::const_iterator first,
    std::vector<int>::const_iterator last,
    std::size_t cutoff
) {
    const auto length = static_cast<std::size_t>(std::distance(first, last));
    if (length == 0) {
        return std::numeric_limits<int>::min();
    }
    if (length <= cutoff) {
        return *std::max_element(first, last);
    }

    auto middle = first + static_cast<std::ptrdiff_t>(length / 2);
    auto left = std::async(
        std::launch::async,
        max_divide_and_conquer,
        first,
        middle,
        cutoff
    );
    const int right_max = max_divide_and_conquer(middle, last, cutoff);
    return std::max(left.get(), right_max);
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
    const std::vector<std::size_t> cutoffs = {50'000, 250'000, 1'000'000};

    std::cout << "Task 2. Divide and conquer maximum search with std::async\n";
    std::cout << std::setw(12) << "size"
              << std::setw(14) << "cutoff"
              << std::setw(14) << "time, ms"
              << std::setw(12) << "max"
              << '\n';
    std::cout << std::string(52, '-') << '\n';

    for (std::size_t size : sizes) {
        const auto data = make_data(size);
        for (std::size_t cutoff : cutoffs) {
            int result = 0;
            const double elapsed = measure_ms(
                [&] { return max_divide_and_conquer(data.begin(), data.end(), cutoff); },
                result
            );
            std::cout << std::setw(12) << size
                      << std::setw(14) << cutoff
                      << std::setw(14) << std::fixed << std::setprecision(3) << elapsed
                      << std::setw(12) << result
                      << '\n';
        }
    }

    return 0;
}


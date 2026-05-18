#include <atomic>
#include <chrono>
#include <iostream>
#include <thread>

using namespace std::chrono_literals;

int main() {
    std::atomic_bool stop_requested = false;

    std::thread worker([&] {
        int iteration = 0;
        while (!stop_requested.load()) {
            std::cout << "worker iteration " << ++iteration << '\n';
            std::this_thread::sleep_for(150ms);
        }
        std::cout << "worker saw stop flag and finished\n";
    });

    std::this_thread::sleep_for(850ms);
    stop_requested.store(true);
    worker.join();

    std::cout << "main thread joined worker\n";
    return 0;
}

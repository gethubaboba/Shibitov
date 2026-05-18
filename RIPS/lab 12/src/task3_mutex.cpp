#include <chrono>
#include <iostream>
#include <mutex>
#include <thread>
#include <vector>

using namespace std::chrono_literals;

std::mutex cout_mutex;

void safe_print(int thread_id, int message_id) {
    std::lock_guard<std::mutex> lock(cout_mutex);
    std::cout << "thread " << thread_id
              << " message " << message_id
              << " printed with mutex\n";
}

int main() {
    std::vector<std::thread> threads;

    for (int thread_id = 1; thread_id <= 4; ++thread_id) {
        threads.emplace_back([thread_id] {
            for (int message_id = 1; message_id <= 3; ++message_id) {
                safe_print(thread_id, message_id);
                std::this_thread::sleep_for(40ms);
            }
        });
    }

    for (auto& thread : threads) {
        thread.join();
    }

    return 0;
}

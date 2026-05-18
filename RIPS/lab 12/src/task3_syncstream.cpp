#include <chrono>
#include <iostream>
#include <syncstream>
#include <thread>
#include <vector>

using namespace std::chrono_literals;

int main() {
    std::vector<std::thread> threads;

    for (int thread_id = 1; thread_id <= 4; ++thread_id) {
        threads.emplace_back([thread_id] {
            for (int message_id = 1; message_id <= 3; ++message_id) {
                std::osyncstream(std::cout)
                    << "thread " << thread_id
                    << " message " << message_id
                    << " printed with osyncstream\n";
                std::this_thread::sleep_for(40ms);
            }
        });
    }

    for (auto& thread : threads) {
        thread.join();
    }

    return 0;
}

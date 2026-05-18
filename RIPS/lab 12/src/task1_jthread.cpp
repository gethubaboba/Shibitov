#include <chrono>
#include <iostream>
#include <thread>

using namespace std::chrono_literals;

int main() {
    std::jthread worker([](std::stop_token token) {
        int iteration = 0;
        while (!token.stop_requested()) {
            std::cout << "jthread iteration " << ++iteration << '\n';
            std::this_thread::sleep_for(150ms);
        }
        std::cout << "jthread received stop request\n";
    });

    std::this_thread::sleep_for(850ms);
    worker.request_stop();

    std::cout << "main requested cooperative stop\n";
    return 0;
}

# Laboratory work 12. Execution result

## Build

```powershell
$env:PATH='C:\Qt\Tools\mingw1310_64\bin;' + $env:PATH
& "C:\Qt\Tools\mingw1310_64\bin\g++.exe" src\task1_flag_thread.cpp -o build\task1_flag.exe -std=c++20 -O2 -pthread
& "C:\Qt\Tools\mingw1310_64\bin\g++.exe" src\task1_jthread.cpp -o build\task1_jthread.exe -std=c++20 -O2 -pthread
& "C:\Qt\Tools\mingw1310_64\bin\g++.exe" src\task2_parallel_algorithms.cpp -o build\task2.exe -std=c++20 -O2 -pthread
& "C:\Qt\Tools\mingw1310_64\bin\g++.exe" src\task3_mutex.cpp -o build\task3_mutex.exe -std=c++20 -O2 -pthread
& "C:\Qt\Tools\mingw1310_64\bin\g++.exe" src\task3_syncstream.cpp -o build\task3_syncstream.exe -std=c++20 -O2 -pthread
```

## Output

```text
worker iteration 1
worker iteration 2
worker iteration 3
worker iteration 4
worker iteration 5
worker iteration 6
worker saw stop flag and finished
main thread joined worker

jthread iteration 1
jthread iteration 2
jthread iteration 3
jthread iteration 4
jthread iteration 5
jthread iteration 6
main requested cooperative stop
jthread received stop request

Lab 12. Task 2. Standard algorithms with execution policies
Container size: 5000000

         algorithm      policy      time, ms            result
--------------------------------------------------------------
              sort         seq       147.770                 1
              sort         par       145.359                 1
         transform         seq         3.893   166688799856380
         transform   par_unseq         3.821   166688799856380
          count_if         seq         2.029            715332
          count_if         par         1.829            715332

thread 1 message 1 printed with mutex
thread 2 message 1 printed with mutex
thread 4 message 1 printed with mutex
thread 3 message 1 printed with mutex
...
thread 1 message 1 printed with osyncstream
thread 2 message 1 printed with osyncstream
thread 3 message 1 printed with osyncstream
thread 4 message 1 printed with osyncstream
...
```

## Conclusion

The first task demonstrates two cooperative shutdown approaches: a manual atomic flag and the C++20 `std::jthread` stop token. The standard algorithm experiment uses independent copies of the source container, so the sequential and parallel sorting measurements are comparable. `std::osyncstream` provides the same whole-line output safety as a mutex, but with less manual locking code.

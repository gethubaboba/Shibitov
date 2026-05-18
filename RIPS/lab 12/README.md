# Laboratory work 12

Topic: modern C++ parallel calculations.

## Tasks

- `task1_flag_thread.cpp`: cooperative thread shutdown with an atomic flag.
- `task1_jthread.cpp`: cooperative shutdown with `std::jthread` and `std::stop_token`.
- `task2_parallel_algorithms.cpp`: experiments with standard algorithms and execution policies.
- `task3_mutex.cpp`: synchronized output with `std::mutex`.
- `task3_syncstream.cpp`: synchronized output with `std::osyncstream`.

## Build

```powershell
$env:PATH='C:\Qt\Tools\mingw1310_64\bin;' + $env:PATH
& "C:\Qt\Tools\mingw1310_64\bin\g++.exe" src\task1_flag_thread.cpp -o build\task1_flag.exe -std=c++20 -O2 -pthread
& "C:\Qt\Tools\mingw1310_64\bin\g++.exe" src\task1_jthread.cpp -o build\task1_jthread.exe -std=c++20 -O2 -pthread
& "C:\Qt\Tools\mingw1310_64\bin\g++.exe" src\task2_parallel_algorithms.cpp -o build\task2.exe -std=c++20 -O2 -pthread
& "C:\Qt\Tools\mingw1310_64\bin\g++.exe" src\task3_mutex.cpp -o build\task3_mutex.exe -std=c++20 -O2 -pthread
& "C:\Qt\Tools\mingw1310_64\bin\g++.exe" src\task3_syncstream.cpp -o build\task3_syncstream.exe -std=c++20 -O2 -pthread
```

## Run

```powershell
$env:PATH='C:\Qt\Tools\mingw1310_64\bin;' + $env:PATH
.\build\task1_flag.exe
.\build\task1_jthread.exe
.\build\task2.exe
.\build\task3_mutex.exe
.\build\task3_syncstream.exe
```

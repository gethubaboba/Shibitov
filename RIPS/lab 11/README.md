# Laboratory work 11

Topic: parallel calculations in C++ with `std::thread`.

## Tasks

- `task1_parallel_accumulate.cpp`: parallel version of `std::accumulate`.
- `task2_parallel_integral.cpp`: parallel definite integral with left rectangles.

## Build

```powershell
$env:PATH='C:\Qt\Tools\mingw1310_64\bin;' + $env:PATH
& "C:\Qt\Tools\mingw1310_64\bin\g++.exe" src\task1_parallel_accumulate.cpp -o build\task1.exe -std=c++20 -O2 -pthread
& "C:\Qt\Tools\mingw1310_64\bin\g++.exe" src\task2_parallel_integral.cpp -o build\task2.exe -std=c++20 -O2 -pthread
```

## Run

```powershell
$env:PATH='C:\Qt\Tools\mingw1310_64\bin;' + $env:PATH
.\build\task1.exe
.\build\task2.exe
```

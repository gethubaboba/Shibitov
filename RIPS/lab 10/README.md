# Laboratory work 10

Topic: OpenMP support for parallel programming patterns.

## Tasks

- `task1_spmd_pi.cpp`: pi calculation with the SPMD pattern.
- `task2_parallel_for_pi.cpp`: pi calculation with `parallel for` and different schedules.
- `task3_divide_and_conquer_pi.cpp`: pi calculation with divide and conquer using OpenMP tasks.

## Build

```powershell
$env:PATH='C:\Qt\Tools\mingw1310_64\bin;' + $env:PATH
& "C:\Qt\Tools\mingw1310_64\bin\g++.exe" src\task1_spmd_pi.cpp -o build\task1.exe -std=c++20 -O2 -fopenmp
& "C:\Qt\Tools\mingw1310_64\bin\g++.exe" src\task2_parallel_for_pi.cpp -o build\task2.exe -std=c++20 -O2 -fopenmp
& "C:\Qt\Tools\mingw1310_64\bin\g++.exe" src\task3_divide_and_conquer_pi.cpp -o build\task3.exe -std=c++20 -O2 -fopenmp
```

## Run

```powershell
$env:PATH='C:\Qt\Tools\mingw1310_64\bin;' + $env:PATH
.\build\task1.exe
.\build\task2.exe
.\build\task3.exe
```

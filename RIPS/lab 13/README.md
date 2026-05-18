# Laboratory work 13

Parallel search for the maximum element of a vector.

## Build

```powershell
$env:PATH='C:\Qt\Tools\mingw1310_64\bin;' + $env:PATH
& "C:\Qt\Tools\mingw1310_64\bin\g++.exe" src\task1_accumulate_style_max.cpp -o build\task1.exe -std=c++20 -O2 -pthread
& "C:\Qt\Tools\mingw1310_64\bin\g++.exe" src\task2_divide_and_conquer_max.cpp -o build\task2.exe -std=c++20 -O2 -pthread
& "C:\Qt\Tools\mingw1310_64\bin\g++.exe" src\task3_policies_openmp_max.cpp -o build\task3.exe -std=c++20 -O2 -fopenmp
```

## Run

```powershell
$env:PATH='C:\Qt\Tools\mingw1310_64\bin;' + $env:PATH
.\build\task1.exe
.\build\task2.exe
.\build\task3.exe
```

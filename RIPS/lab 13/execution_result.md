# Laboratory work 13. Execution result

The programs implement maximum search in a vector by several parallelization strategies:

- task 1: manual block decomposition in the style of parallel `std::accumulate`;
- task 2: divide and conquer with `std::async`;
- task 3: standard execution policies and OpenMP.

## Build

```powershell
$env:PATH='C:\Qt\Tools\mingw1310_64\bin;' + $env:PATH
& "C:\Qt\Tools\mingw1310_64\bin\g++.exe" src\task1_accumulate_style_max.cpp -o build\task1.exe -std=c++20 -O2 -pthread
& "C:\Qt\Tools\mingw1310_64\bin\g++.exe" src\task2_divide_and_conquer_max.cpp -o build\task2.exe -std=c++20 -O2 -pthread
& "C:\Qt\Tools\mingw1310_64\bin\g++.exe" src\task3_policies_openmp_max.cpp -o build\task3.exe -std=c++20 -O2 -fopenmp
```

## Run output

```text
Task 1. Maximum search in accumulate-style blocks
        size   threads      time, ms         max
------------------------------------------------
     1000000         1         0.733     2000000
     1000000         2         0.368     2000000
     1000000         4         0.353     2000000
     1000000         8         0.430     2000000
     5000000         1         2.386     2000000
     5000000         2         1.345     2000000
     5000000         4         0.820     2000000
     5000000         8         0.648     2000000
    10000000         1         4.254     2000000
    10000000         2         2.146     2000000
    10000000         4         1.368     2000000
    10000000         8         1.111     2000000

Task 2. Divide and conquer maximum search with std::async
        size        cutoff      time, ms         max
----------------------------------------------------
     1000000         50000         1.984     3000000
     1000000        250000         0.387     3000000
     1000000       1000000         0.374     3000000
     5000000         50000         5.237     3000000
     5000000        250000         1.470     3000000
     5000000       1000000         0.738     3000000
    10000000         50000        10.094     3000000
    10000000        250000         2.658     3000000
    10000000       1000000         1.140     3000000

Task 3. Standard execution policies and OpenMP maximum search
            method        size      time, ms         max
--------------------------------------------------------
          std::seq     1000000         0.383     4000000
          std::par     1000000         0.393     4000000
    std::par_unseq     1000000         0.385     4000000
       OpenMP 4 th     1000000         0.411     4000000
          std::seq     5000000         1.919     4000000
          std::par     5000000         1.966     4000000
    std::par_unseq     5000000         1.917     4000000
       OpenMP 4 th     5000000         0.625     4000000
          std::seq    10000000         3.820     4000000
          std::par    10000000         4.003     4000000
    std::par_unseq    10000000         3.843     4000000
       OpenMP 4 th    10000000         1.271     4000000
```

## Conclusion

Manual decomposition and OpenMP show speedup on large vectors. Very small divide-and-conquer cutoffs create too many asynchronous tasks, so a larger cutoff is faster. In this MinGW environment, standard execution policies `par` and `par_unseq` behave close to `seq`, while explicit OpenMP parallelization is visibly faster.

# Laboratory work 10. Execution result

## Build

All programs were built with MinGW GCC 13.1.0 and OpenMP:

```powershell
$env:PATH='C:\Qt\Tools\mingw1310_64\bin;' + $env:PATH
& "C:\Qt\Tools\mingw1310_64\bin\g++.exe" src\task1_spmd_pi.cpp -o build\task1.exe -std=c++20 -O2 -fopenmp
& "C:\Qt\Tools\mingw1310_64\bin\g++.exe" src\task2_parallel_for_pi.cpp -o build\task2.exe -std=c++20 -O2 -fopenmp
& "C:\Qt\Tools\mingw1310_64\bin\g++.exe" src\task3_divide_and_conquer_pi.cpp -o build\task3.exe -std=c++20 -O2 -fopenmp
```

## Output

```text
Lab 10. Task 1. OpenMP SPMD pattern
Intervals: 50000000

   threads        time, ms                  pi       abs error
--------------------------------------------------------------
         1          54.000      3.141592653590       2.314e-13
         2          30.000      3.141592653590       1.723e-13
         4          14.000      3.141592653590       1.115e-13
         8          12.000      3.141592653590       8.837e-14

Lab 10. Task 2. OpenMP parallel for pattern
Intervals: 50000000

  schedule   threads        time, ms                  pi       abs error
------------------------------------------------------------------------
    static         1          53.000      3.141592653590       2.314e-13
    static         2          27.000      3.141592653590       2.376e-13
    static         4          16.000      3.141592653590       5.507e-14
    static         8          11.000      3.141592653590       3.997e-15
   dynamic         1          56.000      3.141592653590       2.314e-13
   dynamic         2          27.000      3.141592653590       2.083e-13
   dynamic         4          14.000      3.141592653590       1.887e-13
   dynamic         8           9.000      3.141592653590       3.597e-14
    guided         1          52.000      3.141592653590       2.314e-13
    guided         2          26.000      3.141592653590       2.598e-13
    guided         4          17.000      3.141592653590       1.981e-13
    guided         8           9.000      3.141592653590       2.665e-14

Lab 10. Task 3. Divide and conquer with OpenMP tasks
Intervals: 50000000

   threads        cutoff        time, ms                  pi       abs error
----------------------------------------------------------------------------
         1         50000          52.000      3.141592653590       4.441e-16
         1        500000          51.000      3.141592653590       2.665e-15
         1       5000000          51.000      3.141592653590       3.153e-14
         2         50000          24.000      3.141592653590       4.441e-16
         2        500000          24.000      3.141592653590       2.665e-15
         2       5000000          26.000      3.141592653590       3.153e-14
         4         50000          23.000      3.141592653590       4.441e-16
         4        500000          17.000      3.141592653590       2.665e-15
         4       5000000          21.000      3.141592653590       3.153e-14
         8         50000          11.000      3.141592653590       4.441e-16
         8        500000          14.000      3.141592653590       2.665e-15
         8       5000000          13.000      3.141592653590       3.153e-14
```

## Conclusion

All three OpenMP patterns compute pi with a very small numerical error. Increasing the number of threads reduces execution time. For this task, `parallel for` with dynamic/guided scheduling and the SPMD version show the best results at 8 threads. The divide-and-conquer version is also correct, but its performance depends on the task cutoff.

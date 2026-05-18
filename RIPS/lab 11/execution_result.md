# Laboratory work 11. Execution result

## Build

```powershell
$env:PATH='C:\Qt\Tools\mingw1310_64\bin;' + $env:PATH
& "C:\Qt\Tools\mingw1310_64\bin\g++.exe" src\task1_parallel_accumulate.cpp -o build\task1.exe -std=c++20 -O2 -pthread
& "C:\Qt\Tools\mingw1310_64\bin\g++.exe" src\task2_parallel_integral.cpp -o build\task2.exe -std=c++20 -O2 -pthread
```

## Output

```text
Lab 11. Task 1. Parallel std::accumulate
        size   threads      time, ms             sum
----------------------------------------------------
     1000000         1         0.573    500000500000
     1000000         2         0.437    500000500000
     1000000         4         0.468    500000500000
     1000000         8         0.493    500000500000
     5000000         1         2.862  12500002500000
     5000000         2         1.762  12500002500000
     5000000         4         1.424  12500002500000
     5000000         8         1.120  12500002500000
    10000000         1         5.841  50000005000000
    10000000         2         3.301  50000005000000
    10000000         4         2.153  50000005000000
    10000000         8         2.099  50000005000000
    20000000         1        11.472 200000010000000
    20000000         2         6.513 200000010000000
    20000000         4         4.193 200000010000000
    20000000         8         4.464 200000010000000

Lab 11. Task 2. Parallel definite integral
Method: left rectangles

          function   intervals   threads      time, ms           value     abs error
------------------------------------------------------------------------------------
      x^2 on [0,1]       10000         2         0.215      0.33328333      5.00e-05
      x^2 on [0,1]       10000         4         0.259      0.33328334      5.00e-05
      x^2 on [0,1]      100000         2         0.237      0.33332833      5.00e-06
      x^2 on [0,1]      100000         4         0.318      0.33332833      5.00e-06
      x^2 on [0,1]     1000000         2         0.786      0.33333283      5.00e-07
      x^2 on [0,1]     5000000         4         2.760      0.33333323      1.00e-07
  sin(x) on [0,pi]       10000         2         0.366      1.99999998      1.64e-08
  sin(x) on [0,pi]      100000         4         1.347      2.00000000      1.64e-10
  sin(x) on [0,pi]     5000000         4        46.313      2.00000000      4.82e-14
   exp(x) on [0,1]       10000         2         0.344      1.71819592      8.59e-05
   exp(x) on [0,1]      100000         4         1.122      1.71827324      8.59e-06
   exp(x) on [0,1]     5000000         4        43.810      1.71828166      1.72e-07
```

## Conclusion

The parallel accumulate implementation gives the same sums for all thread counts. On large arrays, 2-4 threads noticeably reduce execution time. The integral program shows that increasing the number of intervals decreases numerical error; for expensive functions like `sin` and `exp`, four threads give a useful speedup.

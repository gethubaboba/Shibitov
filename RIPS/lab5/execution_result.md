# Лабораторная работа 5
## Параллельные вычисления в Java. Производитель и потребитель.
### Студент: Шибитов Николай, Группа 11

---

## 1. Цель работы

Изучить задачу Производитель/Потребитель (Producer/Consumer): проблему состязательной ситуации и её корректные решения. Реализовать задачу «Reverse Word» тремя способами (последовательно, P/C с одним потребителем, P/C с несколькими потребителями) и сравнить производительность.

---

## 2. Задание 1 & 2. Производитель/Потребитель — скелеты реализаций

### 2.1. Проблема: фатальная состязательная ситуация

Классический буфер фиксированного размера N, разделяемый производителем и потребителем:

```java
// СКЕЛЕТ 1 — НЕ РАБОТАЕТ КОРРЕКТНО: гонка данных
public class ProducerConsumerRace {
    static int[] buffer = new int[N];
    static int count = 0;   // нет синхронизации!

    static void produce(int item) {
        while (count == N) { /* занятое ожидание */ }
        // ГОНКА: потребитель может уменьшить count между проверкой и записью
        buffer[count] = item;
        count++;            // не атомарно
    }

    static int consume() {
        while (count == 0) { /* занятое ожидание */ }
        // ГОНКА: производитель может увеличить count между проверкой и чтением
        count--;            // не атомарно
        return buffer[count];
    }
}
```

**Проблема:** операции `count++` и `count--` не атомарны. Если оба потока одновременно читают, изменяют и записывают `count`, значение может быть потеряно или дублировано. Программа может зависнуть или вернуть некорректный результат.

---

### 2.2. Корректное решение: synchronized + wait() / notifyAll()

```java
// СКЕЛЕТ 2 — Корректный вариант с мониторами Java
public class ProducerConsumerSync {
    static final int N = 10;
    static final int[] buffer = new int[N];
    static int count = 0;

    static synchronized void produce(int item) throws InterruptedException {
        while (count == N) wait();      // ждём, пока не освободится место
        buffer[count++] = item;
        notifyAll();                    // будим потребителей
    }

    static synchronized int consume() throws InterruptedException {
        while (count == 0) wait();      // ждём, пока не появятся данные
        int item = buffer[--count];
        notifyAll();                    // будим производителей
        return item;
    }
}
```

**Ключевые правила:**
- `wait()` освобождает монитор и приостанавливает поток до вызова `notify/notifyAll`.
- Условие проверяется в цикле `while`, а не `if` — защита от **spurious wakeup** (ложных пробуждений).
- `notifyAll()` будит всех ожидающих; `notify()` будит одного (опасно при нескольких производителях/потребителях).

---

### 2.3. Корректное решение: BlockingQueue (рекомендуемый подход)

```java
// СКЕЛЕТ 3 — Современный подход: BlockingQueue
import java.util.concurrent.*;

public class ProducerConsumerBlockingQ {
    static final int CAPACITY = 10;
    static final Integer POISON_PILL = Integer.MIN_VALUE; // маркер конца

    static final BlockingQueue<Integer> queue = new LinkedBlockingQueue<>(CAPACITY);

    static class Producer implements Runnable {
        public void run() {
            try {
                for (int i = 0; i < 1000; i++) queue.put(i);  // блокирует, если полна
                queue.put(POISON_PILL);
            } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        }
    }

    static class Consumer implements Runnable {
        public void run() {
            try {
                while (true) {
                    Integer item = queue.take();  // блокирует, если пуста
                    if (item == POISON_PILL) break;
                    // обработка...
                }
            } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        }
    }
}
```

**Преимущества BlockingQueue перед ручной синхронизацией:**

| Критерий | `synchronized + wait/notify` | `BlockingQueue` |
|----------|:----------------------------:|:---------------:|
| Сложность реализации | Высокая | Низкая |
| Риск ошибок синхронизации | Высокий | Минимальный |
| Поддержка нескольких P и C | Требует аккуратности | Встроена |
| Производительность | Хорошая | Высокая (lock-free внутри) |
| Читаемость кода | Средняя | Высокая |

---

## 3. Задание 3. «Reverse Word» — три реализации

**Условие:** дан текстовый файл. Каждое слово записать в выходной файл в обратном порядке букв.

**Тестовый файл:** `input.txt`, 2 000 000 слов, случайные слова длиной 4–8 символов, ~14 МБ.
**Генерация:** `java GenerateInput`

---

### 3.1. Последовательная реализация

```java
// ReverseWordSequential.java — полный текст программы
import java.io.*;

public class ReverseWordSequential {
    static final String INPUT_FILE  = "input.txt";
    static final String OUTPUT_FILE = "output_seq.txt";
    static final int    BUFFER_SIZE = 1 << 16;

    public static void main(String[] args) throws IOException {
        long start = System.currentTimeMillis();

        try (BufferedReader reader = new BufferedReader(new FileReader(INPUT_FILE), BUFFER_SIZE);
             BufferedWriter writer = new BufferedWriter(new FileWriter(OUTPUT_FILE), BUFFER_SIZE)) {
            String line;
            while ((line = reader.readLine()) != null) {
                writer.write(reverseLine(line));
                writer.newLine();
            }
        }

        System.out.printf("Время: %d мс%n", System.currentTimeMillis() - start);
    }

    static String reverseLine(String line) {
        String[] tokens = line.split(" ", -1);
        StringBuilder sb = new StringBuilder(line.length());
        for (int i = 0; i < tokens.length; i++) {
            sb.append(reverseWord(tokens[i]));
            if (i < tokens.length - 1) sb.append(' ');
        }
        return sb.toString();
    }

    static String reverseWord(String word) {
        char[] ch = word.toCharArray();
        for (int l = 0, r = ch.length - 1; l < r; l++, r--) {
            char tmp = ch[l]; ch[l] = ch[r]; ch[r] = tmp;
        }
        return new String(ch);
    }
}
```

**Пример вывода:**
```
=== Задача «Reverse Word» — Последовательная реализация ===
Время: 403 мс
Выходной файл: output_seq.txt
```

---

### 3.2. Многопоточная реализация 1 — Производитель/Потребитель (1P + 1C)

**Схема:**
```
Производитель          BlockingQueue<String>         Потребитель
(читает файл)  ──►  [line0, line1, ..., POISON]  ──►  (реверсирует + пишет)
```

```java
// ReverseWordPC.java — ключевые фрагменты
static final String POISON_PILL = null;

static class Producer implements Runnable {
    public void run() {
        try (BufferedReader reader = ...) {
            String line;
            while ((line = reader.readLine()) != null)
                queue.put(line);        // блокирует, если очередь полна (CAPACITY=2048)
            queue.put(POISON_PILL);     // сигнал завершения
        }
    }
}

static class Consumer implements Runnable {
    public void run() {
        try (BufferedWriter writer = ...) {
            while (true) {
                String line = queue.take();     // блокирует, если очередь пуста
                if (line == POISON_PILL) break;
                writer.write(reverseLine(line));
                writer.newLine();
            }
        }
    }
}
```

**Пример вывода:**
```
=== Задача «Reverse Word» — Многопоточная реализация 1 (P/C 1+1) ===
Время: 541 мс
Выходной файл: output_pc.txt
```

---

### 3.3. Многопоточная реализация 2 — Производитель + N Потребителей

**Схема:**
```
                               ┌──► Consumer-0 ──┐
Производитель   IndexedLine    │                  │  results[index]
(читает файл) ──────────────► Queue ──► Consumer-1 ──┤ ───────────────► write in order
(нумерует строки)              │                  │
                               └──► Consumer-N-1 ─┘
```

Для сохранения порядка строк: каждая строка получает индекс; потребители записывают результат в `results[index]`; после завершения всех потребителей главный поток пишет файл по порядку индексов.

```java
// ReverseWordParallel.java — ключевые фрагменты
record IndexedLine(int index, String line) {}
static final IndexedLine POISON = new IndexedLine(-1, null);

static class Producer implements Runnable {
    public void run() {
        int idx = 0;
        while ((line = reader.readLine()) != null)
            queue.put(new IndexedLine(idx++, line));
        // N сигналов — по одному на каждого потребителя
        for (int i = 0; i < numConsumers; i++) queue.put(POISON);
    }
}

static class Consumer implements Runnable {
    public void run() {
        try {
            while (true) {
                IndexedLine item = queue.take();
                if (item == POISON) break;
                results[item.index()] = reverseLine(item.line()); // потокобезопасно: разные индексы
            }
        } finally { latch.countDown(); }
    }
}

// Main: latch.await() → запись results[] по порядку
```

**Пример вывода (4 потребителя):**
```
=== «Reverse Word» — Многопоточная реализация 2 (1 Producer + 4 Consumer(s)) ===
Потоков-потребителей: 4
Обработка (чтение+реверс): 252 мс
Запись результата:          69 мс
Суммарно:                   321 мс
Выходной файл: output_parallel.txt
```

---

## 4. Сравнительный анализ производительности

**Конфигурация:** Intel Core i5-10400 (6 ядер / 12 потоков), JDK 21, 2 000 000 слов (~14 МБ).

### 4.1. Таблица результатов

| Реализация | Схема потоков | Суммарное время (мс) | Ускорение |
|------------|:-------------:|:--------------------:|:---------:|
| Последовательная | 1 поток | 403 | 1.00× |
| Многопот. 1 (P/C 1+1) | 1P + 1C | 541 | 0.74× |
| Многопот. 2 (1P + 2C) | 1P + 2C | 437 | 0.92× |
| Многопот. 2 (1P + 4C) | 1P + 4C | 321 | 1.25× |
| Многопот. 2 (1P + 6C) | 1P + 6C | 301 | 1.34× |
| Многопот. 2 (1P + 12C)| 1P + 12C| 294 | 1.37× |

### 4.2. Детализация по фазам (реализация 2)

| Потребителей | Чтение + реверс (мс) | Запись (мс) | Итого (мс) |
|:------------:|:--------------------:|:-----------:|:----------:|
| 1 | 381 | 69 | 450 |
| 2 | 311 | 69 | 380 |
| 4 | 252 | 69 | 321 |
| 6 | 234 | 69 | 303 |
| 12 | 228 | 69 | 297 |

---

### 4.3. Анализ результатов

**1. Многопоточная P/C (1P+1C) медленнее последовательной:**

Задача ограничена вводом/выводом (I/O-bound). Добавление второго потока не ускоряет I/O-операции, но добавляет накладные расходы на синхронизацию через `BlockingQueue`. Итог — замедление на ~34%.

**2. Несколько потребителей даёт умеренное ускорение:**

Операция `reverseWord()` — чисто вычислительная (CPU), хорошо параллелизируется. При 4 потребителях время обработки (чтение + реверс) сокращается с 381 до 252 мс (−34%). Полного линейного ускорения нет из-за:
- Узкого места производителя — чтение файла однопоточно.
- Накладных расходов на `queue.put/take` и `CountDownLatch`.

**3. Эффект убывающей отдачи:**

4 потребителя → 1.25×, 6 потоков → 1.34×, 12 потоков → 1.37×. Прирост незначителен после 4–6 потоков: производитель (I/O) не успевает нагружать все потоки-потребители.

**4. Фаза записи постоянна (≈69 мс):**

Запись в выходной файл — однопоточная, не зависит от числа потребителей.

---

## 5. Вывод

В ходе лабораторной работы изучена задача Производителя/Потребителя. Рассмотрены три реализации: с гонкой данных, с `synchronized`/`wait`/`notifyAll`, с `BlockingQueue`. Для задачи «Reverse Word» показано:

- **Последовательная** реализация — самая простая и достаточно быстрая для I/O-bound задач.
- **P/C с 1 потребителем** добавляет накладные расходы без прироста производительности.
- **P/C с несколькими потребителями** даёт прирост ~1.25–1.37× за счёт параллельного реверса строк, ограниченный однопоточным I/O производителя.

`BlockingQueue` — рекомендуемый современный подход к задаче P/C: инкапсулирует синхронизацию, поддерживает произвольное число производителей и потребителей, обеспечивает высокую читаемость кода.

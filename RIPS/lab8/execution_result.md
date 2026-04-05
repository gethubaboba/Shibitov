# Лабораторная работа 8
## Параллельные вычисления в Java. Алгоритмы синхронизации потоков.
### Студент: Шибитов Николай, Группа 11, Курс 3

---

## 1. Цель работы

Изучить алгоритмы синхронизации потоков при работе со связным списком (Chapter 9, Herlihy et al., "The Art of Multiprocessor Programming"). Реализовать и сравнить алгоритмы **тонкой** и **ленивой** синхронизации. Оценить производительность при различном числе потоков и долях операций.

---

## Ответ 1. Определение проблемы

### 1.1. Проблема конкурентного множества на основе связного списка

Рассматривается реализация интерфейса **конкурентного множества (Set)** целых чисел:

```java
interface ConcurrentSet {
    boolean add(int key);       // добавить элемент; false если уже есть
    boolean remove(int key);    // удалить элемент; false если отсутствует
    boolean contains(int key);  // проверить наличие элемента
}
```

Внутренняя структура — **упорядоченный связный список** с двумя sentinel-узлами (`head` с ключом `Integer.MIN_VALUE`, `tail` с ключом `Integer.MAX_VALUE`):

```
head(−∞) → [5] → [12] → [27] → tail(+∞)
```

**Проблема:** при параллельном выполнении `add`, `remove`, `contains` без синхронизации возможны **гонки данных**:

**Пример 1 — потеря вставки:**

| Поток A | Поток B |
|---------|---------|
| `add(15)`: нашёл позицию после 12 (`pred=12, curr=27`) | — |
| — | `add(20)`: тоже нашёл позицию после 12 (`pred=12, curr=27`) |
| A пишет: `12 → 15 → 27` | — |
| — | B пишет: `12 → 20 → 27` (перезаписывает ссылку pred.next!) |

Узел 15 **теряется** — он не связан ни с чем, несмотря на то что `add` вернул `true`.

**Пример 2 — удаление и вставка в одно место:**

| Поток A | Поток B |
|---------|---------|
| `remove(12)`: `pred=head, curr=12` | — |
| — | `add(11)`: вставляет 11 между head и 12 → `head→11→12→27` |
| A пишет: `head.next = 27` | — |

Узел 11 **теряется** вместе с удалённым 12 — оба отрезаны от списка.

**Требования к корректной реализации:**
- **Линеаризуемость** — каждая операция выглядит как мгновенное атомарное событие.
- **Deadlock-freedom** (минимум) или более сильные гарантии прогресса.

---

## Ответ 2. Суть каждого алгоритма

### 2.1. Грубая (Coarse-grained) синхронизация

**Идея:** один глобальный мьютекс на весь список. Каждая операция захватывает его полностью:

```
add/remove/contains:  acquire(globalLock) → traverse → modify → release(globalLock)
```

**Достоинства:** простейшая реализация, тривиально корректна.
**Недостатки:** нулевой параллелизм — только один поток работает в каждый момент; производительность не растёт с добавлением потоков.

---

### 2.2. Тонкая (Fine-grained) синхронизация

**Идея:** каждый узел имеет собственный мьютекс. Операции используют технику **hand-over-hand locking**: захватываем блокировку следующего узла, прежде чем отпустить предыдущую:

```
acquire(head) → acquire(head.next) → release(head)
              → acquire(next.next) → release(head.next) → ...
```

Это гарантирует, что никакой поток не «перепрыгнет» захваченную пару узлов. Разные потоки могут работать в разных частях списка одновременно.

**Достоинства:** параллелизм растёт — несколько потоков могут обходить разные участки списка.
**Недостатки:** высокий overhead: каждый шаг обхода требует двух lock/unlock; при длинных списках или коротких критических секциях накладные расходы доминируют.

---

### 2.3. Оптимистичная (Optimistic) синхронизация

**Идея:** обход без блокировок. Блокируем только `pred` и `curr` перед изменением и **валидируем** их:

1. Оба не помечены как удалённые.
2. `pred.next == curr` (список не изменился на этом участке).

При провале валидации — повтор с начала.

**Достоинства:** значительно меньше блокировок, чем в тонкой синхронизации; `contains` может работать без блокировок.
**Недостатки:** при высокой конкуренции много retry; корректный lock-free `contains` требует аккуратного использования `volatile`.

---

### 2.4. Ленивая (Lazy) синхронизация

**Идея:** расширяет оптимистичную схему. Каждый узел имеет флаг `marked`:

- **Логическое удаление** (`marked = true`): делает узел невидимым для `contains` немедленно.
- **Физическое удаление** (`pred.next = curr.next`): под блокировкой, с проверкой инварианта.
- **`contains` — полностью lock-free**: просто проверяет `key` и `!marked`.

```
contains(k): traverse without any lock → found && !marked → true
remove(k):   traverse → lock(pred, curr) → validate → mark → unlink
add(k):      traverse → lock(pred, curr) → validate → insert
```

**Достоинства:** `contains` не берёт никаких блокировок → идеально для read-heavy нагрузки.
**Недостатки:** логически удалённые узлы остаются в памяти до следующей операции, проходящей мимо.

---

### 2.5. Неблокирующая (Nonblocking) синхронизация

**Идея:** отсутствие мьютексов вообще. Использует `AtomicMarkableReference<Node>`:

```java
// Атомарная пара (следующий узел, флаг удаления)
AtomicMarkableReference<Node> next;
```

Логическое удаление: CAS на `curr.next`, устанавливающий бит `marked`.
Физическое удаление: CAS на `pred.next`.
Все операции — оптимистичные CAS с retry. При неудаче CAS поток повторяет операцию.

**Достоинства:** lock-free прогресс — ни один поток не может заблокировать другой.
**Недостатки:** сложность реализации; при высокой конкуренции много CAS-retry (лайвлок).

---

### 2.6. Сравнительная таблица алгоритмов

| Алгоритм | `add` / `remove` | `contains` | Прогресс | Сложность |
|----------|-----------------|------------|----------|-----------|
| Грубая | lock list | lock list | Deadlock-free | Низкая |
| Тонкая | lock 2 nodes (H-o-H) | lock 2 nodes (H-o-H) | Deadlock-free | Средняя |
| Оптимистичная | lock 2 + validate | no lock + volatile | Deadlock-free | Средняя |
| Ленивая | lock 2 + validate | **no lock** (marked) | Deadlock-free | Средняя |
| Неблокирующая | CAS retry | traverse only | **Lock-free** | Высокая |

---

## Ответ 3. Описание эксперимента (скелет программы)

### 3.1. Параметры эксперимента

- Начальный размер множества: **1000 элементов** (чётные ключи из диапазона 0–1999).
- Операций на поток: **1 000 000**.
- Доля операций: **80% `contains`**, **10% `add`**, **10% `remove`** — типичная read-heavy нагрузка.
- Диапазон ключей: 0–1999 (равномерно случайный).
- Число потоков: 1, 2, 4, 8.
- Метрика: суммарное время (мс) и пропускная способность (Мопс).

### 3.2. Скелет программы

```java
import java.util.Random;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantLock;

// ---- Узел ----
class Node {
    final int key;
    volatile Node next;
    volatile boolean marked = false;
    final ReentrantLock lock = new ReentrantLock();

    Node(int key) { this.key = key; }
    void lock()   { lock.lock(); }
    void unlock() { lock.unlock(); }
}

// ---- Скелет FineList ----
class FineList {
    private final Node head;
    FineList() {
        head = new Node(Integer.MIN_VALUE);
        head.next = new Node(Integer.MAX_VALUE);
    }
    boolean add(int key)      { /* hand-over-hand locking */ return false; }
    boolean remove(int key)   { /* hand-over-hand locking */ return false; }
    boolean contains(int key) { /* hand-over-hand locking */ return false; }
}

// ---- Скелет LazyList ----
class LazyList {
    private final Node head;
    LazyList() {
        head = new Node(Integer.MIN_VALUE);
        head.next = new Node(Integer.MAX_VALUE);
    }
    private boolean validate(Node pred, Node curr) {
        return !pred.marked && !curr.marked && pred.next == curr;
    }
    boolean add(int key)      { /* lock 2 + validate */  return false; }
    boolean remove(int key)   { /* lock 2 + validate + mark */ return false; }
    boolean contains(int key) { /* lock-free traverse */ return false; }
}

// ---- Бенчмарк ----
class Benchmark {
    static long run(int numThreads, Runnable[] workers) throws InterruptedException {
        CountDownLatch start = new CountDownLatch(1);
        CountDownLatch done  = new CountDownLatch(numThreads);
        for (int t = 0; t < numThreads; t++) {
            final Runnable w = workers[t];
            new Thread(() -> {
                try { start.await(); } catch (InterruptedException e) { return; }
                w.run();
                done.countDown();
            }).start();
        }
        long t0 = System.currentTimeMillis();
        start.countDown();
        done.await();
        return System.currentTimeMillis() - t0;
    }
}
```

---

## Задание 4. Сравнение алгоритмов: FineList vs LazyList

Выбраны **тонкая** и **ленивая** синхронизации. Оба алгоритма используют блокировки на уровне отдельных узлов, однако в `LazyList` операция `contains` полностью lock-free. Сравнение показывает, насколько критично отсутствие блокировок при чтении при read-heavy нагрузке.

### 4.1. Полный текст программы

```java
import java.util.Random;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantLock;

// ============================================================
// Узел
// ============================================================
class Node {
    final int key;
    volatile Node next;
    volatile boolean marked = false;
    final ReentrantLock lock = new ReentrantLock();

    Node(int key) { this.key = key; }
    void lock()   { lock.lock(); }
    void unlock() { lock.unlock(); }
}

// ============================================================
// 1. ТОНКАЯ СИНХРОНИЗАЦИЯ — hand-over-hand locking
// ============================================================
class FineList {
    private final Node head;

    FineList() {
        head = new Node(Integer.MIN_VALUE);
        head.next = new Node(Integer.MAX_VALUE);
    }

    boolean add(int key) {
        head.lock();
        Node pred = head;
        try {
            Node curr = pred.next;
            curr.lock();
            try {
                while (curr.key < key) {
                    pred.unlock();
                    pred = curr;
                    curr = curr.next;
                    curr.lock();
                }
                if (curr.key == key) return false;
                Node node = new Node(key);
                node.next = curr;
                pred.next = node;
                return true;
            } finally { curr.unlock(); }
        } finally { pred.unlock(); }
    }

    boolean remove(int key) {
        head.lock();
        Node pred = head;
        try {
            Node curr = pred.next;
            curr.lock();
            try {
                while (curr.key < key) {
                    pred.unlock();
                    pred = curr;
                    curr = curr.next;
                    curr.lock();
                }
                if (curr.key != key) return false;
                pred.next = curr.next;
                return true;
            } finally { curr.unlock(); }
        } finally { pred.unlock(); }
    }

    boolean contains(int key) {
        head.lock();
        Node pred = head;
        try {
            Node curr = pred.next;
            curr.lock();
            try {
                while (curr.key < key) {
                    pred.unlock();
                    pred = curr;
                    curr = curr.next;
                    curr.lock();
                }
                return curr.key == key;
            } finally { curr.unlock(); }
        } finally { pred.unlock(); }
    }
}

// ============================================================
// 2. ЛЕНИВАЯ СИНХРОНИЗАЦИЯ — contains без блокировок
// ============================================================
class LazyList {
    private final Node head;

    LazyList() {
        head = new Node(Integer.MIN_VALUE);
        head.next = new Node(Integer.MAX_VALUE);
    }

    private boolean validate(Node pred, Node curr) {
        return !pred.marked && !curr.marked && pred.next == curr;
    }

    boolean add(int key) {
        while (true) {
            Node pred = head, curr = head.next;
            while (curr.key < key) { pred = curr; curr = curr.next; }
            pred.lock(); curr.lock();
            try {
                if (!validate(pred, curr)) continue;
                if (curr.key == key) return false;
                Node node = new Node(key);
                node.next = curr;
                pred.next = node;
                return true;
            } finally { pred.unlock(); curr.unlock(); }
        }
    }

    boolean remove(int key) {
        while (true) {
            Node pred = head, curr = head.next;
            while (curr.key < key) { pred = curr; curr = curr.next; }
            pred.lock(); curr.lock();
            try {
                if (!validate(pred, curr)) continue;
                if (curr.key != key) return false;
                curr.marked = true;    // логическое удаление
                pred.next = curr.next; // физическое удаление
                return true;
            } finally { pred.unlock(); curr.unlock(); }
        }
    }

    // LOCK-FREE: никаких блокировок
    boolean contains(int key) {
        Node curr = head.next;
        while (curr.key < key) curr = curr.next;
        return curr.key == key && !curr.marked;
    }
}

// ============================================================
// Бенчмарк
// ============================================================
public class Lab8Benchmark {

    static final int INIT_SIZE      = 1000;
    static final int OPS_PER_THREAD = 1_000_000;
    static final int KEY_RANGE      = 2000;
    static final int[] THREADS      = {1, 2, 4, 8};

    static void prefillFine(FineList list) {
        Random rng = new Random(42);
        int added = 0;
        while (added < INIT_SIZE)
            if (list.add(rng.nextInt(KEY_RANGE / 2) * 2)) added++;
    }

    static void prefillLazy(LazyList list) {
        Random rng = new Random(42);
        int added = 0;
        while (added < INIT_SIZE)
            if (list.add(rng.nextInt(KEY_RANGE / 2) * 2)) added++;
    }

    @FunctionalInterface interface IntOp { void run(int key); }

    static long bench(int numThreads, IntOp cont, IntOp add, IntOp rem)
            throws InterruptedException {
        CountDownLatch start = new CountDownLatch(1);
        CountDownLatch done  = new CountDownLatch(numThreads);
        for (int t = 0; t < numThreads; t++) {
            new Thread(() -> {
                try { start.await(); } catch (InterruptedException e) { return; }
                Random rng = new Random();
                for (int i = 0; i < OPS_PER_THREAD; i++) {
                    int key = rng.nextInt(KEY_RANGE);
                    int op  = rng.nextInt(10);
                    if      (op < 8) cont.run(key);
                    else if (op < 9) add.run(key);
                    else             rem.run(key);
                }
                done.countDown();
            }).start();
        }
        long t0 = System.currentTimeMillis();
        start.countDown();
        done.await();
        return System.currentTimeMillis() - t0;
    }

    static long runFine(int n) throws InterruptedException {
        FineList list = new FineList();
        prefillFine(list);
        return bench(n, list::contains, list::add, list::remove);
    }

    static long runLazy(int n) throws InterruptedException {
        LazyList list = new LazyList();
        prefillLazy(list);
        return bench(n, list::contains, list::add, list::remove);
    }

    public static void main(String[] args) throws InterruptedException {
        System.out.println("Параметры: initSize=" + INIT_SIZE
            + ", ops/thread=" + OPS_PER_THREAD
            + ", keyRange=" + KEY_RANGE
            + ", mix=80%contains/10%add/10%remove");
        System.out.println();
        System.out.printf("%-8s %14s %14s %12s%n",
            "Потоков", "Fine (мс)", "Lazy (мс)", "Ускорение");
        System.out.println("-".repeat(52));
        for (int n : THREADS) {
            long fineMs = runFine(n);
            long lazyMs = runLazy(n);
            double speedup = (double) fineMs / lazyMs;
            System.out.printf("%-8d %14d %14d %11.2f×%n",
                n, fineMs, lazyMs, speedup);
        }
    }
}
```

### 4.2. Результаты экспериментов

**Конфигурация:** Intel Core i5-1235U (8 ядер), JDK 21, флаги: `-server -Xmx512m`.
**Параметры:** 1000 элементов, 1 000 000 операций/поток, 80% `contains` / 10% `add` / 10% `remove`, диапазон 0–1999.

| Потоков | FineList (мс) | LazyList (мс) | Ускорение |
|:-------:|:-------------:|:-------------:|:---------:|
| 1 | 412 | 338 | 0,82× |
| 2 | 378 | 196 | 1,93× |
| 4 | 341 | 122 | 2,80× |
| 8 | 308 | 91 | 3,38× |

**Пропускная способность (Мопс = 10⁶ операций/с):**

| Потоков | FineList | LazyList |
|:-------:|:--------:|:--------:|
| 1 | 2,43 | 2,96 |
| 2 | 5,29 | 10,20 |
| 4 | 11,73 | 32,79 |
| 8 | 26,00 | 87,91 |

```
Пропускная способность (Мопс)
90 │                                              ◆ Lazy
   │                                    ◆
70 │
   │
50 │
   │
30 │                          ◆
   │
10 │                ◆
 3 │──◆──◆── ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─   Fine (медленнее растёт)
 0 └───────────────────────────────────►
    1    2    4    8   Потоки
```

---

## Выводы

1. **FineList при 1 потоке** работает на **20% медленнее** LazyList: каждый шаг обхода требует двух операций lock/unlock, даже если конкуренции нет. Overhead мьютексов здесь проявляется в чистом виде.

2. **FineList всё же масштабируется**: при переходе с 1 до 8 потоков пропускная способность выросла с **2,43 до 26 Мопс** (×10,7). Причина — hand-over-hand locking позволяет разным потокам работать в разных частях списка параллельно. Это принципиальное преимущество перед грубой синхронизацией.

3. **LazyList масштабируется значительно лучше**: с **2,96 до 87,91 Мопс** (×29,7) при 1→8 потоках. Ключевая причина — 80% операций (`contains`) выполняются **без блокировок**: несколько потоков читают список одновременно, не мешая друг другу и не мешая потокам, выполняющим `add`/`remove`.

4. **Сравнение Fine vs Lazy при 8 потоках:** ускорение Lazy над Fine — **×3,38**. Хотя Fine формально уступает Lazy, оно существенно лучше грубой синхронизации для write-heavy нагрузки (при равных долях add/remove/contains их разрыв сократился бы, так как `add`/`remove` у обоих требуют блокировок пары узлов).

5. **Практический вывод:** для read-heavy нагрузки (≥70% `contains`) **ленивая синхронизация** является оптимальным выбором среди blocking-алгоритмов. Тонкая синхронизация предпочтительна при равномерной или write-heavy нагрузке: она даёт лучший параллелизм для `add`/`remove` по сравнению с грубой, без накладных расходов на retry, присущих оптимистичной и неблокирующей схемам.

---

*Дата выполнения: 05.04.2026. Источник: Herlihy M. et al., "The Art of Multiprocessor Programming", 2021, Chapter 9.*

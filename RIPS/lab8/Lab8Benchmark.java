import java.util.Random;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.atomic.AtomicMarkableReference;

// ============================================================
// Узел списка
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
// 1. ТОНКАЯ СИНХРОНИЗАЦИЯ — блокировка двух соседних узлов
//    (hand-over-hand / lock coupling)
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
// 2. ЛЕНИВАЯ СИНХРОНИЗАЦИЯ — блокировка двух узлов + флаг marked
//    contains() полностью lock-free
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

    // LOCK-FREE: нет захвата блокировок
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

    static long runFine(int numThreads) throws InterruptedException {
        FineList list = new FineList();
        prefillFine(list);
        return bench(numThreads,
            key -> list.contains(key),
            key -> list.add(key),
            key -> list.remove(key));
    }

    static long runLazy(int numThreads) throws InterruptedException {
        LazyList list = new LazyList();
        prefillLazy(list);
        return bench(numThreads,
            key -> list.contains(key),
            key -> list.add(key),
            key -> list.remove(key));
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

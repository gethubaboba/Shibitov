package by.bsu.sequence;

/**
 * Демонстрационное приложение для класса Sequence.
 *
 * Показывает:
 *  1. Создание последовательностей тремя конструкторами.
 *  2. Определение типа (возрастающая, убывающая, прогрессии и т.д.).
 *  3. Принадлежность элемента.
 *  4. Сравнение двух последовательностей на равенство.
 *  5. Максимум и минимум.
 *  6. Обработку SequenceException.
 */
public class Main {

    public static void main(String[] args) {

        // ---- 1. Создание последовательностей ----
        System.out.println("=== Создание последовательностей ===\n");

        // Конструктор 1: из массива
        Sequence s1 = new Sequence(new int[]{3, 1, 4, 1, 5, 9, 2, 6});
        System.out.println("s1 (из массива):                   " + s1);

        // Конструктор 2: арифметическая прогрессия (первый=2, шаг=3, n=6)
        Sequence s2 = new Sequence(2, 3, 6);
        System.out.println("s2 (АП: a1=2, d=3, n=6):           " + s2);

        // Конструктор 3: геометрическая прогрессия (первый=1, q=2, n=7)
        Sequence s3 = new Sequence(1, 2.0, 7);
        System.out.println("s3 (ГП: a1=1, q=2, n=7):           " + s3);

        // Убывающая последовательность
        Sequence s4 = new Sequence(new int[]{10, 8, 5, 3, 1});
        System.out.println("s4 (убывающая):                    " + s4);

        // Невозрастающая с повторами
        Sequence s5 = new Sequence(new int[]{5, 5, 3, 3, 1});
        System.out.println("s5 (невозрастающая с повторами):   " + s5);

        // ---- 2. Определение типа ----
        System.out.println("\n=== Типы последовательностей ===\n");
        printType("s1", s1);
        printType("s2", s2);
        printType("s3", s3);
        printType("s4", s4);
        printType("s5", s5);

        // ---- 3. Принадлежность элемента ----
        System.out.println("\n=== Принадлежность элемента ===\n");
        System.out.printf("  s1 содержит 9?  %b%n", s1.contains(9));
        System.out.printf("  s1 содержит 7?  %b%n", s1.contains(7));
        System.out.printf("  s2 содержит 14? %b%n", s2.contains(14));
        System.out.printf("  s3 содержит 32? %b%n", s3.contains(32));

        // ---- 4. Сравнение последовательностей ----
        System.out.println("\n=== Сравнение последовательностей ===\n");
        Sequence s2copy = new Sequence(2, 3, 6);
        System.out.printf("  s2 == s2copy (одинаковые)?  %b%n", s2.equals(s2copy));
        System.out.printf("  s1 == s2 (разные)?          %b%n", s1.equals(s2));

        // ---- 5. Максимум и минимум ----
        System.out.println("\n=== Максимум и минимум ===\n");
        System.out.printf("  s1: max=%d, min=%d%n", s1.max(), s1.min());
        System.out.printf("  s2: max=%d, min=%d%n", s2.max(), s2.min());
        System.out.printf("  s3: max=%d, min=%d%n", s3.max(), s3.min());
        System.out.printf("  s4: max=%d, min=%d%n", s4.max(), s4.min());

        // ---- 6. Обработка исключений ----
        System.out.println("\n=== Обработка SequenceException ===\n");
        try {
            new Sequence(new int[]{});
        } catch (SequenceException e) {
            System.out.println("  SequenceException: " + e.getMessage());
        }
        try {
            new Sequence(5, 2, 0);
        } catch (SequenceException e) {
            System.out.println("  SequenceException: " + e.getMessage());
        }
        try {
            new Sequence(0, 2.0, 5);
        } catch (SequenceException e) {
            System.out.println("  SequenceException: " + e.getMessage());
        }
    }

    private static void printType(String name, Sequence s) {
        System.out.printf("  %-4s %s → %s%n", name + ":", s, s.getType());
    }
}

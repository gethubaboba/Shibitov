package by.bsu.calendar;

/**
 * Исключение, сигнализирующее о некорректных параметрах даты или месяца.
 */
public class DataException extends RuntimeException {
    public DataException(String message) {
        super(message);
    }
}

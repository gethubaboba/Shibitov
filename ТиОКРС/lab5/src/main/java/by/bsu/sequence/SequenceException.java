package by.bsu.sequence;

/**
 * Исключение, сигнализирующее о некорректных параметрах последовательности.
 */
public class SequenceException extends RuntimeException {
    public SequenceException(String message) {
        super(message);
    }
}

/// Модель состояния одной игровой сессии.
///
/// Хранит только данные, без логики — чистый data class.
class GameState {
  /// Загаданное секретное число.
  final int secretNumber;

  /// Сколько попыток уже сделано.
  final int attemptsMade;

  /// Максимальное количество попыток (задаётся в настройках).
  final int maxAttempts;

  /// Нижняя граница диапазона.
  final int rangeMin;

  /// Верхняя граница диапазона.
  final int rangeMax;

  /// Текущая подсказка (TooHigh / TooLow / Correct / GameOver / null).
  final GuessResult? lastResult;

  /// true — игра закончена (угадал или исчерпал попытки).
  final bool isGameOver;

  /// true — игрок угадал число.
  final bool isWon;

  const GameState({
    required this.secretNumber,
    required this.attemptsMade,
    required this.maxAttempts,
    required this.rangeMin,
    required this.rangeMax,
    this.lastResult,
    this.isGameOver = false,
    this.isWon = false,
  });

  /// Количество оставшихся попыток.
  int get attemptsLeft => maxAttempts - attemptsMade;

  /// Копия объекта с изменёнными полями (паттерн copyWith).
  GameState copyWith({
    int? secretNumber,
    int? attemptsMade,
    int? maxAttempts,
    int? rangeMin,
    int? rangeMax,
    GuessResult? lastResult,
    bool? isGameOver,
    bool? isWon,
  }) {
    return GameState(
      secretNumber: secretNumber ?? this.secretNumber,
      attemptsMade: attemptsMade ?? this.attemptsMade,
      maxAttempts: maxAttempts ?? this.maxAttempts,
      rangeMin: rangeMin ?? this.rangeMin,
      rangeMax: rangeMax ?? this.rangeMax,
      lastResult: lastResult ?? this.lastResult,
      isGameOver: isGameOver ?? this.isGameOver,
      isWon: isWon ?? this.isWon,
    );
  }
}

/// Перечисление возможных результатов одной попытки.
enum GuessResult {
  tooHigh,   // введённое число больше загаданного
  tooLow,    // введённое число меньше загаданного
  correct,   // угадал
  gameOver,  // исчерпаны все попытки
}

import 'dart:math';
import 'package:flutter/foundation.dart';
import 'package:shared_preferences/shared_preferences.dart';

import '../models/game_state.dart';
import '../services/stats_service.dart';

/// Ключи для хранения настроек в SharedPreferences.
const _kRangeMin = 'rangeMin';
const _kRangeMax = 'rangeMax';

/// Константа: максимальное количество попыток по условию задания.
const kDefaultMaxAttempts = 7;

/// GameProvider — главный класс состояния, используется через Provider.
///
/// Почему Provider, а не BLoC/Riverpod?
/// Provider — наименьший порог вхождения для учебного проекта:
/// один класс, extends ChangeNotifier, notifyListeners() — и всё работает.
/// BLoC требует Events/States/Bloc-классов; Riverpod — отдельных провайдеров.
class GameProvider extends ChangeNotifier {
  GameProvider({
    StatsService? statsService,
    Random? random,
  })  : _statsService = statsService ?? StatsService(),
        _random = random ?? Random.secure() {
    // При создании загружаем сохранённые настройки и начинаем игру.
    _initAsync();
  }

  final StatsService _statsService;
  final Random _random;

  // ─── Настройки ────────────────────────────────────────────────────────────
  int _rangeMin = 1;
  int _rangeMax = 100;

  int get rangeMin => _rangeMin;
  int get rangeMax => _rangeMax;

  // ─── Статистика ───────────────────────────────────────────────────────────
  int _totalGames = 0;
  int _wins = 0;
  int? _bestScore; // минимальное количество попыток за победу

  int get totalGames => _totalGames;
  int get wins => _wins;
  int? get bestScore => _bestScore;

  /// Процент побед, форматированный как строка (напр. "66.7").
  String get winRate {
    if (_totalGames == 0) return '0.0';
    return (_wins / _totalGames * 100).toStringAsFixed(1);
  }

  // ─── Состояние текущей игры ───────────────────────────────────────────────
  GameState _state = const GameState(
    secretNumber: 1,
    attemptsMade: 0,
    maxAttempts: kDefaultMaxAttempts,
    rangeMin: 1,
    rangeMax: 100,
  );
  GameState get state => _state;

  /// Загружает настройки и статистику, затем стартует первую игру.
  Future<void> _initAsync() async {
    await _loadSettings();
    await _loadStats();
    _startNewGame();
  }

  Future<void> _loadSettings() async {
    final prefs = await SharedPreferences.getInstance();
    _rangeMin = prefs.getInt(_kRangeMin) ?? 1;
    _rangeMax = prefs.getInt(_kRangeMax) ?? 100;
  }

  Future<void> _loadStats() async {
    _totalGames = await _statsService.getTotalGames();
    _wins = await _statsService.getWins();
    _bestScore = await _statsService.getBestScore();
  }

  /// Генерирует новое секретное число и сбрасывает состояние игры.
  void _startNewGame() {
    // nextInt(n) даёт [0, n), поэтому +1 чтобы включить rangeMax.
    final secret = _rangeMin + _random.nextInt(_rangeMax - _rangeMin + 1);
    _state = GameState(
      secretNumber: secret,
      attemptsMade: 0,
      maxAttempts: kDefaultMaxAttempts,
      rangeMin: _rangeMin,
      rangeMax: _rangeMax,
    );
    notifyListeners();
  }

  /// Публичный метод: начать новую игру.
  Future<void> newGame() async {
    // Перед новой игрой убеждаемся, что настройки актуальны.
    await _loadSettings();
    _startNewGame();
  }

  /// Проверяет введённое число [guess] и обновляет состояние.
  ///
  /// Возвращает [GuessResult] — результат текущего хода.
  /// Если игра уже закончена, вызов игнорируется.
  Future<GuessResult> checkGuess(int guess) async {
    if (_state.isGameOver) return GuessResult.gameOver;

    final newAttemptsMade = _state.attemptsMade + 1;
    late GuessResult result;
    late bool isGameOver;
    late bool isWon;

    if (guess == _state.secretNumber) {
      result = GuessResult.correct;
      isGameOver = true;
      isWon = true;
    } else if (newAttemptsMade >= _state.maxAttempts) {
      // Попытки исчерпаны и число не угадано.
      result = GuessResult.gameOver;
      isGameOver = true;
      isWon = false;
    } else {
      result = guess > _state.secretNumber ? GuessResult.tooHigh : GuessResult.tooLow;
      isGameOver = false;
      isWon = false;
    }

    _state = _state.copyWith(
      attemptsMade: newAttemptsMade,
      lastResult: result,
      isGameOver: isGameOver,
      isWon: isWon,
    );

    notifyListeners();

    // Если игра завершилась — сохраняем статистику.
    if (isGameOver) {
      await _saveGameResult(won: isWon, attempts: newAttemptsMade);
    }

    return result;
  }

  /// Сохраняет результат игры в StatsService и обновляет локальные значения.
  Future<void> _saveGameResult({required bool won, required int attempts}) async {
    await _statsService.saveGameResult(won: won, attempts: attempts);
    // Обновляем кешированные значения, чтобы экран статистики был актуальным.
    _totalGames = await _statsService.getTotalGames();
    _wins = await _statsService.getWins();
    _bestScore = await _statsService.getBestScore();
    notifyListeners();
  }

  /// Изменяет диапазон и сохраняет в SharedPreferences.
  Future<void> setRange(int min, int max) async {
    assert(min < max, 'min должен быть меньше max');
    _rangeMin = min;
    _rangeMax = max;
    final prefs = await SharedPreferences.getInstance();
    await prefs.setInt(_kRangeMin, min);
    await prefs.setInt(_kRangeMax, max);
    notifyListeners();
  }

  /// Сбрасывает всю статистику игр.
  Future<void> resetStats() async {
    await _statsService.resetStats();
    _totalGames = 0;
    _wins = 0;
    _bestScore = null;
    notifyListeners();
  }

  /// Валидация строки ввода.
  ///
  /// Возвращает null, если строка корректна, иначе — сообщение об ошибке.
  /// Используется в FormField.validator и напрямую в тестах.
  String? validateInput(String? value) {
    if (value == null || value.trim().isEmpty) {
      return 'empty'; // ключ для локализованного сообщения
    }
    final n = int.tryParse(value.trim());
    if (n == null) return 'invalid';
    if (n < _state.rangeMin || n > _state.rangeMax) return 'outOfRange';
    return null;
  }
}

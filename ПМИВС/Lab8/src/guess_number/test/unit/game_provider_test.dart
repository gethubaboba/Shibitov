import 'dart:math';

import 'package:flutter_test/flutter_test.dart';
import 'package:shared_preferences/shared_preferences.dart';

import 'package:guess_number/models/game_state.dart';
import 'package:guess_number/providers/game_provider.dart';
import 'package:guess_number/services/stats_service.dart';

/// Unit-тесты для GameProvider.
///
/// Тестируем логику игры в изоляции:
/// - SharedPreferences мокируется через setMockInitialValues.
/// - Random фиксируется через FakeRandom, чтобы тест был детерминированным.
void main() {
  // Перед каждым тестом сбрасываем SharedPreferences.
  setUp(() {
    SharedPreferences.setMockInitialValues({});
  });

  group('GameProvider — логика игры', () {
    test('newGame генерирует число в заданном диапазоне (1–100)', () async {
      final gp = GameProvider(random: _fixedRandom(42));
      await _waitForInit(gp);

      expect(gp.state.secretNumber, inInclusiveRange(1, 100));
    });

    test('checkGuess(correct) → GuessResult.correct, isWon = true', () async {
      final secret = 37;
      final gp = GameProvider(random: _fixedRandom(secret - 1)); // nextInt(100) = 36, +1 = 37
      await _waitForInit(gp);

      final result = await gp.checkGuess(secret);

      expect(result, GuessResult.correct);
      expect(gp.state.isWon, true);
      expect(gp.state.isGameOver, true);
    });

    test('checkGuess(слишком большое) → GuessResult.tooHigh', () async {
      final gp = GameProvider(random: _fixedRandom(49)); // secret = 50
      await _waitForInit(gp);

      final result = await gp.checkGuess(99);

      expect(result, GuessResult.tooHigh);
      expect(gp.state.isGameOver, false);
    });

    test('checkGuess(слишком маленькое) → GuessResult.tooLow', () async {
      final gp = GameProvider(random: _fixedRandom(49)); // secret = 50
      await _waitForInit(gp);

      final result = await gp.checkGuess(10);

      expect(result, GuessResult.tooLow);
    });

    test('после maxAttempts неверных попыток → GuessResult.gameOver', () async {
      final gp = GameProvider(random: _fixedRandom(49)); // secret = 50
      await _waitForInit(gp);

      // Делаем 6 неверных попыток (число не 50).
      for (int i = 0; i < kDefaultMaxAttempts - 1; i++) {
        await gp.checkGuess(1);
      }
      // 7-я попытка — последняя, число по-прежнему неверное.
      final result = await gp.checkGuess(1);

      expect(result, GuessResult.gameOver);
      expect(gp.state.isGameOver, true);
      expect(gp.state.isWon, false);
    });

    test('после isGameOver вызов checkGuess игнорируется', () async {
      final gp = GameProvider(random: _fixedRandom(49)); // secret = 50
      await _waitForInit(gp);
      await gp.checkGuess(50); // win
      expect(gp.state.isGameOver, true);

      final attemptsBefore = gp.state.attemptsMade;
      await gp.checkGuess(50); // должен быть проигнорирован
      expect(gp.state.attemptsMade, attemptsBefore);
    });

    test('newGame сбрасывает состояние', () async {
      final gp = GameProvider(random: _fixedRandom(49));
      await _waitForInit(gp);
      await gp.checkGuess(50); // win

      await gp.newGame();

      expect(gp.state.isGameOver, false);
      expect(gp.state.attemptsMade, 0);
      expect(gp.state.lastResult, null);
    });
  });

  group('GameProvider — валидация ввода', () {
    late GameProvider gp;

    setUp(() async {
      gp = GameProvider(random: _fixedRandom(49));
      await _waitForInit(gp);
    });

    test('пустая строка → ошибка', () {
      expect(gp.validateInput(''), isNotNull);
      expect(gp.validateInput(null), isNotNull);
      expect(gp.validateInput('   '), isNotNull);
    });

    test('не число → ошибка', () {
      expect(gp.validateInput('abc'), isNotNull);
      expect(gp.validateInput('12.5'), isNotNull);
    });

    test('вне диапазона → ошибка (диапазон 1-100)', () {
      expect(gp.validateInput('0'), isNotNull);
      expect(gp.validateInput('101'), isNotNull);
      expect(gp.validateInput('-1'), isNotNull);
    });

    test('корректные числа → null (валидно)', () {
      expect(gp.validateInput('1'), isNull);
      expect(gp.validateInput('50'), isNull);
      expect(gp.validateInput('100'), isNull);
    });
  });

  group('GameProvider — статистика', () {
    test('победа увеличивает totalGames и wins', () async {
      final gp = GameProvider(random: _fixedRandom(49)); // secret = 50
      await _waitForInit(gp);
      await gp.checkGuess(50);

      expect(gp.totalGames, 1);
      expect(gp.wins, 1);
    });

    test('проигрыш увеличивает totalGames, но не wins', () async {
      final gp = GameProvider(random: _fixedRandom(49)); // secret = 50
      await _waitForInit(gp);
      for (int i = 0; i < kDefaultMaxAttempts; i++) {
        await gp.checkGuess(1);
      }

      expect(gp.totalGames, 1);
      expect(gp.wins, 0);
    });

    test('bestScore обновляется при лучшем результате', () async {
      final gp1 = GameProvider(random: _fixedRandom(49)); // secret = 50
      await _waitForInit(gp1);
      // Тратим 3 попытки: 2 промаха + угадывание.
      await gp1.checkGuess(1);
      await gp1.checkGuess(2);
      await gp1.checkGuess(50);
      expect(gp1.bestScore, 3);

      // Новая игра, угадываем с 1-й попытки.
      await gp1.newGame();
      await gp1.checkGuess(gp1.state.secretNumber);
      expect(gp1.bestScore, 1); // обновился
    });

    test('resetStats обнуляет все поля', () async {
      final gp = GameProvider(random: _fixedRandom(49));
      await _waitForInit(gp);
      await gp.checkGuess(50);
      await gp.resetStats();

      expect(gp.totalGames, 0);
      expect(gp.wins, 0);
      expect(gp.bestScore, null);
    });

    test('winRate = 0.0 при нулевом количестве игр', () async {
      final gp = GameProvider(random: _fixedRandom(49));
      await _waitForInit(gp);
      expect(gp.winRate, '0.0');
    });
  });

  group('StatsService — сохранение и загрузка', () {
    test('saveGameResult(won) корректно обновляет SharedPreferences', () async {
      final service = StatsService();
      await service.saveGameResult(won: true, attempts: 3);
      await service.saveGameResult(won: false, attempts: 7);
      await service.saveGameResult(won: true, attempts: 2);

      expect(await service.getTotalGames(), 3);
      expect(await service.getWins(), 2);
      expect(await service.getBestScore(), 2); // лучший из 3 и 2
    });

    test('resetStats очищает все значения', () async {
      final service = StatsService();
      await service.saveGameResult(won: true, attempts: 4);
      await service.resetStats();

      expect(await service.getTotalGames(), 0);
      expect(await service.getWins(), 0);
      expect(await service.getBestScore(), null);
    });
  });
}

// ─── Вспомогательные утилиты ──────────────────────────────────────────────────

/// Создаёт Random, который всегда возвращает [value] из nextInt().
Random _fixedRandom(int value) => _FixedRandom(value);

class _FixedRandom implements Random {
  final int _value;
  _FixedRandom(this._value);

  @override
  int nextInt(int max) => _value % max;

  @override
  double nextDouble() => 0.5;

  @override
  bool nextBool() => true;
}

/// Ждём завершения _initAsync внутри GameProvider.
/// GameProvider инициализируется асинхронно — даём event loop выполниться.
Future<void> _waitForInit(GameProvider gp) async {
  // 3 итерации Future.microtask гарантируют завершение цепочки async-вызовов.
  await Future<void>.delayed(const Duration(milliseconds: 50));
}

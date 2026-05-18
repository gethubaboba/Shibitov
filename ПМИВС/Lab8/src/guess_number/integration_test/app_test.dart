import 'dart:math';

import 'package:flutter/material.dart';
import 'package:flutter_localizations/flutter_localizations.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:integration_test/integration_test.dart';
import 'package:provider/provider.dart';
import 'package:shared_preferences/shared_preferences.dart';

import 'package:guess_number/main.dart' as app;
import 'package:guess_number/providers/game_provider.dart';
import 'package:guess_number/screens/game_screen.dart';

/// Интеграционные тесты — запускаются на эмуляторе/устройстве.
///
/// Запуск: flutter test integration_test/app_test.dart
///
/// Тестируем полный игровой сценарий «от запуска до победы».
void main() {
  IntegrationTestWidgetsFlutterBinding.ensureInitialized();

  setUp(() {
    SharedPreferences.setMockInitialValues({});
  });

  group('Интеграционный тест — полный игровой цикл', () {
    testWidgets('Сценарий: запуск → ввод числа → победа', (tester) async {
      // Запускаем приложение со «зафиксированным» Random,
      // чтобы знать заранее, какое число загадано.
      await tester.pumpWidget(
        ChangeNotifierProvider(
          create: (_) => GameProvider(random: _FixedRandom(49)), // secret = 50
          child: MaterialApp(
            localizationsDelegates: const [
              GlobalMaterialLocalizations.delegate,
              GlobalWidgetsLocalizations.delegate,
            ],
            supportedLocales: const [Locale('ru')],
            locale: const Locale('ru'),
            home: GameScreen(onLocaleChange: (_) {}),
          ),
        ),
      );
      await tester.pumpAndSettle();

      // 1. Поле ввода отображается и доступно.
      final inputFinder = find.byKey(const Key('guessInput'));
      expect(inputFinder, findsOneWidget);

      // 2. Вводим неверное число (слишком маленькое).
      await tester.enterText(inputFinder, '20');
      await tester.tap(find.byKey(const Key('checkButton')));
      await tester.pumpAndSettle();

      // После хода поле очищается.
      final inputWidget = tester.widget<TextFormField>(inputFinder);
      // Попыток стало 1.
      final providerAfter1 = tester
          .element(find.byType(GameScreen))
          .read<GameProvider>();
      expect(providerAfter1.state.attemptsMade, 1);

      // 3. Вводим верное число.
      await tester.enterText(inputFinder, '50');
      await tester.tap(find.byKey(const Key('checkButton')));
      await tester.pumpAndSettle();

      final providerWin = tester
          .element(find.byType(GameScreen))
          .read<GameProvider>();
      expect(providerWin.state.isWon, true);
      expect(providerWin.state.isGameOver, true);

      // 4. Кнопка «Проверить» задизейблена.
      final btn = tester.widget<FilledButton>(find.byKey(const Key('checkButton')));
      expect(btn.onPressed, isNull);

      // 5. Начинаем новую игру — всё сбрасывается.
      await tester.tap(find.byKey(const Key('newGameButton')));
      await tester.pumpAndSettle();

      final providerNew = tester
          .element(find.byType(GameScreen))
          .read<GameProvider>();
      expect(providerNew.state.isGameOver, false);
      expect(providerNew.state.attemptsMade, 0);

      // 6. Статистика обновилась: 1 игра, 1 победа.
      expect(providerNew.totalGames, 1);
      expect(providerNew.wins, 1);
    });

    testWidgets('Сценарий: проигрыш после 7 попыток', (tester) async {
      await tester.pumpWidget(
        ChangeNotifierProvider(
          create: (_) => GameProvider(random: _FixedRandom(49)), // secret = 50
          child: MaterialApp(
            localizationsDelegates: const [
              GlobalMaterialLocalizations.delegate,
              GlobalWidgetsLocalizations.delegate,
            ],
            supportedLocales: const [Locale('ru')],
            locale: const Locale('ru'),
            home: GameScreen(onLocaleChange: (_) {}),
          ),
        ),
      );
      await tester.pumpAndSettle();

      // Делаем 7 неверных попыток (число не 50).
      for (int i = 1; i <= 7; i++) {
        await tester.enterText(find.byKey(const Key('guessInput')), '1');
        await tester.tap(find.byKey(const Key('checkButton')));
        await tester.pumpAndSettle();
      }

      final provider = tester
          .element(find.byType(GameScreen))
          .read<GameProvider>();
      expect(provider.state.isGameOver, true);
      expect(provider.state.isWon, false);
      expect(provider.wins, 0);
      expect(provider.totalGames, 1);
    });
  });
}

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

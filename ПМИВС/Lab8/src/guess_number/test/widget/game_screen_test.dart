import 'dart:math';

import 'package:flutter/material.dart';
import 'package:flutter_localizations/flutter_localizations.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:provider/provider.dart';
import 'package:shared_preferences/shared_preferences.dart';

import 'package:guess_number/l10n/app_localizations.dart';
import 'package:guess_number/providers/game_provider.dart';
import 'package:guess_number/screens/game_screen.dart';

/// Widget-тесты для GameScreen.
///
/// Тестируем UI-поведение: отображение элементов, реакция на ввод,
/// граничные случаи (пустой ввод, число вне диапазона).
void main() {
  setUp(() {
    SharedPreferences.setMockInitialValues({});
  });

  /// Вспомогательная функция: оборачивает тестируемый виджет в нужный контекст.
  Widget buildTestApp({Random? random}) {
    return ChangeNotifierProvider(
      create: (_) => GameProvider(random: random ?? Random()),
      child: MaterialApp(
        localizationsDelegates: const [
          // Используем делегат из пакета, сгенерированный при build.
          // В тестах мы мокируем его через MaterialApp напрямую.
          AppLocalizations.delegate,
          GlobalMaterialLocalizations.delegate,
          GlobalWidgetsLocalizations.delegate,
          GlobalCupertinoLocalizations.delegate,
        ],
        supportedLocales: const [
          Locale('ru'),
          Locale('en'),
          Locale('be'),
        ],
        locale: const Locale('ru'),
        home: GameScreen(onLocaleChange: (_) {}),
      ),
    );
  }

  group('GameScreen — отображение элементов', () {
    testWidgets('при запуске видны поле ввода и кнопки', (tester) async {
      await tester.pumpWidget(buildTestApp());
      await tester.pumpAndSettle();

      expect(find.byKey(const Key('guessInput')), findsOneWidget);
      expect(find.byKey(const Key('checkButton')), findsOneWidget);
      expect(find.byKey(const Key('newGameButton')), findsOneWidget);
    });
  });

  group('GameScreen — ввод и валидация', () {
    testWidgets('пустой ввод не приводит к checkGuess', (tester) async {
      await tester.pumpWidget(buildTestApp(random: _FixedRandom(49))); // secret=50
      await tester.pumpAndSettle();

      // Нажимаем «Проверить» без ввода.
      await tester.tap(find.byKey(const Key('checkButton')));
      await tester.pumpAndSettle();

      // Подсказка не должна была появиться (игра не обработала ход).
      // Проверяем через провайдер
      final provider = tester
          .element(find.byType(GameScreen))
          .read<GameProvider>();
      expect(provider.state.attemptsMade, 0);
    });

    testWidgets('корректный ввод увеличивает счётчик попыток', (tester) async {
      await tester.pumpWidget(buildTestApp(random: _FixedRandom(49))); // secret=50
      await tester.pumpAndSettle();

      await tester.enterText(find.byKey(const Key('guessInput')), '30');
      await tester.tap(find.byKey(const Key('checkButton')));
      await tester.pumpAndSettle();

      final provider = tester
          .element(find.byType(GameScreen))
          .read<GameProvider>();
      expect(provider.state.attemptsMade, 1);
    });

    testWidgets('кнопка «Новая игра» сбрасывает попытки', (tester) async {
      await tester.pumpWidget(buildTestApp(random: _FixedRandom(49)));
      await tester.pumpAndSettle();

      await tester.enterText(find.byKey(const Key('guessInput')), '30');
      await tester.tap(find.byKey(const Key('checkButton')));
      await tester.pumpAndSettle();

      await tester.tap(find.byKey(const Key('newGameButton')));
      await tester.pumpAndSettle();

      final provider = tester
          .element(find.byType(GameScreen))
          .read<GameProvider>();
      expect(provider.state.attemptsMade, 0);
    });

    testWidgets('после победы кнопка «Проверить» отключается', (tester) async {
      await tester.pumpWidget(buildTestApp(random: _FixedRandom(49))); // secret=50
      await tester.pumpAndSettle();

      // Угадываем число.
      await tester.enterText(find.byKey(const Key('guessInput')), '50');
      await tester.tap(find.byKey(const Key('checkButton')));
      await tester.pumpAndSettle();

      // Кнопка должна быть задизейблена.
      final btn = tester.widget<FilledButton>(find.byKey(const Key('checkButton')));
      expect(btn.onPressed, isNull);
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

import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:provider/provider.dart';
import 'package:guess_number/l10n/app_localizations.dart';

import '../models/game_state.dart';
import '../providers/game_provider.dart';
import '../widgets/hint_display.dart';
import '../widgets/attempt_counter.dart';
import 'stats_screen.dart';
import 'settings_screen.dart';

/// Главный экран игры.
///
/// Тонкий UI-слой: вся логика делегируется GameProvider.
/// Виджет только «слушает» состояние и вызывает методы провайдера.
class GameScreen extends StatefulWidget {
  /// Колбэк для смены локали (передаётся из main.dart).
  final void Function(Locale) onLocaleChange;

  const GameScreen({super.key, required this.onLocaleChange});

  @override
  State<GameScreen> createState() => _GameScreenState();
}

class _GameScreenState extends State<GameScreen>
    with SingleTickerProviderStateMixin {
  final _formKey = GlobalKey<FormState>();
  final _controller = TextEditingController();

  // Анимационный контроллер для встряхивания при ошибке.
  late AnimationController _shakeController;
  late Animation<double> _shakeAnimation;

  // Анимация фона: меняет цвет в зависимости от результата.
  Color _bgColor = const Color(0xFFF3EFF4);

  @override
  void initState() {
    super.initState();
    _shakeController = AnimationController(
      vsync: this,
      duration: const Duration(milliseconds: 400),
    );
    // Горизонтальная встряска: -10px → +10px → 0.
    _shakeAnimation = TweenSequence<double>([
      TweenSequenceItem(tween: Tween(begin: 0.0, end: -10.0), weight: 1),
      TweenSequenceItem(tween: Tween(begin: -10.0, end: 10.0), weight: 2),
      TweenSequenceItem(tween: Tween(begin: 10.0, end: -10.0), weight: 2),
      TweenSequenceItem(tween: Tween(begin: -10.0, end: 0.0), weight: 1),
    ]).animate(CurvedAnimation(parent: _shakeController, curve: Curves.easeInOut));
  }

  @override
  void dispose() {
    _controller.dispose();
    _shakeController.dispose();
    super.dispose();
  }

  /// Обрабатывает нажатие кнопки «Проверить».
  Future<void> _onCheck(GameProvider gp, AppLocalizations l10n) async {
    if (!_formKey.currentState!.validate()) {
      // Некорректный ввод — встряхиваем форму.
      _shakeController.forward(from: 0);
      return;
    }

    final guess = int.parse(_controller.text.trim());
    final result = await gp.checkGuess(guess);
    _controller.clear();

    // Обновляем цвет фона по результату.
    setState(() {
      switch (result) {
        case GuessResult.correct:
          _bgColor = const Color(0xFFD4EDDA); // зелёный
        case GuessResult.gameOver:
          _bgColor = const Color(0xFFF8D7DA); // красный
        case GuessResult.tooHigh:
        case GuessResult.tooLow:
          _bgColor = const Color(0xFFFFF3CD); // жёлтый
      }
    });

    if (result == GuessResult.tooHigh || result == GuessResult.tooLow) {
      // Визуальная обратная связь при неверной попытке.
      _shakeController.forward(from: 0);
    }

    // Убираем фокус с поля ввода, чтобы скрыть клавиатуру.
    FocusScope.of(context).unfocus();
  }

  Future<void> _onNewGame(GameProvider gp) async {
    await gp.newGame();
    _controller.clear();
    setState(() => _bgColor = const Color(0xFFF3EFF4));
  }

  @override
  Widget build(BuildContext context) {
    final l10n = AppLocalizations.of(context);
    // Consumer перестраивает только нужную часть дерева при изменении GameProvider.
    return Consumer<GameProvider>(
      builder: (context, gp, _) {
        final state = gp.state;
        return AnimatedContainer(
          duration: const Duration(milliseconds: 500),
          color: _bgColor,
          child: Scaffold(
            backgroundColor: Colors.transparent,
            appBar: AppBar(
              title: Text(l10n.appTitle),
              backgroundColor: Colors.transparent,
              elevation: 0,
              actions: [
                // Кнопка перехода на экран статистики.
                IconButton(
                  icon: const Icon(Icons.bar_chart),
                  tooltip: l10n.statistics,
                  onPressed: () => Navigator.push(
                    context,
                    MaterialPageRoute(builder: (_) => const StatsScreen()),
                  ),
                ),
                // Кнопка перехода в настройки.
                IconButton(
                  icon: const Icon(Icons.settings),
                  tooltip: l10n.settings,
                  onPressed: () => Navigator.push(
                    context,
                    MaterialPageRoute(
                      builder: (_) => SettingsScreen(onLocaleChange: widget.onLocaleChange),
                    ),
                  ),
                ),
              ],
            ),
            body: SafeArea(
              child: SingleChildScrollView(
                padding: const EdgeInsets.symmetric(horizontal: 24, vertical: 16),
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.stretch,
                  children: [
                    const SizedBox(height: 16),

                    // ── Счётчик попыток ──────────────────────────────────
                    AttemptCounter(
                      attemptsMade: state.attemptsMade,
                      attemptsLeft: state.attemptsLeft,
                      maxAttempts: state.maxAttempts,
                    ),

                    const SizedBox(height: 24),

                    // ── Подсказка ────────────────────────────────────────
                    HintDisplay(
                      result: state.lastResult,
                      secretNumber: state.secretNumber,
                    ),

                    const SizedBox(height: 32),

                    // ── Форма ввода ──────────────────────────────────────
                    AnimatedBuilder(
                      animation: _shakeAnimation,
                      builder: (context, child) => Transform.translate(
                        offset: Offset(_shakeAnimation.value, 0),
                        child: child,
                      ),
                      child: Form(
                        key: _formKey,
                        child: TextFormField(
                          key: const Key('guessInput'),
                          controller: _controller,
                          enabled: !state.isGameOver,
                          keyboardType: TextInputType.number,
                          // Разрешаем вводить только цифры.
                          inputFormatters: [FilteringTextInputFormatter.digitsOnly],
                          decoration: InputDecoration(
                            labelText: l10n.guessLabel,
                            hintText: l10n.inputHint(state.rangeMin, state.rangeMax),
                          ),
                          validator: (value) {
                            final err = gp.validateInput(value);
                            if (err == null) return null;
                            return l10n.invalidInput(state.rangeMin, state.rangeMax);
                          },
                          onFieldSubmitted: state.isGameOver
                              ? null
                              : (_) => _onCheck(gp, l10n),
                        ),
                      ),
                    ),

                    const SizedBox(height: 16),

                    // ── Кнопка «Проверить» ───────────────────────────────
                    FilledButton(
                      key: const Key('checkButton'),
                      onPressed: state.isGameOver ? null : () => _onCheck(gp, l10n),
                      child: Text(l10n.checkButton),
                    ),

                    const SizedBox(height: 12),

                    // ── Кнопка «Новая игра» ──────────────────────────────
                    OutlinedButton(
                      key: const Key('newGameButton'),
                      onPressed: () => _onNewGame(gp),
                      child: Text(l10n.newGame),
                    ),

                    const SizedBox(height: 24),
                  ],
                ),
              ),
            ),
          ),
        );
      },
    );
  }
}

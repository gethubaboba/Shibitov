import 'package:flutter/material.dart';
import 'package:guess_number/l10n/app_localizations.dart';

import '../models/game_state.dart';

/// Виджет отображения подсказки.
///
/// Показывает текущий результат последней попытки с анимацией появления.
/// Использует FadeTransition + AnimatedSwitcher для плавной смены текста.
class HintDisplay extends StatelessWidget {
  final GuessResult? result;
  final int secretNumber;

  const HintDisplay({
    super.key,
    required this.result,
    required this.secretNumber,
  });

  @override
  Widget build(BuildContext context) {
    final l10n = AppLocalizations.of(context);
    final cs = Theme.of(context).colorScheme;

    final (text, color, icon) = switch (result) {
      GuessResult.tooHigh => (l10n.hintTooHigh, cs.error, Icons.arrow_upward),
      GuessResult.tooLow => (l10n.hintTooLow, cs.tertiary, Icons.arrow_downward),
      GuessResult.correct => (l10n.hintCorrect, Colors.green.shade700, Icons.check_circle),
      GuessResult.gameOver => (
          l10n.hintGameOver(secretNumber),
          cs.error,
          Icons.sentiment_dissatisfied
        ),
      null => ('', cs.outline, Icons.help_outline),
    };

    // AnimatedSwitcher плавно меняет один дочерний виджет на другой.
    return AnimatedSwitcher(
      duration: const Duration(milliseconds: 350),
      transitionBuilder: (child, animation) =>
          FadeTransition(opacity: animation, child: child),
      child: result == null
          // Пустой контейнер с фиксированной высотой, чтобы не прыгал layout.
          ? const SizedBox(key: ValueKey('empty'), height: 80)
          : Container(
              key: ValueKey(result),
              padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 20),
              decoration: BoxDecoration(
                color: color.withOpacity(0.12),
                borderRadius: BorderRadius.circular(12),
                border: Border.all(color: color.withOpacity(0.4)),
              ),
              child: Row(
                mainAxisAlignment: MainAxisAlignment.center,
                children: [
                  Icon(icon, color: color, size: 28),
                  const SizedBox(width: 12),
                  Flexible(
                    child: Text(
                      text,
                      style: TextStyle(
                        color: color,
                        fontSize: 18,
                        fontWeight: FontWeight.w600,
                      ),
                      textAlign: TextAlign.center,
                    ),
                  ),
                ],
              ),
            ),
    );
  }
}

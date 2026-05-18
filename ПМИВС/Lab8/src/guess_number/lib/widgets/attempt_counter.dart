import 'package:flutter/material.dart';
import 'package:guess_number/l10n/app_localizations.dart';

/// Виджет счётчика попыток.
///
/// Отображает прогресс-бар и текстовые счётчики.
/// Сделан отдельным виджетом, чтобы легко протестировать в widget-тестах.
class AttemptCounter extends StatelessWidget {
  final int attemptsMade;
  final int attemptsLeft;
  final int maxAttempts;

  const AttemptCounter({
    super.key,
    required this.attemptsMade,
    required this.attemptsLeft,
    required this.maxAttempts,
  });

  @override
  Widget build(BuildContext context) {
    final l10n = AppLocalizations.of(context);
    final cs = Theme.of(context).colorScheme;

    // Прогресс: насколько попытки исчерпаны (0.0 — начало, 1.0 — всё).
    final progress = maxAttempts > 0 ? attemptsMade / maxAttempts : 0.0;

    // Цвет прогресс-бара: зелёный → жёлтый → красный по мере исчерпания попыток.
    final barColor = progress < 0.5
        ? Colors.green
        : progress < 0.85
            ? Colors.orange
            : cs.error;

    return Column(
      crossAxisAlignment: CrossAxisAlignment.stretch,
      children: [
        Row(
          mainAxisAlignment: MainAxisAlignment.spaceBetween,
          children: [
            Text(
              l10n.attemptsMade(attemptsMade),
              style: Theme.of(context).textTheme.bodyMedium,
            ),
            Text(
              l10n.attemptsLeft(attemptsLeft),
              style: Theme.of(context)
                  .textTheme
                  .bodyMedium
                  ?.copyWith(fontWeight: FontWeight.bold),
            ),
          ],
        ),
        const SizedBox(height: 8),
        // Анимированный прогресс-бар.
        TweenAnimationBuilder<double>(
          tween: Tween(begin: 0, end: progress),
          duration: const Duration(milliseconds: 300),
          builder: (_, value, __) => LinearProgressIndicator(
            value: value,
            color: barColor,
            backgroundColor: cs.surfaceContainerHighest,
            minHeight: 8,
            borderRadius: BorderRadius.circular(4),
          ),
        ),
      ],
    );
  }
}

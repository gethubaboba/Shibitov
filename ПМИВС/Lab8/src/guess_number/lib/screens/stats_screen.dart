import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:guess_number/l10n/app_localizations.dart';

import '../providers/game_provider.dart';

/// Экран статистики — отображает сохранённые данные о прошедших играх.
class StatsScreen extends StatelessWidget {
  const StatsScreen({super.key});

  @override
  Widget build(BuildContext context) {
    final l10n = AppLocalizations.of(context);
    // Читаем только статистические поля — не нужно перерисовываться при ходах.
    return Scaffold(
      appBar: AppBar(
        title: Text(l10n.statistics),
        leading: BackButton(
          onPressed: () => Navigator.pop(context),
        ),
      ),
      body: Consumer<GameProvider>(
        builder: (context, gp, _) {
          return Padding(
            padding: const EdgeInsets.all(24),
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.stretch,
              children: [
                _StatCard(
                  icon: Icons.sports_esports,
                  label: l10n.statsGamesPlayed(gp.totalGames),
                ),
                const SizedBox(height: 12),
                _StatCard(
                  icon: Icons.emoji_events,
                  label: l10n.statsWins(gp.wins),
                ),
                const SizedBox(height: 12),
                _StatCard(
                  icon: Icons.percent,
                  label: l10n.statsWinRate(gp.winRate),
                ),
                const SizedBox(height: 12),
                _StatCard(
                  icon: Icons.star,
                  label: gp.bestScore != null
                      ? l10n.statsBestScore(gp.bestScore!)
                      : l10n.statsBestScoreNone,
                ),
                const Spacer(),
                // Кнопка сброса статистики — требует подтверждения.
                OutlinedButton.icon(
                  key: const Key('resetStatsButton'),
                  icon: const Icon(Icons.delete_outline),
                  label: Text(l10n.resetStats),
                  style: OutlinedButton.styleFrom(
                    foregroundColor: Theme.of(context).colorScheme.error,
                  ),
                  onPressed: () => _confirmReset(context, gp, l10n),
                ),
              ],
            ),
          );
        },
      ),
    );
  }

  Future<void> _confirmReset(
    BuildContext context,
    GameProvider gp,
    AppLocalizations l10n,
  ) async {
    final confirmed = await showDialog<bool>(
      context: context,
      builder: (ctx) => AlertDialog(
        title: Text(l10n.resetStats),
        content: const Text('Вы уверены? Статистика будет удалена навсегда.'),
        actions: [
          TextButton(
            onPressed: () => Navigator.pop(ctx, false),
            child: Text(l10n.back),
          ),
          FilledButton(
            onPressed: () => Navigator.pop(ctx, true),
            child: const Text('Сбросить'),
          ),
        ],
      ),
    );
    if (confirmed == true) await gp.resetStats();
  }
}

/// Карточка одной статистической метрики.
class _StatCard extends StatelessWidget {
  final IconData icon;
  final String label;

  const _StatCard({required this.icon, required this.label});

  @override
  Widget build(BuildContext context) {
    final cs = Theme.of(context).colorScheme;
    return Card(
      color: cs.primaryContainer,
      child: ListTile(
        leading: Icon(icon, color: cs.primary),
        title: Text(label, style: const TextStyle(fontSize: 16)),
      ),
    );
  }
}

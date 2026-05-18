import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:guess_number/l10n/app_localizations.dart';

import '../providers/game_provider.dart';

/// Экран настроек приложения.
///
/// Позволяет:
/// — изменить диапазон чисел (1-50, 1-100, 1-200);
/// — переключить язык интерфейса.
class SettingsScreen extends StatelessWidget {
  /// Колбэк для смены локали — передаётся «наверх» в main.dart.
  final void Function(Locale) onLocaleChange;

  const SettingsScreen({super.key, required this.onLocaleChange});

  @override
  Widget build(BuildContext context) {
    final l10n = AppLocalizations.of(context);
    final gp = context.read<GameProvider>(); // read, а не watch — изменения не нужны

    return Scaffold(
      appBar: AppBar(title: Text(l10n.settings)),
      body: ListView(
        padding: const EdgeInsets.all(16),
        children: [
          // ── Диапазон чисел ───────────────────────────────────────────────
          Text(
            l10n.rangeLabel,
            style: Theme.of(context).textTheme.titleMedium,
          ),
          const SizedBox(height: 8),
          _RangeTile(
            key: const Key('range50'),
            label: l10n.range1to50,
            onTap: () async {
              await gp.setRange(1, 50);
              await gp.newGame();
            },
          ),
          _RangeTile(
            key: const Key('range100'),
            label: l10n.range1to100,
            onTap: () async {
              await gp.setRange(1, 100);
              await gp.newGame();
            },
          ),
          _RangeTile(
            key: const Key('range200'),
            label: l10n.range1to200,
            onTap: () async {
              await gp.setRange(1, 200);
              await gp.newGame();
            },
          ),

          const Divider(height: 32),

          // ── Язык интерфейса ──────────────────────────────────────────────
          Text(
            l10n.language,
            style: Theme.of(context).textTheme.titleMedium,
          ),
          const SizedBox(height: 8),
          _LangTile(
            key: const Key('langRu'),
            label: 'Русский',
            onTap: () => onLocaleChange(const Locale('ru')),
          ),
          _LangTile(
            key: const Key('langEn'),
            label: 'English',
            onTap: () => onLocaleChange(const Locale('en')),
          ),
          _LangTile(
            key: const Key('langBe'),
            label: 'Беларуская',
            onTap: () => onLocaleChange(const Locale('be')),
          ),
        ],
      ),
    );
  }
}

class _RangeTile extends StatelessWidget {
  final String label;
  final VoidCallback onTap;

  const _RangeTile({super.key, required this.label, required this.onTap});

  @override
  Widget build(BuildContext context) {
    return ListTile(
      title: Text(label),
      trailing: const Icon(Icons.chevron_right),
      onTap: () {
        onTap();
        Navigator.pop(context); // возвращаемся на игровой экран
      },
    );
  }
}

class _LangTile extends StatelessWidget {
  final String label;
  final VoidCallback onTap;

  const _LangTile({super.key, required this.label, required this.onTap});

  @override
  Widget build(BuildContext context) {
    return ListTile(
      title: Text(label),
      trailing: const Icon(Icons.language),
      onTap: () {
        onTap();
        Navigator.pop(context);
      },
    );
  }
}

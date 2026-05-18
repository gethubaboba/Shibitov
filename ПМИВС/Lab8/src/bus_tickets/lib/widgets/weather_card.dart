import 'package:flutter/material.dart';
import 'package:bus_tickets/l10n/app_localizations.dart';

import '../models/weather_data.dart';
import '../providers/app_provider.dart';

/// Виджет карточки погоды.
///
/// Отображает текущую погоду для выбранного города.
/// Показывает индикатор загрузки, данные или сообщение об ошибке.
class WeatherCard extends StatelessWidget {

  const WeatherCard({
    super.key,
    required this.state,
    required this.data,
    required this.error,
    required this.cityName,
  });
  final LoadingState state;
  final WeatherData? data;
  final String error;
  final String cityName;

  @override
  Widget build(BuildContext context) {
    final l10n = AppLocalizations.of(context);
    final cs = Theme.of(context).colorScheme;

    return AnimatedSwitcher(
      duration: const Duration(milliseconds: 400),
      child: switch (state) {
        LoadingState.loading || LoadingState.idle => _buildLoading(l10n),
        LoadingState.error => _buildError(l10n),
        LoadingState.success when data != null => _buildData(context, cs, l10n, data!),
        _ => _buildError(l10n),
      },
    );
  }

  Widget _buildLoading(AppLocalizations l10n) {
    return Card(
      key: const ValueKey('loading'),
      child: Padding(
        padding: const EdgeInsets.all(16),
        child: Row(
          children: [
            const SizedBox(width: 24, height: 24, child: CircularProgressIndicator(strokeWidth: 2)),
            const SizedBox(width: 12),
            Text(l10n.weatherLoading),
          ],
        ),
      ),
    );
  }

  Widget _buildError(AppLocalizations l10n) {
    return Card(
      key: const ValueKey('error'),
      color: Colors.orange.shade50,
      child: Padding(
        padding: const EdgeInsets.all(12),
        child: Row(
          children: [
            const Icon(Icons.cloud_off, color: Colors.orange),
            const SizedBox(width: 8),
            Flexible(child: Text(l10n.weatherError, style: const TextStyle(color: Colors.orange))),
          ],
        ),
      ),
    );
  }

  Widget _buildData(
    BuildContext context,
    ColorScheme cs,
    AppLocalizations l10n,
    WeatherData d,
  ) {
    final tempStr = d.temperature.toStringAsFixed(1);
    return Card(
      key: const ValueKey('data'),
      color: cs.primaryContainer,
      child: Padding(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text(
              l10n.weatherFor(cityName),
              style: Theme.of(context).textTheme.titleSmall?.copyWith(
                    color: cs.onPrimaryContainer,
                    fontWeight: FontWeight.bold,
                  ),
            ),
            const SizedBox(height: 8),
            Row(
              children: [
                Text(
                  l10n.temperature(tempStr),
                  style: Theme.of(context).textTheme.headlineMedium?.copyWith(
                        color: cs.onPrimaryContainer,
                        fontWeight: FontWeight.bold,
                      ),
                ),
                const SizedBox(width: 12),
                Expanded(
                  child: Text(
                    d.description,
                    style: TextStyle(color: cs.onPrimaryContainer),
                  ),
                ),
              ],
            ),
            const SizedBox(height: 4),
            Row(
              children: [
                Icon(Icons.water_drop, size: 14, color: cs.onPrimaryContainer),
                const SizedBox(width: 4),
                Text(
                  l10n.humidity(d.humidity),
                  style: TextStyle(color: cs.onPrimaryContainer, fontSize: 13),
                ),
                const SizedBox(width: 12),
                Icon(Icons.air, size: 14, color: cs.onPrimaryContainer),
                const SizedBox(width: 4),
                Text(
                  l10n.wind(d.windSpeed.toStringAsFixed(1)),
                  style: TextStyle(color: cs.onPrimaryContainer, fontSize: 13),
                ),
              ],
            ),
          ],
        ),
      ),
    );
  }
}

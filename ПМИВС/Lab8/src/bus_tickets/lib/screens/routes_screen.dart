import 'package:flutter/material.dart';
import 'package:bus_tickets/l10n/app_localizations.dart';
import 'package:provider/provider.dart';

import '../models/bus_route.dart';
import '../models/city.dart';
import '../providers/app_provider.dart';
import '../widgets/weather_card.dart';

/// Экран маршрутов выбранного города.
///
/// Показывает: карточку погоды (сверху) + список маршрутов с местами.
class RoutesScreen extends StatelessWidget {
  const RoutesScreen({super.key, required this.city});
  final City city;

  @override
  Widget build(BuildContext context) {
    final l10n = AppLocalizations.of(context);
    return Scaffold(
      appBar: AppBar(title: Text(city.nameRu)),
      body: Consumer<AppProvider>(
        builder: (context, ap, _) {
          return CustomScrollView(
            slivers: [
              // ── Карточка погоды ────────────────────────────────────────
              SliverToBoxAdapter(
                child: Padding(
                  padding: const EdgeInsets.all(12),
                  child: WeatherCard(
                    state: ap.weatherState,
                    data: ap.weatherData,
                    error: ap.weatherError,
                    cityName: city.nameRu,
                  ),
                ),
              ),

              // ── Заголовок раздела маршрутов ────────────────────────────
              SliverToBoxAdapter(
                child: Padding(
                  padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 4),
                  child: Text(
                    l10n.routes,
                    style: Theme.of(context).textTheme.titleMedium,
                  ),
                ),
              ),

              // ── Список маршрутов ───────────────────────────────────────
              if (ap.routesState == LoadingState.loading)
                const SliverFillRemaining(
                  child: Center(child: CircularProgressIndicator()),
                )
              else if (ap.routes.isEmpty)
                SliverFillRemaining(
                  child: Center(child: Text(l10n.noRoutesFound)),
                )
              else
                SliverList(
                  delegate: SliverChildBuilderDelegate(
                    (context, index) {
                      final route = ap.routes[index];
                      return _RouteCard(
                        key: Key('route_${route.id}'),
                        route: route,
                        onBook: () => _confirmBook(context, ap, route, l10n),
                      );
                    },
                    childCount: ap.routes.length,
                  ),
                ),
            ],
          );
        },
      ),
    );
  }

  Future<void> _confirmBook(
    BuildContext context,
    AppProvider ap,
    BusRoute route,
    AppLocalizations l10n,
  ) async {
    final confirmed = await showDialog<bool>(
      context: context,
      builder: (ctx) => AlertDialog(
        title: Text(l10n.book),
        content: Text(
          '${l10n.routeNumber(route.number)}\n'
          '${l10n.departure}: ${route.departure}\n'
          '${l10n.priceValue(route.price.toStringAsFixed(2))}',
        ),
        actions: [
          TextButton(
            onPressed: () => Navigator.pop(ctx, false),
            child: Text(l10n.cancel),
          ),
          FilledButton(
            onPressed: () => Navigator.pop(ctx, true),
            child: Text(l10n.confirm),
          ),
        ],
      ),
    );

    if (confirmed == true && context.mounted) {
      final success = await ap.bookTicket(route);
      if (context.mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(
            content: Text(success ? l10n.bookedSuccess : l10n.errorOccurred(ap.errorMessage)),
            backgroundColor: success ? Colors.green : Colors.red,
          ),
        );
      }
    }
  }
}

/// Карточка одного маршрута.
class _RouteCard extends StatelessWidget {

  const _RouteCard({super.key, required this.route, required this.onBook});
  final BusRoute route;
  final VoidCallback onBook;

  @override
  Widget build(BuildContext context) {
    final l10n = AppLocalizations.of(context);
    final cs = Theme.of(context).colorScheme;
    final hasSeats = route.hasSeats;

    return AnimatedContainer(
      duration: const Duration(milliseconds: 300),
      margin: const EdgeInsets.symmetric(horizontal: 12, vertical: 6),
      decoration: BoxDecoration(
        color: hasSeats ? cs.surface : cs.surfaceContainerHighest,
        borderRadius: BorderRadius.circular(12),
        border: Border.all(
          color: hasSeats ? cs.primary.withOpacity(0.3) : cs.outline.withOpacity(0.2),
        ),
        boxShadow: hasSeats
            ? [BoxShadow(color: cs.primary.withOpacity(0.08), blurRadius: 8, offset: const Offset(0, 2))]
            : null,
      ),
      child: Padding(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Row(
              children: [
                Icon(Icons.directions_bus, color: cs.primary),
                const SizedBox(width: 8),
                Text(
                  l10n.routeNumber(route.number),
                  style: Theme.of(context).textTheme.titleMedium?.copyWith(
                        color: cs.primary,
                        fontWeight: FontWeight.bold,
                      ),
                ),
                const Spacer(),
                // Бейдж с количеством мест.
                Container(
                  padding: const EdgeInsets.symmetric(horizontal: 10, vertical: 4),
                  decoration: BoxDecoration(
                    color: hasSeats ? Colors.green.shade100 : Colors.red.shade100,
                    borderRadius: BorderRadius.circular(20),
                  ),
                  child: Text(
                    hasSeats
                        ? l10n.seatsValue(route.seatsAvailable)
                        : l10n.noSeats,
                    style: TextStyle(
                      color: hasSeats ? Colors.green.shade700 : Colors.red.shade700,
                      fontSize: 12,
                      fontWeight: FontWeight.w600,
                    ),
                  ),
                ),
              ],
            ),
            const SizedBox(height: 12),
            Row(
              children: [
                _InfoChip(icon: Icons.schedule, label: route.departure),
                const Icon(Icons.arrow_forward, size: 16, color: Colors.grey),
                _InfoChip(icon: Icons.flag, label: route.arrival),
                const Spacer(),
                Text(
                  l10n.priceValue(route.price.toStringAsFixed(2)),
                  style: Theme.of(context)
                      .textTheme
                      .titleMedium
                      ?.copyWith(fontWeight: FontWeight.bold),
                ),
              ],
            ),
            if (hasSeats) ...[
              const SizedBox(height: 12),
              SizedBox(
                width: double.infinity,
                child: FilledButton.icon(
                  key: Key('bookButton_${route.id}'),
                  onPressed: onBook,
                  icon: const Icon(Icons.confirmation_num),
                  label: Text(l10n.book),
                ),
              ),
            ],
          ],
        ),
      ),
    );
  }
}

class _InfoChip extends StatelessWidget {
  const _InfoChip({required this.icon, required this.label});
  final IconData icon;
  final String label;

  @override
  Widget build(BuildContext context) {
    return Row(children: [
      Icon(icon, size: 14, color: Colors.grey),
      const SizedBox(width: 4),
      Text(label, style: const TextStyle(fontSize: 13)),
      const SizedBox(width: 8),
    ]);
  }
}

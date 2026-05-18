import 'package:flutter/material.dart';
import 'package:bus_tickets/l10n/app_localizations.dart';
import 'package:provider/provider.dart';

import '../models/ticket.dart';
import '../providers/app_provider.dart';

/// Экран «Мои билеты» — список забронированных билетов.
///
/// Использует AnimatedList для плавного удаления элементов при отмене брони.
class TicketsScreen extends StatefulWidget {
  const TicketsScreen({super.key});

  @override
  State<TicketsScreen> createState() => _TicketsScreenState();
}

class _TicketsScreenState extends State<TicketsScreen> {
  final GlobalKey<AnimatedListState> _listKey = GlobalKey<AnimatedListState>();

  @override
  void initState() {
    super.initState();
    // Загружаем актуальный список билетов при открытии экрана.
    WidgetsBinding.instance.addPostFrameCallback((_) {
      context.read<AppProvider>().loadMyTickets();
    });
  }

  @override
  Widget build(BuildContext context) {
    final l10n = AppLocalizations.of(context);
    return Scaffold(
      appBar: AppBar(title: Text(l10n.myTickets)),
      body: Consumer<AppProvider>(
        builder: (context, ap, _) {
          final tickets = ap.tickets;

          if (tickets.isEmpty) {
            return Center(
              child: Column(
                mainAxisSize: MainAxisSize.min,
                children: [
                  Icon(
                    Icons.confirmation_num_outlined,
                    size: 64,
                    color: Theme.of(context).colorScheme.outline,
                  ),
                  const SizedBox(height: 16),
                  Text(l10n.noTickets),
                ],
              ),
            );
          }

          // AnimatedList с ключом для управления анимацией удаления.
          return AnimatedList(
            key: _listKey,
            initialItemCount: tickets.length,
            padding: const EdgeInsets.all(8),
            itemBuilder: (context, index, animation) {
              if (index >= tickets.length) return const SizedBox.shrink();
              return _buildTicketTile(context, tickets[index], animation, ap, l10n);
            },
          );
        },
      ),
    );
  }

  Widget _buildTicketTile(
    BuildContext context,
    Ticket ticket,
    Animation<double> animation,
    AppProvider ap,
    AppLocalizations l10n,
  ) {
    // Анимация появления/исчезновения элемента через SizeTransition.
    return SizeTransition(
      sizeFactor: animation,
      child: _TicketCard(
        ticket: ticket,
        onCancel: () => _onCancel(context, ticket, ap, l10n),
      ),
    );
  }

  Future<void> _onCancel(
    BuildContext context,
    Ticket ticket,
    AppProvider ap,
    AppLocalizations l10n,
  ) async {
    final confirmed = await showDialog<bool>(
      context: context,
      builder: (ctx) => AlertDialog(
        title: Text(l10n.cancel),
        content: Text('${l10n.ticketRoute(ticket.routeNumber)}\n${l10n.ticketFrom(ticket.cityName)}'),
        actions: [
          TextButton(
            onPressed: () => Navigator.pop(ctx, false),
            child: Text(l10n.back),
          ),
          FilledButton(
            style: FilledButton.styleFrom(
              backgroundColor: Theme.of(context).colorScheme.error,
            ),
            onPressed: () => Navigator.pop(ctx, true),
            child: Text(l10n.cancel),
          ),
        ],
      ),
    );

    if (confirmed == true && context.mounted) {
      // Находим индекс до удаления из провайдера.
      final index = ap.tickets.indexOf(ticket);
      await ap.cancelTicket(ticket);

      if (index >= 0 && context.mounted) {
        // Анимируем удаление элемента из списка.
        _listKey.currentState?.removeItem(
          index,
          (context, animation) => _buildTicketTile(context, ticket, animation, ap, l10n),
          duration: const Duration(milliseconds: 300),
        );
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(content: Text(l10n.bookingCancelled)),
        );
      }
    }
  }
}

/// Карточка одного билета.
class _TicketCard extends StatelessWidget {

  const _TicketCard({required this.ticket, required this.onCancel});
  final Ticket ticket;
  final VoidCallback onCancel;

  @override
  Widget build(BuildContext context) {
    final l10n = AppLocalizations.of(context);
    final cs = Theme.of(context).colorScheme;
    final date = DateTime.tryParse(ticket.bookedAt);
    final dateStr = date != null
        ? '${date.day.toString().padLeft(2, '0')}.${date.month.toString().padLeft(2, '0')}.${date.year}'
        : ticket.bookedAt;

    return Card(
      margin: const EdgeInsets.symmetric(horizontal: 8, vertical: 6),
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
                  l10n.routeNumber(ticket.routeNumber),
                  style: Theme.of(context)
                      .textTheme
                      .titleMedium
                      ?.copyWith(fontWeight: FontWeight.bold),
                ),
                const Spacer(),
                Text(
                  l10n.priceValue(ticket.price.toStringAsFixed(2)),
                  style: TextStyle(color: cs.primary, fontWeight: FontWeight.bold),
                ),
              ],
            ),
            const SizedBox(height: 8),
            Text(l10n.ticketFrom(ticket.cityName)),
            Text(l10n.ticketDate(dateStr)),
            Text('${l10n.departure}: ${ticket.departure}'),
            const SizedBox(height: 12),
            Align(
              alignment: Alignment.centerRight,
              child: OutlinedButton.icon(
                key: Key('cancelTicket_${ticket.id}'),
                onPressed: onCancel,
                icon: const Icon(Icons.close, size: 16),
                label: Text(l10n.cancel),
                style: OutlinedButton.styleFrom(
                  foregroundColor: cs.error,
                  side: BorderSide(color: cs.error),
                ),
              ),
            ),
          ],
        ),
      ),
    );
  }
}

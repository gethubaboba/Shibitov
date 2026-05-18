import 'package:flutter/material.dart';
import 'package:bus_tickets/l10n/app_localizations.dart';
import 'package:flutter_map/flutter_map.dart';
import 'package:latlong2/latlong.dart';
import 'package:provider/provider.dart';

import '../models/city.dart';
import '../providers/app_provider.dart';
import 'routes_screen.dart';
import 'tickets_screen.dart';

/// Главный экран: список городов + карта Гродненской области.
///
/// Города можно выбрать из списка ИЛИ нажав маркер на карте.
class CitiesScreen extends StatefulWidget {
  const CitiesScreen({super.key, required this.onLocaleChange});
  final void Function(Locale) onLocaleChange;

  @override
  State<CitiesScreen> createState() => _CitiesScreenState();
}

class _CitiesScreenState extends State<CitiesScreen>
    with SingleTickerProviderStateMixin {
  late TabController _tabController;
  // Гродно — центр карты по умолчанию.
  static const _mapCenter = LatLng(53.688, 23.826);

  @override
  void initState() {
    super.initState();
    _tabController = TabController(length: 2, vsync: this);
  }

  @override
  void dispose() {
    _tabController.dispose();
    super.dispose();
  }

  void _onCityTap(BuildContext context, City city) {
    context.read<AppProvider>().selectCity(city);
    Navigator.push(
      context,
      PageRouteBuilder(
        // Анимация перехода: слайд снизу вверх.
        pageBuilder: (_, animation, __) => RoutesScreen(city: city),
        transitionsBuilder: (_, animation, __, child) {
          final tween = Tween(
            begin: const Offset(0, 1),
            end: Offset.zero,
          ).chain(CurveTween(curve: Curves.easeInOut));
          return SlideTransition(position: animation.drive(tween), child: child);
        },
        transitionDuration: const Duration(milliseconds: 350),
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
    final l10n = AppLocalizations.of(context);
    return Scaffold(
      appBar: AppBar(
        title: Text(l10n.appTitle),
        bottom: TabBar(
          controller: _tabController,
          tabs: [
            Tab(icon: const Icon(Icons.list), text: l10n.listView),
            Tab(icon: const Icon(Icons.map), text: l10n.mapView),
          ],
        ),
        actions: [
          // Переход к забронированным билетам.
          IconButton(
            key: const Key('myTicketsButton'),
            icon: const Icon(Icons.confirmation_num_outlined),
            tooltip: l10n.myTickets,
            onPressed: () => Navigator.push(
              context,
              MaterialPageRoute(builder: (_) => const TicketsScreen()),
            ),
          ),
          // Смена языка.
          PopupMenuButton<Locale>(
            icon: const Icon(Icons.language),
            onSelected: widget.onLocaleChange,
            itemBuilder: (_) => const [
              PopupMenuItem(value: Locale('ru'), child: Text('Русский')),
              PopupMenuItem(value: Locale('en'), child: Text('English')),
              PopupMenuItem(value: Locale('be'), child: Text('Беларуская')),
            ],
          ),
        ],
      ),
      body: Consumer<AppProvider>(
        builder: (context, ap, _) {
          if (ap.citiesState == LoadingState.loading) {
            return const Center(child: CircularProgressIndicator());
          }
          if (ap.citiesState == LoadingState.error) {
            return Center(child: Text(l10n.errorOccurred(ap.errorMessage)));
          }

          return TabBarView(
            controller: _tabController,
            children: [
              // ── Вкладка: список городов ────────────────────────────────
              _CityListView(cities: ap.cities, onTap: _onCityTap),

              // ── Вкладка: карта ────────────────────────────────────────
              _CityMapView(cities: ap.cities, onTap: _onCityTap),
            ],
          );
        },
      ),
    );
  }
}

/// Список городов с анимированным появлением.
class _CityListView extends StatelessWidget {

  const _CityListView({required this.cities, required this.onTap});
  final List<City> cities;
  final void Function(BuildContext, City) onTap;

  @override
  Widget build(BuildContext context) {
    return ListView.builder(
      padding: const EdgeInsets.all(8),
      itemCount: cities.length,
      itemBuilder: (context, index) {
        final city = cities[index];
        return TweenAnimationBuilder<double>(
          // Каждый элемент появляется с задержкой — эффект «водопада».
          tween: Tween(begin: 0.0, end: 1.0),
          duration: Duration(milliseconds: 200 + index * 80),
          curve: Curves.easeOut,
          builder: (_, value, child) => Opacity(
            opacity: value,
            child: Transform.translate(
              offset: Offset(0, 20 * (1 - value)),
              child: child,
            ),
          ),
          child: Card(
            child: ListTile(
              key: Key('city_${city.name}'),
              leading: const Icon(Icons.location_city, color: Color(0xFF1565C0)),
              title: Text(city.nameRu, style: const TextStyle(fontWeight: FontWeight.w600)),
              trailing: const Icon(Icons.chevron_right),
              onTap: () => onTap(context, city),
            ),
          ),
        );
      },
    );
  }
}

/// Карта Гродненской области с маркерами городов.
class _CityMapView extends StatelessWidget {

  const _CityMapView({required this.cities, required this.onTap});
  final List<City> cities;
  final void Function(BuildContext, City) onTap;

  @override
  Widget build(BuildContext context) {
    return FlutterMap(
      options: const MapOptions(
        initialCenter: LatLng(53.5, 24.5), // центр Гродненской области
        initialZoom: 8.5,
      ),
      children: [
        // Тайлы OpenStreetMap — бесплатно, без API-ключа.
        TileLayer(
          urlTemplate: 'https://tile.openstreetmap.org/{z}/{x}/{y}.png',
          userAgentPackageName: 'com.petrakov.bus_tickets',
        ),
        // Маркеры городов.
        MarkerLayer(
          markers: cities.map((city) {
            return Marker(
              point: LatLng(city.latitude, city.longitude),
              width: 120,
              height: 60,
              child: GestureDetector(
                onTap: () => onTap(context, city),
                child: Column(
                  mainAxisSize: MainAxisSize.min,
                  children: [
                    const Icon(Icons.location_pin, color: Color(0xFF1565C0), size: 32),
                    Container(
                      padding: const EdgeInsets.symmetric(horizontal: 4, vertical: 2),
                      decoration: BoxDecoration(
                        color: Colors.white,
                        borderRadius: BorderRadius.circular(4),
                        boxShadow: const [BoxShadow(blurRadius: 2, color: Colors.black26)],
                      ),
                      child: Text(
                        city.nameRu,
                        style: const TextStyle(fontSize: 11, fontWeight: FontWeight.bold),
                      ),
                    ),
                  ],
                ),
              ),
            );
          }).toList(),
        ),
      ],
    );
  }
}

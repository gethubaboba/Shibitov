import 'package:flutter/material.dart';
import 'package:flutter_localizations/flutter_localizations.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:provider/provider.dart';

import 'package:bus_tickets/models/city.dart';
import 'package:bus_tickets/models/bus_route.dart';
import 'package:bus_tickets/models/ticket.dart';
import 'package:bus_tickets/models/weather_data.dart';
import 'package:bus_tickets/l10n/app_localizations.dart';
import 'package:bus_tickets/providers/app_provider.dart';
import 'package:bus_tickets/screens/cities_screen.dart';
import 'package:bus_tickets/services/database_service.dart';
import 'package:bus_tickets/services/weather_service.dart';
import 'package:bus_tickets/widgets/weather_card.dart';

/// Widget-тесты для экранов и виджетов bus_tickets.
///
/// Используем фиктивные реализации (fakes) вместо реальных сервисов,
/// чтобы не зависеть от SQLite и сети.
void main() {
  // ─── WeatherCard ─────────────────────────────────────────────────────────────
  group('WeatherCard', () {
    Widget buildCard({
      required LoadingState state,
      WeatherData? data,
      String error = '',
    }) {
      return MaterialApp(
        localizationsDelegates: const [
          AppLocalizations.delegate,
          GlobalMaterialLocalizations.delegate,
          GlobalWidgetsLocalizations.delegate,
          GlobalCupertinoLocalizations.delegate,
        ],
        supportedLocales: const [Locale('ru')],
        locale: const Locale('ru'),
        home: Scaffold(
          body: WeatherCard(
            state: state,
            data: data,
            error: error,
            cityName: 'Гродно',
          ),
        ),
      );
    }

    testWidgets('показывает индикатор при loading', (tester) async {
      await tester.pumpWidget(buildCard(state: LoadingState.loading));
      expect(find.byType(CircularProgressIndicator), findsOneWidget);
    });

    testWidgets('показывает ошибку при error', (tester) async {
      await tester.pumpWidget(buildCard(state: LoadingState.error, error: 'err'));
      await tester.pumpAndSettle();
      expect(find.byIcon(Icons.cloud_off), findsOneWidget);
    });

    testWidgets('показывает температуру при success', (tester) async {
      const data = WeatherData(
        cityName: 'Grodno',
        temperature: 20.0,
        description: 'ясно',
        humidity: 65,
        windSpeed: 3.0,
      );
      await tester.pumpWidget(buildCard(state: LoadingState.success, data: data));
      await tester.pumpAndSettle();
      // Проверяем, что виджет содержит строку с температурой.
      expect(find.textContaining('20.0'), findsOneWidget);
    });
  });

  // ─── CitiesScreen ─────────────────────────────────────────────────────────────
  group('CitiesScreen', () {
    late AppProvider fakeProvider;

    setUp(() {
      fakeProvider = _FakeAppProvider();
    });

    Widget buildApp() {
      return ChangeNotifierProvider<AppProvider>.value(
        value: fakeProvider,
        child: MaterialApp(
          localizationsDelegates: const [
            AppLocalizations.delegate,
            GlobalMaterialLocalizations.delegate,
            GlobalWidgetsLocalizations.delegate,
            GlobalCupertinoLocalizations.delegate,
          ],
          supportedLocales: const [Locale('ru')],
          locale: const Locale('ru'),
          home: CitiesScreen(onLocaleChange: (_) {}),
        ),
      );
    }

    testWidgets('отображает список городов', (tester) async {
      await tester.pumpWidget(buildApp());
      await tester.pumpAndSettle();

      expect(find.byKey(const Key('city_grodno')), findsOneWidget);
      expect(find.byKey(const Key('city_lida')), findsOneWidget);
    });

    testWidgets('кнопка "Мои билеты" присутствует', (tester) async {
      await tester.pumpWidget(buildApp());
      await tester.pumpAndSettle();

      expect(find.byKey(const Key('myTicketsButton')), findsOneWidget);
    });
  });
}

// ─── Фиктивный провайдер для widget-тестов ────────────────────────────────────

/// Fake AppProvider с предопределёнными данными, не использующий реальные сервисы.
class _FakeAppProvider extends AppProvider {
  _FakeAppProvider() : super(db: _FakeDb(), weather: _FakeWeather());

  @override
  List<City> get cities => [
        const City(id: 1, name: 'grodno', nameRu: 'Гродно', latitude: 53.6884, longitude: 23.8258),
        const City(id: 2, name: 'lida', nameRu: 'Лида', latitude: 53.8879, longitude: 25.2979),
      ];

  @override
  LoadingState get citiesState => LoadingState.success;

  @override
  List<BusRoute> get routes => [
        const BusRoute(
          id: 1, cityId: 1, number: '101',
          departure: '07:00', arrival: '09:30',
          price: 4.5, seatsAvailable: 5,
        ),
      ];

  @override
  LoadingState get routesState => LoadingState.success;

  @override
  List<Ticket> get tickets => [];

  @override
  WeatherData? get weatherData => null;

  @override
  LoadingState get weatherState => LoadingState.idle;

  @override
  Future<void> loadCities() async {}

  @override
  Future<void> selectCity(City city) async {}

  @override
  Future<void> loadMyTickets() async {}
}

class _FakeDb extends DatabaseService {
  _FakeDb() : super.forTesting();
}

class _FakeWeather extends WeatherService {}

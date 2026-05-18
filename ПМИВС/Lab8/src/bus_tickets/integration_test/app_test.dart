import 'package:flutter/material.dart';
import 'package:flutter_localizations/flutter_localizations.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:integration_test/integration_test.dart';
import 'package:provider/provider.dart';
import 'package:sqflite_common_ffi/sqflite_ffi.dart';

import 'package:bus_tickets/providers/app_provider.dart';
import 'package:bus_tickets/screens/cities_screen.dart';
import 'package:bus_tickets/services/database_service.dart';
import 'package:bus_tickets/services/weather_service.dart';
import 'package:bus_tickets/models/weather_data.dart';

/// Интеграционные тесты — сквозное тестирование на эмуляторе/устройстве.
///
/// Запуск: flutter test integration_test/app_test.dart
///
/// Тесты используют in-memory SQLite (через FFI) и фиктивный WeatherService,
/// поэтому не требуют реального интернета.
void main() {
  IntegrationTestWidgetsFlutterBinding.ensureInitialized();

  setUpAll(() {
    sqfliteFfiInit();
    databaseFactory = databaseFactoryFfi;
  });

  late DatabaseService testDb;
  late AppProvider testProvider;

  setUp(() async {
    testDb = DatabaseService.forTesting();
    await testDb.initForTesting();
    testProvider = AppProvider(db: testDb, weather: _MockWeatherService());
    await testProvider.loadCities();
  });

  tearDown(() async {
    await testDb.closeForTesting();
  });

  Widget buildApp() {
    return ChangeNotifierProvider<AppProvider>.value(
      value: testProvider,
      child: MaterialApp(
        localizationsDelegates: const [
          GlobalMaterialLocalizations.delegate,
          GlobalWidgetsLocalizations.delegate,
        ],
        supportedLocales: const [Locale('ru')],
        locale: const Locale('ru'),
        home: CitiesScreen(onLocaleChange: (_) {}),
      ),
    );
  }

  group('Интеграционный тест — полный сценарий бронирования', () {
    testWidgets('Сценарий: выбрать город → маршруты загружены', (tester) async {
      await tester.pumpWidget(buildApp());
      await tester.pumpAndSettle();

      // 1. Экран городов загрузился, список видим.
      expect(find.byKey(const Key('city_grodno')), findsOneWidget);

      // 2. Нажимаем на город.
      await tester.tap(find.byKey(const Key('city_grodno')));
      await tester.pumpAndSettle();

      // Маршруты должны загрузиться.
      expect(testProvider.routesState, LoadingState.success);
      expect(testProvider.routes.isNotEmpty, true);
    });

    testWidgets('Сценарий: бронирование и отмена билета', (tester) async {
      await tester.pumpWidget(buildApp());
      await tester.pumpAndSettle();

      // Выбираем город.
      final cities = testProvider.cities;
      expect(cities.isNotEmpty, true);

      final city = cities.firstWhere((c) => c.name == 'grodno');
      await testProvider.selectCity(city);
      await tester.pumpAndSettle();

      // Находим маршрут с местами.
      final routeWithSeats = testProvider.routes.firstWhere((r) => r.hasSeats);
      final seatsBefore = routeWithSeats.seatsAvailable;

      // Бронируем.
      final success = await testProvider.bookTicket(routeWithSeats);
      expect(success, true);

      // Проверяем что место уменьшилось.
      final updatedRoute = testProvider.routes.firstWhere((r) => r.id == routeWithSeats.id);
      expect(updatedRoute.seatsAvailable, seatsBefore - 1);

      // Проверяем что билет в списке.
      expect(testProvider.tickets.length, 1);
      expect(testProvider.tickets.first.cityName, city.nameRu);

      // Отменяем бронь.
      final ticket = testProvider.tickets.first;
      await testProvider.cancelTicket(ticket);

      expect(testProvider.tickets.isEmpty, true);

      // Место вернулось.
      final restoredRoute = testProvider.routes.firstWhere((r) => r.id == routeWithSeats.id);
      expect(restoredRoute.seatsAvailable, seatsBefore);
    });

    testWidgets('Сценарий: бронирование маршрута без мест — ошибка', (tester) async {
      await tester.pumpWidget(buildApp());
      await tester.pumpAndSettle();

      final cities = testProvider.cities;
      final city = cities.first;
      await testProvider.selectCity(city);
      await tester.pumpAndSettle();

      // Ищем маршрут без мест.
      final noSeatRoutes = testProvider.routes.where((r) => !r.hasSeats).toList();
      if (noSeatRoutes.isEmpty) {
        // Пропускаем тест если все маршруты с местами.
        return;
      }

      final fullRoute = noSeatRoutes.first;
      final result = await testProvider.bookTicket(fullRoute);
      expect(result, false); // бронирование должно провалиться
      expect(testProvider.tickets.isEmpty, true);
    });
  });
}

/// Фиктивный WeatherService — не делает реальных HTTP-запросов.
class _MockWeatherService extends WeatherService {
  @override
  Future<WeatherData> getWeatherByCoords(double lat, double lon) async {
    // Имитируем задержку сети.
    await Future<void>.delayed(const Duration(milliseconds: 10));
    throw WeatherException('Test: no real network available');
  }
}

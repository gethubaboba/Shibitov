import 'package:flutter_test/flutter_test.dart';
import 'package:sqflite_common_ffi/sqflite_ffi.dart';

import 'package:bus_tickets/models/city.dart';
import 'package:bus_tickets/models/bus_route.dart';
import 'package:bus_tickets/models/weather_data.dart';
import 'package:bus_tickets/services/database_service.dart';

/// Unit-тесты для моделей и DatabaseService.
///
/// Используем sqflite_common_ffi для запуска тестов на десктопе (Windows/Linux)
/// без реального Android-устройства.
///
/// Добавить зависимость: sqflite_common_ffi: ^2.3.3 в dev_dependencies.
void main() {
  // Инициализируем FFI-реализацию SQLite перед тестами.
  setUpAll(() {
    sqfliteFfiInit();
    databaseFactory = databaseFactoryFfi;
  });

  // ─── Тесты модели City ──────────────────────────────────────────────────────
  group('City — сериализация', () {
    test('toMap() содержит все поля', () {
      const city = City(
        id: 1,
        name: 'grodno',
        nameRu: 'Гродно',
        latitude: 53.6884,
        longitude: 23.8258,
      );
      final map = city.toMap();
      expect(map['id'], 1);
      expect(map['name'], 'grodno');
      expect(map['name_ru'], 'Гродно');
      expect(map['latitude'], 53.6884);
      expect(map['longitude'], 23.8258);
    });

    test('fromMap() восстанавливает объект', () {
      final map = {
        'id': 2,
        'name': 'lida',
        'name_ru': 'Лида',
        'latitude': 53.8879,
        'longitude': 25.2979,
      };
      final city = City.fromMap(map);
      expect(city.id, 2);
      expect(city.name, 'lida');
      expect(city.nameRu, 'Лида');
    });
  });

  // ─── Тесты модели BusRoute ──────────────────────────────────────────────────
  group('BusRoute — логика', () {
    test('hasSeats = true когда seatsAvailable > 0', () {
      const route = BusRoute(
        id: 1, cityId: 1, number: '101',
        departure: '07:00', arrival: '09:30',
        price: 4.5, seatsAvailable: 5,
      );
      expect(route.hasSeats, true);
    });

    test('hasSeats = false когда seatsAvailable == 0', () {
      const route = BusRoute(
        id: 2, cityId: 1, number: '101',
        departure: '11:00', arrival: '13:30',
        price: 4.5, seatsAvailable: 0,
      );
      expect(route.hasSeats, false);
    });

    test('copyWith обновляет seatsAvailable', () {
      const route = BusRoute(
        id: 1, cityId: 1, number: '101',
        departure: '07:00', arrival: '09:30',
        price: 4.5, seatsAvailable: 5,
      );
      final updated = route.copyWith(seatsAvailable: 4);
      expect(updated.seatsAvailable, 4);
      expect(updated.number, '101'); // остальные поля не изменились
    });

    test('toMap/fromMap round-trip', () {
      const route = BusRoute(
        id: 1, cityId: 2, number: '203',
        departure: '15:00', arrival: '17:20',
        price: 5.20, seatsAvailable: 7,
      );
      final restored = BusRoute.fromMap(route.toMap());
      expect(restored.number, route.number);
      expect(restored.price, route.price);
      expect(restored.seatsAvailable, route.seatsAvailable);
    });
  });

  // ─── Тесты модели WeatherData ───────────────────────────────────────────────
  group('WeatherData — разбор JSON', () {
    test('fromJson корректно парсит ответ OpenWeatherMap', () {
      final json = {
        'name': 'Grodno',
        'main': {'temp': 293.15, 'humidity': 75}, // 20.0°C
        'weather': [{'description': 'ясно'}],
        'wind': {'speed': 3.5},
      };
      final data = WeatherData.fromJson(json);
      expect(data.cityName, 'Grodno');
      expect(data.temperature.toStringAsFixed(1), '20.0');
      expect(data.humidity, 75);
      expect(data.windSpeed, 3.5);
      expect(data.description, 'ясно');
    });

    test('fromJson не падает при отсутствии полей', () {
      // Пустой JSON не должен выбросить исключение.
      final data = WeatherData.fromJson({});
      expect(data.cityName, '');
      expect(data.humidity, 0);
    });
  });

  // ─── Тесты DatabaseService (in-memory FFI) ─────────────────────────────────
  group('DatabaseService — CRUD', () {
    late DatabaseService db;

    setUp(() async {
      // Каждый тест работает с чистой in-memory БД.
      db = DatabaseService.forTesting();
      await db.initForTesting();
    });

    tearDown(() async {
      await db.closeForTesting();
    });

    test('getCities() возвращает 6 городов', () async {
      final cities = await db.getCities();
      expect(cities.length, 6);
      expect(cities.any((c) => c.name == 'grodno'), true);
    });

    test('getRoutesByCity() возвращает маршруты для города', () async {
      final cities = await db.getCities();
      final grodno = cities.firstWhere((c) => c.name == 'grodno');
      final routes = await db.getRoutesByCity(grodno.id!);
      expect(routes.isNotEmpty, true);
      expect(routes.every((r) => r.cityId == grodno.id), true);
    });

    test('bookTicket() уменьшает количество мест', () async {
      final cities = await db.getCities();
      final city = cities.first;
      final routes = await db.getRoutesByCity(city.id!);
      final routeWithSeats = routes.firstWhere((r) => r.hasSeats);
      final seatsBefore = routeWithSeats.seatsAvailable;

      await db.bookTicket(route: routeWithSeats, cityName: city.nameRu);

      final updatedRoutes = await db.getRoutesByCity(city.id!);
      final updated = updatedRoutes.firstWhere((r) => r.id == routeWithSeats.id);
      expect(updated.seatsAvailable, seatsBefore - 1);
    });

    test('bookTicket() создаёт запись в таблице tickets', () async {
      final cities = await db.getCities();
      final city = cities.first;
      final routes = await db.getRoutesByCity(city.id!);
      final route = routes.firstWhere((r) => r.hasSeats);

      await db.bookTicket(route: route, cityName: city.nameRu);
      final tickets = await db.getMyTickets();

      expect(tickets.length, 1);
      expect(tickets.first.routeId, route.id);
      expect(tickets.first.cityName, city.nameRu);
    });

    test('bookTicket() выбрасывает исключение если мест нет', () async {
      final cities = await db.getCities();
      final city = cities.first;
      final routes = await db.getRoutesByCity(city.id!);
      final fullRoute = routes.firstWhere((r) => !r.hasSeats);

      expect(
        () => db.bookTicket(route: fullRoute, cityName: city.nameRu),
        throwsException,
      );
    });

    test('cancelTicket() удаляет билет и возвращает место', () async {
      final cities = await db.getCities();
      final city = cities.first;
      final routes = await db.getRoutesByCity(city.id!);
      final route = routes.firstWhere((r) => r.hasSeats);
      final seatsBefore = route.seatsAvailable;

      final ticket = await db.bookTicket(route: route, cityName: city.nameRu);
      await db.cancelTicket(ticket);

      final tickets = await db.getMyTickets();
      expect(tickets.isEmpty, true);

      final updatedRoutes = await db.getRoutesByCity(city.id!);
      final updated = updatedRoutes.firstWhere((r) => r.id == route.id);
      expect(updated.seatsAvailable, seatsBefore); // место вернулось
    });
  });
}

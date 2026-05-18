import 'package:sqflite/sqflite.dart';
// ignore: depend_on_referenced_packages
import 'package:sqflite_common_ffi/sqflite_ffi.dart' show sqfliteFfiInit, databaseFactoryFfi;
import 'package:path/path.dart';

import '../models/city.dart';
import '../models/bus_route.dart';
import '../models/ticket.dart';

/// DatabaseService — единственная точка работы с SQLite.
///
/// Паттерн Singleton: один экземпляр на всё приложение.
/// Инициализирует БД при первом обращении, заполняет тестовыми данными.
///
/// Для тестов использовать [DatabaseService.forTesting()] — создаёт
/// отдельный экземпляр с in-memory FFI базой.
class DatabaseService {
  factory DatabaseService() => _instance;
  DatabaseService._internal();

  /// Конструктор для тестов: создаёт независимый экземпляр (не singleton).
  DatabaseService.forTesting();
  static final DatabaseService _instance = DatabaseService._internal();


  Database? _db;

  /// Получить (или открыть) базу данных.
  Future<Database> get database async {
    if (_db != null) return _db!;
    _db = await _initDatabase();
    return _db!;
  }

  Future<Database> _initDatabase() async {
    final dbPath = await getDatabasesPath();
    final path = join(dbPath, 'bus_tickets.db');

    return openDatabase(
      path,
      version: 1,
      onCreate: _onCreate,
    );
  }

  /// Создание таблиц и наполнение начальными данными.
  Future<void> _onCreate(Database db, int version) async {
    // Таблица городов.
    await db.execute('''
      CREATE TABLE cities (
        id        INTEGER PRIMARY KEY AUTOINCREMENT,
        name      TEXT NOT NULL,
        name_ru   TEXT NOT NULL,
        latitude  REAL NOT NULL,
        longitude REAL NOT NULL
      )
    ''');

    // Таблица маршрутов.
    await db.execute('''
      CREATE TABLE routes (
        id               INTEGER PRIMARY KEY AUTOINCREMENT,
        city_id          INTEGER NOT NULL,
        number           TEXT    NOT NULL,
        departure        TEXT    NOT NULL,
        arrival          TEXT    NOT NULL,
        price            REAL    NOT NULL,
        seats_available  INTEGER NOT NULL,
        FOREIGN KEY (city_id) REFERENCES cities (id)
      )
    ''');

    // Таблица забронированных билетов.
    await db.execute('''
      CREATE TABLE tickets (
        id           INTEGER PRIMARY KEY AUTOINCREMENT,
        route_id     INTEGER NOT NULL,
        city_name    TEXT    NOT NULL,
        route_number TEXT    NOT NULL,
        departure    TEXT    NOT NULL,
        price        REAL    NOT NULL,
        booked_at    TEXT    NOT NULL,
        FOREIGN KEY (route_id) REFERENCES routes (id)
      )
    ''');

    // Начальные данные — города Гродненской области.
    await _seedData(db);
  }

  /// Заполнение БД тестовыми городами и маршрутами.
  Future<void> _seedData(Database db) async {
    final cities = [
      {'name': 'grodno',     'name_ru': 'Гродно',     'latitude': 53.6884, 'longitude': 23.8258},
      {'name': 'lida',       'name_ru': 'Лида',       'latitude': 53.8879, 'longitude': 25.2979},
      {'name': 'volkovysk',  'name_ru': 'Волковыск',  'latitude': 53.1514, 'longitude': 24.4508},
      {'name': 'slonim',     'name_ru': 'Слоним',     'latitude': 53.0940, 'longitude': 25.3154},
      {'name': 'novogrudok', 'name_ru': 'Новогрудок', 'latitude': 53.6000, 'longitude': 25.8222},
      {'name': 'mosty',      'name_ru': 'Мосты',      'latitude': 53.4072, 'longitude': 24.5420},
    ];

    for (final city in cities) {
      await db.insert('cities', city);
    }

    // Маршруты: 3 маршрута на каждый город.
    final routes = [
      // Гродно (id=1)
      {'city_id': 1, 'number': '101', 'departure': '07:00', 'arrival': '09:30', 'price': 4.50,  'seats_available': 12},
      {'city_id': 1, 'number': '101', 'departure': '11:00', 'arrival': '13:30', 'price': 4.50,  'seats_available': 0},
      {'city_id': 1, 'number': '203', 'departure': '15:00', 'arrival': '17:20', 'price': 5.20,  'seats_available': 7},
      // Лида (id=2)
      {'city_id': 2, 'number': '142', 'departure': '08:15', 'arrival': '10:45', 'price': 6.80,  'seats_available': 5},
      {'city_id': 2, 'number': '142', 'departure': '13:00', 'arrival': '15:30', 'price': 6.80,  'seats_available': 18},
      {'city_id': 2, 'number': '308', 'departure': '16:30', 'arrival': '19:00', 'price': 7.10,  'seats_available': 3},
      // Волковыск (id=3)
      {'city_id': 3, 'number': '217', 'departure': '06:45', 'arrival': '08:15', 'price': 3.90,  'seats_available': 22},
      {'city_id': 3, 'number': '217', 'departure': '12:30', 'arrival': '14:00', 'price': 3.90,  'seats_available': 0},
      {'city_id': 3, 'number': '415', 'departure': '17:00', 'arrival': '18:30', 'price': 4.20,  'seats_available': 9},
      // Слоним (id=4)
      {'city_id': 4, 'number': '321', 'departure': '09:00', 'arrival': '11:10', 'price': 5.60,  'seats_available': 14},
      {'city_id': 4, 'number': '321', 'departure': '14:00', 'arrival': '16:10', 'price': 5.60,  'seats_available': 6},
      {'city_id': 4, 'number': '502', 'departure': '18:00', 'arrival': '20:10', 'price': 5.80,  'seats_available': 1},
      // Новогрудок (id=5)
      {'city_id': 5, 'number': '178', 'departure': '07:30', 'arrival': '10:00', 'price': 7.40,  'seats_available': 8},
      {'city_id': 5, 'number': '178', 'departure': '13:30', 'arrival': '16:00', 'price': 7.40,  'seats_available': 20},
      {'city_id': 5, 'number': '256', 'departure': '16:00', 'arrival': '18:30', 'price': 7.80,  'seats_available': 0},
      // Мосты (id=6)
      {'city_id': 6, 'number': '433', 'departure': '08:00', 'arrival': '09:20', 'price': 2.80,  'seats_available': 30},
      {'city_id': 6, 'number': '433', 'departure': '13:45', 'arrival': '15:05', 'price': 2.80,  'seats_available': 15},
      {'city_id': 6, 'number': '511', 'departure': '17:30', 'arrival': '18:50', 'price': 3.00,  'seats_available': 4},
    ];

    for (final route in routes) {
      await db.insert('routes', route);
    }
  }

  // ─── CRUD: Города ─────────────────────────────────────────────────────────

  Future<List<City>> getCities() async {
    final db = await database;
    final maps = await db.query('cities', orderBy: 'name_ru ASC');
    return maps.map(City.fromMap).toList();
  }

  Future<City?> getCityById(int id) async {
    final db = await database;
    final maps = await db.query('cities', where: 'id = ?', whereArgs: [id]);
    return maps.isEmpty ? null : City.fromMap(maps.first);
  }

  // ─── CRUD: Маршруты ───────────────────────────────────────────────────────

  /// Возвращает все маршруты для указанного города.
  Future<List<BusRoute>> getRoutesByCity(int cityId) async {
    final db = await database;
    final maps = await db.query(
      'routes',
      where: 'city_id = ?',
      whereArgs: [cityId],
      orderBy: 'departure ASC',
    );
    return maps.map(BusRoute.fromMap).toList();
  }

  Future<BusRoute?> getRouteById(int id) async {
    final db = await database;
    final maps = await db.query('routes', where: 'id = ?', whereArgs: [id]);
    return maps.isEmpty ? null : BusRoute.fromMap(maps.first);
  }

  // ─── CRUD: Билеты ─────────────────────────────────────────────────────────

  /// Бронирует билет на указанный маршрут.
  ///
  /// Уменьшает количество свободных мест на 1.
  /// Если мест нет — выбрасывает исключение.
  Future<Ticket> bookTicket({
    required BusRoute route,
    required String cityName,
  }) async {
    if (!route.hasSeats) {
      throw Exception('No seats available on route ${route.number}');
    }

    final db = await database;

    // Транзакция гарантирует атомарность: либо оба запроса выполнятся, либо ни один.
    return db.transaction((txn) async {
      // Уменьшаем количество мест.
      await txn.update(
        'routes',
        {'seats_available': route.seatsAvailable - 1},
        where: 'id = ?',
        whereArgs: [route.id],
      );

      final ticket = Ticket(
        routeId: route.id!,
        cityName: cityName,
        routeNumber: route.number,
        departure: route.departure,
        price: route.price,
        bookedAt: DateTime.now().toIso8601String(),
      );

      final id = await txn.insert('tickets', ticket.toMap());
      return ticket.copyWith(id: id);
    });
  }

  Future<List<Ticket>> getMyTickets() async {
    final db = await database;
    final maps = await db.query('tickets', orderBy: 'booked_at DESC');
    return maps.map(Ticket.fromMap).toList();
  }

  /// Отменяет бронь и возвращает место обратно в маршрут.
  Future<void> cancelTicket(Ticket ticket) async {
    final db = await database;
    await db.transaction((txn) async {
      await txn.delete('tickets', where: 'id = ?', whereArgs: [ticket.id]);
      // Возвращаем место.
      await txn.rawUpdate(
        'UPDATE routes SET seats_available = seats_available + 1 WHERE id = ?',
        [ticket.routeId],
      );
    });
  }

  /// Для тестов: сбросить базу данных (закрыть и удалить файл).
  Future<void> deleteDatabase() async {
    if (_db != null) {
      await _db!.close();
      _db = null;
    }
    final dbPath = await getDatabasesPath();
    await databaseFactory.deleteDatabase(join(dbPath, 'bus_tickets.db'));
  }

  // ─── Методы только для тестов ────────────────────────────────────────────

  /// Открывает in-memory БД для изолированного тестирования.
  /// Вызывать только в экземплярах, созданных через [DatabaseService.forTesting()].
  Future<void> initForTesting() async {
    _db = await databaseFactoryFfi.openDatabase(
      inMemoryDatabasePath,
      options: OpenDatabaseOptions(version: 1, onCreate: _onCreate),
    );
  }

  /// Закрывает тестовую БД.
  Future<void> closeForTesting() async {
    await _db?.close();
    _db = null;
  }
}

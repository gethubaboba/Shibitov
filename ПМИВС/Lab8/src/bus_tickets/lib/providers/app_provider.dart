import 'package:flutter/foundation.dart';

import '../models/city.dart';
import '../models/bus_route.dart';
import '../models/ticket.dart';
import '../models/weather_data.dart';
import '../services/database_service.dart';
import '../services/weather_service.dart';

/// Состояние загрузки для асинхронных операций.
enum LoadingState { idle, loading, success, error }

/// AppProvider — центральный провайдер состояния приложения.
///
/// Управляет:
/// - списком городов (из SQLite)
/// - маршрутами выбранного города
/// - забронированными билетами
/// - данными погоды для выбранного города
class AppProvider extends ChangeNotifier {

  AppProvider({
    DatabaseService? db,
    WeatherService? weather,
  })  : _db = db ?? DatabaseService(),
        _weather = weather ?? WeatherService() {
    loadCities();
  }
  final DatabaseService _db;
  final WeatherService _weather;

  // ─── Города ───────────────────────────────────────────────────────────────
  List<City> _cities = [];
  List<City> get cities => _cities;

  City? _selectedCity;
  City? get selectedCity => _selectedCity;

  LoadingState _citiesState = LoadingState.idle;
  LoadingState get citiesState => _citiesState;

  // ─── Маршруты ─────────────────────────────────────────────────────────────
  List<BusRoute> _routes = [];
  List<BusRoute> get routes => _routes;

  LoadingState _routesState = LoadingState.idle;
  LoadingState get routesState => _routesState;

  // ─── Билеты ───────────────────────────────────────────────────────────────
  List<Ticket> _tickets = [];
  List<Ticket> get tickets => _tickets;

  // ─── Погода ───────────────────────────────────────────────────────────────
  WeatherData? _weatherData;
  WeatherData? get weatherData => _weatherData;

  LoadingState _weatherState = LoadingState.idle;
  LoadingState get weatherState => _weatherState;
  String _weatherError = '';
  String get weatherError => _weatherError;

  // ─── Ошибки ───────────────────────────────────────────────────────────────
  String _errorMessage = '';
  String get errorMessage => _errorMessage;

  // ─── Методы ───────────────────────────────────────────────────────────────

  /// Загружает список городов из БД при первом запуске.
  Future<void> loadCities() async {
    _citiesState = LoadingState.loading;
    notifyListeners();
    try {
      _cities = await _db.getCities();
      _citiesState = LoadingState.success;
    } catch (e, st) {
      _citiesState = LoadingState.error;
      _errorMessage = e.toString();
      // ignore: avoid_print
      debugPrint('[AppProvider] loadCities error: $e\n$st');
    }
    notifyListeners();
  }

  /// Выбирает город, загружает маршруты и погоду для него.
  Future<void> selectCity(City city) async {
    _selectedCity = city;
    _routes = [];
    _weatherData = null;
    notifyListeners();

    await Future.wait([
      _loadRoutes(city.id!),
      _loadWeather(city.latitude, city.longitude),
    ]);
  }

  Future<void> _loadRoutes(int cityId) async {
    _routesState = LoadingState.loading;
    notifyListeners();
    try {
      _routes = await _db.getRoutesByCity(cityId);
      _routesState = LoadingState.success;
    } catch (e, st) {
      _routesState = LoadingState.error;
      _errorMessage = e.toString();
      debugPrint('[AppProvider] _loadRoutes error: $e\n$st');
    }
    notifyListeners();
  }

  Future<void> _loadWeather(double lat, double lon) async {
    _weatherState = LoadingState.loading;
    _weatherError = '';
    notifyListeners();
    try {
      _weatherData = await _weather.getWeatherByCoords(lat, lon);
      _weatherState = LoadingState.success;
    } catch (e) {
      _weatherState = LoadingState.error;
      _weatherError = e.toString();
      debugPrint('[AppProvider] _loadWeather error: $e');
    }
    notifyListeners();
  }

  /// Бронирует билет. Обновляет список маршрутов (уменьшает места).
  Future<bool> bookTicket(BusRoute route) async {
    if (_selectedCity == null) return false;
    try {
      final ticket = await _db.bookTicket(
        route: route,
        cityName: _selectedCity!.nameRu,
      );
      _tickets = [ticket, ..._tickets];
      // Обновляем количество мест в локальном списке без повторного запроса к БД.
      _routes = _routes.map((r) {
        if (r.id == route.id) {
          return r.copyWith(seatsAvailable: r.seatsAvailable - 1);
        }
        return r;
      }).toList();
      notifyListeners();
      return true;
    } catch (e, st) {
      _errorMessage = e.toString();
      debugPrint('[AppProvider] bookTicket error: $e\n$st');
      notifyListeners();
      return false;
    }
  }

  /// Отменяет бронь.
  Future<void> cancelTicket(Ticket ticket) async {
    try {
      await _db.cancelTicket(ticket);
      _tickets = _tickets.where((t) => t.id != ticket.id).toList();
      // Если этот маршрут сейчас отображается — возвращаем место.
      if (_selectedCity != null) {
        _routes = _routes.map((r) {
          if (r.id == ticket.routeId) {
            return r.copyWith(seatsAvailable: r.seatsAvailable + 1);
          }
          return r;
        }).toList();
      }
      notifyListeners();
    } catch (e, st) {
      _errorMessage = e.toString();
      debugPrint('[AppProvider] cancelTicket error: $e\n$st');
      notifyListeners();
    }
  }

  /// Загружает все мои билеты из БД.
  Future<void> loadMyTickets() async {
    try {
      _tickets = await _db.getMyTickets();
      notifyListeners();
    } catch (e, st) {
      debugPrint('[AppProvider] loadMyTickets error: $e\n$st');
    }
  }
}

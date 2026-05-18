import 'dart:convert';
import 'package:http/http.dart' as http;

import '../models/weather_data.dart';

/// Сервис для получения прогноза погоды через OpenWeatherMap API.
///
/// Документация API: https://openweathermap.org/api/one-call-3
///
/// Важно: замените [_apiKey] на свой ключ из openweathermap.org (бесплатный план).
class WeatherService {

  WeatherService({http.Client? client}) : _client = client ?? http.Client();
  // TODO: замените на свой API-ключ с openweathermap.org
  // Бесплатный ключ получить на: https://home.openweathermap.org/users/sign_up
  static const String _apiKey = 'YOUR_OPENWEATHERMAP_API_KEY';
  static const String _baseUrl = 'https://api.openweathermap.org/data/2.5';

  final http.Client _client;

  /// Получить погоду по координатам (широта, долгота).
  ///
  /// lang=ru — ответ на русском языке.
  /// units=metric — температура в °C.
  ///
  /// Выбрасывает [WeatherException] при ошибке сети или API.
  Future<WeatherData> getWeatherByCoords(double lat, double lon) async {
    final uri = Uri.parse(
      '$_baseUrl/weather?lat=$lat&lon=$lon&appid=$_apiKey&units=metric&lang=ru',
    );

    try {
      final response = await _client.get(uri).timeout(
        const Duration(seconds: 10),
      );

      if (response.statusCode == 200) {
        final json = jsonDecode(response.body) as Map<String, dynamic>;
        return WeatherData.fromJson(json);
      } else if (response.statusCode == 401) {
        throw WeatherException('Invalid API key. Please check your OpenWeatherMap API key.');
      } else {
        throw WeatherException('API error: ${response.statusCode}');
      }
    } on WeatherException {
      rethrow;
    } catch (e) {
      // Логируем в консоль согласно требованию методички.
      // ignore: avoid_print
      print('[WeatherService] Error: $e');
      throw WeatherException('Network error: $e');
    }
  }
}

/// Кастомное исключение для ошибок погодного сервиса.
class WeatherException implements Exception {
  WeatherException(this.message);
  final String message;

  @override
  String toString() => 'WeatherException: $message';
}

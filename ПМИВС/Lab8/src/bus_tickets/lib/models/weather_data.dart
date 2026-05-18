/// Простая модель данных погоды, полученных от OpenWeatherMap API.
class WeatherData {    // м/с

  const WeatherData({
    required this.cityName,
    required this.temperature,
    required this.description,
    required this.humidity,
    required this.windSpeed,
  });

  /// Разбирает ответ JSON от OpenWeatherMap /weather endpoint.
  factory WeatherData.fromJson(Map<String, dynamic> json) {
    return WeatherData(
      cityName: json['name'] as String? ?? '',
      temperature: ((json['main']?['temp'] ?? 0) as num).toDouble() - 273.15,
      description: (json['weather']?[0]?['description'] as String?) ?? '',
      humidity: (json['main']?['humidity'] as int?) ?? 0,
      windSpeed: ((json['wind']?['speed'] ?? 0) as num).toDouble(),
    );
  }
  final String cityName;
  final double temperature;  // °C
  final String description;  // «ясно», «облачно» и т.д.
  final int humidity;        // %
  final double windSpeed;
}

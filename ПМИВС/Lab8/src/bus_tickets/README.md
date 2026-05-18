# Автобус Гродненской области — Flutter (Вариант 15, Задание 3)

Мобильное приложение «Бронирование билетов на маршрутный автобус» Гродненской области.

## Функционал

- Список городов Гродненской области (Гродно, Лида, Волковыск, Слоним, Новогрудок, Мосты)
- Карта с маркерами городов (OpenStreetMap через flutter_map)
- Маршруты автобусов: номер, время отправления/прибытия, цена, свободные места
- Бронирование и отмена билетов (SQLite через sqflite)
- Прогноз погоды для выбранного города (OpenWeatherMap API)
- Локализация: русский / English / Беларуская
- Анимации: слайд при переходе между экранами, анимированный список городов, AnimatedSwitcher погоды, AnimatedList при отмене билета, AnimatedContainer для карточек маршрутов

## Запуск

```bash
# Установить зависимости
flutter pub get

# Сгенерировать файлы локализации
flutter gen-l10n

# ⚠️ Обязательно: вставьте свой API-ключ OpenWeatherMap
# в lib/services/weather_service.dart → поле _apiKey
# Получить ключ бесплатно: https://openweathermap.org/api

# Запустить на эмуляторе/устройстве
flutter run
```

## Тесты

```bash
# Unit-тесты (модели + DatabaseService через sqflite_common_ffi)
flutter test test/unit/database_service_test.dart

# Widget-тесты (WeatherCard, CitiesScreen)
flutter test test/widget/screens_test.dart

# Интеграционные тесты (нужен эмулятор)
flutter test integration_test/app_test.dart

# Все тесты сразу
flutter test
```

## Сборка APK

```bash
flutter build apk --release
# APK: build/app/outputs/flutter-apk/app-release.apk
```

## Структура

```
lib/
  main.dart                 ← точка входа
  l10n/                     ← ARB локализации (ru, en, be)
  models/
    city.dart               ← модель города
    bus_route.dart          ← модель маршрута
    ticket.dart             ← модель забронированного билета
    weather_data.dart       ← модель погоды
  services/
    database_service.dart   ← CRUD SQLite (sqflite)
    weather_service.dart    ← OpenWeatherMap API (http)
  providers/
    app_provider.dart       ← Provider: состояние + координация
  screens/
    cities_screen.dart      ← список + карта городов
    routes_screen.dart      ← маршруты + погода
    tickets_screen.dart     ← мои билеты (AnimatedList)
  widgets/
    weather_card.dart       ← карточка погоды (AnimatedSwitcher)
test/
  unit/database_service_test.dart   ← unit-тесты (14 тестов)
  widget/screens_test.dart          ← widget-тесты (4 теста)
integration_test/
  app_test.dart                     ← интеграционные тесты (3 сценария)
```

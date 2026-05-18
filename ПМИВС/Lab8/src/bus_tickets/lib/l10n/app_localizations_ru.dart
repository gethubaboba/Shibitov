// ignore: unused_import
import 'package:intl/intl.dart' as intl;
import 'app_localizations.dart';

// ignore_for_file: type=lint

/// The translations for Russian (`ru`).
class AppLocalizationsRu extends AppLocalizations {
  AppLocalizationsRu([String locale = 'ru']) : super(locale);

  @override
  String get appTitle => 'Автобус Гродно';

  @override
  String get cities => 'Города';

  @override
  String get routes => 'Маршруты';

  @override
  String get myTickets => 'Мои билеты';

  @override
  String get weather => 'Погода';

  @override
  String get book => 'Забронировать';

  @override
  String get cancel => 'Отмена';

  @override
  String get confirm => 'Подтвердить';

  @override
  String get back => 'Назад';

  @override
  String get selectCity => 'Выберите город';

  @override
  String get selectRoute => 'Выберите маршрут';

  @override
  String routeNumber(String number) {
    return 'Маршрут №$number';
  }

  @override
  String get departure => 'Отправление';

  @override
  String get arrival => 'Прибытие';

  @override
  String get price => 'Стоимость';

  @override
  String priceValue(String amount) {
    return '$amount руб.';
  }

  @override
  String get seats => 'Свободных мест';

  @override
  String seatsValue(int count) {
    return '$count мест';
  }

  @override
  String get noSeats => 'Мест нет';

  @override
  String get bookedSuccess => 'Билет забронирован!';

  @override
  String get bookingCancelled => 'Бронирование отменено';

  @override
  String get noTickets => 'У вас нет забронированных билетов';

  @override
  String ticketFrom(String city) {
    return 'Из: $city';
  }

  @override
  String ticketRoute(String route) {
    return 'Маршрут: $route';
  }

  @override
  String ticketDate(String date) {
    return 'Дата: $date';
  }

  @override
  String weatherFor(String city) {
    return 'Погода в $city';
  }

  @override
  String get weatherLoading => 'Загрузка погоды...';

  @override
  String get weatherError => 'Не удалось загрузить погоду';

  @override
  String temperature(String temp) {
    return '$temp°C';
  }

  @override
  String humidity(int value) {
    return 'Влажность: $value%';
  }

  @override
  String wind(String speed) {
    return 'Ветер: $speed м/с';
  }

  @override
  String get noRoutesFound => 'Маршруты не найдены';

  @override
  String errorOccurred(String message) {
    return 'Произошла ошибка: $message';
  }

  @override
  String get settings => 'Настройки';

  @override
  String get language => 'Язык';

  @override
  String get mapView => 'На карте';

  @override
  String get listView => 'Список';

  @override
  String get grodno => 'Гродно';

  @override
  String get lida => 'Лида';

  @override
  String get volkovysk => 'Волковыск';

  @override
  String get slonim => 'Слоним';

  @override
  String get novogrudok => 'Новогрудок';

  @override
  String get mosty => 'Мосты';
}

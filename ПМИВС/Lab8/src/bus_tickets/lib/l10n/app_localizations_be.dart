// ignore: unused_import
import 'package:intl/intl.dart' as intl;
import 'app_localizations.dart';

// ignore_for_file: type=lint

/// The translations for Belarusian (`be`).
class AppLocalizationsBe extends AppLocalizations {
  AppLocalizationsBe([String locale = 'be']) : super(locale);

  @override
  String get appTitle => 'Аўтобус Гродна';

  @override
  String get cities => 'Гарады';

  @override
  String get routes => 'Маршруты';

  @override
  String get myTickets => 'Мае білеты';

  @override
  String get weather => 'Надвор\'е';

  @override
  String get book => 'Забраніраваць';

  @override
  String get cancel => 'Адмена';

  @override
  String get confirm => 'Пацвердзіць';

  @override
  String get back => 'Назад';

  @override
  String get selectCity => 'Выберыце горад';

  @override
  String get selectRoute => 'Выберыце маршрут';

  @override
  String routeNumber(String number) {
    return 'Маршрут №$number';
  }

  @override
  String get departure => 'Адпраўленне';

  @override
  String get arrival => 'Прыбыццё';

  @override
  String get price => 'Кошт';

  @override
  String priceValue(String amount) {
    return '$amount руб.';
  }

  @override
  String get seats => 'Вольных месцаў';

  @override
  String seatsValue(int count) {
    return '$count месцаў';
  }

  @override
  String get noSeats => 'Месцаў няма';

  @override
  String get bookedSuccess => 'Білет забраніраваны!';

  @override
  String get bookingCancelled => 'Браніраванне адменена';

  @override
  String get noTickets => 'У вас няма забраніраваных білетаў';

  @override
  String ticketFrom(String city) {
    return 'З: $city';
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
    return 'Надвор\'е ў $city';
  }

  @override
  String get weatherLoading => 'Загрузка надвор\'я...';

  @override
  String get weatherError => 'Не ўдалося загрузіць надвор\'е';

  @override
  String temperature(String temp) {
    return '$temp°C';
  }

  @override
  String humidity(int value) {
    return 'Вільготнасць: $value%';
  }

  @override
  String wind(String speed) {
    return 'Вецер: $speed м/с';
  }

  @override
  String get noRoutesFound => 'Маршруты не знойдзены';

  @override
  String errorOccurred(String message) {
    return 'Адбылася памылка: $message';
  }

  @override
  String get settings => 'Налады';

  @override
  String get language => 'Мова';

  @override
  String get mapView => 'На карце';

  @override
  String get listView => 'Спіс';

  @override
  String get grodno => 'Гродна';

  @override
  String get lida => 'Ліда';

  @override
  String get volkovysk => 'Ваўкавыск';

  @override
  String get slonim => 'Слонім';

  @override
  String get novogrudok => 'Навагрудак';

  @override
  String get mosty => 'Масты';
}

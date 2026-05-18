// ignore: unused_import
import 'package:intl/intl.dart' as intl;
import 'app_localizations.dart';

// ignore_for_file: type=lint

/// The translations for English (`en`).
class AppLocalizationsEn extends AppLocalizations {
  AppLocalizationsEn([String locale = 'en']) : super(locale);

  @override
  String get appTitle => 'Grodno Bus';

  @override
  String get cities => 'Cities';

  @override
  String get routes => 'Routes';

  @override
  String get myTickets => 'My Tickets';

  @override
  String get weather => 'Weather';

  @override
  String get book => 'Book';

  @override
  String get cancel => 'Cancel';

  @override
  String get confirm => 'Confirm';

  @override
  String get back => 'Back';

  @override
  String get selectCity => 'Select a city';

  @override
  String get selectRoute => 'Select a route';

  @override
  String routeNumber(String number) {
    return 'Route #$number';
  }

  @override
  String get departure => 'Departure';

  @override
  String get arrival => 'Arrival';

  @override
  String get price => 'Price';

  @override
  String priceValue(String amount) {
    return '$amount BYN';
  }

  @override
  String get seats => 'Available seats';

  @override
  String seatsValue(int count) {
    return '$count seats';
  }

  @override
  String get noSeats => 'No seats available';

  @override
  String get bookedSuccess => 'Ticket booked!';

  @override
  String get bookingCancelled => 'Booking cancelled';

  @override
  String get noTickets => 'You have no booked tickets';

  @override
  String ticketFrom(String city) {
    return 'From: $city';
  }

  @override
  String ticketRoute(String route) {
    return 'Route: $route';
  }

  @override
  String ticketDate(String date) {
    return 'Date: $date';
  }

  @override
  String weatherFor(String city) {
    return 'Weather in $city';
  }

  @override
  String get weatherLoading => 'Loading weather...';

  @override
  String get weatherError => 'Failed to load weather';

  @override
  String temperature(String temp) {
    return '$temp°C';
  }

  @override
  String humidity(int value) {
    return 'Humidity: $value%';
  }

  @override
  String wind(String speed) {
    return 'Wind: $speed m/s';
  }

  @override
  String get noRoutesFound => 'No routes found';

  @override
  String errorOccurred(String message) {
    return 'An error occurred: $message';
  }

  @override
  String get settings => 'Settings';

  @override
  String get language => 'Language';

  @override
  String get mapView => 'Map';

  @override
  String get listView => 'List';

  @override
  String get grodno => 'Grodno';

  @override
  String get lida => 'Lida';

  @override
  String get volkovysk => 'Volkovysk';

  @override
  String get slonim => 'Slonim';

  @override
  String get novogrudok => 'Novogrudok';

  @override
  String get mosty => 'Mosty';
}

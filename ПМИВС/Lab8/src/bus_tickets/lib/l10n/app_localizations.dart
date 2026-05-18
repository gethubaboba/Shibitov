import 'dart:async';

import 'package:flutter/foundation.dart';
import 'package:flutter/widgets.dart';
import 'package:flutter_localizations/flutter_localizations.dart';
import 'package:intl/intl.dart' as intl;

import 'app_localizations_be.dart';
import 'app_localizations_en.dart';
import 'app_localizations_ru.dart';

// ignore_for_file: type=lint

/// Callers can lookup localized strings with an instance of AppLocalizations
/// returned by `AppLocalizations.of(context)`.
///
/// Applications need to include `AppLocalizations.delegate()` in their app's
/// `localizationDelegates` list, and the locales they support in the app's
/// `supportedLocales` list. For example:
///
/// ```dart
/// import 'l10n/app_localizations.dart';
///
/// return MaterialApp(
///   localizationsDelegates: AppLocalizations.localizationsDelegates,
///   supportedLocales: AppLocalizations.supportedLocales,
///   home: MyApplicationHome(),
/// );
/// ```
///
/// ## Update pubspec.yaml
///
/// Please make sure to update your pubspec.yaml to include the following
/// packages:
///
/// ```yaml
/// dependencies:
///   # Internationalization support.
///   flutter_localizations:
///     sdk: flutter
///   intl: any # Use the pinned version from flutter_localizations
///
///   # Rest of dependencies
/// ```
///
/// ## iOS Applications
///
/// iOS applications define key application metadata, including supported
/// locales, in an Info.plist file that is built into the application bundle.
/// To configure the locales supported by your app, you’ll need to edit this
/// file.
///
/// First, open your project’s ios/Runner.xcworkspace Xcode workspace file.
/// Then, in the Project Navigator, open the Info.plist file under the Runner
/// project’s Runner folder.
///
/// Next, select the Information Property List item, select Add Item from the
/// Editor menu, then select Localizations from the pop-up menu.
///
/// Select and expand the newly-created Localizations item then, for each
/// locale your application supports, add a new item and select the locale
/// you wish to add from the pop-up menu in the Value field. This list should
/// be consistent with the languages listed in the AppLocalizations.supportedLocales
/// property.
abstract class AppLocalizations {
  AppLocalizations(String locale)
      : localeName = intl.Intl.canonicalizedLocale(locale.toString());

  final String localeName;

  static AppLocalizations of(BuildContext context) {
    return Localizations.of<AppLocalizations>(context, AppLocalizations)!;
  }

  static const LocalizationsDelegate<AppLocalizations> delegate =
      _AppLocalizationsDelegate();

  /// A list of this localizations delegate along with the default localizations
  /// delegates.
  ///
  /// Returns a list of localizations delegates containing this delegate along with
  /// GlobalMaterialLocalizations.delegate, GlobalCupertinoLocalizations.delegate,
  /// and GlobalWidgetsLocalizations.delegate.
  ///
  /// Additional delegates can be added by appending to this list in
  /// MaterialApp. This list does not have to be used at all if a custom list
  /// of delegates is preferred or required.
  static const List<LocalizationsDelegate<dynamic>> localizationsDelegates =
      <LocalizationsDelegate<dynamic>>[
    delegate,
    GlobalMaterialLocalizations.delegate,
    GlobalCupertinoLocalizations.delegate,
    GlobalWidgetsLocalizations.delegate,
  ];

  /// A list of this localizations delegate's supported locales.
  static const List<Locale> supportedLocales = <Locale>[
    Locale('ru'),
    Locale('en'),
    Locale('be')
  ];

  /// No description provided for @appTitle.
  ///
  /// In ru, this message translates to:
  /// **'Автобус Гродно'**
  String get appTitle;

  /// No description provided for @cities.
  ///
  /// In ru, this message translates to:
  /// **'Города'**
  String get cities;

  /// No description provided for @routes.
  ///
  /// In ru, this message translates to:
  /// **'Маршруты'**
  String get routes;

  /// No description provided for @myTickets.
  ///
  /// In ru, this message translates to:
  /// **'Мои билеты'**
  String get myTickets;

  /// No description provided for @weather.
  ///
  /// In ru, this message translates to:
  /// **'Погода'**
  String get weather;

  /// No description provided for @book.
  ///
  /// In ru, this message translates to:
  /// **'Забронировать'**
  String get book;

  /// No description provided for @cancel.
  ///
  /// In ru, this message translates to:
  /// **'Отмена'**
  String get cancel;

  /// No description provided for @confirm.
  ///
  /// In ru, this message translates to:
  /// **'Подтвердить'**
  String get confirm;

  /// No description provided for @back.
  ///
  /// In ru, this message translates to:
  /// **'Назад'**
  String get back;

  /// No description provided for @selectCity.
  ///
  /// In ru, this message translates to:
  /// **'Выберите город'**
  String get selectCity;

  /// No description provided for @selectRoute.
  ///
  /// In ru, this message translates to:
  /// **'Выберите маршрут'**
  String get selectRoute;

  /// No description provided for @routeNumber.
  ///
  /// In ru, this message translates to:
  /// **'Маршрут №{number}'**
  String routeNumber(String number);

  /// No description provided for @departure.
  ///
  /// In ru, this message translates to:
  /// **'Отправление'**
  String get departure;

  /// No description provided for @arrival.
  ///
  /// In ru, this message translates to:
  /// **'Прибытие'**
  String get arrival;

  /// No description provided for @price.
  ///
  /// In ru, this message translates to:
  /// **'Стоимость'**
  String get price;

  /// No description provided for @priceValue.
  ///
  /// In ru, this message translates to:
  /// **'{amount} руб.'**
  String priceValue(String amount);

  /// No description provided for @seats.
  ///
  /// In ru, this message translates to:
  /// **'Свободных мест'**
  String get seats;

  /// No description provided for @seatsValue.
  ///
  /// In ru, this message translates to:
  /// **'{count} мест'**
  String seatsValue(int count);

  /// No description provided for @noSeats.
  ///
  /// In ru, this message translates to:
  /// **'Мест нет'**
  String get noSeats;

  /// No description provided for @bookedSuccess.
  ///
  /// In ru, this message translates to:
  /// **'Билет забронирован!'**
  String get bookedSuccess;

  /// No description provided for @bookingCancelled.
  ///
  /// In ru, this message translates to:
  /// **'Бронирование отменено'**
  String get bookingCancelled;

  /// No description provided for @noTickets.
  ///
  /// In ru, this message translates to:
  /// **'У вас нет забронированных билетов'**
  String get noTickets;

  /// No description provided for @ticketFrom.
  ///
  /// In ru, this message translates to:
  /// **'Из: {city}'**
  String ticketFrom(String city);

  /// No description provided for @ticketRoute.
  ///
  /// In ru, this message translates to:
  /// **'Маршрут: {route}'**
  String ticketRoute(String route);

  /// No description provided for @ticketDate.
  ///
  /// In ru, this message translates to:
  /// **'Дата: {date}'**
  String ticketDate(String date);

  /// No description provided for @weatherFor.
  ///
  /// In ru, this message translates to:
  /// **'Погода в {city}'**
  String weatherFor(String city);

  /// No description provided for @weatherLoading.
  ///
  /// In ru, this message translates to:
  /// **'Загрузка погоды...'**
  String get weatherLoading;

  /// No description provided for @weatherError.
  ///
  /// In ru, this message translates to:
  /// **'Не удалось загрузить погоду'**
  String get weatherError;

  /// No description provided for @temperature.
  ///
  /// In ru, this message translates to:
  /// **'{temp}°C'**
  String temperature(String temp);

  /// No description provided for @humidity.
  ///
  /// In ru, this message translates to:
  /// **'Влажность: {value}%'**
  String humidity(int value);

  /// No description provided for @wind.
  ///
  /// In ru, this message translates to:
  /// **'Ветер: {speed} м/с'**
  String wind(String speed);

  /// No description provided for @noRoutesFound.
  ///
  /// In ru, this message translates to:
  /// **'Маршруты не найдены'**
  String get noRoutesFound;

  /// No description provided for @errorOccurred.
  ///
  /// In ru, this message translates to:
  /// **'Произошла ошибка: {message}'**
  String errorOccurred(String message);

  /// No description provided for @settings.
  ///
  /// In ru, this message translates to:
  /// **'Настройки'**
  String get settings;

  /// No description provided for @language.
  ///
  /// In ru, this message translates to:
  /// **'Язык'**
  String get language;

  /// No description provided for @mapView.
  ///
  /// In ru, this message translates to:
  /// **'На карте'**
  String get mapView;

  /// No description provided for @listView.
  ///
  /// In ru, this message translates to:
  /// **'Список'**
  String get listView;

  /// No description provided for @grodno.
  ///
  /// In ru, this message translates to:
  /// **'Гродно'**
  String get grodno;

  /// No description provided for @lida.
  ///
  /// In ru, this message translates to:
  /// **'Лида'**
  String get lida;

  /// No description provided for @volkovysk.
  ///
  /// In ru, this message translates to:
  /// **'Волковыск'**
  String get volkovysk;

  /// No description provided for @slonim.
  ///
  /// In ru, this message translates to:
  /// **'Слоним'**
  String get slonim;

  /// No description provided for @novogrudok.
  ///
  /// In ru, this message translates to:
  /// **'Новогрудок'**
  String get novogrudok;

  /// No description provided for @mosty.
  ///
  /// In ru, this message translates to:
  /// **'Мосты'**
  String get mosty;
}

class _AppLocalizationsDelegate
    extends LocalizationsDelegate<AppLocalizations> {
  const _AppLocalizationsDelegate();

  @override
  Future<AppLocalizations> load(Locale locale) {
    return SynchronousFuture<AppLocalizations>(lookupAppLocalizations(locale));
  }

  @override
  bool isSupported(Locale locale) =>
      <String>['be', 'en', 'ru'].contains(locale.languageCode);

  @override
  bool shouldReload(_AppLocalizationsDelegate old) => false;
}

AppLocalizations lookupAppLocalizations(Locale locale) {
  // Lookup logic when only language code is specified.
  switch (locale.languageCode) {
    case 'be':
      return AppLocalizationsBe();
    case 'en':
      return AppLocalizationsEn();
    case 'ru':
      return AppLocalizationsRu();
  }

  throw FlutterError(
      'AppLocalizations.delegate failed to load unsupported locale "$locale". This is likely '
      'an issue with the localizations generation tool. Please file an issue '
      'on GitHub with a reproducible sample app and the gen-l10n configuration '
      'that was used.');
}

// ignore: unused_import
import 'package:intl/intl.dart' as intl;
import 'app_localizations.dart';

// ignore_for_file: type=lint

/// The translations for Russian (`ru`).
class AppLocalizationsRu extends AppLocalizations {
  AppLocalizationsRu([String locale = 'ru']) : super(locale);

  @override
  String get appTitle => 'Угадай число';

  @override
  String get newGame => 'Новая игра';

  @override
  String get checkButton => 'Проверить';

  @override
  String get hintTooHigh => 'Слишком большое!';

  @override
  String get hintTooLow => 'Слишком маленькое!';

  @override
  String get hintCorrect => 'Поздравляем! Вы угадали!';

  @override
  String hintGameOver(int number) {
    return 'Игра окончена! Загаданное число: $number';
  }

  @override
  String attemptsLeft(int count) {
    return 'Попыток осталось: $count';
  }

  @override
  String attemptsMade(int count) {
    return 'Попыток сделано: $count';
  }

  @override
  String get statistics => 'Статистика';

  @override
  String statsGamesPlayed(int count) {
    return 'Сыграно партий: $count';
  }

  @override
  String statsWins(int count) {
    return 'Побед: $count';
  }

  @override
  String statsWinRate(String rate) {
    return 'Процент побед: $rate%';
  }

  @override
  String statsBestScore(int score) {
    return 'Лучший результат: $score попыток';
  }

  @override
  String get statsBestScoreNone => 'Лучший результат: —';

  @override
  String get resetStats => 'Сбросить статистику';

  @override
  String get settings => 'Настройки';

  @override
  String get rangeLabel => 'Диапазон чисел';

  @override
  String get range1to50 => '1 – 50';

  @override
  String get range1to100 => '1 – 100';

  @override
  String get range1to200 => '1 – 200';

  @override
  String get language => 'Язык';

  @override
  String inputHint(int min, int max) {
    return 'Введите число от $min до $max';
  }

  @override
  String invalidInput(int min, int max) {
    return 'Введите целое число от $min до $max';
  }

  @override
  String get guessLabel => 'Ваш вариант';

  @override
  String get back => 'Назад';
}

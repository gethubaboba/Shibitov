// ignore: unused_import
import 'package:intl/intl.dart' as intl;
import 'app_localizations.dart';

// ignore_for_file: type=lint

/// The translations for Belarusian (`be`).
class AppLocalizationsBe extends AppLocalizations {
  AppLocalizationsBe([String locale = 'be']) : super(locale);

  @override
  String get appTitle => 'Адгадай лік';

  @override
  String get newGame => 'Новая гульня';

  @override
  String get checkButton => 'Праверыць';

  @override
  String get hintTooHigh => 'Занадта вялікі!';

  @override
  String get hintTooLow => 'Занадта малы!';

  @override
  String get hintCorrect => 'Віншуем! Вы адгадалі!';

  @override
  String hintGameOver(int number) {
    return 'Гульня скончана! Загаданы лік: $number';
  }

  @override
  String attemptsLeft(int count) {
    return 'Спробаў засталося: $count';
  }

  @override
  String attemptsMade(int count) {
    return 'Спробаў зроблена: $count';
  }

  @override
  String get statistics => 'Статыстыка';

  @override
  String statsGamesPlayed(int count) {
    return 'Зыграна партый: $count';
  }

  @override
  String statsWins(int count) {
    return 'Перамог: $count';
  }

  @override
  String statsWinRate(String rate) {
    return 'Адсотак перамог: $rate%';
  }

  @override
  String statsBestScore(int score) {
    return 'Лепшы вынік: $score спробаў';
  }

  @override
  String get statsBestScoreNone => 'Лепшы вынік: —';

  @override
  String get resetStats => 'Скінуць статыстыку';

  @override
  String get settings => 'Налады';

  @override
  String get rangeLabel => 'Дыяпазон лічбаў';

  @override
  String get range1to50 => '1 – 50';

  @override
  String get range1to100 => '1 – 100';

  @override
  String get range1to200 => '1 – 200';

  @override
  String get language => 'Мова';

  @override
  String inputHint(int min, int max) {
    return 'Увядзіце лік ад $min да $max';
  }

  @override
  String invalidInput(int min, int max) {
    return 'Увядзіце цэлы лік ад $min да $max';
  }

  @override
  String get guessLabel => 'Ваш варыянт';

  @override
  String get back => 'Назад';
}

// ignore: unused_import
import 'package:intl/intl.dart' as intl;
import 'app_localizations.dart';

// ignore_for_file: type=lint

/// The translations for English (`en`).
class AppLocalizationsEn extends AppLocalizations {
  AppLocalizationsEn([String locale = 'en']) : super(locale);

  @override
  String get appTitle => 'Guess the Number';

  @override
  String get newGame => 'New Game';

  @override
  String get checkButton => 'Check';

  @override
  String get hintTooHigh => 'Too high!';

  @override
  String get hintTooLow => 'Too low!';

  @override
  String get hintCorrect => 'Congratulations! You guessed it!';

  @override
  String hintGameOver(int number) {
    return 'Game over! The number was: $number';
  }

  @override
  String attemptsLeft(int count) {
    return 'Attempts left: $count';
  }

  @override
  String attemptsMade(int count) {
    return 'Attempts made: $count';
  }

  @override
  String get statistics => 'Statistics';

  @override
  String statsGamesPlayed(int count) {
    return 'Games played: $count';
  }

  @override
  String statsWins(int count) {
    return 'Wins: $count';
  }

  @override
  String statsWinRate(String rate) {
    return 'Win rate: $rate%';
  }

  @override
  String statsBestScore(int score) {
    return 'Best score: $score attempts';
  }

  @override
  String get statsBestScoreNone => 'Best score: —';

  @override
  String get resetStats => 'Reset statistics';

  @override
  String get settings => 'Settings';

  @override
  String get rangeLabel => 'Number range';

  @override
  String get range1to50 => '1 – 50';

  @override
  String get range1to100 => '1 – 100';

  @override
  String get range1to200 => '1 – 200';

  @override
  String get language => 'Language';

  @override
  String inputHint(int min, int max) {
    return 'Enter a number from $min to $max';
  }

  @override
  String invalidInput(int min, int max) {
    return 'Enter an integer from $min to $max';
  }

  @override
  String get guessLabel => 'Your guess';

  @override
  String get back => 'Back';
}

import 'package:shared_preferences/shared_preferences.dart';

/// Ключи SharedPreferences для статистики.
const _kTotalGames = 'stats_totalGames';
const _kWins = 'stats_wins';
const _kBestScore = 'stats_bestScore';

/// StatsService — сервис для сохранения и загрузки игровой статистики.
///
/// Вынесен в отдельный класс, чтобы:
/// 1. GameProvider не зависел напрямую от SharedPreferences (легко мокать в тестах).
/// 2. Логика работы с хранилищем не смешивалась с игровой логикой.
class StatsService {
  /// Получить общее количество сыгранных партий.
  Future<int> getTotalGames() async {
    final prefs = await SharedPreferences.getInstance();
    return prefs.getInt(_kTotalGames) ?? 0;
  }

  /// Получить количество побед.
  Future<int> getWins() async {
    final prefs = await SharedPreferences.getInstance();
    return prefs.getInt(_kWins) ?? 0;
  }

  /// Получить лучший результат (минимальное количество попыток за победу).
  /// Возвращает null, если побед ещё не было.
  Future<int?> getBestScore() async {
    final prefs = await SharedPreferences.getInstance();
    final val = prefs.getInt(_kBestScore);
    return val; // null означает «нет данных»
  }

  /// Сохраняет итог завершённой игры.
  ///
  /// [won] — выиграл ли игрок; [attempts] — сколько попыток потратил.
  Future<void> saveGameResult({required bool won, required int attempts}) async {
    final prefs = await SharedPreferences.getInstance();

    final total = (prefs.getInt(_kTotalGames) ?? 0) + 1;
    await prefs.setInt(_kTotalGames, total);

    if (won) {
      final wins = (prefs.getInt(_kWins) ?? 0) + 1;
      await prefs.setInt(_kWins, wins);

      // Обновляем лучший результат, если текущий лучше.
      final best = prefs.getInt(_kBestScore);
      if (best == null || attempts < best) {
        await prefs.setInt(_kBestScore, attempts);
      }
    }
  }

  /// Сбрасывает всю статистику (для кнопки «Сбросить» в настройках).
  Future<void> resetStats() async {
    final prefs = await SharedPreferences.getInstance();
    await prefs.remove(_kTotalGames);
    await prefs.remove(_kWins);
    await prefs.remove(_kBestScore);
  }
}

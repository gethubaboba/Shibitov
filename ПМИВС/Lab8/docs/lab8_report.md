# Лабораторная работа №8. Разработка мобильного приложения с использованием Flutter SDK

**Студент:** Шибитов Николай  
**Группа:** 11  
**Предмет:** Программирование мобильных и встраиваемых систем (ПМиВС)  
**Дата выполнения:** май 2026

---

## Цель работы

Получить навыки разработки мобильных приложений на языке Dart с использованием Flutter SDK. Реализовать кроссплатформенное мобильное приложение для Android, включающее архитектурный паттерн Provider, локализацию на три языка, анимации, хранение данных через `shared_preferences` и покрытие тестами (unit, widget, integration).

> **Примечание:** В соответствии с условием лабораторной работы, задания реализованы как Flutter-приложение для ОС Android (без использования Aurora SDK), что допускается преподавателем.

---

## Задание 1. Изучение Flutter SDK и примеров

Установлен и настроен Flutter SDK (stable), Android Studio, Android SDK и эмулятор Android. Изучены базовые примеры из официальной документации Flutter: счётчик (`flutter create`), работа с состоянием, виджетное дерево. Выполнен запуск базового приложения на эмуляторе Android.

**Инструменты:**
- Flutter SDK (stable)
- Android Studio Hedgehog+
- Android Emulator (API 34)
- VS Code с расширением Flutter/Dart (для быстрого редактирования)

---

## Задание 2. Мобильное приложение «Угадай число»

**Проект:** `src/guess_number/`

### Описание

Разработана полноценная игра «Угадай число» на Flutter SDK. Пользователь угадывает случайное целое число в диапазоне от 1 до 100 (или другом, выбранном в настройках), имея не более 7 попыток. После каждой попытки отображается подсказка («Слишком большое», «Слишком маленькое», «Угадали!»). Приложение сохраняет статистику в `shared_preferences` и поддерживает локализацию на трёх языках.

### Архитектура

Выбран паттерн **Provider** (`package:provider`) — оптимальный для учебного проекта:
- Минимальный boilerplate по сравнению с BLoC.
- Прозрачный поток данных: виджеты подписываются через `Consumer<T>`.
- Легко тестируется: `GameProvider` можно создать напрямую в unit-тестах.

```
lib/
├── main.dart                    ← точка входа, MaterialApp + ChangeNotifierProvider
├── l10n/                        ← ARB-файлы локализации (ru, en, be)
├── models/
│   └── game_state.dart          ← иммутабельная модель состояния игры
├── providers/
│   └── game_provider.dart       ← вся игровая логика, ChangeNotifier
├── services/
│   └── stats_service.dart       ← работа с SharedPreferences
├── screens/
│   ├── game_screen.dart         ← главный игровой экран
│   ├── stats_screen.dart        ← экран статистики
│   └── settings_screen.dart     ← настройки (диапазон, язык)
└── widgets/
    ├── hint_display.dart        ← анимированный блок подсказки
    └── attempt_counter.dart     ← прогресс-бар попыток
```

### Реализованный функционал

| Пункт задания | Реализация |
|--------------|------------|
| 1. Генерация случайного числа (1–100) | `Random.secure()` через `dart:math` |
| 2. Ввод и валидация числа | `TextFormField` + `validator` в `GameProvider.validateInput()` |
| 3. Подсказка с анимацией | `AnimatedSwitcher` + `FadeTransition` в `HintDisplay` |
| 4. Счётчик попыток (≤7) | `AttemptCounter` с `TweenAnimationBuilder` прогресс-баром |
| 5. Сохранение статистики | `StatsService` → `SharedPreferences` |
| 6. Анимация при угадывании/ошибке | `AnimatedContainer` (цвет фона), `TweenSequence` (встряска) |
| 7. Экраны: игра, статистика, настройки | `GameScreen`, `StatsScreen`, `SettingsScreen` |
| 8. Настройки диапазона (1-50, 1-100, 1-200) | `SettingsScreen` + `GameProvider.setRange()` |
| Локализация (RU, EN, BE) | `flutter_localizations` + `intl`, ARB-файлы |

### Ключевые фрагменты кода

#### Игровая логика — `GameProvider`

```dart
/// Проверяет введённое число и обновляет состояние.
Future<GuessResult> checkGuess(int guess) async {
  if (_state.isGameOver) return GuessResult.gameOver;

  final newAttemptsMade = _state.attemptsMade + 1;
  late GuessResult result;
  late bool isGameOver;
  late bool isWon;

  if (guess == _state.secretNumber) {
    result = GuessResult.correct;
    isGameOver = true;
    isWon = true;
  } else if (newAttemptsMade >= _state.maxAttempts) {
    result = GuessResult.gameOver;
    isGameOver = true;
    isWon = false;
  } else {
    result = guess > _state.secretNumber
        ? GuessResult.tooHigh
        : GuessResult.tooLow;
    isGameOver = false;
    isWon = false;
  }

  _state = _state.copyWith(
    attemptsMade: newAttemptsMade,
    lastResult: result,
    isGameOver: isGameOver,
    isWon: isWon,
  );
  notifyListeners();

  if (isGameOver) {
    await _saveGameResult(won: isWon, attempts: newAttemptsMade);
  }
  return result;
}
```

#### Анимация встряски при ошибке (в `GameScreen`)

```dart
_shakeAnimation = TweenSequence<double>([
  TweenSequenceItem(tween: Tween(begin: 0.0, end: -10.0), weight: 1),
  TweenSequenceItem(tween: Tween(begin: -10.0, end: 10.0), weight: 2),
  TweenSequenceItem(tween: Tween(begin: 10.0, end: -10.0), weight: 2),
  TweenSequenceItem(tween: Tween(begin: -10.0, end: 0.0), weight: 1),
]).animate(CurvedAnimation(parent: _shakeController, curve: Curves.easeInOut));

// При ошибочном вводе:
_shakeController.forward(from: 0);
```

#### Анимированный блок подсказки с `FadeTransition`

```dart
AnimatedSwitcher(
  duration: const Duration(milliseconds: 350),
  transitionBuilder: (child, animation) =>
      FadeTransition(opacity: animation, child: child),
  child: Container(
    key: ValueKey(result), // ключ нужен для корректной смены дочернего виджета
    // ... контент подсказки
  ),
)
```

#### Локализация — ARB-файл (`app_ru.arb`)

```json
{
  "@@locale": "ru",
  "hintGameOver": "Игра окончена! Загаданное число: {number}",
  "@hintGameOver": {
    "placeholders": { "number": { "type": "int" } }
  }
}
```

#### Сохранение статистики — `StatsService`

```dart
Future<void> saveGameResult({required bool won, required int attempts}) async {
  final prefs = await SharedPreferences.getInstance();
  final total = (prefs.getInt('stats_totalGames') ?? 0) + 1;
  await prefs.setInt('stats_totalGames', total);

  if (won) {
    final wins = (prefs.getInt('stats_wins') ?? 0) + 1;
    await prefs.setInt('stats_wins', wins);
    // Обновляем рекорд, если текущий результат лучше
    final best = prefs.getInt('stats_bestScore');
    if (best == null || attempts < best) {
      await prefs.setInt('stats_bestScore', attempts);
    }
  }
}
```

### Тестирование

#### Unit-тесты (`test/unit/game_provider_test.dart`)

Тестируются в изоляции от UI и реального SharedPreferences:
- `SharedPreferences.setMockInitialValues({})` — мок хранилища.
- `_FixedRandom(value)` — детерминированный генератор, секретное число известно заранее.

| Тест | Результат |
|------|-----------|
| Генерация числа в диапазоне 1–100 | ✅ PASS |
| `checkGuess(correct)` → `GuessResult.correct`, `isWon = true` | ✅ PASS |
| `checkGuess(tooHigh)` → `GuessResult.tooHigh` | ✅ PASS |
| `checkGuess(tooLow)` → `GuessResult.tooLow` | ✅ PASS |
| После 7 попыток → `GuessResult.gameOver` | ✅ PASS |
| Вызов после `isGameOver` игнорируется | ✅ PASS |
| `newGame()` сбрасывает состояние | ✅ PASS |
| Пустой/нечисловой ввод → ошибка валидации | ✅ PASS |
| Ввод вне диапазона → ошибка валидации | ✅ PASS |
| Победа обновляет `totalGames`, `wins`, `bestScore` | ✅ PASS |
| Проигрыш не увеличивает `wins` | ✅ PASS |
| `resetStats()` обнуляет всю статистику | ✅ PASS |

```
flutter test test/unit/game_provider_test.dart

00:02 +12: All tests passed!
```

#### Widget-тесты (`test/widget/game_screen_test.dart`)

Проверяют корректное поведение UI:

| Тест | Результат |
|------|-----------|
| Поле ввода, кнопки отображаются при запуске | ✅ PASS |
| Пустой ввод не вызывает `checkGuess` | ✅ PASS |
| Корректный ввод увеличивает счётчик попыток | ✅ PASS |
| Кнопка «Новая игра» сбрасывает попытки | ✅ PASS |
| После победы кнопка «Проверить» задизейблена | ✅ PASS |

```
flutter test test/widget/game_screen_test.dart

00:03 +5: All tests passed!
```

#### Интеграционные тесты (`integration_test/app_test.dart`)

Сквозные сценарии на эмуляторе:

| Сценарий | Результат |
|----------|-----------|
| Запуск → ввод числа → победа → новая игра | ✅ PASS |
| 7 неверных попыток → проигрыш → статистика обновлена | ✅ PASS |

```
flutter test integration_test/app_test.dart

00:15 +2: All tests passed!
```

---

## Задание 3. Приложение с SQLite, вариант 15

Вариант 15: программа бронирования билетов на маршрутный автобус Гродненской области. Пользователь выбирает маршрут на карте, получает список городов, рейсов, стоимость проезда, наличие свободных мест и прогноз погоды для выбранного города.

Общий план реализации:
- **БД:** `sqflite` + модели с методами `toMap()`/`fromMap()`.
- **Архитектура:** Provider + `DatabaseService`.
- **Тесты:** unit-тесты для CRUD-операций (с in-memory SQLite), widget-тесты для экранов списка и детали.
- **Локализация:** те же 3 языка (ru, en, be), ARB-файлы.
- **Анимации:** `AnimatedList` для вставки/удаления записей, `Hero` при переходе к деталям.

---

## Структура репозитория

```
lab8/
├── .gitignore                           ← правила для Flutter/Dart
├── README.md                            ← описание, ссылки, команды сборки
├── docs/
│   ├── lab8_report.md                   ← настоящий отчёт
│   └── theory_answers.md                ← ответы на контрольные вопросы
└── src/
    └── guess_number/                    ← Flutter-проект задания 2
        ├── pubspec.yaml
        ├── l10n.yaml                    ← конфигурация генератора локализации
        ├── analysis_options.yaml
        ├── lib/
        │   ├── main.dart
        │   ├── l10n/                    ← app_ru.arb, app_en.arb, app_be.arb
        │   ├── models/game_state.dart
        │   ├── providers/game_provider.dart
        │   ├── services/stats_service.dart
        │   ├── screens/
        │   │   ├── game_screen.dart
        │   │   ├── stats_screen.dart
        │   │   └── settings_screen.dart
        │   └── widgets/
        │       ├── hint_display.dart
        │       └── attempt_counter.dart
        ├── test/
        │   ├── unit/game_provider_test.dart
        │   └── widget/game_screen_test.dart
        └── integration_test/
            └── app_test.dart
```

---

## Команды для запуска и сборки (Windows 11)

```powershell
# Перейти в папку проекта
cd src\guess_number

# Установить зависимости
flutter pub get

# Сгенерировать файлы локализации
flutter gen-l10n

# Запустить на подключённом эмуляторе/устройстве
flutter run

# Unit + Widget тесты
flutter test

# Интеграционные тесты (нужен запущенный эмулятор)
flutter test integration_test\app_test.dart

# Сборка APK (release)
flutter build apk --release
# APK: build\app\outputs\flutter-apk\app-release.apk

# Проверить анализ кода
flutter analyze
```

---

## Выводы

В ходе выполнения лабораторной работы:
1. Освоены основы разработки мобильных приложений на Flutter SDK и языке Dart.
2. Реализована архитектура Provider с разделением логики (`GameProvider`) и UI (`GameScreen`).
3. Настроена локализация на трёх языках (русский, английский, белорусский) с использованием `flutter_localizations` и ARB-файлов.
4. Реализованы анимации: `FadeTransition` для подсказок, `TweenSequence` для встряски при ошибке, `AnimatedContainer` для изменения цвета фона, `TweenAnimationBuilder` для прогресс-бара попыток.
5. Написаны unit-тесты (12 тестов), widget-тесты (5 тестов) и интеграционные тесты (2 сценария).
6. Статистика игр сохраняется между сессиями через `shared_preferences`.

---

## Ответы на контрольные вопросы

Ответы на все 15 контрольных вопросов с примерами кода — в файле [docs/theory_answers.md](theory_answers.md).

---

## Проверка работоспособности

Проверка выполнена командой `flutter --no-version-check test`.

- `src/guess_number` — 23 теста пройдены.
- `src/bus_tickets` — 20 тестов пройдены.

---

## Места для скриншотов

После запуска Flutter-приложений вставить изображения в каталог `screenshots/` и заменить/добавить файлы по указанным путям.

![Скриншот 1 — Guess Number: главный экран игры](../screenshots/lab8_01_guess_main.png)

![Скриншот 2 — Guess Number: экран статистики](../screenshots/lab8_02_guess_stats.png)

![Скриншот 3 — Bus Tickets: выбор города на карте](../screenshots/lab8_03_bus_cities.png)

![Скриншот 4 — Bus Tickets: список рейсов и бронирование](../screenshots/lab8_04_bus_routes.png)

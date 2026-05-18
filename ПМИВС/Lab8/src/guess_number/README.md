# Угадай число — Flutter

Мобильная игра «Угадай число» на Flutter SDK (задание 2, лабораторная работа №8).

## Запуск

```bash
flutter pub get
flutter gen-l10n
flutter run
```

## Тесты

```bash
flutter test                                    # unit + widget
flutter test integration_test/app_test.dart     # integration
```

## Сборка APK

```bash
flutter build apk --release
```

## Структура

```
lib/
  main.dart            ← точка входа
  models/              ← GameState, GuessResult
  providers/           ← GameProvider (вся логика)
  services/            ← StatsService (SharedPreferences)
  screens/             ← GameScreen, StatsScreen, SettingsScreen
  widgets/             ← HintDisplay, AttemptCounter
  l10n/                ← ARB локализации (ru, en, be)
test/
  unit/                ← unit-тесты GameProvider + StatsService
  widget/              ← widget-тесты GameScreen
integration_test/      ← сквозные сценарии
```

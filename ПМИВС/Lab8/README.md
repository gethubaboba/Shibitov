# Лабораторная работа №8

## Overview

Лабораторная работа №8 по дисциплине «Программирование мобильных и встраиваемых систем».  
Реализация мобильного приложения с использованием **Flutter SDK** (кроссплатформенное приложение для Android/iOS).

Отчёт: [docs/lab8_report.md](docs/lab8_report.md)

---

## Задания

| Задание | Описание | Папка |
|---------|----------|-------|
| Задание 1 | Изучение Flutter SDK и примеров | — |
| Задание 2 | Игра «Угадай число» на Flutter | `src/guess_number/` |
| Задание 3 | Автобус Гродненской области — Вариант 15 (SQLite + карта) | `src/bus_tickets/` |

---

## Usage

Чтобы запустить проекты из репозитория, склонируйте репозиторий и следуйте инструкциям ниже.

### Требования

- Flutter SDK (stable) — [flutter.dev](https://flutter.dev)
- Android Studio + Android SDK (для сборки APK)
- Dart SDK (входит в состав Flutter)

### Запуск Задания 2 — «Угадай число»

```bash
cd src/guess_number
flutter pub get
flutter run
```

### Сборка APK

```bash
cd src/guess_number
flutter build apk --release
# APK: build/app/outputs/flutter-apk/app-release.apk
```

### Запуск тестов

```bash
cd src/guess_number
flutter test

# Интеграционные тесты (требуется подключённый эмулятор или устройство)
flutter test integration_test/app_test.dart
```

### Запуск Задания 3 — «Автобус Гродненской области» (Вариант 15)

```bash
cd src/bus_tickets
flutter pub get
flutter gen-l10n
# ⚠️ Вставьте ключ OpenWeatherMap в lib/services/weather_service.dart
flutter run
```

### Тесты Задания 3

```bash
cd src/bus_tickets
flutter test
flutter test integration_test/app_test.dart
```

---

## Author

**Студент:** Шибитов Николай  
**Группа:** 11  
**Предмет:** Программирование мобильных и встраиваемых систем (ПМиВС)

---

## Additional Notes

Репозиторий: учебный каталог `students/Шибитов/ПМИВС/lab8`

Проекты реализованы на Flutter (Android-таргет) без использования Aurora SDK,  
что допускается преподавателем согласно условию лабораторной работы.

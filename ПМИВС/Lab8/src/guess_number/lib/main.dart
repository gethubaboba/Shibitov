import 'package:flutter/material.dart';
import 'package:flutter_localizations/flutter_localizations.dart';
import 'package:provider/provider.dart';

// Импортируем сгенерированный файл локализации.
// Он создаётся командой `flutter gen-l10n` или автоматически при flutter run/build.
import 'package:guess_number/l10n/app_localizations.dart';

import 'providers/game_provider.dart';
import 'screens/game_screen.dart';

void main() {
  // Гарантируем инициализацию Flutter-движка перед async-кодом.
  WidgetsFlutterBinding.ensureInitialized();
  runApp(const GuessNumberApp());
}

/// Корневой виджет приложения.
///
/// Оборачивает дерево в ChangeNotifierProvider<GameProvider>,
/// что делает провайдер доступным для любого виджета через context.
class GuessNumberApp extends StatefulWidget {
  const GuessNumberApp({super.key});

  @override
  State<GuessNumberApp> createState() => _GuessNumberAppState();
}

class _GuessNumberAppState extends State<GuessNumberApp> {
  // Текущая локаль приложения; можно менять через Settings-экран.
  Locale _locale = const Locale('ru');

  void _setLocale(Locale locale) {
    setState(() => _locale = locale);
  }

  @override
  Widget build(BuildContext context) {
    return ChangeNotifierProvider(
      // create вызывается один раз — при первом построении дерева.
      create: (_) => GameProvider(),
      child: MaterialApp(
        title: 'Угадай число',
        debugShowCheckedModeBanner: false,

        // ─── Локализация ──────────────────────────────────────────────────
        locale: _locale,
        supportedLocales: AppLocalizations.supportedLocales,
        localizationsDelegates: const [
          AppLocalizations.delegate,
          GlobalMaterialLocalizations.delegate,
          GlobalWidgetsLocalizations.delegate,
          GlobalCupertinoLocalizations.delegate,
        ],

        // ─── Тема ─────────────────────────────────────────────────────────
        theme: ThemeData(
          useMaterial3: true,
          colorScheme: ColorScheme.fromSeed(
            seedColor: const Color(0xFF6750A4),
            brightness: Brightness.light,
          ),
          inputDecorationTheme: const InputDecorationTheme(
            border: OutlineInputBorder(),
            contentPadding: EdgeInsets.symmetric(horizontal: 16, vertical: 12),
          ),
        ),

        // Передаём _setLocale вниз через роут-аргументы или через отдельный
        // провайдер. Здесь для простоты используем именованные роуты.
        onGenerateRoute: (settings) {
          return MaterialPageRoute(
            builder: (context) => GameScreen(onLocaleChange: _setLocale),
          );
        },

        home: GameScreen(onLocaleChange: _setLocale),
      ),
    );
  }
}

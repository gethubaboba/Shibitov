import 'package:flutter/material.dart';
import 'package:flutter_localizations/flutter_localizations.dart';
import 'package:provider/provider.dart';
import 'package:bus_tickets/l10n/app_localizations.dart';

import 'providers/app_provider.dart';
import 'screens/cities_screen.dart';

void main() {
  WidgetsFlutterBinding.ensureInitialized();
  runApp(const BusTicketsApp());
}

/// Корневой виджет приложения «Автобус Гродно».
class BusTicketsApp extends StatefulWidget {
  const BusTicketsApp({super.key});

  @override
  State<BusTicketsApp> createState() => _BusTicketsAppState();
}

class _BusTicketsAppState extends State<BusTicketsApp> {
  Locale _locale = const Locale('ru');

  void _setLocale(Locale locale) => setState(() => _locale = locale);

  @override
  Widget build(BuildContext context) {
    return ChangeNotifierProvider(
      create: (_) => AppProvider(),
      child: MaterialApp(
        title: 'Автобус Гродно',
        debugShowCheckedModeBanner: false,
        locale: _locale,
        supportedLocales: AppLocalizations.supportedLocales,
        localizationsDelegates: const [
          AppLocalizations.delegate,
          GlobalMaterialLocalizations.delegate,
          GlobalWidgetsLocalizations.delegate,
          GlobalCupertinoLocalizations.delegate,
        ],
        theme: ThemeData(
          useMaterial3: true,
          colorScheme: ColorScheme.fromSeed(
            seedColor: const Color(0xFF1565C0), // синий — цвет транспортной тематики
            brightness: Brightness.light,
          ),
        ),
        home: CitiesScreen(onLocaleChange: _setLocale),
      ),
    );
  }
}

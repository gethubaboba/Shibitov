# Ответы на контрольные вопросы — Лабораторная работа №8

**Студент:** Шибитов Николай  
**Группа:** 11  
**Предмет:** Программирование мобильных и встраиваемых систем

---

## 1. Что такое Flutter?

Flutter — это открытый кроссплатформенный UI-фреймворк от Google для разработки мобильных, веб- и десктопных приложений из единой кодовой базы на языке Dart. Особенность — собственный движок рендеринга (Skia/Impeller), который не использует нативные виджеты платформы, а рисует интерфейс сам.

```dart
// Минимальное Flutter-приложение
void main() => runApp(const MyApp());

class MyApp extends StatelessWidget {
  const MyApp({super.key});
  @override
  Widget build(BuildContext context) {
    return const MaterialApp(home: Text('Hello, Flutter!'));
  }
}
```

---

## 2. Что такое Dart и почему он используется во Flutter?

Dart — объектно-ориентированный язык программирования от Google. Причины выбора для Flutter:
- **AOT-компиляция** → высокая производительность в production.
- **JIT-компиляция** → hot reload при разработке.
- **Строгая типизация** с хорошим выводом типов (`var`, `final`).
- **Async/await** и Futures — удобная работа с асинхронными операциями.
- Dart компилируется в нативный ARM/x86 код, а не использует WebView или JavaScript-мост.

```dart
Future<String> fetchData() async {
  await Future.delayed(const Duration(seconds: 1));
  return 'Данные загружены';
}
```

---

## 3. Какие различные типы виджетов существуют во Flutter?

| Тип | Описание | Пример |
|-----|----------|--------|
| **StatelessWidget** | Неизменяемый виджет, состояние не хранит | `Text`, `Icon`, `Padding` |
| **StatefulWidget** | Хранит изменяемое состояние через `State` | `Checkbox`, `TextField` |
| **InheritedWidget** | Передаёт данные вниз по дереву | `Theme`, `MediaQuery` |
| **RenderObjectWidget** | Низкоуровневый, управляет рендерингом | `CustomPaint`, `SliverList` |

Дополнительно по назначению:
- **Layout-виджеты**: `Row`, `Column`, `Stack`, `Expanded`
- **Input-виджеты**: `TextField`, `GestureDetector`, `InkWell`
- **Animation-виджеты**: `AnimatedContainer`, `FadeTransition`, `AnimatedBuilder`

---

## 4. В чём разница между StatelessWidget и StatefulWidget?

**StatelessWidget** — неизменяем: строится один раз по входным параметрам (через конструктор), не хранит внутреннего состояния.

**StatefulWidget** — может изменяться: хранит объект `State`, вызов `setState()` заставляет Flutter перестроить виджет.

```dart
// StatelessWidget — иконка с заданным цветом, не меняется
class ColoredIcon extends StatelessWidget {
  final Color color;
  const ColoredIcon({super.key, required this.color});
  @override
  Widget build(BuildContext context) => Icon(Icons.star, color: color);
}

// StatefulWidget — счётчик, который меняется по нажатию
class Counter extends StatefulWidget {
  const Counter({super.key});
  @override
  State<Counter> createState() => _CounterState();
}

class _CounterState extends State<Counter> {
  int _count = 0;

  @override
  Widget build(BuildContext context) {
    return TextButton(
      onPressed: () => setState(() => _count++),
      child: Text('$_count'),
    );
  }
}
```

**Правило:** если виджет не нуждается в изменении после построения → `StatelessWidget`. Если данные меняются по действиям пользователя или таймеру → `StatefulWidget`.

---

## 5. Для чего нужен ключ (Key) во Flutter?

`Key` — уникальный идентификатор виджета в дереве. Flutter использует ключи, чтобы при перестройке дерева **правильно сопоставить** старые виджеты с новыми и сохранить их состояние.

**Когда нужен:**
- При динамическом добавлении/удалении/перемещении виджетов одного типа в списке.
- При тестировании — для однозначного нахождения виджета: `find.byKey(const Key('myButton'))`.

```dart
// Без Key — Flutter может перепутать состояния двух одинаковых виджетов
ListView(children: [
  MyCard(key: const Key('card_1'), title: 'Карточка 1'),
  MyCard(key: const Key('card_2'), title: 'Карточка 2'),
]);
```

---

## 6. Какие типы ключей используются во Flutter?

| Тип | Назначение |
|-----|-----------|
| `ValueKey<T>` | Идентификатор по значению (`ValueKey(id)`) |
| `ObjectKey` | Идентификатор по объекту (по ссылке) |
| `UniqueKey` | Гарантированно уникальный (создаётся каждый раз) |
| `GlobalKey` | Глобальный — даёт доступ к `State` виджета из любого места |
| `PageStorageKey` | Для сохранения позиции прокрутки в `ListView`/`PageView` |

```dart
// GlobalKey — доступ к форме из любого места
final _formKey = GlobalKey<FormState>();

// Валидация через GlobalKey
_formKey.currentState?.validate();
```

---

## 7. В чём разница между MaterialApp и WidgetsApp?

`WidgetsApp` — базовый класс, предоставляет только фундаментальную функциональность: навигацию, локализацию, жизненный цикл приложения. Не имеет Material Design компонентов.

`MaterialApp` — надстройка над `WidgetsApp`, добавляет:
- **Material Design** тему и виджеты (`Scaffold`, `AppBar`, `FloatingActionButton` и др.)
- Шрифт по умолчанию (Roboto)
- `showDialog`, `showSnackBar` и другие Material-паттерны

```dart
// MaterialApp — стандартный выбор для Android-приложений
MaterialApp(
  theme: ThemeData(colorScheme: ColorScheme.fromSeed(seedColor: Colors.blue)),
  home: const MyHomePage(),
)

// CupertinoApp — для iOS-стиля (аналог MaterialApp)
CupertinoApp(home: const MyPage())
```

---

## 8. Перечислите методы оптимизации приложений Flutter

1. **`const` конструкторы** — неизменяемые виджеты не перестраиваются.
2. **`Consumer` / `Selector`** (Provider) — перерисовываем только нужную часть дерева.
3. **`ListView.builder`** вместо `ListView` — ленивый рендеринг длинных списков.
4. **`RepaintBoundary`** — изолирует часть дерева от перерисовки.
5. **Кеширование изображений** — `cached_network_image`.
6. **`AutomaticKeepAliveClientMixin`** — сохраняет состояние вкладки при переключении.
7. **Разбиение на мелкие виджеты** — `build()` вызывается только для изменённого поддерева.
8. **`compute()`** — вычисления в изоляте, не блокируют UI-поток.
9. **Профилирование** — Flutter DevTools → Performance, Memory.

```dart
// Плохо: перестраивается весь экран
class BadList extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return Column(children: List.generate(1000, (i) => Text('$i')));
  }
}

// Хорошо: ленивая загрузка
ListView.builder(
  itemCount: 1000,
  itemBuilder: (context, i) => Text('$i'),
)
```

---

## 9. В чём разница между Navigator и Router во Flutter?

**Navigator** — императивный API навигации. Управляет стеком экранов (push/pop). Простой и понятный для небольших приложений.

**Router** (GoRouter, auto_route и пр.) — декларативный API. Использует `RouteInformation` и `RouterDelegate`. Поддерживает deep linking, URL-based routing, web. Рекомендован для сложных приложений.

```dart
// Navigator — императивный, простой
Navigator.push(
  context,
  MaterialPageRoute(builder: (_) => const DetailScreen()),
);

// GoRouter — декларативный
final router = GoRouter(routes: [
  GoRoute(path: '/', builder: (_, __) => const HomeScreen()),
  GoRoute(path: '/detail', builder: (_, __) => const DetailScreen()),
]);
```

**Когда что использовать:**
- `Navigator` — учебные проекты, 2-5 экранов, нет deep links.
- `Router` — продакшн-приложения, много экранов, web, deep linking.

---

## 10. Что такое State во Flutter?

`State` — объект, содержащий изменяемые данные виджета. Живёт дольше самого виджета (виджет может пересоздаваться, State — нет).

```dart
class _CounterState extends State<Counter> {
  int _count = 0; // ← это State

  @override
  Widget build(BuildContext context) => Text('$_count');

  // setState перестраивает дерево виджетов, связанное с этим State.
  void increment() => setState(() => _count++);
}
```

**Жизненный цикл State:**
`initState()` → `build()` → `didUpdateWidget()` → `setState()` → `build()` → ... → `dispose()`

---

## 11. Для чего используется параметр BuildContext в методе build()?

`BuildContext` — ссылка на позицию виджета в дереве виджетов. Через него виджет:
- Находит данные предков (`Theme.of(context)`, `MediaQuery.of(context)`).
- Получает провайдеры (`context.read<T>()`, `Provider.of<T>(context)`).
- Открывает диалоги и навигирует (`Navigator.of(context).push(...)`).

```dart
@override
Widget build(BuildContext context) {
  // Берём тему из ближайшего MaterialApp-предка.
  final color = Theme.of(context).colorScheme.primary;

  // Получаем провайдер из дерева.
  final gp = context.read<GameProvider>();

  // Навигация.
  Navigator.of(context).push(MaterialPageRoute(builder: (_) => DetailScreen()));

  return Text('Hello', style: TextStyle(color: color));
}
```

**Нюанс:** нельзя использовать `context` после `dispose()` — виджет уже удалён из дерева.

---

## 12. В чём разница между методами push и pushReplacement?

| Метод | Поведение | Когда использовать |
|-------|-----------|-------------------|
| `push` | Добавляет экран **поверх** стека. Можно вернуться назад (← Back). | Переход к экрану с возвратом |
| `pushReplacement` | **Заменяет** текущий экран. Назад вернуться нельзя. | Login → Home (не должен возвращать на Login) |
| `pushAndRemoveUntil` | Заменяет текущий и **чистит** стек до условия. | Выход из приложения, очистка стека |

```dart
// push — добавляет экран в стек
Navigator.push(context, MaterialPageRoute(builder: (_) => const DetailScreen()));

// pushReplacement — заменяет текущий экран (Login → Home)
Navigator.pushReplacement(
  context,
  MaterialPageRoute(builder: (_) => const HomeScreen()),
);

// pushAndRemoveUntil — переход на Home с очисткой всего стека
Navigator.pushAndRemoveUntil(
  context,
  MaterialPageRoute(builder: (_) => const HomeScreen()),
  (route) => false, // убираем ВСЕ предыдущие маршруты
);
```

---

## 13. Для чего нужен инспектор виджетов во Flutter?

**Widget Inspector** (Flutter DevTools → Inspector) — инструмент визуальной отладки:
- Показывает **дерево виджетов** в реальном времени.
- Позволяет выбрать любой виджет на экране и увидеть его свойства (размер, отступы, тему).
- Помогает находить лишние `rebuild`-вызовы через подсветку перерисовок.
- Режим **Highlight Repaints** — выделяет перерисовываемые области.
- Режим **Slow Animations** — замедляет анимации для отладки.

---

## 14. Для чего нужен виджет MediaQuery?

`MediaQuery` — `InheritedWidget`, предоставляющий информацию об экране и системных настройках устройства.

```dart
@override
Widget build(BuildContext context) {
  final mq = MediaQuery.of(context);

  final screenWidth = mq.size.width;           // ширина экрана
  final screenHeight = mq.size.height;         // высота экрана
  final devicePixelRatio = mq.devicePixelRatio; // плотность пикселей
  final textScaleFactor = mq.textScaler;       // масштаб текста (accessibility)
  final bottomInset = mq.viewInsets.bottom;    // высота клавиатуры

  // Пример: прячем виджет при открытой клавиатуре
  return bottomInset > 0 ? const SizedBox.shrink() : const MyWidget();
}
```

**Практическое применение:**
- Адаптивный layout: разные layouts для телефона и планшета.
- Учёт клавиатуры: `padding: EdgeInsets.only(bottom: MediaQuery.of(context).viewInsets.bottom)`.

---

## 15. Для чего нужен виджет SafeArea?

`SafeArea` — автоматически добавляет отступы от «небезопасных» зон экрана: вырезы (notch), закруглённые углы, системные панели навигации/статуса.

```dart
Scaffold(
  body: SafeArea(
    // Контент будет расположен внутри «безопасной» зоны.
    // На iPhone с «чёлкой» добавится отступ сверху,
    // на Android — отступ снизу от навигационных кнопок.
    child: Column(
      children: [
        const Text('Заголовок'),
        // ... остальной контент
      ],
    ),
  ),
)
```

**Без `SafeArea`:** контент может перекрываться системными элементами (status bar, home indicator).  
**С `SafeArea`:** Flutter автоматически запрашивает безопасные отступы через `MediaQuery.of(context).padding`.

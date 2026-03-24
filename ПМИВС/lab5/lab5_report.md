# Лабораторная работа №5. Мультисенсорное взаимодействие в мобильных интерфейсах

**Студент:** Шибитов Николай
**Группа:** 11
**Предмет:** Программирование мобильных и встраиваемых систем (ПМиВС)

---

## Цели работы

- Разработать приложения для демонстрации распознавания стандартных жестов
- Разработать приложение с демонстрацией определения местоположения

---

## Задание 1. Распознавание всех поддерживаемых жестов

**Проект:** `Lab_5_Task_1_Shibitov_Nikolay`
**Пакет:** `com.example.lab_5_task_1_shibitov_nikolay`

Разработано приложение, демонстрирующее распознавание всех поддерживаемых стандартных жестов с помощью класса `GestureDetector`.

Класс активности реализует интерфейсы:
- `GestureDetector.OnGestureListener` — для прослушивания базовых жестов
- `GestureDetector.OnDoubleTapListener` — для прослушивания жеста двойного касания

**Распознаваемые жесты:**

| Метод | Описание |
|---|---|
| `onDown` | Начало касания экрана |
| `onShowPress` | Удержание без движения |
| `onSingleTapUp` | Одиночное касание (кнопка отпущена) |
| `onSingleTapConfirmed` | Подтверждённое одиночное касание |
| `onDoubleTap` | Двойное касание |
| `onDoubleTapEvent` | Событие внутри двойного касания |
| `onLongPress` | Длительное нажатие |
| `onScroll` | Прокрутка (drag) |
| `onFling` | Смахивание (бросок) |

### Ключевые фрагменты кода

```java
public class MainActivity extends AppCompatActivity
        implements GestureDetector.OnGestureListener,
        GestureDetector.OnDoubleTapListener {

    private GestureDetectorCompat mDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mDetector = new GestureDetectorCompat(this, this);
        mDetector.setOnDoubleTapListener(this);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2,
                           float velocityX, float velocityY) {
        appendGesture("onFling: vX=" + String.format("%.1f", velocityX)
                + ", vY=" + String.format("%.1f", velocityY));
        return true;
    }
    // ... остальные методы аналогично
}
```

---

## Задание 2. Жесты в Jetpack Compose

**Проект:** `Lab_5_Task_2_Shibitov_Nikolay`
**Пакет:** `com.example.lab_5_task_2_shibitov_nikolay`

Разработано демонстрационное приложение на Kotlin + Jetpack Compose, демонстрирующее обработку жестов средствами Compose.

### Реализованные жесты

| Зона | Жест | API |
|---|---|---|
| Синяя зона | Tap, Double Tap, Long Press, Press | `detectTapGestures` |
| Зелёная зона | Drag (перетаскивание) | `detectDragGestures` |
| Жёлтая зона | Pinch (масштаб), Rotate (поворот) | `detectTransformGestures` |

### Ключевые фрагменты кода

```kotlin
Box(
    modifier = Modifier
        .pointerInput(Unit) {
            detectTapGestures(
                onTap = { log("onTap: ${it.x.toInt()},${it.y.toInt()}") },
                onDoubleTap = { log("onDoubleTap") },
                onLongPress = { log("onLongPress") }
            )
        }
)

Box(
    modifier = Modifier
        .pointerInput(Unit) {
            detectTransformGestures { _, _, zoom, rotationChange ->
                scale *= zoom
                rotation += rotationChange
            }
        }
)
```

---

## Задание 3. Угадай число — жестовый ввод цифр

**Проект:** `Lab_5_Task_3_Shibitov_Nikolay`
**Пакет:** `com.example.lab_5_task_3_shibitov_nikolay`

Реализовано приложение «Угадай число» с жестовым вводом цифр 0–9 и специальным жестом «submit» для подтверждения числа. Используется `GestureOverlayView` и библиотека жестов (`res/raw/gestures`).

### Жесты

- **0–9** — ввод цифр числа
- **submit** — подтверждение числа и проверка

### Ключевые фрагменты кода

```java
gestureLibrary = GestureLibraries.fromRawResource(this, R.raw.gestures);
gestureLibrary.load();

GestureOverlayView overlay = findViewById(R.id.gestureOverlay);
overlay.addOnGesturePerformedListener(this);

@Override
public void onGesturePerformed(GestureOverlayView overlay, Gesture gesture) {
    ArrayList<Prediction> predictions = gestureLibrary.recognize(gesture);
    String name = predictions.get(0).name;
    if (name.matches("[0-9]")) {
        inputBuffer.append(name);
    } else if (name.equals("submit")) {
        checkGuess();
    }
}
```

---

## Задание 4. Калькулятор-конвертер площади с жестовым вводом

**Проект:** `Lab_5_Task_4_Shibitov_Nikolay`
**Пакет:** `com.example.lab_5_task_4_shibitov_nikolay`
**Основа:** приложение из Лабораторной работы №2, Задание 3 (Вариант 16) — конвертер площади (м² ↔ in², ft², yd², acre, mi²).

В приложение из лаб. 2 добавлен жестовый ввод с помощью `GestureOverlayView`, обёртывающего весь экран (`eventsInterceptionEnabled="false"` — кнопки продолжают работать). Кнопка «Жесты: ВЫКЛ/ВКЛ» переключает режим.

### Интерфейс (сохранён из Lab 2)

- Дисплей ввода (`tvDisplay`, 32sp, gravity=end)
- Строка результата (`tvResult`)
- Два `Spinner` для выбора единиц (из: m², cm², km², ha → в: in², ft², yd², acre, mi²)
- `GridLayout` 4×5 с кнопками 0–9, `C`, `⌫`, `±`, `.`, `=`
- Кнопка переключения языка (EN/RU)

### Добавленные компоненты (Lab 5)

- `GestureOverlayView` как корневой элемент разметки (прозрачный слой)
- Кнопка «Жесты: ВЫКЛ» — включает/выключает распознавание жестов
- Подсказка `tvGestureHint` (скрыта по умолчанию)

### Поддерживаемые жесты

| Жест | Действие |
|---|---|
| `0`–`9` | Ввод цифры |
| `dot` | Десятичная точка |
| `clear` | Сброс (= кнопка C) |
| `backspace` | Удалить последний символ (= ⌫) |
| `plus_minus` | Смена знака (= ±) |
| `equals` | Конвертировать (= =) |

### Ключевые фрагменты кода

```kotlin
// Инициализация
gestureLibrary = GestureLibraries.fromRawResource(this, R.raw.gestures)
gestureLibrary.load()
val overlay = findViewById<GestureOverlayView>(R.id.gestureOverlay)
overlay.addOnGesturePerformedListener(this)
overlay.isEnabled = false  // по умолчанию выключено

// Переключение режима
btnGestureMode.setOnClickListener {
    gestureMode = !gestureMode
    overlay.isEnabled = gestureMode
}

// Обработка жеста
override fun onGesturePerformed(overlay: GestureOverlayView, gesture: Gesture) {
    val predictions = gestureLibrary.recognize(gesture)
    if (predictions.isEmpty() || predictions[0].score < 1.0) return
    applyInput(predictions[0].name)
}

private fun applyInput(name: String) {
    when (name) {
        "0","1","2","3","4","5","6","7","8","9" ->
            inputString = if (inputString == "0") name else inputString + name
        "dot"       -> if (!inputString.contains(".")) inputString += "."
        "clear"     -> { inputString = "0"; tvResult.text = "" }
        "backspace" -> inputString = inputString.dropLast(1).ifEmpty { "0" }
        "plus_minus"-> inputString = if (inputString.startsWith("-"))
                           inputString.drop(1) else "-$inputString"
        "equals"    -> calculateResult()
    }
    updateDisplay()
}
```

---

## Задание 5. Блокнот с рукописным вводом (Kotlin)

**Проект:** `Lab_5_Task_5_Shibitov_Nikolay`
**Пакет:** `com.example.lab_5_task_5_shibitov_nikolay`

Блокнот на Kotlin с возможностью рукописного ввода текста. Реализован кастомный `View` — `DrawingView`, работающий с `Canvas` и `Path`. Поддерживаются выбор цвета, толщина линии через `SeekBar`, отмена последнего штриха и очистка холста.

### Функциональность

- Рисование пальцем по экрану (штрихи через `Path`)
- Кнопки выбора цвета: чёрный, красный, синий, зелёный
- Кнопка «Ластик» (белый цвет)
- Кнопка «Отмена» (undo последнего штриха)
- Кнопка «Очистить» (clear canvas)
- `SeekBar` для регулировки толщины линии

### Ключевые фрагменты кода

```kotlin
class DrawingView(context: Context, attrs: AttributeSet?) : View(context, attrs) {
    private val paths = mutableListOf<Pair<Path, Paint>>()

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> currentPath.moveTo(event.x, event.y)
            MotionEvent.ACTION_MOVE -> { currentPath.lineTo(event.x, event.y); invalidate() }
            MotionEvent.ACTION_UP   -> { paths.add(currentPath to currentPaint); invalidate() }
        }
        return true
    }

    override fun onDraw(canvas: Canvas) {
        canvas.drawColor(Color.WHITE)
        for ((path, paint) in paths) canvas.drawPath(path, paint)
    }
}
```

---

## Задание 6. RunTracker — приложение отслеживания местоположения (Java)

**Проект:** `Lab_5_Task_6_Shibitov_Nikolay`
**Пакет:** `com.example.lab_5_task_6_shibitov_nikolay`

Приложение позволяет отслеживать текущее местоположение устройства с помощью GPS и сотовых вышек. Интерфейс построен на `ConstraintLayout`.

### Функциональность

- Кнопка **Старт** — запускает отслеживание местоположения
- Кнопка **Стоп** — останавливает отслеживание
- Отображаемые данные: широта, долгота, высота, точность, скорость, провайдер, время последнего обновления
- Запрос разрешения `ACCESS_FINE_LOCATION` в рантайме (API 23+)
- Поддержка провайдеров `GPS_PROVIDER` и `NETWORK_PROVIDER`
- Корректная работа с жизненным циклом Activity (`onPause`/`onResume`)

### AndroidManifest.xml

```xml
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
```

### Ключевые фрагменты кода

```java
locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

locationListener = new LocationListener() {
    @Override
    public void onLocationChanged(@NonNull Location location) {
        updateUI(location);
    }
};

// Запуск отслеживания
locationManager.requestLocationUpdates(
        LocationManager.GPS_PROVIDER, 0, 0, locationListener);
```

```java
private void updateUI(Location location) {
    tvLatitude.setText(String.format(Locale.getDefault(),
            "%.6f°", location.getLatitude()));
    tvLongitude.setText(String.format(Locale.getDefault(),
            "%.6f°", location.getLongitude()));
    tvSpeed.setText(String.format(Locale.getDefault(),
            "%.1f км/ч", location.getSpeed() * 3.6f));
    // ...
}
```

---

## Задание 7. RunTracker + GPX-маршрут

**Проект:** `Lab_5_Task_7_Shibitov_Nikolay`
**Пакет:** `com.example.lab_5_task_7_shibitov_nikolay`

Расширение задания 6: при нажатии «Стоп» собранный маршрут сохраняется в GPX-файл (`res/raw/gestures` не используется, только стандартный `LocationManager`). GPX — стандартный XML-формат для GPS-данных (широта, долгота, высота, время).

### Алгоритм сохранения GPX

1. При отслеживании каждая точка `Location` добавляется в список `trackPoints`.
2. При остановке вызывается `saveGpx()`, формирующий XML по стандарту GPX 1.1.
3. Файл сохраняется в `getExternalFilesDir(DIRECTORY_DOCUMENTS)`.

### Ключевые фрагменты кода

```java
// Структура GPX 1.1
writer.write("<gpx version=\"1.1\" creator=\"RunTracker\">\n");
writer.write("  <trk><trkseg>\n");
for (Location loc : trackPoints) {
    writer.write(String.format(Locale.US,
        "<trkpt lat=\"%.6f\" lon=\"%.6f\">\n", loc.getLatitude(), loc.getLongitude()));
    writer.write(String.format("<ele>%.1f</ele>\n", loc.getAltitude()));
    writer.write("<time>" + isoTime(loc.getTime()) + "</time>\n");
    writer.write("</trkpt>\n");
}
writer.write("  </trkseg></trk>\n</gpx>");
```

---

## Задание 8. RunTracker — TableLayout + PendingIntent API

**Проект:** `Lab_5_Task_8_Shibitov_Nikolay`
**Пакет:** `com.example.lab_5_task_8_shibitov_nikolay`

Реализация RunTracker с двумя отличиями от задания 6:
1. **Разметка**: `TableLayout` (5 строк `TableRow`) + `LinearLayout` с двумя кнопками.
2. **Получение локации**: вместо `LocationListener` используется `PendingIntent` API.

### Архитектура PendingIntent

- `PendingIntent.getBroadcast(...)` оборачивает `Intent` с action `LOCATION_UPDATE`.
- `LocationManager.requestLocationUpdates(provider, time, dist, pendingIntent)` — доставляет обновления через broadcast.
- `BroadcastReceiver` регистрируется в `startTracking()` и принимает `KEY_LOCATION_CHANGED`.

### Ключевые фрагменты кода

```java
Intent locationIntent = new Intent(ACTION_LOCATION_UPDATE);
locationPendingIntent = PendingIntent.getBroadcast(this, 0, locationIntent,
        PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE);

// Регистрация приёмника и запуск
registerReceiver(locationReceiver, new IntentFilter(ACTION_LOCATION_UPDATE),
        Context.RECEIVER_NOT_EXPORTED);
locationManager.requestLocationUpdates(
        LocationManager.GPS_PROVIDER, 1000, 1, locationPendingIntent);

// BroadcastReceiver
@Override
public void onReceive(Context context, Intent intent) {
    Location location = intent.getParcelableExtra(LocationManager.KEY_LOCATION_CHANGED);
    if (location != null) updateUI(location);
}
```

### Разметка TableLayout

```xml
<TableLayout android:stretchColumns="1">
    <TableRow>
        <TextView android:text="Широта:" android:textStyle="bold" />
        <TextView android:id="@+id/tvLatitude" android:text="—" />
    </TableRow>
    <!-- ... 4 аналогичные строки ... -->
</TableLayout>
<LinearLayout android:orientation="horizontal">
    <Button android:id="@+id/btnStart" android:text="Старт" />
    <Button android:id="@+id/btnStop"  android:text="Стоп"  />
</LinearLayout>
```

---

## Задание 9. Погодное приложение (Java, OpenWeatherMap API)

**Проект:** `Lab_5_Task_9_Shibitov_Nikolay`
**Пакет:** `com.example.lab_5_task_9_shibitov_nikolay`
**API:** OpenWeatherMap (`api.openweathermap.org/data/2.5/`)

### Встроенные города

| № | Город | Основание |
|---|---|---|
| 1 | **Perm** | Областной город Пермского края |
| 2 | **Prague** | Город на букву «П» (первая буква фамилии Петраков) |

### Функциональность

- **a)** Список городов с текущей температурой (`RecyclerView` с карточками)
- **b)** Подробная информация по городу (отдельный экран):
  - Температура, ощущаемая температура, описание погоды
  - Влажность, давление, скорость ветра, видимость
- **c)** Прогноз до 7 дней (`/forecast` API, одна запись на день)
- Добавление своего города через FAB → диалог ввода
- Долгое нажатие на город — удаление (пользовательские; встроенные защищены)
- Сохранение добавленных городов в `SharedPreferences`

### Архитектура

```
MainActivity       — список городов + температуры (RecyclerView + FAB)
DetailActivity     — детали + прогноз
WeatherApiHelper   — HTTP-запросы к OpenWeatherMap (HttpURLConnection)
CityAdapter        — RecyclerView.Adapter
WeatherData        — POJO для текущей погоды и прогноза
```

Сетевые запросы выполняются в `ExecutorService` (3 потока), результат доставляется в UI через `Handler(Looper.getMainLooper())`.

### Ключевые фрагменты кода

```java
// Текущая погода
String urlStr = "https://api.openweathermap.org/data/2.5/weather?q=" + city
        + "&appid=" + API_KEY + "&units=metric&lang=ru";

// Прогноз (5 дней, каждые 3 часа; берём первую запись нового дня)
String urlStr = "https://api.openweathermap.org/data/2.5/forecast?q=" + city
        + "&appid=" + API_KEY + "&units=metric&lang=ru&cnt=24";

// Фоновый поток + обновление UI
executor.execute(() -> {
    WeatherData data = WeatherApiHelper.fetchCurrentWeather(city);
    handler.post(() -> tvTemp.setText(String.format("%+.1f°C", data.temp)));
});
```

---

## Контрольные вопросы

**1. Вызов какого метода инициируется при появлении сенсорного события? При каком условии возможна обработка жеста?**

При появлении сенсорного события инициируется вызов метода `onTouchEvent(MotionEvent event)`. Для обработки жеста необходимо передать событие в `GestureDetector`: `mDetector.onTouchEvent(event)`. Метод `onDown()` должен возвращать `true`, чтобы детектор мог получать последующие события жеста.

**2. Какой класс позволяет распознавать стандартные жесты без обработки отдельных сенсорных событий?**

Класс `GestureDetector` (или `GestureDetectorCompat` из AndroidX).

**3. Перечислите методы, отвечающие за прослушивание сенсорных событий.**

Интерфейс `OnGestureListener`: `onDown`, `onShowPress`, `onSingleTapUp`, `onScroll`, `onLongPress`, `onFling`.
Интерфейс `OnDoubleTapListener`: `onSingleTapConfirmed`, `onDoubleTap`, `onDoubleTapEvent`.

**4. С помощью какого приложения можно создавать свои жесты?**

С помощью приложения **Gesture Builder** (доступного в Google Play или предустановленного в некоторых образах AVD).

**5. Какой элемент требуется добавить в XML-файл активности для распознавания кастомных жестов? Какие способы его добавления?**

Элемент `GestureOverlayView`. Два способа добавления: как обычный компонент интерфейса (занимает часть экрана) или как прозрачный слой поверх всех компонентов.

**6. Какой интерфейс должен реализовывать класс активности при обработке кастомных жестов?**

Интерфейс `GestureOverlayView.OnGesturePerformedListener` с методом `onGesturePerformed(GestureOverlayView overlay, Gesture gesture)`.

**7. Какой интерфейс используется для получения уведомлений от LocationManager, когда местоположение изменилось?**

Интерфейс `LocationListener` с методом `onLocationChanged(Location location)`.

**8. Как расшифровывается аббревиатура NMEA? И для чего применяется?**

NMEA — National Marine Electronics Association. Стандарт передачи навигационных данных между электронными устройствами. Применяется для передачи данных от GPS-приёмников (координаты, скорость, курс и др.) в текстовом формате.

**9. Как расшифровывается аббревиатура GNSS?**

GNSS — Global Navigation Satellite System (Глобальная навигационная спутниковая система). Общее название для систем GPS, ГЛОНАСС, Galileo, BeiDou и др.

**10. Расшифруйте аббревиатуры GPX, KML. Для каких задач применяются?**

- **GPX** (GPS Exchange Format) — XML-формат для обмена GPS-данными: треки, маршруты, точки. Используется в навигаторах, фитнес-трекерах, туристических приложениях.
- **KML** (Keyhole Markup Language) — XML-формат для геопространственных данных, разработанный для Google Earth. Используется для хранения точек, линий, полигонов на карте.

**11. Когда рекомендуется применять PendingIntent API вместо LocationListener?**

PendingIntent API рекомендуется применять когда необходимо получать обновления местоположения независимо от состояния UI и даже когда приложение свёрнуто или его процесс завершён. LocationListener подходит только когда данные нужны одному активному компоненту.

**12. Как называется класс данных, использующийся для представления географического местоположения?**

Класс `android.location.Location`.

**13. Какими данными описывается местоположение?**

Широта (`latitude`), долгота (`longitude`), высота над уровнем моря (`altitude`), точность (`accuracy`), скорость (`speed`), направление (`bearing`), время (`time`), провайдер (`provider`).

**14. Какие строки необходимо добавить в AndroidManifest.xml для доступа к интернету?**

```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
```

**15. Какие концепции реализации многопоточности в Android можем использовать?**

`Thread`, `Handler`/`Looper`, `AsyncTask` (устарел), `Executors`, `WorkManager`, Kotlin Coroutines, RxJava.

**16. Для каких основных компонент может применяться асинхронная обработка?**

- **Activity/Fragment** — Coroutines с `lifecycleScope`, `Handler`, `AsyncTask`
- **Service** — `IntentService`, `JobIntentService`, потоки
- **BroadcastReceiver** — `goAsync()` для асинхронных операций
- **Общие подходы** — `Executors`, `ThreadPoolExecutor`, `WorkManager` для фоновых задач с ограничениями

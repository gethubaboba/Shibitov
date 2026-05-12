# Лабораторная работа №4
## Тестирование Android-приложений

**Студент:** Шибитов Николай
**Группа:** 11
**Курс:** 3
**Предмет:** Программирование мобильных и встраиваемых систем (ПМИВС)
**Язык:** Java

---

## Приложения

| Приложение | Пакет |
|---|---|
| Калькулятор | `com.example.lab_4_calculator_shibitov_nikolay` |
| Угадай число | `com.example.lab_4_guessnumber_shibitov_nikolay` |

---

## Задание 1. Изучение примера тестирования

Изучен пример проекта. Проекты созданы по аналогичной структуре:
- Бизнес-логика (`Calculator.java`, `NumberGame.java`) — чистые Java-классы без зависимостей на Android SDK.
- UI-слой (`MainActivity.java`) — взаимодействует с View, использует ViewModel для GuessNumber.
- Интерфейс `NumberGenerator` — позволяет изолировать генератор случайных чисел в тестах через Mockito.

Модульные тесты (`src/test/`) — запускаются на JVM (без эмулятора).
Инструментальные тесты (`src/androidTest/`) — запускаются на устройстве/эмуляторе через Espresso.

---

## Задание 2. Unit-тесты бизнес-логики калькулятора

**Файл:** `Lab_4_Calculator_Shibitov_Nikolay/app/src/test/java/.../CalculatorUnitTest.java`

Написано 21 unit-тест методами JUnit 4. Каждый тест использует `@Before` для инициализации объекта `Calculator`.

```java
@Before
public void setUp() {
    calc = new Calculator();
}

@Test
public void add_positive() { assertEquals(5.0, calc.add(2, 3), DELTA); }

@Test
public void div_byZero_throws() {
    assertThrows(ArithmeticException.class, () -> calc.div(5, 0));
}

@Test
public void evaluate_unknownOp_throws() {
    assertThrows(IllegalArgumentException.class,
            () -> calc.evaluate(1, "^", 2));
}
```

### Результаты

| Тест | Операция | Результат |
|---|---|---|
| add_positive | 2 + 3 | ✅ PASS |
| add_negative | -3 + 2 | ✅ PASS |
| add_zeros | 0 + 0 | ✅ PASS |
| add_largeNumbers | 1e15 + 1e15 | ✅ PASS |
| sub_positive | 3 - 2 | ✅ PASS |
| sub_negative | -2 - 3 | ✅ PASS |
| sub_sameValues | 7 - 7 | ✅ PASS |
| mul_positive | 2 × 3 | ✅ PASS |
| mul_byZero | 99 × 0 | ✅ PASS |
| mul_negatives | (-2) × (-3) | ✅ PASS |
| mul_fraction | 0.25 × 2 | ✅ PASS |
| div_normal | 6 ÷ 3 | ✅ PASS |
| div_fraction | 1 ÷ 2 | ✅ PASS |
| div_negatives | (-6) ÷ 2 | ✅ PASS |
| div_byZero_throws | 5 ÷ 0 | ✅ PASS |
| percentage_100 | 100% | ✅ PASS |
| percentage_50 | 50% | ✅ PASS |
| negate_positive | negate(5) | ✅ PASS |
| negate_negative | negate(-3) | ✅ PASS |
| negate_zero | negate(0) | ✅ PASS |
| evaluate_unknownOp_throws | evaluate(1,"^",2) | ✅ PASS |

**Итого: 21/21 PASSED**

---

## Задание 3. Espresso UI-тесты калькулятора

**Файл:** `Lab_4_Calculator_Shibitov_Nikolay/app/src/androidTest/java/.../CalculatorUITest.java`

Написаны 9 Espresso-тестов, проверяющих реальное взаимодействие с UI на устройстве/эмуляторе.

```java
@Rule
public ActivityScenarioRule<MainActivity> activityRule =
        new ActivityScenarioRule<>(MainActivity.class);

@Test
public void testUi_SimpleAddition() {
    onView(withId(R.id.btn_2)).perform(click());
    onView(withId(R.id.btn_plus)).perform(click());
    onView(withId(R.id.btn_3)).perform(click());
    onView(withId(R.id.btn_equals)).perform(click());
    onView(withId(R.id.tv_result)).check(matches(withText("5")));
}
```

### Результаты

| Тест | Сценарий | Результат |
|---|---|---|
| testUi_SimpleAddition | 2 + 3 = 5 | ✅ PASS |
| testUi_Subtraction | 9 - 4 = 5 | ✅ PASS |
| testUi_Multiplication | 3 × 4 = 12 | ✅ PASS |
| testUi_Division | 8 ÷ 2 = 4 | ✅ PASS |
| testUi_DivisionByZero_ShowsError | 5 ÷ 0 → ошибка | ✅ PASS |
| testUi_Clear_ResetsDisplay | AC → "0" | ✅ PASS |
| testUi_Backspace_RemovesLastDigit | "123" → ⌫ → "12" | ✅ PASS |
| testUi_Negate | 5 → +/- → "-5" | ✅ PASS |
| testUi_Percent | 50 → % → "0.5" | ✅ PASS |

**Итого: 9/9 PASSED**

---

## Задание 4. Сохранение состояния при повороте экрана

**Файл:** `CalculatorUITest.java` (тесты `testUi_Rotation*`)
**Механизм:** `onSaveInstanceState` / `onCreate(Bundle)` в `MainActivity.java`

```java
// В MainActivity.java:
@Override
protected void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    outState.putString(KEY_EXPRESSION,    expression);
    outState.putString(KEY_INPUT,         currentInput);
    outState.putDouble(KEY_FIRST_OPERAND, firstOperand);
    outState.putString(KEY_OPERATOR,      currentOperator);
    outState.putBoolean(KEY_EXPECT_NEW,   expectNewInput);
}

// В тесте:
@Test
public void testUi_RotationSavesInput() {
    onView(withId(R.id.btn_1)).perform(click());
    onView(withId(R.id.btn_2)).perform(click());
    onView(withId(R.id.btn_3)).perform(click());
    activityRule.getScenario().recreate();   // имитация поворота
    onView(withId(R.id.tv_expression)).check(matches(withText("123")));
}
```

### Баг-репорт (до исправления)

| Поле | Значение |
|---|---|
| ID | BUG-001 |
| Компонент | MainActivity — сохранение состояния |
| Шаги | Ввести "42 + ", повернуть экран |
| Ожидаемый результат | Выражение "42 + " сохранено |
| Фактический результат | Поле expression сброшено в "" |
| Причина | `currentInput` сохранялся, `expression` — нет |
| Исправление | Добавлен `KEY_EXPRESSION` в `onSaveInstanceState` |
| Статус | ✅ Исправлен |

### Результаты

| Тест | Результат |
|---|---|
| testUi_RotationSavesInput ("123") | ✅ PASS |
| testUi_RotationSavesFullExpression ("4 + 5") | ✅ PASS |

---

## Задание 5. Unit-тесты с Mockito (игра «Угадай число»)

**Файл:** `Lab_4_GuessNumber_Shibitov_Nikolay/app/src/test/java/.../GameUnitTest.java`

Использованы: `mock()`, `when(...).thenReturn(...)`, `verify(...)`, `spy()`.

```java
@Mock NumberGenerator mockGenerator;

@Before
public void setUp() {
    MockitoAnnotations.openMocks(this);
    when(mockGenerator.generate(anyInt(), anyInt())).thenReturn(42);
    game = new NumberGame(mockGenerator, 1, 100, 7);
}

// Verify: генератор вызван ровно один раз при создании игры
@Test
public void verify_generatorCalledOnce() {
    verify(mockGenerator, times(1)).generate(1, 100);
}

// Spy: реальный объект + проверка вызова
@Test
public void spy_realGeneratorUsed() {
    NumberGenerator realGen = new RandomNumberGenerator();
    NumberGenerator spy     = spy(realGen);
    new NumberGame(spy, 1, 100, 7);
    verify(spy, times(1)).generate(1, 100);
}
```

### Результаты

| Тест | Результат |
|---|---|
| verify_generatorCalledOnce | ✅ PASS |
| guess_correct | ✅ PASS |
| guess_tooLow | ✅ PASS |
| guess_tooHigh | ✅ PASS |
| isGameOver_afterCorrect | ✅ PASS |
| gameOver_afterMaxAttempts | ✅ PASS |
| guess_afterGameOver_returnsGameOver | ✅ PASS |
| guess_nonNumeric | ✅ PASS |
| guess_empty | ✅ PASS |
| guess_outOfRange_low | ✅ PASS |
| guess_outOfRange_high | ✅ PASS |
| attempts_incrementOnValid | ✅ PASS |
| attempts_notIncrementOnInvalid | ✅ PASS |
| spy_realGeneratorUsed | ✅ PASS |
| stub_differentSecretNumber | ✅ PASS |
| guess_atMin | ✅ PASS |
| guess_atMax | ✅ PASS |

**Итого: 17/17 PASSED**

---

## Задание 6. Espresso UI-тесты игры «Угадай число»

**Файл:** `Lab_4_GuessNumber_Shibitov_Nikolay/app/src/androidTest/java/.../GameUITest.java`

```java
@Test
public void testUi_InitialState_GuessButtonEnabled() {
    onView(withId(R.id.btn_guess)).check(matches(isEnabled()));
}

@Test
public void testUi_AttemptsDisplayUpdates() {
    onView(withId(R.id.et_guess)).perform(typeText("50"), closeSoftKeyboard());
    onView(withId(R.id.btn_guess)).perform(click());
    onView(withId(R.id.tv_attempts)).check(matches(withText(containsString("/ 7"))));
}
```

### Результаты

| Тест | Результат |
|---|---|
| testUi_InitialState_GuessButtonEnabled | ✅ PASS |
| testUi_InitialState_NewGameButtonDisabled | ✅ PASS |
| testUi_InvalidInput_ShowsMessage | ✅ PASS |
| testUi_OutOfRangeInput_ShowsMessage | ✅ PASS |
| testUi_AttemptsDisplayUpdates | ✅ PASS |

**Итого: 5/5 PASSED**

---

## Задание 7. Accessibility-тесты (TalkBack)

Accessibility Checks включён через `@BeforeClass` в обоих тест-классах:

```java
@BeforeClass
public static void enableAccessibilityChecks() {
    AccessibilityChecks.enable().setRunChecksFromRootView(true);
}

@Test
public void testUi_Accessibility_GuessButton_HasContentDescription() {
    onView(withId(R.id.btn_guess)).check(matches(
            withContentDescription(R.string.btn_guess)));
}
```

Все интерактивные элементы снабжены `android:contentDescription` в XML-разметке.

### Результаты

| Элемент | contentDescription | Результат |
|---|---|---|
| btn_guess | "Guess" | ✅ PASS |
| btn_new_game | "New Game" | ✅ PASS |
| tv_result | "Game result" | ✅ PASS |
| tv_attempts | "Attempts counter" | ✅ PASS |

**Итого: 4/4 PASSED**

---

## Задание 8. Параметризованные тесты

**Файл:** `ParameterizedGuessTest.java` — 10 наборов входных данных.

```java
@RunWith(Parameterized.class)
public class ParameterizedGuessTest {

    @Parameterized.Parameters(name = "{index}: guess({0}) secret={1} -> {2}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
            { "42",  42, GuessResult.CORRECT      },
            { "10",  42, GuessResult.TOO_LOW       },
            { "99",  42, GuessResult.TOO_HIGH      },
            { "abc", 42, GuessResult.INVALID_INPUT },
            { "",    42, GuessResult.INVALID_INPUT },
            { "0",   42, GuessResult.INVALID_INPUT },
            { "101", 42, GuessResult.INVALID_INPUT },
            { "1",   1,  GuessResult.CORRECT       },
            { "100", 100,GuessResult.CORRECT       },
            { "50",  50, GuessResult.CORRECT       },
        });
    }
```

### Результаты

| # | Ввод | Секрет | Ожидаемый результат | Итог |
|---|---|---|---|---|
| 0 | "42" | 42 | CORRECT | ✅ PASS |
| 1 | "10" | 42 | TOO_LOW | ✅ PASS |
| 2 | "99" | 42 | TOO_HIGH | ✅ PASS |
| 3 | "abc" | 42 | INVALID_INPUT | ✅ PASS |
| 4 | "" | 42 | INVALID_INPUT | ✅ PASS |
| 5 | "0" | 42 | INVALID_INPUT | ✅ PASS |
| 6 | "101" | 42 | INVALID_INPUT | ✅ PASS |
| 7 | "1" | 1 | CORRECT | ✅ PASS |
| 8 | "100" | 100 | CORRECT | ✅ PASS |
| 9 | "50" | 50 | CORRECT | ✅ PASS |

**Итого: 10/10 PASSED**

---

## Задание 9. ADB: установка и запуск

**Скрипт:** `scripts/task09_adb_install_launch.sh`

```bash
adb install -r "Lab_4_Calculator_Shibitov_Nikolay/app/build/outputs/apk/debug/app-debug.apk"
adb shell am start -n "com.example.lab_4_calculator_shibitov_nikolay/.MainActivity"
adb shell screencap /sdcard/task09_calculator.png
adb pull /sdcard/task09_calculator.png task09_calculator.png
```

| Команда | Результат |
|---|---|
| adb install Calculator | ✅ Success |
| adb install GuessNumber | ✅ Success |
| am start Calculator | ✅ Запущен |
| screencap + pull | ✅ Скриншот сохранён |
| am start GuessNumber | ✅ Запущен |
| logcat filter | ✅ Активности видны в логе |

---

## Задание 10. Logcat

**Скрипт:** `scripts/task10_adb_logcat.sh`

| Команда ADB | Описание |
|---|---|
| `adb logcat -c` | Очистить буфер |
| `adb logcat -d *:W` | Только WARNING и выше |
| `adb logcat -d \| grep "com.example.*"` | Фильтр по пакету |
| `adb logcat -d *:E` | Только ошибки |
| `adb logcat -d > file.txt` | Сохранить в файл |

Результат: ошибок уровня E/F не обнаружено. Активности корректно отображаются в logcat с тегами `ActivityManager`.

---

## Задание 11. Monkey-тестирование

**Скрипт:** `scripts/task11_monkey.sh`

```bash
adb shell monkey \
    -p "com.example.lab_4_calculator_shibitov_nikolay" \
    -s 12345 \
    --pct-touch 60 \
    --pct-motion 20 \
    --pct-appswitch 5 \
    --ignore-crashes \
    --ignore-timeouts \
    --ignore-security-exceptions \
    -v 5000
```

| Параметр | Значение | Описание |
|---|---|---|
| `-s 12345` | seed | Воспроизводимая последовательность событий |
| `--pct-touch 60` | 60% | Сенсорные нажатия |
| `--pct-motion 20` | 20% | Жесты движения |
| `--pct-appswitch 5` | 5% | Переключение приложений |
| `5000` | events | Количество событий |

| Приложение | Краши | Результат |
|---|---|---|
| Calculator | 0 | ✅ PASS |
| GuessNumber | 0 | ✅ PASS |

---

## Задание 12. ADB-регрессия

**Скрипт:** `scripts/task12_adb_regression.sh`

Сценарий регрессии:
1. Установка APK обоих приложений
2. Запуск Calculator, ввод выражения "42 + 8"
3. Отправка SMS: `adb emu sms send +79001234567 "Test SMS"`
4. Возврат в приложение — выражение сохранено
5. Входящий звонок: `adb emu gsm call +79009876543`
6. Завершение звонка: `adb emu gsm cancel +79009876543`
7. Низкий заряд: `adb shell dumpsys battery set level 15`
8. Сброс: `adb shell dumpsys battery reset`
9. Отключение/включение WiFi: `adb shell svc wifi disable/enable`
10. Проверка logcat на FATAL EXCEPTION

| Шаг | Результат |
|---|---|
| Установка APK | ✅ PASS |
| Ввод выражения | ✅ PASS |
| SMS-прерывание | ✅ PASS — состояние сохранено |
| Звонок | ✅ PASS — приложение восстановилось |
| Низкий заряд | ✅ PASS — предупреждение показано |
| WiFi toggle | ✅ PASS — нет ошибок |
| Crashes в logcat | ✅ PASS — 0 краши |

---

## Задание 13. Псевдо-локали

**Скрипт:** `scripts/task13_pseudolocales.sh`
**Конфигурация:** `pseudoLocalesEnabled true` в `buildTypes.debug` (app/build.gradle)

| Псевдо-локаль | Описание | Результат |
|---|---|---|
| `en-XA` | Акцентированные символы (Latin) | ✅ Текст расширен, нет обрезания |
| `ar-XB` | RTL-зеркалирование | ✅ Макет корректно зеркален |

Выявлена потенциальная проблема: при `ar-XB` кнопки калькулятора не имеют явного `android:layoutDirection="locale"`, однако ConstraintLayout корректно обрабатывает RTL при `android:supportsRtl="true"` в манифесте.

---

## Задание 14. Сравнение скриншотов

**Скрипт:** `scripts/task14_screenshot_compare.sh`

Методология:
1. Эталонный скриншот сохраняется в `task14_reference/`
2. Актуальный скриншот после действий сохраняется в `task14_actual/`
3. Сравнение производится визуально или через `imagemagick compare`

```bash
adb shell screencap /sdcard/sc_initial.png
adb pull /sdcard/sc_initial.png task14_reference/sc_initial.png
# ... perform actions ...
adb shell screencap /sdcard/sc_result_42.png
adb pull /sdcard/sc_result_42.png task14_actual/sc_result_42.png
```

| Снимок | Размер | Результат |
|---|---|---|
| sc_initial.png (reference) | > 0 байт | ✅ Сохранён |
| sc_result_42.png (after 7×6=42) | > 0 байт | ✅ Сохранён |
| sc_after_clear.png | > 0 байт | ✅ Сохранён |

---

## Задание 15. Локализация (немецкий язык)

**Файлы ресурсов:**
- `values/strings.xml` — English (default)
- `values-de/strings.xml` — Deutsch

**Тест:** `CalculatorLocalizationTest.java`

```java
@Test
public void testGermanLocale_divisionByZeroString() {
    Context baseCtx = InstrumentationRegistry.getInstrumentation().getTargetContext();
    Configuration config = new Configuration(baseCtx.getResources().getConfiguration());
    config.setLocale(Locale.GERMAN);
    Context deCtx = baseCtx.createConfigurationContext(config);

    assertEquals("Fehler: Division durch Null",
            deCtx.getString(R.string.error_division_by_zero));
}
```

| Ключ | EN | DE | Результат |
|---|---|---|---|
| app_name | Calculator | Taschenrechner | ✅ PASS |
| error_division_by_zero | Error: Division by zero | Fehler: Division durch Null | ✅ PASS |
| cd_expression | Expression | Ausdruck | ✅ PASS |
| cd_result | Result | Ergebnis | ✅ PASS |

**Итого: 3/3 PASSED**

---

## Задание 16. Полный цикл CI

**Скрипт:** `scripts/task16_full_cycle.sh`

Скрипт выполняет последовательно:

| Шаг | Действие | Результат |
|---|---|---|
| 1 | Calculator unit tests (21) | ✅ PASS |
| 2 | GuessNumber unit tests (17 + 10) | ✅ PASS |
| 3 | Calculator APK build | ✅ PASS |
| 4 | GuessNumber APK build | ✅ PASS |
| 5 | ADB install both APKs | ✅ PASS |
| 6 | Calculator Espresso (11) | ✅ PASS |
| 7 | GuessNumber Espresso (9) | ✅ PASS |
| 8 | Screenshot capture | ✅ PASS |
| 9 | SMS interruption during game | ✅ PASS |
| 10 | Monkey (3000 events × 2) | ✅ PASS |

**Итог: PASS=10 FAIL=0**

---

## Контрольные вопросы

**1. В чём разница между unit-тестами и инструментальными тестами?**

Unit-тесты (`src/test/`) запускаются на JVM без Android SDK — быстро, без эмулятора, изолированно. Инструментальные тесты (`src/androidTest/`) выполняются на устройстве/эмуляторе: они имеют доступ к Context, View, базам данных. Espresso — фреймворк для инструментальных UI-тестов.

**2. Для чего нужен Mockito?**

Mockito позволяет создавать моки (заглушки) для зависимостей. Например, `NumberGenerator` — интерфейс; в тестах мы заменяем реализацию моком, который всегда возвращает известное число (42). Это изолирует тестируемый код от внешних факторов (случайности, сети, базы данных).

- `mock()` — создаёт заглушку интерфейса или класса.
- `when(...).thenReturn(...)` — определяет поведение заглушки.
- `verify(...)` — проверяет, что метод был вызван.
- `spy()` — частичная заглушка над реальным объектом.

**3. Почему в тестах нельзя использовать `Thread.sleep()`?**

`Thread.sleep()` создаёт хрупкие тесты: на медленных устройствах таймаут может быть недостаточным, на быстрых — лишняя задержка. Espresso использует `IdlingResource` — механизм ожидания, пока приложение находится в "idle"-состоянии, что делает тесты надёжными и быстрыми.

**4. Как Espresso синхронизируется с UI-потоком?**

Espresso автоматически ожидает, пока главный поток и AsyncTask-пул станут "idle" перед выполнением каждого `perform()` и `check()`. Для кастомных асинхронных операций используется `IdlingResource`, который регистрируется через `IdlingRegistry`.

**5. Что такое `ActivityScenario.recreate()` и зачем он нужен?**

`recreate()` воссоздаёт `Activity`, имитируя поворот экрана или смену конфигурации. Это позволяет проверить корректность `onSaveInstanceState` и `onCreate(Bundle)` без физического поворота устройства. Тест становится детерминированным и воспроизводимым.

**6. Что такое AccessibilityChecks и как они работают?**

`AccessibilityChecks.enable()` подключает проверки доступности к каждому Espresso-действию. При нажатии на элемент проверяется: наличие `contentDescription`, достаточный размер touch-цели (минимум 48×48dp), контрастность текста. При нарушении тест автоматически падает.

**7. Для чего нужны псевдо-локали (`pseudoLocalesEnabled true`)?**

Псевдо-локали позволяют без перевода проверить:
- `en-XA`: расширяет текст на ~30-40% (имитирует языки с длинными словами) — выявляет обрезание строк в UI.
- `ar-XB`: зеркалит текст справа-налево — проверяет поддержку RTL-макетов.

**8. Что проверяет Monkey и каковы ключевые параметры?**

Monkey генерирует случайные события (касания, жесты, системные события) для стресс-тестирования. Ключевые параметры: `-s seed` (воспроизводимость), `--pct-touch` (доля касаний), `--ignore-crashes` (продолжать при краше), количество событий. Если `CRASH` появляется в выводе — найден баг.

**9. Зачем интерфейс `NumberGenerator` вместо прямого вызова `Random`?**

Прямое использование `Random` делает код нетестируемым — невозможно предсказать загаданное число. Интерфейс `NumberGenerator` позволяет подставить мок в тестах (`when(gen.generate(...)).thenReturn(42)`), сохраняя реальную реализацию `RandomNumberGenerator` для продакшна. Это принцип Dependency Inversion (DIP).

---

## Сводная таблица результатов

| Задание | Описание | Тестов | Результат |
|---|---|---|---|
| 2 | Unit-тесты Calculator | 21 | ✅ 21/21 PASS |
| 3 | Espresso UI Calculator | 9 | ✅ 9/9 PASS |
| 4 | Rotation state preservation | 2 | ✅ 2/2 PASS |
| 5 | Mockito unit-тесты | 17 | ✅ 17/17 PASS |
| 6 | Espresso UI GuessNumber | 5 | ✅ 5/5 PASS |
| 7 | Accessibility checks | 4 | ✅ 4/4 PASS |
| 8 | Parameterized tests | 10 | ✅ 10/10 PASS |
| 9 | ADB install & launch | — | ✅ PASS |
| 10 | ADB logcat | — | ✅ PASS |
| 11 | Monkey stress test | — | ✅ PASS |
| 12 | ADB regression | — | ✅ PASS |
| 13 | Pseudolocales | — | ✅ PASS |
| 14 | Screenshot compare | — | ✅ PASS |
| 15 | Localization DE | 3 | ✅ 3/3 PASS |
| 16 | Full CI pipeline | 10 шагов | ✅ PASS |

**Все 16 заданий выполнены. Общее количество автотестов: 71**

# Лабораторная работа №3
# PySpark, MovieLens и рекомендательные системы

**Студент:** Шибитов Николай  
**Группа:** 11  
**Курс:** 3  
**Дисциплина:** Искусственный интеллект  
**Проверяемый файл:** `lab3.ipynb`

---

## 1. Цель работы

Цель лабораторной работы - познакомиться с обработкой больших данных и построением рекомендательной системы в PySpark на датасете MovieLens 1M.

В работе требуется:

1. Разобраться с базовой теорией Spark.
2. Создать `SparkSession`.
3. Загрузить `movies.dat` и `ratings.dat` в Spark DataFrame.
4. Выполнить предобработку и кэширование.
5. Провести аналитику по жанрам и фильмам.
6. Показать работу разных видов join.
7. Выполнить запрос через Spark SQL.
8. Обучить ALS-модель из MLlib.
9. Получить персональные рекомендации для пользователя.
10. Корректно завершить Spark-сессию.

---

## 2. Статус проверки работы

В папке:

```text
students/Шибитов/АИ
```

создана и подготовлена лабораторная №3:

```text
students/Шибитов/АИ/lab3/lab3.ipynb
```

Ноутбук содержит теоретический блок, загрузку MovieLens 1M, предобработку жанров, аналитику, демонстрацию видов `join`, Spark SQL, обучение ALS-модели, оценку RMSE, персональные рекомендации и завершение Spark-сессии через `spark.stop()`.

---

## 3. Теоретическая часть

### 3.1. Что такое PySpark

PySpark - Python-интерфейс к Apache Spark. Apache Spark - распределенный фреймворк для обработки больших данных. Он позволяет выполнять вычисления не только на одном компьютере, но и на кластере из многих машин.

PySpark дает возможность писать код на Python, а Spark при этом выполняет тяжелые операции в своей распределенной среде.

Главные задачи PySpark:

- загрузка больших наборов данных;
- распределенная фильтрация, агрегация и join;
- обработка потоков данных;
- машинное обучение через MLlib;
- работа с SQL-запросами поверх больших таблиц.

### 3.2. Главное отличие PySpark от Pandas

Pandas работает в памяти одного процесса Python. Это удобно для небольших и средних таблиц, которые помещаются в оперативную память компьютера.

PySpark рассчитан на распределенную обработку. Данные могут быть разбиты на части и обработаны параллельно на нескольких исполнителях.

Сравнение:

| Критерий | Pandas | PySpark |
|---|---|---|
| Масштаб данных | обычно до объема RAM одной машины | большие данные, кластерная обработка |
| Выполнение | сразу после вызова операции | ленивое, до action |
| Основная структура | `DataFrame` Pandas | Spark `DataFrame`, `RDD` |
| Скорость на малых данных | часто быстрее и проще | может быть медленнее из-за накладных расходов |
| Скорость на больших данных | ограничена одной машиной | масштабируется на кластер |
| Типичный сценарий | анализ CSV, быстрые эксперименты | ETL, аналитика, ML на больших данных |

Простое правило: если данные помещаются в память и задача локальная, Pandas проще. Если данных много или нужна распределенная обработка, нужен Spark.

### 3.3. Что такое PySpark MLlib

MLlib - библиотека машинного обучения в Apache Spark. Она содержит алгоритмы, которые умеют работать с распределенными данными.

MLlib включает:

- классификацию;
- регрессию;
- кластеризацию;
- рекомендательные системы;
- пайплайны обработки признаков;
- метрики качества.

В лабораторной используется ALS - алгоритм рекомендаций.

### 3.4. Отличие MLlib от Sklearn

Sklearn ориентирован на локальное машинное обучение. Обычно данные находятся в NumPy-массивах или Pandas DataFrame и обрабатываются на одной машине.

MLlib ориентирован на распределенную среду Spark. Данные хранятся в Spark DataFrame, а обучение может выполняться на кластере.

Сравнение:

| Критерий | Sklearn | PySpark MLlib |
|---|---|---|
| Среда | одна машина | Spark-кластер или локальный Spark |
| Данные | NumPy, Pandas | Spark DataFrame |
| Удобство экспериментов | очень высокое | выше порог входа |
| Масштабирование | ограничено ресурсами машины | распределенное |
| Поддержка алгоритмов | очень широкая | меньше, но рассчитана на большие данные |

Sklearn удобнее для прототипов и классических учебных задач. MLlib нужен, когда данные слишком велики для локальной обработки.

### 3.5. Ленивые вычисления в Spark

Lazy Evaluation - одна из ключевых идей Spark. Spark не выполняет трансформацию сразу после ее вызова. Вместо этого он строит план вычислений.

Например:

```python
filtered = ratings_df.filter(col("rating") >= 4)
grouped = filtered.groupBy("movieID").count()
```

На этих строках Spark еще не обязан реально читать и обрабатывать все данные. Он только запоминает, что нужно сделать.

Вычисления запускаются только при action, например:

```python
grouped.show()
grouped.count()
grouped.collect()
```

Плюсы ленивых вычислений:

- Spark может оптимизировать весь план целиком;
- лишние операции могут быть исключены;
- фильтры могут быть перенесены ближе к источнику данных;
- можно строить длинную цепочку преобразований без немедленных затрат.

### 3.6. Transformations и Actions

В Spark операции делятся на две большие группы.

Transformations - преобразования. Они создают новый DataFrame/RDD, но не запускают вычисление сразу.

Примеры:

```python
filter()
select()
withColumn()
groupBy()
join()
orderBy()
```

Actions - действия. Они требуют результата и запускают выполнение плана.

Примеры:

```python
show()
count()
collect()
take()
write()
```

Важно понимать разницу: если код содержит только transformations, Spark может ничего не посчитать. Чтобы увидеть результат, нужно action.

### 3.7. RDD, DataFrame и SparkSession

`RDD` - Resilient Distributed Dataset. Это низкоуровневая распределенная коллекция объектов. RDD дает гибкость, но требует больше ручной работы.

`DataFrame` - табличная структура Spark с именованными столбцами. Она похожа на Pandas DataFrame, но распределенная и оптимизируемая Spark SQL Catalyst Optimizer.

`SparkSession` - главная точка входа в Spark-приложение. Через нее создаются DataFrame, выполняются SQL-запросы, читаются файлы и настраивается приложение.

Пример:

```python
spark = (
    SparkSession.builder
    .appName("MovieLens Lab")
    .master("local[*]")
    .getOrCreate()
)
```

`local[*]` означает запуск локально с использованием всех доступных ядер процессора.

### 3.8. Архитектура Spark: Driver и Executors

Spark-приложение состоит из Driver и Executors.

Driver - управляющий процесс. Он:

- выполняет пользовательскую программу;
- строит план вычислений;
- распределяет задачи;
- собирает метаданные о выполнении.

Executors - рабочие процессы. Они:

- выполняют задачи над частями данных;
- хранят кэшированные данные;
- возвращают результаты Driver.

Упрощенная схема:

```text
Python-код пользователя
        |
        v
Driver строит план
        |
        v
Executors выполняют задачи параллельно
        |
        v
Driver получает результат
```

Даже при запуске `local[*]` эта модель сохраняется логически: Spark работает как распределенная система, но все процессы находятся на одной машине.

### 3.9. Apache Parquet

Parquet - колоночный формат хранения данных. В отличие от CSV, где данные хранятся построчно и все значения обычно представлены текстом, Parquet хранит данные по столбцам и сохраняет схему.

Преимущества Parquet:

- быстрее чтение нужных столбцов;
- меньше размер файла за счет сжатия;
- сохраняются типы данных;
- хорошо подходит для аналитики;
- эффективно работает со Spark.

Например, если запросу нужны только `movieID` и `rating`, Spark может читать только эти столбцы, а не всю таблицу.

---

## 4. Данные MovieLens 1M

MovieLens 1M - набор данных с оценками фильмов:

- примерно 1 миллион оценок;
- около 6000 пользователей;
- около 4000 фильмов.

В лабораторной нужны файлы:

- `movies.dat`;
- `ratings.dat`.

Обычно формат файлов такой:

```text
movies.dat:
movieID::title::genres

ratings.dat:
userID::movieID::rating::timestamp
```

Разделитель `::` нестандартный, поэтому при чтении его нужно указать явно.

---

## 5. Подготовка Spark

### 5.1. Импорты

Для работы нужны основные функции Spark SQL:

```python
import pyspark
from pyspark.sql import SparkSession
from pyspark.sql.functions import col, avg, count, split, explode, lit
from pyspark.sql.types import StructType, StructField, StringType, IntegerType, FloatType
```

Для MLlib:

```python
from pyspark.ml.recommendation import ALS
from pyspark.ml.evaluation import RegressionEvaluator
```

### 5.2. Создание SparkSession

```python
spark = (
    SparkSession.builder
    .appName("MovieLens Lab")
    .master("local[*]")
    .getOrCreate()
)

spark.sparkContext.setLogLevel("ERROR")
```

Отключение лишнего логирования нужно, чтобы вывод ноутбука был читаемым. Spark печатает много служебной информации, которая мешает анализу результатов.

---

## 6. Загрузка данных

### 6.1. Схема для movies.dat

```python
movies_schema = StructType([
    StructField("movieID", IntegerType(), True),
    StructField("title", StringType(), True),
    StructField("genres", StringType(), True),
])
```

### 6.2. Схема для ratings.dat

```python
ratings_schema = StructType([
    StructField("userID", IntegerType(), True),
    StructField("movieID", IntegerType(), True),
    StructField("rating", FloatType(), True),
    StructField("timestamp", IntegerType(), True),
])
```

Явная схема лучше, чем `inferSchema`, потому что:

- Spark не тратит время на угадывание типов;
- код становится воспроизводимым;
- снижается риск неправильного определения типов.

### 6.3. Чтение файлов

```python
movies_df = (
    spark.read
    .option("sep", "::")
    .option("header", "false")
    .schema(movies_schema)
    .csv("ml-1m/movies.dat")
)

ratings_df = (
    spark.read
    .option("sep", "::")
    .option("header", "false")
    .schema(ratings_schema)
    .csv("ml-1m/ratings.dat")
)
```

В некоторых версиях Spark для многосимвольного разделителя может понадобиться:

```python
.option("delimiter", "::")
```

или чтение через `sparkContext.textFile` с последующим `split("::")`. В отчете важно указать, какой вариант сработал.

---

## 7. Кэширование данных

В задании требуется выполнить:

```python
ratings_df.cache()
```

Кэширование сообщает Spark, что DataFrame нужно сохранить в памяти после первого вычисления. Это полезно, если `ratings_df` используется много раз: для аналитики, join, SQL и обучения модели.

Без кэша Spark из-за ленивых вычислений может повторно перечитывать и пересчитывать одни и те же данные при каждом action.

Обычно после `cache()` полезно вызвать action, чтобы кэш реально заполнился:

```python
ratings_df.cache()
ratings_df.count()
```

---

## 8. Обработка жанров

В `movies.dat` жанры записаны строкой:

```text
Adventure|Animation|Children
```

Для аналитики удобнее получить одну строку на один жанр. Это делается через `split` и `explode`:

```python
genres_df = movies_df.withColumn(
    "genre",
    explode(split(col("genres"), "\\|"))
)
```

`split` превращает строку в массив жанров, а `explode` разворачивает массив в отдельные строки.

Пример:

```text
movieID | title      | genres              | genre
1       | Toy Story  | Animation|Comedy    | Animation
1       | Toy Story  | Animation|Comedy    | Comedy
```

---

## 9. Аналитика

### 9.1. Топ-10 популярных жанров

Популярность жанра можно считать по количеству фильмов:

```python
top_genres = (
    genres_df
    .groupBy("genre")
    .count()
    .orderBy(col("count").desc())
)

top_genres.show(10)
```

Также можно считать популярность по количеству оценок. Для этого нужно соединить жанры с рейтингами:

```python
genre_ratings = ratings_df.join(genres_df, on="movieID", how="inner")

top_genres_by_ratings = (
    genre_ratings
    .groupBy("genre")
    .agg(count("*").alias("rating_count"))
    .orderBy(col("rating_count").desc())
)
```

Второй вариант часто более осмысленный: он показывает, какие жанры чаще оцениваются пользователями.

### 9.2. Топ-10 на свой вкус

Пример дополнительной аналитики - топ-10 пользователей по числу оценок:

```python
top_users = (
    ratings_df
    .groupBy("userID")
    .agg(count("*").alias("rating_count"))
    .orderBy(col("rating_count").desc())
)

top_users.show(10)
```

Можно также найти:

- самые спорные фильмы по дисперсии оценок;
- фильмы с максимальной средней оценкой при минимуме оценок;
- годы с наибольшим числом фильмов;
- пользователей с самым высоким средним рейтингом.

---

## 10. Join в Spark

Join нужен, чтобы соединить оценки с названиями фильмов.

### 10.1. Самые популярные фильмы по количеству оценок

```python
popular_movies = (
    ratings_df
    .groupBy("movieID")
    .agg(count("*").alias("rating_count"))
    .join(movies_df, on="movieID", how="inner")
    .orderBy(col("rating_count").desc())
)

popular_movies.select("title", "rating_count").show(10, truncate=False)
```

### 10.2. Лучшие фильмы с минимум 500 оценок

```python
best_movies = (
    ratings_df
    .groupBy("movieID")
    .agg(
        avg("rating").alias("avg_rating"),
        count("*").alias("rating_count")
    )
    .filter(col("rating_count") >= 500)
    .join(movies_df, on="movieID", how="inner")
    .orderBy(col("avg_rating").desc())
)

best_movies.select("title", "avg_rating", "rating_count").show(10, truncate=False)
```

Ограничение `rating_count >= 500` важно. Без него фильм с одной оценкой `5.0` мог бы оказаться «лучшим», хотя статистически это ненадежно.

### 10.3. Виды join

Для объяснения join удобно создать маленькие таблицы:

```python
left = spark.createDataFrame(
    [(1, "A"), (2, "B"), (3, "C")],
    ["id", "left_value"]
)

right = spark.createDataFrame(
    [(2, "X"), (3, "Y"), (4, "Z")],
    ["id", "right_value"]
)
```

`inner join` оставляет только совпадающие ключи:

```text
2, 3
```

`left join` оставляет все строки слева и добавляет совпадения справа:

```text
1, 2, 3
```

`right join` оставляет все строки справа:

```text
2, 3, 4
```

`outer join` оставляет все ключи из обеих таблиц:

```text
1, 2, 3, 4
```

`cross join` строит декартово произведение: каждая строка слева соединяется с каждой строкой справа. Если слева 3 строки и справа 3 строки, получится 9 строк.

---

## 11. Spark SQL

Spark позволяет выполнять SQL-запросы поверх DataFrame. Для этого DataFrame регистрируются как временные представления:

```python
ratings_df.createOrReplaceTempView("ratings")
movies_df.createOrReplaceTempView("movies")
```

Топ-10 фильмов с минимум 500 оценок через SQL:

```python
best_movies_sql = spark.sql("""
    SELECT
        m.title,
        AVG(r.rating) AS avg_rating,
        COUNT(*) AS rating_count
    FROM ratings r
    JOIN movies m ON r.movieID = m.movieID
    GROUP BY m.movieID, m.title
    HAVING COUNT(*) >= 500
    ORDER BY avg_rating DESC
    LIMIT 10
""")

best_movies_sql.show(truncate=False)
```

Spark SQL полезен, если аналитика естественно выражается SQL-запросом или если в команде есть специалисты, привыкшие к SQL.

---

## 12. ALS и рекомендательные системы

### 12.1. Задача рекомендаций

Рекомендательная система пытается предсказать, какие фильмы могут понравиться пользователю. В MovieLens есть матрица взаимодействий:

```text
          Movie 1   Movie 2   Movie 3
User 1       5        ?         3
User 2       ?        4         ?
User 3       2        ?         5
```

Знак `?` означает неизвестную оценку. Задача модели - заполнить такие пропуски прогнозами.

### 12.2. ALS

ALS - Alternating Least Squares, метод попеременных наименьших квадратов. Он используется для матричной факторизации.

Идея:

- каждому пользователю сопоставляется скрытый вектор интересов;
- каждому фильму сопоставляется скрытый вектор свойств;
- оценка пользователя фильму приближается скалярным произведением этих векторов.

Модель не знает явно жанры или сюжеты. Она ищет скрытые факторы из поведения пользователей.

### 12.3. Создание модели

```python
als = ALS(
    maxIter=5,
    regParam=0.01,
    userCol="userID",
    itemCol="movieID",
    ratingCol="rating",
    coldStartStrategy="drop"
)
```

Параметры:

- `maxIter` - количество итераций обучения;
- `regParam` - регуляризация, ограничивает переобучение;
- `userCol` - колонка пользователя;
- `itemCol` - колонка объекта, то есть фильма;
- `ratingCol` - колонка оценки;
- `coldStartStrategy="drop"` - удаляет строки, для которых модель не может построить прогноз из-за неизвестного пользователя или фильма.

### 12.4. Обучение и проверка

```python
train_df, test_df = ratings_df.randomSplit([0.8, 0.2], seed=42)

model = als.fit(train_df)
predictions = model.transform(test_df)
```

Оценка RMSE:

```python
evaluator = RegressionEvaluator(
    metricName="rmse",
    labelCol="rating",
    predictionCol="prediction"
)

rmse = evaluator.evaluate(predictions)
print(f"RMSE = {rmse:.4f}")
```

RMSE показывает среднюю ошибку прогноза с усиленным штрафом за крупные ошибки. Для рейтингов от 1 до 5 чем меньше RMSE, тем лучше.

---

## 13. Персональные рекомендации

Чтобы получить рекомендации для одного пользователя:

```python
user_id = 1

user_subset = spark.createDataFrame([(user_id,)], ["userID"])

recommendations = model.recommendForUserSubset(user_subset, 10)
recommendations.show(truncate=False)
```

Результат содержит массив рекомендаций, где для каждого фильма указана предсказанная оценка.

Чтобы получить названия фильмов, массив нужно развернуть:

```python
from pyspark.sql.functions import explode

rec_movies = (
    recommendations
    .select("userID", explode("recommendations").alias("rec"))
    .select(
        "userID",
        col("rec.movieID").alias("movieID"),
        col("rec.rating").alias("predicted_rating")
    )
    .join(movies_df, on="movieID", how="inner")
    .orderBy(col("predicted_rating").desc())
)

rec_movies.select("title", "predicted_rating").show(10, truncate=False)
```

Такой вывод намного понятнее, потому что показывает не только `movieID`, но и название фильма.

---

## 14. Завершение Spark-сессии

В конце работы обязательно нужно выполнить:

```python
spark.stop()
```

Это освобождает ресурсы:

- завершает Driver;
- останавливает Executors;
- очищает кэш;
- освобождает память и занятые порты.

Если не остановить Spark-сессию, в локальной среде могут остаться фоновые процессы, из-за чего следующий запуск будет работать нестабильно или потреблять лишнюю память.

---

## 15. Рекомендуемая структура ноутбука

Для аккуратной сдачи лабораторной №3 ноутбук лучше оформить так:

1. Титульный блок: название, студент, группа.
2. Цель работы.
3. Теоретические ответы на вопросы из задания.
4. Импорты и создание `SparkSession`.
5. Загрузка `movies.dat` и `ratings.dat` с явными схемами.
6. Кэширование `ratings_df` и объяснение кэша.
7. Предобработка жанров через `split` и `explode`.
8. Аналитика: популярные жанры и дополнительный топ.
9. Join: популярные фильмы, лучшие фильмы, демонстрация join-типов.
10. Spark SQL.
11. ALS: обучение, предсказания, RMSE.
12. Персональные рекомендации.
13. Завершение `spark.stop()`.
14. Итоговые выводы.

---

## 16. Что обязательно проверить перед сдачей

1. Ноутбук выполняется сверху вниз без ошибок.
2. Пути к `movies.dat` и `ratings.dat` относительные, а не абсолютные.
3. В ноутбуке есть выводы после ключевых блоков.
4. Для DataFrame заданы явные схемы.
5. Есть объяснение `cache()`.
6. Есть демонстрация всех join: `inner`, `left`, `right`, `outer`, `cross`.
7. SQL-задание дает тот же смысловой результат, что DataFrame API.
8. ALS обучается на train и оценивается на test.
9. RMSE выведен численно.
10. Персональные рекомендации содержат названия фильмов, а не только ID.
11. В конце есть `spark.stop()`.
12. Ноутбук сохранен с outputs и экспортирован в HTML/PDF.

---

## 17. Вывод

Лабораторная работа №3 посвящена переходу от локального анализа данных к распределенной обработке. PySpark отличается от Pandas ленивыми вычислениями, архитектурой Driver/Executors и возможностью масштабироваться на большие данные. На MovieLens хорошо видны типичные задачи Spark: чтение файлов с явной схемой, кэширование, группировки, join, SQL-запросы и обучение модели рекомендаций.

Лабораторная №3 подготовлена в виде Jupyter Notebook `lab3.ipynb`. Перед сдачей нужно положить рядом с ноутбуком архив `ml-1m.zip` или папку `ml-1m`, выполнить все ячейки сверху вниз, сохранить результаты и экспортировать ноутбук в HTML/PDF.

# Mission Analyzer (веб-приложение)

Сервис принимает файлы миссий (JSON, XML, YAML, TXT и др. через фабрику парсеров), сохраняет структуру в **PostgreSQL**, позволяет просматривать и удалять записи и **генерировать отчёты**. Веб-интерфейс и REST API работают на **Spring Boot 3**; описание API доступно в **Swagger UI**.

## Требования

- **JDK 17** (для сборки и запуска; рекомендуется LTS).
- **Docker Desktop** (или иной Docker) — для PostgreSQL по `docker-compose.yml`.
- **Maven 3.9+**.

## Быстрый старт

1. Запустите базу данных:

```bash
docker compose up -d
```

По умолчанию: БД `mission_analyzer`, пользователь `mission`, пароль `mission`, порт `5432`.

2. Соберите проект и запустите приложение:

```bash
mvn spring-boot:run
```

3. Откройте в браузере:

- **Интерфейс:** [http://localhost:8080](http://localhost:8080)
- **Swagger UI:** [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)
- **OpenAPI JSON:** [http://localhost:8080/api/v3/api-docs](http://localhost:8080/api/v3/api-docs)

## Переменные окружения (опционально)

| Переменная   | Назначение        | По умолчанию        |
|-------------|-------------------|---------------------|
| `DB_HOST`   | Хост PostgreSQL   | `localhost`         |
| `DB_PORT`   | Порт              | `5432`              |
| `DB_NAME`   | Имя БД            | `mission_analyzer`  |
| `DB_USER`   | Пользователь      | `mission`           |
| `DB_PASSWORD` | Пароль          | `mission`           |
| `SERVER_PORT` | Порт HTTP       | `8080`              |

## REST API

| Метод | Путь | Описание |
|--------|------|----------|
| `POST` | `/api/missions` | Загрузка файла (`multipart/form-data`, поле `file`) |
| `GET` | `/api/missions` | Список миссий |
| `GET` | `/api/missions/{id}` | Детали миссии |
| `DELETE` | `/api/missions/{id}` | Удаление миссии и связанных отчётов |
| `POST` | `/api/missions/{id}/reports` | Создание отчёта (`{"reportType":"SUMMARY"}` и т.д.) |
| `GET` | `/api/missions/{id}/reports` | Сохранённые отчёты по миссии |

Типы отчётов: `SUMMARY`, `DETAILED`, `RISK`, `STATISTICAL`.

Повторная загрузка файла с тем же `missionId`, что уже есть в БД, **обновляет** данные миссии в PostgreSQL (тот же числовой `id`, сохранённые отчёты не удаляются).

## Архитектура проекта

- **`domain`**, **`parser`**, **`reporter`**, **`facade`**, **`enums`**, **`exception`** — предметная область и логика разбора/отчётов.
- **`persistence.entity`**, **`persistence.repository`** — JPA-сущности и репозитории, соответствующие таблицам PostgreSQL.
- **`service`** — оркестрация: загрузка файла во временный путь, парсинг, маппинг в сущности, генерация отчётов.
- **`api`** — REST-контроллеры и DTO.
- **`config`** — Swagger, CORS, бины `MissionParserFactory` и `ReportFactory`.
- Схема БД создаётся Hibernate автоматически из JPA-сущностей (`spring.jpa.hibernate.ddl-auto=update`).

Десктопный GUI (`app.MissionAnalyzerApp`, `app.MainFrame`) сохранён; для веб-режима точка входа — `app.MissionWebApplication`.

## Тесты

Запуск всех юнит-тестов:

```bash
mvn test
```

Набор тестов покрывает основную бизнес-логику, проверку входных данных и REST API (без поднятия PostgreSQL — репозитории и сервисы мокируются там, где нужно).

| Класс теста | Что проверяет |
|-------------|----------------|
| `parser/MissionParsingTest` | Корректный разбор JSON без искажений; отклонение битого JSON, отсутствующего `curse`, неверного `outcome`; неподдерживаемое расширение; файл из `missions/A` |
| `parser/MissionConverterTest` | Преобразование карты в `Mission`; обязательные поля и валидность enum |
| `service/MissionMapperTest` | Round-trip domain ↔ entity; порядок `pos`; замена данных при обновлении (`replaceMissionPayload` + `flush`) |
| `service/MissionApplicationServiceTest` | Пустой файл; создание и обновление миссии; удаление; генерация и список отчётов; `EntityNotFoundException` |
| `reporter/ReportFactoryTest` | Поддержка всех типов отчётов |
| `reporter/SummaryReportTest` | Содержимое сгенерированного краткого отчёта |
| `api/MissionControllerTest` | HTTP-контракт всех эндпоинтов; 400 при ошибке парсинга; 404 при отсутствии миссии |

Тестовые файлы миссий лежат в `src/test/resources/missions/`:

- `valid-mission.json` — корректная миссия;
- `invalid-malformed.json` — повреждённый JSON;
- `invalid-missing-curse.json` — неполные данные;
- `invalid-bad-outcome.json` — неверное значение перечисления.

На **Java 25** в `pom.xml` для Surefire задан флаг `-Dnet.bytebuddy.experimental=true` (для Mockito).

## Сборка JAR

```bash
mvn -DskipTests package
java -jar target/laba_3-1.0.jar
```

# Bank Card Management System

Backend-приложение на Java (Spring Boot) для управления банковскими картами.

## Технологии
![Java](https://img.shields.io/badge/java-21-blue?logo=java)
![Spring Boot](https://img.shields.io/badge/spring%20boot-4.1.0-brightgreen?logo=springboot)
![PostgreSQL](https://img.shields.io/badge/postgresql-latest-blue?logo=postgresql)
![Docker](https://img.shields.io/badge/docker-compose-blue?logo=docker)
![JWT](https://img.shields.io/badge/security-JWT-red?logo=jsonwebtokens)
![Liquibase](https://img.shields.io/badge/liquibase-migration-red?logo=liquibase)
![Swagger](https://img.shields.io/badge/api-OpenAPI-green?logo=swagger)

- Java 21, Spring Boot 4.1.0
- Spring Security + JWT
- Spring Data JPA + PostgreSQL
- Liquibase (миграции БД)
- Swagger UI / OpenAPI
- Docker Compose
- Lombok

## Быстрый старт

### 1. Клонировать репозиторий

```bash
git clone <url>
cd bank_rest
```

### 2. Настроить переменные окружения

```bash
cp .env.example .env
```

Отредактируйте `.env` — задайте пароли и JWT_SECRET.

### 3. Запуск (Docker — рекомендуется)

```bash
docker-compose up --build
```

Это соберёт образ приложения, поднимет PostgreSQL и приложение.
Приложение будет доступно на `http://localhost:8080`.

### 4. Запуск (локально, без Docker)

Если PostgreSQL уже запущен локально:

```bash
docker-compose up -d postgres   # только БД
./mvnw spring-boot:run          # приложение
```

### 5. Swagger UI

Откройте в браузере: `http://localhost:8080/swagger-ui.html`

## API Endpoints

### Аутентификация

| Метод | Описание |
|-------|----------|
| `POST /auth/register` | Регистрация нового пользователя |
| `POST /auth/login` | Вход, получение JWT-токена |

### Пользователи (только ADMIN)

| Метод | Описание |
|-------|----------|
| `GET /api/users` | Список пользователей |
| `GET /api/users/{id}` | Пользователь по ID |
| `POST /api/users` | Создать пользователя |
| `DELETE /api/users/{id}` | Удалить пользователя |

### Карты

| Метод | Описание |
|-------|----------|
| `GET /api/cards` | Все карты (ADMIN) / свои карты (USER) |
| `GET /api/cards/{id}` | Карта по ID |
| `POST /api/cards` | Создать карту (ADMIN) |
| `PUT /api/cards/{id}/block` | Заблокировать карту |
| `PUT /api/cards/{id}/activate` | Активировать карту (ADMIN) |
| `DELETE /api/cards/{id}` | Удалить карту (ADMIN) |
| `GET /api/cards/{id}/balance` | Баланс карты (владелец) |

### Переводы (только USER)

| Метод | Описание |
|-------|----------|
| `POST /api/transfers` | Перевод между своими картами |

## Пример использования

```bash
# Регистрация
curl -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"password123"}'

# Вход
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"password123"}'
# Вернёт: {"token":"eyJhbGci..."}

# Создание карты (с JWT-токеном)
curl -X POST http://localhost:8080/api/cards \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <token>" \
  -d '{"cardNumber":"1234567890123456","cardholder":"Ivan Ivanov","expiryDate":"2028-12-31","ownerId":1}'
```

## Тесты

```bash
./mvnw test
```

## Структура проекта

```
src/main/java/api/bank/
  ├── config/          — Конфигурация (CORS, OpenAPI)
  ├── controller/      — REST-контроллеры
  ├── dto/             — Объекты передачи данных
  ├── entity/          — JPA-сущности (User, Card, Role, CardStatus)
  ├── exception/       — Обработка ошибок
  ├── repository/      — Spring Data JPA репозитории
  ├── security/        — JWT, фильтры, SecurityConfig
  ├── service/         — Бизнес-логика
  └── util/            — Утилиты (шифрование, маскирование)
```
    

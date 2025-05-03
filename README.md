````markdown
# Test Project – Spring Boot API с JWT-аутентификацией

## Описание

Данное приложение реализует систему аутентификации с использованием JWT, управление ролями пользователей и базовые эндпоинты для взаимодействия с "картами" (cards) пользователей и админов. Также интегрирован OpenAPI/Swagger для визуализации и тестирования API.

---

## Технологии

- Java 17
- Spring Boot 3.4.5
- Spring Security
- Spring Data JPA
- PostgreSQL
- JWT (jjwt)
- Liquibase
- OpenAPI (springdoc-openapi)
- Testcontainers (для тестов)

---

## Эндпоинты

### 🔐 Аутентификация

#### POST `/auth/register`

Регистрация нового пользователя.  
**Тело запроса (JSON):**
```json
{
  "email": "user@example.com",
  "password": "securePassword"
}
````

#### POST `/auth/login`

Авторизация пользователя и получение JWT токена.
**Тело запроса (JSON):**

```json
{
  "email": "user@example.com",
  "password": "securePassword"
}
```

**Ответ:**

```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6..."
}
```

---

### 👤 Пользовательские эндпоинты

Все маршруты требуют авторизации с ролью `ROLE_USER`.
Добавьте заголовок:

```
Authorization: Bearer <your_jwt_token>
```

#### 📇 Карты пользователя

##### `GET /user/cards`

Получение всех карт текущего пользователя с возможностью фильтрации и пагинации.
**Параметры запроса:**

* `status` – фильтр по статусу (`ACTIVE`, `BLOCKED`, и т.д.) *(необязательно)*
* `page` – номер страницы (по умолчанию 0)
* `size` – размер страницы (по умолчанию 10)

##### `POST /user/cards/{id}/block`

Блокировка своей карты по ID.
**Пример:** `/user/cards/42/block`

##### `POST /user/cards/transfer`

Перевод средств между своими картами.
**Тело запроса (JSON):**

```json
{
  "fromCardId": 1,
  "toCardId": 2,
  "amount": 100.50
}
```

##### `GET /user/cards/balance`

Получение общей суммы средств на всех своих активных картах.
**Ответ (JSON):**

```json
{
  "totalBalance": 2500.75
}
```

##### `GET /user/cards/{card_id}`

Получение информации о конкретной карте по её ID, если она принадлежит текущему пользователю.
**Пример:** `/user/cards/5`

---

### 🛡️ Админские эндпоинты

Все маршруты требуют авторизации с ролью `ROLE_ADMIN`.
Добавьте заголовок:

```
Authorization: Bearer <your_jwt_token>
```

#### 📇 Карточки

##### `GET /admin/cards`

Получение всех карточек с фильтрацией по статусу и пагинацией.
**Параметры запроса:**

* `status` – фильтр по статусу карты (`ACTIVE`, `BLOCKED` и т.д.) *(необязательно)*
* `page` – номер страницы (по умолчанию 0)
* `size` – размер страницы (по умолчанию 10)

##### `POST /admin/cards`

Создание новой карточки.
**Тело запроса (JSON):**

```json
{
  "title": "Some title",
  "description": "Some description"
}
```

##### `POST /admin/cards/{id}/block`

Блокировка карточки по ID.
**Пример:** `/admin/cards/42/block`

##### `POST /admin/cards/{id}/activate`

Активация карточки по ID.
**Пример:** `/admin/cards/42/activate`

##### `DELETE /admin/cards/{id}`

Удаление карточки по ID.
**Пример:** `/admin/cards/42`

---

#### 👥 Пользователи

##### `GET /admin/users`

Получение списка всех пользователей (в формате DTO).

##### `PUT /admin/users/{id}/roles`

Обновление ролей пользователя по ID.
**Тело запроса (JSON):**

```json
["ROLE_USER", "ROLE_ADMIN"]
```

##### `DELETE /admin/users/{id}`

Удаление пользователя по ID.

---

Если хочешь, я могу сразу сформировать полный `README.md` как файл — хочешь?

---
Перед запуском приложения нужно ввести секретные ключи в файле .env в корневой директории. После из этой же директорри ввести следующую команду

## Запуск

```bash
docker-compose up --build
```

Далее нужно подождать, пока приложение запустится
---

## Тестирование

Присутствуют интеграционные тесты с использованием Testcontainers:

```bash
./mvnw test
```

---

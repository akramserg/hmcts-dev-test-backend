# HMCTS Dev Test Backend
This will be the backend for the brand new HMCTS case management system. As a potential candidate we are leaving
this in your hands. Please refer to the brief for the complete list of tasks! Complete as much as you can and be
as creative as you want.

You should be able to run `./gradlew build` to start with to ensure it builds successfully. Then from that you
can run the service in IntelliJ (or your IDE of choice) or however you normally would.

There is an example endpoint provided to retrieve an example of a case. You are free to add/remove fields as you
wish.

## Prerequisites

- Java 21
- Docker (for the local PostgreSQL database)

## Running locally

**1. Start the database**

```bash
docker run --name hmcts-tasks-postgres \
  -e POSTGRES_DB=tasks \
  -e POSTGRES_USER=tasks \
  -e POSTGRES_PASSWORD=tasks \
  -p 5433:5432 \
  -d postgres:16
```

**2. Start the application**

```bash
DB_USER_NAME=tasks DB_PASSWORD=tasks ./gradlew bootRun
```

The API will be available at `http://localhost:4000`.

Swagger UI: `http://localhost:4000/swagger-ui/index.html`

## Running tests

```bash
# Unit tests
./gradlew test

# Integration tests
./gradlew integration
```

## API endpoints

| Method | Path | Description |
|--------|------|-------------|
| `GET` | `/tasks` | List all tasks |
| `POST` | `/tasks` | Create a task |
| `GET` | `/tasks/{id}` | Get a task by ID |
| `PATCH` | `/tasks/{id}` | Update task status |
| `DELETE` | `/tasks/{id}` | Delete a task |

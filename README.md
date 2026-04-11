# Parser Engine (Monorepo)

This repository contains:

- `backend/`: Spring Boot (Java 17) API for authentication, file upload/processing, and property management.
- `frontend/`: Vite + React dashboard UI that talks to the backend API.

## Repo structure

```text
parser-engine/
  backend/
  frontend/
  .github/workflows/
  docker-compose.yml
```

## Quick start (local)

### 1) Start MySQL

From repo root:

```bash
docker compose up -d mysql
```

### 2) Run backend

```bash
cd backend
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

Backend base URL: `http://localhost:8080/parser-engine`

### 3) Run frontend

```bash
cd frontend
npm install
cp .env.example .env.local
npm run dev
```

## Docs

- Backend details: see `backend/README.md`
- Frontend details: see `frontend/README.md`
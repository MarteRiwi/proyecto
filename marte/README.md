# Estructura del proyecto

## controller
### Esta carpeta contiene las clases que controlan el flujo de la aplicación, reciben las solicitudes del usuario o de la interfaz, y delegan la lógica a otras capas.

## service
### Aquí está la lógica de negocio de la aplicación.

## repository
### Simula la base de datos (Aqui se guardara los arrayList)

## model
### Contiene las clases que representan los datos o entidades de la aplicación.

## excpetion
### Manejo de errores

```text
marte/
 ├── src/
 │   └── com/sigp/
 │        ├── controller/
 │        ├── service/
 │        ├── repository/
 │        ├── model/
 │        ├── exception/
 │        └── Main.java
 │
 ├── docs/
 ├── diagrams/
 └── README.md
 ```
## Conexión a PostgreSQL
El proyecto está configurado para usar PostgreSQL por JDBC en:
- Host: `localhost`
- Puerto: `5433`
- Base de datos: `sigp`
- Usuario: `postgres`

Puedes sobreescribir esos valores con variables de entorno:
- `SIGP_DB_URL`
- `SIGP_DB_HOST`
- `SIGP_DB_PORT`
- `SIGP_DB_NAME`
- `SIGP_DB_USER`
- `SIGP_DB_PASSWORD`

Ejemplo para ejecutar con variables de entorno:
`SIGP_DB_HOST=localhost SIGP_DB_PORT=5433 SIGP_DB_NAME=sigp SIGP_DB_USER=postgres SIGP_DB_PASSWORD={{SIGP_DB_PASSWORD}} mvn exec:java`

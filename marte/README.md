# Estructura del proyecto - MARTE (migrado)

## Resumen de migración
Se migró toda la lógica de `proyect_Patient` al encarpetado MVC de `marte`.
Se conservaron todos los nombres de variables y funciones originales.

## Estructura MVC

```text
marte/
 └── src/
     └── com/sigp/
          ├── controller/
          │    └── controllerAdmin.java       ← Menú admin + gestión doctores (migrado de login.java)
          ├── service/
          │    ├── serviceLogin.java          ← Autenticación + registro (fusión ambos proyectos)
          │    └── serviceAsignacionPatient.java ← Registro pacientes (migrado de Asing_patient.java)
          ├── repository/
          │    ├── RepositoryPatient.java     ← Almacenamiento de pacientes (NUEVO)
          │    └── RepositoryUser.java        ← Almacenamiento de usuarios (NUEVO)
          ├── model/
          │    ├── Patient.java               ← Record con validaciones (fusión ambos proyectos)
          │    ├── User.java                  ← Record con validaciones (fusión ambos proyectos)
          │    └── doctor.java               ← Modelo doctor con CRUD (migrado de esquema/doctor.java)
          ├── exception/
          │    └── CustomException.java       ← Sin cambios
          └── Main.java                       ← Menú principal con Login/Register/Exit
```

## Cambios realizados y por qué

### Agregados nuevos
| Archivo | Motivo |
|---|---|
| `RepositoryPatient.java` | Separa el almacenamiento de la lógica de negocio (patrón MVC) |
| `RepositoryUser.java` | Idem para usuarios |

### Fusiones
| Resultado | Fuentes | Qué se conservó |
|---|---|---|
| `model/Patient.java` | marte `Patient.java` + proyect_Patient `patient.java` | Campos originales de marte + validaciones del compact constructor de proyect_Patient |
| `model/User.java` | marte `User.java` + proyect_Patient `user.java` | Campos de marte (username/password) + validaciones de proyect_Patient |
| `service/serviceLogin.java` | marte `serviceLogin.java` + proyect_Patient `login.java` | `authenticate()`, `registerUser()`, `isAdmin()`, `handleLoginSuccess()`, + `validateAdminLogin()` |
| `controller/controllerAdmin.java` | marte `controllerAdmin.java` + proyect_Patient `login.java` (switch admin) | `showAdminMenu()` + casos: crear/ver/eliminar doctor, ver pacientes |
| `Main.java` | marte `Main.java` + proyect_Patient `main.java` | Flujo login + opción Register y Exit del proyect_Patient |

### Correcciones al migrar
- **Bug en `Getpatient()`**: En el original de proyect_Patient, el mensaje "Patient not found" se imprimía dentro del loop en cada iteración que no coincidía. Corregido para que solo se imprima si no se encontró al final.
- **`phone` es String**: proyect_Patient usaba `int` para teléfono, lo que no permite prefijos (+57). Se mantuvo como `String` igual que en marte.
- **Switch moderno**: Se usó switch expression (`->`) consistentemente como en proyect_Patient.

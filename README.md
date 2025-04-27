Customer Management API

Spring Boot REST API JWT alapú authentikációval, H2 adatbázissal.

## 🌐 Elérhetőség

- Alap URL: `http://localhost:8080`
- Adatbázis: Beépített **H2 Database** (`/h2-console` elérhető fejlesztéshez)


| Végpont                      | Módszer | USER hozzáférés | ADMIN hozzáférés | Leírás                                                                        |
|------------------------------|:-------:|:---------------:|:----------------:|-------------------------------------------------------------------------------|
| `/auth/register`              | POST    | ✅               | ✅                | Új felhasználó regisztráció                                               |
| `/auth/login`                 | POST    | ✅               | ✅                | Bejelentkezés, JWT token szerzés                                          |
| `/customer`                   | GET     | ❌               | ✅                | Összes customer listázása                                                 |
| `/customer/id`                | DEL     | ❌               | ✅                | customer törlése Id alapján                                               |
| `/custome/id`                 | GET     | ❌               | ✅                | customer listázása  id alapján                                            | 
| `/customer`                   | PUT     | ❌               | ✅                | customer modósítása új token adása saját user modósítása után             |
| `/customer/avarageAge`        | GET     | ✅               | ✅                | Customer átlag életkor lekérdezése                                        |
| `/customer/between18And40`    | GET     | ✅               | ✅                | 18 és 40 év közötti customer-ek listázása                                 |

## 👤 Jogosultságok összefoglaló

- **USER**:
  - Eléri: `/customer/avarageAge`, `/customer/between18And40`
- **ADMIN**:
  - Minden végpontot elér.

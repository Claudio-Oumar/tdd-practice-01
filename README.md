# tdd-practice-01

Spring Boot project for TDD practice. Base package: `ec.edu.epn`.

---

## Práctica: Desarrollo de la Capa de Servicio con TDD

### Contexto

El proyecto modela una **tienda virtual** con las siguientes entidades JPA ya
implementadas:

| Entidad | Descripción |
| --- | --- |
| `Category` | Categoría de productos (Electrónica, Libros, Ropa, Hogar) |
| `Customer` | Cliente registrado en la tienda |
| `Product` | Producto con SKU, nombre, precio, stock, categoría y estado activo/inactivo |
| `Order` | Pedido asociado a un cliente, con fecha, estado (`PENDING`, `COMPLETED`, `CANCELLED`) y total |
| `OrderItem` | Línea de un pedido: producto, cantidad, precio unitario y subtotal |

Los repositorios Spring Data JPA también están implementados en el paquete
`ec.edu.epn.repository`. La base de datos H2 se crea y se puebla automáticamente
con Flyway al iniciar la aplicación.

**Lo que falta** — y lo que ustedes deben construir — es la capa de servicio:
la lógica de negocio que orquesta estas entidades y repositorios.

### Objetivos de aprendizaje

1. **Comprender y aplicar el ciclo de vida de TDD: Red - Green - Refactor**
   (Rojo, Verde, Refactorizar).
2. **Diseñar interfaces y lógica de negocio desde la perspectiva de su
   consumo** (escribir la prueba primero).
3. **Aislar dependencias utilizando dobles de prueba** (Mocks con Mockito) en
   un entorno Spring Boot.
4. **Escribir pruebas unitarias deterministas, rápidas y legibles** utilizando
   JUnit 5.

### Requerimientos funcionales a implementar

Deben crear las clases de servicio en el paquete `ec.edu.epn.service`. Para
cada requerimiento, **escriban primero la prueba unitaria, veanla fallar (RED),
luego implementen el código mínimo para que pase (GREEN) y finalmente
refactoricen (REFACTOR)**.

#### Servicio 1: `ProductService`

| # | Requerimiento | Método |
| --- | --- | --- |
| P1 | Buscar un producto por SKU. Si no existe, lanzar `RuntimeException`. | `findBySku(String sku)` |
| P2 | Listar todos los productos activos (`active = true`). | `findActiveProducts()` |
| P3 | Verificar si hay stock suficiente para un producto dada una cantidad solicitada. Retorna `boolean`. | `hasSufficientStock(String sku, int quantity)` |
| P4 | Reducir el stock de un producto en una cantidad dada. Si el stock resultante es negativo, lanzar `RuntimeException`. | `reduceStock(String sku, int quantity)` |
| P5 | Listar todos los productos que pertenecen a una categoría. | `findByCategory(Long categoryId)` |

#### Servicio 2: `OrderService`

| # | Requerimiento | Método |
| --- | --- | --- |
| O1 | Crear un pedido (`Order`) para un cliente, dado su email y una lista de ítems (cada ítem contiene SKU del producto y cantidad). El pedido debe: validar que el cliente existe, validar que cada producto existe y tiene stock suficiente, reducir el stock de cada producto, calcular el total del pedido como la suma de los subtotales, y establecer el estado inicial en `PENDING`. Retornar el `Order` creado. | `createOrder(String customerEmail, List<OrderItemRequest> items)` |
| O2 | Obtener todas las órdenes de un cliente por su email. | `findOrdersByCustomer(String customerEmail)` |
| O3 | Cambiar el estado de una orden. Si la orden no existe, lanzar `RuntimeException`. | `updateOrderStatus(Long orderId, OrderStatus newStatus)` |
| O4 | Obtener las órdenes filtradas por estado (ej. todas las `PENDING`). | `findOrdersByStatus(OrderStatus status)` |

> **Nota para O1**: `OrderItemRequest` es un DTO (o record de Java) que deben
> crear en el paquete `ec.edu.epn.dto` con los campos `String sku` e
> `Integer quantity`.

### Restricciones técnicas

- **Usar Mockito** para simular (mockear) los repositorios en las pruebas. No
  se debe levantar el contexto de Spring (`@SpringBootTest`). Usar pruebas
  unitarias puras con `@ExtendWith(MockitoExtension.class)`.
- **Usar JUnit 5** (`@Test`, `@DisplayName`, `@BeforeEach`) y assertions
  expresivas (`assertEquals`, `assertThrows`, `assertTrue`, etc.).
- Cada prueba debe ser **independiente y determinista**: no debe depender del
  orden de ejecución ni de datos externos.
- Seguir estrictamente el ciclo **RED → GREEN → REFACTOR**. Hacer commits
  atómicos por cada fase es recomendable pero no obligatorio.

### Estructura de paquetes esperada

```
src/main/java/ec/edu/epn/
├── dto/
│   └── OrderItemRequest.java        ← Ustedes lo crean
├── service/
│   ├── ProductService.java          ← Ustedes lo crean
│   └── OrderService.java            ← Ustedes lo crean

src/test/java/ec/edu/epn/
└── service/
    ├── ProductServiceTest.java      ← Ustedes lo crean
    └── OrderServiceTest.java        ← Ustedes lo crean
```

### Criterios de evaluación

| Criterio | Peso |
| --- | --- |
| Las pruebas se escribieron primero y cubren todos los requerimientos | 30% |
| Uso correcto de Mockito para aislar dependencias | 25% |
| Cobertura de casos de error (excepciones, datos inválidos) | 20% |
| Código limpio, nombres expresivos, sin lógica duplicada | 15% |
| Commits atómicos que evidencian el ciclo RED-GREEN-REFACTOR | 10% |

### Comandos útiles

```bash
mvn test                          # ejecutar todas las pruebas
mvn test -Dtest=ProductServiceTest  # ejecutar una clase de prueba
```

---

## Configuración del proyecto

### Requirements

- Java 17+
- Maven 3.9+ (`mvn -v` to verify)

## Running the application

From the project root:

```bash
mvn spring-boot:run
```

The app will start on [http://localhost:8080](http://localhost:8080).

Other useful commands:

```bash
mvn clean package          # build the jar into target/
mvn test                   # run unit tests
java -jar target/tdd-practice-01-0.0.1-SNAPSHOT.jar
```

## Database

The project uses an **in-memory H2 database** configured in MySQL compatibility
mode. No external database setup is required — it is created fresh on every
startup.

Connection details (see `src/main/resources/application.properties`):

| Property | Value |
| --- | --- |
| JDBC URL | `jdbc:h2:mem:virtualstore;DB_CLOSE_DELAY=-1;MODE=MySQL` |
| Driver | `org.h2.Driver` |
| Username | `sa` |
| Password | *(empty)* |

### Schema and seed data (Flyway)

Migrations run automatically on startup from `src/main/resources/db/migration`:

- `V1__create_virtual_store_schema.sql` — creates tables: `categories`,
  `customers`, `products`, `orders`, `order_items`.
- `V2__seed_sample_data.sql` — inserts sample rows for each table.

JPA is set to `ddl-auto=validate`, so **Flyway owns the schema** — add a new
`V{n}__{description}.sql` file for any schema change instead of relying on
Hibernate auto-DDL.

### H2 console

While the app is running, open the web console at:

[http://localhost:8080/h2-console](http://localhost:8080/h2-console)

Use the JDBC URL, username, and password from the table above. Because the
database is in-memory, all data is lost when the application stops.

## Project layout

```
src/main/java/ec/edu/epn/
├── TddPractice01Application.java
├── model/         # JPA entities (Category, Customer, Product, Order, OrderItem, OrderStatus)
└── repository/    # Spring Data JPA repositories

src/main/resources/
├── application.properties
└── db/migration/  # Flyway migrations
```

## Testing

JUnit 5 and Mockito are on the test classpath via `spring-boot-starter-test`.

```bash
mvn test
```

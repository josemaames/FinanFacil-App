# FinanFácil 💰

FinanFácil es una aplicación móvil Android para el control financiero personal de forma offline. Permite registrar gastos, guardarlos localmente y consultar el total gastado desde el dispositivo.

## Funcionalidades

- Inicio de sesión con validación de correo electrónico.
- Registro de gastos con monto, categoría y descripción.
- Validación de datos antes de guardar un gasto.
- Adjuntar imagen de váucher desde el dispositivo.
- Listado de gastos registrados.
- Cálculo del total gastado.
- Eliminación de gastos.
- Botón para salir y volver al login.
- Almacenamiento local offline con Room.

## Tecnologías utilizadas

- Kotlin
- Android Studio
- XML Views
- ViewBinding
- Room Database
- Coroutines
- Material Design
- RecyclerView

## Requisitos

- Android Studio instalado.
- JDK configurado desde Android Studio.
- Emulador Android o dispositivo físico conectado.
- Conexión a internet para descargar dependencias Gradle la primera vez.

## Cómo ejecutar el proyecto

1. Clonar el repositorio:

   ```bash
   git clone https://github.com/josemaames/FinanFacil-App.git
   ```

2. Abrir Android Studio.

3. Seleccionar **File > Open**.

4. Abrir la carpeta del proyecto:

   ```text
   FinanFacil-App
   ```

5. Esperar a que Android Studio sincronice Gradle.

6. Seleccionar un emulador o dispositivo físico.

7. Presionar **Run** para ejecutar la app.

## Estructura principal

```text
app/src/main/java/upch/jamesss/finanfacil
```

Contiene las pantallas principales de la aplicación:

- `LoginActivity.kt`
- `MainActivity.kt`
- `RegisterExpenseActivity.kt`
- `ExpensesActivity.kt`
- `ExpenseAdapter.kt`

La base de datos local se encuentra en:

```text
app/src/main/java/upch/jamesss/finanfacil/data/local
```

## Estado del proyecto

La aplicación se encuentra en una versión inicial funcional. Actualmente permite registrar y consultar gastos personales de manera local, sin depender de servicios externos.

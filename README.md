# MoneyTracker - Control de Gastos Personales
​
 Una aplicación para Android diseñada para ayudarte a llevar un control detallado de tus ingresos y gastos personales. Con almacenamiento local, configuración personalizada y consulta de tasas de cambio en tiempo real.
​
 ## ✨ Funcionalidades Principales
​
 -   **Onboarding Inicial:** Configura tu nombre, presupuesto mensual, moneda y día de inicio del mes la primera vez que abres la app.
 -   **Dashboard Principal:** Un resumen visual de tus finanzas del mes, incluyendo ingresos, gastos, balance y una barra de progreso de tu presupuesto con alertas.
 -   **Gestión de Transacciones:**
     -   Lista de transacciones ordenada por fecha.
     -   Filtros por tipo (ingreso/gasto) y por categoría.
     -   Desliza para eliminar una transacción (con opción para deshacer).
     -   Haz clic para editar una transacción existente.
 -   **Formulario Inteligente:** Añade o edita transacciones fácilmente.
 -   **Conversor de Moneda:** Convierte montos en tiempo real usando la API de ExchangeRate-API.
 -   **Estadísticas Detalladas:** Visualiza tus gastos por categoría con un gráfico circular y conoce tu gasto promedio diario.
 -   **Configuración Flexible:** Modifica tus preferencias en cualquier momento y restablece todos los datos de la aplicación si lo necesitas.
​
 ## 🛠️ Tecnologías y Librerías
​
 -   **Lenguaje:** Java
 -   **Base de Datos:** SQLite para el almacenamiento local de todas las transacciones y categorías.
 -   **Arquitectura:** Patrón Repositorio para la gestión de datos.
 -   **Red (Networking):** Retrofit para consumir la API de tasas de cambio.
 -   **Gráficos:** MPAndroidChart para la visualización de estadísticas.
 -   **UI:** Componentes de Material Design (MaterialCardView, RecyclerView, etc.).
​
 ## 🚀 Instalación y Puesta en Marcha
​
 1.  **Clona ste repositorio:**
     ```sh
     git clone https://github.com/LindaBalarezo/MoneyTracker-_BalarezoLinda.git
     ```
 2.  Abre el proyecto en Android Studio.
 3.  Espera a que Gradle sincronice todas las dependencias.
 4.  Ejecuta la aplicación en un emulador o dispositivo físico.
​
 ## 🔌 API
 
 Este proyecto utiliza la API gratuita de [ExchangeRate-API](https://www.exchangerate-api.com/) para la conversión de monedas. No se requiere una clave de API para el plan gratuito que se está utilizando.
​

# RentiTem 🛠️ - Plataforma de Alquiler de Artículos

RentiTem es una aplicación móvil de economía colaborativa que permite a los usuarios alquilar herramientas y artículos entre sí. El proyecto destaca por su capacidad **offline-first**, comunicación en tiempo real y una arquitectura moderna y escalable.

---

## 🏗️ Arquitectura del Sistema

La aplicación sigue los principios de **Clean Architecture** y el patrón **MVVM (Model-View-ViewModel)**, asegurando un desacoplamiento total entre la lógica de negocio y la interfaz de usuario.

### Capas del Proyecto (Android):
1.  **Presentation (UI):** Desarrollada íntegramente en **Jetpack Compose**. Maneja el estado de forma reactiva mediante `UiState` y `StateFlow`.
2.  **Domain:** Contiene las entidades de negocio y los casos de uso (UseCases). Es código Kotlin puro, sin dependencias de frameworks externos.
3.  **Data:** Implementa el patrón **Repository**. Gestiona tres fuentes de datos:
    *   **Retrofit:** Comunicación con el backend en Go (Publicaciones y Perfil).
    *   **Room:** Persistencia local para soporte offline.
    *   **Firebase Firestore:** Motor de chat en tiempo real.

---

## 🛠️ Stack Tecnológico

### Móvil (Android):
*   **Lenguaje:** Kotlin 2.0.21 (Compilador K2).
*   **UI:** Jetpack Compose con Material Theme 3.0.
*   **DI:** Hilt (Dagger) para Inyección de Dependencias.
*   **Async:** Corrutinas y Flow para flujos de datos asíncronos.
*   **Database:** Room para el caché local.
*   **Background:** WorkManager para envío de mensajes diferidos.

### Backend & Servicios:
*   **API Principal:** Go (Golang) con GORM y Gin (Gestión de ítems y usuarios).
*   **Auth:** Firebase Authentication (Google & Email).
*   **Chat:** Firebase Firestore (Subcolecciones anidadas).
*   **Storage:** Firebase Storage (Imágenes de productos y perfiles).
*   **Notificaciones:** Firebase Cloud Functions (Node.js) + FCM.

---

## ✨ Características Principales

### 📡 Comunicación en Tiempo Real
Chat persona a persona con notificaciones push inteligentes. Las notificaciones incluyen la foto circular del remitente y el sistema gestiona un **Badge (contador)** de mensajes no leídos en tiempo real en la pantalla de inicio.

### 📶 Soporte Offline (Offline-First)
Gracias a la integración de **Room** y **WorkManager**, los usuarios pueden:
*   Ver publicaciones previamente cargadas sin internet.
*   Escribir mensajes que se enviarán automáticamente cuando se recupere la conexión.

### 🎨 Identidad Visual Consistente
Se ha desactivado el sistema de colores dinámicos de Android 12+ para garantizar que la marca **RentiTem** (paleta café/crema) se mantenga uniforme en todos los dispositivos, incluyendo el soporte nativo para **Modo Oscuro**.

### 📍 Geolocalización
Integración con sensores GPS para la ubicación precisa de los artículos, utilizando `osmdroid` para la visualización en mapas interactivos.

---

## 🚀 Instalación y Configuración

1.  **Firebase:** Es necesario añadir el archivo `google-services.json` en la carpeta `app/` y registrar la huella SHA-1 para el correcto funcionamiento de Auth y Chat.
2.  **Backend:** Configurar la `baseUrl` en el `AppContainer` apuntando a la IP local o servidor donde se ejecute la API de Go.
3.  **Compilación:** Utilizar Android Studio Ladybug o superior con soporte para el compilador K2 de Kotlin.

---

## 👷 Trabajo Futuro
*   Integración de pasarela de pagos (Stripe/PayPal).
*   Sistema de reseñas y reputación de arrendadores.
*   Filtros avanzados por categoría y radio de distancia GPS.

---
*Desarrollado como proyecto de Ingeniería de Software - 2026.*

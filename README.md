# Jet X — Plataforma Móvil de Reserva de Jets Privados

Jet X es una aplicación móvil nativa para Android desarrollada en Kotlin y Jetpack Compose, inspirada en el modelo de movilidad bajo demanda tipo Uber pero adaptada al sector de la aviación privada ejecutiva. La plataforma permite a los usuarios reservar aeronaves completas o asientos individuales mediante un visor 3D de cabina, cálculo dinámico de tarifas en tiempo real, mapas satelitales con radar de tráfico aéreo y asistencia conversacional impulsada por inteligencia artificial.

---

## Características Principales

*   **Buscador Inteligente de Aeropuertos:** Búsqueda rápida y tolerante a errores ortográficos de terminales ejecutivas y códigos ICAO/IATA conectada a Algolia y bases de datos aeronáuticas.
*   **Radar y Trazado de Rutas en Vivo:** Mapa interactivo en modo oscuro con Google Maps Compose SDK, animación geodésica de rutas de vuelo y visualización de tráfico aéreo en tiempo real mediante OpenSky Network.
*   **Visor de Cabina 3D:** Renderizado de modelos tridimensionales (`.glb`) con Google Sceneview/Filament para inspección de interiores y selección táctil interactiva de asientos.
*   **Motor Propietario de Precios Dinámicos (Yield Engine):** Algoritmo nativo en Kotlin que calcula tarifas a partir de la distancia de Gran Círculo (ortodrómica), tiempo de vuelo, recargos de urgencia temporal (< 8h / > 48h), meteorología en ruta (viento en contra, tormentas), congestión en terminales FBO y exceso de equipaje.
*   **Conversor de Divisas en Tiempo Real:** Cotización de importes en USD, EUR, GBP y otras monedas internacionales mediante Frankfurter API.
*   **Concierge Virtual con IA (Google Gemini):** Asistente integrado con *Function Calling / Tool Use* capaz de interpretar lenguaje natural, consultar disponibilidad de aeronaves y manipular el estado de la interfaz de usuario de forma desatendida.
*   **Pasarela de Pago Segura:** Procesamiento de cobros y preautorizaciones bancarias con Stripe Android SDK (`PaymentSheet`).
*   **Emisión de Boletos Digitales:** Envío automático de confirmaciones de vuelo con diseño ejecutivo en HTML/CSS responsivo a través de Brevo API (v3 SMTP).
*   **Autenticación Moderna:** Inicio de sesión y registro seguro con Google OAuth 2.0 mediante Credential Manager API.

---

## Arquitectura de Software

* La aplicación móvil implementa los principios de **Clean Architecture** junto con el patrón de presentación **MVVM / MVI**, garantizando una estricta separación de responsabilidades y la unidireccionalidad en el flujo de datos:
---
## Integrantes del desarrollo
* Durante todo el desarollo contamos con la participación de las siguentes personas:   
* **MIGUEL ANGEL CRUZ OLMEDO**              **27-2723-2025**

* **RICARDO PEDRO ORANTES CUELLAR**     **27-6587-2013**
* **ADRINA MELISSA ROQUE DE PAZ**              **25-0674-2022**
* **JESÚS ENRIQUE MARTINEZ JUÁREZ**           **27-0637-2025**
* **GEOVANNY DANIEL BARRERA HERRERA**        **25-0615-2022**
* **ABNER ESAU VASQUEZ DIAZ**                       **23-3426-2022**
* **DIEGO ALEJANDRO SAMAYOA TORRES**       **17-2106-2021**
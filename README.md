# 🚀 stock overflow - Backend

Das **stock overflow Backend** ist eine robuste Spring Boot Applikation, die als Herzstück für die gleichnamige Web-Applikation dient. Es stellt die notwendigen REST-Schnittstellen bereit, um Aktiendaten in Echtzeit zu suchen, historische Kursverläufe abzurufen und persönliche Watchlists sicher zu verwalten.

Das Projekt wurde im Rahmen des Moduls **Webtechnologien an der HTW Berlin** entwickelt.

---

## ✨ Hauptfunktionen

### 🔍 Aktiendata & Suche
*   **Integration externer APIs:** Anbindung an die [Financial Modeling Prep (FMP) API](https://financialmodelingprep.com/) für zuverlässige Marktdaten.
*   **Historische Kursdaten:** Aufbereitung von Kursverläufen für interaktive Charts (30 Tage bis 3 Jahre).
*   **Effiziente Suche:** Schnellsuche nach Tickersymbolen und Firmennamen.

### ⭐ Watchlist-Management
*   **Benutzerspezifische Listen:** Speichern und Verwalten von Favoriten pro Benutzer.
*   **CRUD-Operationen:** Vollständige Unterstützung zum Hinzufügen, Auslesen und Entfernen von Aktien aus der Watchlist.
*   **Persistenz:** Sichere Speicherung der Benutzerdaten in einer relationalen Datenbank.

### 🛠️ Infrastruktur
*   **Docker-Unterstützung:** Containerisierte Bereitstellung für einfache Skalierung und Deployment.
*   **Datenbank-Migration:** Vorbereitet für automatisierte Schema-Updates.
*   **CORS-Konfiguration:** Optimiert für die sichere Kommunikation mit dem Nuxt.js Frontend.

---

## 🚀 Technologien

Das Backend nutzt moderne Java-Technologien:

*   **Framework:** [Spring Boot 3.5.6](https://spring.io/projects/spring-boot)
*   **Sprache:** [Java 25](https://openjdk.org/projects/jdk/25/)
*   **Datenbank:** [PostgreSQL](https://www.postgresql.org/) (Produktion), [H2](https://www.h2database.com/) (Testing/Lokal)
*   **ORM:** Spring Data JPA / Hibernate
*   **API-Client:** Spring RestTemplate
*   **Build-Tool:** Gradle
*   **Deployment:** Docker & Docker Hub
*   **Testing:** JUnit 5 & MockMvc

---

## 🛠️ Installation & Setup

### Voraussetzungen
*   JDK 25 installierte
*   Docker (optional für Container-Start)
*   Ein gültiger [FMP API Key](https://financialmodelingprep.com/developer/docs/)

### 1. Umgebungsvariablen setzen
Die Applikation benötigt folgende Umgebungsvariablen (z.B. in der `application.properties` oder als System-Variablen):

```properties
DB_URL=jdbc:postgresql://your-db-host:5432/dbname
DB_USERNAME=your-username
DB_PASSWORD=your-password
FMP_API_KEY=your-api-key
```

### 2. Projekt bauen
```bash
./gradlew build
```

### 3. Anwendung starten
```bash
./gradlew bootRun
```
Die API ist standardmäßig unter `http://localhost:8080` erreichbar.

### 4. Mit Docker starten
```bash
docker build -t finance-app-backend .
docker run -p 8080:8080 -e DB_URL=... finance-app-backend
```

---

## 📍 API Endpunkte (Auszug)

### Aktien & Kurse
*   `GET /api/stocks/all` - Liste aller unterstützten/gecacheten Aktien.
*   `GET /api/stocks/{symbol}` - Historische Kursdaten für ein Tickersymbol.
*   `GET /api/stocks/search?query={...}` - Suche nach Aktien.

### Watchlist
*   `GET /api/watchlist/{userId}` - Watchlist eines Benutzers abrufen.
*   `POST /api/watchlist/{userId}` - Aktie zur Watchlist hinzufügen.
*   `DELETE /api/watchlist/{userId}/{symbol}` - Aktie aus der Watchlist entfernen.

---

## 📁 Projektstruktur (Auszug)

*   `src/main/java/.../controller/` - REST-Controller (API-Endpunkte)
*   `src/main/java/.../service/` - Geschäftslogik und API-Kommunikation
*   `src/main/java/.../entity/` - Datenmodelle (JPA Entities)
*   `src/main/java/.../repository/` - Datenbankzugriffsschicht
*   `src/main/resources/` - Konfiguration (`application.properties`) und Initial-Daten (`data.sql`)

---

## 👥 Entwickler
Entwickelt von:
*   🧑🏻‍💻 **Rami Eter**
*   🧑🏻‍💻 **Christian Püschel**

HTW Berlin – Projekt Webtechnologien 2026

package FinanceApp.FinanceApp.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Konfigurationsklasse für Cross-Origin Resource Sharing (CORS) in der Finance-Anwendung.
 *
 * <p>Diese Klasse ermöglicht es, dass das Backend von verschiedenen Origins (Domains) aus
 * aufgerufen werden kann. Ohne CORS-Konfiguration würden Browser aus Sicherheitsgründen
 * Requests von anderen Domains blockieren (Same-Origin Policy).</p>
 *
 * <h2>Konfigurierte Origins:</h2>
 * <ul>
 *   <li>localhost:3000 - Entwicklungsumgebung für das Frontend (z.B. React)</li>
 *   <li>localhost:8081 - Alternative lokale Entwicklungsumgebung</li>
 *   <li>stockoverflow-80m4.onrender.com - Produktions-Frontend auf Render.com</li>
 *   <li>stockoverflow-backend-1.onrender.com - Produktions-Backend auf Render.com</li>
 * </ul>
 *
 * <h2>Erlaubte HTTP-Methoden:</h2>
 * GET, POST, PUT, DELETE, OPTIONS
 *
 * <h2>Sicherheitshinweise:</h2>
 * <ul>
 *   <li><b>Credentials sind aktiviert:</b> Erlaubt das Senden von Cookies und Auth-Headers</li>
 *   <li><b>Alle Header sind erlaubt (allowedHeaders("*")):</b>
 *       <p><strong>⚠️ Sicherheitsrisiko für Produktionsumgebungen!</strong></p>
 *       <p>Gründe für Einschränkung in Produktion:</p>
 *       <ul>
 *         <li><em>Principle of Least Privilege:</em> Nur benötigte Header sollten erlaubt sein</li>
 *         <li><em>Vergrößerte Angriffsfläche:</em> Potentielle Header Injection Attacks möglich</li>
 *         <li><em>Header-Spoofing:</em> Manipulation von Forwarding-Headers (X-Forwarded-For, X-Real-IP)</li>
 *         <li><em>Unerwartetes Verhalten:</em> Unbekannte Header können zu Sicherheitslücken führen</li>
 *       </ul>
 *       <p><strong>Empfohlene Produktions-Konfiguration:</strong></p>
 *       <pre>
 *       .allowedHeaders(
 *           "Content-Type",      // Für JSON/XML Requests
 *           "Authorization",     // Für Bearer Tokens
 *           "Accept",           // Content Negotiation
 *           "X-Requested-With"  // AJAX-Identifikation
 *       )
 *       </pre>
 *   </li>
 *   <li><b>Alle Endpoints (/**):</b> CORS gilt für alle API-Endpoints - ggf. spezifischer konfigurieren</li>
 * </ul>
 *
 * @author christiancode1296
 * @version 1.0
 * @since 1.0
 * @see WebMvcConfigurer
 * @see CorsRegistry
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    /**
     * Konfiguriert die CORS-Mappings für die gesamte Anwendung.
     *
     * <p>Diese Methode wird automatisch beim Start der Anwendung von Spring aufgerufen
     * und registriert die CORS-Konfiguration für alle Endpoints.</p>
     *
     * <h3>Konfigurationsdetails:</h3>
     * <ul>
     *   <li><b>Mapping Pattern:</b> "/**" - gilt für alle API-Endpoints</li>
     *   <li><b>Allowed Origins:</b> Spezifische Liste von erlaubten Domains (siehe Klassendokumentation)</li>
     *   <li><b>Allowed Methods:</b> GET, POST, PUT, DELETE, OPTIONS - Standard REST-Operationen</li>
     *   <li><b>Allowed Headers:</b> "*" - ⚠️ ALLE Request-Headers sind erlaubt (siehe Sicherheitshinweise in Klassendokumentation)</li>
     *   <li><b>Allow Credentials:</b> true - ermöglicht das Senden von Cookies und Authentifizierungs-Informationen</li>
     * </ul>
     *
     * <h3>Beispiel eines erlaubten Requests:</h3>
     * <pre>
     * fetch('http://localhost:8080/api/stocks', {
     *   method: 'GET',
     *   credentials: 'include',
     *   headers: {
     *     'Content-Type': 'application/json',
     *     'Authorization': 'Bearer token123'
     *   }
     * });
     * </pre>
     *
     * <h3>TODO für Produktionsumgebung:</h3>
     * <ul>
     *   <li>allowedHeaders("*") durch spezifische Header-Liste ersetzen</li>
     *   <li>Ggf. Mapping auf spezifische Endpoints einschränken (z.B. "/api/**")</li>
     *   <li>Produktions-Origins überprüfen und aktualisieren</li>
     * </ul>
     *
     * @param registry Das CorsRegistry-Objekt, das die CORS-Mappings verwaltet
     * @throws IllegalArgumentException wenn ungültige Konfigurationswerte übergeben werden
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        System.out.println("🔧 CORS Config wird geladen!");
        registry.addMapping("/**")
                .allowedOrigins(
                        "http://localhost:3000",
                        "http://localhost:8081",
                        "https://stockoverflow-80m4.onrender.com",
                        "https://stockoverflow-backend-1.onrender.com"
                )
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);
    }
}
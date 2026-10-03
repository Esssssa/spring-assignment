## Session plan

|Section|Topic|
|---|---|
|0|Recap & bridge from Session 1|
|1|Configuration for real: precedence, profiles, `@ConfigurationProperties`|
|2|Logging|
|3|Developer productivity: DevTools|
|4|Production visibility: Actuator|

---

## 0. Recap & bridge

From Session 1:

- **Starters + BOM** decide what's on the classpath and its version.
- **Auto-configuration** turns that into beans, and always **backs off** for your own bean/property.
- **Embedded server** runs the result as one jar.
- `application.yml` can hold simple values, read with `@Value`.

The gap this session closes: Session 1's `application.yml` had one file and one environment. A real app runs in **dev, CI, staging, prod** — different database URLs, different log levels, secrets that must never sit in git. That's what sections 1–4 are for.

---

## 1. Configuration for real

### 1.1 Where does a value come from? — precedence

Boot merges many **property sources**. **Higher overrides lower**:

| Priority    | Source                                            | Typical use                    |
| ----------- | ------------------------------------------------- | ------------------------------ |
| 1 (highest) | **Command-line args** `--server.port=9090`        | one-off override               |
| 2           | **JVM system properties** `-Dserver.port=9090`    | JVM-level config               |
| 3           | **OS environment variables** `SERVER_PORT=9090`   | containers, cloud, **secrets** |
| 4           | **Profile-specific files** `application-prod.yml` | per-environment                |
| 5           | **`application.yml`**                             | shared defaults                |
| 6 (lowest)  | Defaults in code / auto-configuration             | fallback                       |

---
### 1.1 Profiles — per-environment configuration

**Profiles are a way to have different configurations/beans for different environments or situations**.
**Why you need it:** the same jar runs everywhere (dev, staging, prod), but each environment needs different settings — different DB, different log level. Profiles let the _config_ change per environment without rebuilding the jar or writing `if (env == "prod")` in your code.

**A concrete example you can already picture — `ProductController`:** imagine that controller backed by a real database instead of the in-memory map. Every time you restart while developing, you'd have to manually re-POST a few products just to have something to test against. A `dev` profile fixes that with almost no ceremony:

```java
@Configuration
@Profile("dev")
class DevDataSeeder {
    @Bean
    CommandLineRunner seedProducts(ProductRepository repo) {
        return args -> {
            repo.save(new Product("Keyboard", 49.99));
            repo.save(new Product("Mouse", 19.99));
        };
    }
}
```

Run with `dev` active, and your app boots with two sample products already there — zero manual setup, every single time. Run in `prod` (or with no profile active), and this class is invisible to Spring entirely — `@Profile("dev")` means "only register this bean when `dev` is active," so production never even sees a seeder that could otherwise overwrite real data. That's the "already useful in real work" case: it's not just about _logging levels_ differing per environment, it's about entire pieces of behavior existing only where they make sense.

```
application.yml          ← always loaded
application-dev.yml      ← loaded when profile "dev" is active
application-prod.yml     ← loaded when profile "prod" is active
```

Activate a profile:

```bash
java -jar app.jar --spring.profiles.active=prod
SPRING_PROFILES_ACTIVE=prod java -jar app.jar
```

Profile-specific **beans**:

```java
@Configuration
@Profile("dev")
class DevDataConfig {
    @Bean
    CommandLineRunner seed(ProductRepository repo) { ... }   // only runs in dev
}
```

### 1.3 `@ConfigurationProperties` — typed, grouped configuration

Session 1 used `@Value` for one-off values. Once you have a **group** of related settings, that gets awkward fast — this is the typed alternative.

```yaml
shop:
  name: Demo Shop
  currency: EGP
  page-size: 20
```

```java
@ConfigurationProperties(prefix = "shop")
public record ShopProperties(
        String name,
        @DefaultValue("USD") String currency,
        @DefaultValue("20") int pageSize) {
}
```

Register it (once) on the main class:

```java
@SpringBootApplication
@ConfigurationPropertiesScan
public class ShopApplication { ... }
```

Inject it like any bean:

```java
@RestController
class InfoController {
    private final ShopProperties props;

    InfoController(ShopProperties props) { this.props = props; }

    @GetMapping("/api/info")
    ShopProperties info() { return props; }
}
```

---

## 2. Logging

Boot uses **SLF4J + Logback** with a ready-made format. **Do not use `System.out.println`** in real code.

### 2.1 Why not `println`, specifically

`println` looks harmless in a quick test, but it's missing everything a real deployment needs:

- **No levels, so no filtering.** `println` always prints. The only way to "turn it off" in production is to delete the line and redeploy. A real logger lets you leave the line in forever and just dial the verbosity up or down via config (section 1's `application.yml`, no code change, no redeploy).
- **No structured metadata.** A logger call automatically stamps each line with a timestamp, thread name, log level, and logger name (which class it came from). `println` gives you the raw string and nothing else — no way to tell _when_ it happened, on _which thread_, or _how severe_ it was, without hand-rolling that yourself every time.
- **Wasted work for messages nobody will see.** `log.debug("Order details: {}", this)` only builds that string if `DEBUG` is actually enabled — the placeholder isn't filled in otherwise. `println("Order details: " + this)` always concatenates the string, every single call, even in production where nobody's watching `DEBUG` output.

### 2.2 Is there a default level if you don't configure anything?

Yes — **`INFO`** is the root default the moment you add any Boot starter (Logback ships with `spring-boot-starter-logging`, pulled in transitively). Without touching `logging.level` at all, you already get `INFO`, `WARN`, and `ERROR` messages on the console, and `DEBUG`/`TRACE` are silent. You only add a `logging.level` block once you want _more_ than that default, for some package.

Ordered from least to most verbose — setting a level shows everything to its left:

```
ERROR  <  WARN  <  INFO  <  DEBUG  <  TRACE
(least verbose)                 (most verbose)
```

|You set the level to...|You will see|
|---|---|
|`ERROR`|`ERROR` only|
|`WARN`|`WARN`, `ERROR`|
|`INFO` (the default)|`INFO`, `WARN`, `ERROR`|
|`DEBUG`|`DEBUG`, `INFO`, `WARN`, `ERROR` — **not** `TRACE`|
|`TRACE`|everything — the only setting that includes `TRACE`|

### 2.4 What each level is actually _for_

```java
@Service
class OrderService {
    private static final Logger log = LoggerFactory.getLogger(OrderService.class);

    void place(long id) {
        log.info("Placing order {}", id);        // parameterized: no manual string concatenation
        log.debug("Order details: {}", this);    // message is only formatted if DEBUG is enabled
    }
}
```

```yaml
logging:
  level:
    com.example.shop: DEBUG
```

| Level   | Use it for                                                                                                                                              | Example, for `OrderService`                                               |
| ------- | ------------------------------------------------------------------------------------------------------------------------------------------------------- | ------------------------------------------------------------------------- |
| `ERROR` | Something **failed** and needs a human's attention — this is usually what triggers an on-call alert.                                                    | `log.error("Payment failed for order {}", id, exception);`                |
| `WARN`  | Something **unexpected happened but the app recovered** — worth a human noticing eventually, not urgent right now.                                      | `log.warn("Retrying payment gateway call for order {} (attempt 2)", id);` |
| `INFO`  | **Key business/lifecycle events**, meant to stay on permanently, even in production — the normal narrative of "what the app is doing."                  | `log.info("Order {} placed by customer {}", id, customerId);`             |
| `DEBUG` | **Developer detail**, useful while actively chasing a specific bug — too noisy to leave on for everyone, so you flip it on for one package temporarily. | `log.debug("Calculated total {} before tax for order {}", total, id);`    |
| `TRACE` | **Extremely fine-grained, step-by-step internals** — loop iterations, every method entry/exit. You'll rarely write this yourself;                       | Framework-level, e.g. Hibernate logging every SQL parameter bind.         |

---

## 3. Developer productivity: DevTools

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-devtools</artifactId>
    <scope>runtime</scope>
    <optional>true</optional>
</dependency>
```

- **Automatic restart** when classes change.
- **Never active** in production.
- ⚠️ Boot 4: **live reload is disabled by default** — enable with `spring.devtools.livereload.enabled=true`.

---

## 4. Production visibility: Actuator

> [!important] is a Spring Boot feature that gives you **endpoints for monitoring and inspecting your running application**.

### 4.2 A worked example for each endpoint

**`/actuator/health`** — with a Postgres dependency configured, a healthy app returns:

```json
{ "status": "UP" }
```

If the database connection drops:

```json
{
  "status": "DOWN",
  "components": {
    "db": { "status": "DOWN", "details": { "error": "Connection refused" } },
    "diskSpace": { "status": "UP" }
  }
}
```

That second response is the whole point: the **process** never stopped, but the app is telling you, precisely, _which_ dependency is the problem — something a cluster-level "is the pod running" check would never surface.

**`/actuator/info`** — not business data, **build/version identity**. Configure what it shows via properties:

```yaml
info:  
  app:  
    name: Task Tracker  
    version: 1.0.0
```

```json
"app": {
        "name": "Task Tracker",
        "version": "1.0.0"
    }
```

**`/actuator/metrics`** — numeric, over-time operational data: JVM heap usage, garbage collection pauses, thread counts, HTTP request counts and latency per endpoint, and any custom counters you define. A single call, e.g. `/actuator/metrics/http.server.requests`, returns something like:

```json
{
  "name": "http.server.requests",
  "measurements": [
    { "statistic": "COUNT", "value": 1532 },
    { "statistic": "TOTAL_TIME", "value": 245.7 },
    { "statistic": "MAX", "value": 3.2 }
  ]
}
```


Add the starter:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```

This is pillar 1 + 2 again, one more time: adding the starter puts Actuator's auto-configuration classes on the classpath, and Boot creates the endpoint beans for you — same mechanism as everything in Session 1, just a different starter.

By default only `/actuator/health` is exposed over HTTP. Expose more deliberately:

```yaml
management:
  endpoints:
    web:
      exposure:
        include: health, info, metrics
```

|Endpoint|Shows|
|---|---|
|`/actuator/health`|Is the app (and its dependencies — DB, disk…) up?|
|`/actuator/info`|Whatever you put under `info.*` in properties, or build info|
|`/actuator/metrics`|JVM, HTTP, and custom metrics|

---
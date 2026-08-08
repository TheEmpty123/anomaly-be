# Stellar Market Data Backend

Spring Boot service for reading market-data and analytics views from the Stellar data mart. It provides REST endpoints for OHLCV, index valuation and impact, stock anomalies, foreign flow, sector analytics, and Redis-backed realtime market data.

The public API is documented in [docs/API_REFERENCE.md](docs/API_REFERENCE.md). That document is the endpoint-level contract; this README focuses on running and operating the service.

## At a glance

| Item | Value |
| --- | --- |
| Runtime | Java 17 |
| Framework | Spring Boot 3.4.4 |
| Build tool | Maven Wrapper 3.9.11 |
| Artifact | com.mobile:be 1.0.9 |
| Default HTTP port | 8080 |
| Default API prefix | /stellar-api/v1 |
| Health endpoint | /actuator/health |
| Data stores | PostgreSQL data mart and Redis |

## Prerequisites

- JDK 17
- A reachable PostgreSQL instance containing the pre-provisioned Stellar data-mart schema
- A reachable Redis instance

The application maps existing JPA entities but does not include Flyway, Liquibase, or schema-initialization configuration. It expects the database schema and data to be provisioned outside this repository.

## Configure a local environment

The defaults in application.properties are development conveniences only. Set explicit environment variables for a real environment and never commit secrets.

| Variable | Purpose | Default |
| --- | --- | --- |
| DM_URL | Complete JDBC URL; takes precedence over the composed DM connection | jdbc:postgresql://localhost:5432/stellar_dm |
| DM_HOST, DM_PORT, DM_NAME | Components used when DM_URL is not set | localhost, 5432, stellar_dm |
| DM_USER, DM_PASSWORD | Data-mart credentials | postgres, 1 |
| REDIS_HOST, REDIS_PORT | Redis connection | localhost, 6379 |
| MARKET_PUBSUB_CHANNEL | Redis Pub/Sub channel for market updates | market_channel |
| SERVER_PORT | HTTP port | 8080 |
| STELLAR_JWT_ENABLED | Enables JWT validation for the default Stellar API path | true |
| STELLAR_JWT_SECRET | HMAC signing secret; use a strong secret of at least 32 bytes for HS256 | dev-secret-change-me (unsafe and too short for HS256) |
| STELLAR_JWT_ISSUER | Required JWT issuer claim | stellar-api |
| STELLAR_JWT_AUDIENCE | Required JWT audience claim | stellar-backend |
| API_STELLAR_BASE_PATH | Controller base path | /stellar-api/v1 |

Standard Spring variables such as SPRING_DATASOURCE_URL, SPRING_DATASOURCE_USERNAME, and SPRING_DATASOURCE_PASSWORD also work and are used by the Helm deployment.

For a local exploratory run without a token issuer, disable JWT only in your shell:

~~~powershell
$env:DM_URL = "jdbc:postgresql://localhost:5432/stellar_dm"
$env:DM_USER = "postgres"
$env:DM_PASSWORD = "<password>"
$env:REDIS_HOST = "localhost"
$env:REDIS_PORT = "6379"
$env:STELLAR_JWT_ENABLED = "false"   # Local development only
~~~

Important: while JWT is enabled, keep the default API prefix. Controller mappings can be changed with API_STELLAR_BASE_PATH, but the current JWT filter and its SSE query-token fallback are coded for the literal default path /stellar-api/v1.

## Build and run

On Windows:

~~~powershell
.\mvnw.cmd clean package
.\mvnw.cmd spring-boot:run
~~~

On macOS or Linux:

~~~bash
./mvnw clean package
./mvnw spring-boot:run
~~~

The packaged application is written to target/backend.jar:

~~~powershell
java -jar target/backend.jar
~~~

Verify that the service is running:

~~~powershell
curl http://localhost:8080/actuator/health
~~~

With JWT enabled, Stellar API requests require a valid token:

~~~powershell
curl -H "Authorization: Bearer <JWT_TOKEN>" "http://localhost:8080/stellar-api/v1/ohlcv/latest?timeframe=1d"
~~~

There is no Swagger/OpenAPI UI configured in this project.

## Tests

~~~powershell
.\mvnw.cmd test
~~~

The test suite includes a Spring context test, so it starts the application's PostgreSQL and Redis integrations. Both services must be reachable with the configured credentials; there is no isolated test profile or Testcontainers setup. If Redis is unavailable, the context test fails during listener startup.

## API behavior

- All endpoints below the default Stellar path require a JWT when STELLAR_JWT_ENABLED is true.
- Every HTTP response includes an X-Correlation-ID header for request tracing.
- The only exposed Actuator endpoint is /actuator/health. Health details are enabled by configuration, so restrict access appropriately in production.
- Empty collections are returned as 200 with an empty array unless an endpoint explicitly documents 204 No Content.

See [the full API reference](docs/API_REFERENCE.md) for endpoint parameters, response schemas, pagination limits, JWT requirements, and SSE usage.

## Docker

The Dockerfile uses a Maven/Temurin 17 build stage and produces an image that exposes port 8080.

~~~bash
docker build -t stellar-backend:local .
docker run --rm -p 8080:8080 -v /data/backend/logs:/data/backend/logs \
  -e SPRING_DATASOURCE_URL="jdbc:postgresql://host.docker.internal:5432/stellar_dm" \
  -e SPRING_DATASOURCE_USERNAME="postgres" \
  -e SPRING_DATASOURCE_PASSWORD="<password>" \
  -e REDIS_HOST="host.docker.internal" \
  -e STELLAR_JWT_ENABLED="false" \
  stellar-backend:local
~~~

Pass real PostgreSQL, Redis, and JWT settings for any non-local deployment. The image build skips tests; run the appropriate verification before building a release image.

### Persistent logs

Logback writes the general application log, one log per active controller, and the heatmap SSE-service log to /data/backend/logs. Every file rolls daily or after 100 MB, compresses completed files, and retains 30 days of history.

The Docker image declares this directory as a volume. The Helm chart mounts the Kubernetes node directory at the same path with a hostPath volume. The container image runs as its default root user, so the Kubelet-created DirectoryOrCreate path is writable. If the deployment is later changed to a non-root user, the node directory must be owned by or writable to that UID and group before rollout.

## Helm and CI/CD

The Helm chart lives in helm/backend. It deploys a ClusterIP service on port 8080, mounts /data/backend/logs from the node for persistent logs, and reads database credentials and the JWT secret from the configured Kubernetes secret (backend-db-secret by default).

~~~bash
helm upgrade --install backend helm/backend
~~~

The Jenkins pipeline reads the Maven project version, builds target/backend.jar, pushes localhost:5000/backend:<version>, and runs Helm with the matching image tag. Review [VersionControl.md](VersionControl.md) before changing release versions.

The chart currently does not define readiness or liveness probes. Add them before relying on automated Kubernetes traffic management.

## Project layout

~~~text
src/main/java/com/mobile/backendjava/
  BackendJavaApplication.java
  dm/config/                 # JWT, Redis, request tracing, executor configuration
  dm/controllers/stellar/    # Active HTTP API controllers
  dm/dto/                    # Response and query DTOs
  dm/entities/               # JPA mappings for the data mart
  dm/repository/             # PostgreSQL queries
  dm/service/                # Business and Redis/SSE services
src/main/resources/
  application.properties
docs/
  API_REFERENCE.md
helm/backend/
Dockerfile
Jenkinsfile
~~~

## Documentation

- [API reference](docs/API_REFERENCE.md)
- [Versioning and release notes](VersionControl.md)

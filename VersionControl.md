# Versioning and releases

This project has three version values that serve different purposes:

| Location | Purpose |
| --- | --- |
| pom.xml | Application and Docker image version used by Jenkins |
| helm/backend/Chart.yaml | Helm chart version and chart appVersion |
| helm/backend/values.yaml | Default image tag for manual Helm deployments |

The current CI pipeline treats the Maven project version as the image version. It reads pom.xml, builds localhost:5000/backend:<version>, and runs Helm with image.tag set to that version. Therefore, the Jenkins deployment does not depend on the checked-in default image tag at deploy time.

## Release checklist

1. Update the Maven project version in pom.xml.
2. When publishing or changing the Helm chart, update helm/backend/Chart.yaml:
   - version: chart package version
   - appVersion: application version represented by the chart
3. Update helm/backend/values.yaml image.tag when its default should point to the same release for manual deployments.
4. Verify the application and chart:

   ~~~powershell
   .\mvnw.cmd test
   helm lint helm/backend
   helm template backend helm/backend
   ~~~

5. Run the Jenkins pipeline or perform the approved deployment process.

The Maven test suite starts a full Spring context and needs reachable PostgreSQL and Redis. Configure those services before treating a test failure as a code failure.

## Check the Maven version

~~~powershell
.\mvnw.cmd help:evaluate -Dexpression=project.version -q -DforceStdout
~~~

## Deployment notes

- Jenkins pushes localhost:5000/backend:<Maven version>.
- The pipeline deploys with helm upgrade --install and overrides image.tag with the Maven version.
- The Helm chart expects the configured Kubernetes secret (backend-db-secret by default) to contain SPRING_DATASOURCE_USERNAME, SPRING_DATASOURCE_PASSWORD, and STELLAR_JWT_SECRET.
- Chart versions are not automatically synchronized by the Jenkinsfile; keep them intentionally aligned as part of a release.

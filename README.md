## Running locally

1. Build and compile
   ```bash
   sbt compile
   ```
   You might need to run these two before recompiling:
   ```bash
   sbt clean
   sbt reload
   ```
2. Dockerize
   ```bash
   sbt Docker / publishLocal
   ```
   You might like to clean the local Docker repository beforehand
   ```bash
   sbt Docker / clean
   ```

### Connecting to Postgresql from local pgadmin instance

To connect database from a local Docker environment, the hostname should be `host.docker.internal`
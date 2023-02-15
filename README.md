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
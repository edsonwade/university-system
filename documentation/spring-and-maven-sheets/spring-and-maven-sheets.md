# Spring Boot & Maven Commands Cheat Sheet

## Maven Commands

### Project Management

#### Create a New Maven Project
- `mvn archetype:generate -DgroupId=<group-id> -DartifactId=<artifact-id> -DarchetypeArtifactId=maven-archetype-quickstart -DinteractiveMode=false`: Create a new Maven project using the Quickstart archetype.

#### Build Project
- `mvn clean install`: Clean previous builds and compile, test, package, and install the project JAR to the local Maven repository.

#### Clean Project
- `mvn clean`: Delete the `target` directory containing compiled classes and resources.

### Dependency Management

#### Add Dependency
- Edit `pom.xml` and add dependencies under `<dependencies>` tag, then run `mvn clean install` to download and install dependencies.

#### List Dependencies
- `mvn dependency:tree`: Display the project's dependency tree.

#### Update Dependencies
- `mvn versions:display-dependency-updates`: Check for available dependency updates.

### Running and Testing

#### Run Application
- `mvn spring-boot:run`: Run the Spring Boot application.

#### Run Tests
- `mvn test`: Run tests in the project.

#### Skip Tests
- `mvn install -DskipTests`: Skip running tests during the build process.

#### Packaging

#### Package Application
- `mvn package`: Package the application into a JAR or WAR file.

#### Install Artifact to Local Repository
- `mvn install`: Install the project artifact to the local Maven repository.

#### Clean and Package
- `mvn clean package`: Clean the project and package the application into a JAR or WAR file.

### Miscellaneous

#### Generate JavaDoc
- `mvn javadoc:javadoc`: Generate JavaDoc documentation for the project.

#### Skip JavaDoc Generation
- `mvn install -Dmaven.javadoc.skip=true`: Skip JavaDoc generation during the build.

## Spring Boot CLI Commands

### Create and Run Spring Boot Applications

#### Create a New Spring Boot Project
- `spring init -d=web,data-jpa,mysql my-spring-boot-app`: Initialize a new Spring Boot project with dependencies.

#### Run Spring Boot Application
- `java -jar target/my-spring-boot-app.jar`: Run the packaged Spring Boot application JAR.

## Spring Boot DevTools Commands

### Hot Reload and Developer Tools

#### Automatic Restart
- Enable `spring-boot-devtools` dependency in `pom.xml` and restart the application for changes to take effect automatically.

---

This cheat sheet covers essential Maven and Spring Boot commands for project management, dependency management, running and testing applications, packaging, and Spring Boot CLI operations. Customize commands and options based on your specific project requirements and environment.

For more details on each command, refer to the [Maven Documentation](https://maven.apache.org/) and [Spring Boot Documentation](https://docs.spring.io/spring-boot/docs/current/reference/html/).

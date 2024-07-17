## Student Management System Configuration**

This configuration file is used to set up the Student Management System using Spring Boot and PostgreSQL.

**Application Name:**
- `spring.application.name`: Specifies the name of the application, which is "student-management" in this case.

**Database Configuration:**
- `spring.datasource.url`: Specifies the URL of the PostgreSQL database, which is `jdbc:postgresql://localhost:5432/student_management_db`.
- `spring.datasource.username`: Specifies the username for the PostgreSQL database. The value is obtained from the environment variable `POSTGRES_USERNAME`.
- `spring.datasource.password`: Specifies the password for the PostgreSQL database. The value is obtained from the environment variable `POSTGRES_PASSWORD`.
- `spring.datasource.driver-class-name`: Specifies the driver class name for the PostgreSQL database, which is `org.postgresql.Driver`.

**JPA Configuration:**
- `spring.jpa.properties.hibernate.dialect`: Specifies the Hibernate dialect for the PostgreSQL database, which is `org.hibernate.dialect.PostgreSQLDialect`.
- `spring.jpa.hibernate.ddl-auto`: Specifies the Hibernate DDL auto generation strategy. In this case, it is set to `update`, which means Hibernate will update the database schema based on the entity classes.
- `spring.jpa.hibernate.format_sql`: Enables SQL formatting for better readability.
- `spring.jpa.hibernate.show-sql`: Enables logging of SQL statements.
- `spring.jpa.database`: Specifies the database type, which is `postgresql` in this case.
- `spring.jpa.open-in-view`: Enables the open-in-view feature, which allows Hibernate to load entities in the same HTTP request.

**Server Configuration:**
- `server.port`: Specifies the port number for the server, which is `8081` in this case.

**Management Endpoints Configuration:**
- `management.endpoints.web.exposure.include`: Specifies which endpoints should be exposed. In this case, all endpoints are exposed.
- `management.endpoint.health.show-details`: Specifies the level of detail in the health endpoint. In this case, it is set to `always`, which shows detailed health information.

**Springdoc Configuration:**
- `springdoc.api-docs.path`: Specifies the path for the API documentation in JSON format, which is `/v3/api-docs`.
- `springdoc.swagger-ui.path`: Specifies the path for the Swagger UI, which is `/student-api.html`.
- `springdoc.swagger-ui.operationsSorter`: Specifies the sorting order for API operations in the Swagger UI, which is `method`.
- `springdoc.swagger-ui.tagsSorter`: Specifies the sorting order for API tags in the Swagger UI, which is `alpha`.
- `springdoc.swagger-ui.default-sort`: Specifies the default sorting order for API operations and tags in the Swagger UI, which is `method,asc`.
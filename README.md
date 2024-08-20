# Student Management System 🚀

## Description  📚
A system designed to efficiently manage student records, incorporating CRUD operations and advanced search functionalities. It also manages degrees, courses, teachers, appointments, and teacher availabilities.

## Objective 🎯
To practice CRUD operations, database integration, and building a console application with sophisticated search features, while handling complex relationships between entities.

## Features and Functionalities

### 🎓 Student Management
- Add a new student record
- Update a student record
- Delete a student record
- Search for a student by name or ID
- List all students

### 📜 Degree Management
- Add a new degree
- Update a degree
- Delete a degree
- List all degrees

### 📘 Course Management
- Add a new course
- Update a course
- Delete a course
- List all courses
- Assign courses to degrees

### 👨‍🏫 Teacher Management
- Add a new teacher
- Update a teacher
- Delete a teacher
- List all teachers

### 📅 Appointment Management
- Schedule an appointment
- Update an appointment
- Cancel an appointment
- List all appointments

### ⏰ Availability Management
- Add availability for a teacher
- Update availability
- Delete availability
- List all availabilities

## Entities and Relationships

### 📝 Student
- **Attributes:** id, name, age, gender, enrollment_date, degree_id
- **Relationships:** Belongs to Degree

### 🎓 Degree
- **Attributes:** id, name, description
- **Relationships:** Has many Students, has many Courses

### 📘 Course
- **Attributes:** id, name, description, degree_id
- **Relationships:** Belongs to Degree, has many Teachers

### 👨‍🏫 Teacher
- **Attributes:** id, name, expertise, email
- **Relationships:** Teaches many Courses, has many Availabilities

### 📅 Appointment
- **Attributes:** id, student_id, teacher_id, date_time, description
- **Relationships:** Belongs to Student, belongs to Teacher

### ⏰ Availability
- **Attributes:** id, teacher_id, day_of_week, start_time, end_time
- **Relationships:** Belongs to Teacher

## Class Diagram 📊
A comprehensive class diagram illustrating the relationships and attributes of each entity should be created.

## Sequence Diagram 🔄
Sequence diagrams should demonstrate the flow of interactions between entities and services for key functionalities such as adding a student or scheduling an appointment.

## Functional Requirements 📝
- The application should support CRUD operations for all entities.
- The application should provide search functionalities for students, teachers, and appointments.
- The application should list all entities and their relationships.

## Non-Functional Requirements 🚀
- **Performance:** The application should respond within 200ms for any request.
- **Security:** The application should protect user data and ensure secure access.
- **Maintainability:** The application should be easy to maintain and extend with new features.

## Order of Implementation 🛠️
1. Set up the Java project.
2. Implement entities and repositories for Student, Degree, Course, Teacher, Appointment, and Availability.
3. Implement the service layer for business logic.
4. Develop a console-based user interface.
5. Integrate the application with the PostgreSQL database.
6. Write unit and integration tests.
7. Create Dockerfile and docker-compose configuration.


## Database Choice and Schema
- **Database:** PostgreSQL
- A well-defined schema should be designed to accommodate the entities and their relationships effectively.

## Testing 🧪

- Unit tests and integration tests ensure functionality and reliability.
- Use [testing framework] to run tests and verify system behavior.

## Deployment 🚀

- Deployment strategies (e.g., Docker, Kubernetes) for production environments.
- Configuration management and scaling considerations.

## Security 🔒

- Security measures implemented (e.g., HTTPS, input validation).
- Data protection and user authentication strategies.

## Performance ⚡

- Performance benchmarks and considerations.
- Optimization techniques implemented (e.g., caching, database indexing).

## Error Handling

The API follows standard HTTP status codes and includes error responses with detailed error messages in JSON format.

### HTTP Status Codes

- `200 OK`: Successful request
- `400 Bad Request`: Invalid request data
- `401 Unauthorized`: Unauthorized request (not used currently)
- `404 Not Found`: Resource not found
- `500 Internal Server Error`: Server error

## Endpoints 🚀

### REST API

The REST API endpoints allow interaction with the application:

- Detailed documentation of all endpoints, request formats, response formats, and examples.

### Students

#### Get All Students

- **URL**: `/students`
- **Method**: `GET`
- **Description**: Retrieve all students.
- **Request Parameters**: None
- **Response**: List of student objects.

#### Get Student by ID

- **URL**: `/students/{studentId}`
- **Method**: `GET`
- **Description**: Retrieve a student by ID.
- **Request Parameters**: `studentId` (Path parameter)
- **Response**: Student object with HATEOAS links to related resources.

#### Create Student

- **URL**: `/students`
- **Method**: `POST`
- **Description**: Create a new student.
- **Request Body**: JSON object with student details (name, age, gender, enrollment date).
- **Response**: Success message with student ID and HATEOAS links.

#### Update Student

- **URL**: `/students/{studentId}`
- **Method**: `PUT`
- **Description**: Update an existing student.
- **Request Parameters**: `studentId` (Path parameter)
- **Request Body**: JSON object with updated student details.
- **Response**: Success message with updated student details and HATEOAS links.

#### Delete Student

- **URL**: `/students/{studentId}`
- **Method**: `DELETE`
- **Description**: Delete a student by ID.
- **Request Parameters**: `studentId` (Path parameter)
- **Response**: Success message.

### Appointments

#### Get Appointments for Student

- **URL**: `/students/{studentId}/appointments`
- **Method**: `GET`
- **Description**: Retrieve appointments for a specific student.
- **Request Parameters**: `studentId` (Path parameter)
- **Response**: List of appointment objects with HATEOAS links to related resources.

#### Get Appointments for Teacher

- **URL**: `/teachers/{teacherId}/appointments`
- **Method**: `GET`
- **Description**: Retrieve appointments for a specific teacher.
- **Request Parameters**: `teacherId` (Path parameter)
- **Response**: List of appointment objects with HATEOAS links to related resources.

### HATEOAS Links

- HATEOAS links are included in responses to navigate between related resources.
- Example links: `self`, `appointments`, etc.

## Example Usage

### Create Student Example

```http
POST /api/v1/students HTTP/1.1
Host: localhost:8080
Content-Type: application/json
Accept: application/json

{
  "name": "John Doe",
  "age": 22,
  "gender": "Male",
  "enrollmentDate": "2024-07-10"
}
```
### HTTP Response

**HTTP/1.1 201 Created**
**Content-Type:** application/json

```json
{
  "message": "Student created successfully",
  "studentId": "12345",
  "_links": {
    "self": {
      "href": "http://localhost:8080/api/v1/students/12345"
    },
    "appointments": {
      "href": "http://localhost:8080/api/v1/students/12345/appointments"
    }
  }
}
```
### Resources for Further Learning
1. [Spring Boot Documentation](https://docs.spring.io/spring-boot/index.html)
2. [HTTP Specification](https://developer.mozilla.org/en-US/docs/Web/HTTP/Methods)
3. [API Design Best Practices](https://restfulapi.net/)
4. [OpenAPI Specification:](https://swagger.io/specification/)

## Contribution
Please read [CONTRIBUTING.md](link-to-contributing-file) for details on how to contribute to this project.🤝

## License ⚖️
This project is licensed under the [MIT License](https://opensource.org/licenses/MIT).




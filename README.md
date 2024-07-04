# Enhanced Student Management System

## Description
A system designed to efficiently manage student records, incorporating CRUD operations and advanced search functionalities. It also manages degrees, courses, teachers, appointments, and teacher availabilities.

## Objective
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

## Class Diagram
A comprehensive class diagram illustrating the relationships and attributes of each entity should be created.

## Sequence Diagram
Sequence diagrams should demonstrate the flow of interactions between entities and services for key functionalities such as adding a student or scheduling an appointment.

## Functional Requirements
- The application should support CRUD operations for all entities.
- The application should provide search functionalities for students, teachers, and appointments.
- The application should list all entities and their relationships.

## Non-Functional Requirements
- **Performance:** The application should respond within 200ms for any request.
- **Security:** The application should protect user data and ensure secure access.
- **Maintainability:** The application should be easy to maintain and extend with new features.

## Order of Implementation
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

## Contribution
Please read [CONTRIBUTING.md](link-to-contributing-file) for details on how to contribute to this project.

## License
This project is licensed under the [MIT License](https://opensource.org/licenses/MIT).




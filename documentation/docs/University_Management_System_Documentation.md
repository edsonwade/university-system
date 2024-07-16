
# University Management System Documentation

## Objective
To develop a University Management System (UMS) that facilitates the management of student registrations, course enrollments, class attendances, appointment scheduling with teachers, and other related services within a university environment.

## Introduction
The University Management System (UMS) is designed to streamline and automate the administrative processes of a university. This system allows students to register for courses, attend classes, and make appointments with faculty members for academic consultations. Faculty members can manage their schedules, conduct classes, and handle student queries effectively. The system also provides administrative staff with tools to manage student records, course details, and overall university operations.

## Entities of the System

### Student
- Properties:
  - `studentId` (Primary Key)
  - `firstName`
  - `lastName`
  - `email`
  - `phoneNumber`
  - `address`
  - `dateOfBirth`
  - `degree`
  - `courses` (Many-to-Many with Course)
- Methods:
  - `register()`
  - `attendClass()`
  - `makeAppointment()`
  - `getDetails()`

### Course
- Properties:
  - `courseId` (Primary Key)
  - `courseName`
  - `courseCode`
  - `credits`
  - `description`
  - `teacher` (Many-to-One with Teacher)
  - `students` (Many-to-Many with Student)
- Methods:
  - `addCourse()`
  - `removeCourse()`
  - `getCourseDetails()`

### Teacher
- Properties:
  - `teacherId` (Primary Key)
  - `firstName`
  - `lastName`
  - `email`
  - `phoneNumber`
  - `department`
  - `courses` (One-to-Many with Course)
  - `appointments` (One-to-Many with Appointment)
- Methods:
  - `scheduleClass()`
  - `manageAppointments()`
  - `getDetails()`

### Appointment
- Properties:
  - `appointmentId` (Primary Key)
  - `dateTime`
  - `student` (Many-to-One with Student)
  - `teacher` (Many-to-One with Teacher)
  - `topic`
- Methods:
  - `scheduleAppointment()`
  - `cancelAppointment()`
  - `getAppointmentDetails()`

### Class
- Properties:
  - `classId` (Primary Key)
  - `course` (Many-to-One with Course)
  - `teacher` (Many-to-One with Teacher)
  - `students` (Many-to-Many with Student)
  - `dateTime`
- Methods:
  - `scheduleClass()`
  - `cancelClass()`
  - `getClassDetails()`

## Relational Database Configuration

### Annotations
- `@Entity`
- `@Id`
- `@GeneratedValue`
- `@OneToOne`
- `@ManyToOne`
- `@ManyToMany`
- `@JoinColumn`
- `@JoinTable`
- `@Cascade`

### Database Schema

#### Student Table
- `studentId` INT PRIMARY KEY AUTO_INCREMENT
- `firstName` VARCHAR(255)
- `lastName` VARCHAR(255)
- `email` VARCHAR(255)
- `phoneNumber` VARCHAR(20)
- `address` VARCHAR(255)
- `dateOfBirth` DATE
- `degree` VARCHAR(255)

#### Course Table
- `courseId` INT PRIMARY KEY AUTO_INCREMENT
- `courseName` VARCHAR(255)
- `courseCode` VARCHAR(50)
- `credits` INT
- `description` TEXT
- `teacherId` INT FOREIGN KEY REFERENCES Teacher(teacherId)

#### Teacher Table
- `teacherId` INT PRIMARY KEY AUTO_INCREMENT
- `firstName` VARCHAR(255)
- `lastName` VARCHAR(255)
- `email` VARCHAR(255)
- `phoneNumber` VARCHAR(20)
- `department` VARCHAR(255)

#### Appointment Table
- `appointmentId` INT PRIMARY KEY AUTO_INCREMENT
- `dateTime` TIMESTAMP
- `studentId` INT FOREIGN KEY REFERENCES Student(studentId)
- `teacherId` INT FOREIGN KEY REFERENCES Teacher(teacherId)
- `topic` VARCHAR(255)

#### Class Table
- `classId` INT PRIMARY KEY AUTO_INCREMENT
- `courseId` INT FOREIGN KEY REFERENCES Course(courseId)
- `teacherId` INT FOREIGN KEY REFERENCES Teacher(teacherId)
- `dateTime` TIMESTAMP

#### Join Tables

- `Student_Course` (For Many-to-Many between Student and Course)
  - `studentId` INT FOREIGN KEY REFERENCES Student(studentId)
  - `courseId` INT FOREIGN KEY REFERENCES Course(courseId)

- `Student_Class` (For Many-to-Many between Student and Class)
  - `studentId` INT FOREIGN KEY REFERENCES Student(studentId)
  - `classId` INT FOREIGN KEY REFERENCES Class(classId)

## Functional Requirements

1. Student registration and profile management.
2. Course enrollment and management.
3. Class scheduling and attendance tracking.
4. Appointment scheduling with teachers.
5. Teacher profile and schedule management.
6. Administrative functionalities for managing courses, students, and teachers.

## Non-Functional Requirements

1. Security: Authentication and authorization for different roles (student, teacher, admin).
2. Performance: The system should handle a large number of concurrent users.
3. Scalability: The system should be able to scale with the increasing number of users.
4. Usability: The system should have an intuitive user interface.
5. Reliability: The system should be reliable with minimal downtime.

## Use Cases

### Student Registration
- Actor: Student
- Description: A student registers with the university by providing personal details and choosing a degree program.
- Precondition: None
- Postcondition: Student account is created and stored in the database.

### Course Enrollment
- Actor: Student
- Description: A student enrolls in courses for a semester.
- Precondition: Student is registered and logged in.
- Postcondition: Student is enrolled in selected courses.

### Schedule Class
- Actor: Teacher
- Description: A teacher schedules a class for a course they are teaching.
- Precondition: Teacher is assigned to a course.
- Postcondition: Class is scheduled and added to the timetable.

### Make Appointment
- Actor: Student
- Description: A student schedules an appointment with a teacher to discuss academic matters.
- Precondition: Student is registered and logged in.
- Postcondition: Appointment is scheduled and stored in the database.

### Manage Appointments
- Actor: Teacher
- Description: A teacher views and manages appointments with students.
- Precondition: Teacher is logged in.
- Postcondition: Teacher can view, confirm, or cancel appointments.

## Sequence Diagram

### Student Registration
\```mermaid
sequenceDiagram
    participant Student
    participant RegistrationService
    participant Database

    Student->>RegistrationService: Submit registration form
    RegistrationService->>Database: Save student details
    Database-->>RegistrationService: Confirmation
    RegistrationService-->>Student: Registration successful
\```

### Course Enrollment
\```mermaid
sequenceDiagram
    participant Student
    participant CourseService
    participant Database

    Student->>CourseService: Select courses to enroll
    CourseService->>Database: Save enrollment details
    Database-->>CourseService: Confirmation
    CourseService-->>Student: Enrollment successful
\```

### Schedule Class
\```mermaid
sequenceDiagram
    participant Teacher
    participant ClassService
    participant Database

    Teacher->>ClassService: Schedule class details
    ClassService->>Database: Save class schedule
    Database-->>ClassService: Confirmation
    ClassService-->>Teacher: Class scheduled
\```

### Make Appointment
\```mermaid
sequenceDiagram
    participant Student
    participant AppointmentService
    participant Database

    Student->>AppointmentService: Request appointment
    AppointmentService->>Database: Save appointment details
    Database-->>AppointmentService: Confirmation
    AppointmentService-->>Student: Appointment scheduled
\```

### Manage Appointments
\```mermaid
sequenceDiagram
    participant Teacher
    participant AppointmentService
    participant Database

    Teacher->>AppointmentService: View appointments
    AppointmentService->>Database: Fetch appointment details
    Database-->>AppointmentService: Appointment details
    AppointmentService-->>Teacher: Display appointments
\```

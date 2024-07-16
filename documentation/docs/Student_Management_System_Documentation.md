
# Student Management System (SMS) Documentation

## Table of Contents

1. [Project Overview](#1-project-overview)
2. [Features](#2-features)
3. [System Architecture](#3-system-architecture)
4. [Modules](#4-modules)
    - [User Management](#41-user-management)
    - [Student Management](#42-student-management)
    - [Course Management](#43-course-management)
    - [Enrollment Management](#44-enrollment-management)
    - [Grades Management](#45-grades-management)
5. [Database Schema](#5-database-schema)
6. [API Endpoints](#6-api-endpoints)
7. [Technologies Used](#7-technologies-used)
8. [Installation](#8-installation)
9. [Usage](#9-usage)
10. [Security](#10-security)
11. [Future Enhancements](#11-future-enhancements)

---

## 1. Project Overview

The Student Management System (SMS) is a web-based application designed to manage student data, course registrations, grades, and academic information. It provides a user-friendly interface for administrators, teachers, and students to interact with the system and manage academic records efficiently.

## 2. Features

- User authentication and authorization
- Student information management
- Course management
- Enrollment management
- Grade management
- Reporting and analytics
- Responsive design

## 3. System Architecture

The SMS follows a multi-tier architecture with the following layers:

- **Presentation Layer**: User interface (web application)
- **Business Logic Layer**: Application logic and services
- **Data Access Layer**: Database interaction
- **Database Layer**: Database management system

## 4. Modules

### 4.1 User Management

**Services:**

- User registration
- User login
- Role-based access control (Admin, Teacher, Student)
- Password management

**Database Tables:**

- \`Users\`: Stores user information (id, username, password, role)
- \`Roles\`: Stores role information (id, role_name)

### 4.2 Student Management

**Services:**

- Add/edit/delete student information
- View student details
- Search students

**Database Tables:**

- \`Students\`: Stores student details (id, name, date_of_birth, address, email)

### 4.3 Course Management

**Services:**

- Add/edit/delete courses
- View course details
- Search courses

**Database Tables:**

- \`Courses\`: Stores course information (id, course_name, description, credits)

### 4.4 Enrollment Management

**Services:**

- Enroll/unenroll students in/from courses
- View enrollment details
- Search enrollments

**Database Tables:**

- \`Enrollments\`: Stores enrollment details (id, student_id, course_id, enrollment_date)

### 4.5 Grades Management

**Services:**

- Add/edit/delete grades
- View grades
- Calculate GPA

**Database Tables:**

- \`Grades\`: Stores grade details (id, enrollment_id, grade, grade_point)

## 5. Database Schema

Here is an example schema for the SMS:

\`\`\`sql
CREATE TABLE Users (
    id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL
);

CREATE TABLE Roles (
    id INT PRIMARY KEY AUTO_INCREMENT,
    role_name VARCHAR(50) NOT NULL
);

CREATE TABLE Students (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    date_of_birth DATE NOT NULL,
    address VARCHAR(255),
    email VARCHAR(100) UNIQUE
);

CREATE TABLE Courses (
    id INT PRIMARY KEY AUTO_INCREMENT,
    course_name VARCHAR(100) NOT NULL,
    description TEXT,
    credits INT NOT NULL
);

CREATE TABLE Enrollments (
    id INT PRIMARY KEY AUTO_INCREMENT,
    student_id INT,
    course_id INT,
    enrollment_date DATE,
    FOREIGN KEY (student_id) REFERENCES Students(id),
    FOREIGN KEY (course_id) REFERENCES Courses(id)
);

CREATE TABLE Grades (
    id INT PRIMARY KEY AUTO_INCREMENT,
    enrollment_id INT,
    grade CHAR(2),
    grade_point DECIMAL(3, 2),
    FOREIGN KEY (enrollment_id) REFERENCES Enrollments(id)
);
\`\`\`

## 6. API Endpoints

### User Management

- **POST /api/register**: Register a new user
- **POST /api/login**: User login
- **GET /api/users**: Get all users (Admin only)

### Student Management

- **POST /api/students**: Add a new student
- **GET /api/students**: Get all students
- **GET /api/students/{id}**: Get student by ID
- **PUT /api/students/{id}**: Update student by ID
- **DELETE /api/students/{id}**: Delete student by ID

### Course Management

- **POST /api/courses**: Add a new course
- **GET /api/courses**: Get all courses
- **GET /api/courses/{id}**: Get course by ID
- **PUT /api/courses/{id}**: Update course by ID
- **DELETE /api/courses/{id}**: Delete course by ID

### Enrollment Management

- **POST /api/enrollments**: Enroll a student in a course
- **GET /api/enrollments**: Get all enrollments
- **GET /api/enrollments/{id}**: Get enrollment by ID
- **DELETE /api/enrollments/{id}**: Unenroll student from course

### Grades Management

- **POST /api/grades**: Add a grade
- **GET /api/grades**: Get all grades
- **GET /api/grades/{id}**: Get grade by ID
- **PUT /api/grades/{id}**: Update grade by ID
- **DELETE /api/grades/{id}**: Delete grade by ID

## 7. Technologies Used

- **Frontend**: HTML, CSS, JavaScript, React.js
- **Backend**: Node.js, Express.js
- **Database**: MySQL
- **Authentication**: JWT (JSON Web Tokens)
- **Hosting**: AWS

## 8. Installation

### Prerequisites

- Node.js and npm installed
- MySQL installed and running

### Steps

1. Clone the repository:
    \`\`\`bash
    git clone https://github.com/your-repo/sms.git
    cd sms
    \`\`\`

2. Install backend dependencies:
    \`\`\`bash
    cd backend
    npm install
    \`\`\`

3. Set up the database:
    - Create a MySQL database named \`sms\`.
    - Run the SQL script provided in \`database/schema.sql\` to create tables.

4. Configure environment variables:
    - Create a \`.env\` file in the \`backend\` directory and add the following:
    \`\`\`env
    DB_HOST=localhost
    DB_USER=root
    DB_PASSWORD=password
    DB_NAME=sms
    JWT_SECRET=your_secret_key
    \`\`\`

5. Start the backend server:
    \`\`\`bash
    npm start
    \`\`\`

6. Install frontend dependencies:
    \`\`\`bash
    cd ../frontend
    npm install
    \`\`\`

7. Start the frontend server:
    \`\`\`bash
    npm start
    \`\`\`

## 9. Usage

- Access the web application at \`http://localhost:3000\`.
- Register as a new user and log in.
- Use the dashboard to manage students, courses, enrollments, and grades.

## 10. Security

- User passwords are hashed using bcrypt.
- JWT is used for authentication.
- Role-based access control ensures that only authorized users can perform certain actions.

## 11. Future Enhancements

- Implementing email notifications for students and teachers.
- Adding more detailed reporting and analytics.
- Integrating a payment system for course fees.
- Developing a mobile application version.

# Student Management API Documentation

## Overview

The Student Management API provides endpoints to manage students, degrees, courses, teachers, appointments, and availabilities within an educational institution.

Base URL: `http://localhost:8080/api/v1`

## Authentication

The API does not currently require authentication.

## Error Handling

The API follows standard HTTP status codes and includes error responses with detailed error messages in JSON format.

### HTTP Status Codes

- `200 OK`: Successful request
- `400 Bad Request`: Invalid request data
- `401 Unauthorized`: Unauthorized request (not used currently)
- `404 Not Found`: Resource not found
- `500 Internal Server Error`: Server error

## Endpoints

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


### Explanation:

- **Endpoints**: Added specific endpoints for retrieving appointments related to students and teachers.
- **HATEOAS Links**: Included `_links` section in response examples to demonstrate navigation links (`self`, `appointments`) to related resources.
- **Example Usage**: Updated example HTTP request and response to include HATEOAS links.


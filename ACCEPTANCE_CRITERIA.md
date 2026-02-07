# Acceptance Criteria

## 1. Authentication Module (`/api/v1/auth`)

### POST /api/v1/auth/register

**Description**: Register a new user.

#### Scenario 1: Successful Registration

- **Test Scenario**: Register with valid data (firstname, lastname, email, password, role).
- **Expected Outcome**: 200 OK, returns access token and refresh token.
- **Suggested Assertions**:
  - Status is 200.
  - JSON body contains `data.accessToken` and `data.refreshToken`.
  - DB contains the new user.

#### Scenario 2: Validation Failure

- **Test Scenario**: Register with missing email or short password.
- **Expected Outcome**: 400 Bad Request.
- **Suggested Assertions**:
  - Status is 400.
  - JSON body contains error message specifying the invalid field.

#### Scenario 3: Email Already Exists

- **Test Scenario**: Register with an email that already exists.
- **Expected Outcome**: 409 Conflict (or 400 depending on implementation).
- **Suggested Assertions**:
  - Status is 409/400.
  - Error message indicates email is taken.

### POST /api/v1/auth/authenticate

**Description**: Authenticate a user.

#### Scenario 1: Successful Login

- **Test Scenario**: Login with correct email and password.
- **Expected Outcome**: 200 OK, returns tokens.
- **Suggested Assertions**:
  - Status is 200.
  - Tokens are present.

#### Scenario 2: Invalid Credentials

- **Test Scenario**: Login with wrong password.
- **Expected Outcome**: 401 Unauthorized.
- **Suggested Assertions**:
  - Status is 401.

### POST /api/v1/auth/refreshtoken

**Description**: Refresh access token.

#### Scenario 1: Successful Refresh

- **Test Scenario**: Provide valid refresh token.
- **Expected Outcome**: 200 OK, returns new access token.
- **Suggested Assertions**:
  - Status is 200.
  - New access token is returned.

#### Scenario 2: Invalid/Expired Token

- **Test Scenario**: Provide expired or invalid refresh token.
- **Expected Outcome**: 403 Forbidden or 400 Bad Request.
- **Suggested Assertions**:
  - Status is 403/400.

---

## 2. Student Module (`/api/v1/students`)

### GET /api/v1/students

**Description**: Get all students.

#### Scenario 1: Retrieve All

- **Test Scenario**: Request all students as authenticated user.
- **Expected Outcome**: 200 OK, list of students.
- **Suggested Assertions**:
  - Status is 200.
  - Body is a list.

### GET /api/v1/students/{id}

**Description**: Get student by ID.

#### Scenario 1: Found

- **Test Scenario**: Request existing ID.
- **Expected Outcome**: 200 OK, student details.
- **Suggested Assertions**:
  - Status is 200.
  - ID matches.

#### Scenario 2: Not Found

- **Test Scenario**: Request non-existing ID.
- **Expected Outcome**: 404 Not Found.
- **Suggested Assertions**:
  - Status is 404.

### POST /api/v1/students

**Description**: Create student.

#### Scenario 1: Success (Admin)

- **Test Scenario**: Admin creates valid student.
- **Expected Outcome**: 200 OK, created student.
- **Suggested Assertions**:
  - Status is 200.
  - DB has new record.

#### Scenario 2: Forbidden (User)

- **Test Scenario**: Non-admin tries to create.
- **Expected Outcome**: 403 Forbidden.
- **Suggested Assertions**:
  - Status is 403.

#### Scenario 3: Validation Error

- **Test Scenario**: Create with invalid email.
- **Expected Outcome**: 400 Bad Request.
- **Suggested Assertions**:
  - Status is 400.

### PUT /api/v1/students/{id}

**Description**: Update student.

#### Scenario 1: Success

- **Test Scenario**: Update existing student with valid data.
- **Expected Outcome**: 200 OK, updated details.
- **Suggested Assertions**:
  - Status is 200.
  - DB reflects changes.

### DELETE /api/v1/students/{id}

**Description**: Delete student.

#### Scenario 1: Success

- **Test Scenario**: Delete existing student.
- **Expected Outcome**: 200 OK.
- **Suggested Assertions**:
  - Status is 200.
  - DB record is gone.

---

## 3. Teacher Module (`/api/v1/teachers`)

_Similar patterns apply as Student Module._

### GET /api/v1/teachers

...

### POST /api/v1/teachers

...

---

## 4. Course Module (`/api/v1/courses`)

_Similar patterns apply._

---

## 5. Appointment Module (`/api/v1/appointments`)

### POST /api/v1/appointments

**Description**: Schedule appointment.

#### Scenario 1: Success

- **Test Scenario**: Student schedules appointment with teacher.
- **Expected Outcome**: 200 OK.
- **Suggested Assertions**:
  - Status is 200.
  - Appointment saved.

#### Scenario 2: Conflict

- **Test Scenario**: Schedule at a time teacher is busy.
- **Expected Outcome**: 409 Conflict.
- **Suggested Assertions**:
  - Status is 409.

---

## 6. Billing Module (`/api/v1/billing`)

### POST /api/v1/billing/payment

**Description**: Process payment.

#### Scenario 1: Success

- **Test Scenario**: Valid payment details.
- **Expected Outcome**: 200 OK.
- **Suggested Assertions**:
  - Status is 200.
  - Payment recorded.

---

## 7. Notification Module (`/api/v1/notifications`)

### POST /api/v1/notifications/send

**Description**: Send notification.

#### Scenario 1: Success

- **Test Scenario**: Send email/SMS.
- **Expected Outcome**: 200 OK.
- **Suggested Assertions**:
  - Status is 200.
  - Notification service invoked (mocked).

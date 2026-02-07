Feature: Student Management
  As an administrator
  I want to manage student records
  So that I can keep track of student information

  Scenario: Successfully create a new student
    Given I am an authenticated administrator
    When I request to create a student with the following details:
      | firstName | lastName | email                  | dateOfBirth | address     |
      | John      | Doe      | john.doe@example.com   | 2000-01-01  | 123 Main St |
    Then the student should be created successfully
    And the student details should be:
      | firstName | lastName | email                  |
      | John      | Doe      | john.doe@example.com   |

  Scenario: Prevent creating a student with invalid data
    Given I am an authenticated administrator
    When I request to create a student with invalid email "invalid-email"
    Then the creation should fail with a bad request error

  Scenario: Retrieve an existing student
    Given I am an authenticated administrator
    And a student exists with email "jane.doe@example.com"
    When I request to get the student by ID
    Then the student details should be returned successfully

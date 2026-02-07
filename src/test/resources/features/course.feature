Feature: Course Management
  As an administrator
  I want to manage courses
  So that students can enroll in them

  Scenario: Successfully create a new course
    Given I am an authenticated administrator
    When I request to create a course with the following details:
      | name        | code    | description |
      | Mathematics | MATH101 | Basic Math  |
    Then the course should be created successfully
    And the course details should be:
      | name        | code    |
      | Mathematics | MATH101 |

  Scenario: Retrieve an existing course
    Given I am an authenticated administrator
    And a course exists with code "PHYS101"
    When I request to get the course by ID
    Then the course details should be returned successfully

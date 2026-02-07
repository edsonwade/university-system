Feature: Degree Management
  As an administrator
  I want to manage degrees
  So that students can enroll in programs

  Scenario: Successfully create a new degree
    Given I am an authenticated administrator
    When I request to create a degree with the following details:
      | name             | code | duration |
      | Computer Science | CS   | 4        |
    Then the degree should be created successfully
    And the degree details should be:
      | name             | code |
      | Computer Science | CS   |

  Scenario: Retrieve an existing degree
    Given I am an authenticated administrator
    And a degree exists with code "ENG"
    When I request to get the degree by ID
    Then the degree details should be returned successfully

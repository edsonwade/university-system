Feature: Teacher Management
  As an administrator
  I want to manage teacher records
  So that I can assign them to courses

  Scenario: Successfully create a new teacher
    Given I am an authenticated administrator
    When I request to create a teacher with the following details:
      | name  | email                  | address     | phoneNumber |
      | Alice | alice.t@example.com    | 123 Main St | 1234567890  |
    Then the teacher should be created successfully
    And the teacher details should be:
      | name  | email                  |
      | Alice | alice.t@example.com    |

  Scenario: Retrieve an existing teacher
    Given I am an authenticated administrator
    And a teacher exists with email "bob.t@example.com"
    When I request to get the teacher by ID
    Then the teacher details should be returned successfully

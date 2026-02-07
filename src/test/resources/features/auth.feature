Feature: Authentication Management
  As a user
  I want to register and log in
  So that I can access the system

  Scenario: Successful User Registration
    When I register with the following details:
      | firstname | lastname | email                  | password    | role |
      | John      | Doe      | john.doe@example.com   | password123 | USER |
    Then the registration should be successful
    And I should receive an access token

  Scenario: Registration with Existing Email
    Given a user exists with email "existing@example.com"
    When I register with the following details:
      | firstname | lastname | email                  | password    | role |
      | Jane      | Doe      | existing@example.com   | password123 | USER |
    Then the registration should fail with a conflict or bad request

  Scenario: Successful Login
    Given a user exists with email "login@example.com" and password "password123"
    When I authenticate with email "login@example.com" and password "password123"
    Then the authentication should be successful
    And I should receive an access token

  Scenario: Login with Invalid Credentials
    Given a user exists with email "user@example.com" and password "password123"
    When I authenticate with email "user@example.com" and password "wrongpassword"
    Then the authentication should fail with unauthorized status

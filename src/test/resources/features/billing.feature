Feature: Billing Management
  As a student
  I want to pay my fees
  So that I can continue my studies

  Scenario: Successfully process a payment
    Given I am an authenticated student
    When I request to pay "100.00" for "Tuition Fee"
    Then the payment should be processed successfully

  Scenario: Payment with invalid amount
    Given I am an authenticated student
    When I request to pay "-50.00" for "Tuition Fee"
    Then the payment should fail with a bad request

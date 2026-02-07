Feature: Appointment Management
  As a student
  I want to schedule appointments with teachers
  So that I can discuss my progress

  Scenario: Successfully schedule an appointment
    Given I am an authenticated student
    And a teacher exists with email "teacher@example.com"
    When I request to schedule an appointment with the teacher at "2026-02-01T10:00:00"
    Then the appointment should be scheduled successfully

  Scenario: Conflict when scheduling at a busy time
    Given I am an authenticated student
    And a teacher exists with email "busy@example.com"
    And the teacher has an appointment at "2026-02-01T10:00:00"
    When I request to schedule an appointment with the teacher at "2026-02-01T10:00:00"
    Then the scheduling should fail with a conflict

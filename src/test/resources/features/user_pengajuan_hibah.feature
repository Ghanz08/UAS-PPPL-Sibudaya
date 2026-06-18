@sibudaya @e2e @user @submit @user-hibah-submit
Feature: User pengajuan fasilitasi hibah
  Background:
    Given Sibudaya E2E credentials are configured

  Scenario: Ordinary user submits a hibah facilitation request
    When the ordinary user logs in to Sibudaya
    Then the user dashboard page is shown
    When the user opens the facilitation selection page
    And the user starts a hibah facilitation submission
    And the user completes and submits the hibah facilitation form
    Then the submitted request status page is shown

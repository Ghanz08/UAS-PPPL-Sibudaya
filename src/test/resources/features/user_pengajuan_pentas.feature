@sibudaya @e2e @user @submit @user-pentas-submit
Feature: User pengajuan fasilitasi pentas
  Background:
    Given Sibudaya E2E credentials are configured

  Scenario: Ordinary user submits a pentas facilitation request
    When the ordinary user logs in to Sibudaya
    Then the user dashboard page is shown
    When the user opens the facilitation selection page
    And the user starts a pentas facilitation submission
    And the user completes and submits the pentas facilitation form
    Then the submitted request status page is shown

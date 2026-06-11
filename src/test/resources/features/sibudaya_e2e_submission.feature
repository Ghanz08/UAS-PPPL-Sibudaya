@sibudaya @e2e @submit
Feature: Sibudaya real submission end-to-end flow
  Background:
    Given Sibudaya E2E credentials are configured

  Scenario: Ordinary user submits a facilitation request and superadmin can see it
    When the ordinary user logs in to Sibudaya
    Then the user dashboard page is shown
    When the user opens the facilitation selection page
    And the user starts the first available facilitation submission
    And the user completes and submits the facilitation form
    Then the submitted request status page is shown
    When the superadmin logs in to Sibudaya
    Then the superadmin dashboard page is shown
    When the superadmin searches for the submitted request
    Then the submitted request is visible to the superadmin

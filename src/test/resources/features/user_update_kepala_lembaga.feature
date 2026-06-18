@sibudaya @e2e @user @profile @user-kepala-update
Feature: User update Data Kepala Lembaga
  Background:
    Given Sibudaya E2E credentials are configured

  Scenario: Ordinary user updates the first name in Data Kepala Lembaga
    When the ordinary user logs in to Sibudaya
    Then the user dashboard page is shown
    When the user updates the Data Kepala Lembaga first name

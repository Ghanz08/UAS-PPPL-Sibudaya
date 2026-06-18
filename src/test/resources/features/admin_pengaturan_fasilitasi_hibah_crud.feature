@sibudaya @e2e @admin @crud @admin-hibah-crud
Feature: Admin CRUD pengaturan fasilitasi hibah
  Background:
    Given Sibudaya E2E credentials are configured

  Scenario: Superadmin creates reads updates and deletes a hibah facilitation package
    When the superadmin logs in to Sibudaya
    Then the superadmin dashboard page is shown
    When the superadmin performs CRUD on the hibah facilitation settings page

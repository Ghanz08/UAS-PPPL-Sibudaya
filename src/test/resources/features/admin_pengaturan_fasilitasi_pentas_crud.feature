@sibudaya @e2e @admin @crud @admin-pentas-crud
Feature: Admin CRUD pengaturan fasilitasi pentas
  Background:
    Given Sibudaya E2E credentials are configured

  Scenario: Superadmin creates reads updates and deletes a pentas facilitation package
    When the superadmin logs in to Sibudaya
    Then the superadmin dashboard page is shown
    When the superadmin performs CRUD on the pentas facilitation settings page

@sibudaya @e2e @admin @crud @admin-user-crud
Feature: Admin CRUD manajemen pengguna
  Background:
    Given Sibudaya E2E credentials are configured

  Scenario: Superadmin creates reads updates and deletes an admin account
    When the superadmin logs in to Sibudaya
    Then the superadmin dashboard page is shown
    When the superadmin performs CRUD on the user management page

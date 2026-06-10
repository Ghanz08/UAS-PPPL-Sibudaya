@sibudaya @e2e @readonly
Feature: Sibudaya read-only end-to-end navigation
  Background:
    Given Sibudaya E2E credentials are configured

  Scenario: Ordinary user and superadmin can open read-only pages in order
    When the ordinary user logs in to Sibudaya
    Then the user dashboard page is shown
    When the user opens the facilitation selection page
    Then the facilitation selection page is shown without submitting data
    When the user opens an existing submission status page if available
    Then the user status page or dashboard fallback is shown
    When the user opens the profile page
    Then the user profile page is shown
    When the superadmin logs in to Sibudaya
    Then the superadmin dashboard page is shown
    When the superadmin opens a read-only administration page
    Then the read-only administration page is shown

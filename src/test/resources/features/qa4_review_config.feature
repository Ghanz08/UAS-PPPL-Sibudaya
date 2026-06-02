@qa4
Feature: QA-4 review status and admin dashboard
  Automation validates review timeline, revision upload, terminal status rules,
  and admin dashboard summary for Sibudaya facilitation submissions.

  Background:
    Given QA4 account configuration is available

  @M4F3-P01
  Scenario: Timeline tracking shows chronological status history
    Given a "TIMELINE" pengajuan is available
    When the lembaga opens the pengajuan detail
    Then the chronological timeline status is shown

  @M4F3-P02
  Scenario: Revised pengajuan can upload proposal again
    Given a "REVISI" pengajuan is available
    When the lembaga uploads a revised proposal
    Then the revision submission is accepted
    And the admin notification shows the updated pengajuan

  @M4F3-N01
  Scenario: Finished pengajuan cannot be edited
    Given a "SELESAI" pengajuan is available
    When the lembaga opens the pengajuan detail
    Then the finished pengajuan has no revision upload action

  @M4F3-N02
  Scenario: Rejected pengajuan cannot be edited
    Given a "DITOLAK" pengajuan is available
    When the lembaga opens the pengajuan detail
    Then the rejected pengajuan has no revision upload action

  @M5F1-P01
  Scenario: Admin dashboard shows pengajuan summary
    When the admin opens the dashboard
    Then the admin dashboard summary is shown

  @M5F1-E01
  Scenario: Empty admin dashboard shows zero without backend error
    Given empty dashboard validation is enabled
    When the admin opens the dashboard
    Then the empty admin dashboard is shown without backend error

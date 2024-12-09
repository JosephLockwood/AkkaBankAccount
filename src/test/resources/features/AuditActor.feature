Feature: Audit Actor processes transactions and batches them for submission

  Scenario: Submit exactly 1000 transactions
    Given I have 1000 transactions
    When I submit these transactions to the audit actor
    Then The audit actor should process 1000 transactions and submit them to the audit system

  Scenario: Submit more than 1000 transactions
    Given I have more than 1000 transactions
    When I submit these transactions to the audit actor
    Then The audit actor should create multiple batches for submission

Feature: Compare Prod and UAT report in xlsx

Scenario: Validate that Prod and UAT reports are compared successfully and comparison reports are generated
Given Prod and UAT Reports are available to compare
When User runs the Excel Utility
Then Comparison Report is genrated with ProdData in First sheet , results in second Tab and Summary Report report in Third tab
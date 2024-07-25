Feature: Request work

  Scenario: Request work
    Given TODAY's block A

    When work is requested

    Then work is generated

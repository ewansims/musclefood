Feature: Multi-Currency Support
  As a customer, I want to be able to pay for products using either British or Euro coins.
  Acceptance criteria
  • All British and Euro coins are accepted.
  • As coins are entered, the total credit value is uploaded on a display screen.
  • Mixed currency is not accepted; the first coin of the second currency entered does not contribute to the
  credit value and is returned to the Customer (with explanation massage on the screen).

  Background:
    Given the vending machine is powered on
    And no existing credit is present

  Scenario Outline: Check all <Currency> coin denominations are accepted and total credit is correctly incremented
    When I insert the below <Currency> coins
      | 0.01 | 0.02 | 0.05 | 0.1 | 0.2 | 0.5 | 1 | 2 |
    Then the coins are not rejected
    And the total credit shows <Currency>3.88
    Examples:
      | Currency |
      | £        |
      | €        |

  Scenario: Insert invalid currency from the start
    When I insert $1
    Then The coin is rejected
    And the display screen contains "****Invalid coin****"
    And the $1 is returned to me

  Scenario Outline: Insert invalid currency after a valid <Currency> coin inserted
    When I insert the below <Currency> coins
      | 1 |
    Then I insert $1
    Then The coin is rejected
    And the total credit shows <Currency>1
    And the $1 is returned to me
    Examples:
      | Currency |
      | £        |
      | €        |

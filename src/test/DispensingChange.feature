Feature: Dispensing Change
  As a coin paying customer, after paying all my items, I want to be able to receive change.
  Acceptance criteria
  • After a purchase, the customer’s remaining credit is displayed on the screen.
  • Change is dispensed at the customer's request.
  • Change is returned in the smallest number of coins.
  • The customer is notified about how much change has been dispensed on the display screen.
  • If the machine is unable to offer change, the customer must be notified of this via the display screen prior to
  making a purchase.

  Background:
    Given the vending machine is powered on

  Scenario Outline: Purchase an item to the value of <Value>
    And the current credit is British 5.00
    And I purchase a product that costs <Value>
    When I press the coin return
    Then <Change> is returned to me in <Coins>
    And the display screen contains "<Coins> coins returned, total £<Change>"
    Examples:
      | Value | Change | Coins |
      | 0.72  | 4.28   | 6     |
      | 1.00  | 4.00   | 2     |
      | 4.99  | 0.01   | 1     |

  Scenario: Insufficient change in machine
    And the current credit is British 11.00
    And I purchase a product that costs 0.50
    Then the display screen contains "Insufficient change in vending machine, please select another item or return coins"

Feature: Multiple Vends
  As a coin paying customer, I want to be able to pay for more than one product in a single transaction.
  Acceptance criteria
  • To enable multi-product purchase, change is not dispensed immediately after the customer pays fo the firs
  product.
  • Credit must be displayed on the display screen.

  Scenario: Purchase multiple items in one transaction
    Given the vending machine is powered on
    And the current credit is £5.00
    And I purchase a product that costs 2.00
    And product is delivered
    And the display screen contains "New total £3.00"
    When I purchase a product that costs 1.00
    And product is delivered
    When I press the coin return
    Then 2.00 is returned to me in 1 coin
    And the display screen contains "1 coins returned, total £2.00"

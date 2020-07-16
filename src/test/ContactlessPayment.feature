Feature: Contactless Payment
  As a customer, I want to be able to pay for products using contactless debit/credit card or Apple Pay.
  Acceptance criteria
  • The machine will debit the customer’s credit card/bank account with value of selected product.

  Scenario: Pay by contactless transaction
    Given the vending machine is powered on
    And no existing credit is present
    When I select a product that costs £17.00
    Then I can pay by card
    And product is delivered
    And no change is due

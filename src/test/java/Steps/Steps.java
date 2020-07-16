package Steps;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.After;
import io.cucumber.java.en.*;
import org.junit.Assert;

import java.util.Arrays;
import java.util.List;

public class Steps {
    final String[] acceptedCurrencies = {"British", "Euro"};
    final Double[] acceptedCoins = {2.00, 1.00, 0.50, 0.20, 0.10, 0.05, 0.02, 0.01};

    boolean powerLight = false;
    double totalBritish = 10.00;
    double totalEuros = 10.00;
    double currentCredit = 0.00;
    double coinRejectTotal = 0.00;
    int rejectedCoins = 0;
    String currentCurrency = "";
    String displayScreen = "Welcome";

    @After
    public void resetValues() {
        pressCoinReturn();
        emptyCoinRejectBox();
        pressPowerButton();
    }

    private void pressPowerButton() {
        powerLight = !powerLight;
        displayScreen = powerLight ? "Welcome" : "";
    }

    private void setCurrency(String currency) {
        currentCurrency = currency;
    }

    private void rejectCoin(Double denomination) {
        if (currentCredit > 0.00)
            displayScreen = "****Invalid coin****\nCurrent credit " + (currentCurrency.equals("British") ? "£" : "€") + currentCredit;
        else
            displayScreen = "****Invalid coin****\nWelcome";
        coinRejectTotal += denomination;
    }

    private void insertCoin(String currency, Double denomination) {
        // Is it a valid currency?
        if (!Arrays.asList(acceptedCurrencies).contains(currency) ||
                // Is it the same currency as previously inserted coins?
                (currentCurrency != "" && !currentCurrency.equals(currency)) ||
                // Is it a valid denomination?
                !Arrays.asList(acceptedCoins).contains(denomination)) {
            rejectCoin(denomination);
            return;
        }
        if (currentCredit == 0.00)
            setCurrency(currency);
        currentCredit += denomination;
        assert currentCurrency != "";
        displayScreen = "Current credit " + (currentCurrency.equals("British") ? "£" : "€") + currentCredit;
    }

    private void pressCoinReturn() {
        double total = currentCredit;
        for (Double denomination : acceptedCoins) {
            // return the least amount of possible coins
            while (currentCredit >= denomination) {
                // return 1x denomination
                currentCredit -= denomination;
                coinRejectTotal += denomination;
                rejectedCoins++;
            }
            if (currentCredit == 0.00) {
                break;
            }
        }
        displayScreen = rejectedCoins + " coins returned, total " + (currentCurrency.equals("British") ? "£" : "€") + total;
        currentCurrency = "";
    }

    private void emptyCoinRejectBox() {
        coinRejectTotal = 0.00;
        rejectedCoins = 0;
    }

    private void purchaseItem(double itemCost, String currency) {
        if (itemCost > currentCredit) {
            displayScreen = "Insufficient credit, current total " + (currentCurrency.equals("British") ? "£" : "€") + currentCredit;
            return;
        }
        if (currency.equals("British")) {
            totalBritish += itemCost;
        } else {
            totalEuros += itemCost;
        }
        currentCredit -= itemCost;
        displayScreen = "Item dispensed. New total " + (currentCurrency.equals("British") ? "£" : "€") + currentCredit;
    }

    @Given("^(?:the vending machine is powered on|I turn on the vending machine)$")
    public void theVendingMachineIsPoweredOn() {
        if (!powerLight) {
            pressPowerButton();
        }
        Assert.assertTrue(powerLight);
    }

    @And("^no existing credit is present$")
    public void noExistingCreditIsPresent() {
        Assert.assertEquals(0.00, currentCredit, 0.00001);
    }

    @When("^I insert the below (British|Euro) coins$")
    public void iInsertTheBelowCoins(String currency, DataTable inserted) {
        List<String> coins = inserted.asList();
        for (String coin : coins) {
            insertCoin(currency, Double.parseDouble(coin));
        }
    }

    @Then("^the coin(?:s) (?:is|are) not rejected$")
    public void theCoinIsNotRejected() {
        Assert.assertEquals(0.00, coinRejectTotal, 0.00001);
    }

    @And("^the total credit shows (British|Euro) ([^\"]*)$")
    public void theTotalCreditShowsCurrency(String currency, String total) {
        Assert.assertEquals(currency, currentCurrency);
        Assert.assertEquals(Double.parseDouble(total), currentCredit, 0.0001);
    }

    @When("^I insert (\\d+) ([^\"]*)$")
    public void iInsertInvalidCoins(String denomination, String currency) {
        insertCoin(currency, Double.parseDouble(denomination));
    }

    @Then("^The coin is rejected$")
    public void theCoinIsRejected() {
        Assert.assertTrue(coinRejectTotal > 0.00);
    }

    @And("^the ([^\"]*) (?:[^\"]*) is returned to the customer$")
    public void theCHFIsReturnedToTheCustomer(String denomination) {
        Assert.assertEquals(Double.parseDouble(denomination), coinRejectTotal, 0.0001);
    }

    @And("^the current credit is (British|Euro) ([^\"]*)$")
    public void theCurrentCreditIs(String currency, String credit) {
        currentCurrency = currency;
        currentCredit = Double.parseDouble(credit);
        displayScreen = "Current credit " + (currentCurrency.equals("British") ? "£" : "€") + currentCredit;
    }

    @And("^I purchase a product that costs (British|Euros) ([^\"]*)$")
    public void iPurchaseAProductThatCostsValue(String currency, String cost) {
        // if insufficient change
        if (
                (currentCredit - Double.parseDouble(cost))  // change required
                        > (currency.equals("British") ? totalBritish : totalEuros)) {
            displayScreen = "Insufficient change in vending machine, please select another item or return coins";
            return;
        }
        purchaseItem(Double.parseDouble(cost), currency);
    }

    @When("^I press the coin return$")
    public void iPressTheCoinReturn() {
        pressCoinReturn();
    }

    @Then("^([^\"]*) is returned to me in (\\d+)$")
    public void changeIsReturnedToMeInCoins(String total, int coins) {
        Assert.assertEquals(coinRejectTotal, Double.parseDouble(total), 0.0001);
        Assert.assertEquals(coins, rejectedCoins);
    }

    @Then("^the display screen contains \"([^\"]*)\"$")
    public void theDisplayScreenContains(String string) {
        Assert.assertTrue(displayScreen.contains(string));
    }
}

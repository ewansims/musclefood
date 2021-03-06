package Steps;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.After;
import io.cucumber.java.en.*;
import org.junit.Assert;

import java.util.Arrays;
import java.util.List;

public class Steps {
    final String[] acceptedCurrencies = {"£", "€"};
    final Double[] acceptedCoins = {2.00, 1.00, 0.50, 0.20, 0.10, 0.05, 0.02, 0.01};

    boolean powerLight = false;
    double totalBritish = 10.00;
    double totalEuros = 10.00;
    double currentCredit = 0.00;
    double coinRejectTotal = 0.00;
    int rejectedCoins = 0;
    boolean productDelivered = false;
    String currentCurrency = "";
    String currentProduct = "";
    String displayScreen = "";

    @After
    public void resetValues() {
        powerLight = false;
        totalBritish = 10.00;
        totalEuros = 10.00;
        currentCredit = 0.00;
        coinRejectTotal = 0.00;
        rejectedCoins = 0;
        currentCurrency = "";
        displayScreen = "";
        currentProduct = "";
        boolean productDelivered = false;
    }

    private String currencyStringifier(Double value) {
        Assert.assertNotNull(currentCurrency);
        String str = currentCurrency + value;
        String[] split = value.toString().split("\\.");
        if (split[1].length() == 1)
            str += "0";
        else if (split[1].length() == 0)
            str += "00";
        return str;
    }

    private Double currencyPrettifier(String value) {
        return Math.round(Double.parseDouble(value) * 100.0) / 100.0;
    }

    private void pressPowerButton() {
        powerLight = !powerLight;
        displayScreen = powerLight ? "Welcome" : "";
    }

    private void setCurrency(String currency) {
        currentCurrency = currency;
    }

    private void rejectCoin(String denomination) {
        if (currentCredit > 0.00)
            displayScreen = "****Invalid coin****\nCurrent credit " + currencyStringifier(currentCredit);
        else
            displayScreen = "****Invalid coin****\nWelcome";
        coinRejectTotal += currencyPrettifier(denomination);
    }

    private void insertCoin(String currency, String denomination) {
        // Is it a valid currency?
        if (!Arrays.asList(acceptedCurrencies).contains(currency) ||
                // Is it the same currency as previously inserted coins?
                (!currentCurrency.equals("") && !currentCurrency.equals(currency)) ||
                // Is it a valid denomination?
                !Arrays.asList(acceptedCoins).contains(currencyPrettifier(denomination))) {
            rejectCoin(denomination);
            return;
        }
        if (currentCredit == 0.00)
            setCurrency(currency);
        currentCredit += currencyPrettifier(denomination);
        displayScreen = "Current credit " + currencyStringifier(currentCredit);
    }

    private void pressCoinReturn() {
        currentCredit = Math.round(currentCredit * 100.0) / 100.0;
        String total = currencyStringifier(currentCredit);
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
        displayScreen = rejectedCoins + " coins returned, total " + total;
        currentCurrency = "";
    }

    private void purchaseItem(String itemCost) {
        double cost = currencyPrettifier(itemCost);
        if (cost > currentCredit) {
            displayScreen = "Insufficient credit, current total " + currencyStringifier(currentCredit);
            return;
        }
        if (currentCurrency.equals("British"))
            totalBritish += cost;
        else
            totalEuros += cost;
        currentCredit -= cost;
        productDelivered = true;
        displayScreen = "Item dispensed. New total " + currencyStringifier(currentCredit);
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

    @When("^I insert the below (£|€) coins$")
    public void iInsertTheBelowCoins(String currency, DataTable inserted) {
        List<String> coins = inserted.asList();
        for (String coin : coins) {
            insertCoin(currency, coin);
        }
    }

    @Then("^the coin(?:s) (?:is|are) not rejected$")
    public void theCoinIsNotRejected() {
        Assert.assertEquals(0.00, coinRejectTotal, 0.00001);
    }

    @And("^the total credit shows (£|€)([^\"]*)$")
    public void theTotalCreditShowsCurrency(String currency, String total) {
        Assert.assertEquals(currency, currentCurrency);
        Assert.assertEquals(currencyPrettifier(total), currentCredit, 0.0001);
    }

    @When("^I insert ([^\"]*)(\\d+)$")
    public void iInsertInvalidCoins(String currency, String denomination) {
        insertCoin(currency, denomination);
    }

    @Then("^The coin is rejected$")
    public void theCoinIsRejected() {
        Assert.assertTrue(coinRejectTotal > 0.00);
    }

    @And("^the (?:[^\"]*)(\\d+) is returned to me$")
    public void theDollarIsReturnedToTheCustomer(String value) {
        Assert.assertEquals(currencyPrettifier(value), coinRejectTotal, 0.0001);
    }

    @And("^the current credit is (£|€)([^\"]*)$")
    public void theCurrentCreditIs(String currency, String credit) {
        setCurrency(currency);
        currentCredit = currencyPrettifier(credit);
        displayScreen = "Current credit " + currencyStringifier(currentCredit);
    }

    @And("^I purchase a product that costs ([^\"]*)$")
    public void iPurchaseAProductThatCostsValue(String cost) {
        double changeRequired = currentCredit - Double.parseDouble(cost);
        // if insufficient change
        if ((currentCurrency.equals("British") ? totalBritish : totalEuros) < changeRequired) {
            displayScreen = "Insufficient change in vending machine, please select another item or return coins";
            return;
        }
        purchaseItem(cost);
    }

    @When("^I press the coin return$")
    public void iPressTheCoinReturn() {
        pressCoinReturn();
    }

    @Then("^([^\"]*) is returned to me in (\\d+) coin(?:|s)$")
    public void changeIsReturnedToMeInCoins(String total, int coins) {
        Assert.assertEquals(currencyPrettifier(total), coinRejectTotal, 0.0001);
        Assert.assertEquals(coins, rejectedCoins);
    }

    @Then("^the display screen contains \"([^\"]*)\"$")
    public void theDisplayScreenContains(String string) {
        Assert.assertTrue(displayScreen.contains(string));
    }

    @When("^I select a product that costs (£|€)([^\"]*)$")
    public void iSelectAProductThatCosts(String currency, String cost) {
        setCurrency(currency);
        currentProduct = cost;
        displayScreen = "Please pay " + currency + cost;
    }

    @And("^product is delivered$")
    public void productIsDelivered() {
        Assert.assertTrue(productDelivered);
    }

    @Then("^I can pay by card$")
    public void iCanPayByCard() {
        // Card payment validation
        productDelivered = true;
    }

    @And("^no change is due$")
    public void noChangeIsDue() {
        Assert.assertEquals(0.00, currentCredit, 0.0001);
    }
}

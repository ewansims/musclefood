import io.github.bonigarcia.wdm.config.DriverManagerType;
import io.github.bonigarcia.wdm.managers.FirefoxDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

public class Main {

    public static void main(String[] args) {
        FirefoxDriverManager.getInstance(DriverManagerType.FIREFOX).setup();
        WebDriver browser = new FirefoxDriver();
        browser.get("");
    }
}

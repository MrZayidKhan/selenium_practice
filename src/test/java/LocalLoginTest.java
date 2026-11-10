import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.File;
import java.time.Duration;

public class LocalLoginTest {

    private WebDriver driver;

    @BeforeMethod
    public void setUp() {
        // Configure Headless Chrome Options (Selenium 4 syntax)
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new");
        options.addArguments("--disable-gpu");
        options.addArguments("--window-size=1920,1080");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");

        driver = new ChromeDriver(options);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));

        // Load the local HTML file using a file URI
        File file = new File("webpage/login.html");
        String loginPageUrl = file.toURI().toString();
        driver.get(loginPageUrl);
    }

    @Test(priority = 1)
    public void testValidLogin() {
        WebElement usernameField = driver.findElement(By.id("username"));
        WebElement passwordField = driver.findElement(By.id("password"));
        WebElement loginButton = driver.findElement(By.id("loginBtn"));

        usernameField.sendKeys("admin");
        passwordField.sendKeys("password123");
        loginButton.click();

        WebElement message = driver.findElement(By.id("message"));
        Assert.assertEquals(message.getText(), "Login Successful!", "Valid login verification failed!");
    }

    @Test(priority = 2)
    public void testInvalidLogin() {
        WebElement usernameField = driver.findElement(By.id("username"));
        WebElement passwordField = driver.findElement(By.id("password"));
        WebElement loginButton = driver.findElement(By.id("loginBtn"));

        usernameField.sendKeys("wrongUser");
        passwordField.sendKeys("wrongPass");
        loginButton.click();

        WebElement message = driver.findElement(By.id("message"));
        Assert.assertEquals(message.getText(), "Invalid username or password!", "Invalid login verification failed!");
    }

    @Test(priority = 3)
    public void testBackgroundColor() {
        WebElement body = driver.findElement(By.tagName("body"));
        String bgColor = body.getCssValue("background-color");

        // Verify that the body background color matches the expected #f0f4f8 value (rgb/rgba format)
        boolean isExpectedColor = bgColor.equals("rgba(240, 244, 248, 1)") || bgColor.equals("rgb(240, 244, 248)");
        Assert.assertTrue(isExpectedColor, "Background color mismatch! Actual color: " + bgColor);
    }

    @AfterMethod
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
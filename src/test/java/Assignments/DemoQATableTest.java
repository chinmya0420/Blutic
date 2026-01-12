package Assignments;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.*;

import java.time.Duration;
import java.util.List;

public class DemoQATableTest {

	WebDriver driver;
	WebDriverWait wait;

	String firstName = "Rakesh";
	String lastName = "Kumar";
	String email = "rakesh@test.com";
	String age = "28";
	String updatedAge = "35";

	@BeforeClass
	public void setup() {
		WebDriverManager.chromedriver().setup();
		driver = new ChromeDriver();
		wait = new WebDriverWait(driver, Duration.ofSeconds(10));
		driver.manage().window().maximize();
		driver.get("https://demoqa.com/webtables");
	}

	@Test
	public void webTableAutomation() {

		int initialRowCount = getRealRowCount();
		System.out.println("Initial real rows = " + initialRowCount);

		driver.findElement(By.id("addNewRecordButton")).click();

		wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("firstName"))).sendKeys(firstName);
		driver.findElement(By.id("lastName")).sendKeys(lastName);
		driver.findElement(By.id("userEmail")).sendKeys(email);
		driver.findElement(By.id("age")).sendKeys(age);
		driver.findElement(By.id("salary")).sendKeys("50000");
		driver.findElement(By.id("department")).sendKeys("QA");
		driver.findElement(By.id("submit")).click();

		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[text()='" + email + "']")));

		int newRowCount = getRealRowCount();
		System.out.println("After add real rows = " + newRowCount);
		Assert.assertEquals(newRowCount, initialRowCount + 1);

		clickElementJS(getEditButtonOfUser(email));

		WebElement ageField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("age")));
		ageField.clear();
		ageField.sendKeys(updatedAge);
		driver.findElement(By.id("submit")).click();

		String actualAge = getAgeOfUser(email);
		System.out.println("Updated Age = " + actualAge);
		Assert.assertEquals(actualAge, updatedAge);
	}

	public int getRealRowCount() {
		List<WebElement> rows = driver.findElements(By.cssSelector(".rt-tbody .rt-tr-group"));
		int count = 0;
		for (WebElement row : rows) {
			if (!row.getText().trim().isEmpty()) {
				count++;
			}
		}
		return count;
	}

	public WebElement getEditButtonOfUser(String email) {
		List<WebElement> rows = driver.findElements(By.cssSelector(".rt-tr-group"));
		for (WebElement row : rows) {
			if (row.getText().contains(email)) {
				return row.findElement(By.cssSelector("span[title='Edit']"));
			}
		}
		throw new RuntimeException("User not found: " + email);
	}

	public String getAgeOfUser(String email) {
		List<WebElement> rows = driver.findElements(By.cssSelector(".rt-tr-group"));
		for (WebElement row : rows) {
			if (row.getText().contains(email)) {
				return row.findElements(By.cssSelector(".rt-td")).get(2).getText();
			}
		}
		return "";
	}

	public void clickElementJS(WebElement element) {
		JavascriptExecutor executor = (JavascriptExecutor) driver;
		executor.executeScript("arguments[0].click();", element);
	}

	@AfterClass
	public void tearDown() {
		driver.quit();
	}
}

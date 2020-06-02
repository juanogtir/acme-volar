package acmevolar.ui;

import java.util.regex.Pattern;
import java.util.concurrent.TimeUnit;
import org.junit.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.Test;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.Select;
import org.springframework.boot.web.server.LocalServerPort;

public class TestUIjossanrui5 {
  private WebDriver driver;
  private String baseUrl;
  private boolean acceptNextAlert = true;
  private StringBuffer verificationErrors = new StringBuffer();
  
  @LocalServerPort
	private int				port;

  @BeforeEach
  public void setUp() throws Exception {

	String pathToChromeDriver="D:\\Descargas";
	System.setProperty("webdriver.chrome.driver", pathToChromeDriver + "\\chromedriver.exe");
    driver = new ChromeDriver();
    baseUrl = "https://www.google.com/";
    driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
  }

  @Test
  public void testUIjossanrui5() throws Exception {
	  driver.get("http://localhost:"+port+"/");
    	driver.findElement(By.linkText("LOGIN")).click();
    	driver.findElement(By.id("username")).click();
    	driver.findElement(By.id("username")).clear();
    	driver.findElement(By.id("username")).sendKeys("airline1");
    	driver.findElement(By.id("password")).click();
    	driver.findElement(By.id("password")).clear();
    	driver.findElement(By.id("password")).sendKeys("airline1");
    	driver.findElement(By.xpath("//button[@type='submit']")).click();
    	
  }

  @AfterEach
  public void tearDown() throws Exception {
    driver.quit();
    String verificationErrorString = verificationErrors.toString();
    if (!"".equals(verificationErrorString)) {
      fail(verificationErrorString);
    }
  }

  private boolean isElementPresent(By by) {
    try {
      driver.findElement(by);
      return true;
    } catch (NoSuchElementException e) {
      return false;
    }
  }

  private boolean isAlertPresent() {
    try {
      driver.switchTo().alert();
      return true;
    } catch (NoAlertPresentException e) {
      return false;
    }
  }

  private String closeAlertAndGetItsText() {
    try {
      Alert alert = driver.switchTo().alert();
      String alertText = alert.getText();
      if (acceptNextAlert) {
        alert.accept();
      } else {
        alert.dismiss();
      }
      return alertText;
    } finally {
      acceptNextAlert = true;
    }
  }
}
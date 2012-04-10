import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.DOM.*;
import static com.codeborne.selenide.Navigation.*;

public class MobileIdAuthenticationSpec {

  @BeforeClass
  public static void configureBaseUrl() {
    baseUrl = "http://localhost:9000";
  }

  @Before
  public void gotoDashboard() {
    open("/auth/logout");
    click(By.linkText("Log in"));
  }

  @Test
  public void userShouldEnterValidMobileNumber() {
    setValue(By.name("phone"), "+372");
    click(By.tagName("button"));
    waitUntil(By.className("alert-error"), hasText("INVALID_INPUT: Invalid PhoneNo"));
  }

  @Test
  public void userShouldHaveMobileIDAgreement() {
    setValue(By.name("phone"), "+37255667788");
    click(By.tagName("button"));
    waitUntil(By.className("alert-error"), hasText("NO_AGREEMENT: User is not a Mobile-ID client"));
  }

  @Test
  public void userGetsSmsAndEntersPassword() {
    // MID test numbers from http://www.openxades.org/dds_test_phone_numbers.html
    setValue(By.name("phone"), "+37200007");
    click(By.tagName("button"));
    waitFor(By.id("challenge"));
    waitUntil(By.id("welcomeMessage"), visible, 30000);
    assertElement(By.id("userName"), hasText("SEITSMES TESTNUMBER"));
    assertElement(By.id("personalCode"), hasText("14212128025"));
  }
}

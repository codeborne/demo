import com.codeborne.selenide.Condition;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.openqa.selenium.By;
import play.test.UnitTest;

import static com.codeborne.selenide.Condition.hasText;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.DOM.*;
import static com.codeborne.selenide.Navigation.baseUrl;
import static com.codeborne.selenide.Navigation.open;

public class MobileIdAuthenticationSpec extends UnitTest {

  @BeforeClass
  public static void configureBaseUrl() {
    baseUrl = "http://localhost:9000";
  }

  @Before
  public void gotoDashboard() {
    open("/user/logout");
    click(By.linkText("Log in"));
  }

  @Test
  public void userShouldEnterValidMobileNumber() {
    setValue(By.name("phone"), "+372");
    click(By.tagName("button"));
    waitFor(By.className("alert-error"), hasText("INVALID_INPUT: Invalid PhoneNo"));
  }

  @Test
  public void userShouldHaveMobileIDAgreement() {
    setValue(By.name("phone"), "+37255667788");
    click(By.tagName("button"));
    waitFor(By.className("alert-error"), hasText("NO_AGREEMENT: User is not a Mobile-ID client"));
  }

  @Test
  public void userGetsSmsAndEntersPassword() {
    setValue(By.name("phone"), "+37259180809");
    click(By.tagName("button"));
    waitFor(By.id("challenge"), visible);
    waitFor(By.id("welcomeMessage"));
    assertElement(By.id("userName"), hasText("Hello, Tõnis Jäägup!"));
    assertElement(By.id("personalCode"), hasText("Personal code: 37612310010"));
  }
}

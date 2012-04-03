import com.codeborne.selenide.Navigation;
import org.junit.*;

import java.util.*;

import org.openqa.selenium.By;
import play.test.*;

import static com.codeborne.selenide.DOM.assertVisible;
import static com.codeborne.selenide.Navigation.baseUrl;
import static com.codeborne.selenide.Navigation.open;

public class DashboardSpec extends UnitTest {

  @BeforeClass
  public static void configureBaseUrl() {
    baseUrl = "http://localhost:9000";
  }

  @Before
  public void gotoDashboard() {
    open("/");
  }

  @Test
  public void showsWelcomeMessage() {
    assertVisible(By.id("welcomeMessage"));
  }

  @Test
  public void showsLoginButton() {
    assertVisible(By.linkText("Log in"));
  }
}

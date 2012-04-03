package controllers;

import com.codeborne.security.AuthenticationException;
import com.codeborne.security.mobileid.MobileIDAuthenticator;
import com.codeborne.security.mobileid.MobileIDSession;
import com.codeborne.security.mobileid.test.MobileIDAuthenticatorStub;
import play.Play;
import play.data.validation.Validation;
import play.mvc.Controller;

import java.io.File;

import com.codeborne.security.mobileid.test.MobileIDAuthenticatorStub;

public class User extends Controller {

  static MobileIDAuthenticator mid = new MobileIDAuthenticator("https://www.openxades.org:8443/");
//  static MobileIDAuthenticator mid = new MobileIDAuthenticatorStub();

  static {
    System.setProperty("javax.net.ssl.trustStore", new File("keystore.jks").getAbsolutePath());
  }
  public static void login(String error) {
    render(error);
  }

  public static void startMid(String phone) {
    try {
      MobileIDSession mobileIDSession = mid.startLogin(phone);
      session.put("mobileid-session", mobileIDSession);

      String challenge = mobileIDSession.challenge;
      render(challenge);
    } catch (AuthenticationException e) {
      String error = e.getMessage();
      params.flash(); // add http parameters to the flash scope
      Validation.keep(); // keep the errors for the next request
      login(error);
    }
  }

  public static void completeMid() {
    try {
      MobileIDSession mobileIDSession = MobileIDSession.fromString(session.get("mobileid-session"));
      mid.waitForLogin(mobileIDSession);
      session.put("userName", mobileIDSession.firstName + " " + mobileIDSession.lastName);
      session.put("personalCode", mobileIDSession.personalCode);
      Dashboard.index();
      session.remove("mobileid-session");
    } catch (AuthenticationException e) {
      String error = e.getMessage();
      params.flash(); // add http parameters to the flash scope
      Validation.keep(); // keep the errors for the next request
      login(error);
    }
  }

  public static void logout() {
    session.remove("userName");
    session.remove("personalCode");
    Dashboard.index();
  }
}
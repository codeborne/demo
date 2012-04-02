package controllers;

import com.codeborne.security.AuthenticationException;
import com.codeborne.security.mobileid.MobileIDAuthenticator;
import com.codeborne.security.mobileid.MobileIDSession;
import play.*;
import play.cache.Cache;
import play.mvc.*;

public class Login extends Controller {

  static MobileIDAuthenticator mid = new MobileIDAuthenticator("https://www.openxades.org:8443/");

  public static void login(String error) {
    render(error);
  }

  public static void startMid(String phone) {
    try {
      MobileIDSession mobileIDSession = mid.startLogin(phone);
      Cache.set(session.getId(), mobileIDSession);
      String challenge = mobileIDSession.challenge;
      render(challenge);
    }
    catch (AuthenticationException e) {
      String error = e.getMessage();
      params.flash(); // add http parameters to the flash scope
      validation.keep(); // keep the errors for the next request
      login(error);
    }
  }

  public static void mid() {
      try {
        MobileIDSession mobileIDSession = (MobileIDSession) Cache.get(session.getId());
        mid.waitForLogin(mobileIDSession);
        String firstName = mobileIDSession.firstName;
        String lastName = mobileIDSession.lastName;
        String personalCode = mobileIDSession.personalCode;
        render(firstName, lastName, personalCode);
      }
      catch (AuthenticationException e) {
        String error = e.getMessage();
        params.flash(); // add http parameters to the flash scope
        validation.keep(); // keep the errors for the next request
        login(error);
      }
    }

}
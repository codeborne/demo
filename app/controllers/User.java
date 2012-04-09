package controllers;

import com.codeborne.security.AuthenticationException;
import com.codeborne.security.mobileid.MobileIDAuthenticator;
import com.codeborne.security.mobileid.MobileIDSession;
import play.Play;
import play.mvc.Catch;
import play.mvc.Controller;

public class User extends Controller {
    static {
        // need to specify a custom truststore with SK root cert in it, otherwise https requests won't work
        System.setProperty("javax.net.ssl.trustStore", "conf/keystore.jks");
    }

    static MobileIDAuthenticator mid = new MobileIDAuthenticator(Play.configuration.getProperty("digidoc.url"));

    @Catch(AuthenticationException.class)
    public static void handleMidFailure(AuthenticationException e) {
        validation.addError("phone", e.getMessage());
        validation.keep();
        login();
    }

    public static void login() {
        render();
    }

    public static void startMid(String phone) {
        MobileIDSession mobileIDSession = mid.startLogin(phone);
        session.put("mobileid-session", mobileIDSession);

        String challenge = mobileIDSession.challenge;
        render(challenge);
    }

    public static void completeMid() {
        MobileIDSession mobileIDSession = MobileIDSession.fromString(session.get("mobileid-session"));
        mid.waitForLogin(mobileIDSession);
        session.put("userName", mobileIDSession.firstName + " " + mobileIDSession.lastName);
        session.put("personalCode", mobileIDSession.personalCode);
        Dashboard.index();
        session.remove("mobileid-session");
    }

    public static void logout() {
        session.remove("userName");
        session.remove("personalCode");
        Dashboard.index();
    }
}
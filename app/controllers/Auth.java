package controllers;

import com.codeborne.security.AuthenticationException;
import com.codeborne.security.mobileid.MobileIDAuthenticator;
import com.codeborne.security.mobileid.MobileIDSession;
import play.Play;
import play.mvc.Catch;
import play.mvc.Controller;

public class Auth extends Controller {
    static {
        // need to specify a custom truststore with SK root cert in it, otherwise https requests won't work
        System.setProperty("javax.net.ssl.trustStore", "conf/keystore.jks");
    }

    static MobileIDAuthenticator mid = new MobileIDAuthenticator(Play.configuration.getProperty("digidoc.url"));

    @Catch(AuthenticationException.class)
    public static void handleMidFailure(AuthenticationException e) {
        validation.addError("phone", e.getMessage());
        validation.keep();
        form();
    }

    public static void form() {
        render();
    }

    public static void startLogin(String phone) {
        MobileIDSession midSession = mid.startLogin(phone);
        session.put("mid-session", midSession);

        String challenge = midSession.challenge;
        render(challenge);
    }

    public static void completeLogin() {
        MobileIDSession midSession = MobileIDSession.fromString(session.get("mid-session"));
        mid.waitForLogin(midSession);
        session.put("userName", midSession.firstName + " " + midSession.lastName);
        session.put("personalCode", midSession.personalCode);
        session.remove("mid-session");
        Dashboard.index();
    }

    public static void logout() {
        session.remove("userName");
        session.remove("personalCode");
        Dashboard.index();
    }
}
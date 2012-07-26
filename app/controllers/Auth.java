package controllers;

import com.codeborne.security.AuthenticationException;
import com.codeborne.security.mobileid.MobileIDAuthenticator;
import com.codeborne.security.mobileid.MobileIDSession;
import play.Logger;
import play.Play;
import play.data.validation.Validation;
import play.mvc.Catch;
import play.mvc.Controller;
import sun.security.provider.X509Factory;

import java.io.File;
import org.apache.commons.codec.binary.Base64;

import javax.security.cert.CertificateException;
import javax.security.cert.X509Certificate;

public class Auth extends Controller {
    static {
        // need to specify a custom truststore with SK root cert in it, otherwise https requests won't work
      File keystore = new File("conf", "keystore.jks");
      if (!keystore.exists())
        throw new RuntimeException("File not found: " + keystore.getAbsolutePath());

      Logger.info("Use digidoc url " + Play.configuration.getProperty("digidoc.url"));
      Logger.info("Read certificates from " + keystore.getAbsolutePath());
      System.setProperty("javax.net.ssl.trustStore", keystore.getAbsolutePath());
    }

    static MobileIDAuthenticator mid = new MobileIDAuthenticator(Play.configuration.getProperty("digidoc.url"));

    @Catch(AuthenticationException.class)
    public static void handleMidFailure(AuthenticationException e) {
        Logger.info(e, e.getMessage());
        Validation.addError("phone", e.getMessage());
        Validation.keep();
        form();
    }

    @Catch(Throwable.class)
    public static void handleAllExceptions(AuthenticationException e) {
        Logger.error(e, e.getMessage());
        Validation.addError("phone", e.getMessage());
        Validation.keep();
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
      try {
        MobileIDSession midSession = MobileIDSession.fromString(session.get("mid-session"));
        mid.waitForLogin(midSession);
        session.put("userName", midSession.firstName + " " + midSession.lastName);
        session.put("personalCode", midSession.personalCode);
        session.remove("mid-session");
        renderJSON("{\"status\": \"ok\"}");
      }
      catch (Exception e) {
        renderJSON("{\"status\": \"error\", \"description\": \"" + e.getMessage() + "\"}");
      }
    }

    public static void loginWithIdCard() throws CertificateException {
      String pem = request.headers.get("SSL_CLIENT_CERT").value();
      String base64 = pem.replace(X509Factory.BEGIN_CERT, "").replace(X509Factory.END_CERT, "").replace(" ", "");
      X509Certificate cert = X509Certificate.getInstance(Base64.decodeBase64(base64));
      String personalCode = cert.getSubjectDN().getName().replaceFirst(".*SERIALNUMBER=(\\d{11}),.*", "$1");
      session.put("userName", cert.getSubjectDN().getName());
      session.put("personalCode", personalCode);
      Dashboard.index();
    }

  public static void logout() {
        session.remove("userName");
        session.remove("personalCode");
        Dashboard.index();
    }
}
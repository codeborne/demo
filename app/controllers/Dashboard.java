package controllers;

import play.mvc.Controller;

public class Dashboard extends Controller {
    public static void index() {
        String userName = session.get("userName");
        String personalCode = session.get("personalCode");
        render(userName, personalCode);
    }
}

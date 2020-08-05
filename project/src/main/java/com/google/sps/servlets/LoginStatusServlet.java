package com.google.sps.servlets;

import com.google.appengine.api.users.UserServiceFactory;
import com.google.sps.utilities.CommonUtils;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet that returns whether user is logged in and email of the user. */
@WebServlet("/login-status")
public class LoginStatusServlet extends HttpServlet {
  /**
   * Writes whether user is logged in and email of user if the user is logged in to response.
   *
   * @param request servlet request
   * @param response servlet response
   * @throws IOException if Input/Output error occurs
   */
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    response.setContentType("application/json;");

    LoginStatus status;
    if (UserServiceFactory.getUserService().isUserLoggedIn()) {
      status =
          new LoginStatus(/* isLoggedin= */ true,
              UserServiceFactory.getUserService().getCurrentUser().getEmail());
    } else {
      status = new LoginStatus(/* isLoggedin= */ false, /* userEmail= */ "");
    }

    response.getWriter().println(CommonUtils.convertToJson(status));
  }

  private final class LoginStatus {
    private final boolean isLoggedIn;
    private final String userEmail;

    public LoginStatus(boolean isLoggedIn, String userEmail) {
      this.isLoggedIn = isLoggedIn;
      this.userEmail = userEmail;
    }
  }
}

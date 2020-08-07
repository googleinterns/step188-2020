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
          new LoginStatus(
              LoginStatus.LoginState.LOGGED_IN,
              UserServiceFactory.getUserService().getCurrentUser().getEmail());
    } else {
      status = new LoginStatus(LoginStatus.LoginState.LOGGED_OUT, /* userEmail= */ "");
    }

    response.getWriter().println(CommonUtils.convertToJson(status));
  }

  public static final class LoginStatus {
    private final LoginState loginState;

    public static enum LoginState {
      LOGGED_IN,
      LOGGED_OUT,
    }

    private final String userEmail;

    public LoginStatus(LoginState loginState, String userEmail) {
      this.loginState = loginState;
      this.userEmail = userEmail;
    }
  }
}
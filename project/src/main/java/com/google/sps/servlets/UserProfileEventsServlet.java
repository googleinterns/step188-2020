package com.google.sps.servlets;

import com.google.appengine.api.users.UserServiceFactory;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/user-events")
public class UserProfileEventsServlet extends HttpServlet {
  private static final String email =
      UserServiceFactory.getUserService().getCurrentUser().getEmail();

  /** Gets the current user's corresponding events in DB */
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    
  }

  /** Updates the current user's corresponding events in DB */
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

  }
}

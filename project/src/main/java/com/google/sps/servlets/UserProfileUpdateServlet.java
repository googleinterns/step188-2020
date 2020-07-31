package com.google.sps.servlets;

import com.google.appengine.api.users.UserServiceFactory;
import com.google.sps.data.User;
import com.google.sps.utilities.CommonUtils;
import com.google.sps.utilities.DatabaseConstants;
import com.google.sps.utilities.DatabaseWrapper;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import javax.json.Json;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet that handles user profile edits. */
@WebServlet("/profile-update")
public class UserProfileUpdateServlet extends HttpServlet {
  private static final String email =
      UserServiceFactory.getUserService().getCurrentUser().getEmail();
  private DatabaseWrapper databaseWrapper =
      new DatabaseWrapper(DatabaseConstants.INSTANCE_ID, DatabaseConstants.DATABASE_ID);

  /** Writes out information for the user corresponding to the logged-in email */
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    Optional<User> userOptional = databaseWrapper.readUserFromEmail(email);
    if (!userOptional.isPresent()) {
      String userJson =
          Json.createObjectBuilder()
              .add("name", "anonymous")
              .add("email", email)
              .add("interests", CommonUtils.createJsonArray(new HashSet<>()))
              .add("skills", CommonUtils.createJsonArray(new HashSet<>()))
              .build()
              .toString();
    }
    User user = userOptional.get();
    String userJson =
        Json.createObjectBuilder()
            .add("name", user.getName())
            .add("email", email)
            .add("interests", CommonUtils.createJsonArray(user.getInterests()))
            .add("skills", CommonUtils.createJsonArray(user.getSkills()))
            .build()
            .toString();
    response.setContentType("application/json;");
    response.setCharacterEncoding("UTF-8");
    response.getWriter().println(userJson);
  }

  /** Given user fields, inserts or updates the corresponding entry in storage */
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    // Get the input from the form.
    String name = request.getParameter("name");
    Set<String> interests = new HashSet<>(Arrays.asList(request.getParameter("interests")));
    Set<String> skills = new HashSet<>(Arrays.asList(request.getParameter("skills")));

    User updatedUser =
        new User.Builder(name, email).setInterests(interests).setSkills(skills).build();
    databaseWrapper.insertOrUpdateUser(updatedUser);
    response.sendRedirect("/profile.html");
  }
}

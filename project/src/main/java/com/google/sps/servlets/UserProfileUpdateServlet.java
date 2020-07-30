package com.google.sps.servlets;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.sps.data.User;
import com.google.sps.utilities.Constants;
import com.google.sps.utilities.DatabaseWrapper;
import java.io.IOException;
import java.util.HashSet;
import java.util.Arrays;
import java.util.Set;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet that handles user profile edits. */
@WebServlet("/profile-update")
public class UserProfileUpdateServlet extends HttpServlet {
  private static final String email = UserServiceFactory.getUserService().getCurrentUser().getEmail();
  private DatabaseWrapper databaseWrapper = new DatabaseWrapper(Constants.DB_INSTANCEID, Constants.DB_DATABASEID);

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    User user = databaseWrapper.readUserFromEmail(email);
    String userJson = Json.createObjectBuilder()
        .add("name", user.getName())
        .add("email", email)
        .add("interests", createJsonArray(user.getInterests()))
        .add("skills", createJsonArray(user.getSkills()))
        .build()
        .toString();
    response.setContentType("application/json;");
    response.setCharacterEncoding("UTF-8");
    response.getWriter().println(userJson);
  }

  public JsonArray createJsonArray(Set<String> elements) {
    JsonArrayBuilder builder = Json.createArrayBuilder();
    for (String element : elements) {
      builder.add(element);
    }
    return builder.build();
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    // Get the input from the form.
    String name = request.getParameter("name");
    Set<String> interests = new HashSet<>(Arrays.asList(request.getParameter("interests")));
    Set<String> skills = new HashSet<>(Arrays.asList(request.getParameter("skills")));
    
    User updatedUser =
        new User.Builder(name, email)
            .setInterests(interests)
            .setSkills(skills)
            .build();
    databaseWrapper.insertOrUpdateUser(updatedUser);
    response.sendRedirect("/profile.html");
  }
}

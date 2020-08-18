package com.google.sps.servlets;

import com.google.appengine.api.users.UserServiceFactory;
import com.google.sps.data.User;
import com.google.sps.utilities.CommonUtils;
import com.google.sps.utilities.SpannerTasks;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
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
  private static final String NAME = "name";
  private static final String EMAIL = "email";
  private static final String INTERESTS = "interests";
  private static final String SKILLS = "skills";
  private static String email;

  /** Writes out information for the user corresponding to the logged-in email */
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    email = UserServiceFactory.getUserService().getCurrentUser().getEmail();
    Optional<User> userOptional = SpannerTasks.shallowReadUserFromEmail(email);
    User user;
    if (userOptional.isPresent()) {
      user = userOptional.get();
    } else {
      user = new User.Builder("anonymous", email).build();
      SpannerTasks.insertOrUpdateUser(user);
    }
    String userJson =
        Json.createObjectBuilder()
            .add(NAME, user.getName())
            .add(EMAIL, email)
            .add(INTERESTS, CommonUtils.createJsonArray(user.getInterests()))
            .add(SKILLS, CommonUtils.createJsonArray(user.getSkills()))
            .build()
            .toString();
    response.setContentType("application/json;charset=UTF-8");
    response.getWriter().println(userJson);
  }

  /** Given user fields, inserts or updates the corresponding entry in storage */
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    email = UserServiceFactory.getUserService().getCurrentUser().getEmail();
    // Get the input from the form.
    String name = request.getParameter(NAME);
    Set<String> interests = new HashSet<>(splitAsList(request.getParameter(INTERESTS)));
    Set<String> skills = new HashSet<>(splitAsList(request.getParameter(SKILLS)));
    User updatedUser =
        new User.Builder(name, email).setInterests(interests).setSkills(skills).build();

    SpannerTasks.insertOrUpdateUser(updatedUser);
    response.sendRedirect("/profile.html");
  }

  private static List<String> splitAsList(String values) {
    return Arrays.asList(values.split("\\s*,\\s*"));
  }
}

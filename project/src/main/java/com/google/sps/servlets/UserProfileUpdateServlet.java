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
  private static final String email =
      UserServiceFactory.getUserService().getCurrentUser().getEmail();

  /** Writes out information for the user corresponding to the logged-in email */
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    Optional<User> userOptional = SpannerTasks.shallowReadUserFromEmail(email);
    String userJson;
    if (!userOptional.isPresent()) {
      userJson =
          Json.createObjectBuilder()
              .add("name", "anonymous")
              .add("email", email)
              .add("interests", CommonUtils.createJsonArray(new HashSet<>()))
              .add("skills", CommonUtils.createJsonArray(new HashSet<>()))
              .add("eventsHosting", CommonUtils.createJsonArray(new HashSet<>()))
              .add("eventsParticipating", CommonUtils.createJsonArray(new HashSet<>()))
              .add("eventsVolunteering", CommonUtils.createJsonArray(new HashSet<>()))
              .build()
              .toString();
    } else {
      // TODO: When PRs for user profile events are merged, update the events here
      User user = userOptional.get();
      userJson =
          Json.createObjectBuilder()
              .add("name", user.getName())
              .add("email", email)
              .add("interests", CommonUtils.createJsonArray(user.getInterests()))
              .add("skills", CommonUtils.createJsonArray(user.getSkills()))
              .add("eventsHosting", CommonUtils.createJsonArray(new HashSet<>()))
              .add("eventsParticipating", CommonUtils.createJsonArray(new HashSet<>()))
              .add("eventsVolunteering", CommonUtils.createJsonArray(new HashSet<>()))
              .build()
              .toString();
    }
    response.setContentType("application/json;charset=UTF-8");
    response.getWriter().println(userJson);
  }

  /** Given user fields, inserts or updates the corresponding entry in storage */
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    // Get the input from the form.
    String name = request.getParameter("name");
    Set<String> interests = new HashSet<>(splitAsList(request.getParameter("interests")));
    Set<String> skills = new HashSet<>(splitAsList(request.getParameter("skills")));
    User updatedUser =
        new User.Builder(name, email).setInterests(interests).setSkills(skills).build();

    SpannerTasks.insertOrUpdateUser(updatedUser);
    response.sendRedirect("/profile.html");
  }

  private static List<String> splitAsList(String values) {
    return Arrays.asList(values.split("\\s*,\\s*"));
  }
}

package com.google.sps.servlets;

import com.google.appengine.api.users.UserServiceFactory;
import com.google.sps.data.Event;
import com.google.sps.data.User;
import com.google.sps.utilities.CommonUtils;
import com.google.sps.utilities.EventRanker;
import com.google.sps.utilities.SpannerTasks;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/event-ranker")
public class EventRankerServlet extends HttpServlet {
  /** Returns events in order of relevance */
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String email = UserServiceFactory.getUserService().getCurrentUser().getEmail();
    Optional<User> userOptional = SpannerTasks.shallowReadUserFromEmail(email);
    User user;
    if (userOptional.isPresent()) {
      user = userOptional.get();
      // TODO: Replace getAllEvents() with the appropriate method to get all results to display
      Set<Event> relevantEvents = SpannerTasks.getAllEvents();
      List<Event> rankedRelevantEvents = EventRanker.rankEvents(user, relevantEvents);
      response.setContentType("application/json;");
      response.getWriter().println(CommonUtils.convertToJson(rankedRelevantEvents));
    } else {
      response.sendError(HttpServletResponse.SC_BAD_REQUEST, "User not found.");
    }
  }
}

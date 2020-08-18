package com.google.sps.servlets;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.sps.data.Event;
import com.google.sps.utilities.CommonUtils;
import com.google.sps.utilities.EventRanker;
import com.google.sps.utilities.SpannerTasks;
import java.io.IOException;
import java.util.List;
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
    User user = UserServiceFactory.getUserService().getCurrentUser();
    // TODO: Replace getAllEvents() with the appropriate method to get all results to display
    Set<Event> relevantEvents = SpannerTasks.getAllEvents();
    List<Event> rankedRelevantEvents = EventRanker.rankEvents(relevantEvents);
    response.setContentType("application/json;");
    response.getWriter().println(CommonUtils.convertToJson(rankedRelevantEvents));
  }
}

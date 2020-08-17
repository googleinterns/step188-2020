package com.google.sps.servlets;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.sps.data.Event;
import com.google.sps.data.EventVolunteering;
import com.google.sps.utilities.CommonUtils;
import com.google.sps.utilities.SpannerTasks;
import java.io.IOException;
import java.util.Set;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/user-events")
public class UserProfileEventsServlet extends HttpServlet {
  private static final String EVENT_TYPE = "event-type";
  private static final String HOSTING = "hosting";
  private static final String VOLUNTEERING = "volunteering";
  private static final String PARTICIPATING = "participating";

  /** 
   * Gets the current user's events corresponding to the event type specified as a parameter.
   */
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String eventType = request.getParameter(EVENT_TYPE);
    User user = UserServiceFactory.getUserService().getCurrentUser();

    if (user == null) {
      response.sendRedirect("/index.html");
      return;
    } else if (eventType == null) {
      response.sendError(
          HttpServletResponse.SC_BAD_REQUEST, String.format("No event type specified."));
      return;
    }

    try {
      response.getWriter().println(getEventsJSONByEmail(eventType, user.getEmail()));
    } catch (IllegalArgumentException e) {
      response.sendError(
        HttpServletResponse.SC_BAD_REQUEST, String.format("Invalid event type."));
    }
  }

  /**
   * Get the events of the given type for the given email.
   * @param eventType type of event data to retrieve
   * @param userEmail user for which to retrieve events
   * @return JSON representing the event data
   * @throw IllegalArgumentException if eventType is invalid
   */
  private static String getEventsJSONByEmail(String eventType, String userEmail) {
    switch (eventType) {
      case VOLUNTEERING:
        Set<EventVolunteering> eventsVolunteering =
            SpannerTasks.getEventsVolunteeringByEmail(userEmail);
        return CommonUtils.convertToJson(eventsVolunteering);
      case PARTICIPATING:
        Set<Event> eventsParticipating =
            SpannerTasks.getEventsParticipatingByEmail(userEmail);
        return CommonUtils.convertToJson(eventsParticipating);
      case HOSTING:
        Set<Event> eventsHosting =
            SpannerTasks.getEventsHostingByEmail(userEmail);
        return CommonUtils.convertToJson(eventsHosting);
      default:
        throw new IllegalArgumentException("Invalid event type.");
    }
  }
}

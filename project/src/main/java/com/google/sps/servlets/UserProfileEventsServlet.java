package com.google.sps.servlets;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserServiceFactory;
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
  private static final String VOLUNTEERING = "volunteering";

  /** 
   * Gets the current user's events corresponding to the event type
   * specified as a parameter.
   */
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String eventType = request.getParameter(EVENT_TYPE);
    User user = UserServiceFactory.getUserService().getCurrentUser();

    if (user == null) {
      response.sendRedirect("/index.html");
    } else if (eventType == null) {
      response.sendError(
          HttpServletResponse.SC_BAD_REQUEST, String.format("No event type specified."));
      return;
    } else {
      switch(eventType) {
        case VOLUNTEERING:
          Set<EventVolunteering> eventsVolunteering = SpannerTasks.getEventsVolunteeringByEmail(user.getEmail());

          response.getWriter().println(CommonUtils.convertToJson(eventsVolunteering));
          break;
        // TO DO: add case statements for hosting and participating with retrieval of data
        default:
          response.sendError(HttpServletResponse.SC_BAD_REQUEST, String.format("Invalid event type."));
    }
    }
  }
}

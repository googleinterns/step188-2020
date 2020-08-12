package com.google.sps.servlets;

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
  private static final String email =
      UserServiceFactory.getUserService().getCurrentUser().getEmail();

  /** Gets the current user's corresponding events in DB */
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String eventType = request.getParameter(EVENT_TYPE);
    if (eventType.equals(VOLUNTEERING)) {
        Set<EventVolunteering> eventsVolunteering = SpannerTasks.getEventsVolunteeringByEmail(email);
        response.getWriter().println(CommonUtils.convertToJson(eventsVolunteering));
    }
  }

  /** Updates the current user's corresponding events in DB */
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

  }
}

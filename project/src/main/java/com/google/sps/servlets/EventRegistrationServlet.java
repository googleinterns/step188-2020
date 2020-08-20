package com.google.sps.servlets;

import com.google.appengine.api.users.UserServiceFactory;
import com.google.cloud.Date;
import com.google.gson.Gson;
import com.google.sps.data.Event;
import com.google.sps.data.User;
import com.google.sps.utilities.CommonUtils;
import com.google.sps.utilities.SpannerTasks;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/register-event")
public class EventRegistrationServlet extends HttpServlet {
  /** Update event with logged in user as attendee when they hit register */
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String eventId = request.getParameter("eventId");
    Optional<Event> eventOptional = SpannerTasks.getEventById(eventId);

    // If event DNE, sends 404 ERR to frontend
    if (eventOptional.isPresent()) {
      User loggedInUser = SpannerTasks.getLoggedInUser().get();
      Event event = eventOptional.get();
      boolean isHost = false;
      if (!loggedInUser.getEmail().equals(event.getHost().getEmail())) {
        SpannerTasks.insertorUpdateEvent(
            event.toBuilder().setId(eventId).addAttendee(loggedInUser).build());
        isHost = true;
      }
      // redirect to event details
      String redirectUrl =
          String.format(
              "/event-details.html?eventId=%s&register=false&isHost=%s", event.getId(), isHost);
      response.sendRedirect(redirectUrl);
      response.getWriter().println(CommonUtils.convertToJson(event));
    } else {
      response.sendError(HttpServletResponse.SC_NOT_FOUND);
    } 
  }
}

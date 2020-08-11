package com.google.sps.servlets;

import com.google.cloud.Date;
import com.google.gson.Gson;
import com.google.sps.data.Event;
import com.google.sps.data.User;
import com.google.sps.utilities.SpannerTasks;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/create-event")
public class EventCreationServlet extends HttpServlet {
  /** Returns event details from database */
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String eventId = request.getParameter("eventId");
    Optional<Event> eventOptional = SpannerTasks.getEventById(eventId);

    // If event DNE, sends 404 ERR to frontend
    if (eventOptional.isPresent()) {
      Event event = eventOptional.get();
      response.setContentType("text/html;");
      response.getWriter().println(new Gson().toJson(event));
    } else {
      response.sendError(
          HttpServletResponse.SC_NOT_FOUND,
          String.format("No events found with event ID %s", eventId));
    }
  }

  /** Posts new created event to database and redirects to page with created event details */
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String name = request.getParameter("name");
    String[] parsedDate = request.getParameter("date").split("/");
    Date date =
        Date.fromYearMonthDay(
            Integer.parseInt(parsedDate[2]),
            Integer.parseInt(parsedDate[1]),
            Integer.parseInt(parsedDate[0]));
    String time = request.getParameter("time");
    String description = request.getParameter("description");
    String location = request.getParameter("location");
    Set<String> labels =
        Collections.unmodifiableSet(
            new HashSet<>(
                Arrays.asList("None"))); // hardcoded for now, we need to create label pool first

    /** TO DO: Replace with current logged in user after PR #43 pushed */
    String NAME = "Bob Smith";
    String EMAIL = "bobsmith@example.com";
    User host = new User.Builder(NAME, EMAIL).build();
    Event event = new Event.Builder(name, description, labels, location, date, time, host).build();
    SpannerTasks.insertorUpdateEvent(event);

    String redirectUrl = "/event-details.html?eventId=" + event.getId();
    response.sendRedirect(redirectUrl);
  }
}

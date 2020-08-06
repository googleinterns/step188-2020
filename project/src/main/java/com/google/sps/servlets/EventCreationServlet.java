package com.google.sps.servlets;

import com.google.appengine.api.users.UserServiceFactory;
import com.google.cloud.Date;
import com.google.gson.Gson;
import com.google.sps.data.Event;
import com.google.sps.data.User;
import com.google.sps.utilities.DatabaseWrapper;
import com.google.sps.utilities.WrapperDatabaseService;
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

@WebServlet("/create-event")
public class EventCreationServlet extends HttpServlet {
  private static final DatabaseWrapper dbWrapper = new DatabaseWrapper(new WrapperDatabaseService());

  /** Returns event details from database */
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String eventId = request.getParameter("eventId");
    Optional<Event> eventOptional = dbWrapper.getEventById(eventId);
    dbWrapper.closeConnection();

    // If event DNE, sends 404 ERR to frontend
    if (!eventOptional.isPresent()) {
        response.sendError(HttpServletResponse.SC_NOT_FOUND);
    } else {
        Event event = eventOptional.get().toBuilder().setId(eventId).build();
        response.setContentType("text/html;");
        response.getWriter().println(new Gson().toJson(event));
    }  
  }

  /** Posts new created event to database and redirects to page with created event details*/
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String name = request.getParameter("name");
    String[] parsedDate = request.getParameter("date").split("/");
    Date date = Date.fromYearMonthDay(Integer.parseInt(parsedDate[2]), Integer.parseInt(parsedDate[1]), Integer.parseInt(parsedDate[0]));
    String description = request.getParameter("description");
    String location = request.getParameter("location");
    Set<String> labels = Collections.unmodifiableSet(new HashSet<>(
        Arrays.asList("None"))); // hardcoded for now, we need to create label pool first

    /** TO DO: Replace with current logged in user after PR #43 pushed*/
    String NAME = "Bob Smith";
    String EMAIL = "bobsmith@example.com";
    User host = new User.Builder(NAME, EMAIL).build();
    Event event = new Event.Builder(name, description, labels, location, date, host).build();
    dbWrapper.insertorUpdateEvent(event);
    dbWrapper.closeConnection();

    String redirectUrl = "/event-details.html?eventId=" + event.getId();
    response.sendRedirect(redirectUrl);
  }
}
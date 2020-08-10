package com.google.sps.servlets;

import com.google.appengine.api.users.UserServiceFactory;
import com.google.cloud.Date;
import com.google.gson.Gson;
import com.google.sps.data.Event;
import com.google.sps.data.User;
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

@WebServlet("/event-details")
public class EventDetailServlet extends HttpServlet {
  /** Returns all event details from database */
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    Set<Event> events = SpannerTasks.getAllEvents();
    response.setContentType("application/json;");
    response.getWriter().println(new Gson().toJson(events));
  }
}

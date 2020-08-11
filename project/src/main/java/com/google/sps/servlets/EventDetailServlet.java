package com.google.sps.servlets;

import com.google.sps.data.Event;
import com.google.sps.utilities.CommonUtils;
import com.google.sps.utilities.SpannerTasks;
import java.io.IOException;
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
    response.getWriter().println(CommonUtils.convertToJson(events));
  }
}

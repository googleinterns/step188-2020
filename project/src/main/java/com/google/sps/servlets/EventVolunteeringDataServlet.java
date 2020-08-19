package com.google.sps.servlets;

import com.google.sps.data.VolunteeringOpportunity;
import com.google.sps.utilities.CommonUtils;
import com.google.sps.utilities.DatabaseConstants;
import com.google.sps.utilities.SpannerTasks;
import java.io.IOException;
import java.util.Set;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet to get all volunteering data for a given event */
@WebServlet("/event-volunteering-data")
public class EventVolunteeringDataServlet extends HttpServlet {
  private static final String EVENT_ID = "event-id";

  /**
   * Queries database for all opportunities with event ID given in the request parameter and writes
   * opportunities to the response.
   *
   * @param request servlet request
   * @param response servlet response
   * @throws IOException if Input/Output error occurs
   */
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String eventId = request.getParameter(EVENT_ID);
    if (eventId == null) {
      response.sendError(HttpServletResponse.SC_BAD_REQUEST, String.format("No event specified."));
      return;
    }

    Set<VolunteeringOpportunity> opportunities =
        SpannerTasks.getVolunteeringOpportunitiesByEventId(eventId);
    response.setContentType("application/json;");
    response.getWriter().println(CommonUtils.convertToJson(opportunities));
  }
}

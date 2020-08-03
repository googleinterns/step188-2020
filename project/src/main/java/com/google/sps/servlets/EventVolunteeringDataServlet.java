package com.google.sps.servlets;

import com.google.sps.data.VolunteeringOpportunity;
import com.google.sps.utilities.CommonUtils;
import com.google.sps.utilities.DatabaseConstants;
import com.google.sps.utilities.DatabaseWrapper;
import java.io.IOException;
import java.util.Set;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet to get all volunteering data for a given event */
@WebServlet("/event-volunteering-data")
public class EventVolunteeringDataServlet extends HttpServlet {
  private static final String HARDCODED_EVENT_ID = "0883de79-17d7-49a3-a866-dbd5135062a8";
  private static final DatabaseWrapper databaseWrapper =
      new DatabaseWrapper(DatabaseConstants.INSTANCE_ID, DatabaseConstants.DATABASE_ID);

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    // TO DO: change eventId to parameter value
    Set<VolunteeringOpportunity> opportunities =
        databaseWrapper.getVolunteeringOpportunitiesByEventId(HARDCODED_EVENT_ID);

    response.setContentType("application/json;");
    response.getWriter().println(CommonUtils.convertToJson(opportunities));
  }
}

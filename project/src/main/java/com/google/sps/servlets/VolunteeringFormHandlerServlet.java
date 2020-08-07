package com.google.sps.servlets;

import com.google.sps.data.VolunteeringOpportunity;
import com.google.sps.utilities.CommonUtils;
import com.google.sps.utilities.SpannerTasks;
import java.io.IOException;
import java.util.Set;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet that handles submission of the form for creating volunteering opportunities. */
@WebServlet("/volunteering-form-handler")
public class VolunteeringFormHandlerServlet extends HttpServlet {
  private static final String OPPORTUNITY_ID = "opportunity-id";
  private static final String NAME = "name";
  private static final String NUM_SPOTS_LEFT = "num-spots-left";
  private static final String REQUIRED_SKILL = "required-skill";
  private static final String HARDCODED_EVENT_ID = "0883de79-17d7-49a3-a866-dbd5135062a8";

  /**
   * Inserts volunteering opportunity with parameter values for attributes into the database if
   * opportunity ID is not given as a request parameter and updates volunteering opportunity with
   * the opportunity ID if given as a request parameter.
   *
   * @param request servlet request
   * @param response servlet response
   * @throws IOException if Input/Output error occurs
   */
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String opportunityId = request.getParameter(OPPORTUNITY_ID);
    String name = CommonUtils.getParameter(request, NAME, /* DefaultValue= */ "");
    long numSpotsLeft =
        Long.parseLong(CommonUtils.getParameter(request, NUM_SPOTS_LEFT, /* DefaultValue= */ "0"));
    Set<String> requiredSkills = CommonUtils.getParameterValues(request, REQUIRED_SKILL);

    // If opportunityId is not passed as a parameter, perform an insert else perform an update
    if (opportunityId == null) {
      insertVolunteeringOpportunityInDB(name, numSpotsLeft, requiredSkills);
    } else {
      updateVolunteeringOpportunityInDB(opportunityId, name, numSpotsLeft, requiredSkills);
    }
    response.sendRedirect("/events-feed.html");
  }

  private static void insertVolunteeringOpportunityInDB(
      String name, long numSpotsLeft, Set<String> requiredSkills) {
    VolunteeringOpportunity opportunity =
        new VolunteeringOpportunity.Builder(HARDCODED_EVENT_ID, name, numSpotsLeft)
            .setRequiredSkills(requiredSkills)
            .build();
    // TO DO: change eventId to parameter value
    SpannerTasks.insertVolunteeringOpportunity(opportunity);
  }

  private static void updateVolunteeringOpportunityInDB(
      String opportunityId, String name, long numSpotsLeft, Set<String> requiredSkills) {
    VolunteeringOpportunity opportunity =
        new VolunteeringOpportunity.Builder(HARDCODED_EVENT_ID, name, numSpotsLeft)
            .setOpportunityId(opportunityId)
            .setRequiredSkills(requiredSkills)
            .build();
    // TO DO: change eventId to parameter value
    SpannerTasks.updateVolunteeringOpportunity(opportunity);
  }
}

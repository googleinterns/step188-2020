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

/** Servlet that handles submission of the form for creating volunteering opportunities. */
@WebServlet("/volunteering-form-handler")
public class VolunteeringFormHandlerServlet extends HttpServlet {
  private static final String OPPORTUNITY_ID = "opportunity-id";
  private static final String OPPORTUNITY_NAME = "opportunity-name";
  private static final String OPPORTUNITY_NUM_SPOTS = "opportunity-num-spots";
  private static final String SKILL = "skill";
  private static final String HARDCODED_EVENT_ID = "0883de79-17d7-49a3-a866-dbd5135062a8";
  private static final DatabaseWrapper databaseWrapper =
      new DatabaseWrapper(DatabaseConstants.INSTANCE_ID, DatabaseConstants.DATABASE_ID);

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String opportunityId = request.getParameter(OPPORTUNITY_ID);
    String opportunityName =
        CommonUtils.getParameter(request, OPPORTUNITY_NAME, /* DefaultValue= */ "");
    int opportunityNumberOfSpots =
        Integer.parseInt(
            CommonUtils.getParameter(request, OPPORTUNITY_NUM_SPOTS, /* DefaultValue= */ "0"));
    Set<String> skills = CommonUtils.getParameterValues(request, SKILL);

    // If opportunityId is not passed as a parameter, perform an insert else perform an update
    if (opportunityId == null)
      insertVolunteeringOpportunityInDB(opportunityName, opportunityNumberOfSpots, skills);
    else
      updateVolunteeringOpportunityInDB(
          opportunityId, opportunityName, opportunityNumberOfSpots, skills);

    response.sendRedirect("/events-feed.html");
  }

  private static void insertVolunteeringOpportunityInDB(
      String opportunityName, int opportunityNumberOfSpots, Set<String> requiredSkills) {
    VolunteeringOpportunity opportunity =
        new VolunteeringOpportunity.Builder(
                HARDCODED_EVENT_ID, opportunityName, opportunityNumberOfSpots)
            .setRequiredSkills(requiredSkills)
            .build();
    // TO DO: change eventId to parameter value
    databaseWrapper.insertVolunteeringOpportunity(opportunity);
  }

  private static void updateVolunteeringOpportunityInDB(
      String opportunityId,
      String opportunityName,
      int opportunityNumberOfSpots,
      Set<String> requiredSkills) {
    VolunteeringOpportunity opportunity =
        new VolunteeringOpportunity.Builder(
                HARDCODED_EVENT_ID, opportunityName, opportunityNumberOfSpots)
            .setOpportunityId(opportunityId)
            .setRequiredSkills(requiredSkills)
            .build();
    // TO DO: change eventId to parameter value
    databaseWrapper.updateVolunteeringOpportunity(opportunity);
  }
}

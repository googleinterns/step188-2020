package com.google.sps.servlets;

import com.google.sps.data.VolunteeringOpportunity;
import com.google.sps.utilities.CommonUtils;
import com.google.sps.utilities.SpannerTasks;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
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
  private static final String EVENT_ID = "event-id";

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
    String name = request.getParameter(NAME);
    long numSpotsLeft =
        Long.parseLong(CommonUtils.getParameter(request, NUM_SPOTS_LEFT, /* DefaultValue= */ "0"));
    Set<String> requiredSkills = split(CommonUtils.getParameter(request, REQUIRED_SKILL, /* DefaultValue= */ ""));
    String eventId = request.getParameter(EVENT_ID);

    if (name == null) {
      response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Name not specified.");
      return;
    } else if (eventId == null) {
      response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Event ID not specified.");
      return;
    }

    // If opportunityId is not passed as a parameter, perform an insert else perform an update
    if (opportunityId == null) {
      insertVolunteeringOpportunityInDB(eventId, name, numSpotsLeft, requiredSkills);
    } else {
      updateVolunteeringOpportunityInDB(opportunityId, eventId, name, numSpotsLeft, requiredSkills);
    }
    response.sendRedirect(String.format("/event-details.html?eventId=%s", eventId));
  }

  private static void insertVolunteeringOpportunityInDB(
      String eventId, String name, long numSpotsLeft, Set<String> requiredSkills) {
    VolunteeringOpportunity opportunity =
        new VolunteeringOpportunity.Builder(eventId, name, numSpotsLeft)
            .setRequiredSkills(requiredSkills)
            .build();
    // TO DO: change eventId to parameter value
    SpannerTasks.insertVolunteeringOpportunity(opportunity);
  }

  private static void updateVolunteeringOpportunityInDB(
      String opportunityId,
      String eventId,
      String name,
      long numSpotsLeft,
      Set<String> requiredSkills) {
    VolunteeringOpportunity opportunity =
        new VolunteeringOpportunity.Builder(eventId, name, numSpotsLeft)
            .setOpportunityId(opportunityId)
            .setRequiredSkills(requiredSkills)
            .build();
    // TO DO: change eventId to parameter value
    SpannerTasks.updateVolunteeringOpportunity(opportunity);
  }

  private static Set<String> split(String values) {
    return Arrays.stream(values.split("\\s*,\\s*")).collect(Collectors.toSet());
  }
}

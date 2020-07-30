package com.google.sps.servlets;

import com.google.sps.data.VolunteeringOpportunity;
import com.google.sps.utilities.CommonUtils;
import com.google.sps.utilities.Constants;
import com.google.sps.utilities.DatabaseWrapper;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/volunteering-form-handler")
public class VolunteeringFormHandlerServlet extends HttpServlet {
  private static final String OPPORTUNITY_NAME = "opportunity-name";
  private static final String OPPORTUNITY_NUM_SPOTS = "opportunity-num-spots";
  private static final String SKILL = "skill";
  private static final String HARDCODED_EVENT_ID = "0883de79-17d7-49a3-a866-dbd5135062a8";

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String volunteerName =
        CommonUtils.getParameter(request, OPPORTUNITY_NAME, /* DefaultValue= */ "");
    Integer volunteerNumber =
        Integer.parseInt(
            CommonUtils.getParameter(request, OPPORTUNITY_NUM_SPOTS, /* DefaultValue= */ "0"));
    Set<String> skills = CommonUtils.getParameterValues(request, SKILL);

    insertVolunteeringOpportunityIntoDB(volunteerName, volunteerNumber, skills);

    response.sendRedirect("/events-feed.html");
  }

  private void insertVolunteeringOpportunityIntoDB(
      String volunteerName, Integer volunteerNumber, Set<String> requiredSkills) {
    DatabaseWrapper dbWrapper = new DatabaseWrapper(Constants.DB_INSTANCEID, Constants.DB_DATABASEID);
    VolunteeringOpportunity opportunity =
        new VolunteeringOpportunity
            .Builder(HARDCODED_EVENT_ID, volunteerName, volunteerNumber)
            .setRequiredSkills(requiredSkills)
            .build();
    // TO DO: change eventId to parameter value
    dbWrapper.insertVolunteeringOpportunity(opportunity);
  }
}

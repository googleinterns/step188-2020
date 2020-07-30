package com.google.sps.servlets;

import com.google.sps.data.VolunteeringOpportunity;
import com.google.sps.utilities.CommonUtils;
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
  private static final String VOLUNTEER_TYPE = "volunteer-type";
  private static final String VOLUNTEER_NUMBER = "volunteer-number";
  private static final String SKILL = "skill";

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String volunteerName =
        CommonUtils.getParameter(request, VOLUNTEER_TYPE, /* DefaultValue= */ "");

    Integer volunteerNumber = Integer.parseInt(
        CommonUtils.getParameter(request, VOLUNTEER_NUMBER, /* DefaultValue= */ "0"));

    Set<String> skills = new HashSet<String>();

    String currentSkill = "";
    int skillNumber = 0;
    do {
      currentSkill = CommonUtils.getParameter(
          request, String.format("skill%d", skillNumber), /* DefaultValue= */ "");
      if (!currentSkill.isEmpty())
        skills.add(currentSkill);
      skillNumber++;
    } while (!currentSkill.isEmpty());

    insertVolunteeringOpportunityIntoDB(volunteerName, volunteerNumber, skills);

    response.sendRedirect("/events-feed.html");
  }

  private void insertVolunteeringOpportunityIntoDB(
      String volunteerName, Integer volunteerNumber, Set<String> requiredSkills) {
    DatabaseWrapper dbWrapper = new DatabaseWrapper("step-188-instance", "event-organizer-db");
    VolunteeringOpportunity opportunity =
        new VolunteeringOpportunity
            .Builder("0883de79-17d7-49a3-a866-dbd5135062a8", volunteerName, volunteerNumber)
            .setRequiredSkills(requiredSkills)
            .build();
    // TO DO: change eventId to non-hardcoded value
    dbWrapper.insertVolunteeringOpportunity(opportunity);
  }
}
package com.google.sps.servlets;

import com.google.sps.data.VolunteeringOpportunity;
import com.google.sps.utilities.CommonUtils;
import com.google.sps.utilities.DatabaseWrapper;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Set;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.Map;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.sps.utilities.DatabaseWrapper;

/**
 * When the user submits the form, Blobstore processes the file upload and then forwards the request
 * to this servlet. This servlet can then process the request using the file URL we get from
 * Blobstore.
 */
@WebServlet("/my-form-handler")
public class FormHandlerServlet extends HttpServlet {
  private static final String VOLUNTEER_TYPE = "volunteer-type";
  private static final String VOLUNTEER_NUMBER = "volunteer-number";
  private static final String SKILL = "skill";

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    // Get input from the form for comment text.
    String volunteerName = CommonUtils.getParameter(request, VOLUNTEER_TYPE, /* DefaultValue= */ "");

    // Get text with the sentiment tag added.
    Integer volunteerNumber = Integer.parseInt(CommonUtils.getParameter(request, VOLUNTEER_NUMBER, /* DefaultValue= */ "0"));

    Set<String> skills = new HashSet<String>();

    String currentSkill = "";
    int skillNumber = 0;
    do {
        currentSkill = CommonUtils.getParameter(request, String.format("skill%d", skillNumber), /* DefaultValue= */ "");
        if (!currentSkill.isEmpty())
            skills.add(currentSkill);
        else
           break;
        skillNumber++;
    } while (!currentSkill.isEmpty());

    insertVolunteeringOpportunityIntoDB(volunteerName, volunteerNumber, skills);

    // Redirect to same HTML page.
    response.sendRedirect("/index.html");
  }

  private void insertVolunteeringOpportunityIntoDB(String volunteerName, Integer volunteerNumber, Set<String> requiredSkills) {
      DatabaseWrapper dbWrapper = new DatabaseWrapper("step-188-instance","event-organizer-db");
      VolunteeringOpportunity opportunity =
        new VolunteeringOpportunity.Builder(volunteerName, volunteerNumber).setRequiredSkills(requiredSkills).build();
    dbWrapper.insertVolunteeringOpportunity(opportunity);
  }
}
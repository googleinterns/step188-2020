package com.google.sps.servlets;

import com.google.appengine.api.users.UserServiceFactory;
import com.google.sps.data.VolunteeringOpportunity;
import com.google.sps.data.OpportunitySignup;
import com.google.sps.utilities.CommonUtils;
import com.google.sps.utilities.SpannerTasks;
import java.io.IOException;
import java.util.Set;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet that handles submission of the form for signing up for opportunities. */
@WebServlet("/opportunity-signup-form-handler")
public class OpportunitySignupFormHandler extends HttpServlet {
  private static final String OPPORTUNITY_ID = "opportunity-id";

  /**
   * Inserts opportunity signup with parameter value for name and current user email.
   *
   * @param request servlet request
   * @param response servlet response
   * @throws IOException if Input/Output error occurs
   */
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String opportunityId = CommonUtils.getParameter(request, OPPORTUNITY_ID, /* DefaultValue= */ "");
    String email = CommonUtils.getParameter(request, UserServiceFactory.getUserService().getCurrentUser().getEmail(), /* DefaultValue= */ "");

    OpportunitySignup signup =
        new OpportunitySignup.Builder(opportunityId, email).build();
    SpannerTasks.insertOpportunitySignup(signup);

    response.sendRedirect("/events-feed.html");
  }
}
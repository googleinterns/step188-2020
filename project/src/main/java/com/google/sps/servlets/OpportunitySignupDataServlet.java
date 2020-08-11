package com.google.sps.servlets;

import com.google.sps.data.OpportunitySignup;
import com.google.sps.utilities.CommonUtils;
import com.google.sps.utilities.DatabaseConstants;
import com.google.sps.utilities.SpannerTasks;
import java.io.IOException;
import java.util.Set;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet to get all signups for a volunteering opportunity. */
@WebServlet("/event-volunteering-data")
public class OpportunitySignupDataServlet extends HttpServlet {
  private static final String OPPORTUNITY_ID = "opportunity-id";

  /**
   * Queries database for all opportunity signups that are for a specific volunteering opportunity.
   *
   * @param request servlet request
   * @param response servlet response
   * @throws IOException if Input/Output error occurs
   */
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String opportunityId = request.getParameter(OPPORTUNITY_ID);

    Set<OpportunitySignup> opportunities = SpannerTasks.getSignupsByOpportunityId(opportunityId);
    
    response.setContentType("application/json;");
    response.getWriter().println(CommonUtils.convertToJson(opportunities));
  }
}

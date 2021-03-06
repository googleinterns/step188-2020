package com.google.sps.servlets;

import com.google.sps.data.VolunteeringOpportunity;
import com.google.sps.utilities.CommonUtils;
import com.google.sps.utilities.SpannerTasks;
import java.io.IOException;
import java.util.Optional;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet to get volunteering opportunity given opportunityId */
@WebServlet("/volunteering-opportunity-data")
public class VolunteeringOpportunityDataServlet extends HttpServlet {
  private static final String OPPORTUNITY_ID = "opportunity-id";

  /**
   * Queries database for opportunity with opportunity ID given in the request parameter and writes
   * opportunity to the response.
   *
   * @param request servlet request
   * @param response servlet response
   * @throws IOException if Input/Output error occurs
   */
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String opportunityId = request.getParameter(OPPORTUNITY_ID);
    if (opportunityId == null) {
      response.sendError(HttpServletResponse.SC_BAD_REQUEST, "No opportunity id specified.");
      return;
    }

    Optional<VolunteeringOpportunity> opportunity =
        SpannerTasks.getVolunteeringOpportunityByOppportunityId(opportunityId);
    if (!opportunity.isPresent()) {
      response
        .sendError(HttpServletResponse.SC_BAD_REQUEST,
        String.format("Error: No opportunity found for opportunityId %s", opportunityId));
      return;
    }
    response.setContentType("application/json;");
    response.getWriter().println(CommonUtils.convertToJson(opportunity.get()));
  }
}

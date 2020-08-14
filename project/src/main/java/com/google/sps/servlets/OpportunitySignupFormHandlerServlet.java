package com.google.sps.servlets;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.sps.data.OpportunitySignup;
import com.google.sps.utilities.SpannerTasks;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
 
/** Servlet that handles submission of the form for signing up for opportunities. */
@WebServlet("/opportunity-signup-form-handler")
public class OpportunitySignupFormHandlerServlet extends HttpServlet {
  private static final String OPPORTUNITY_ID = "opportunity-id";
  private static final String EVENT_ID = "event-id";

  /**
   * Inserts opportunity signup with parameter value for name and current user email.
   *
   * @param request servlet request
   * @param response servlet response
   * @throws IOException if Input/Output error occurs
   */
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String opportunityId = request.getParameter(OPPORTUNITY_ID);
    String eventId = request.getParameter(EVENT_ID);
    User user = UserServiceFactory.getUserService().getCurrentUser();
    
    if (eventId == null) {
      response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Event ID not specified.");
    } else if (opportunityId == null) {
      response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Opportunity ID not specified.");
    } else if (user == null) {
      response.sendRedirect("/index.html");
    } else {
      OpportunitySignup signup =
          new OpportunitySignup.Builder(opportunityId, user.getEmail()).build();
      SpannerTasks.insertOpportunitySignup(signup);

      response.sendRedirect(String.format("/event-details.html?eventId=%s", eventId));
    }   
  }
}

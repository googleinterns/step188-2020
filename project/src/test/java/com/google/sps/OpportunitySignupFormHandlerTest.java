package com.google.sps;

import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.appengine.tools.development.testing.LocalUserServiceTestConfig;
import com.google.sps.data.VolunteeringOpportunity;
import com.google.sps.servlets.OpportunitySignupFormHandlerServlet;
import com.google.sps.utilities.SpannerClient;
import com.google.sps.utilities.SpannerTasks;
import com.google.sps.utilities.SpannerTestTasks;
import com.google.sps.utilities.TestUtils;
import java.io.IOException;
import javax.servlet.ServletContextEvent;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mockito;
import org.springframework.mock.web.MockServletContext;


/** Test that tests the opportunity signup form functionality. */
@RunWith(JUnit4.class)
public final class OpportunitySignupFormHandlerTest {
  private static final String PARAMETER_OPPORTUNITY_ID = "opportunity-id";
  private static final LocalServiceTestHelper authenticationHelper =
      new LocalServiceTestHelper(new LocalUserServiceTestConfig());
  private OpportunitySignupFormHandlerServlet opportunitySignupServlet;
  private HttpServletRequest request;
  private HttpServletResponse response;
  private VolunteeringOpportunity opportunity;

  @Before
  public void setUp() throws Exception {
    // Mock a request to trigger the SpannerClient setup to run.
    MockServletContext mockServletContext = new MockServletContext();
    new SpannerClient().contextInitialized(new ServletContextEvent(mockServletContext));
    SpannerTestTasks.setup();

    authenticationHelper.setUp();

    opportunitySignupServlet = new OpportunitySignupFormHandlerServlet();
    request = Mockito.mock(HttpServletRequest.class);
    response = Mockito.mock(HttpServletResponse.class);
  }

  @After
  public void tearDown() {
    SpannerTestTasks.cleanup();
    authenticationHelper.tearDown();
  }

  @Test
  public void testAddOpportunitySignup_LoggedIn() throws IOException {
    VolunteeringOpportunity opportunity = TestUtils.newVolunteeringOpportunity();
    SpannerTasks.insertVolunteeringOpportunity(opportunity);
    authenticationHelper
        .setEnvIsLoggedIn(true)
        .setEnvEmail("test@gmail.com")
        .setEnvAuthDomain("gmail.com");
    Mockito.when(request.getParameter(PARAMETER_OPPORTUNITY_ID))
        .thenReturn(opportunity.getOpportunityId());

    opportunitySignupServlet.doPost(request, response);

    Mockito.verify(response).sendRedirect("/event-details.html");
  }

  @Test
  public void testAddOpportunitySignup_LoggedInOpportunityIdNotSpecified() throws IOException {
    VolunteeringOpportunity opportunity = TestUtils.newVolunteeringOpportunity();
    SpannerTasks.insertVolunteeringOpportunity(opportunity);
    authenticationHelper
        .setEnvIsLoggedIn(true)
        .setEnvEmail("test@gmail.com")
        .setEnvAuthDomain("gmail.com");
    Mockito.when(request.getParameter(PARAMETER_OPPORTUNITY_ID)).thenReturn(null);

    opportunitySignupServlet.doPost(request, response);

    Mockito.verify(response)
        .sendError(HttpServletResponse.SC_BAD_REQUEST, "Opportunity ID not specified.");
  }

  @Test
  public void testAddOpportunitySignup_NotLoggedIn() throws IOException {
    VolunteeringOpportunity opportunity = TestUtils.newVolunteeringOpportunity();
    SpannerTasks.insertVolunteeringOpportunity(opportunity);
    authenticationHelper.setEnvIsLoggedIn(false);
    Mockito.when(request.getParameter(PARAMETER_OPPORTUNITY_ID))
        .thenReturn(opportunity.getOpportunityId());

    opportunitySignupServlet.doPost(request, response);

    Mockito.verify(response).sendRedirect("/index.html");
  }
}

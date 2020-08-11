package com.google.sps;

import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.appengine.tools.development.testing.LocalUserServiceTestConfig;
import com.google.sps.data.VolunteeringOpportunity;
import com.google.sps.servlets.OpportunitySignupFormHandlerServlet;
import com.google.sps.utilities.SpannerClient;
import com.google.sps.utilities.SpannerTasks;
import com.google.sps.utilities.SpannerTestTasks;
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
  private static final String EVENT_ID = "0883de79-17d7-49a3-a866-dbd5135062a8";
  private static final String NAME = "Performer";
  private static final int NUMBER_OF_SPOTS = 240;
  private static final String OPPORTUNITY_ID = "opportunity-id";
  private static final String EMAIL = "email";
  private static final LocalServiceTestHelper helper =
      new LocalServiceTestHelper(new LocalUserServiceTestConfig());

  @Before
  public void setUp() throws Exception {
    // Mock a request to trigger the SpannerClient setup to run
    MockServletContext mockServletContext = new MockServletContext();
    new SpannerClient().contextInitialized(new ServletContextEvent(mockServletContext));
    SpannerTestTasks.setup();
    helper.setUp();
  }

  @After
  public void tearDown() {
    SpannerTestTasks.cleanup();
    helper.tearDown();
  }

  @Test
  public void testAddOpportunitySignup_LoggedIn() throws IOException {
    VolunteeringOpportunity opportunity =
        new VolunteeringOpportunity.Builder(EVENT_ID, NAME, NUMBER_OF_SPOTS).build();
    SpannerTasks.insertVolunteeringOpportunity(opportunity);
    helper.setEnvIsLoggedIn(true);
    String emailParameter = "test@gmail.com";
    helper.setEnvEmail(emailParameter).setEnvAuthDomain("gmail.com");
    HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
    HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
    Mockito.when(request.getParameter(OPPORTUNITY_ID)).thenReturn(opportunity.getOpportunityId());
    Mockito.when(request.getParameter(EMAIL)).thenReturn(emailParameter);

    new OpportunitySignupFormHandlerServlet().doPost(request, response);

    Mockito.verify(response).sendRedirect("/event-details.html");
  }

  @Test
  public void testAddOpportunitySignup_OpportunityIdNotSpecified() throws IOException {
    HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
    HttpServletResponse response = Mockito.mock(HttpServletResponse.class);

    new OpportunitySignupFormHandlerServlet().doPost(request, response);

    Mockito.verify(response)
        .sendError(HttpServletResponse.SC_UNAUTHORIZED, "Opportunity ID not specified.");
  }

  @Test
  public void testAddOpportunitySignup_NotLoggedIn() throws IOException {
    VolunteeringOpportunity opportunity =
        new VolunteeringOpportunity.Builder(EVENT_ID, NAME, NUMBER_OF_SPOTS).build();
    SpannerTasks.insertVolunteeringOpportunity(opportunity);
    helper.setEnvIsLoggedIn(false);
    HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
    HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
    Mockito.when(request.getParameter(OPPORTUNITY_ID)).thenReturn(opportunity.getOpportunityId());
    
    new OpportunitySignupFormHandlerServlet().doPost(request, response);

    Mockito.verify(response)
        .sendError(HttpServletResponse.SC_UNAUTHORIZED, "You have not logged in.");
  }
}

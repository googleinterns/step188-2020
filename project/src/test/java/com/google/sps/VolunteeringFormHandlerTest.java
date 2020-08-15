package com.google.sps;

import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.appengine.tools.development.testing.LocalUserServiceTestConfig;
import com.google.sps.data.VolunteeringOpportunity;
import com.google.sps.servlets.VolunteeringFormHandlerServlet;
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


/** Test that tests the volunteering opportunity form functionality. */
@RunWith(JUnit4.class)
public final class VolunteeringFormHandlerTest {
  private static final String PARAMETER_OPPORTUNITY_ID = "opportunity-id";
  private static final String PARAMETER_EVENT_ID = "event-id";
  private static final String PARAMETER_NAME = "name";
  private static final String NAME = "Performer";
  private VolunteeringFormHandlerServlet eventVolunteeringFormHandlerServlet;
  private HttpServletRequest request;
  private HttpServletResponse response;

  @Before
  public void setUp() throws Exception {
    // Mock a request to trigger the SpannerClient setup to run.
    MockServletContext mockServletContext = new MockServletContext();
    new SpannerClient().contextInitialized(new ServletContextEvent(mockServletContext));
    SpannerTestTasks.setup();

    eventVolunteeringFormHandlerServlet = new VolunteeringFormHandlerServlet();

    request = Mockito.mock(HttpServletRequest.class);
    response = Mockito.mock(HttpServletResponse.class);
  }

  @After
  public void tearDown() {
    SpannerTestTasks.cleanup();
  }

  @Test
  public void testAddOpportunity() throws IOException {
    String eventId = TestUtils.newRandomId();
    Mockito.when(request.getParameter(PARAMETER_NAME)).thenReturn(NAME);
    Mockito.when(request.getParameter(PARAMETER_EVENT_ID)).thenReturn(eventId);

    eventVolunteeringFormHandlerServlet.doPost(request, response);

    Mockito.verify(response).sendRedirect(String.format("/event-details.html?eventId=%s", eventId));
  }

  @Test
  public void testAddOpportunitySignup_NameNotSpecified_sendErrorResponse() throws IOException {
    Mockito.when(request.getParameter(PARAMETER_NAME)).thenReturn(null);
    Mockito.when(request.getParameter(PARAMETER_EVENT_ID)).thenReturn(TestUtils.newRandomId());

    eventVolunteeringFormHandlerServlet.doPost(request, response);

    Mockito.verify(response)
        .sendError(HttpServletResponse.SC_BAD_REQUEST, "Name not specified.");
  }

  @Test
  public void testAddOpportunitySignup_EventNotSpecified_sendErrorResponse() throws IOException {
    Mockito.when(request.getParameter(PARAMETER_NAME)).thenReturn(NAME);
    Mockito.when(request.getParameter(PARAMETER_EVENT_ID)).thenReturn(null);

    eventVolunteeringFormHandlerServlet.doPost(request, response);

    Mockito.verify(response)
        .sendError(HttpServletResponse.SC_BAD_REQUEST, "Event ID not specified.");
  }
}
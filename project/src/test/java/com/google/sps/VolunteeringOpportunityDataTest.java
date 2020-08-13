package com.google.sps;
 
import com.google.sps.data.VolunteeringOpportunity;
import com.google.sps.servlets.VolunteeringOpportunityDataServlet;
import com.google.sps.utilities.CommonUtils;
import com.google.sps.utilities.SpannerClient;
import com.google.sps.utilities.SpannerTasks;
import com.google.sps.utilities.SpannerTestTasks;
import com.google.sps.utilities.TestUtils;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import javax.servlet.ServletContextEvent;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mockito;
import org.springframework.mock.web.MockServletContext;

/** Test that tests getting the signups for a volunteering opportunity. */
@RunWith(JUnit4.class)
public final class VolunteeringOpportunityDataTest {
  private static final String PARAMETER_OPPORTUNITY_ID = "opportunity-id";
  private HttpServletRequest request;
  private HttpServletResponse response;
  private StringWriter stringWriter;
  private PrintWriter printWriter;
  private VolunteeringOpportunityDataServlet opportunityDataServlet;

  @Before
  public void setUp() throws Exception {
    // Mock a request to trigger the SpannerClient setup to run
    MockServletContext mockServletContext = new MockServletContext();
    new SpannerClient().contextInitialized(new ServletContextEvent(mockServletContext));
    SpannerTestTasks.setup();
 
    request = Mockito.mock(HttpServletRequest.class);
    response = Mockito.mock(HttpServletResponse.class);
    stringWriter = new StringWriter();
    printWriter = new PrintWriter(stringWriter);
    Mockito.when(response.getWriter()).thenReturn(printWriter);

    opportunityDataServlet = new VolunteeringOpportunityDataServlet();
  }

  @After
  public void tearDown() {
    SpannerTestTasks.cleanup();
  }

  @Test
  public void getOpportunity_opportunityIdInDatabase() throws IOException {
    VolunteeringOpportunity opportunity = TestUtils.newVolunteeringOpportunity();
    SpannerTasks.insertVolunteeringOpportunity(opportunity);
    Mockito.when(request.getParameter(PARAMETER_OPPORTUNITY_ID))
        .thenReturn(opportunity.getOpportunityId());
 
    opportunityDataServlet.doGet(request, response);
 
    Assert.assertEquals(
        CommonUtils.convertToJson(opportunity), stringWriter.toString().trim());
  }

  @Test
  public void getOpportunity_opportunityIdNotInDatabase_sendErrorResponse() throws IOException {
    String opportunityId = TestUtils.newRandomId();
    Mockito.when(request.getParameter(PARAMETER_OPPORTUNITY_ID)).thenReturn(opportunityId);
 
    opportunityDataServlet.doGet(request, response);
 
    Mockito.verify(response)
        .sendError(
            HttpServletResponse.SC_BAD_REQUEST,
            String.format("Error: No opportunity found for opportunityId %s", opportunityId));
  }

  @Test
  public void getOpportunity_opportunityNotSpecified_sendErrorResponse() throws IOException {
    Mockito.when(request.getParameter(PARAMETER_OPPORTUNITY_ID)).thenReturn(null);
 
    opportunityDataServlet.doGet(request, response);
 
    Mockito.verify(response)
        .sendError(
            HttpServletResponse.SC_BAD_REQUEST, "No opportunity id specified.");
  }
}

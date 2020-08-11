package com.google.sps;

import com.google.sps.data.OpportunitySignup;
import com.google.sps.data.VolunteeringOpportunity;
import com.google.sps.servlets.OpportunitySignupDataServlet;
import com.google.sps.utilities.CommonUtils;
import com.google.sps.utilities.SpannerClient;
import com.google.sps.utilities.SpannerTasks;
import com.google.sps.utilities.SpannerTestTasks;
import com.google.sps.utilities.TestUtils;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.HashSet;
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
public final class OpportunitySignupDataTest {
  private static final String PARAMETER_OPPORTUNITY_ID = "opportunity-id";
  private VolunteeringOpportunity opportunity;
  private HttpServletRequest request;
  private HttpServletResponse response;
  private StringWriter stringWriter;
  private PrintWriter printWriter;
  private OpportunitySignupDataServlet signupDataServlet;

  @Before
  public void setUp() throws Exception {
    // Mock a request to trigger the SpannerClient setup to run
    MockServletContext mockServletContext = new MockServletContext();
    new SpannerClient().contextInitialized(new ServletContextEvent(mockServletContext));
    SpannerTestTasks.setup();

    opportunity = TestUtils.newVolunteeringOpportunity();
    SpannerTasks.insertVolunteeringOpportunity(opportunity);

    request = Mockito.mock(HttpServletRequest.class);
    response = Mockito.mock(HttpServletResponse.class);
    stringWriter = new StringWriter();
    printWriter = new PrintWriter(stringWriter);
    Mockito.when(response.getWriter()).thenReturn(printWriter);

    signupDataServlet = new OpportunitySignupDataServlet();
  }

  @After
  public void tearDown() {
    SpannerTestTasks.cleanup();
  }

  @Test
  public void testGetSignups_NoSignups() throws IOException {
    Mockito
        .when(request.getParameter(PARAMETER_OPPORTUNITY_ID))
        .thenReturn(opportunity.getOpportunityId());

    signupDataServlet.doGet(request, response);

    Assert.assertEquals(
        CommonUtils.convertToJson(new HashSet()), stringWriter.toString().trim());
  }

  @Test
  public void testGetSignups_NonzeroSignups() throws IOException {
    String opportunityId = opportunity.getOpportunityId();
    OpportunitySignup signup = TestUtils.newOpportunitySignup(opportunityId);
    SpannerTasks.insertOpportunitySignup(signup);
    Mockito.when(request.getParameter(PARAMETER_OPPORTUNITY_ID)).thenReturn(opportunityId);

    signupDataServlet.doGet(request, response);

    Assert.assertEquals(
        CommonUtils.convertToJson(new HashSet<>(Arrays.asList(signup))).trim(),
        stringWriter.toString().trim());
  }
}

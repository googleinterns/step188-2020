package com.google.sps;

import com.google.sps.data.OpportunitySignup;
import com.google.sps.data.VolunteeringOpportunity;
import com.google.sps.servlets.OpportunitySignupDataServlet;
import com.google.sps.utilities.CommonUtils;
import com.google.sps.utilities.SpannerClient;
import com.google.sps.utilities.SpannerTasks;
import com.google.sps.utilities.SpannerTestTasks;
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
  private static final String NAME = "Performer";
  private static final int NUMBER_OF_SPOTS = 240;
  private static final String OPPORTUNITY_ID = "opportunity-id";
  private static final String EMAIL_PARAMETER = "test@gmail.com";
  private static final String EVENT_ID = "0883de79-17d7-49a3-a866-dbd5135062a8";
  private static final String VOLUNTEER_EMAIL = "test2@gmail.com";

  @Before
  public void setUp() throws Exception {
    // Mock a request to trigger the SpannerClient setup to run
    MockServletContext mockServletContext = new MockServletContext();
    new SpannerClient().contextInitialized(new ServletContextEvent(mockServletContext));
    SpannerTestTasks.setup();
  }

  @After
  public void tearDown() {
    SpannerTestTasks.cleanup();
  }

  @Test
  public void testGetSignups_NoSignups() throws IOException {
    VolunteeringOpportunity opportunity =
        new VolunteeringOpportunity.Builder(EVENT_ID, NAME, NUMBER_OF_SPOTS).build();
    SpannerTasks.insertVolunteeringOpportunity(opportunity);

    HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
    HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
    StringWriter stringWriter = new StringWriter();
    PrintWriter writer = new PrintWriter(stringWriter);
    Mockito.when(request.getParameter(OPPORTUNITY_ID)).thenReturn(opportunity.getOpportunityId());
    Mockito.when(response.getWriter()).thenReturn(writer);
    new OpportunitySignupDataServlet().doGet(request, response);

    Assert.assertEquals(
        CommonUtils.convertToJson(new HashSet()).trim(), stringWriter.toString().trim());
  }

  @Test
  public void testGetSignups_NonzeroSignups() throws IOException {
    VolunteeringOpportunity opportunity =
        new VolunteeringOpportunity.Builder(EVENT_ID, NAME, NUMBER_OF_SPOTS).build();
    SpannerTasks.insertVolunteeringOpportunity(opportunity);
    OpportunitySignup signup =
        new OpportunitySignup.Builder(opportunity.getOpportunityId(), VOLUNTEER_EMAIL).build();
    SpannerTasks.insertOpportunitySignup(signup);

    HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
    HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
    StringWriter stringWriter = new StringWriter();
    PrintWriter writer = new PrintWriter(stringWriter);
    Mockito.when(request.getParameter(OPPORTUNITY_ID)).thenReturn(opportunity.getOpportunityId());
    Mockito.when(response.getWriter()).thenReturn(writer);
    new OpportunitySignupDataServlet().doGet(request, response);

    Assert.assertEquals(
        CommonUtils.convertToJson(new HashSet<>(Arrays.asList(signup))).trim(),
        stringWriter.toString().trim());
  }
}

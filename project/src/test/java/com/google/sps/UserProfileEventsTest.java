package com.google.sps;

import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.appengine.tools.development.testing.LocalUserServiceTestConfig;
import com.google.sps.data.Event;
import com.google.sps.data.EventVolunteering;
import com.google.sps.data.OpportunitySignup;
import com.google.sps.data.User;
import com.google.sps.data.VolunteeringOpportunity;
import com.google.sps.servlets.UserProfileEventsServlet;
import com.google.sps.utilities.CommonUtils;
import com.google.sps.utilities.SpannerClient;
import com.google.sps.utilities.SpannerTasks;
import com.google.sps.utilities.SpannerTestTasks;
import com.google.sps.utilities.TestUtils;
import java.io.IOException;
import com.google.sps.data.User;
import com.google.sps.data.Event;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.Set;
import java.util.HashSet;
import javax.servlet.ServletContextEvent;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mockito;
import org.springframework.mock.web.MockServletContext;

/** Unit tests for updates and retrievals for a user's corresponding events. */
@RunWith(JUnit4.class)
public class UserProfileEventsTest {
  private HttpServletRequest request;
  private HttpServletResponse response;
  private StringWriter stringWriter;
  private PrintWriter printWriter;
  private UserProfileEventsServlet userProfileEventsServlet;
  private static final LocalServiceTestHelper authenticationHelper =
    new LocalServiceTestHelper(new LocalUserServiceTestConfig());

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

    userProfileEventsServlet = new UserProfileEventsServlet();
  }

  @AfterClass
  public static void tearDown() throws Exception {
    SpannerTestTasks.cleanup();
  }

  @Test
  public void verifyGetUserEventsHosting() {
    
  }

  @Test
  public void verifyGetUserEventsParticipating() {
    
  }

  @Test
  public void verifyGetUserEventsVolunteering() throws IOException {
    User user = TestUtils.newUser();
    SpannerTasks.insertOrUpdateUser(user);
    Event event = TestUtils.newEvent();
    SpannerTasks.insertorUpdateEvent(event);
    VolunteeringOpportunity opportunity = TestUtils.newVolunteeringOpportunityWithEventId(event.getId());
    SpannerTasks.insertVolunteeringOpportunity(opportunity);
    OpportunitySignup opportunitySignup = new OpportunitySignup.Builder(opportunity.getOpportunityId(), user.getEmail()).build();
    SpannerTasks.insertOpportunitySignup(opportunitySignup);

    userProfileEventsServlet.doGet(request, response);

    Assert.assertEquals(
        CommonUtils.convertToJson(new HashSet<>(Arrays.asList(new EventVolunteering(event, opportunity.getName())))).trim(),
        stringWriter.toString().trim());
  }

  @Test
  public void verifyGetUserEventsInvalidParameter() {
    
  }

  @Test
  public void verifyPostUserEventsHosting() {
    
  }

  @Test
  public void verifyPostUserEventsParticipating() {
    
  }

  @Test
  public void verifyPostUserEventsInvalidParameter() {
    
  }
}

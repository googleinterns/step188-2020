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
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.HashSet;
import javax.servlet.ServletContextEvent;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mockito;
import org.springframework.mock.web.MockServletContext;
import org.skyscreamer.jsonassert.JSONAssert;
import org.json.JSONException;

/** Unit tests for updates and retrievals for a user's corresponding events. */
@RunWith(JUnit4.class)
public class UserProfileEventsTest {
  private HttpServletRequest request;
  private HttpServletResponse response;
  private StringWriter stringWriter;
  private PrintWriter printWriter;
  private UserProfileEventsServlet profileEventsServlet;
  private static final LocalServiceTestHelper authenticationHelper =
      new LocalServiceTestHelper(new LocalUserServiceTestConfig());
  private static final String PARAMETER_EVENT_TYPE = "event-type";
  private static final String VOLUNTEERING = "volunteering";
  private static final String PARTICIPATING = "participating";
  private static final String HOST_NAME = "Host Bob";
  private static final String HOST_EMAIL = "hostbobsmith@example.com";
  private static final String INVALID_EVENT_TYPE = "all";
  private static final String EMAIL = "test@example.com";
   private static final String AUTH_DOMAIN = "example.com";

  @Before
  public void setUp() throws Exception {
    MockServletContext mockServletContext = new MockServletContext();
    new SpannerClient().contextInitialized(new ServletContextEvent(mockServletContext));
    SpannerTestTasks.setup();

    authenticationHelper.setUp();

    profileEventsServlet = new UserProfileEventsServlet();
    
    request = Mockito.mock(HttpServletRequest.class);
    response = Mockito.mock(HttpServletResponse.class);
    stringWriter = new StringWriter();
    printWriter = new PrintWriter(stringWriter);
    Mockito.when(response.getWriter()).thenReturn(printWriter);
  }

  @After
  public void tearDown() {
    SpannerTestTasks.cleanup();
    authenticationHelper.tearDown();
  }

  @Test
  public void getUserEventsHosting() throws IOException {
    // TO DO: Add tests for events hosting.
  }

  @Test
  public void getUserEventsVolunteering_noVolunteerSignupsForUser() throws IOException {
    User user = TestUtils.newUserWithEmail(EMAIL);
    SpannerTasks.insertOrUpdateUser(user);

    User host = new User.Builder(HOST_NAME, EMAIL).build();
    SpannerTasks.insertOrUpdateUser(host);
    Event event = TestUtils.newEventWithHost(host);
    SpannerTasks.insertorUpdateEvent(event);

    VolunteeringOpportunity opportunity =
    TestUtils.newVolunteeringOpportunityWithEventId(event.getId());
    SpannerTasks.insertVolunteeringOpportunity(opportunity);

    authenticationHelper
        .setEnvIsLoggedIn(true)
        .setEnvEmail(EMAIL)
        .setEnvAuthDomain(AUTH_DOMAIN);
    Mockito.when(request.getParameter(PARAMETER_EVENT_TYPE)).thenReturn(VOLUNTEERING);

    profileEventsServlet.doGet(request, response);

    Assert.assertEquals(
        CommonUtils.convertToJson(new HashSet<>()).trim(),
        stringWriter.toString().trim());
  }

  @Test
  public void getUserEventsVolunteering_nonzeroVolunteerSignupsForUser() throws IOException {
    User user = TestUtils.newUserWithEmail(EMAIL);
    String userEmail = user.getEmail();
    SpannerTasks.insertOrUpdateUser(user);

    User host = new User.Builder(HOST_NAME, HOST_EMAIL).build();
    SpannerTasks.insertOrUpdateUser(host);
    Event event = TestUtils.newEventWithHost(host);
    SpannerTasks.insertorUpdateEvent(event);

    VolunteeringOpportunity opportunity =
        TestUtils.newVolunteeringOpportunityWithEventId(event.getId());
    SpannerTasks.insertVolunteeringOpportunity(opportunity);
    OpportunitySignup opportunitySignup =
        new OpportunitySignup.Builder(opportunity.getOpportunityId(), userEmail).build();
    SpannerTasks.insertOpportunitySignup(opportunitySignup);

    authenticationHelper
        .setEnvIsLoggedIn(true)
        .setEnvEmail(userEmail)
        .setEnvAuthDomain("gmail.com");
    Mockito.when(request.getParameter(PARAMETER_EVENT_TYPE)).thenReturn(VOLUNTEERING);

    profileEventsServlet.doGet(request, response);

    Assert.assertEquals(
        CommonUtils.convertToJson(
            new HashSet<>(Arrays.asList(new EventVolunteering(event, opportunity.getName()))))
        .trim(),
        stringWriter.toString().trim());
  }

  @Test
  public void verifyGetUserEvents_eventNotSpecified_sendErrorResponse() throws IOException {
    authenticationHelper
        .setEnvIsLoggedIn(true)
        .setEnvEmail(EMAIL)
        .setEnvAuthDomain(AUTH_DOMAIN);
    Mockito.when(request.getParameter(PARAMETER_EVENT_TYPE)).thenReturn(null);

    profileEventsServlet.doGet(request, response);

    Mockito.verify(response)
        .sendError(HttpServletResponse.SC_BAD_REQUEST, "No event type specified.");
  }

  @Test
  public void verifyGetUserEvents_invalidParameter_sendErrorResponse() throws IOException {
    authenticationHelper
        .setEnvIsLoggedIn(true)
        .setEnvEmail(EMAIL)
        .setEnvAuthDomain(AUTH_DOMAIN);
    Mockito.when(request.getParameter(PARAMETER_EVENT_TYPE)).thenReturn(INVALID_EVENT_TYPE);

    profileEventsServlet.doGet(request, response);

    Mockito.verify(response)
        .sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid event type.");
  }

  @Test
  public void verifyGetUserEvents_notLoggedIn_redirectToLogin() throws Exception {
    authenticationHelper.setEnvIsLoggedIn(false);
    Mockito.when(request.getParameter(PARAMETER_EVENT_TYPE)).thenReturn(VOLUNTEERING);

    profileEventsServlet.doGet(request, response);

    Mockito.verify(response).sendRedirect("/index.html");
  }

  /*Test to get all events logged in user is participating in */
  @Test
  public void getUserEventsParticipating() throws IOException {
    //Logged in user
    User user = TestUtils.newUserWithEmail(EMAIL);
    String userEmail = user.getEmail(); 
    SpannerTasks.insertOrUpdateUser(user);
    authenticationHelper
        .setEnvIsLoggedIn(true)
        .setEnvEmail(userEmail)
        .setEnvAuthDomain("example.com");
    //Random Attendee
    User ATTENDEE = new User.Builder("New attendee", "attendee@example.com").build();
    SpannerTasks.insertOrUpdateUser(ATTENDEE);   
    User host = new User.Builder(HOST_NAME, HOST_EMAIL).build();
    SpannerTasks.insertOrUpdateUser(host);

    Event event = TestUtils.newEventWithHost(host).toBuilder()
      .setAttendees(new HashSet<User>(Arrays.asList(ATTENDEE, user))).build();
    SpannerTasks.insertorUpdateEvent(event);
    Event event2 = TestUtils.newEventWithHost(host).toBuilder()
      .setAttendees(new HashSet<User>(Arrays.asList(user))).build();
    SpannerTasks.insertorUpdateEvent(event2);

    Mockito.when(request.getParameter(PARAMETER_EVENT_TYPE)).thenReturn(PARTICIPATING);
    profileEventsServlet.doGet(request, response);

    try {
    //Asserts unordered JSON, otherwise leads to flakey tests
    JSONAssert.assertEquals(CommonUtils.convertToJson(
        new HashSet<>(Arrays.asList(event, event2))).trim(),stringWriter.toString().trim(), false);
    } catch (JSONException e) {
        System.out.println("JSON conversion failed.");
    }
  }
}

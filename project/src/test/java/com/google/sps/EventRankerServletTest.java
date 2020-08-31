package com.google.sps;

import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.appengine.tools.development.testing.LocalUserServiceTestConfig;
import com.google.cloud.Date;
import com.google.sps.data.Event;
import com.google.sps.data.User;
import com.google.sps.data.VolunteeringOpportunity;
import com.google.sps.servlets.EventRankerServlet;
import com.google.sps.utilities.CommonUtils;
import com.google.sps.utilities.SpannerClient;
import com.google.sps.utilities.SpannerTasks;
import com.google.sps.utilities.SpannerTestTasks;
import com.google.sps.utilities.TestUtils;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.servlet.ServletContextEvent;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mockito;
import org.springframework.mock.web.MockServletContext;

/** Tests the ranking algorithm for event discovery */
@RunWith(JUnit4.class)
public final class EventRankerServletTest {
  private static final HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
  private static final HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
  private static final StringWriter stringWriter = new StringWriter();
  private static final PrintWriter writer = new PrintWriter(stringWriter);
  private static final LocalServiceTestHelper authenticationHelper =
      new LocalServiceTestHelper(new LocalUserServiceTestConfig());
  private static final String FOOD = "food";
  private static final String SEWING = "sewing";
  private static Event EVENT_FOOD;
  private static Event EVENT_SEWING;
  private static User USER;
  private static User USER_FOOD;
  private static String EMAIL = "test@example.com";
  private static String INVALID_EMAIL = "invalid@example.com";
  private static String EVENTS_KEY = "events";

  @BeforeClass
  public static void setUp() throws Exception {
    setUpDatabase();
    authenticationHelper.setUp();
    setUpEventsAndUsers();
    Mockito.when(response.getWriter()).thenReturn(writer);
  }

  @AfterClass
  public static void tearDown() throws Exception {
    SpannerTestTasks.cleanup();
    authenticationHelper.tearDown();
  }

  @After
  public void flushWriter() {
    Mockito.reset(request);
    stringWriter.getBuffer().setLength(0);
  }

  @Test
  public void testServletGetRanking() throws IOException {
    setAuthenticationHelper(EMAIL);
    SpannerTasks.insertOrUpdateUser(USER);
    SpannerTasks.insertOrUpdateUser(USER_FOOD);
    Set<Event> events = new HashSet<>(Arrays.asList(EVENT_FOOD, EVENT_SEWING));
    String eventIds = String.format("[%s,%s]", EVENT_FOOD.getId(), EVENT_SEWING.getId());
    Mockito.when(request.getParameter(EVENTS_KEY)).thenReturn(eventIds);
    for (Event event : events) {
      SpannerTasks.insertorUpdateEvent(event);
    }
    List<Event> expectedEvents = Arrays.asList(EVENT_FOOD, EVENT_SEWING);

    new EventRankerServlet().doGet(request, response);

    Assert.assertEquals(
        CommonUtils.convertToJson(expectedEvents).trim(), stringWriter.toString().trim());
  }

  @Test
  public void testServletGetRankingError() throws IOException {
    setAuthenticationHelper(INVALID_EMAIL);

    new EventRankerServlet().doGet(request, response);

    Mockito.verify(response).sendError(HttpServletResponse.SC_BAD_REQUEST, "User not found.");
  }

  private static void setUpDatabase() throws Exception {
    // Mock a request to trigger the SpannerClient setup to run
    MockServletContext mockServletContext = new MockServletContext();
    new SpannerClient().contextInitialized(new ServletContextEvent(mockServletContext));
    SpannerTestTasks.setup();
  }

  private static void setUpEventsAndUsers() {
    USER = TestUtils.newUser();
    USER_FOOD = 
        TestUtils.newUserWithEmail(EMAIL).toBuilder()
            .setInterests(new HashSet<>(Arrays.asList(FOOD)))
            .build();
    EVENT_FOOD =
        TestUtils.newEvent().toBuilder()
            .setHost(USER)
            .setLabels(new HashSet<>(Arrays.asList(FOOD)))
            .build();
    EVENT_SEWING =
        TestUtils.newEvent().toBuilder()
            .setHost(USER)
            .setLabels(new HashSet<>(Arrays.asList(SEWING)))
            .build();
  }

  private static void setAuthenticationHelper(String email) {
    authenticationHelper
        .setEnvIsLoggedIn(true)
        .setEnvEmail(email)
        .setEnvAuthDomain(getDomain(email));
  }

  private static String getDomain(String email) {
    return email.split("@")[1];
  }
}

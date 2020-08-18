package com.google.sps;

import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.appengine.tools.development.testing.LocalUserServiceTestConfig;
import com.google.cloud.Date;
import com.google.sps.data.Event;
import com.google.sps.data.User;
import com.google.sps.data.VolunteeringOpportunity;
import com.google.sps.servlets.EventRankerServlet;
import com.google.sps.utilities.CommonUtils;
import com.google.sps.utilities.EventRanker;
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
import javax.servlet.http.HttpServlet;
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
public final class EventRankerTest {
  private static final HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
  private static final HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
  private static final StringWriter stringWriter = new StringWriter();
  private static final PrintWriter writer = new PrintWriter(stringWriter);
  private static final LocalServiceTestHelper authenticationHelper =
      new LocalServiceTestHelper(new LocalUserServiceTestConfig());
  private static final String CONSERVATION = "conservation";
  private static final String FOOD = "food";
  private static final String MUSIC = "music";
  private static final String SEWING = "sewing";
  private static final Set<String> INTERESTS_CONSERVATION_FOOD =
      new HashSet<>(Arrays.asList(CONSERVATION, FOOD));
  private static final Set<String> SKILLS_MUSIC = new HashSet<>(Arrays.asList(MUSIC));
  private static Event EVENT_CONSERVATION_FOOD_MUSIC;
  private static Event EVENT_FOOD_MUSIC;
  private static Event EVENT_CONSERVATION_MUSIC;
  private static Event EVENT_FOOD;
  private static Event EVENT_SEWING;
  private static User USER_CONSERVATION_FOOD_MUSIC;
  private static VolunteeringOpportunity OPPORTUNITY_MUSIC;
  private static String NAME = "Bob Smith";
  private static String EMAIL = "test@example.com";
  private static String DOMAIN;

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
  public void testRankingEmptyEvents() throws IOException {
    Assert.assertEquals(
        new ArrayList<Event>(),
        EventRanker.rankEvents(USER_CONSERVATION_FOOD_MUSIC, new HashSet<Event>()));
  }

  @Test
  public void testRankingUntiedEvents() throws IOException {
    Set<Event> eventsToRank =
        new HashSet<>(
            Arrays.asList(
                EVENT_FOOD, EVENT_SEWING, EVENT_CONSERVATION_FOOD_MUSIC, EVENT_FOOD_MUSIC));
    List<Event> expectedEventRanking =
        Arrays.asList(EVENT_CONSERVATION_FOOD_MUSIC, EVENT_FOOD_MUSIC, EVENT_FOOD, EVENT_SEWING);

    List<Event> actualEventRanking =
        EventRanker.rankEvents(USER_CONSERVATION_FOOD_MUSIC, eventsToRank);

    Assert.assertEquals(expectedEventRanking, actualEventRanking);
  }

  @Test
  public void testRankingTiedEvents() throws IOException {
    Set<Event> eventsToRank =
        new HashSet<>(
            Arrays.asList(
                EVENT_CONSERVATION_MUSIC,
                EVENT_FOOD,
                EVENT_SEWING,
                EVENT_CONSERVATION_FOOD_MUSIC,
                EVENT_FOOD_MUSIC));
    List<Event> expectedEventRanking =
        Arrays.asList(
            EVENT_CONSERVATION_FOOD_MUSIC,
            EVENT_FOOD_MUSIC,
            EVENT_CONSERVATION_MUSIC,
            EVENT_FOOD,
            EVENT_SEWING);

    List<Event> actualEventRanking =
        EventRanker.rankEvents(USER_CONSERVATION_FOOD_MUSIC, eventsToRank);

    Assert.assertEquals(expectedEventRanking, actualEventRanking);
  }

  @Test
  public void testServletGetRanking() throws IOException {
    setAuthenticationHelper();
    SpannerTasks.insertOrUpdateUser(USER_CONSERVATION_FOOD_MUSIC);
    Set<Event> events =
        new HashSet<>(
            Arrays.asList(
                EVENT_FOOD,
                EVENT_SEWING));
    for (Event event : events) {
      SpannerTasks.insertorUpdateEvent(event);
    }
    List<Event> expectedEvents =
        EventRanker.rankEvents(USER_CONSERVATION_FOOD_MUSIC, events);

    new EventRankerServlet().doGet(request, response);

    Assert.assertEquals(
        CommonUtils.convertToJson(expectedEvents).trim(),
        stringWriter.toString().trim());
  }

  private static void setUpDatabase() throws Exception {
    // Mock a request to trigger the SpannerClient setup to run
    MockServletContext mockServletContext = new MockServletContext();
    new SpannerClient().contextInitialized(new ServletContextEvent(mockServletContext));
    SpannerTestTasks.setup();
  }

  private static void setUpEventsAndUsers() {
    int currentYear = new java.util.Date().getYear();
    USER_CONSERVATION_FOOD_MUSIC =
        new User.Builder(NAME, EMAIL)
            .setInterests(INTERESTS_CONSERVATION_FOOD)
            .setSkills(SKILLS_MUSIC)
            .build();
    EVENT_CONSERVATION_FOOD_MUSIC =
        TestUtils.newEvent().toBuilder()
            .setHost(USER_CONSERVATION_FOOD_MUSIC)
            .setLabels(new HashSet<>(Arrays.asList(CONSERVATION, FOOD, MUSIC)))
            .build();
    EVENT_FOOD_MUSIC =
        TestUtils.newEvent().toBuilder()
            .setHost(USER_CONSERVATION_FOOD_MUSIC)
            .setLabels(new HashSet<>(Arrays.asList(FOOD, MUSIC)))
            .setDate(Date.fromYearMonthDay(currentYear + 1, 1, 1))
            .build();
    EVENT_CONSERVATION_MUSIC =
        TestUtils.newEvent().toBuilder()
            .setHost(USER_CONSERVATION_FOOD_MUSIC)
            .setLabels(new HashSet<>(Arrays.asList(CONSERVATION, MUSIC)))
            .setDate(Date.fromYearMonthDay(currentYear + 2, 1, 1))
            .build();
    EVENT_FOOD =
        TestUtils.newEvent().toBuilder()
            .setHost(USER_CONSERVATION_FOOD_MUSIC)
            .setLabels(new HashSet<>(Arrays.asList(FOOD)))
            .build();
    EVENT_SEWING =
        TestUtils.newEvent().toBuilder()
            .setHost(USER_CONSERVATION_FOOD_MUSIC)
            .setLabels(new HashSet<>(Arrays.asList(SEWING)))
            .build();
    OPPORTUNITY_MUSIC =
        new VolunteeringOpportunity.Builder(EVENT_FOOD_MUSIC.getId(), "", 1).build();
    EVENT_FOOD_MUSIC = EVENT_FOOD_MUSIC.toBuilder().addOpportunity(OPPORTUNITY_MUSIC).build();
    DOMAIN = getDomain(EMAIL);
  }

  private static void setAuthenticationHelper() {
    authenticationHelper
        .setEnvIsLoggedIn(true)
        .setEnvEmail(EMAIL)
        .setEnvAuthDomain(DOMAIN);
  }

  private static String getDomain(String email) {
    return email.split("@")[1];
  }
}

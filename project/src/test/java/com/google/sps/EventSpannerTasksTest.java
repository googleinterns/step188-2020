package com.google.sps;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.appengine.tools.development.testing.LocalUserServiceTestConfig;
import com.google.cloud.Date;
import com.google.gson.Gson;
import com.google.sps.data.Event;
import com.google.sps.data.User;
import com.google.sps.data.VolunteeringOpportunity;
import com.google.sps.servlets.EventCreationServlet;
import com.google.sps.servlets.EventRegistrationServlet;
import com.google.sps.utilities.SpannerClient;
import com.google.sps.utilities.SpannerTasks;
import com.google.sps.utilities.SpannerTestTasks;
import com.google.sps.utilities.TestUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.servlet.ServletException;
import javax.servlet.ServletContextEvent;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.springframework.mock.web.MockServletContext;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import java.util.stream.Collectors;
import java.util.stream.Stream; 
import org.mockito.internal.matchers.apachecommons.ReflectionEquals;

/** Unit tests for DatabaseWrapper functionality related to Event class. */
@RunWith(JUnit4.class)
public class EventSpannerTasksTest {
  private static final String HOST_NAME = "Bob Smith";
  private static final String EMAIL = "bobsmith@example.com";
  private static final User HOST = new User.Builder(HOST_NAME, EMAIL).build();
  private static final String ATTENDEE_NAME = "New attendee";
  private static final String ATTENDEE_EMAIL = "attendee@example.com";
  private static final User ATTENDEE = new User.Builder(ATTENDEE_NAME, ATTENDEE_EMAIL).build();
  private static final String EVENT_NAME = "Team Meeting";
  private static final String NEW_EVENT_NAME = "Daily Team Meeting";
  private static final String DESCRIPTION = "Daily Team Sync";
  private static final Set<String> LABELS =
      Collections.unmodifiableSet(new HashSet<>(Arrays.asList("Tech", "Work")));
  private static final String LOCATION = "Remote";
  private static final Date DATE_PAST = Date.fromYearMonthDay(2016, 9, 15);
  private static final Date DATE_FUTURE =
      Date.fromYearMonthDay(Calendar.getInstance().get(Calendar.YEAR) + 1, 9, 15);
  private static final String DATE_FUTURE_STRING = DATE_FUTURE.getYear() + "-09-15";
  private static final String TIME = "3:00PM-5:00PM";
  private MockHttpServletRequest request;
  private MockHttpServletResponse response;
  private static final LocalServiceTestHelper authenticationHelper =
      new LocalServiceTestHelper(new LocalUserServiceTestConfig());

  @BeforeClass
  public static void setUp() throws Exception {
    // Mock a request to trigger the SpannerClient setup to run
    MockServletContext mockServletContext = new MockServletContext();
    new SpannerClient().contextInitialized(new ServletContextEvent(mockServletContext));
    SpannerTestTasks.setup();
  }

  @Before 
  public void testSetUp() throws Exception {
    // Host inserted because needed to query getEventById() in db
    SpannerTasks.insertOrUpdateUser(HOST);
    request = new MockHttpServletRequest();
    response = new MockHttpServletResponse();
    authenticationHelper.setUp();
  } 

  @AfterClass
  public static void tearDown() throws Exception {
    SpannerTestTasks.cleanup();
    authenticationHelper.tearDown();
  }

  /** Verify insertion of event in db and retrieval by id 
   * Also tests behavior of EventCreationServlet doGet() where doGet request.getParameter("eventId") == event.getId()
   */
  @Test
  public void eventInsertAndRetrieval() {
    Event event =
        new Event.Builder(EVENT_NAME, DESCRIPTION, LABELS, LOCATION, DATE_FUTURE, TIME, HOST).build();
    SpannerTasks.insertorUpdateEvent(event);
    Event dbEvent = SpannerTasks.getEventById(event.getId()).get();

    Assert.assertEquals(dbEvent.getName(), EVENT_NAME);
    Assert.assertEquals(dbEvent.getDescription(), DESCRIPTION);
    Assert.assertEquals(dbEvent.getLabels(), LABELS);
    Assert.assertEquals(dbEvent.getLocation(), LOCATION);
    Assert.assertEquals(dbEvent.getDate(), DATE_FUTURE);
    Assert.assertEquals(dbEvent.getTime(), TIME);
    Assert.assertEquals(dbEvent.getHost().getName(), HOST_NAME);
    Assert.assertEquals(dbEvent.getHost().getEmail(), EMAIL);
  }

  /** Verify getting Set<Event> from corresponding eventIds */
  @Test
  public void getEventsByIdTest() {
    Event event =
        new Event.Builder(EVENT_NAME, DESCRIPTION, LABELS, LOCATION, DATE_FUTURE, TIME, HOST).build();
    Event otherEvent =
        new Event.Builder(NEW_EVENT_NAME, DESCRIPTION, LABELS, LOCATION, DATE_FUTURE, TIME, HOST).build();
    SpannerTasks.insertorUpdateEvent(event);
    SpannerTasks.insertorUpdateEvent(otherEvent);
    Set<Event> actualEvents = new HashSet<>(Arrays.asList(event, otherEvent));
    Set<Event> insertedEvents =
        SpannerTasks.getEventsFromIds(Arrays.asList(event.getId(), otherEvent.getId()));
    List<Event> insertedEventsList =
        insertedEvents.stream().collect(Collectors.toCollection(ArrayList::new));
    List<Event> actualEventsList =
        actualEvents.stream().collect(Collectors.toCollection(ArrayList::new));

    Assert.assertTrue(new ReflectionEquals(insertedEventsList,/*excludeFields= */ null).matches(actualEventsList));
  }

  /**
   * Verify putting event in database through /create-event doPost with correct params and getting 
   * back correct event out of db
   */
  @Test
  public void testEventCreationDoPost() throws Exception {
    request.addParameter("name", EVENT_NAME);
    request.addParameter("date", DATE_FUTURE_STRING);
    request.addParameter("time", TIME);
    request.addParameter("description", DESCRIPTION);
    request.addParameter("location", LOCATION);
    request.addParameter("interests", "Tech");
    authenticationHelper
        .setEnvIsLoggedIn(true)
        .setEnvEmail(EMAIL)
        .setEnvAuthDomain("example.com");

    new EventCreationServlet().doPost(request, response);
    // Get back Event posted in db
    Event returnedEvent = new Gson().fromJson(response.getContentAsString(), Event.class);

    // Check that event inserted into doPost is same as event returned from doPost db
    Assert.assertEquals(returnedEvent.getName(), EVENT_NAME);
    Assert.assertEquals(returnedEvent.getDescription(), DESCRIPTION);
    Assert.assertEquals(returnedEvent.getLocation(), LOCATION);
    Assert.assertEquals(returnedEvent.getDate(), DATE_FUTURE);
    Assert.assertEquals(returnedEvent.getTime(), TIME);
    Assert.assertEquals(returnedEvent.getLabels(), new HashSet<>(Arrays.asList("Tech")));
    Assert.assertEquals(returnedEvent.getHost().getName(), HOST_NAME);
    Assert.assertEquals(returnedEvent.getHost().getEmail(), EMAIL);
  }

  /** Verify getting event from valid id in /create-event doGet() */
  @Test
  public void testEventCreationDoGet() throws Exception {
    Event event =
        new Event.Builder(EVENT_NAME, DESCRIPTION, LABELS, LOCATION, DATE_FUTURE, TIME, HOST).build();
    SpannerTasks.insertorUpdateEvent(event);

    request.addParameter("eventId", event.getId());
    new EventCreationServlet().doGet(request, response);
    Event returnedEvent = new Gson().fromJson(response.getContentAsString(), Event.class);

    Assert.assertEquals(returnedEvent.getName(), EVENT_NAME);
    Assert.assertEquals(returnedEvent.getDescription(), DESCRIPTION);
    Assert.assertEquals(returnedEvent.getLocation(), LOCATION);
    Assert.assertEquals(returnedEvent.getDate(), DATE_FUTURE);
    Assert.assertEquals(returnedEvent.getTime(), TIME);
    Assert.assertEquals(returnedEvent.getHost().getName(), HOST_NAME);
    Assert.assertEquals(returnedEvent.getHost().getEmail(), EMAIL);
  }

  /** Verify getting event from invalid id in /create-event doGet() */
  @Test
  public void testEventCreationDoGetInvalid() throws Exception {
    request.addParameter("eventId", /*invalid id= */ "1");
    new EventCreationServlet().doGet(request, response);

    Assert.assertEquals(response.getStatus(), HttpServletResponse.SC_NOT_FOUND);
  }

  /**
   * Verify registering for event that already has attendee
   */
  @Test
  public void testEventRegistrationDoGet() throws Exception {
    //Existing event to sign up for
    SpannerTasks.insertOrUpdateUser(ATTENDEE);
    Event event =
        new Event.Builder(EVENT_NAME, DESCRIPTION, LABELS, LOCATION, DATE_FUTURE, TIME, HOST)
            .setAttendees(new HashSet<User>(Arrays.asList(ATTENDEE)))
            .build();
    request.addParameter("eventId", event.getId());   
    SpannerTasks.insertorUpdateEvent(event);
    // User that is registering
    authenticationHelper.setEnvIsLoggedIn(true).setEnvEmail(EMAIL).setEnvAuthDomain("example.com");

    new EventRegistrationServlet().doPost(request, response);
    Event returnedEvent = new Gson().fromJson(response.getContentAsString(), Event.class);

    Assert.assertEquals(returnedEvent.getName(), EVENT_NAME);
    Assert.assertEquals(returnedEvent.getDescription(), DESCRIPTION);
    Assert.assertEquals(returnedEvent.getLocation(), LOCATION);
    Assert.assertEquals(returnedEvent.getDate(), DATE_FUTURE);
    Assert.assertEquals(returnedEvent.getTime(), TIME);
    Assert.assertTrue(
        new ReflectionEquals(returnedEvent.getAttendees(),/*excludeFields= */ null)
            .matches(new HashSet<User>(Arrays.asList(ATTENDEE, HOST))));
  }

  /**
   * Verify that events in the past are NOT retrieved
   */
  @Test
  public void testGetFutureEventsOnly() throws Exception {
    Event eventFuture = TestUtils.newEvent().toBuilder().setDate(DATE_FUTURE).build();
    Event eventPast = TestUtils.newEvent().toBuilder().setDate(DATE_PAST).build();
    SpannerTasks.insertorUpdateEvent(eventFuture);
    SpannerTasks.insertorUpdateEvent(eventPast);

    Set<Event> returnedEvents =
        SpannerTasks.getEventsFromIds(Arrays.asList(eventFuture.getId(), eventPast.getId()));
    
    // Only the ID is compared because this unit test is not meant to check that all params were
    // returned appropriately (previous unit tests do that)
    Assert.assertEquals(1, returnedEvents.size());
    Assert.assertEquals(eventFuture.getId(), returnedEvents.iterator().next().getId());
  }
}

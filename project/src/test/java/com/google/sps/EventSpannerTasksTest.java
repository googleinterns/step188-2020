package com.google.sps;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.appengine.tools.development.testing.LocalUserServiceTestConfig;
import com.google.cloud.Date;
import com.google.gson.Gson;
import com.google.sps.data.Event;
import com.google.sps.data.User;
import com.google.sps.data.VolunteeringOpportunity;
import com.google.sps.servlets.EventCreationServlet;
import com.google.sps.utilities.CommonUtils;
import com.google.sps.utilities.NlpProcessing;
import com.google.sps.utilities.SpannerClient;
import com.google.sps.utilities.SpannerTasks;
import com.google.sps.utilities.SpannerTestTasks;
import java.io.StringWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
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
import org.json.JSONException;
import org.mockito.Mockito;
import org.springframework.mock.web.MockServletContext;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.skyscreamer.jsonassert.JSONAssert;
import java.util.stream.Collectors;
import java.util.stream.Stream; 
import org.mockito.internal.matchers.apachecommons.ReflectionEquals;

/** Unit tests for DatabaseWrapper functionality related to Event class. */
@RunWith(JUnit4.class)
public class EventSpannerTasksTest {
  private static final String HOST_NAME = "Bob Smith";
  private static final String EMAIL = "bobsmith@example.com";
  private static final User HOST = new User.Builder(HOST_NAME, EMAIL).build();
  private static final String EVENT_NAME = "Team Meeting";
  private static final String NEW_EVENT_NAME = "Daily Team Meeting";
  private static final String DESCRIPTION = "Daily Team Sync";
  private static final Set<String> LABELS =
      Collections.unmodifiableSet(new HashSet<>(Arrays.asList("Tech", "Work")));
  private static final String LOCATION = "Remote";
  private static final Date DATE = Date.fromYearMonthDay(2016, 9, 15);
  private static final String DATE_STRING = "09/15/2016";
  private static final String TIME = "3:00PM-5:00PM";
  private HttpServletRequest request;
  private HttpServletResponse response;
  private StringWriter stringWriter;
  private  PrintWriter printWriter;
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
    request = Mockito.mock(HttpServletRequest.class);
    response = Mockito.mock(HttpServletResponse.class);
    stringWriter = new StringWriter();
    printWriter = new PrintWriter(stringWriter);
    authenticationHelper.setUp();
    Mockito.when(response.getWriter()).thenReturn(printWriter);
  } 

  @AfterClass
  public static void tearDown() throws Exception {
    SpannerTestTasks.cleanup();
    authenticationHelper.tearDown();
  }

  /** Verify insertion of event in db and retrieval by id 
   * Also tests behavior of EventCreationServlet doGet() where doGet request.getParameter("eventId") == event.getId() */
  @Test
  public void eventInsertAndRetrieval() {
    Event event =
        new Event.Builder(EVENT_NAME, DESCRIPTION, LABELS, LOCATION, DATE, TIME, HOST).build();
    SpannerTasks.insertOrUpdateUser(HOST);
    SpannerTasks.insertorUpdateEvent(event);
    Event dbEvent = SpannerTasks.getEventById(event.getId()).get();

    Assert.assertEquals(dbEvent.getName(), EVENT_NAME);
    Assert.assertEquals(dbEvent.getDescription(), DESCRIPTION);
    Assert.assertEquals(dbEvent.getLabels(), LABELS);
    Assert.assertEquals(dbEvent.getLocation(), LOCATION);
    Assert.assertEquals(dbEvent.getDate(), DATE);
    Assert.assertEquals(dbEvent.getTime(), TIME);
    Assert.assertEquals(dbEvent.getHost().getName(), HOST_NAME);
    Assert.assertEquals(dbEvent.getHost().getEmail(), EMAIL);
  }

  /** Verify getting Set<Event> from corresponding eventIds */
  @Test
  public void getEventsByIdTest() {
    Event event =
        new Event.Builder(EVENT_NAME, DESCRIPTION, LABELS, LOCATION, DATE, TIME, HOST).build();
    Event otherEvent =
        new Event.Builder(NEW_EVENT_NAME, DESCRIPTION, LABELS, LOCATION, DATE, TIME, HOST).build();
    SpannerTasks.insertOrUpdateUser(HOST);
    SpannerTasks.insertorUpdateEvent(event);
    SpannerTasks.insertorUpdateEvent(otherEvent);
    Set<Event> actualEvents = new HashSet<>(Arrays.asList(event, otherEvent));
    Set<Event> insertedEvents = SpannerTasks.getEventsFromIds(Arrays.asList(event.getId(), otherEvent.getId()));
    List<Event> insertedEventsList = insertedEvents.stream().collect(Collectors.toCollection(ArrayList::new));
    List<Event> actualEventsList = actualEvents.stream().collect(Collectors.toCollection(ArrayList::new));

    Assert.assertTrue(new ReflectionEquals(insertedEventsList,/*excludeFields= */ null).matches(actualEventsList));
  }

  /**
   * Verify putting event in database through /create-event doPost with correct params and
   * getting back correct event out of db without going through NLP API
   */
  @Test
  public void testEventCreationDoPost_noNlp() throws Exception {
    SpannerTasks.insertOrUpdateUser(HOST);
    setAuthenticationHelper();
    Mockito.when(request.getParameter("name")).thenReturn(EVENT_NAME);
    Mockito.when(request.getParameter("date")).thenReturn(DATE_STRING);
    Mockito.when(request.getParameter("time")).thenReturn(TIME);
    Mockito.when(request.getParameter("description")).thenReturn(DESCRIPTION);
    Mockito.when(request.getParameter("location")).thenReturn(LOCATION);
    Mockito.when(request.getParameter("interests")).thenReturn("Tech, Work");

    new EventCreationServlet().doPost(request, response);
    Event event =
        new Event.Builder(EVENT_NAME, DESCRIPTION, LABELS, LOCATION, DATE, TIME, HOST).setId(stringWriter.toString().trim().split("\"")[3]).build();

    try {
    JSONAssert.assertEquals(CommonUtils.convertToJson(event).trim(), stringWriter.toString().trim(), /*assert order= */ false);
    } catch (JSONException e) {
        System.out.println("JSON conversion failed.");
    }
  }

  /**
   * Verify putting event in database through /create-event doPost with correct params and
   * getting back correct event out of db with NLP API labeling suggestions
   */
  @Test
  public void testEventCreationDoPost_Nlp() throws Exception {
    SpannerTasks.insertOrUpdateUser(HOST);
    setAuthenticationHelper();
    Mockito.when(request.getParameter("name")).thenReturn(EVENT_NAME);
    Mockito.when(request.getParameter("date")).thenReturn(DATE_STRING);
    Mockito.when(request.getParameter("time")).thenReturn(TIME);
    String DESCRIPTION_COOKING_CLASS =
    "Come learn to meal prep with us. This class will include prepping foods beyond sandwiches or salads."
    + " We will learn to bake desserts as well. Bring a friend!";

    Mockito.when(request.getParameter("description")).thenReturn(DESCRIPTION_COOKING_CLASS);
    Mockito.when(request.getParameter("location")).thenReturn(LOCATION);
    Mockito.when(request.getParameter("interests")).thenReturn("Cooking");
    String text = new StringBuilder().append(EVENT_NAME).append(" ").append(DESCRIPTION_COOKING_CLASS).toString();

    //Mock NLP API response with real category response
    EventCreationServlet servlet = Mockito.spy(EventCreationServlet.class);
    Mockito.doReturn(new ArrayList<>(Arrays.asList("Jobs and Education", "Food and Drink"))).when(servlet).getNlpSuggestedFilters(text, new ArrayList<String>());
    servlet.doPost(request, response);

    Event event =
        new Event.Builder(EVENT_NAME, DESCRIPTION_COOKING_CLASS, LABELS, LOCATION, DATE, TIME, HOST)
          .setLabels(new HashSet<>(Arrays.asList("Cooking", "Jobs and Education", "Food and Drink")))
          .setId(stringWriter.toString().trim().split("\"")[3])
          .build();

    try {
      JSONAssert.assertEquals(CommonUtils.convertToJson(event).trim(), stringWriter.toString().trim(), /*assert order= */ false);
    } catch (JSONException e) {
      System.out.println("JSON conversion failed.");
    }
  }
  
  /**
   * Verify getting event from valid id in /create-event doGet()
   */
  @Test
  public void testEventCreationDoGet() throws Exception {
    Event event =
        new Event.Builder(EVENT_NAME, DESCRIPTION, LABELS, LOCATION, DATE, TIME, HOST).build();
    SpannerTasks.insertOrUpdateUser(HOST);
    SpannerTasks.insertorUpdateEvent(event);
    Mockito.when(request.getParameter("eventId")).thenReturn(event.getId());

    new EventCreationServlet().doGet(request, response);

    try {
      JSONAssert.assertEquals(CommonUtils.convertToJson(event).trim(), stringWriter.toString().trim(), /*assert order= */ false);
    } catch (JSONException e) {
      System.out.println("JSON conversion failed.");
    }
  }

  /**
   * Verify getting event from invalid id in /create-event doGet()
   */
  @Test
  public void testEventCreationDoGetInvalid() throws Exception {
    Mockito.when(request.getParameter("eventId")).thenReturn(/*invalid id= */ "1");

    new EventCreationServlet().doGet(request, response);

    Mockito.verify(response)
        .sendError(HttpServletResponse.SC_NOT_FOUND, "No events found with event ID 1");
  }

  private void setAuthenticationHelper() {
    authenticationHelper.setEnvIsLoggedIn(true).setEnvEmail(EMAIL).setEnvAuthDomain("example.com");
  }
}


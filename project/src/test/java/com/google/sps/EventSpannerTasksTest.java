package com.google.sps;
import com.google.cloud.Date;
import com.google.sps.data.Event;
import com.google.sps.data.User;
import com.google.sps.data.VolunteeringOpportunity;
import com.google.sps.utilities.SpannerClient;
import com.google.sps.utilities.SpannerTasks;
import com.google.sps.utilities.SpannerTestTasks;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream; 
import javax.servlet.ServletContextEvent;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.springframework.mock.web.MockServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.gson.Gson;

import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import com.google.sps.servlets.EventCreationServlet;
import org.json.simple.JSONObject;

import org.json.simple.parser.JSONParser;

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

  @BeforeClass
  public static void setUp() throws Exception {
    // Mock a request to trigger the SpannerClient setup to run
    MockServletContext mockServletContext = new MockServletContext();
    new SpannerClient().contextInitialized(new ServletContextEvent(mockServletContext));
    SpannerTestTasks.setup();
  }

  @AfterClass
  public static void tearDown() throws Exception {
    SpannerTestTasks.cleanup();
  }


  /** Verify insertion of event in db and retrieval by id*/
  /** Also tests behavior of EventCreationServlet doGet() where request.getParameter("eventId") == event.getId() here*/
  @Test
  public void eventInsertAndRetrieval() {
    SpannerTasks.insertOrUpdateUser(HOST);
    Event event =
        new Event.Builder(EVENT_NAME, DESCRIPTION, LABELS, LOCATION, DATE, TIME, HOST).build();
    SpannerTasks.insertorUpdateEvent(event);
    Event dbEvent = SpannerTasks.getEventById(event.getId()).get();

    Assert.assertEquals(dbEvent.getName(), EVENT_NAME);
    Assert.assertEquals(dbEvent.getDescription(), DESCRIPTION);
    Assert.assertEquals(dbEvent.getLabels(), LABELS);
    Assert.assertEquals(dbEvent.getLocation(), LOCATION);
    Assert.assertEquals(dbEvent.getDate(), DATE);
    Assert.assertEquals(dbEvent.getTime(), TIME);
    Assert.assertEquals(dbEvent.getHost(), HOST);
  }

  /** Verify getting Set<Event> from corresponding eventIds*/
  @Test
  public void getEventsByIdTest() {
    SpannerTasks.insertOrUpdateUser(HOST);
    Event event =
        new Event.Builder(EVENT_NAME, DESCRIPTION, LABELS, LOCATION, DATE, TIME, HOST).build();
    SpannerTasks.insertorUpdateEvent(event);
    Event otherEvent =
        new Event.Builder(NEW_EVENT_NAME, DESCRIPTION, LABELS, LOCATION, DATE, TIME, HOST).build();
    SpannerTasks.insertorUpdateEvent(otherEvent);
    Set<Event> insertedEvents = new HashSet<>(Arrays.asList(event, otherEvent));

    Set<Event> dbEvents = SpannerTasks.getEventsFromIds(Arrays.asList(event.getId(), otherEvent.getId()));

    Assert.assertEquals(dbEvents, insertedEvents);
  }

  /** Verify putting event in database through doPost with correct params and getting back correct redirectURL*/
  @Test
  public void testEventCreationDoPost() throws Exception {
    MockHttpServletRequest request = new MockHttpServletRequest();
    MockHttpServletResponse response = new MockHttpServletResponse();
    request.addParameter("name", EVENT_NAME);
    request.addParameter("date", DATE_STRING);
    request.addParameter("time", TIME);
    request.addParameter("description", DESCRIPTION);
    request.addParameter("location", LOCATION);

    new EventCreationServlet().doPost(request, response);

    // Get back Event posted in db
    Event returnEvent = new Gson().fromJson(response.getContentAsString(), Event.class);

    // Check redirected URL is the ID of the item put in as request
    Assert.assertEquals(response.getRedirectedUrl(), "/event-details.html?eventId=" + returnEvent.getId());
  }
}

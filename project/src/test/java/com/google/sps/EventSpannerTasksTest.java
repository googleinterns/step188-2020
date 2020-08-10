package com.google.sps;
import com.google.cloud.Date;
import com.google.sps.data.Event;
import com.google.sps.data.User;
import com.google.sps.data.VolunteeringOpportunity;
import com.google.sps.utilities.SpannerClient;
import com.google.sps.utilities.SpannerTasks;
import com.google.sps.utilities.SpannerTestTasks;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import javax.servlet.ServletContextEvent;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.springframework.mock.web.MockServletContext;

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

  /** Verify getting Set<Event> from eventIds*/
  @Test
  public void getEventsByIdTest() {
    SpannerTasks.insertOrUpdateUser(HOST);
    Event event =
        new Event.Builder(EVENT_NAME, DESCRIPTION, LABELS, LOCATION, DATE, TIME, HOST).build();
    SpannerTasks.insertorUpdateEvent(event);
    Event otherEvent =
        new Event.Builder(NEW_EVENT_NAME, DESCRIPTION, LABELS, LOCATION, DATE, TIME, HOST).build();
    SpannerTasks.insertorUpdateEvent(otherEvent);

    Set<Event> dbEvents = SpannerTasks.getEventsFromIds(Arrays.asList(event.getId(), otherEvent.getId() ));
    Set<Event> compared = new HashSet<>(Arrays.asList(event, otherEvent));
    //events given back have diff ids
    //Assert.assertEquals(dbEvents.get(event),  event);

    Assert.assertEquals(dbEvents, compared);


  }

//     /** Verify getting Set<Event> from eventIds*/
//   @Test
//   public void getEventsByIdTest() {
//     SpannerTasks.insertOrUpdateUser(HOST);
//     Event event =
//         new Event.Builder(EVENT_NAME, DESCRIPTION, LABELS, LOCATION, DATE, TIME, HOST).build();
//     SpannerTasks.insertorUpdateEvent(event);
//     Event otherEvent =
//         new Event.Builder(NEW_EVENT_NAME, DESCRIPTION, LABELS, LOCATION, DATE, TIME, HOST).build();
//     SpannerTasks.insertorUpdateEvent(otherEvent);
//     Set<Event> dbEvents = SpannerTasks.getEventsFromIds(Arrays.asList(event.getId(), otherEvent.getId()));
//     Event item = dbEvents.iterator().next();
//     Assert.assertEquals(item, event);
//     Event nextItem = dbEvents.iterator().next();
//     Assert.assertEquals(nextItem, otherEvent);
//     //events given back have diff ids
//     //Assert.assertEquals(dbEvents,  new HashSet<Event> (Arrays.asList(event, otherEvent)));

//   }
}

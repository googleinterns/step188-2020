package com.google.sps;

import com.google.cloud.Date;
import com.google.sps.data.Event;
import com.google.sps.data.User;
import com.google.sps.servlets.EventDetailServlet;
import com.google.sps.servlets.FilteredEventServlet;
import com.google.sps.utilities.CommonUtils;
import com.google.sps.utilities.SpannerClient;
import com.google.sps.utilities.SpannerTasks;
import com.google.sps.utilities.SpannerTestTasks;
import com.google.sps.utilities.TestUtils;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import javax.servlet.ServletContextEvent;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONException;
import org.hamcrest.CoreMatchers;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mockito;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.mock.web.MockServletContext;


@RunWith(JUnit4.class)
public final class EventDetailTest {
  private static final String NAME = "Bob Smith";
  private static final String EMAIL = "bobsmith@example.com";
  private static final Set<String> INTERESTS =
      Collections.unmodifiableSet(new HashSet<>(Arrays.asList("Conservation", "Food")));
  private static final Set<String> SKILLS =
      Collections.unmodifiableSet(new HashSet<>(Arrays.asList("Cooking")));
  private static final Date DATE = Date.fromYearMonthDay(2016, 9, 15);
  private static final String LOCATION = "Online";
  private static final String TIME = "3:00PM-5:00PM";
  private static final String IMAGE_URL = "image-url.com";
  private static final String EVENT_ID1 = "0883de79-17d7-49a3-a866-dbd5135062a8";
  private static final String EVENT_ID2 = "4fdcd5e9-52b5-4a43-a1f3-2b697c3d5244";
  private static final User USER =
      new User.Builder(NAME, EMAIL).setInterests(INTERESTS).setSkills(SKILLS).build();
  private static final Event EVENT1 = 
      new Event.Builder(
              "Weekly Meal Prep: Angel Food Cake",
              "In this Meal Prep Seminar, we will be teaching you how to make a delicious cake!",
              new HashSet<>(Arrays.asList("Tech", "Work")),
              LOCATION,
              DATE,
              TIME,
              USER)
          .setId(EVENT_ID1)
          .setImageUrl(IMAGE_URL)
          .build();
  private static final Event EVENT2 =
      new Event.Builder(
              "Chess tournaments",
              "Gather all the nerds in your life for the checkmate of a lifetime.",
              new HashSet<>(Arrays.asList("Chess", "Tournaments")),
              LOCATION,
              DATE,
              TIME,
              USER)
          .setId(EVENT_ID2)
          .setImageUrl(IMAGE_URL)
          .build();
  private static final HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
  private static final HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
  private static final StringWriter stringWriter = new StringWriter();
  private static final PrintWriter writer = new PrintWriter(stringWriter);

  @BeforeClass
  public static void setUp() throws Exception {
    // Mock a request to trigger the SpannerClient setup to run
    MockServletContext mockServletContext = new MockServletContext();
    new SpannerClient().contextInitialized(new ServletContextEvent(mockServletContext));

    SpannerTestTasks.setup();

    Mockito.when(response.getWriter()).thenReturn(writer);
    insertRequiredRows();
  }

  @AfterClass
  public static void tearDown() throws Exception {
    SpannerTestTasks.cleanup();
  }

  @After
  public void flushWriter() {
    Mockito.reset(request);
    stringWriter.getBuffer().setLength(0);
  }

  @Test
  public void verifyGetAllEvents() throws IOException {
    Set<Event> expectedEvents = new HashSet(Arrays.asList(EVENT1, EVENT2));

    new EventDetailServlet().doGet(request, response);

    String actualEvents = stringWriter.toString().trim();
    for (Event event : expectedEvents) {
      Assert.assertThat(actualEvents, CoreMatchers.containsString(CommonUtils.convertToJson(event).trim()));
    }
  }

  private static void insertRequiredRows() {
    SpannerTasks.insertOrUpdateUser(USER);
    SpannerTasks.insertorUpdateEvent(EVENT1);
    SpannerTasks.insertorUpdateEvent(EVENT2);
  }
}

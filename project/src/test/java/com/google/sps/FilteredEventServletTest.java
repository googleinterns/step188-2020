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
public final class FilteredEventServletTest {
  private static final String NAME = "Bob Smith";
  private static final String EMAIL = "bobsmith@example.com";
  private static final String LABEL_PARAMETER = "labelParams";
  private static final User USER =
      new User.Builder(NAME, EMAIL).build();
  private static final Event EVENT1 = TestUtils.newEvent().toBuilder()
      .setLabels(new HashSet<>(Arrays.asList("Tech", "Work")))
      .build();
  private static final Event EVENT2 = TestUtils.newEvent().toBuilder()
      .setLabels(new HashSet<>(Arrays.asList("Chess", "Tournament")))
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
  public void verifyGetFilteredEvents() throws IOException {
    Set<Event> expectedEvents = new HashSet(Arrays.asList(EVENT1, EVENT2));
    Mockito.when(request.getParameter(LABEL_PARAMETER)).thenReturn("Work-Chess");

    new FilteredEventServlet().doGet(request, response);

    try {
      JSONAssert.assertEquals(CommonUtils.convertToJson(expectedEvents).trim(),stringWriter.toString().trim(), /*ordered=*/ false);
    } catch (JSONException e) {
      System.out.println("JSON conversion failed.");
    }
  }

  @Test
  public void verifyGetOneFilteredEvent() throws IOException {
    Set<Event> expectedEvents = new HashSet(Arrays.asList(EVENT1));
    Mockito.when(request.getParameter(LABEL_PARAMETER)).thenReturn( "Work");

    new FilteredEventServlet().doGet(request, response);

    try {
      JSONAssert.assertEquals(CommonUtils.convertToJson(expectedEvents).trim(),stringWriter.toString().trim(), /*ordered=*/ false);
    } catch (JSONException e) {
      System.out.println("JSON conversion failed.");
    }
  }

  @Test
  public void verifyGetNoFilteredEvent() throws IOException {
    Set<Event> expectedEvents = new HashSet(Arrays.asList(EVENT1));
    Mockito.when(request.getParameter(LABEL_PARAMETER)).thenReturn("None");

    new FilteredEventServlet().doGet(request, response);

    Mockito.verify(response)
        .sendError(HttpServletResponse.SC_NOT_FOUND, "No events found with labels None");
  }

  private static void insertRequiredRows() {
    SpannerTasks.insertOrUpdateUser(USER);
    SpannerTasks.insertorUpdateEvent(EVENT1);
    SpannerTasks.insertorUpdateEvent(EVENT2);
  }
}

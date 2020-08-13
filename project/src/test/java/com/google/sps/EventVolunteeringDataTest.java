package com.google.sps;
 
import com.google.sps.data.Event;
import com.google.sps.data.VolunteeringOpportunity;
import com.google.sps.servlets.EventVolunteeringDataServlet;
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
import java.util.UUID;
import javax.servlet.ServletContextEvent;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mockito;
import org.springframework.mock.web.MockServletContext;
 
/** Test that tests getting the signups for a volunteering opportunity. */
@RunWith(JUnit4.class)
public final class EventVolunteeringDataTest {
  private static final String PARAMETER_EVENT_ID = "event-id";
  private HttpServletRequest request;
  private HttpServletResponse response;
  private StringWriter stringWriter;
  private PrintWriter printWriter;
  private EventVolunteeringDataServlet eventVolunteeringDataServlet;
 
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
 
    eventVolunteeringDataServlet = new EventVolunteeringDataServlet();
  }
 
  @After
  public void tearDown() {
    SpannerTestTasks.cleanup();
  }
 
  @Test
  public void testGetSignups_EventNotSpecified() throws IOException {
    Mockito
        .when(request.getParameter(PARAMETER_EVENT_ID))
        .thenReturn(null);
 
    eventVolunteeringDataServlet.doGet(request, response);
 
    Mockito.verify(response)
        .sendError(HttpServletResponse.SC_BAD_REQUEST, String.format("No event specified."));
  }
 
  @Test
  public void testGetOppportunities_EventIdNotInDatabase() throws IOException {
    String eventId = TestUtils.newRandomId();
    Mockito
        .when(request.getParameter(PARAMETER_EVENT_ID))
        .thenReturn(eventId);
 
    eventVolunteeringDataServlet.doGet(request, response);
 
    Assert.assertEquals(
        CommonUtils.convertToJson(new HashSet()), stringWriter.toString().trim());
  }
 
  @Test
  public void testGetOpportunities_NoOpportunities() throws IOException {
    Event event = TestUtils.newEvent();
    SpannerTasks.insertorUpdateEvent(event);

    Mockito
        .when(request.getParameter(PARAMETER_EVENT_ID))
        .thenReturn(event.getId());

    eventVolunteeringDataServlet.doGet(request, response);
 
    Assert.assertEquals(
        CommonUtils.convertToJson(new HashSet()), stringWriter.toString().trim());
  }
 
  @Test
  public void testGetOpportunities_NonzeroOpportunities() throws IOException {
    Event event = TestUtils.newEvent();
    SpannerTasks.insertorUpdateEvent(event);
    String eventId = event.getId();
    VolunteeringOpportunity opportunity = TestUtils.newVolunteeringOpportunityWithEventId(eventId);
    SpannerTasks.insertVolunteeringOpportunity(opportunity);
 
    Mockito.when(request.getParameter(PARAMETER_EVENT_ID)).thenReturn(eventId);
 
    eventVolunteeringDataServlet.doGet(request, response);
 
    Assert.assertEquals(
        CommonUtils.convertToJson(new HashSet<>(Arrays.asList(opportunity))).trim(),
        stringWriter.toString().trim());
  }
}

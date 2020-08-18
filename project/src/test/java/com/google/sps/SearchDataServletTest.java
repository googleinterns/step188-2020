package com.google.sps;

import com.google.sps.servlets.SearchDataServlet;
import com.google.sps.utilities.CommonUtils;
import com.google.sps.utilities.TestUtils;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletContextEvent;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mockito;

/** Unit tests for adding new events to search index and retrieving search results. */
@RunWith(JUnit4.class)
public class SearchDataServletTest {
  private HttpServletRequest postRequest;
  private HttpServletResponse postResponse;
  private HttpServletRequest secondPostRequest;
  private HttpServletResponse secondPostResponse;
  private HttpServletRequest getRequest;
  private HttpServletResponse getResponse;
  private StringWriter stringWriter;
  private PrintWriter printWriter;
  private SearchDataServlet searchDataServlet;
  private static final String EVENT_ID_1 = TestUtils.newRandomId();
  private static final String EVENT_ID_2 = TestUtils.newRandomId();
  private static final String PARAMETER_KEYWORD = "keyword";
  private static final String PARAMETER_EVENT_ID = "event-id";
  private static final String PARAMETER_DESCRIPTION = "description";
  private static final String WALKING = "walking";
  private static final String CAKE = "cake";
  private static final String MUSIC = "music";
  private static final String DESCRIPTION_WITH_MUSIC_KEYWORD =
    "This is an annual event held at the State Capitol to celebrate agricultures."
    + "The event will include food, live music and educational booths with public speakers."
    + "Event hours is 9 am- 1 pm. 2550 attendees anticipated.";
  private static final String DESCRIPTION_WITH_WALKING_KEYWORD =
    "Sutter Middle School will be walking to McKinley Park. 7th grade class and teachers"
    + " will have a picnic, play games and eat lunch at the park and Clunie Pool.";
  private static final String DESCRIPTION_WITHOUT_CAKE_KEYWORD =
    "Sutter Middle School will be walking to McKinley Park. 7th grade class and teachers"
    + " will have a picnic, play games and eat lunch at the park and Clunie Pool.";

  @Before
  public void setUp() throws Exception {
    postRequest = Mockito.mock(HttpServletRequest.class);
    postResponse = Mockito.mock(HttpServletResponse.class);

    secondPostRequest = Mockito.mock(HttpServletRequest.class);
    secondPostResponse = Mockito.mock(HttpServletResponse.class);

    getRequest = Mockito.mock(HttpServletRequest.class);
    getResponse = Mockito.mock(HttpServletResponse.class);

    stringWriter = new StringWriter();
    printWriter = new PrintWriter(stringWriter);
    Mockito.when(getResponse.getWriter()).thenReturn(printWriter);

    searchDataServlet = new SearchDataServlet();
  }

  @Test
  public void addEventAndRetrieveResultsForKeywordNotInEventDescription_noResultsReturned()
      throws IOException {
    Mockito.when(postRequest.getParameter(PARAMETER_DESCRIPTION))
        .thenReturn(DESCRIPTION_WITHOUT_CAKE_KEYWORD);
    Mockito.when(postRequest.getParameter(PARAMETER_EVENT_ID)).thenReturn(EVENT_ID_1);
    Mockito.when(getRequest.getParameter(PARAMETER_KEYWORD)).thenReturn(CAKE);

    searchDataServlet.doPost(postRequest, postResponse);
    searchDataServlet.doGet(getRequest, getResponse);

    Assert.assertEquals(CommonUtils.convertToJson(Arrays.asList()), stringWriter.toString().trim());
  }

  @Test
  public void addEventAndRetrieveResultsForKeywordInEventDescription_oneResultReturned()
      throws IOException {
    Mockito.when(postRequest.getParameter(PARAMETER_DESCRIPTION))
        .thenReturn(DESCRIPTION_WITH_WALKING_KEYWORD);
    Mockito.when(postRequest.getParameter(PARAMETER_EVENT_ID)).thenReturn(EVENT_ID_1);
    Mockito.when(getRequest.getParameter(PARAMETER_KEYWORD)).thenReturn(WALKING);

    searchDataServlet.doPost(postRequest, postResponse);
    searchDataServlet.doGet(getRequest, getResponse);

    Assert.assertEquals(
        CommonUtils.convertToJson(Arrays.asList(EVENT_ID_1)).trim(),
        stringWriter.toString().trim());
  }

  @Test
  public void addTwoEventsAndRetrieveResultsForKeywordInDescriptionOfTwoEvents_twoResultsReturned()
      throws IOException {
    Mockito.when(postRequest.getParameter(PARAMETER_EVENT_ID)).thenReturn(EVENT_ID_1);
    Mockito.when(postRequest.getParameter(PARAMETER_DESCRIPTION))
        .thenReturn(DESCRIPTION_WITH_MUSIC_KEYWORD);
    Mockito.when(secondPostRequest.getParameter(PARAMETER_EVENT_ID)).thenReturn(EVENT_ID_2);
     Mockito.when(secondPostRequest.getParameter(PARAMETER_DESCRIPTION))
        .thenReturn(DESCRIPTION_WITH_MUSIC_KEYWORD);
    Mockito.when(getRequest.getParameter(PARAMETER_KEYWORD)).thenReturn(MUSIC);

    searchDataServlet.doPost(postRequest, postResponse);
    searchDataServlet.doPost(secondPostRequest, secondPostResponse);
    searchDataServlet.doGet(getRequest, getResponse);

    Assert.assertEquals(
        CommonUtils.convertToJson(Arrays.asList(EVENT_ID_1, EVENT_ID_2)),
        stringWriter.toString().trim());
  }

  @Test
  public void retrieveResultsKeywordNotSpecified_errorResponse() throws IOException {
    Mockito.when(getRequest.getParameter(PARAMETER_KEYWORD)).thenReturn(null);
 
    searchDataServlet.doGet(getRequest, getResponse);
 
    Mockito.verify(getResponse)
        .sendError(
            HttpServletResponse.SC_BAD_REQUEST, String.format("No keyword specified."));
  }

  @Test
  public void addEvent_keywordNotSpecified_errorResponse() throws IOException {
    Mockito.when(postRequest.getParameter(PARAMETER_EVENT_ID)).thenReturn(null);
 
    searchDataServlet.doPost(postRequest, postResponse);
 
    Mockito.verify(postResponse)
        .sendError(
            HttpServletResponse.SC_BAD_REQUEST, String.format("No event ID specified."));
  }
}

package com.google.sps;

import java.util.Arrays;
import java.util.Set;
import javax.servlet.ServletContextEvent;
import com.google.sps.servlets.SearchDataServlet;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.springframework.mock.web.MockServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import org.junit.Before;
import org.junit.After;
import org.mockito.Mockito;
import com.google.sps.utilities.CommonUtils;


/** Unit tests for Search Data servlet. */
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
  private static final String EVENT_ID_1 = "0883de79-17d7-49a3-a866-dbd5135062a8";
  private static final String EVENT_ID_2 = "9912eu99-17d7-49a3-a866-dbd513456708";
  private static final String PARAMETER_KEYWORD = "keyword";
  private static final String PARAMETER_EVENT_ID = "event-id";
  private static final String PARAMETER_NAME = "name";
  private static final String PARAMETER_DESCRIPTION = "description";
  private static final String WALKING = "walking";
  private static final String CAKE = "cake";
  private static final String MUSIC = "music";
 
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
  public void retrieveResultsForKeywordInOnEventDescription_oneResultReturned() throws IOException {
    Mockito.when(postRequest.getParameter(PARAMETER_DESCRIPTION))
        .thenReturn(newDescriptionWithWalkingKeyword());
    Mockito.when(postRequest.getParameter(PARAMETER_EVENT_ID)).thenReturn(EVENT_ID_1);
    Mockito.when(getRequest.getParameter(PARAMETER_KEYWORD)).thenReturn(WALKING);

    searchDataServlet.doPost(postRequest, postResponse);
    searchDataServlet.doGet(getRequest, getResponse);

    Assert.assertEquals(
        CommonUtils.convertToJson(Arrays.asList(EVENT_ID_1)).trim(), stringWriter.toString().trim());
  }

  @Test
  public void addSingleEventAndRetrieveResultsForKeywordNotInEventInfo_noResultsReturned() throws IOException {
    Mockito.when(postRequest.getParameter(PARAMETER_DESCRIPTION))
        .thenReturn(newDescriptionWithoutCakeKeyword());
    Mockito.when(postRequest.getParameter(PARAMETER_EVENT_ID)).thenReturn(EVENT_ID_1);
    Mockito.when(getRequest.getParameter(PARAMETER_KEYWORD)).thenReturn(CAKE);

    searchDataServlet.doPost(postRequest, postResponse);
    searchDataServlet.doGet(getRequest, getResponse);

    Assert.assertEquals(
        CommonUtils.convertToJson(Arrays.asList()), stringWriter.toString().trim());
  }

  @Test
  public void addTwoEventsAndRetrieveResultsForKeywordInDescriptionOfTwoEvents_twoResultsReturned() throws IOException {
    Mockito.when(postRequest.getParameter(PARAMETER_DESCRIPTION))
        .thenReturn(newDescriptionWithMusicKeyword());
    Mockito.when(postRequest.getParameter(PARAMETER_EVENT_ID)).thenReturn(EVENT_ID_1);
    Mockito.when(secondPostRequest.getParameter(PARAMETER_DESCRIPTION))
        .thenReturn(newDescriptionWithMusicKeyword());
    Mockito.when(secondPostRequest.getParameter(PARAMETER_EVENT_ID)).thenReturn(EVENT_ID_2);
    Mockito.when(getRequest.getParameter(PARAMETER_KEYWORD)).thenReturn(MUSIC);

    searchDataServlet.doPost(postRequest, postResponse);
    searchDataServlet.doPost(secondPostRequest, secondPostResponse);
    searchDataServlet.doGet(getRequest, getResponse);

    Assert.assertEquals(
        CommonUtils.convertToJson(Arrays.asList(EVENT_ID_1, EVENT_ID_2)), stringWriter.toString().trim());
  }

  private static String newDescriptionWithMusicKeyword() {
    return "This is an annual event held at the State Capitol to celebrate agricultures."
        + "The event will include food, live music and educational booths with public speakers."
            + "Event hours is 9 am- 1 pm. 2550 attendees anticipated.";
  }

  private static String newDescriptionWithWalkingKeyword() {
    return "Sutter Middle School will be walking to McKinley Park. 7th grade class and teachers will"
        + "have a picnic, play games and eat lunch at the park and Clunie Pool.";
  }

  private static String newDescriptionWithoutCakeKeyword() {
    return "Sutter Middle School will be walking to McKinley Park. 7th grade class and teachers will"
        + "have a picnic, play games and eat lunch at the park and Clunie Pool.";
  }
}

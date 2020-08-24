package com.google.sps;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.anyObject;

import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.appengine.tools.development.testing.LocalUserServiceTestConfig;
import com.google.cloud.Date;
import com.google.common.collect.ImmutableList;
import com.google.sps.servlets.SearchDataServlet;
import com.google.sps.servlets.EventCreationServlet;
import com.google.sps.data.Event;
import com.google.sps.data.EventResult;
import com.google.sps.data.Keyword;
import com.google.sps.data.User;
import com.google.sps.store.SearchStore;
import com.google.sps.utilities.CommonUtils;
import com.google.sps.utilities.NlpProcessing;
import com.google.sps.utilities.KeywordHelper;
import com.google.sps.utilities.SpannerClient;
import com.google.sps.utilities.SpannerTasks;
import com.google.sps.utilities.SpannerTestTasks;
import com.google.sps.utilities.TestUtils;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Calendar;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.Assert;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.mockito.Mockito;
import org.mockito.ArgumentMatchers;
import javax.servlet.ServletContextEvent;
import org.springframework.mock.web.MockServletContext;
import com.google.gson.Gson;

/**
 * Unit tests for testing addition to the search index upon event creation in EventCreationServlet
 * and retrieving search results using SearchDataServlet.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(EventCreationServlet.class)
public class SearchDataServletTest {
  private HttpServletRequest postRequest;
  private HttpServletResponse postResponse;
  private HttpServletRequest secondPostRequest;
  private HttpServletResponse secondPostResponse;
  private HttpServletRequest getRequest;
  private HttpServletResponse getResponse;
  private StringWriter getStringWriter;
  private StringWriter postStringWriter;
  private StringWriter secondPostStringWriter;
  private PrintWriter getPrintWriter;
  private PrintWriter postPrintWriter;
  private PrintWriter secondPostPrintWriter;
  private SearchDataServlet searchDataServlet;
  private EventCreationServlet eventCreationServlet;
  private KeywordHelper mockKeywordHelper;
  private static final String EVENT_ID_1 = TestUtils.newRandomId();
  private static final String EVENT_ID_2 = TestUtils.newRandomId();
  private static final String PARAMETER_KEYWORD = "keyword";
  private static final String PARAMETER_EVENT_ID = "event-id";
  private static final String PARAMETER_NAME = "name";
  private static final String PARAMETER_DESCRIPTION = "description";
  private static final String CAKE = "cake";
  private static final String GAMES = "games";
  private static final String NAME_WITHOUT_GAMES = "End of the Year Picnic";
  private static final String DESCRIPTION_WITHOUT_GAMES =
      "Sutter Middle School will be walking to McKinley Park. 7th grade class and teachers will"
          + " have a picnic and eat lunch at the park and Clunie Pool.";
  private static final String NAME_WITH_GAMES = "End of the Year Picnic and Games";
  private static final ImmutableList<Keyword> KEYWORDS_NAME_WITHOUT_GAMES =
      ImmutableList.of(new Keyword("picnic", 1.00f));
  private static final String DESCRIPTION_WITH_GAMES_IN_LOW_RELEVANCE =
      "Sutter Middle School will be walking to McKinley Park. 7th grade class and teachers will"
          + " have a picnic, play games, and eat lunch at the park and Clunie Pool.";
  private static final ImmutableList<Keyword> KEYWORDS_DESCRIPTION_WITHOUT_GAMES =
      ImmutableList.of(
          new Keyword("Sutter Middle School", 0.43f),
          new Keyword("McKinley Park", 0.14f),
          new Keyword("teachers", 0.10f),
          new Keyword("class", 0.10f),
          new Keyword("picnic", 0.09f),
          new Keyword("park", 0.08f),
          new Keyword("lunch", 0.03f),
          new Keyword("Clunie Pool", 0.03f));
  private static final ImmutableList<Keyword> KEYWORDS_DESCRIPTION_WITH_GAMES_IN_LOW_RELEVANCE =
    ImmutableList.of(
        new Keyword("Sutter Middle School", 0.41f),
        new Keyword("McKinley Park", 0.13f),
        new Keyword("teachers", 0.09f),
        new Keyword("class", 0.09f),
        new Keyword("picnic", 0.09f),
        new Keyword("park", 0.08f),
        new Keyword("lunch", 0.07f),
        new Keyword(GAMES, 0.01f),
        new Keyword("Clunie Pool", 0.03f));
  private static final String DESCRIPTION_WITH_GAMES_IN_HIGH_RELEVANCE =
      "Community harvest festival with games, food, and candy. Event open to the public 5pm-9pm."
          + "Complete full closure for 700 attendees.";
  private static final ImmutableList<Keyword> KEYWORDS_DESCRIPTION_WITH_GAMES_IN_HIGH_RELEVANCE =
    ImmutableList.of(
        new Keyword("Community Harvest festival", 0.40f),
        new Keyword(GAMES, 0.17f),
        new Keyword("food", 0.17f),
        new Keyword("candy", 0.12f),
        new Keyword("Event", 0.06f),
        new Keyword("closure", 0.04f),
        new Keyword("attendees", 0.03f));
  private static final LocalServiceTestHelper authenticationHelper =
      new LocalServiceTestHelper(new LocalUserServiceTestConfig());
  private static final String LOCATION = "Remote";
  private static final Date FUTURE_DATE =
      Date.fromYearMonthDay(Calendar.getInstance().get(Calendar.YEAR) + 1, 9, 15);
  private static final String TIME = "3:00PM-5:00PM";
  private static final String TECH = "Tech";
  private static final String EMAIL = "bobsmith@example.com";
  private static final String DOMAIN = "example.com";
  private static final String PARAMETER_DATE = "date";
  private static final String PARAMETER_TIME = "time";
  private static final String PARAMETER_LOCATION = "location";
  private static final String PARAMETER_INTERESTS = "interests";
  private static final String HOST_NAME = "Bob Smith";
  private static final User HOST = new User.Builder(HOST_NAME, EMAIL).build();

  @Before
  public void setUp() throws Exception {
    postRequest = Mockito.mock(HttpServletRequest.class);
    postResponse = Mockito.mock(HttpServletResponse.class);

    secondPostRequest = Mockito.mock(HttpServletRequest.class);
    secondPostResponse = Mockito.mock(HttpServletResponse.class);

    postStringWriter = new StringWriter();
    postPrintWriter = new PrintWriter(postStringWriter);
    Mockito.when(postResponse.getWriter()).thenReturn(postPrintWriter);

    secondPostStringWriter = new StringWriter();
    secondPostPrintWriter = new PrintWriter(secondPostStringWriter);
    Mockito.when(secondPostResponse.getWriter()).thenReturn(secondPostPrintWriter);

    searchDataServlet = new SearchDataServlet();
    eventCreationServlet = new EventCreationServlet();
    mockKeywordHelper = Mockito.mock(KeywordHelper.class);
    eventCreationServlet.setSearchStore(new SearchStore(mockKeywordHelper));

    getRequest = Mockito.mock(HttpServletRequest.class);
    getResponse = Mockito.mock(HttpServletResponse.class);

    getStringWriter = new StringWriter();
    getPrintWriter = new PrintWriter(getStringWriter);
    Mockito.when(getResponse.getWriter()).thenReturn(getPrintWriter);

    // Mock a request to trigger the SpannerClient setup to run
    MockServletContext mockServletContext = new MockServletContext();
    new SpannerClient().contextInitialized(new ServletContextEvent(mockServletContext));
    SpannerTestTasks.setup();
    authenticationHelper.setUp();

    // Mocking necessary for labels feature
    NlpProcessing nlpProcessor = PowerMock.createMock(NlpProcessing.class);
    PowerMock.expectNew(NlpProcessing.class).andStubReturn(nlpProcessor);
    expect(nlpProcessor.getNlp(anyObject(String.class))).andStubReturn(new ArrayList());
    PowerMock.replay(nlpProcessor, NlpProcessing.class);

    SpannerTasks.insertOrUpdateUser(HOST);
    loginHost();
  }

  @After
  public void tearDown() throws Exception {
    SpannerTestTasks.cleanup();
    authenticationHelper.tearDown();
  }

  /**
   * Add event using EventCreationServlet instance and check that search for keyword using
   * SearchDataServlet instance not relevant in the event name or description does not appear in the results.
   */
  @Test
  public void oneEvent_KeywordNotRelevantInEventTitleOrDescription_noResultsReturned()
      // ID         |   Title Has Games  |   Description Has Games
      // 1                    No                  No
      throws IOException {
    Mockito.when(postRequest.getParameter(PARAMETER_NAME)).thenReturn(NAME_WITHOUT_GAMES);
    Mockito.when(postRequest.getParameter(PARAMETER_DESCRIPTION))
        .thenReturn(DESCRIPTION_WITHOUT_GAMES);
    setRequiredRequestParameters(postRequest);
    Mockito.when(getRequest.getParameter(PARAMETER_KEYWORD)).thenReturn(CAKE);

    // On the first call the getKeywords for title, return empty list
    // On the second call the getKeyords for description, return empty list
    Mockito.when(mockKeywordHelper.getKeywords())
        .thenReturn(KEYWORDS_NAME_WITHOUT_GAMES, KEYWORDS_DESCRIPTION_WITHOUT_GAMES);

    // Add an event using eventCreationServlet
    eventCreationServlet.doPost(postRequest, postResponse);
    Event returnedEvent = new Gson().fromJson(postStringWriter.toString().trim(), Event.class);
    // Assert that the returned event is the same as the inserted event.
    Assert.assertEquals(returnedEvent.getName(), NAME_WITHOUT_GAMES);
    Assert.assertEquals(returnedEvent.getDescription(), DESCRIPTION_WITHOUT_GAMES);

    // Get search results using the searchDataServlet
    searchDataServlet.doGet(getRequest, getResponse);

    Assert.assertEquals(
        CommonUtils.convertToJson(Arrays.asList()), getStringWriter.toString().trim());
  }

  /**
   * Add two events using EventCreationServlet instance and check that a search for a keyword using
   * SearchDataServlet instance returns the event with higher relevance for that keyword first.
   */
  @Test
  public void twoEvents_secondWithHigherKeywordRelevance_returnsSecondEventBeforeFirst()
      throws IOException {
    // ID         |   Title Has Games  |   Description Has Games
    // 1                   No                    Yes - LOW
    // 2                   No                    Yes - HIGH
    setRequiredRequestParameters(postRequest);
    Mockito.when(postRequest.getParameter(PARAMETER_NAME)).thenReturn(NAME_WITHOUT_GAMES);
    Mockito.when(postRequest.getParameter(PARAMETER_DESCRIPTION))
        .thenReturn(DESCRIPTION_WITH_GAMES_IN_LOW_RELEVANCE);
    setRequiredRequestParameters(secondPostRequest);
    Mockito.when(secondPostRequest.getParameter(PARAMETER_NAME))
        .thenReturn(NAME_WITHOUT_GAMES);
    Mockito.when(secondPostRequest.getParameter(PARAMETER_DESCRIPTION))
        .thenReturn(DESCRIPTION_WITH_GAMES_IN_HIGH_RELEVANCE);
    Mockito.when(getRequest.getParameter(PARAMETER_KEYWORD)).thenReturn(GAMES);
    Mockito.when(mockKeywordHelper.getKeywords())
        .thenReturn(
            // Keywords for Event with ID 1
            KEYWORDS_NAME_WITHOUT_GAMES,
            KEYWORDS_DESCRIPTION_WITH_GAMES_IN_LOW_RELEVANCE,
            // Keywords for Event with ID 2
            KEYWORDS_NAME_WITHOUT_GAMES,
            KEYWORDS_DESCRIPTION_WITH_GAMES_IN_HIGH_RELEVANCE);

    // Add an event using eventCreationServlet
    eventCreationServlet.doPost(postRequest, postResponse);
    // Assert the returned event is the same as the inserted event
    Event returnedEvent = new Gson().fromJson(postStringWriter.toString().trim(), Event.class);
    Assert.assertEquals(returnedEvent.getName(), NAME_WITHOUT_GAMES);
    Assert.assertEquals(returnedEvent.getDescription(), DESCRIPTION_WITH_GAMES_IN_LOW_RELEVANCE);

    // Add a second event using eventCreationServlet
    eventCreationServlet.doPost(secondPostRequest, secondPostResponse);
    Event secondReturnedEvent =
        new Gson().fromJson(secondPostStringWriter.toString().trim(), Event.class);
    // Assert that the returned event is the same as the second inserted event
    Assert.assertEquals(secondReturnedEvent.getName(), NAME_WITHOUT_GAMES);
    Assert.assertEquals(
        secondReturnedEvent.getDescription(), DESCRIPTION_WITH_GAMES_IN_HIGH_RELEVANCE);

    // Get search results using the searchDataServlet
    searchDataServlet.doGet(getRequest, getResponse);
    Event[] actualResults = new Gson().fromJson(getStringWriter.toString().trim(), Event[].class);
    
    Assert.assertEquals(NAME_WITHOUT_GAMES, actualResults[0].getName());
    Assert.assertEquals(DESCRIPTION_WITH_GAMES_IN_LOW_RELEVANCE, actualResults[1].getDescription());
    Assert.assertEquals(NAME_WITHOUT_GAMES, actualResults[1].getName());
    Assert.assertEquals(DESCRIPTION_WITH_GAMES_IN_LOW_RELEVANCE, actualResults[1].getDescription());
  }

  @Test
  public void retrieveResultsKeywordNotSpecified_errorResponse() throws IOException {
    Mockito.when(getRequest.getParameter(PARAMETER_KEYWORD)).thenReturn(null);

    searchDataServlet.doGet(getRequest, getResponse);

    Mockito.verify(getResponse)
        .sendError(HttpServletResponse.SC_BAD_REQUEST, String.format("No keyword specified."));
  }

  private static void loginHost() {
    authenticationHelper.setEnvIsLoggedIn(true).setEnvEmail(EMAIL).setEnvAuthDomain(DOMAIN);
  }

  private static void setRequiredRequestParameters(HttpServletRequest mockRequest) {
    SpannerTasks.insertOrUpdateUser(TestUtils.newUserWithEmail(EMAIL));
    Mockito.when(mockRequest.getParameter(PARAMETER_DATE)).thenReturn(FUTURE_DATE.toString());
    Mockito.when(mockRequest.getParameter(PARAMETER_TIME)).thenReturn(TIME);
    Mockito.when(mockRequest.getParameter(PARAMETER_LOCATION)).thenReturn(LOCATION);
    Mockito.when(mockRequest.getParameter(PARAMETER_INTERESTS)).thenReturn(TECH);
  }
}

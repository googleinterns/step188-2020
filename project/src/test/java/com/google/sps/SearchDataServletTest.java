package com.google.sps;

import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.expect;

import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.appengine.tools.development.testing.LocalUserServiceTestConfig;
import com.google.cloud.Date;
import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;
import com.google.sps.data.Event;
import com.google.sps.data.EventResult;
import com.google.sps.data.Keyword;
import com.google.sps.data.User;
import com.google.sps.servlets.EventCreationServlet;
import com.google.sps.servlets.SearchDataServlet;
import com.google.sps.store.SearchStore;
import com.google.sps.utilities.CommonUtils;
import com.google.sps.utilities.KeywordHelper;
import com.google.sps.utilities.NlpProcessing;
import com.google.sps.utilities.SpannerClient;
import com.google.sps.utilities.SpannerTasks;
import com.google.sps.utilities.SpannerTestTasks;
import com.google.sps.utilities.TestUtils;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import javax.servlet.ServletContextEvent;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.powermock.api.easymock.PowerMock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.mock.web.MockServletContext;

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
  private static final String PARAMETER_EVENT_ID = "eventId";
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
          new Keyword("Sutter Middle School", 0.41f),
          new Keyword("McKinley Park", 0.13f),
          new Keyword("teachers", 0.09f),
          new Keyword("class", 0.09f),
          new Keyword("picnic", 0.09f),
          new Keyword("park", 0.08f),
          new Keyword("lunch", 0.07f),
          new Keyword(GAMES, 0.01f),
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
   private static final String NAME_WITHOUT_GROWERS =  "Wednesday Cesar Chavez Farmers' Market with Food Vendors";
  private static final String DESCRIPTION_WITH_GROWERS =
      "Weekly farmers' market with certified growers and hot food vendors."
      + "Event hours are 10am to 1:30pm.";
 private static final String DESCRIPTION_WITHOUT_GROWERS =
      "Weekly farmers' market with hot food vendors."
      + "Event hours are 10am to 1:30pm.";
  private static final ImmutableList<Keyword> KEYWORDS_NAME_WITHOUT_GROWERS =
    ImmutableList.of(new Keyword("farmers' market", 0.54f), new Keyword("cesar chavez", 0.26f));
  private static final ImmutableList<Keyword> KEYWORDS_DESCRIPTION_WITH_GROWERS =
      ImmutableList.of(
          new Keyword("farmers' market", 0.36f),
          new Keyword("food vendors", 0.26f),
          new Keyword("growers", 0.26f),
          new Keyword("Event", 0.12f));
  private static final ImmutableList<Keyword> KEYWORDS_DESCRIPTION_WITHOUT_GROWERS =
      ImmutableList.of(
          new Keyword("farmers' market", 0.36f),
          new Keyword("food vendors", 0.26f),
          new Keyword("Event", 0.12f));
  private static final String GROWERS = "growers";

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
   * Adds event using EventCreationServlet instance and checks that search for keyword
   * not relevant in the event name or description does not appear in the results.
   * Searches for keyword using SearchDataServlet instance.
   */
  @Test
  public void oneEvent_KeywordNotRelevantInEventTitleOrDescription_noResultsReturned()
      // ID         |   Name Has Games  |   Description Has Games
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

    // Add an event using eventCreationServlet instance
    eventCreationServlet.doPost(postRequest, postResponse);
    Event returnedEvent = new Gson().fromJson(postStringWriter.toString().trim(), Event.class);
    // Assert that the returned event is the same as the inserted event.
    Assert.assertEquals(returnedEvent.getName(), NAME_WITHOUT_GAMES);
    Assert.assertEquals(returnedEvent.getDescription(), DESCRIPTION_WITHOUT_GAMES);

    // Get search results using the searchDataServlet instance
    searchDataServlet.doGet(getRequest, getResponse);

    Assert.assertEquals(
        CommonUtils.convertToJson(Arrays.asList()), getStringWriter.toString().trim());
  }

  /**
   * Adds two events using EventCreationServlet instance and checks that a search for a keyword using
   * SearchDataServlet instance returns the event with higher relevance for that keyword first.
   */
  @Test
  public void twoEvents_secondWithHigherKeywordRelevance_returnsSecondEventBeforeFirst()
      throws IOException {
    // ID         |   Name Has Games  |   Description Has Games
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

    // Add an event using eventCreationServlet instance
    eventCreationServlet.doPost(postRequest, postResponse);
    // Assert the returned event is the same as the inserted event
    Event returnedEvent = new Gson().fromJson(postStringWriter.toString().trim(), Event.class);
    Assert.assertEquals(returnedEvent.getName(), NAME_WITHOUT_GAMES);
    Assert.assertEquals(returnedEvent.getDescription(), DESCRIPTION_WITH_GAMES_IN_LOW_RELEVANCE);

    // Add a second event using eventCreationServlet instance
    eventCreationServlet.doPost(secondPostRequest, secondPostResponse);
    Event secondReturnedEvent =
        new Gson().fromJson(secondPostStringWriter.toString().trim(), Event.class);
    // Assert that the returned event is the same as the second inserted event
    Assert.assertEquals(secondReturnedEvent.getName(), NAME_WITHOUT_GAMES);
    Assert.assertEquals(
        secondReturnedEvent.getDescription(), DESCRIPTION_WITH_GAMES_IN_HIGH_RELEVANCE);

    // Get search results using the searchDataServlet instance
    searchDataServlet.doGet(getRequest, getResponse);
    Event[] actualResults = new Gson().fromJson(getStringWriter.toString().trim(), Event[].class);
    
    Assert.assertEquals(NAME_WITHOUT_GAMES, actualResults[0].getName());
    Assert.assertEquals(DESCRIPTION_WITH_GAMES_IN_LOW_RELEVANCE, actualResults[1].getDescription());
    Assert.assertEquals(NAME_WITHOUT_GAMES, actualResults[1].getName());
    Assert.assertEquals(DESCRIPTION_WITH_GAMES_IN_LOW_RELEVANCE, actualResults[1].getDescription());
  }

  /**
   * Adds event without growers keyword using EventCreationServlet instance, updates event to have
   * growers keyword in description, checks that a search for keyword growers returns one result.
   */
  @Test
  public void addEventWithoutKeyword_updateEventWithKeyword_searchForKeywordInNewDescription_oneResultReturned()
      throws IOException {
    // Update                   | Name Has Growers | Description Has Growers
    // Before Update             No                  No
    // After Update              No                  Yes
    
    // Insert event with growers in description using eventCreationServlet instance
    setRequiredRequestParameters(postRequest);
    Mockito.when(postRequest.getParameter(PARAMETER_NAME)).thenReturn(NAME_WITHOUT_GROWERS);
    Mockito.when(postRequest.getParameter(PARAMETER_DESCRIPTION))
        .thenReturn(DESCRIPTION_WITHOUT_GROWERS);
    Mockito.when(mockKeywordHelper.getKeywords())
        .thenReturn(
            // Keywords for insert
            KEYWORDS_NAME_WITHOUT_GROWERS,
            KEYWORDS_DESCRIPTION_WITHOUT_GROWERS);
    eventCreationServlet.doPost(postRequest, postResponse);

    // Assert the returned event is the same as the inserted event
    Event returnedEvent = new Gson().fromJson(postStringWriter.toString().trim(), Event.class);
    Assert.assertEquals(returnedEvent.getName(), NAME_WITHOUT_GROWERS);
    Assert.assertEquals(returnedEvent.getDescription(), DESCRIPTION_WITHOUT_GROWERS);

    // Update the inserted event with description having growers using eventCreationServlet instance
    setRequiredRequestParameters(secondPostRequest);
    Mockito.when(secondPostRequest.getParameter(PARAMETER_EVENT_ID)).thenReturn(returnedEvent.getId());
    Mockito.when(secondPostRequest.getParameter(PARAMETER_NAME))
        .thenReturn(NAME_WITHOUT_GROWERS);
    Mockito.when(secondPostRequest.getParameter(PARAMETER_DESCRIPTION))
        .thenReturn(DESCRIPTION_WITH_GROWERS);
    Mockito.when(mockKeywordHelper.getKeywords())
        .thenReturn(
            // Keywords for update
            KEYWORDS_NAME_WITHOUT_GROWERS,
            KEYWORDS_DESCRIPTION_WITH_GROWERS);
    eventCreationServlet.doPost(secondPostRequest, secondPostResponse);

    // Assert that the returned event is the same as the updated event
    Event secondReturnedEvent =
        new Gson().fromJson(secondPostStringWriter.toString().trim(), Event.class);
    Assert.assertEquals(secondReturnedEvent.getName(), NAME_WITHOUT_GROWERS);
    Assert.assertEquals(
        secondReturnedEvent.getDescription(), DESCRIPTION_WITH_GROWERS);

    // Get search results for growers using the searchDataServlet instance
    Mockito.when(getRequest.getParameter(PARAMETER_KEYWORD)).thenReturn(GROWERS);
    searchDataServlet.doGet(getRequest, getResponse);
    Event[] actualResults = new Gson().fromJson(getStringWriter.toString().trim(), Event[].class);
    
    // Assert that the search result for the event with growers in description is returned
    Assert.assertEquals(NAME_WITHOUT_GROWERS, actualResults[0].getName());
    Assert.assertEquals(DESCRIPTION_WITH_GROWERS, actualResults[0].getDescription());
  }

  /**
   * Adds event with growers keyword in description using EventCreationServlet instance, updates event to
   * remove growers keyword in description, checks that a search for keyword growers returns no results.
   */
  @Test
  public void addEventWithKeyword_updateEventToRemoveKeyword_searchForKeyword_noResultsReturned()
      throws IOException {
    // Update                   | Name Has Growers | Description Has Growers
    // Before Update             No                  Yes
    // After Update              No                  No
    
    // Add event with growers in description using eventCreationServlet instance
    setRequiredRequestParameters(postRequest);
    Mockito.when(postRequest.getParameter(PARAMETER_NAME)).thenReturn(NAME_WITHOUT_GROWERS);
    Mockito.when(postRequest.getParameter(PARAMETER_DESCRIPTION))
        .thenReturn(DESCRIPTION_WITH_GROWERS);
    Mockito.when(mockKeywordHelper.getKeywords())
        .thenReturn(
            // Keywords for insert
            KEYWORDS_NAME_WITHOUT_GROWERS,
            KEYWORDS_DESCRIPTION_WITH_GROWERS);
    eventCreationServlet.doPost(postRequest, postResponse);

    // Assert the returned event is the same as the inserted event
    Event returnedEvent = new Gson().fromJson(postStringWriter.toString().trim(), Event.class);
    Assert.assertEquals(returnedEvent.getName(), NAME_WITHOUT_GROWERS);
    Assert.assertEquals(returnedEvent.getDescription(), DESCRIPTION_WITH_GROWERS);

    // Do a second POST request using eventCreationServlet instance to remove growers from description
    setRequiredRequestParameters(secondPostRequest);
    Mockito.when(secondPostRequest.getParameter(PARAMETER_EVENT_ID)).thenReturn(returnedEvent.getId());
    Mockito.when(secondPostRequest.getParameter(PARAMETER_NAME))
        .thenReturn(NAME_WITHOUT_GROWERS);
    Mockito.when(secondPostRequest.getParameter(PARAMETER_DESCRIPTION))
        .thenReturn(DESCRIPTION_WITHOUT_GROWERS);
     Mockito.when(mockKeywordHelper.getKeywords())
        .thenReturn(
            // Keywords for update
            KEYWORDS_NAME_WITHOUT_GROWERS,
            KEYWORDS_DESCRIPTION_WITHOUT_GROWERS);
    eventCreationServlet.doPost(secondPostRequest, secondPostResponse);
    Event secondReturnedEvent =
        new Gson().fromJson(secondPostStringWriter.toString().trim(), Event.class);

    // Assert that the returned event is the same as the updated event
    Assert.assertEquals(secondReturnedEvent.getName(), NAME_WITHOUT_GROWERS);
    Assert.assertEquals(
        secondReturnedEvent.getDescription(), DESCRIPTION_WITHOUT_GROWERS);

    // Get search results for growers using the searchDataServlet instance
    Mockito.when(getRequest.getParameter(PARAMETER_KEYWORD)).thenReturn(GROWERS);
    searchDataServlet.doGet(getRequest, getResponse);
    Event[] actualResults = new Gson().fromJson(getStringWriter.toString().trim(), Event[].class);
    
    // Assert that no search results are returned
    Assert.assertEquals(
        CommonUtils.convertToJson(Arrays.asList()), getStringWriter.toString().trim());
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

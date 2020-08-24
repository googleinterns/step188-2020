package com.google.sps;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableList;
import com.google.sps.data.Event;
import com.google.sps.data.Keyword;
import com.google.sps.store.SearchStore;
import com.google.sps.utilities.KeywordHelper;
import com.google.sps.utilities.SpannerClient;
import com.google.sps.utilities.SpannerTasks;
import com.google.sps.utilities.SpannerTestTasks;
import com.google.sps.utilities.TestUtils;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletContextEvent;
import org.junit.Assert;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mockito;
import org.springframework.mock.web.MockServletContext;

/** Unit tests for adding new events to search index and retrieving search results. */
@RunWith(JUnit4.class)
public class SearchStoreTest {
  private SearchStore searchStore;
  private KeywordHelper mockKeywordHelper;
  private static final String EVENT_ID_1 = TestUtils.newRandomId();
  private static final String EVENT_ID_2 = TestUtils.newRandomId();
  private static final String GAMES = "games";
  private static final String NAME_WITHOUT_GAMES = "End of the Year Picnic";
  private static final String DESCRIPTION_WITHOUT_GAMES =
      "Sutter Middle School will be walking to McKinley Park. 7th grade class and teachers will"
          + " have a picnic and eat lunch at the park and Clunie Pool.";
  private static final String NAME_WITH_GAMES = "End of the Year Picnic and Games";
  private static final String DESCRIPTION_WITH_GAMES =
      "Sutter Middle School will be walking to McKinley Park. 7th grade class and teachers will"
          + " have a picnic, play games, and eat lunch at the park and Clunie Pool.";
   private static final ImmutableList<Keyword> KEYWORDS_NAME_WITHOUT_GAMES =
      ImmutableList.of(new Keyword("picnic", 1.00f));
  private static final ImmutableList<Keyword> KEYWORDS_NAME_WITH_GAMES =
      ImmutableList.of(new Keyword("picnic", 0.56f), new Keyword(GAMES, 0.44f));
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
  private static final ImmutableList<Keyword> KEYWORDS_DESCRIPTION_WITH_GAMES =
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
  private static final String DESCRIPTION_WITH_GAMES_IN_LOW_RELEVANCE =
      "Sutter Middle School will be walking to McKinley Park. 7th grade class and teachers will"
          + " have a picnic, play games, and eat lunch at the park and Clunie Pool.";
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
  private static final String NAME_WITH_GAMES_IN_HIGH_RELEVANCE =
      "End of the Year Games and Picnic";
  private static final ImmutableList<Keyword> KEYWORDS_NAME_WITH_GAMES_IN_HIGH_RELEVANCE =
      ImmutableList.of(new Keyword("picnic", 0.56f), new Keyword(GAMES, 0.17f));
  private static final String NAME_WITHOUT_FOOD_VENDORS = "Wednesday Cesar Chavez Farmers' Market";
  private static final ImmutableList<Keyword> KEYWORDS_NAME_WITHOUT_FOOD_VENDORS =
      ImmutableList.of(new Keyword("farmers' market", 0.54f), new Keyword("cesar chavez", 0.26f));
  private static final String DESCRIPTION_WITH_FOOD_VENDORS =
      "Weekly farmers' market with certified growers and hot food vendors."
          + "Event hours are 10am to 1:30pm.";
  private static final ImmutableList<Keyword> KEYWORDS_DESCRIPTION_WITH_FOOD_VENDORS =
      ImmutableList.of(
          new Keyword("farmers' market", 0.36f),
          new Keyword("food vendors", 0.26f),
          new Keyword("growers", 0.26f),
          new Keyword("Event", 0.12f));
  private static final String FOOD = "food";
  private static final ImmutableMap<String, String> TOKENS_NAME_WITHOUT_FOOD_VENDORS =
      ImmutableMap.<String, String>builder()
          .put("wednesday", "wednesday")
          .put("cesar", "cesar")
          .put("chavez", "chavez")
          .put("farmers", "farmer")
          .put("'", "'")
          .put("market", "market")
          .build();
  private static final ImmutableMap<String, String> TOKENS_DESCRIPTION_WITH_FOOD_VENDORS =
      ImmutableMap.<String, String>builder()
          .put("Weekly", "Weekly")
          .put("farmers", "farmer")
          .put("'", "'")
          .put("market", "market")
          .put("with", "with")
          .put("certified", "certified")
          .put("growers", "grower")
          .put("and", "and")
          .put("hot", "hot")
          .put("food", "food")
          .put("vendors", "vendor")
          .put(".", ".")
          .build();
  private static final String VENDOR = "vendor";
  private static final String NAME_WITHOUT_GROWERS = "Wednesday Cesar Chavez Farmers' Market";
  private static final ImmutableList<Keyword> KEYWORDS_NAME_WITHOUT_GROWERS =
      ImmutableList.of(
          new Keyword("farmers' market", 0.54f),
          new Keyword("cesar chavez", 0.26f));
  private static final String DESCRIPTION_WITH_GROWERS =
      "Weekly farmers' market with certified growers and hot food vendors."
      + "Event hours are 10am to 1:30pm.";
  private static final ImmutableList<Keyword> KEYWORDS_DESCRIPTION_WITH_GROWERS =
      ImmutableList.of(
          new Keyword("farmers' market", 0.36f),
          new Keyword("food vendors", 0.26f),
          new Keyword("growers", 0.26f),
          new Keyword("Event", 0.12f));
  private static final ImmutableMap<String, String> TOKENS_NAME_WITHOUT_GROWERS =
      ImmutableMap.<String, String>builder()
          .put("wednesday", "wednesday")
          .put("cesar", "cesar")
          .put("chavez", "chavez")
          .put("farmers", "farmer")
          .put("'", "'")
          .put("market", "market")
          .build();
  private static final ImmutableMap<String, String> TOKENS_DESCRIPTION_WITH_GROWERS =
      ImmutableMap.<String, String>builder()
          .put("Weekly", "Weekly")
          .put("farmers", "farmer")
          .put("'", "'")
          .put("market", "market")
          .put("with", "with")
          .put("certified", "certified")
          .put("growers", "grower")
          .put("and", "and")
          .put("hot", "hot")
          .put("food", "food")
          .put("vendors", "vendor")
          .put(".", ".")
          .build();
  private static final String NAME_WITH_FOOD_VENDORS =
      "Wednesday Cesar Chavez Farmers' Market with Food Vendors";
  private static final ImmutableList<Keyword> KEYWORDS_NAME_WITH_FOOD_VENDORS =
       ImmutableList.of(
          new Keyword("farmers' market", 0.40f),
          new Keyword("cesar chavez", 0.26f),
          new Keyword("cesar chavez", 0.22f));
  private static final ImmutableMap<String, String> TOKENS_NAME_WITH_FOOD_VENDORS =
      ImmutableMap.<String, String>builder()
          .put("wednesday", "wednesday")
          .put("cesar", "cesar")
          .put("chavez", "chavez")
          .put("farmers", "farmer")
          .put("'", "'")
          .put("market", "market")
          .put("with", "with")
          .put("food", "food")
          .put("vendors", "vendor")
          .build();
  private static final String GROWER = "grower";

  @Before
  public void setUp() throws Exception {
    // Mock a request to trigger the SpannerClient setup to run.
    MockServletContext mockServletContext = new MockServletContext();
    new SpannerClient().contextInitialized(new ServletContextEvent(mockServletContext));
    SpannerTestTasks.setup();

    mockKeywordHelper = Mockito.mock(KeywordHelper.class);
    searchStore = new SearchStore(mockKeywordHelper);
  }

  @After
  public void tearDown() throws Exception {
    SpannerTestTasks.cleanup();
  }

  /**
   * Add event to the index and check that search for keyword not relevant
   * in the event name or description does not appear in the results.
   */
  @Test
  public void oneEvent_KeywordNotRelevantInEventNameOrDescription_noResultsReturned()
      throws IOException {
    // ID         |   Name Has Games  |   Description Has Games
    // 1                    No                  No
    SpannerTasks.insertorUpdateEvent(
        TestUtils.newEventWithFutureDate(EVENT_ID_1, NAME_WITHOUT_GAMES, DESCRIPTION_WITHOUT_GAMES));
    // On the first call the getKeywords for name, return empty list
    // On the second call the getKeyords for description, return empty list
    Mockito.when(mockKeywordHelper.getKeywords())
        .thenReturn(KEYWORDS_NAME_WITHOUT_GAMES, KEYWORDS_DESCRIPTION_WITHOUT_GAMES);
    
    searchStore.addEventToIndex(EVENT_ID_1, NAME_WITHOUT_GAMES, DESCRIPTION_WITHOUT_GAMES);
    List<Event> actualResults = searchStore.getSearchResults(GAMES);

    Assert.assertEquals(Arrays.asList(), actualResults);
  }

  @Test
  public void oneEvent_keywordRelevantInNameEventInPast_noResultsReturned() throws IOException {
    // ID         |   Name Has Games  |   Description Has Games
    // 1                     Yes                    No
    SpannerTasks.insertorUpdateEvent(TestUtils.newEventWithPastDate());
    Mockito.when(mockKeywordHelper.getKeywords())
        .thenReturn(KEYWORDS_NAME_WITH_GAMES, KEYWORDS_DESCRIPTION_WITHOUT_GAMES);

    searchStore.addEventToIndex(EVENT_ID_1, NAME_WITH_GAMES, DESCRIPTION_WITHOUT_GAMES);
    List<Event> actualResults = searchStore.getSearchResults(GAMES);

    Assert.assertEquals(Arrays.asList(), actualResults);
  }

  @Test
  public void oneEvent_keywordRelevantInNameEventInFuture_oneResultReturned() throws IOException {
    // ID         |   Name Has Games  |   Description Has Games
    // 1                     Yes                    No
    SpannerTasks.insertorUpdateEvent(
        TestUtils.newEventWithFutureDate(EVENT_ID_1, NAME_WITH_GAMES, DESCRIPTION_WITHOUT_GAMES));
    Mockito.when(mockKeywordHelper.getKeywords())
        .thenReturn(KEYWORDS_NAME_WITH_GAMES, KEYWORDS_DESCRIPTION_WITHOUT_GAMES);

    searchStore.addEventToIndex(EVENT_ID_1, NAME_WITH_GAMES, DESCRIPTION_WITHOUT_GAMES);
    List<Event> actualResults = searchStore.getSearchResults(GAMES);

    Assert.assertEquals(EVENT_ID_1, actualResults.get(0).getId());
    Assert.assertEquals(NAME_WITH_GAMES, actualResults.get(0).getName());
    Assert.assertEquals(
        DESCRIPTION_WITHOUT_GAMES, actualResults.get(0).getDescription());
  }

  @Test
  public void twoEvents_tieInRelevanceInDescriptionOfBothEvents_twoResultsReturnedAnyOrder()
      throws IOException {
    // ID         |   Name Has Games  |   Description Has Games
    // 1                   No                    Yes
    // 2                   No                    Yes
    SpannerTasks.insertorUpdateEvent(
        TestUtils.newEventWithFutureDate(EVENT_ID_1, NAME_WITH_GAMES, DESCRIPTION_WITHOUT_GAMES));
    SpannerTasks.insertorUpdateEvent(
        TestUtils.newEventWithFutureDate(EVENT_ID_2, NAME_WITH_GAMES, DESCRIPTION_WITHOUT_GAMES));
    Mockito.when(mockKeywordHelper.getKeywords())
        .thenReturn(
            // Keywords for Event with ID 1
            KEYWORDS_NAME_WITHOUT_GAMES,
            KEYWORDS_DESCRIPTION_WITH_GAMES,
            // Keywords for Event with ID 2
            KEYWORDS_NAME_WITHOUT_GAMES,
            KEYWORDS_DESCRIPTION_WITH_GAMES);

    searchStore.addEventToIndex(EVENT_ID_1, NAME_WITHOUT_GAMES, DESCRIPTION_WITH_GAMES);
    searchStore.addEventToIndex(EVENT_ID_2, NAME_WITHOUT_GAMES, DESCRIPTION_WITH_GAMES);
    List<Event> actualResults = searchStore.getSearchResults(GAMES);

    Assert.assertTrue(
        ((EVENT_ID_1.equals(actualResults.get(0).getId())
                    && EVENT_ID_2.equals(actualResults.get(1).getId()))
                || (EVENT_ID_2.equals(actualResults.get(0).getId())
                    && EVENT_ID_1.equals(actualResults.get(1).getId())))
            && NAME_WITH_GAMES.equals(actualResults.get(0).getName())
            && NAME_WITH_GAMES.equals(actualResults.get(1).getName())
            && DESCRIPTION_WITHOUT_GAMES.equals(actualResults.get(0).getDescription())
            && DESCRIPTION_WITHOUT_GAMES.equals(actualResults.get(1).getDescription()));
  }

  @Test
  public void
      twoEvents_firstWithKeywordLowRelevanceInDesc_secondWithKeywordHighRelevanceInDesc_returnsSecondEventBeforeFirst()
          throws IOException {
    // ID             |   Name Has Games    |   Description Has Games
    // 1              |        No            |       Yes - LOW relevance
    // 2              |        No            |       Yes - HIGH relevance
    SpannerTasks.insertorUpdateEvent(
        TestUtils.newEventWithFutureDate(
            EVENT_ID_1, NAME_WITHOUT_GAMES, DESCRIPTION_WITH_GAMES_IN_LOW_RELEVANCE));
    SpannerTasks.insertorUpdateEvent(
        TestUtils.newEventWithFutureDate(
            EVENT_ID_2, NAME_WITHOUT_GAMES, DESCRIPTION_WITH_GAMES_IN_HIGH_RELEVANCE));
    Mockito.when(mockKeywordHelper.getKeywords())
        .thenReturn(
            // Keywords for Event with ID 1
            KEYWORDS_NAME_WITHOUT_GAMES,
            KEYWORDS_DESCRIPTION_WITH_GAMES_IN_LOW_RELEVANCE,
            // Keywords for Event with ID 2
            KEYWORDS_NAME_WITHOUT_GAMES,
            KEYWORDS_DESCRIPTION_WITH_GAMES_IN_HIGH_RELEVANCE);

    searchStore.addEventToIndex(
        EVENT_ID_1, NAME_WITHOUT_GAMES, DESCRIPTION_WITH_GAMES_IN_LOW_RELEVANCE);
    searchStore.addEventToIndex(
        EVENT_ID_2, NAME_WITHOUT_GAMES, DESCRIPTION_WITH_GAMES_IN_HIGH_RELEVANCE);
    List<Event> actualResults = searchStore.getSearchResults(GAMES);

    Assert.assertEquals(EVENT_ID_2, actualResults.get(0).getId());
    Assert.assertEquals(NAME_WITHOUT_GAMES, actualResults.get(0).getName());
    Assert.assertEquals(
        DESCRIPTION_WITH_GAMES_IN_HIGH_RELEVANCE, actualResults.get(0).getDescription());
    Assert.assertEquals(EVENT_ID_1, actualResults.get(1).getId());
    Assert.assertEquals(NAME_WITHOUT_GAMES, actualResults.get(1).getName());
    Assert.assertEquals(
        DESCRIPTION_WITH_GAMES_IN_LOW_RELEVANCE, actualResults.get(1).getDescription());
  }

  @Test
  public void
      twoEvents_tieInRelevanceInDescription_secondHasRelevanceInName_returnsSecondEventBeforeFirst()
          throws IOException {
    // ID             |   Name Has Games    |   Description Has Games
    // 1              |        No            |     Yes - HIGH relevance
    // 2              |        Yes           |    Yes - HIGH relevance
    SpannerTasks.insertorUpdateEvent(
        TestUtils.newEventWithFutureDate(
            EVENT_ID_1, NAME_WITHOUT_GAMES, DESCRIPTION_WITH_GAMES_IN_HIGH_RELEVANCE));
    SpannerTasks.insertorUpdateEvent(
        TestUtils.newEventWithFutureDate(
            EVENT_ID_2, NAME_WITH_GAMES, DESCRIPTION_WITH_GAMES_IN_HIGH_RELEVANCE));
    Mockito.when(mockKeywordHelper.getKeywords())
        .thenReturn(
            // Keywords for Event with ID 1
            KEYWORDS_NAME_WITHOUT_GAMES,
            KEYWORDS_DESCRIPTION_WITH_GAMES_IN_HIGH_RELEVANCE,
            // Keywords for Event with ID 2
            KEYWORDS_NAME_WITH_GAMES,
            KEYWORDS_DESCRIPTION_WITH_GAMES_IN_HIGH_RELEVANCE);

    searchStore.addEventToIndex(
        EVENT_ID_1, NAME_WITHOUT_GAMES, DESCRIPTION_WITH_GAMES_IN_HIGH_RELEVANCE);
    searchStore.addEventToIndex(
        EVENT_ID_2, NAME_WITH_GAMES, DESCRIPTION_WITH_GAMES_IN_HIGH_RELEVANCE);
    List<Event> actualResults = searchStore.getSearchResults(GAMES);

    Assert.assertEquals(EVENT_ID_2, actualResults.get(0).getId());
    Assert.assertEquals(NAME_WITH_GAMES, actualResults.get(0).getName());
    Assert.assertEquals(
        DESCRIPTION_WITH_GAMES_IN_HIGH_RELEVANCE, actualResults.get(0).getDescription());
    Assert.assertEquals(EVENT_ID_1, actualResults.get(1).getId());
    Assert.assertEquals(NAME_WITHOUT_GAMES, actualResults.get(1).getName());
    Assert.assertEquals(
        DESCRIPTION_WITH_GAMES_IN_HIGH_RELEVANCE, actualResults.get(1).getDescription());
  }

  @Test
  public void
      twoEvents_tieInRelevanceInName_secondHasRelevanceInDescription_returnsSecondEventBeforeFirst()
          throws IOException {
    // ID         |   Name Has Games                       |   Description Has Games
    // 1          |        YES - HIGH relevance            |     No
    // 2          |        Yes - HIGH relevance            |    Yes
    SpannerTasks.insertorUpdateEvent(
        TestUtils.newEventWithFutureDate(
            EVENT_ID_1, NAME_WITH_GAMES_IN_HIGH_RELEVANCE, DESCRIPTION_WITHOUT_GAMES));
    SpannerTasks.insertorUpdateEvent(
        TestUtils.newEventWithFutureDate(EVENT_ID_2, NAME_WITH_GAMES_IN_HIGH_RELEVANCE, DESCRIPTION_WITH_GAMES));
    Mockito.when(mockKeywordHelper.getKeywords())
        .thenReturn(
            // Keywords for Event with ID 1
            KEYWORDS_NAME_WITH_GAMES_IN_HIGH_RELEVANCE,
            KEYWORDS_DESCRIPTION_WITHOUT_GAMES,
            // Keywords for Event with ID 2
            KEYWORDS_NAME_WITH_GAMES_IN_HIGH_RELEVANCE,
            KEYWORDS_DESCRIPTION_WITH_GAMES);

    searchStore.addEventToIndex(
        EVENT_ID_1, NAME_WITH_GAMES_IN_HIGH_RELEVANCE, DESCRIPTION_WITHOUT_GAMES);
    searchStore.addEventToIndex(
        EVENT_ID_2, NAME_WITH_GAMES_IN_HIGH_RELEVANCE, DESCRIPTION_WITH_GAMES);
    List<Event> actualResults = searchStore.getSearchResults(GAMES);

    Assert.assertEquals(EVENT_ID_2, actualResults.get(0).getId());
    Assert.assertEquals(NAME_WITH_GAMES_IN_HIGH_RELEVANCE, actualResults.get(0).getName());
    Assert.assertEquals(
        DESCRIPTION_WITH_GAMES, actualResults.get(0).getDescription());
    Assert.assertEquals(EVENT_ID_1, actualResults.get(1).getId());
    Assert.assertEquals(NAME_WITH_GAMES_IN_HIGH_RELEVANCE, actualResults.get(1).getName());
    Assert.assertEquals(
        DESCRIPTION_WITHOUT_GAMES, actualResults.get(1).getDescription());
  }

  @Test
  public void
      addTwoEvents_firstRelevanceInDescription_secondSameRelevanceInName_returnsSecondEventBeforeFirst()
          throws IOException {
    // ID         |    Name Has Games                    |   Description Has Games
    // 1          |        No                            |     Yes - HIGH relevance
    // 2          |        Yes - HIGH relevance          |     No
    SpannerTasks.insertorUpdateEvent(
        TestUtils.newEventWithFutureDate(
            EVENT_ID_1, NAME_WITHOUT_GAMES, DESCRIPTION_WITH_GAMES_IN_HIGH_RELEVANCE));
    SpannerTasks.insertorUpdateEvent(
        TestUtils.newEventWithFutureDate(
            EVENT_ID_2, NAME_WITH_GAMES_IN_HIGH_RELEVANCE, DESCRIPTION_WITHOUT_GAMES));
    Mockito.when(mockKeywordHelper.getKeywords())
        .thenReturn(
            // Keywords for Event with ID 1
            KEYWORDS_NAME_WITHOUT_GAMES,
            KEYWORDS_DESCRIPTION_WITH_GAMES_IN_HIGH_RELEVANCE,
            // Keywords for Event with ID 2
            KEYWORDS_NAME_WITH_GAMES_IN_HIGH_RELEVANCE,
            KEYWORDS_DESCRIPTION_WITHOUT_GAMES);

    searchStore.addEventToIndex(
        EVENT_ID_1, NAME_WITHOUT_GAMES, DESCRIPTION_WITH_GAMES_IN_HIGH_RELEVANCE);
    searchStore.addEventToIndex(
        EVENT_ID_2, NAME_WITH_GAMES_IN_HIGH_RELEVANCE, DESCRIPTION_WITHOUT_GAMES);
    List<Event> actualResults = searchStore.getSearchResults(GAMES);

    Assert.assertEquals(EVENT_ID_2, actualResults.get(0).getId());
    Assert.assertEquals(NAME_WITH_GAMES_IN_HIGH_RELEVANCE, actualResults.get(0).getName());
    Assert.assertEquals(
        DESCRIPTION_WITHOUT_GAMES, actualResults.get(0).getDescription());
    Assert.assertEquals(EVENT_ID_1, actualResults.get(1).getId());
    Assert.assertEquals(NAME_WITHOUT_GAMES, actualResults.get(1).getName());
    Assert.assertEquals(
        DESCRIPTION_WITH_GAMES_IN_HIGH_RELEVANCE, actualResults.get(1).getDescription());
  }

  /**
   * When an event with description with the keyword food vendors is added to the index,
   * a search for the word food should also return the event in result. Food is a 
   * word within the keyword food vendors.
   */
  @Test
  public void
      addEventWithEntity_searchForKeywordWithinEntity_oneResultReturned()
          throws IOException {
    SpannerTasks.insertorUpdateEvent(
        TestUtils.newEventWithFutureDate(
            EVENT_ID_1, NAME_WITHOUT_FOOD_VENDORS, DESCRIPTION_WITH_FOOD_VENDORS));
    Mockito.when(mockKeywordHelper.getKeywords())
        .thenReturn(
            // Keywords for Event with ID 1
            KEYWORDS_NAME_WITHOUT_FOOD_VENDORS,
            KEYWORDS_DESCRIPTION_WITH_FOOD_VENDORS);

    searchStore.addEventToIndex(
        EVENT_ID_1, NAME_WITHOUT_FOOD_VENDORS, DESCRIPTION_WITH_FOOD_VENDORS);
    List<Event> actualResults = searchStore.getSearchResults(FOOD);

    Assert.assertEquals(EVENT_ID_1, actualResults.get(0).getId());
    Assert.assertEquals(NAME_WITHOUT_FOOD_VENDORS, actualResults.get(0).getName());
    Assert.assertEquals(
        DESCRIPTION_WITH_FOOD_VENDORS, actualResults.get(0).getDescription());
  }

  /**
   * When an event with description with the keyword growers is added to the index,
   * a search for the word grower should also return the event in result as grower is the
   * basic form of growers. The basic form of growers is grower.
   */
  @Test
  public void
      addEventWithKeyword_searchForKeywordBasicForm_oneResultReturned()
          throws IOException {
    SpannerTasks.insertorUpdateEvent(
        TestUtils.newEventWithFutureDate(
            EVENT_ID_1, NAME_WITHOUT_GROWERS, DESCRIPTION_WITH_GROWERS));
    Mockito.when(mockKeywordHelper.getKeywords())
        .thenReturn(
            // Keywords for Event with ID 1
            KEYWORDS_NAME_WITHOUT_GROWERS,
            KEYWORDS_DESCRIPTION_WITH_GROWERS);
    Mockito.when(mockKeywordHelper.getTokensWithBasicForm())
        .thenReturn(
            // Keywords for Event with ID 1
            TOKENS_NAME_WITHOUT_GROWERS,
            TOKENS_DESCRIPTION_WITH_GROWERS);

    searchStore.addEventToIndex(
        EVENT_ID_1, NAME_WITHOUT_GROWERS, DESCRIPTION_WITH_GROWERS);
    List<Event> actualResults = searchStore.getSearchResults(GROWER);

    Assert.assertEquals(EVENT_ID_1, actualResults.get(0).getId());
    Assert.assertEquals(NAME_WITHOUT_GROWERS, actualResults.get(0).getName());
    Assert.assertEquals(
        DESCRIPTION_WITH_GROWERS, actualResults.get(0).getDescription());
  }

  /**
   * Ensures the ranking still holds when basic forms and words within keywords are involved.
   * When two events the first with food vendors in the name and second with food vendors in 
   * name and description is added and the word vendor is searched, the second event is
   * returned before the first.
   */
  @Test
  public void
    addTwoEventsWithKeywordInDescription_secondWithWordInName_searchForWordWithinKeywordBasicForm_returnsSecondEventBeforeFirst()
    // ID         |    Name has food vendors             |   Description has food vendors
    // 1          |        Yes                           |     No
    // 2          |        Yes                           |     Yes
          throws IOException {
    SpannerTasks.insertorUpdateEvent(
        TestUtils.newEventWithFutureDate(
            EVENT_ID_1, NAME_WITHOUT_FOOD_VENDORS, DESCRIPTION_WITH_FOOD_VENDORS));
    SpannerTasks.insertorUpdateEvent(
        TestUtils.newEventWithFutureDate(
            EVENT_ID_2, NAME_WITH_FOOD_VENDORS, DESCRIPTION_WITH_FOOD_VENDORS));
    Mockito.when(mockKeywordHelper.getKeywords())
        .thenReturn(
            // Keywords for Event with ID 1
            KEYWORDS_NAME_WITHOUT_FOOD_VENDORS,
            KEYWORDS_DESCRIPTION_WITH_FOOD_VENDORS);
    Mockito.when(mockKeywordHelper.getTokensWithBasicForm())
        .thenReturn(
            // Keywords for Event with ID 1
            TOKENS_NAME_WITHOUT_FOOD_VENDORS,
            TOKENS_DESCRIPTION_WITH_FOOD_VENDORS);
    Mockito.when(mockKeywordHelper.getTokensWithBasicForm())
        .thenReturn(
            // Keywords for Event with ID 1
            TOKENS_NAME_WITH_FOOD_VENDORS,
            TOKENS_DESCRIPTION_WITH_FOOD_VENDORS);

    searchStore.addEventToIndex(
        EVENT_ID_1, NAME_WITHOUT_FOOD_VENDORS, DESCRIPTION_WITH_FOOD_VENDORS);
    searchStore.addEventToIndex(
        EVENT_ID_2, NAME_WITH_FOOD_VENDORS, NAME_WITH_FOOD_VENDORS);
    List<Event> actualResults = searchStore.getSearchResults(VENDOR);

    Assert.assertEquals(EVENT_ID_2, actualResults.get(0).getId());
    Assert.assertEquals(NAME_WITH_FOOD_VENDORS, actualResults.get(0).getName());
    Assert.assertEquals(
        DESCRIPTION_WITH_FOOD_VENDORS, actualResults.get(0).getDescription());
    Assert.assertEquals(EVENT_ID_1, actualResults.get(1).getId());
    Assert.assertEquals(NAME_WITHOUT_FOOD_VENDORS, actualResults.get(1).getName());
    Assert.assertEquals(
        DESCRIPTION_WITH_FOOD_VENDORS, actualResults.get(1).getDescription());
  }
}

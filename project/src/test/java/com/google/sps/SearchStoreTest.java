package com.google.sps;

import com.google.sps.data.Keyword;
import com.google.sps.store.SearchStore;
import com.google.sps.utilities.KeywordHelper;
import com.google.sps.utilities.TestUtils;
import java.io.IOException;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.collections4.CollectionUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mockito;

/** Unit tests for adding new events to search index and retrieving search results. */
@RunWith(JUnit4.class)
public class SearchStoreTest {
  private SearchStore searchStore;
  private KeywordHelper mockKeywordHelper;
  private static final String EVENT_ID_1 = TestUtils.newRandomId();
  private static final String EVENT_ID_2 = TestUtils.newRandomId();
  private static final String PARAMETER_KEYWORD = "keyword";
  private static final String PARAMETER_EVENT_ID = "event-id";
  private static final String PARAMETER_TITLE = "title";
  private static final String PARAMETER_DESCRIPTION = "description";
  private static final String GAMES = "games";
  private static final String TITLE_WITHOUT_GAMES = "End of the Year Picnic";
  private static final String DESCRIPTION_WITHOUT_GAMES =
      "Sutter Middle School will be walking to McKinley Park. 7th grade class and teachers will"
          + " have a picnic and eat lunch at the park and Clunie Pool.";
  private static final String TITLE_WITH_GAMES = "End of the Year Picnic and Games";
  private static final String DESCRIPTION_WITH_GAMES =
      "Sutter Middle School will be walking to McKinley Park. 7th grade class and teachers will"
          + " have a picnic, play games, and eat lunch at the park and Clunie Pool.";
  private static final ArrayList<Keyword> KEYWORDS_TITLE_WITHOUT_GAMES =
      new ArrayList<Keyword>(Arrays.asList(new Keyword("picnic", 1.00f)));
  private static final ArrayList<Keyword> KEYWORDS_TITLE_WITH_GAMES =
      new ArrayList<Keyword>(
          Arrays.asList(new Keyword("picnic", 0.56f), new Keyword(GAMES, 0.44f)));
  private static final ArrayList<Keyword> KEYWORDS_DESCRIPTION_WITHOUT_GAMES =
      new ArrayList<Keyword>(
          Arrays.asList(
              new Keyword("Sutter Middle School", 0.43f),
              new Keyword("McKinley Park", 0.14f),
              new Keyword("teachers", 0.10f),
              new Keyword("class", 0.10f),
              new Keyword("picnic", 0.09f),
              new Keyword("park", 0.08f),
              new Keyword("lunch", 0.03f),
              new Keyword("Clunie Pool", 0.03f)));
  private static final ArrayList<Keyword> KEYWORDS_DESCRIPTION_WITH_GAMES =
      new ArrayList<Keyword>(
          Arrays.asList(
              new Keyword("Sutter Middle School", 0.41f),
              new Keyword("McKinley Park", 0.13f),
              new Keyword("teachers", 0.09f),
              new Keyword("class", 0.09f),
              new Keyword("picnic", 0.09f),
              new Keyword("park", 0.08f),
              new Keyword("lunch", 0.07f),
              new Keyword(GAMES, 0.01f),
              new Keyword("Clunie Pool", 0.03f)));
  private static final String DESCRIPTION_WITH_GAMES_IN_LOW_RELEVANCE =
      "Sutter Middle School will be walking to McKinley Park. 7th grade class and teachers will"
          + " have a picnic, play games, and eat lunch at the park and Clunie Pool.";
  private static final ArrayList<Keyword> KEYWORDS_DESCRIPTION_WITH_GAMES_IN_LOW_RELEVANCE =
      new ArrayList<Keyword>(
          Arrays.asList(
              new Keyword("Sutter Middle School", 0.41f),
              new Keyword("McKinley Park", 0.13f),
              new Keyword("teachers", 0.09f),
              new Keyword("class", 0.09f),
              new Keyword("picnic", 0.09f),
              new Keyword("park", 0.08f),
              new Keyword("lunch", 0.07f),
              new Keyword(GAMES, 0.01f),
              new Keyword("Clunie Pool", 0.03f)));
  private static final String DESCRIPTION_WITH_GAMES_IN_HIGH_RELEVANCE =
      "Community harvest festival with games, food, and candy. Event open to the public 5pm-9pm."
          + "Complete full closure for 700 attendees.";
  private static final ArrayList<Keyword> KEYWORDS_DESCRIPTION_WITH_GAMES_IN_HIGH_RELEVANCE =
      new ArrayList<Keyword>(
          Arrays.asList(
              new Keyword("Community Harvest festival", 0.40f),
              new Keyword(GAMES, 0.17f),
              new Keyword("food", 0.17f),
              new Keyword("candy", 0.12f),
              new Keyword("Event", 0.06f),
              new Keyword("closure", 0.04f),
              new Keyword("attendees", 0.03f)));
  private static final String TITLE_WITH_GAMES_IN_HIGH_RELEVANCE =  "End of the Year Games and Picnic";
  private static final ArrayList<Keyword> KEYWORDS_TITLE_WITH_GAMES_IN_HIGH_RELEVANCE =
      new ArrayList<Keyword>(
          Arrays.asList(new Keyword("picnic", 0.56f), new Keyword(GAMES, 0.17f)));

  @Before
  public void setUp() throws Exception {
    mockKeywordHelper = Mockito.mock(KeywordHelper.class);
    searchStore = new SearchStore(mockKeywordHelper);
  }

  @Test
  public void oneEvent_KeywordNotRelevantInEventTitleOrDescription_noResultsReturned()
      throws IOException {
    // ID         |   Title Has Games  |   Description Has Games
    // 1                    No                  No    

    // On the first call the getKeywords for title, return empty list
    // On the second call the getKeyords for description, return empty list
    Mockito.when(mockKeywordHelper.getKeywords())
        .thenReturn(KEYWORDS_TITLE_WITHOUT_GAMES, KEYWORDS_DESCRIPTION_WITHOUT_GAMES);
    
    searchStore.addEventToIndex(EVENT_ID_1, TITLE_WITHOUT_GAMES, DESCRIPTION_WITHOUT_GAMES);
    List<String> actualResults = searchStore.getSearchResults(GAMES);

    Assert.assertEquals(Arrays.asList(), actualResults);
  }

  @Test
  public void oneEvent_keywordRelevantInTitle_oneResultReturned()
      throws IOException {
    // ID         |   Title Has Games  |   Description Has Games
    // EVENT_ID_1          Yes                    No
    Mockito.when(mockKeywordHelper.getKeywords()).thenReturn(KEYWORDS_TITLE_WITH_GAMES, KEYWORDS_DESCRIPTION_WITHOUT_GAMES);

    searchStore.addEventToIndex(EVENT_ID_1, TITLE_WITH_GAMES, DESCRIPTION_WITHOUT_GAMES);
    List<String> actualResults = searchStore.getSearchResults(GAMES);

    Assert.assertEquals(Arrays.asList(EVENT_ID_1), actualResults);
  }

  @Test
  public void twoEvents_keywordRelevantInDescriptionOfBothEvents_twoResultsReturned()
      throws IOException {
    // ID         |   Title Has Games  |   Description Has Games
    // 1                   No                    Yes
    // 2                   No                    Yes
    Mockito.when(mockKeywordHelper.getKeywords())
        .thenReturn(
            // Keywords for Event with ID 1
            KEYWORDS_TITLE_WITHOUT_GAMES,
            KEYWORDS_DESCRIPTION_WITH_GAMES,
            // Keywords for Event with ID 2
            KEYWORDS_TITLE_WITHOUT_GAMES,
            KEYWORDS_DESCRIPTION_WITH_GAMES);

    searchStore.addEventToIndex(EVENT_ID_1, TITLE_WITHOUT_GAMES, DESCRIPTION_WITH_GAMES);
    searchStore.addEventToIndex(EVENT_ID_2, TITLE_WITHOUT_GAMES, DESCRIPTION_WITH_GAMES);
    List<String> actualResults = searchStore.getSearchResults(GAMES);

    Assert.assertTrue(CollectionUtils.isEqualCollection(Arrays.asList(EVENT_ID_1, EVENT_ID_2), actualResults));
  }

  @Test
  public void 
      twoEvents_firstWithKeywordLowRelevanceInDesc_secondWithKeywordHighRelevanceInDesc_returnsSecondEventBeforeFirst()
          throws IOException {
    // ID         |   Title Has Games    |   Description Has Games
    // 1          |        No            |       Yes - LOW relevance
    // 2          |        No            |       Yes - HIGH relevance
    Mockito.when(mockKeywordHelper.getKeywords())
        .thenReturn(
            // Keywords for Event with ID 1
            KEYWORDS_TITLE_WITHOUT_GAMES,
            KEYWORDS_DESCRIPTION_WITH_GAMES_IN_LOW_RELEVANCE,
            // Keywords for Event with ID 2
            KEYWORDS_TITLE_WITHOUT_GAMES,
            KEYWORDS_DESCRIPTION_WITH_GAMES_IN_HIGH_RELEVANCE);

    searchStore.addEventToIndex(
        EVENT_ID_1, TITLE_WITHOUT_GAMES, DESCRIPTION_WITH_GAMES_IN_LOW_RELEVANCE);
    searchStore.addEventToIndex(
        EVENT_ID_2, TITLE_WITHOUT_GAMES, DESCRIPTION_WITH_GAMES_IN_HIGH_RELEVANCE);
    List<String> actualResults = searchStore.getSearchResults(GAMES);

    Assert.assertEquals(Arrays.asList(EVENT_ID_2, EVENT_ID_1), actualResults);
  }

  @Test
  public void
      twoEvents_tieInRelevanceInDescription_secondHasRelevanceInTitle_returnsSecondEventBeforeFirst()
          throws IOException {
    // ID         |   Title Has Games    |   Description Has Games
    // 1          |        No            |     Yes - HIGH relevance
    // 2          |        Yes           |    Yes - HIGH relevance
    Mockito.when(mockKeywordHelper.getKeywords())
        .thenReturn(
            // Keywords for Event with ID 1
            KEYWORDS_TITLE_WITHOUT_GAMES,
            KEYWORDS_DESCRIPTION_WITH_GAMES_IN_HIGH_RELEVANCE,
            // Keywords for Event with ID 2
            KEYWORDS_TITLE_WITH_GAMES,
            KEYWORDS_DESCRIPTION_WITH_GAMES_IN_HIGH_RELEVANCE);

    searchStore.addEventToIndex(
        EVENT_ID_1, TITLE_WITHOUT_GAMES, DESCRIPTION_WITH_GAMES_IN_HIGH_RELEVANCE);
    searchStore.addEventToIndex(
        EVENT_ID_2, TITLE_WITH_GAMES, DESCRIPTION_WITH_GAMES_IN_HIGH_RELEVANCE);
    List<String> actualResults = searchStore.getSearchResults(GAMES);

    Assert.assertEquals(Arrays.asList(EVENT_ID_2, EVENT_ID_1), actualResults);
  }

  @Test
  public void
      twoEvents_tieInRelevanceInTitle_secondHasRelevanceInDescription_returnsSecondEventBeforeFirst()
          throws IOException {
    // ID         |   Title Has Games                      |   Description Has Games
    // 1          |        YES - HIGH relevance            |     No
    // 2          |        Yes - HIGH relevance            |    Yes
    Mockito.when(mockKeywordHelper.getKeywords())
        .thenReturn(
            // Keywords for Event with ID 1
            KEYWORDS_TITLE_WITH_GAMES_IN_HIGH_RELEVANCE,
            KEYWORDS_DESCRIPTION_WITHOUT_GAMES,
            // Keywords for Event with ID 2
            KEYWORDS_TITLE_WITH_GAMES_IN_HIGH_RELEVANCE,
            KEYWORDS_DESCRIPTION_WITH_GAMES);

    searchStore.addEventToIndex(
        EVENT_ID_1, TITLE_WITH_GAMES_IN_HIGH_RELEVANCE, DESCRIPTION_WITHOUT_GAMES);
    searchStore.addEventToIndex(
        EVENT_ID_2, TITLE_WITH_GAMES_IN_HIGH_RELEVANCE, DESCRIPTION_WITH_GAMES);
    List<String> actualResults = searchStore.getSearchResults(GAMES);

    Assert.assertEquals(Arrays.asList(EVENT_ID_2, EVENT_ID_1), actualResults);
  }

  @Test
  public void
      addTwoEvents_firstRelevanceInDescription_secondSameRelevanceInTitle_returnsSecondEventBeforeFirst()
          throws IOException {
    // ID         |   Title Has Games                    |   Description Has Games
    // 1          |        No                            |     Yes - HIGH relevance
    // 2          |        Yes - HIGH relevance          |     No
    Mockito.when(mockKeywordHelper.getKeywords())
        .thenReturn(
            // Keywords for Event with ID 1
            KEYWORDS_TITLE_WITHOUT_GAMES,
            KEYWORDS_DESCRIPTION_WITH_GAMES_IN_HIGH_RELEVANCE,
            // Keywords for Event with ID 2
            KEYWORDS_TITLE_WITH_GAMES_IN_HIGH_RELEVANCE,
            KEYWORDS_DESCRIPTION_WITHOUT_GAMES);

    searchStore.addEventToIndex(
        EVENT_ID_1, TITLE_WITHOUT_GAMES, DESCRIPTION_WITH_GAMES_IN_HIGH_RELEVANCE);
    searchStore.addEventToIndex(
        EVENT_ID_2, TITLE_WITH_GAMES_IN_HIGH_RELEVANCE, DESCRIPTION_WITHOUT_GAMES);
    List<String> actualResults = searchStore.getSearchResults(GAMES);

    Assert.assertEquals(Arrays.asList(EVENT_ID_2, EVENT_ID_1), actualResults);
  }
}

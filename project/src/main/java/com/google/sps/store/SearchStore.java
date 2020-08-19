package com.google.sps.store;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.sps.data.EventResult;
import com.google.sps.data.Keyword;
import com.google.sps.utilities.KeywordHelper;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/** Store class that uses in-memory map to hold search results. */
public class SearchStore {
  private ListMultimap<String, EventResult> keywordToEventResults = ArrayListMultimap.create();
  private static final float WEIGHT_IN_TITLE = 0.7f;
  private static final float WEIGHT_IN_DESCRIPTION = 0.3f;
  private KeywordHelper keywordHelper;

  public SearchStore(KeywordHelper keywordHelper) {
    this.keywordHelper = keywordHelper;
  }

  /**
   * Adds keywords for title and description to index. Relevance of each keyword is the weighted
   * sum of the relevance in the title and the relevance in the description.
   *
   * @param eventId event ID
   * @param title title of the event
   * @param description description of the event
   */
  public void addEventToIndex(String eventId, String title, String description) {
    addKeywordsToIndex(eventId, title.toLowerCase(), WEIGHT_IN_TITLE);
    addKeywordsToIndex(eventId, description, WEIGHT_IN_DESCRIPTION);
  }

  /** Adds results for keywords in text to the index with relevance. */
  private void addKeywordsToIndex(String eventId, String text, float weight) {
    keywordHelper.setContent(text);
    ArrayList<Keyword> keywords = new ArrayList<Keyword>();
    try {
      keywords = keywordHelper.getKeywords();
    } catch (IOException e) {
      System.err.println("IO Exception due to NLP library.");
    }

    for (Keyword keyword : keywords) {
      float keywordRank = 0;
      Optional<EventResult> existingResult =
          keywordToEventResults.get(keyword.getName()).stream()
              .filter(result -> result.getEventId().equals(eventId))
              .findFirst();
      if (existingResult.isPresent()) {
        keywordRank += existingResult.get().getRanking();
        keywordToEventResults.remove(keyword.getName(), existingResult.get());
      }
      keywordRank += weight * keyword.getRelevance();
      keywordToEventResults.put(keyword.getName(), new EventResult(eventId, keywordRank));
    }
  }

  /**
   * Gets search results for the given keyword.
   * @param keyword keyword in search
   * @return list of event IDs in decreasing order of ranking as search result
   */
  public List<String> getSearchResults(String keyword) {
    List<EventResult> results = keywordToEventResults.get(keyword);
    Collections.sort(results, EventResult.ORDER_BY_RANKING_DESC);
    return results.stream().map(EventResult::getEventId).collect(Collectors.toList());
  }
}
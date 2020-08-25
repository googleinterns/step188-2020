package com.google.sps.store;

import com.google.sps.data.Event;
import com.google.sps.data.EventResult;
import com.google.sps.data.Keyword;
import com.google.sps.utilities.KeywordHelper;
import com.google.sps.utilities.SpannerTasks;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/** Store class that uses in-memory map to hold search results. */
public class SearchStore {
  private static final float WEIGHT_IN_NAME = 0.7f;
  private static final float WEIGHT_IN_DESCRIPTION = 0.3f;
  private KeywordHelper keywordHelper;

  public SearchStore(KeywordHelper keywordHelper) {
    this.keywordHelper = keywordHelper;
  }

  /**
   * Adds keywords for name and description with mapping to eventId to index. Relevance of each
   * keyword is the weighted sum of the relevance in the name and the relevance in the description
   * where the name is weighted by value WEIGHT_IN_NAME and description is weighted by
   * WEIGHT_IN_DESCRIPTION.
   *
   * @param eventId event ID
   * @param name name of the event
   * @param description description of the event
   */
  public void addEventToIndex(String eventId, String name, String description) {
    Map<String, Float> keywordToRanking = new HashMap<String, Float>();
    addKeywordsInTextToIndex(eventId, name.toLowerCase(), WEIGHT_IN_NAME, keywordToRanking);
    addKeywordsInTextToIndex(eventId, description, WEIGHT_IN_DESCRIPTION, keywordToRanking);
    List<EventResult> entries =
        keywordToRanking.entrySet().stream()
            .map(entry -> new EventResult(eventId, entry.getKey(), entry.getValue()))
            .collect(Collectors.toList());
    SpannerTasks.addResultsToPersistentStorageIndex(entries);
  }

  /**
   * Adds result for keywords in the given text.
   *
   * @param eventId event ID
   * @param text text to search keywords for
   * @param weight weight multiply relevance of keyword by which is proportional to the significance
   *     of the selected piece of text.
   * @param keywordToRanking the index to update
   */
  private void addKeywordsInTextToIndex(
      String eventId, String text, float weight, Map<String, Float> keywordToRanking) {
    keywordHelper.setContent(text);
    List<Keyword> keywords = new ArrayList<Keyword>();
    try {
      keywords = keywordHelper.getKeywords();
    } catch (IOException e) {
      System.err.println("IO Exception due to NLP library.");
    }

    for (Keyword keyword : keywords) {
      keywordToRanking.put(keyword.getName(), getRanking(keyword, weight, keywordToRanking));
    }
  }

  /**
   * Returns ranking equal to the sum of the existing ranking and the relevance of the keyword 
   * in the text multiplied by the given weight.
   */
  private float getRanking(Keyword keyword, float weight, Map<String, Float> keywordToRanking) {
    float keywordRank = 0;
    String name = keyword.getName();
    if (keywordToRanking.containsKey(name)) {
      keywordRank += keywordToRanking.get(name);
    }
    keywordRank += weight * keyword.getRelevance();
    return keywordRank;
  }

  /**
   * Gets event searchResults for the given keyword search.
   *
   * @param keyword keyword to search
   * @return list of events in descending order of ranking as search result
   */
  public static List<Event> getSearchResults(String keyword) {
    return SpannerTasks.getEventResultsByKeywordWithDescendingRanking(keyword);
  }
}

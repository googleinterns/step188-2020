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
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Class that generates search results, stores them in persistent storage, and retrieves
 * search results from persistent storage.
 */
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
   * @param eventId event ID where the text is found
   * @param text text to search keywords for
   * @param weight weight multiply relevance of keyword by which is proportional to the significance
   *     of the selected piece of text
   * @param keywordToRanking the index to update
   */
  private void addKeywordsInTextToIndex(
      String eventId, String text, float weight, Map<String, Float> keywordToRanking) {
    keywordHelper.setContent(text);
    List<Keyword> keywords = keywordHelper.getKeywords();
    Map<String, String> tokenToBasicForm = keywordHelper.getTokensWithBasicForm();
    for (Keyword keyword : keywords) {
      float ranking = getRanking(keyword, weight, keywordToRanking);
      String[] wordsWithinKeyword = keyword.getName().split("[^a-zA-Z0-9']+");
      if (wordsWithinKeyword.length > 1) {
        for (String word : wordsWithinKeyword) {
          addKeywordAndBasicForm(word, ranking, keywordToRanking, tokenToBasicForm);
        }
      }
      addKeywordAndBasicForm(
          keyword.getName(), ranking, keywordToRanking, tokenToBasicForm);
    }
  }

  /**
   * Adds keyword and its basic form if different than keyword to the index.
   *
   * @param keyword keyword to add to index
   * @param ranking ranking of the keyword
   * @param keywordToRanking the index to update
   * @param tokenToBasicForm map of token to its basic form for all tokens
   *        in text where keyword was found
   */
  private void addKeywordAndBasicForm(String keyword, float ranking,
      Map<String, Float> keywordToRanking, Map<String, String> tokenToBasicForm) {
    keywordToRanking.put(keyword, ranking);

    Optional<String> basicForm = Optional.ofNullable(tokenToBasicForm.get(keyword));
    if (basicForm.isPresent() && !basicForm.get().equals(keyword)) {
      keywordToRanking.put(tokenToBasicForm.get(keyword), ranking);
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

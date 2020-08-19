package com.google.sps.store;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import java.util.Collections;
import java.util.Optional;
import java.util.stream.Collectors;
import com.google.sps.data.EventResult;
import com.google.sps.data.Keyword;
import com.google.sps.store.SearchStore;
import com.google.sps.utilities.CommonUtils;
import com.google.sps.utilities.KeywordHelper;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/** Store class that uses in-memory map to hold search results. */
public class SearchStore {
  private ListMultimap<String, EventResult> keywordToEventResults = ArrayListMultimap.create();
  private KeywordHelper keywordHelper;
  private static final float WEIGHT_IN_TITLE = 0.7f;
  private static final float WEIGHT_IN_DESCRIPTION = 0.3f;

  public SearchStore(KeywordHelper keywordHelper) {
    this.keywordHelper = keywordHelper;
  }

  public void addEventToIndex(String eventId, String title, String description) {
    addKeywordsToIndex(eventId, title.toLowerCase(), WEIGHT_IN_TITLE);
    addKeywordsToIndex(eventId, description, WEIGHT_IN_DESCRIPTION);
  }

  /** Add keywords to the index with mapping to given event ID. */
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

  public List<String> getSearchResults(String keyword) {
    List<EventResult> results = keywordToEventResults.get(keyword);
    Collections.sort(results, EventResult.ORDER_BY_RANKING_DESC);
    List<String> eventIdResults = results.stream().map(EventResult::getEventId).collect(Collectors.toList());
    return eventIdResults;
  }
}
package com.google.sps.data;

import java.util.Comparator;

/* Class that represent an event result in search. */
public class EventResult {
  private String eventId;
  private String keyword;
  private float ranking;

  public EventResult(String eventId, String keyword, float ranking) {
    this.eventId = eventId;
    this.keyword = keyword;
    this.ranking = ranking;
  }

  public String getEventId() {
    return eventId;
  }

  public String getKeyword() {
    return keyword;
  }

  public float getRanking() {
    return ranking;
  }
}

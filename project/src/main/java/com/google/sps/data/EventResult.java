package com.google.sps.data;

import java.util.Comparator;

/* Class that represent an event result in search. */
public class EventResult {
  private String eventId;
  private float ranking;

  public EventResult(String eventId, float ranking) {
    this.eventId = eventId;
    this.ranking = ranking;
  }

  public String getEventId() {
    return eventId;
  }

  public float getRanking() {
    return ranking;
  }

  /** A comparator for sorting event results by their ranking in descending order. */
  public static final Comparator<EventResult> ORDER_BY_RANKING_DESC =
      new Comparator<EventResult>() {
        @Override
        public int compare(EventResult a, EventResult b) {
          return Float.compare(b.ranking, a.ranking);
        }
      };
}

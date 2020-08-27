package com.google.sps.utilities;

import com.google.common.collect.Sets;
import com.google.sps.data.Event;
import com.google.sps.data.User;
import com.google.sps.data.VolunteeringOpportunity;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.javatuples.Pair;

public class EventRanker {
  /**
   * Sorts events based on each event's score. Breaks ties with the event's date; the more recent
   * event will be ranked higher. Makes the assumption that all events are NOT in the past.
   *
   * @param user The user for whom the events are ranked
   * @param directMatches A {@link Set} of events that need to be ranked; direct event maches
   * @param similarMatches A {@link Set} of events that need to be ranked; similar event matches
   * @return A {@link List} of events that is ranked according to each element's score
   */
  public static List<Event> rankEvents(User user, Set<Event> events) {
    Map<Event, Double> eventScoreMap = getEventScoreMap(user, events);
    List<Event> rankedEvents = new ArrayList<>(eventScoreMap.keySet());
    Collections.sort(
        rankedEvents,
        (Event event1, Event event2) -> {
          int comparison = eventScoreMap.get(event1).compareTo(eventScoreMap.get(event2));
          if (comparison == 0) {
            comparison = event2.getDate().compareTo(event1.getDate());
          }
          return comparison;
        });
    Collections.reverse(rankedEvents);
    return rankedEvents;
  }

  private static Map<Event, Double> getEventScoreMap(User user, Set<Event> events) {
    List<Set<Pair<Event, Integer>>> allEvents =
        new GetLabelCategories().getEventRelevancy(events, user);
    Set<Pair<Event, Integer>> directEventPairs = allEvents.get(0);
    Set<Pair<Event, Integer>> similarEventPairs = allEvents.get(1);

    Map<Event, Double> eventScoreMap = new HashMap<>();
    for (Pair<Event, Integer> eventRelevancyPair : directEventPairs) {
      eventScoreMap.put(eventRelevancyPair.getValue0(), new Double(eventRelevancyPair.getValue1()));
    }
    for (Pair<Event, Integer> eventRelevancyPair : similarEventPairs) {
      eventScoreMap.put(eventRelevancyPair.getValue0(), 0.7 * eventRelevancyPair.getValue1());
    }
    return eventScoreMap;
  }
}

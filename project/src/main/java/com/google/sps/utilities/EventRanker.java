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
  private static final Double DIRECT_WEIGHT = 1.0;
  private static final Double SIMILAR_WEIGHT = 0.7;
  private static final Double NO_MATCH_WEIGHT = 0.0;

  /**
   * Sorts events based on each event's score. Breaks ties with the event's date; the more recent
   * event will be ranked higher. Makes the assumption that all events are NOT in the past.
   *
   * @param user The user for whom the events are ranked
   * @param events A {@link Set} of events that need to be ranked
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
    Pair<Set<Pair<Event, Integer>>, Set<Pair<Event, Integer>>> allEvents =
        new GetLabelCategories().getEventRelevancy(events, user);
    Map<Event, Double> eventScoreMap = new HashMap<>();
    Set<Event> directEvents = processEventMatches(eventScoreMap, allEvents.getValue0(), DIRECT_WEIGHT);
    Set<Event> similarEvents = processEventMatches(eventScoreMap, allEvents.getValue1(), SIMILAR_WEIGHT);
    for (Event event : getNoMatchEvents(events, directEvents, similarEvents)) {
      eventScoreMap.put(event, NO_MATCH_WEIGHT);
    }
    return eventScoreMap;
  }

  private static Set<Event> processEventMatches(
      Map<Event, Double> eventScoreMap, Set<Pair<Event, Integer>> eventPairs, Double weight) {
    Set<Event> events = new HashSet<>();
    for (Pair<Event, Integer> eventRelevancyPair : eventPairs) {
      Event event = eventRelevancyPair.getValue0();
      eventScoreMap.put(event, weight * eventRelevancyPair.getValue1());
      events.add(event);
    }
    return events;
  }

  private static Set<Event> getNoMatchEvents(Set<Event> events, Set<Event> directEvents, Set<Event> similarEvents) {
    Set<Event> noMatchEvents = new HashSet<>(events);
    noMatchEvents.removeAll(directEvents);
    noMatchEvents.removeAll(similarEvents);
    return noMatchEvents;
  }
}

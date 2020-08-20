package com.google.sps.utilities;

import com.google.common.collect.Sets;
import com.google.sps.data.Event;
import com.google.sps.data.User;
import com.google.sps.data.VolunteeringOpportunity;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class EventRanker {
  /**
   * Sorts events based on each event's score. Breaks ties with the event's date; the more recent
   * event will be ranked higher. Makes the assumption that all events are NOT in the past.
   *
   * @param user The user for whom the events are ranked
   * @param unrankedEvents A {@link Set} of events that need to be ranked
   * @return A {@link List} of events that is ranked according to each element's score
   */
  public static List<Event> rankEvents(User user, Set<Event> unrankedEvents) {
    List<Event> rankedEvents = new ArrayList<>(unrankedEvents);
    Collections.sort(
        rankedEvents,
        (Event event1, Event event2) -> {
          int comparison = getEventScore(user, event1).compareTo(getEventScore(user, event2));
          if (comparison == 0) {
            comparison = event2.getDate().compareTo(event1.getDate());
          }
          return comparison;
        });
    Collections.reverse(rankedEvents);
    return rankedEvents;
  }

  private static Integer getEventScore(User user, Event event) {
    Set<String> interestsAndSkills = user.getInterests();
    interestsAndSkills.addAll(user.getSkills());
    Set<String> labelsAndSkills = event.getLabels();
    for (VolunteeringOpportunity opportunity : event.getOpportunities()) {
      labelsAndSkills.addAll(opportunity.getRequiredSkills());
    }
    return Sets.intersection(interestsAndSkills, labelsAndSkills).size();
  }
}

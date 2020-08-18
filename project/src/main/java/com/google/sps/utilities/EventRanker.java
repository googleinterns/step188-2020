package com.google.sps.utilities;

import com.google.sps.data.Event;
import com.google.sps.data.User;
import com.google.sps.data.VolunteeringOpportunity;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class EventRanker {
  /**
   * Sorts events based on each event's score.
   * Breaks ties with the event's number of VolunteeringOpportunity
   * requiredSkills that match the user's skills.
   * 
   * @param user The user for whom the events are ranked
   * @param unrankedEvents A {@link Set} of events that need to be ranked
   * @return A {@link List} of events that is ranked according to
   * each element's score
   */
  public List<Event> rankEvents(User user, Set<Event> unrankedEvents) {
    return new ArrayList<>();
  }
}

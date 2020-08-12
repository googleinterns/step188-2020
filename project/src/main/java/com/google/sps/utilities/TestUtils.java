package com.google.sps.utilities;

import com.google.cloud.Date;
import com.google.sps.data.Event;
import com.google.sps.data.User;
import com.google.sps.data.VolunteeringOpportunity;
import com.google.sps.data.OpportunitySignup;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/* Class containing utilities for testing. */
public class TestUtils {
  private static final String NAME = "Bob Smith";
  private static final String EMAIL = "bobsmith@example.com";
  private static final String EVENT_NAME = "Team Meeting";
  private static final String DESCRIPTION = "Daily Team Sync";
  private static final Set<String> LABELS =
      Collections.unmodifiableSet(new HashSet<>(Arrays.asList("Tech", "Work")));
  private static final String LOCATION = "Remote";
  private static final Date DATE = Date.fromYearMonthDay(2016, 9, 15);
  private static final String DATE_STRING = "09/15/2016";
  private static final String TIME = "3:00PM-5:00PM";
  private static final User HOST = new User.Builder(NAME, EMAIL).build();
  private static final String EVENT_ID = "0883de79-17d7-49a3-a866-dbd5135062a8";
  private static final String OPPORTUNITY_NAME = "Performer";
  private static final int NUMBER_OF_SPOTS = 240;
  private static final String VOLUNTEER_EMAIL = "volunteer@gmail.com";

  public static User newUser() {
    return new User.Builder(NAME, EMAIL).build(); 
  }

  public static Event newEvent() {
    return new Event.Builder(EVENT_NAME, DESCRIPTION, LABELS, LOCATION, DATE, TIME, HOST).build();
  }

  public static VolunteeringOpportunity newVolunteeringOpportunity() {
    return new VolunteeringOpportunity.Builder(EVENT_ID, OPPORTUNITY_NAME, NUMBER_OF_SPOTS).build();
  }

  /*
   * Return a new volunteering opportunity object with the given eventId.
   * @param eventId
   */
  public static VolunteeringOpportunity newVolunteeringOpportunityWithEventId(String eventId) {
    return new VolunteeringOpportunity.Builder(eventId, NAME, NUMBER_OF_SPOTS).build();
  }

  /*
   * Return a new opportunitySignup object with the given opportunityId.
   * @param opportunityId
   */
  public static OpportunitySignup newOpportunitySignupWithOpportunityId(String opportunityId) {
    return new OpportunitySignup.Builder(opportunityId, VOLUNTEER_EMAIL).build();
  }
 
  public static String newRandomId() {
    return UUID.randomUUID().toString();
  }
}

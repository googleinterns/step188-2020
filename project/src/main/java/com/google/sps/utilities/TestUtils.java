package com.google.sps.utilities;

import com.google.cloud.Date;
import com.google.sps.data.Event;
import com.google.sps.data.OpportunitySignup;
import com.google.sps.data.User;
import com.google.sps.data.VolunteeringOpportunity;
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
  private static final Set<String> INTERESTS =
      Collections.unmodifiableSet(new HashSet<>(Arrays.asList("Conservation", "Food")));
  private static final Set<String> SKILLS =
      Collections.unmodifiableSet(new HashSet<>(Arrays.asList("Cooking")));

  /*
   * Returns a new Event object with arbitrary parameters.
   */
  public static Event newEvent() {
    return new Event.Builder(
            EVENT_NAME,
            DESCRIPTION,
            LABELS,
            LOCATION,
            DATE,
            TIME,
            new User.Builder(NAME, EMAIL).build())
        .build();
  }

  /*
   * Returns a new Event object with given parameters and performs the required
   * setup for the event host.
   * @param eventId eventId to be used to create Event object
   * @param name name to be used to create Event object
   * @param description description to be used to create Event object
   * @return an event with given attributes and host inserted into database
   */
  public static Event newEvent(String eventId, String name, String description) {
    User host = newUser();
    SpannerTasks.insertOrUpdateUser(host);
    return new Event.Builder(name, description, LABELS, LOCATION, DATE, TIME, host)
        .setId(eventId)
            .build();
  }

  /*
   * Returns a new Event object with the given user as host.
   * @param user host to be used to create a Event object
   * @return an event with given user as host
   */
  public static Event newEventWithHost(User user) {
    return new Event.Builder(EVENT_NAME, DESCRIPTION, LABELS, LOCATION, DATE, TIME, user).build();
  }

  /** Returns a new VolunteeringOpportunity object with arbitrary parameters. */
  public static VolunteeringOpportunity newVolunteeringOpportunity() {
    return new VolunteeringOpportunity.Builder(EVENT_ID, OPPORTUNITY_NAME, NUMBER_OF_SPOTS).build();
  }

  /*
   * Returns a new VolunteeringOpportunity object with the given eventId.
   * @param eventId event ID used to create a VolunteeringOpportunity object
   * @return a volunteering opportunity with the given event ID
   */
  public static VolunteeringOpportunity newVolunteeringOpportunityWithEventId(String eventId) {
    return new VolunteeringOpportunity.Builder(eventId, OPPORTUNITY_NAME, NUMBER_OF_SPOTS).build();
  }

  /*
   * Returns a new OpportunitySignup object with the given opportunityId.
   * @param opportunityId opportunity ID to create an OpportunitySignup object
   * @return an opportunity signup with given opportunity ID
   */
  public static OpportunitySignup newOpportunitySignupWithOpportunityId(String opportunityId) {
    return new OpportunitySignup.Builder(opportunityId, VOLUNTEER_EMAIL).build();
  }

  /** Returns a new User object with arbitrary attributes. */
  public static User newUser() {
    return new User.Builder(NAME, EMAIL).setInterests(INTERESTS).setSkills(SKILLS).build();
  }

  /** Returns a new User object with arbitrary attributes and given email. */
  public static User newUserWithEmail(String email) {
    return new User.Builder(NAME, email).setInterests(INTERESTS).setSkills(SKILLS).build();
  }

  /** Returns a random ID. */
  public static String newRandomId() {
    return UUID.randomUUID().toString();
  }
}

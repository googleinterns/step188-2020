package com.google.sps.utilities;

import static com.google.cloud.spanner.TransactionRunner.TransactionCallable;

import com.google.appengine.api.users.UserServiceFactory;
import com.google.cloud.Date;
import com.google.cloud.spanner.Key;
import com.google.cloud.spanner.Mutation;
import com.google.cloud.spanner.ResultSet;
import com.google.cloud.spanner.Statement;
import com.google.cloud.spanner.Struct;
import com.google.cloud.spanner.TransactionContext;
import com.google.sps.data.Event;
import com.google.sps.data.EventVolunteering;
import com.google.sps.data.OpportunitySignup;
import com.google.sps.data.User;
import com.google.sps.data.VolunteeringOpportunity;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/** Class containing methods for interaction with database. */
public class SpannerTasks {
  private static final String USER_TABLE = "Users";
  private static final String VOLUNTEERING_OPPORTUNITY_TABLE = "VolunteeringOpportunity";
  private static final String EVENT_TABLE = "Events";
  private static final String OPPORTUNITY_SIGNUP_TABLE = "OpportunitySignup";
  private static final String OPPORTUNITY_ID = "VolunteeringOpportunityID";
  private static final String EVENT_ID = "EventID";
  private static final String NAME = "Name";
  private static final String EMAIL = "Email";
  private static final String NUM_SPOTS_LEFT = "NumSpotsLeft";
  private static final String REQUIRED_SKILLS = "RequiredSkills";

  /**
   * Get current loggedin User Optional
   */
  public static Optional<User> getLoggedInUser() {
    String email = UserServiceFactory.getUserService().getCurrentUser().getEmail();
    return shallowReadUserFromEmail(email);
  }

  /**
   * Given a user, insert or update a row with all available fields into the DB
   *
   * @param user the user to be updated; user's email may or may not exist in DB
   */
  public static void insertOrUpdateUser(User user) {
    // Given a user, update its corresponding row's new fields in DB

    List<Mutation> mutations =
        getUserMutationsFromBuilder(Mutation.newInsertOrUpdateBuilder(USER_TABLE), user);
    SpannerClient.getDatabaseClient().write(mutations);
  }

  /**
   * Given an email, return the corresponding user from the DB WITHOUT attached events
   *
   * @param email an email to search the 'User' table by; email may or may not exist in DB
   * @return return the user wrapped in an {@link Optional}
   */
  public static Optional<User> shallowReadUserFromEmail(String email) {
    ResultSet resultSet =
        SpannerClient.getDatabaseClient()
            .singleUse()
            .executeQuery(
                Statement.of(
                    String.format(
                        "SELECT Name, Interests, Skills, Image FROM %s WHERE Email='%s'",
                        USER_TABLE, email)));

    if (!resultSet.next()) {
      return Optional.empty();
    }

    return Optional.of(
        new User.Builder(/* name = */ resultSet.getString(0), /* email = */ email)
            .setInterests(new HashSet<String>(resultSet.getStringList(1)))
            .setSkills(new HashSet<String>(resultSet.getStringList(2)))
            .setImageUrl(resultSet.getString(3))
            .build());
  }

  /**
   * Given a set of emails, return the corresponding users from the DB
   *
   * @param emails emails to search the 'User' table by
   * @return return the users that exist in no particular order
   */
  public static Set<User> shallowReadMultipleUsersFromEmails(Set<String> emails) {
    Set<User> users = new HashSet<>();
    for (String email : emails) {
      Optional<User> userOptional = shallowReadUserFromEmail(email);
      if (userOptional.isPresent()) {
        users.add(userOptional.get());
      }
    }
    return users;
  }

  /**
   * Given an event, insert or update a row with all available fields into the DB
   *
   * @param event the event to be inserted or updated; event's ID field should not exist in DB
   */
  public static void insertorUpdateEvent(Event event) {
    List<Mutation> mutations =
        getEventMutationsFromBuilder(Mutation.newInsertOrUpdateBuilder(EVENT_TABLE), event);
    SpannerClient.getDatabaseClient().write(mutations);
  }

  /**
   * Returns from DB all available events that match with provided list of IDs
   *
   * @param eventIds List of IDs of event to be returned
   */
  public static Set<Event> getEventsFromIds(List<String> eventIds) {
    Set<Event> events = new HashSet<Event>();
    String eventIdsFormatted = formatMultipleValuesForQuery(eventIds);
    ResultSet resultSet =
        SpannerClient.getDatabaseClient()
            .singleUse()
            .executeQuery(
                Statement.of(
                    String.format(
                        "SELECT EventID, Name, Description, Labels, Location, Date, Time,"
                            + " Host, Opportunities, Attendees FROM %s WHERE EventID in (%s)"
                            + " AND DATE_DIFF(Date, CURRENT_DATE(), DAY) > 0",
                        EVENT_TABLE, eventIdsFormatted)));
    while (resultSet.next()) {
      events.add(shallowCreateEventFromDatabaseResult(resultSet));
    }
    return events;
  }

  private static String formatMultipleValuesForQuery(List<String> values) {
    return values
        .stream()
        .map(value -> String.format("'%s'", value))
        .collect(Collectors.joining( "," ));
  }

  /**
   * Returns Event by ID from DB
   *
   * @param eventId ID of event to be returned
   */
  public static Optional<Event> getEventById(String eventId) {
    ResultSet resultSet =
        SpannerClient.getDatabaseClient()
            .singleUse()
            .executeQuery(
                Statement.of(
                    String.format(
                        "SELECT EventId, Name, Description, Labels, Location, Date, Time, Host,"
                            + " Opportunities, Attendees FROM %s WHERE EventID='%s'"
                            + " AND DATE_DIFF(Date, CURRENT_DATE(), DAY) > 0",
                        EVENT_TABLE, eventId)));

    /** If ID does not exist */
    if (!resultSet.next()) {
      return Optional.empty();
    }
    return Optional.of(shallowCreateEventFromDatabaseResult(resultSet));
  }

  /**
   * Returns all events stored in DB; events will be shallow copies, so corresponding users will not
   * have their events attached
   *
   * @return Events with a shallow version of its host (no events attached to Users)
   */
  public static Set<Event> getAllEvents() {
    Set<Event> events = new HashSet<>();
    ResultSet resultSet =
        SpannerClient.getDatabaseClient()
            .singleUse()
            .executeQuery(
                Statement.of(
                    String.format(
                        "SELECT EventID, Name, Description, Labels, Location, Date, Time,"
                            + " Host, Opportunities, Attendees FROM %s WHERE"
                            + " DATE_DIFF(Date, CURRENT_DATE(), DAY) > 0",
                        EVENT_TABLE)));
    while (resultSet.next()) {
      Event event = shallowCreateEventFromDatabaseResult(resultSet);
      events.add(event);
    }
    return events;
  }

  private static Event shallowCreateEventFromDatabaseResult(ResultSet resultSet) {
    String eventId = resultSet.getString(0);
    return new Event.Builder(
            /* name = */ resultSet.getString(1),
            /* description = */ resultSet.getString(2),
            /* labels = */ new HashSet<String>(resultSet.getStringList(3)),
            /* location = */ resultSet.getString(4),
            /* date = */ resultSet.getDate(5),
            /* time = */ resultSet.getString(6),
            /* host = */ shallowReadUserFromEmail(resultSet.getString(7)).get())
        .setId(eventId)
        .setLabels(new HashSet<String>(resultSet.getStringList(3)))
        .setOpportunities(getVolunteeringOpportunitiesByEventId(eventId))
        .setAttendees(
            shallowReadMultipleUsersFromEmails(new HashSet<String>(resultSet.getStringList(9))))
        .build();
  }

  private static List<Mutation> getUserMutationsFromBuilder(
      Mutation.WriteBuilder builder, User user) {
    List<Mutation> mutations = new ArrayList<>();
    builder
        .set("Name")
        .to(user.getName())
        .set("Email")
        .to(user.getEmail())
        .set("Interests")
        .toStringArray(user.getInterests())
        .set("Skills")
        .toStringArray(user.getSkills())
        .set("EventsHosting")
        .toStringArray(user.getEventsHostingIds())
        .set("EventsParticipating")
        .toStringArray(user.getEventsParticipatingIds())
        .set("EventsVolunteering")
        .toStringArray(user.getEventsVolunteeringIds())
        .set("Image")
        .to(user.getImageUrl());
    mutations.add(builder.build());
    return mutations;
  }

  /** TO DO: Attendee and Opportunity String Array based on UUID in other PRs */
  private static List<Mutation> getEventMutationsFromBuilder(
      Mutation.WriteBuilder builder, Event event) {
    List<Mutation> mutations = new ArrayList<>();
    builder
        .set("EventId")
        .to(event.getId())
        .set("Name")
        .to(event.getName())
        .set("Description")
        .to(event.getDescription())
        .set("Labels")
        .toStringArray(event.getLabels())
        .set("Location")
        .to(event.getLocation())
        .set("Date")
        .to(event.getDate())
        .set("Time")
        .to(event.getTime())
        .set("Host")
        .to(event.getHost().getEmail())
        .set("Opportunities")
        .toStringArray(event.getOpportunitiesIds())
        .set("Attendees")
        .toStringArray(event.getAttendeeIds());
    mutations.add(builder.build());
    return mutations;
  }

  /**
   * Given a volunteering opportunity, insert a row with all available fields into the DB
   *
   * @param opportunity the volunteering opportunity to be inserted
   */
  public static void insertVolunteeringOpportunity(VolunteeringOpportunity opportunity) {
    List<Mutation> mutations =
        getMutationsFromBuilder(newInsertBuilderFromVolunteeringOpportunity(), opportunity);
    SpannerClient.getDatabaseClient().write(mutations);
  }

  /**
   * Given a volunteering opportunity, update a row with the same opportunityId in the DB
   *
   * @param opportunity the volunteering opportunity to be updated
   */
  public static void updateVolunteeringOpportunity(VolunteeringOpportunity opportunity) {
    List<Mutation> mutations =
        getMutationsFromBuilder(newUpdateBuilderFromVolunteeringOpportunity(), opportunity);
    SpannerClient.getDatabaseClient().write(mutations);
  }

  private static Mutation.WriteBuilder newInsertBuilderFromVolunteeringOpportunity() {
    return Mutation.newInsertBuilder(VOLUNTEERING_OPPORTUNITY_TABLE);
  }

  private static Mutation.WriteBuilder newUpdateBuilderFromVolunteeringOpportunity() {
    return Mutation.newUpdateBuilder(VOLUNTEERING_OPPORTUNITY_TABLE);
  }

  private static List<Mutation> getMutationsFromBuilder(
      Mutation.WriteBuilder builder, VolunteeringOpportunity opportunity) {
    List<Mutation> mutations = new ArrayList<>();
    builder
        .set(OPPORTUNITY_ID)
        .to(opportunity.getOpportunityId())
        .set(EVENT_ID)
        .to(opportunity.getEventId())
        .set(NAME)
        .to(opportunity.getName())
        .set(NUM_SPOTS_LEFT)
        .to(opportunity.getNumSpotsLeft())
        .set(REQUIRED_SKILLS)
        .toStringArray(opportunity.getRequiredSkills());
    mutations.add(builder.build());
    return mutations;
  }

  /**
   * Given an opportunityId, retrieve the corresponding volunteering opportunity
   *
   * @param opportunityId opportunityId of the opportunity to retrieve
   * @return volunteering opportunity wrapped in a {@link Optional}
   */
  public static Optional<VolunteeringOpportunity> getVolunteeringOpportunityByOppportunityId(
      String opportunityId) {
    Optional<VolunteeringOpportunity> result = Optional.empty();
    Statement statement =
        Statement.of(
            String.format(
                "SELECT EventID, Name, NumSpotsLeft, RequiredSkills FROM"
                    + " VolunteeringOpportunity WHERE VolunteeringOpportunityID=\"%s\"",
                opportunityId));
    try (ResultSet resultSet =
        SpannerClient.getDatabaseClient().singleUse().executeQuery(statement)) {
      if (resultSet.next()) {
        String eventId = resultSet.getString(0);
        String name = resultSet.getString(1);
        long numSpotsLeft = resultSet.getLong(2);
        Set<String> requiredSkills =
            resultSet.getStringList(3).stream().collect(Collectors.toSet());
        result =
            Optional.of(
                new VolunteeringOpportunity.Builder(eventId, name, numSpotsLeft)
                    .setOpportunityId(opportunityId)
                    .setRequiredSkills(requiredSkills)
                    .build());
      }
    }
    return result;
  }

  /**
   * Given an eventId, retrieve all volunteering opportunities for that eventId
   *
   * @param eventId eventId for the event to retrieve volunteering opportunities for
   * @return volunteering opportunities with given eventId
   */
  public static Set<VolunteeringOpportunity> getVolunteeringOpportunitiesByEventId(String eventId) {
    Set<VolunteeringOpportunity> results = new HashSet<VolunteeringOpportunity>();
    Statement statement =
        Statement.of(
            String.format(
                "SELECT VolunteeringOpportunityID, Name, NumSpotsLeft, RequiredSkills FROM"
                    + " VolunteeringOpportunity WHERE EventID=\"%s\"",
                eventId));
    try (ResultSet resultSet =
        SpannerClient.getDatabaseClient().singleUse().executeQuery(statement)) {
      while (resultSet.next()) {
        String opportunityId = resultSet.getString(0);
        String name = resultSet.getString(1);
        long numSpotsLeft = resultSet.getLong(2);
        Set<String> requiredSkills =
            resultSet.getStringList(3).stream().collect(Collectors.toSet());
        results.add(
            new VolunteeringOpportunity.Builder(eventId, name, numSpotsLeft)
                .setOpportunityId(opportunityId)
                .setRequiredSkills(requiredSkills)
                .build());
      }
    }
    return results;
  }

  private static List<Mutation> getMutationsFromBuilder(
      Mutation.WriteBuilder builder, OpportunitySignup signup) {
    List<Mutation> mutations = new ArrayList<>();
    builder.set(OPPORTUNITY_ID).to(signup.getOpportunityId()).set(EMAIL).to(signup.getEmail());
    mutations.add(builder.build());
    return mutations;
  }

  /**
   * Given a signup, insert a row with all available fields into the database
   * and decrement the number of spots for the corresponding volunteering 
   * opportunity in the database.
   *
   * @param signup the signup to be inserted
   */
  public static void insertOpportunitySignup(OpportunitySignup signup) {
    SpannerClient.getDatabaseClient()
        .readWriteTransaction()
        .run(
            new TransactionCallable<Void>() {
              @Override
              public Void run(TransactionContext transaction) throws Exception {
                Struct row =
                    transaction.readRow(
                        VOLUNTEERING_OPPORTUNITY_TABLE,
                        Key.of(signup.getOpportunityId()),
                        Arrays.asList(NUM_SPOTS_LEFT));
                long numSpotsLeft = row.getLong(0);
                if (numSpotsLeft > 0) {
                  List<Mutation> signupMutations =
                      getMutationsFromBuilder(newInsertBuilderFromOpportunitySignup(), signup);
                  transaction.buffer(signupMutations);

                  numSpotsLeft--;
                  transaction.buffer(
                      Mutation.newUpdateBuilder(VOLUNTEERING_OPPORTUNITY_TABLE)
                          .set(OPPORTUNITY_ID)
                          .to(signup.getOpportunityId())
                          .set(NUM_SPOTS_LEFT)
                          .to(numSpotsLeft)
                          .build());
                }
                return null;
              }
            });
  }

  /**
   * Given an opportunityId, retrieve all signups for that opportunityId.
   *
   * @param opportunityId opportunityId for the opportunity to retrieve signups for
   * @return signups with given opportunityId
   */
  public static Set<OpportunitySignup> getSignupsByOpportunityId(String opportunityId) {
    Set<OpportunitySignup> results = new HashSet<OpportunitySignup>();
    Statement statement =
        Statement.of(
            String.format(
                "SELECT Email FROM OpportunitySignup WHERE VolunteeringOpportunityID=\"%s\"",
                opportunityId));
    try (ResultSet resultSet =
        SpannerClient.getDatabaseClient().singleUse().executeQuery(statement)) {
      while (resultSet.next()) {
        String email = resultSet.getString(0);
        results.add(new OpportunitySignup.Builder(opportunityId, email).build());
      }
    }
    return results;
  }

  private static Mutation.WriteBuilder newInsertBuilderFromOpportunitySignup() {
    return Mutation.newInsertBuilder(OPPORTUNITY_SIGNUP_TABLE);
  }

  /**
   * Given filters for events, return events whose labels match those filters
   *
   * @param labelParams Labels selected by user in frontend
   * @return events that match labels
  */
  public static Set<Event> getFilteredEvents(String[] labelParams) {
    Set<Event> results = new HashSet<Event>();
    for (String label : labelParams) {
      Statement statement =
          Statement.of(
              String.format(
                  "SELECT EventID, Name, Description, Labels, Location, Date, Time, Host, Attendees"
                      + " FROM %s WHERE \"%s\" IN UNNEST(Labels)"
                      + " AND DATE_DIFF(Date, CURRENT_DATE(), DAY) > 0",
                      EVENT_TABLE, label));
      try (ResultSet resultSet =
          SpannerClient.getDatabaseClient().singleUse().executeQuery(statement)) {
        while (resultSet.next()) {
          Event event =
              new Event.Builder(
                      /* name = */ resultSet.getString(1),
                      /* description = */ resultSet.getString(2),
                      /* labels = */ new HashSet<String>(resultSet.getStringList(3)),
                      /* location = */ resultSet.getString(4),
                      /* date = */ resultSet.getDate(5),
                      /* time = */ resultSet.getString(6),
                      /* host = */ shallowReadUserFromEmail(resultSet.getString(7)).get())
                  .setId(resultSet.getString(0))
                  .setLabels(new HashSet<String>(resultSet.getStringList(3)))
                  .setAttendees(
                      shallowReadMultipleUsersFromEmails(
                          new HashSet<String>(resultSet.getStringList(8))))
                  .build();
            results.add(event);
          }
        }
      }
      return results;
    } 

   /*
   * Given an email, retrieve all events for which the user with the email is
   * hosting.
   *
   * @param email email for the user to retrieve events hosting
   * @return events where the user is host
   */
  public static Set<Event> getEventsHostingByEmail(String email) {
    Set<Event> results = new HashSet<>();
    ResultSet resultSet =
        SpannerClient.getDatabaseClient()
            .singleUse()
            .executeQuery(
                Statement.of(
                    String.format(
                        "SELECT Events.EventID, Events.Name, Events.Description, Events.Labels,"
                            + " Events.Location, Events.Date, Events.Time, Events.Host"
                            + " FROM Events INNER JOIN"
                            + " Users ON Events.EventID IN UNNEST(Users.EventsHosting) WHERE Email=\"%s\"",
                        email)));
    while (resultSet.next()) {
      Event event =
          new Event.Builder(
                  /* name = */ resultSet.getString(1),
                  /* description = */ resultSet.getString(2),
                  /* labels = */ new HashSet<String>(resultSet.getStringList(3)),
                  /* location = */ resultSet.getString(4),
                  /* date = */ resultSet.getDate(5),
                  /* time = */ resultSet.getString(6),
                  /* host = */ shallowReadUserFromEmail(resultSet.getString(7)).get())
              .setId(resultSet.getString(0))
              .build();
      results.add(event);
    }
    return results;
  }

  /**
   * Given an email retrieve all events for which the user with the email is 
   * volunteering for.
   *
   * @param email email for the user to retrieve events volunteering for
   * @return events where the user is volunteering
  */
  public static Set<EventVolunteering> getEventsVolunteeringByEmail(String email) {
    Set<EventVolunteering> results = new HashSet<EventVolunteering>();
    Statement statement =
        Statement.of(
            String.format(
                "SELECT Events.EventID, Events.Name, Events.Description, Events.Labels,"
                    + " Events.Location, Events.Date, Events.Time, Events.Host,"
                    + " VolunteeringOpportunity.Name FROM Events INNER JOIN"
                    + " VolunteeringOpportunity ON Events.EventID ="
                    + " VolunteeringOpportunity.EventID INNER JOIN OpportunitySignup ON"
                    + " VolunteeringOpportunity.VolunteeringOpportunityID ="
                    + " OpportunitySignup.VolunteeringOpportunityID WHERE Email=\"%s\"",
                email));
    try (ResultSet resultSet =
        SpannerClient.getDatabaseClient().singleUse().executeQuery(statement)) {
      while (resultSet.next()) {
        Event event =
            new Event.Builder(
                    /* name = */ resultSet.getString(1),
                    /* description = */ resultSet.getString(2),
                    /* labels = */ new HashSet<String>(resultSet.getStringList(3)),
                    /* location = */ resultSet.getString(4),
                    /* date = */ resultSet.getDate(5),
                    /* time = */ resultSet.getString(6),
                    /* host = */ shallowReadUserFromEmail(resultSet.getString(7)).get())
                .setId(resultSet.getString(0))
                .build();
        results.add(new EventVolunteering(event, /* opportunityName = */ resultSet.getString(8)));
      }
    }
    return results;
  }

  /**
   * Given a user email retrieve all events that the user is 
   * participating in by querying events that the user is an attendee of.
   *
   * @param email of loggedIn user email
   * @return events where the user is participating
  */
  public static Set<Event> getEventsParticipatingByEmail(String email) {
    Set<Event> results = new HashSet<Event>();
    Statement statement = Statement.of(
        String.format(
            "SELECT EventID, Name, Description, Labels, Location, Date, Time, Host, Attendees"
            + " FROM %s WHERE \"%s\" IN UNNEST(Attendees)",
                EVENT_TABLE, email ));
    try (ResultSet resultSet =
        SpannerClient.getDatabaseClient().singleUse().executeQuery(statement)) {
      while (resultSet.next()) {
        Event event =
            new Event.Builder(
                    /* name = */ resultSet.getString(1),
                    /* description = */ resultSet.getString(2),
                    /* labels = */ new HashSet<String>(resultSet.getStringList(3)),
                    /* location = */ resultSet.getString(4),
                    /* date = */ resultSet.getDate(5),
                    /* time = */ resultSet.getString(6),
                    /* host = */ shallowReadUserFromEmail(resultSet.getString(7)).get())
                .setId(resultSet.getString(0))
                .setAttendees(shallowReadMultipleUsersFromEmails(new HashSet<String>(resultSet.getStringList(8))))
                .build();
        results.add(event);
      }
    }
    return results;
  }
}

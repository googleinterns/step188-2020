package com.google.sps.utilities;

import com.google.cloud.spanner.Mutation;
import com.google.cloud.spanner.ResultSet;
import com.google.cloud.spanner.Statement;
import com.google.sps.data.Event;
import com.google.sps.data.User;
import com.google.sps.data.VolunteeringOpportunity;
import java.util.ArrayList;
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
                        "SELECT Name, Interests, Skills, EventsHosting, EventsParticipating,"
                            + " EventsVolunteering FROM %s WHERE Email='%s'",
                        USER_TABLE, email)));

    if (!resultSet.next()) {
      return Optional.empty();
    }

    return Optional.of(
        new User.Builder(/* name = */ resultSet.getString(0), /* email = */ email)
            .setInterests(new HashSet<String>(resultSet.getStringList(1)))
            .setSkills(new HashSet<String>(resultSet.getStringList(2)))
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
   * Returns List of Event Ids from DB
   *
   * @param eventId List of IDs of event to be returned
   */
  public static Set<Event> getEventsFromIds(List<String> eventIds) {
    Set<Event> ids = new HashSet<Event>();
    for (String eventId : eventIds) {
      Optional<Event> event = getEventById(eventId);
      if (event.isPresent()) {
        ids.add(event.get());
      }
    }
    return ids;
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
                            + " Opportunities, Attendees FROM %s WHERE EventID='%s'",
                        EVENT_TABLE, eventId)));

    /** If ID does not exist */
    if (!resultSet.next()) {
      return Optional.empty();
    }
    return Optional.of(shallowCreateEventFromDatabaseResult(resultSet));
  }

  /**
   * Returns all events stored in DB
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
                        "SELECT EventID, Name, Description, Labels, Location, Date, Host,"
                            + " Opportunities, Attendees, Time FROM %s",
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
            /* time = */ resultSet.getString(9),
            /* host = */ shallowReadUserFromEmail(resultSet.getString(6)).get())
        .setId(eventId)
        .setOpportunities(getVolunteeringOpportunitiesByEventId(eventId))
        .setAttendees(
            shallowReadMultipleUsersFromEmails(new HashSet<String>(resultSet.getStringList(7))))
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
        .toStringArray(user.getEventsVolunteeringIds());
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
        .set("VolunteeringOpportunityID")
        .to(opportunity.getOpportunityId())
        .set("EventID")
        .to(opportunity.getEventId())
        .set("Name")
        .to(opportunity.getName())
        .set("NumSpotsLeft")
        .to(opportunity.getNumSpotsLeft())
        .set("RequiredSkills")
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
}

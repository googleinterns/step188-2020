package com.google.sps.utilities;

import com.google.cloud.Date;
import com.google.cloud.spanner.DatabaseClient;
import com.google.cloud.spanner.DatabaseId;
import com.google.cloud.spanner.Mutation;
import com.google.cloud.spanner.ResultSet;
import com.google.cloud.spanner.Spanner;
import com.google.cloud.spanner.SpannerOptions;
import com.google.cloud.spanner.Statement;
import com.google.cloud.spanner.Struct;
import com.google.sps.data.Event;
import com.google.sps.data.User;
import com.google.sps.data.VolunteeringOpportunity;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class DatabaseWrapper {
  private String instanceId;
  private String databaseId;
  private static final String USER_TABLE = "Users";
  private static final String VOLUNTEERING_OPPORTUNITY_TABLE = "VolunteeringOpportunity";
  private static final String EVENT_TABLE = "Events";
  //TO DO: replace with db injection on PR #57
  private static final SpannerOptions options = SpannerOptions.newBuilder().build();
  private static final Spanner spanner = options.getService();
  private final DatabaseId db = DatabaseId.of(options.getProjectId(), instanceId, databaseId);
  private final DatabaseClient dbClient = spanner.getDatabaseClient(db);

  public DatabaseWrapper(String instanceId, String databaseId) {
    this.instanceId = instanceId;
    this.databaseId = databaseId;
  }

  /**
   * Given a user, insert or update a row with all available fields into the DB
   *
   * @param user the user to be updated; user's email may or may not exist in DB
   */
  public void insertOrUpdateUser(User user) {
    // Given a user, update its corresponding row's new fields in DB
    SpannerOptions options = SpannerOptions.newBuilder().build();
    Spanner spanner = options.getService();
    DatabaseId db = DatabaseId.of(options.getProjectId(), instanceId, databaseId);
    DatabaseClient dbClient = spanner.getDatabaseClient(db);

    List<Mutation> mutations =
        getUserMutationsFromBuilder(Mutation.newInsertOrUpdateBuilder(USER_TABLE), user);
    dbClient.write(mutations);
    spanner.close();
  }

  /**
   * Given an email, return the corresponding user from the DB
   *
   * @param email an email to search the 'User' table by; email may or may not exist in DB
   * @return return the user wrapped in an {@link Optional}
   */
  public Optional<User> readUserFromEmail(String email) {
    SpannerOptions options = SpannerOptions.newBuilder().build();
    Spanner spanner = options.getService();
    DatabaseId db = DatabaseId.of(options.getProjectId(), instanceId, databaseId);
    DatabaseClient dbClient = spanner.getDatabaseClient(db);
    ResultSet resultSet =
        dbClient
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
            .setEventsHosting(getEventsFromIds(resultSet.getStringList(3)))
            .setEventsParticipating(getEventsFromIds(resultSet.getStringList(4)))
            .setEventsVolunteering(getEventsFromIds(resultSet.getStringList(5)))
            .build());
  }

  /**
   * Given a set of emails, return the corresponding users from the DB
   *
   * @param emails emails to search the 'User' table by
   * @return return the users that exist in no particular order
   */
  public Set<User> readMultipleUsersFromEmails(Set<String> emails) {
    Set<User> users = new HashSet<>();
    for (String email : emails) {
      Optional<User> userOptional = readUserFromEmail(email);
      if (userOptional.isPresent()) {
        user.add(userOptional.get());
      }
    }
    return users;
  }

  /**
   * Given an event, insert or update a row with all available fields into the DB
   *
   * @param event the event to be inserted or updated; event's ID field should not exist in DB
   */
  public void insertorUpdateEvent(Event event) {
    List<Mutation> mutations =
        getEventMutationsFromBuilder(Mutation.newInsertOrUpdateBuilder(EVENT_TABLE), event);
    dbClient.write(mutations);
    spanner.close();
  }

  /** Returns List of Event Ids from DB
   * 
   * @param eventId List of IDs of event to be returned
  */
  public Set<Event> getEventsFromIds(List<String> eventIds) {
      Set<Event> ids = new HashSet<Event>();
      for (String eventId: eventIds) {
        Optional<Event> event = getEventById(eventId);
        if (event.isPresent()) {
            ids.add( event.get() );
        }
      }
      return ids;
  }

  /** Returns Event by ID from DB
   * 
   * @param eventId ID of event to be returned
  */
  public Optional<Event> getEventById(String eventId) {
    ResultSet resultSet = dbClient.singleUse().executeQuery(Statement.of(String.format(
        "SELECT EventID, Name, Description, Labels, Location, Date, Host, Opportunities, Attendees FROM %s WHERE EventID='%s'",
        EVENT_TABLE, eventId)));    
    /** If ID does not exist */
    if (!resultSet.next()) {
      return Optional.empty();
    }
    return Optional.of(createEventFromDatabaseResult(resultSet));
  }

  /**
   * Returns all events stored in DB
   */
  public Set<Event> getAllEvents() {
    Set<Event> events = new HashSet<>();
    ResultSet resultSet = dbClient.singleUse().executeQuery(Statement.of(String.format(
        "SELECT EventID, Name, Description, Labels, Location, Date, Host, Opportunities, Attendees FROM %s", EVENT_TABLE)));
    while (resultSet.next()) {
      Event event = createEventFromDatabaseResult(resultSet);
      events.add(event);
    }
    return events;
  }
  
  private static Event createEventFromDatabaseResult(ResultSet resultSet) {
    String eventId = resultSet.getString(0);
    return new Event
              .Builder(/* name = */ resultSet.getString(1),
                  /* description = */ resultSet.getString(2),
                  /* labels = */ new HashSet<String>(resultSet.getStringList(3)),
                  /* location = */ resultSet.getString(4), 
                  /* date = */ resultSet.getDate(5),
                  /* host = */ readUserFromEmail(resultSet.getString(6)).get())
              .setId(eventId)
              .setOpportunities(getVolunteeringOpportunityByEventId(eventId))
              .setAttendees(readMultipleUsersFromEmails(new HashSet<String>(resultSet.getStringList(7))))
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

  /** TO DO: Attendee and Opportunity String Array based on UUID in other PRs*/
  private static List<Mutation> getEventMutationsFromBuilder(
      Mutation.WriteBuilder builder, Event event) {
    List<Mutation> mutations = new ArrayList<>();
    builder.set("EventId")
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
  public void insertVolunteeringOpportunity(VolunteeringOpportunity opportunity) {
    SpannerOptions options = SpannerOptions.newBuilder().build();
    Spanner spanner = options.getService();
    DatabaseId db = DatabaseId.of(options.getProjectId(), instanceId, databaseId);
    DatabaseClient dbClient = spanner.getDatabaseClient(db);

    List<Mutation> mutations =
        getMutationsFromBuilder(newInsertBuilderFromVolunteeringOpportunity(), opportunity);
    dbClient.write(mutations);
    spanner.close();
  }

  /**
   * Given a volunteering opportunity, update a row with the same opportunityId in the DB
   *
   * @param opportunity the volunteering opportunity to be updated
   */
  public void updateVolunteeringOpportunity(VolunteeringOpportunity opportunity) {
    SpannerOptions options = SpannerOptions.newBuilder().build();
    Spanner spanner = options.getService();
    DatabaseId db = DatabaseId.of(options.getProjectId(), instanceId, databaseId);
    DatabaseClient dbClient = spanner.getDatabaseClient(db);

    List<Mutation> mutations =
        getMutationsFromBuilder(newUpdateBuilderFromVolunteeringOpportunity(), opportunity);
    dbClient.write(mutations);
    spanner.close();
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
  public Optional<VolunteeringOpportunity> getVolunteeringOpportunityByOppportunityId(
      String opportunityId) {
    SpannerOptions options = SpannerOptions.newBuilder().build();
    Spanner spanner = options.getService();
    DatabaseId db = DatabaseId.of(options.getProjectId(), instanceId, databaseId);
    DatabaseClient dbClient = spanner.getDatabaseClient(db);

    Optional<VolunteeringOpportunity> result = Optional.empty();
    Statement statement =
        Statement.of(
            String.format(
                "SELECT EventID, Name, NumSpotsLeft, RequiredSkills FROM"
                    + " VolunteeringOpportunity WHERE VolunteeringOpportunityID=\"%s\"",
                opportunityId));
    try (ResultSet resultSet = dbClient.singleUse().executeQuery(statement)) {
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
    spanner.close();
    return result;
  }

  /**
   * Given an eventId, retrieve all volunteering opportunities for that eventId
   *
   * @param eventId eventId for the event to retrieve volunteering opportunities for
   * @return volunteering opportunities with given eventId
   */
  public Set<VolunteeringOpportunity> getVolunteeringOpportunitiesByEventId(String eventId) {
    SpannerOptions options = SpannerOptions.newBuilder().build();
    Spanner spanner = options.getService();
    DatabaseId db = DatabaseId.of(options.getProjectId(), instanceId, databaseId);
    DatabaseClient dbClient = spanner.getDatabaseClient(db);

    Set<VolunteeringOpportunity> results = new HashSet<VolunteeringOpportunity>();
    Statement statement =
        Statement.of(
            String.format(
                "SELECT VolunteeringOpportunityID, Name, NumSpotsLeft, RequiredSkills FROM"
                    + " VolunteeringOpportunity WHERE EventID=\"%s\"",
                eventId));
    try (ResultSet resultSet = dbClient.singleUse().executeQuery(statement)) {
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
    spanner.close();
    return results;
  }
}

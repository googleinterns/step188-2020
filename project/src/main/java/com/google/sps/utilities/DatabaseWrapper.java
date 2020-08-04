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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

public class DatabaseWrapper {
  private String instanceId;
  private String databaseId;
  private static final String USER_TABLE = "Users";
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
   * Given a user, insert a row with all available fields into the DB
   *
   * @param user the user to be inserted; user's ID field should not exist in DB
   */
  public void insertUser(User user) {
    SpannerOptions options = SpannerOptions.newBuilder().build();
    Spanner spanner = options.getService();
    DatabaseId db = DatabaseId.of(options.getProjectId(), instanceId, databaseId);
    DatabaseClient dbClient = spanner.getDatabaseClient(db);

    List<Mutation> mutations = getUserMutationsFromBuilder(newInsertBuilderFromUser(), user);
    dbClient.write(mutations);
    spanner.close();
  }

  /**
   * Given a user, insert a row with all available fields into the DB
   *
   * @param user the user to be updated; user's ID field should already exist in DB
   */
  public void updateUser(User user) {
    // Given a user, update its corresponding row's new fields in DB
    SpannerOptions options = SpannerOptions.newBuilder().build();
    Spanner spanner = options.getService();
    DatabaseId db = DatabaseId.of(options.getProjectId(), instanceId, databaseId);
    DatabaseClient dbClient = spanner.getDatabaseClient(db);

    List<Mutation> mutations = getUserMutationsFromBuilder(newUpdateBuilderFromUser(), user);
    dbClient.write(mutations);
    spanner.close();
  }

  private static Mutation.WriteBuilder newInsertBuilderFromUser() {
    return Mutation.newInsertBuilder(USER_TABLE);
  }

  private static Mutation.WriteBuilder newUpdateBuilderFromUser() {
    return Mutation.newUpdateBuilder(USER_TABLE);
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

  /** Returns Event by ID from DB
   * 
   * @param eventId ID of event to be returned
  */
  public Optional<Event> getEventById(String eventId) {
    ResultSet resultSet = dbClient.singleUse().executeQuery(Statement.of(String.format(
        "SELECT Name, Description, Labels, Location, Date, Host, Opportunities, Attendees FROM %s WHERE EventID='%s'",
        EVENT_TABLE, eventId)));
    
    /** If ID does not exist */
    if (!resultSet.next()) {
      return Optional.empty();
    }

    // TO DO: replace with host from db, after PR #43 pushed
    String NAME = "Bob Smith";
    String EMAIL = "bobsmith@example.com";
    User host = new User.Builder(NAME, EMAIL).build();
    Date date = Date.fromYearMonthDay(2016, 9, 15);
    return Optional.of(new Event
                           .Builder(/* name = */ resultSet.getString(0),
                               /* description = */ resultSet.getString(1),
                               /* labels = */ new HashSet<String>(resultSet.getStringList(2)),
                               /* location = */ resultSet.getString(3), /* date = */date,
                               /* host = */ host)
                           .build());
    // TO DO: set volunteer opportunities, attendees by Querying those by ID, wait for PR 43, 44
  }

  private static List<Mutation> getUserMutationsFromBuilder(
      Mutation.WriteBuilder builder, User user) {
    List<Mutation> mutations = new ArrayList<>();
    builder.set("UserId")
        .to(user.getUserId())
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
        .to(event.getHost().getUserId())
        .set("Opportunities")
        .toInt64Array(event.getOpportunitiesIds())
        .set("Attendees")
        .toInt64Array(event.getAttendeeIds());
    mutations.add(builder.build());
    return mutations;
  }
}

package com.google.sps.utilities;

import com.google.cloud.spanner.DatabaseClient;
import com.google.cloud.spanner.DatabaseId;
import com.google.cloud.spanner.Mutation;
import com.google.cloud.spanner.ResultSet;
import com.google.cloud.spanner.Spanner;
import com.google.cloud.spanner.SpannerOptions;
import com.google.cloud.spanner.Statement;
import com.google.sps.data.Event;
import com.google.sps.data.User;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class DatabaseWrapper {
  private String instanceId;
  private String databaseId;
  private static final String USER_TABLE = "Users";

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

  private static Set<Event> getEventsFromIds(List<String> ids) {
    Set<Event> events = new HashSet<>();
    for (String eventId : ids) {
      events.add(readEventFromId(eventId));
    }
    return events;
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
}

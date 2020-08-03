package com.google.sps.utilities;

import com.google.cloud.spanner.DatabaseClient;
import com.google.cloud.spanner.DatabaseId;
import com.google.cloud.spanner.Mutation;
import com.google.cloud.spanner.Spanner;
import com.google.cloud.spanner.SpannerOptions;
import com.google.sps.data.User;
import java.util.ArrayList;
import java.util.List;

public class DatabaseWrapper {
  private String instanceId;
  private String databaseId;
  private final DatabaseService databaseService;
  private static final String USER_TABLE = "Users";

  public DatabaseWrapper(DatabaseService databaseService) {
    this.databaseService = databaseService;
  }

  /**
   * Given a user, insert a row with all available fields into the DB
   *
   * @param user the user to be inserted; user's ID field should not exist in DB
   */
  public void insertUser(User user) {
    List<Mutation> mutations = getMutationsFromBuilder(newInsertBuilderFromUser(), user);
    databaseService.getDatabaseClient().write(mutations);
    databaseService.getSpanner().close();
  }

  /**
   * Given a user, insert a row with all available fields into the DB
   *
   * @param user the user to be updated; user's ID field should already exist in DB
   */
  public void updateUser(User user) {
    // Given a user, update its corresponding row's new fields in DB
    List<Mutation> mutations = getMutationsFromBuilder(newUpdateBuilderFromUser(), user);
    databaseService.getDatabaseClient().write(mutations);
    databaseService.getSpanner().close();
  }

  private static Mutation.WriteBuilder newInsertBuilderFromUser() {
    return Mutation.newInsertBuilder(USER_TABLE);
  }

  private static Mutation.WriteBuilder newUpdateBuilderFromUser() {
    return Mutation.newUpdateBuilder(USER_TABLE);
  }

  private static List<Mutation> getMutationsFromBuilder(Mutation.WriteBuilder builder, User user) {
    List<Mutation> mutations = new ArrayList<>();
    builder
        .set("UserID")
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
        .toInt64Array(user.getEventsHostingIds())
        .set("EventsParticipating")
        .toInt64Array(user.getEventsParticipatingIds())
        .set("EventsVolunteering")
        .toInt64Array(user.getEventsVolunteeringIds());
    mutations.add(builder.build());
    return mutations;
  }

  /** Close the Spanner database connection. */
  public void closeConnection() {
    databaseService.getSpanner().close();
  }
}

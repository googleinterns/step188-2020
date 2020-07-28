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
  private String instanceID;
  private String databaseID;
  private static final String USER_TABLE = "Users";

  public DatabaseWrapper(String instanceID, String databaseID) {
    this.instanceID = instanceID;
    this.databaseID = databaseID;
  }

  /**
   * Given a user, insert a row with all available fields into the DB
   *
   * @param user the user to be inserted; user's ID field should not exist in DB
   */
  public void insertUser(User user) {
    SpannerOptions options = SpannerOptions.newBuilder().build();
    Spanner spanner = options.getService();
    DatabaseId db = DatabaseId.of(options.getProjectId(), instanceID, databaseID);
    DatabaseClient dbClient = spanner.getDatabaseClient(db);

    List<Mutation> mutations = newMutationFromUser(user, false);
    dbClient.write(mutations);
    spanner.close();
  }

  /**
   * Given a user, insert a row with all available fields into the DB
   *
   *  @param user the user to be updated; user's ID field should already exist in DB
   */
  public void updateUser(User user) {
    // Given a user, update its corresponding row's new fields in DB
    SpannerOptions options = SpannerOptions.newBuilder().build();
    Spanner spanner = options.getService();
    DatabaseId db = DatabaseId.of(options.getProjectId(), instanceID, databaseID);
    DatabaseClient dbClient = spanner.getDatabaseClient(db);

    List<Mutation> mutations = newMutationFromUser(user, true);
    dbClient.write(mutations);
    spanner.close();
  }

  private static List<Mutation> newMutationFromUser(User user, boolean update) {
    List<Mutation> mutations = new ArrayList<>();
    Mutation.WriteBuilder builder =
        update ? Mutation.newUpdateBuilder(USER_TABLE) : Mutation.newInsertBuilder(USER_TABLE);
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
        .toInt64Array(user.getEventsHostingIDs())
        .set("EventsParticipating")
        .toInt64Array(user.getEventsParticipatingIDs())
        .set("EventsVolunteering")
        .toInt64Array(user.getEventsVolunteeringIDs());
    mutations.add(builder.build());
    return mutations;
  }
}

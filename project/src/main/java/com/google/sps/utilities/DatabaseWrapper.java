package com.google.sps.utilities;

import com.google.sps.data.VolunteeringOpportunity;
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
  private static final String USER_TABLE = "Users";
  private static final String VOLUNTEERING_OPPORTUNITY_TABLE = "VolunteeringOpportunity";

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

    List<Mutation> mutations = getMutationsFromBuilder(newInsertBuilderFromUser(), user);
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

    List<Mutation> mutations = getMutationsFromBuilder(newUpdateBuilderFromUser(), user);
    dbClient.write(mutations);
    spanner.close();
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

  /**
   * Given a volunteering opportunity, insert a row with all available fields into the DB
   *
   * @param user the user to be inserted; user's ID field should not exist in DB
   */
  public void insertVolunteeringOpportunity(VolunteeringOpportunity opportunity) {
    SpannerOptions options = SpannerOptions.newBuilder().build();
    Spanner spanner = options.getService();
    DatabaseId db = DatabaseId.of(options.getProjectId(), instanceId, databaseId);
    DatabaseClient dbClient = spanner.getDatabaseClient(db);

    List<Mutation> mutations = getMutationsFromBuilder(newInsertBuilderFromVolunteeringOpportunity(), opportunity);
    dbClient.write(mutations);
    spanner.close();
  }

  /**
   * Given a volunteering opportunity, insert a row with all available fields into the DB
   *
   * @param user the user to be updated; user's ID field should already exist in DB
   */
  public void updateVolunteeringOpportunity(VolunteeringOpportunity opportunity) {
    // Given a user, update its corresponding row's new fields in DB
    SpannerOptions options = SpannerOptions.newBuilder().build();
    Spanner spanner = options.getService();
    DatabaseId db = DatabaseId.of(options.getProjectId(), instanceId, databaseId);
    DatabaseClient dbClient = spanner.getDatabaseClient(db);

    List<Mutation> mutations = getMutationsFromBuilder(newUpdateBuilderFromVolunteeringOpportunity(), opportunity);
    dbClient.write(mutations);
    spanner.close();
  }

   private static Mutation.WriteBuilder newInsertBuilderFromVolunteeringOpportunity() {
    return Mutation.newInsertBuilder(VOLUNTEERING_OPPORTUNITY_TABLE);
  }

  private static Mutation.WriteBuilder newUpdateBuilderFromVolunteeringOpportunity() {
    return Mutation.newUpdateBuilder(VOLUNTEERING_OPPORTUNITY_TABLE);
  }

  private static List<Mutation> getMutationsFromBuilder(Mutation.WriteBuilder builder, VolunteeringOpportunity opportunity) {
    List<Mutation> mutations = new ArrayList<>();
    builder
        .set("Name")
        .to(opportunity.getName())
        .set("NumSpotsLeft")
        .to(opportunity.getNumSpotsLeft())
        .set("RequiredSkills")
        .toStringArray(opportunity.getRequiredSkills());
    mutations.add(builder.build());
    return mutations;
  }
}

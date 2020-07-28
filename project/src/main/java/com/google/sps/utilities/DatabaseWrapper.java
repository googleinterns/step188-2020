package com.google.sps.utilities;

import com.google.cloud.spanner.DatabaseClient;
import com.google.cloud.spanner.DatabaseId;
import com.google.cloud.spanner.Mutation;
import com.google.cloud.spanner.Spanner;
import com.google.cloud.spanner.SpannerOptions;
import com.google.sps.data.Table;
import com.google.sps.data.User;
import java.util.ArrayList;
import java.util.List;

public class DatabaseWrapper {
  private static final String INSTANCE_ID = "step-188-instance";
  private static final String DATABASE_ID = "event-organizer-db";
  

  /** Given a user, insert a row with all available fields into the DB 
   *  @param user  the user to be inserted; user's ID field should not exist in DB
  */
  public static void insertUser(User user) {
    SpannerOptions options = SpannerOptions.newBuilder().build();
    Spanner spanner = options.getService();
    DatabaseId db = DatabaseId.of(options.getProjectId(), INSTANCE_ID, DATABASE_ID);
    DatabaseClient dbClient = spanner.getDatabaseClient(db);

    List<Mutation> mutations = new ArrayList<>();
    mutations.add(
        Mutation.newInsertBuilder("Users")
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
            .toInt64Array(user.getEventsVolunteeringIDs())
            .build());
    dbClient.write(mutations);
    spanner.close();
  }

  /** Given a user, insert a row with all available fields into the DB 
   *  @param user  the user to be updated; user's ID field should already exist in DB
  */
  public static void updateUser(User user) {
    // Given a user, update its corresponding row's new fields in DB
    SpannerOptions options = SpannerOptions.newBuilder().build();
    Spanner spanner = options.getService();
    DatabaseId db = DatabaseId.of(options.getProjectId(), INSTANCE_ID, DATABASE_ID);
    DatabaseClient dbClient = spanner.getDatabaseClient(db);

    List<Mutation> mutations = new ArrayList<>();
    mutations.add(
        Mutation.newUpdateBuilder("Users")
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
            .toInt64Array(user.getEventsVolunteeringIDs())
            .build());
    dbClient.write(mutations);
    spanner.close();
  }

  public static String createRestoredSampleDbId(DatabaseId database) {
    // Necessary for test tear-down
    int index = database.getDatabase().indexOf('-');
    String prefix = database.getDatabase().substring(0, index);
    String restoredDbId = database.getDatabase().replace(prefix, "restored");
    if (restoredDbId.length() > 30) {
      restoredDbId = restoredDbId.substring(0, 30);
    }
    return restoredDbId;
  }
}

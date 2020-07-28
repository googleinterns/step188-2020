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
  // private static String INSTANCE_ID;
  // private static String DATABASE_ID;

  // public DatabaseWrapper(String instanceID, String databaseID) {
  //   this.INSTANCE_ID = instanceID;
  //   this.DATABASE_ID = databaseID;
  // }

  // public static void insertUsers(List<User> users) {
  //   SpannerOptions options = SpannerOptions.newBuilder().build();
  //   Spanner spanner = options.getService();
  //   DatabaseId db = DatabaseId.of(options.getProjectId(), INSTANCE_ID, DATABASE_ID);
  //   DatabaseClient dbClient = spanner.getDatabaseClient(db);

  //   List<Mutation> mutations = new ArrayList<>();
  //   for (User user : users) {
  //     mutations.add(
  //         Mutation.newInsertBuilder("Users")
  //             .set("UserID")
  //             .to(user.getUserId())
  //             .set("Name")
  //             .to(user.getName())
  //             .set("Email")
  //             .to(user.getEmail())
  //             .set("Interests")
  //             .toStringArray(user.getInterests())
  //             .set("Skills")
  //             .toStringArray(user.getSkills())
  //             .set("EventsHosting")
  //             .toInt64Array(user.getEventsHostingIDs())
  //             .set("EventsParticipating")
  //             .toInt64Array(user.getEventsParticipatingIDs())
  //             .set("EventsVolunteering")
  //             .toInt64Array(user.getEventsVolunteeringIDs())
  //             .build());
  //   }
  //   dbClient.write(mutations);
  //   spanner.close();
  // }

  public static String createRestoredSampleDbId(DatabaseId database) {
    int index = database.getDatabase().indexOf('-');
    String prefix = database.getDatabase().substring(0, index);
    String restoredDbId = database.getDatabase().replace(prefix, "restored");
    if (restoredDbId.length() > 30) {
      restoredDbId = restoredDbId.substring(0, 30);
    }
    return restoredDbId;
  }
}

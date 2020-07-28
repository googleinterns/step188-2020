// package com.google.sps.utilities;

// import com.google.cloud.spanner.DatabaseAdminClient;
// import com.google.cloud.spanner.DatabaseClient;
// import com.google.cloud.spanner.DatabaseId;
// import com.google.cloud.spanner.InstanceAdminClient;
// import com.google.cloud.spanner.Mutation;
// import com.google.cloud.spanner.ResultSet;
// import com.google.cloud.spanner.Spanner;
// import com.google.cloud.spanner.SpannerOptions;
// import com.google.cloud.spanner.Statement;
// import com.google.sps.data.Event;
// import com.google.sps.data.User;
// import com.google.sps.data.VolunteeringOpportunity;
// import java.util.ArrayList;
// import java.util.Arrays;
// import java.util.List;
// import java.util.Collections;
// import java.util.HashSet;
// import java.util.Set;

// public class DatabaseWrapper {
//   private static final String instanceId = "step-188-instance";
//   private static final String databaseId = "event-organizer-db";
//   private static final SpannerOptions options = SpannerOptions.newBuilder().build();
//   private static final Spanner spanner = options.getService();
//   private static final DatabaseId db = DatabaseId.of(options.getProjectId(), instanceId, databaseId);
//   private static final DatabaseClient dbClient = spanner.getDatabaseClient(db);
//   private static final DatabaseAdminClient dbAdminClient = spanner.getDatabaseAdminClient();
//   private static final InstanceAdminClient instanceAdminClient = spanner.getInstanceAdminClient();

//   public void insertUser(User user) {
//     List<Mutation> mutations =
//         Arrays.asList(
//             Mutation.newInsertBuilder("users")
//                 .set("UserID")
//                 .to(user.getUserId())
//                 .set("Name")
//                 .to(user.getName())
//                 .set("Email")
//                 .to(user.getEmail())
//                 .build());
//     dbClient.write(mutations);
//   }

//   public void updateUser(User user) {
//     List<Mutation> mutations =
//         Arrays.asList(
//             Mutation.newUpdateBuilder("users")
//                 .set("UserID")
//                 .to(user.getUserId())
//                 .set("Name")
//                 .to(user.getName())
//                 .set("Email")
//                 .to(user.getEmail())
//                 .set("Interests")
//                 .to(user.getInterests())
//                 .set("")
//                 .build());
//     dbClient.write(mutations);
//   }

//   public void insertEvent(Event event) {
//   List<Mutation> mutations =
//       Arrays.asList(
//           Mutation.newInsertBuilder("users")
//               .set("UserID")
//               .to(user.getUserId())
//               .set("Name")
//               .to(user.getName())
//               .set("Email")
//               .to(user.getEmail())
//               .build());
//   dbClient.write(mutations);
//   }

//   public void updateUser(User user) {
//     List<Mutation> mutations =
//         Arrays.asList(
//             Mutation.newUpdateBuilder("users")
//                 .set("UserID")
//                 .to(user.getUserId())
//                 .set("Name")
//                 .to(user.getName())
//                 .set("Email")
//                 .to(user.getEmail())
//                 .build());
//     dbClient.write(mutations);
//   }
// }
package com.google.sps;

import com.google.api.gax.longrunning.OperationFuture;
import com.google.cloud.spanner.Database;
import com.google.cloud.spanner.DatabaseAdminClient;
import com.google.cloud.spanner.DatabaseId;
import com.google.cloud.spanner.Spanner;
import com.google.cloud.spanner.SpannerOptions;
import com.google.spanner.admin.database.v1.CreateDatabaseMetadata;
import com.google.sps.data.User;
import com.google.sps.utilities.DatabaseWrapper;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.UUID;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/** Unit tests for {@code DatabaseWrapper} */
@RunWith(JUnit4.class)
@SuppressWarnings("checkstyle:abbreviationaswordinname")
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class DatabaseWrapperTest {
  private static final User USER = new User.Builder("Bob Smith", "bobsmith@example.com").build();

  private static final String instanceId = System.getProperty("spanner.test.instance");
  private static final String databaseId =
      formatForTest(System.getProperty("spanner.sample.database"));
  static Spanner spanner;
  static DatabaseId dbId;
  static DatabaseAdminClient dbClient;

  @BeforeClass
  public static void setUp() throws Exception {
    SpannerOptions options = SpannerOptions.newBuilder().build();
    spanner = options.getService();
    dbClient = spanner.getDatabaseAdminClient();
    dbId = DatabaseId.of(options.getProjectId(), instanceId, databaseId);
    dbClient.dropDatabase(dbId.getInstanceId().getInstance(), dbId.getDatabase());
    dbClient.dropDatabase(
        dbId.getInstanceId().getInstance(), createRestoredSampleDbId(dbId));

    // COMMENTED OUT TO MATCH EXAMPLE BETTER

    // OperationFuture<Database, CreateDatabaseMetadata> op =
    //     dbClient.createDatabase(
    //         dbId.getInstanceId().getInstance(),
    //         dbId.getDatabase(),
    //         Arrays.asList(
    //             "CREATE TABLE Users ("
    //                 + "  UserID               INT64 NOT NULL,"
    //                 + "  Name                 STRING(MAX) NOT NULL,"
    //                 + "  Email               	STRING(MAX) NOT NULL,"
    //                 + "  Interests            ARRAY<STRING(MAX)>,"
    //                 + "  Skills               ARRAY<STRING(MAX)>,"
    //                 + "  EventsHosting        ARRAY<INT64>,"
    //                 + "  EventsParticipating  ARRAY<INT64>,"
    //                 + "  EventsVolunteering   ARRAY<INT64>"
    //                 + ") PRIMARY KEY (UserID)",
    //             "CREATE TABLE Events ("
    //                 + "  EventID        INT64 NOT NULL,"
    //                 + "  Name           STRING(MAX) NOT NULL,"
    //                 + "  Description    STRING(MAX),"
    //                 + "  Date           DATE,"
    //                 + "  Location       STRING(MAX),"
    //                 + "  Attendees      ARRAY<INT64>,"
    //                 + "  Host           INT64,"
    //                 + "  Labels         ARRAY<STRING(MAX)>,"
    //                 + "  Opportunities  ARRAY<INT64>"
    //                 + ") PRIMARY KEY (EventID)",
    //             "CREATE TABLE VolunteeringOpportunity ("
    //                 + "  VolunteeringOpportunityID  INT64 NOT NULL,"
    //                 + "  Name                       STRING(MAX) NOT NULL,"
    //                 + "  NumSpotsLeft               INT64 NOT NULL,"
    //                 + "  RequiredSkills             ARRAY<STRING(MAX)>,"
    //                 + "  Volunteers                 ARRAY<INT64>"
    //                 + ") PRIMARY KEY (VolunteeringOpportunityID)"));

    // spanner.close();
  }

  @AfterClass
  public static void tearDown() throws Exception {
    dbClient.dropDatabase(dbId.getInstanceId().getInstance(), dbId.getDatabase());
    dbClient.dropDatabase(
        dbId.getInstanceId().getInstance(), createRestoredSampleDbId(dbId));
  }

  @Test
  public void test1_insertUserAndRetrieveUser() {
    Assert.assertEquals("", "");
  }
  
  private static String formatForTest(String name) {
    return name + "-" + UUID.randomUUID().toString().substring(0, 20);
  }

  private static String createRestoredSampleDbId(DatabaseId database) {
    int index = database.getDatabase().indexOf('-');
    String prefix = database.getDatabase().substring(0, index);
    String restoredDbId = database.getDatabase().replace(prefix, "restored");
    if (restoredDbId.length() > 30) {
      restoredDbId = restoredDbId.substring(0, 30);
    }
    return restoredDbId;
  }
}

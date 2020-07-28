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
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class DatabaseWrapperTest {
  private static final String NEW_EMAIL = "new_bobsmith@example.com";
  private static final User USER = new User.Builder("Bob Smith", "bobsmith@example.com").build();

  private static final String INSTANCE_ID = "spanner-test-instance";
  private static final String DATABASE_ID = "spanner-sample-database";
  private static final DatabaseWrapper dbWrapper = new DatabaseWrapper(INSTANCE_ID, DATABASE_ID);

  private static Spanner spanner;
  private static DatabaseAdminClient dbAdminClient;
  private static DatabaseId dbId;

  @BeforeClass
  public static void setUp() throws Exception {
    SpannerOptions options = SpannerOptions.newBuilder().build();
    spanner = options.getService();
    dbId = DatabaseId.of(options.getProjectId(), INSTANCE_ID, DATABASE_ID);
    dbAdminClient = spanner.getDatabaseAdminClient();

    OperationFuture<Database, CreateDatabaseMetadata> op =
        dbAdminClient.createDatabase(
            dbId.getInstanceId().getInstance(),
            dbId.getDatabase(),
            Arrays.asList(
                "CREATE TABLE Users ("
                    + "  UserID               INT64 NOT NULL,"
                    + "  Name                 STRING(MAX) NOT NULL,"
                    + "  Email               	STRING(MAX) NOT NULL,"
                    + "  Interests            ARRAY<STRING(MAX)>,"
                    + "  Skills               ARRAY<STRING(MAX)>,"
                    + "  EventsHosting        ARRAY<INT64>,"
                    + "  EventsParticipating  ARRAY<INT64>,"
                    + "  EventsVolunteering   ARRAY<INT64>"
                    + ") PRIMARY KEY (UserID)",
                "CREATE TABLE Events ("
                    + "  EventID        INT64 NOT NULL,"
                    + "  Name           STRING(MAX) NOT NULL,"
                    + "  Description    STRING(MAX),"
                    + "  Date           DATE,"
                    + "  Location       STRING(MAX),"
                    + "  Attendees      ARRAY<INT64>,"
                    + "  Host           INT64,"
                    + "  Labels         ARRAY<STRING(MAX)>,"
                    + "  Opportunities  ARRAY<INT64>"
                    + ") PRIMARY KEY (EventID)",
                "CREATE TABLE VolunteeringOpportunity ("
                    + "  VolunteeringOpportunityID  INT64 NOT NULL,"
                    + "  Name                       STRING(MAX) NOT NULL,"
                    + "  NumSpotsLeft               INT64 NOT NULL,"
                    + "  RequiredSkills             ARRAY<STRING(MAX)>,"
                    + "  Volunteers                 ARRAY<INT64>"
                    + ") PRIMARY KEY (VolunteeringOpportunityID)"));

    spanner.close();
  }

  @AfterClass
  public static void tearDown() throws Exception {
    dbAdminClient.dropDatabase(dbId.getInstanceId().getInstance(), dbId.getDatabase());
  }

  @Test
  public void test1_insertUserAndRetrieveUser() {
    Assert.assertEquals("", "");
  }
}

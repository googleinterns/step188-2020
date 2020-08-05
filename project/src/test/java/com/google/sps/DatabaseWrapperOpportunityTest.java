package com.google.sps;
 
import com.google.api.gax.longrunning.OperationFuture;
import com.google.cloud.spanner.Database;
import com.google.cloud.spanner.DatabaseAdminClient;
import com.google.cloud.spanner.DatabaseId;
import com.google.cloud.spanner.Spanner;
import com.google.cloud.spanner.SpannerOptions;
import com.google.spanner.admin.database.v1.CreateDatabaseMetadata;
import com.google.sps.data.Table;
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
import org.hamcrest.Matchers;
import org.hamcrest.MatcherAssert;
import com.google.sps.data.VolunteeringOpportunity;
import com.google.sps.utilities.DatabaseConstants;
import org.hamcrest.CoreMatchers;
import java.util.Set;
 
/** Unit tests for {@code DatabaseWrapper} */
@RunWith(JUnit4.class)
@SuppressWarnings("checkstyle:abbreviationaswordinname")
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class DatabaseWrapperOpportunityTest {
  private static final String NAME = "Performer";
  private static final int NUMBER_OF_SPOTS = 240;
  private static final String EVENT_ID = "0883de79-17d7-49a3-a866-dbd5135062a8";
  private static final User USER = new User.Builder("Bob Smith", "bobsmith@example.com").build();
 
  private static final String instanceId = "step-188-instance";
  private static final String databaseId = "event-organizer-db-test";
  private static Spanner spanner;
  private static DatabaseId dbId;
  private static DatabaseAdminClient dbClient;
  private static DatabaseWrapper databaseWrapper;
 
  @BeforeClass
  public static void setUp() throws Exception {
    SpannerOptions options = SpannerOptions.newBuilder().build();
    spanner = options.getService();
    dbClient = spanner.getDatabaseAdminClient();
    dbId = DatabaseId.of(options.getProjectId(), DatabaseConstants.INSTANCE_ID, DatabaseConstants.DATABASE_ID);
    OperationFuture<Database, CreateDatabaseMetadata> op =
        dbClient.createDatabase(
            dbId.getInstanceId().getInstance(),
            dbId.getDatabase(),
            Arrays.asList(
                "CREATE TABLE Users ("
                    + "  Name                 STRING(MAX) NOT NULL,"
                    + "  Email                STRING(MAX) NOT NULL,"
                    + "  Interests            ARRAY<STRING(MAX)>,"
                    + "  Skills               ARRAY<STRING(MAX)>,"
                    + "  EventsHosting        ARRAY<STRING(MAX)>,"
                    + "  EventsParticipating  ARRAY<STRING(MAX)>,"
                    + "  EventsVolunteering   ARRAY<STRING(MAX)>"
                    + ") PRIMARY KEY (Email)",
                "CREATE TABLE Events ("
                    + "  EventID        STRING(MAX) NOT NULL,"
                    + "  Name           STRING(MAX) NOT NULL,"
                    + "  Description    STRING(MAX),"
                    + "  Date           DATE,"
                    + "  Location       STRING(MAX),"
                    + "  Attendees      ARRAY<STRING(MAX)>,"
                    + "  Host           STRING(MAX),"
                    + "  Labels         ARRAY<STRING(MAX)>,"
                    + "  Opportunities  ARRAY<STRING(MAX)>"
                    + ") PRIMARY KEY (EventID)",
                "CREATE TABLE VolunteeringOpportunity ("
                    + "  VolunteeringOpportunityID  STRING(MAX) NOT NULL,"
                    + "  EventID  STRING(MAX) NOT NULL,"
                    + "  Name                       STRING(MAX) NOT NULL,"
                    + "  NumSpotsLeft               INT64 NOT NULL,"
                    + "  RequiredSkills             ARRAY<STRING(MAX)>,"
                    + "  Volunteers                 ARRAY<STRING(MAX)>"
                    + ") PRIMARY KEY (VolunteeringOpportunityID)"));
    databaseWrapper = new DatabaseWrapper(DatabaseConstants.INSTANCE_ID, DatabaseConstants.DATABASE_ID);
  }
 
  @Test
  public void testDatabaseWrapperUserInsert() {
    VolunteeringOpportunity opportunity =
        new VolunteeringOpportunity.Builder(EVENT_ID, NAME, NUMBER_OF_SPOTS).build();
    databaseWrapper.insertVolunteeringOpportunity(opportunity);

    Set<VolunteeringOpportunity> opportunities = databaseWrapper.getVolunteeringOpportunitiesByEventId(EVENT_ID);

    Assert.assertTrue(opportunities.size() == 1);
  }

  @AfterClass
  public static void tearDown() throws Exception {
    //dbClient.dropDatabase(dbId.getInstanceId().getInstance(), dbId.getDatabase());
  }
}
 

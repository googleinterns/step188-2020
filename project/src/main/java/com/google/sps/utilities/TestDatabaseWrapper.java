package com.google.sps.utilities;

import com.google.api.gax.longrunning.OperationFuture;
import com.google.cloud.spanner.Database;
import com.google.cloud.spanner.DatabaseAdminClient;
import com.google.cloud.spanner.DatabaseId;
import com.google.cloud.spanner.Spanner;
import com.google.cloud.spanner.SpannerOptions;
import com.google.gson.Gson;
import com.google.spanner.admin.database.v1.CreateDatabaseMetadata;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;

public class TestDatabaseWrapper {
  private static Spanner spanner;
  private static DatabaseId databaseId;
  private static DatabaseAdminClient databaseAdminClient;
  private static DatabaseWrapper databaseWrapper;

  public TestDatabaseWrapper() {
    SpannerOptions options = SpannerOptions.newBuilder().build();
    spanner = options.getService();
    databaseAdminClient = spanner.getDatabaseAdminClient();
    databaseId = DatabaseId.of(
        options.getProjectId(), DatabaseConstants.INSTANCE_ID, DatabaseConstants.DATABASE_ID);
    OperationFuture<Database, CreateDatabaseMetadata> op = databaseAdminClient.createDatabase(
        databaseId.getInstanceId().getInstance(), databaseId.getDatabase(),
        Arrays.asList("CREATE TABLE Users ("
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
  }

  /* Drops the database */
  public void dropDatabase() {
    databaseAdminClient.dropDatabase(
        databaseId.getInstanceId().getInstance(), databaseId.getDatabase());
  }
}
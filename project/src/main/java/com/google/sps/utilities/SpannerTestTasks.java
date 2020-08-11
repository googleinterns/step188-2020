package com.google.sps.utilities;

import com.google.cloud.spanner.Database;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;

/** Class containing methods for interaction with database for tests. */
public class SpannerTestTasks {
  public static void setup() throws InterruptedException, ExecutionException {
    Iterable<String> statements =
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
                + "  Opportunities  ARRAY<STRING(MAX)>,"
                + "  Time           STRING(MAX)"
                + ") PRIMARY KEY (EventID)",
            "CREATE TABLE VolunteeringOpportunity ("
                + "  VolunteeringOpportunityID  STRING(MAX) NOT NULL,"
                + "  EventID                    STRING(MAX) NOT NULL,"
                + "  Name                       STRING(MAX) NOT NULL,"
                + "  NumSpotsLeft               INT64 NOT NULL,"
                + "  RequiredSkills             ARRAY<STRING(MAX)>,"
                + "  Volunteers                 ARRAY<STRING(MAX)>"
                + ") PRIMARY KEY (VolunteeringOpportunityID)",
            "CREATE TABLE OpportunitySignup ("
	            + "  VolunteeringOpportunityID  STRING(MAX) NOT NULL,"
	            + "  Email                      STRING(MAX) NOT NULL,"
                + ") PRIMARY KEY (VolunteeringOpportunityID, Email)");
    Database db =
        SpannerClient.getDatabaseAdminClient()
            .createDatabase(
                SpannerClient.getInstanceId(), SpannerClient.getDatabaseId(), statements)
            .get();
  }

  /* Drops the database */
  public static void cleanup() {
    SpannerClient.getDatabaseAdminClient()
        .dropDatabase(SpannerClient.getInstanceId(), SpannerClient.getDatabaseId());
  }
}

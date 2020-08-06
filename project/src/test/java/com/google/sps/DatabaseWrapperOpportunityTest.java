package com.google.sps;

import com.google.common.collect.ImmutableSet;
import com.google.sps.data.User;
import com.google.sps.data.VolunteeringOpportunity;
import com.google.sps.utilities.DatabaseConstants;
import com.google.sps.utilities.DatabaseWrapper;
import com.google.sps.utilities.TestDatabaseWrapper;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.junit.runners.MethodSorters;
import org.junit.Test;

/** Unit tests for DatabaseWrapper functionality related to VolunteeringOpportunity class. */
@RunWith(JUnit4.class)
public class DatabaseWrapperOpportunityTest {
  private static final String NAME = "Performer";
  private static final int NUMBER_OF_SPOTS = 240;
  private static final String EVENT_ID = "0883de79-17d7-49a3-a866-dbd5135062a8";
  private static TestDatabaseWrapper testDatabaseWrapper;
  private static DatabaseWrapper databaseWrapper;

  @BeforeClass
  public static void setUp() throws Exception {
    testDatabaseWrapper = new TestDatabaseWrapper();
    databaseWrapper =
        new DatabaseWrapper(DatabaseConstants.INSTANCE_ID, DatabaseConstants.DATABASE_ID);
  }

  @Test
  public void opportunityInsertAndRetrieval() {
    VolunteeringOpportunity opportunity =
        new VolunteeringOpportunity.Builder(EVENT_ID, NAME, NUMBER_OF_SPOTS).build();
    databaseWrapper.insertVolunteeringOpportunity(opportunity);

    VolunteeringOpportunity actualOpportunity =
        databaseWrapper.getVolunteeringOpportunityByOppportunityId(opportunity.getOpportunityId()).get();

    Assert.assertEquals(actualOpportunity, opportunity);
  }

  @Test
  public void retrieveVolunteeringOpportunitiesByEvent() {
    VolunteeringOpportunity opportunity =
        new VolunteeringOpportunity.Builder(EVENT_ID, NAME, NUMBER_OF_SPOTS).build();
    databaseWrapper.insertVolunteeringOpportunity(opportunity);

    Set<VolunteeringOpportunity> opportunities =
        databaseWrapper.getVolunteeringOpportunitiesByEventId(EVENT_ID);

    MatcherAssert.assertThat(opportunities, CoreMatchers.hasItems(opportunity));
  }

  @AfterClass
  public static void tearDown() throws Exception {
    testDatabaseWrapper.dropDatabase();
  }
}

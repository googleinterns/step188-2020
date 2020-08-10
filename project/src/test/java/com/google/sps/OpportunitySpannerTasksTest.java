package com.google.sps;

import com.google.sps.data.VolunteeringOpportunity;
import com.google.sps.utilities.SpannerClient;
import com.google.sps.utilities.SpannerTasks;
import com.google.sps.utilities.SpannerTestTasks;
import java.util.Set;
import javax.servlet.ServletContextEvent;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.springframework.mock.web.MockServletContext;

/** Unit tests for DatabaseWrapper functionality related to VolunteeringOpportunity class. */
@RunWith(JUnit4.class)
public class OpportunitySpannerTasksTest {
  private static final String NAME = "Performer";
  private static final int NUMBER_OF_SPOTS = 240;
  private static final String EVENT_ID = "0883de79-17d7-49a3-a866-dbd5135062a8";

  @BeforeClass
  public static void setUp() throws Exception {
    // Mock a request to trigger the SpannerClient setup to run
    MockServletContext mockServletContext = new MockServletContext();
    new SpannerClient().contextInitialized(new ServletContextEvent(mockServletContext));
    //SpannerTestTasks.setup();
  }

  @AfterClass
  public static void tearDown() throws Exception {
    //SpannerTestTasks.cleanup();
  }

  @Test
  public void opportunityInsertAndRetrieval() {
    VolunteeringOpportunity opportunity =
        new VolunteeringOpportunity.Builder(EVENT_ID, NAME, NUMBER_OF_SPOTS).build();
    SpannerTasks.insertVolunteeringOpportunity(opportunity);

    VolunteeringOpportunity actualOpportunity =
        SpannerTasks.getVolunteeringOpportunityByOppportunityId(opportunity.getOpportunityId())
            .get();

    Assert.assertEquals(actualOpportunity.getEventId(), EVENT_ID);
    Assert.assertEquals(actualOpportunity.getName(), NAME);
    Assert.assertEquals(actualOpportunity.getNumSpotsLeft(), NUMBER_OF_SPOTS);
  }

  @Test
  public void retrieveVolunteeringOpportunitiesByEvent() {
    VolunteeringOpportunity opportunity =
        new VolunteeringOpportunity.Builder(EVENT_ID, NAME, NUMBER_OF_SPOTS).build();
    SpannerTasks.insertVolunteeringOpportunity(opportunity);

    Set<VolunteeringOpportunity> opportunities =
        SpannerTasks.getVolunteeringOpportunitiesByEventId(EVENT_ID);
    VolunteeringOpportunity actualOpportunity = opportunities.stream().findFirst().get();

    Assert.assertEquals(actualOpportunity.getEventId(), EVENT_ID);
    Assert.assertEquals(actualOpportunity.getName(), NAME);
    Assert.assertEquals(actualOpportunity.getNumSpotsLeft(), NUMBER_OF_SPOTS);
  }
}

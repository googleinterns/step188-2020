package com.google.sps;

import com.google.sps.data.OpportunitySignup;
import com.google.sps.data.VolunteeringOpportunity;
import com.google.sps.utilities.SpannerClient;
import com.google.sps.utilities.SpannerTasks;
import com.google.sps.utilities.SpannerTestTasks;
import javax.servlet.ServletContextEvent;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.springframework.mock.web.MockServletContext;

/** Unit tests for SpannerTasks functionality related to OpportunitySignup class. */
@RunWith(JUnit4.class)
public class OpportunitySignupTasksTest {
  private static final String NAME = "Performer";
  private static final int NUMBER_OF_SPOTS = 240;
  private static final String EVENT_ID = "0883de79-17d7-49a3-a866-dbd5135062a8";
  private static final String EMAIL = "test@example.com";

  @BeforeClass
  public static void setUp() throws Exception {
    // Mock a request to trigger the SpannerClient setup to run
    MockServletContext mockServletContext = new MockServletContext();
    new SpannerClient().contextInitialized(new ServletContextEvent(mockServletContext));
    SpannerTestTasks.setup();
  }

  @AfterClass
  public static void tearDown() throws Exception {
    SpannerTestTasks.cleanup();
  }

  @Test
  public void opportunitySignupInsertOpportunityUpdated() {
    VolunteeringOpportunity opportunity =
        new VolunteeringOpportunity.Builder(EVENT_ID, NAME, NUMBER_OF_SPOTS).build();
    SpannerTasks.insertVolunteeringOpportunity(opportunity);

    OpportunitySignup signup =
        new OpportunitySignup.Builder(opportunity.getOpportunityId(), EMAIL).build();
    SpannerTasks.insertOpportunitySignup(signup);
    OpportunitySignup actualSignup =
        SpannerTasks.getSignupsByOpportunityId(opportunity.getOpportunityId()).stream().findFirst().get();
    VolunteeringOpportunity actualOpportunity =
        SpannerTasks.getVolunteeringOpportunityByOppportunityId(opportunity.getOpportunityId())
            .stream()
            .findFirst().get();
    
    Assert.assertEquals(actualSignup.getEmail(), EMAIL);
    Assert.assertEquals(actualSignup.getOpportunityId(), opportunity.getOpportunityId());
    Assert.assertEquals(actualOpportunity.getNumSpotsLeft(), NUMBER_OF_SPOTS - 1);
  }
}

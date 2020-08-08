package com.google.sps;

import com.google.sps.data.OpportunitySignup;
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
public class OpportunitySignupTasksTest {
  private static final String OPPORTUNITY_ID = "0883de79-17d7-49a3-a866-dbd5135062a8";
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
  public void opportunitySignupInsertAndRetrieval() {
    OpportunitySignup signup = new OpportunitySignup.Builder(OPPORTUNITY_ID, EMAIL).build();
    
    SpannerTasks.insertOpportunitySignup(signup);
    OpportunitySignup actualSignup =
        SpannerTasks.getSignupsByOpportunityId(OPPORTUNITY_ID).stream().findFirst().get();
    
    Assert.assertEquals(actualSignup.getEmail(), EMAIL);
    Assert.assertEquals(actualSignup.getOpportunityId(), OPPORTUNITY_ID);
  }
}

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

/** Unit tests for updates and retrievals for a user's corresponding events. */
@RunWith(JUnit4.class)
public class UserProfileEventsTest {
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
  public void verifyGetUserEventsHosting() {
    
  }

  @Test
  public void verifyGetUserEventsParticipating() {
    
  }

  @Test
  public void verifyGetUserEventsVolunteering() {
    
  }

  @Test
  public void verifyGetUserEventsInvalidParameter() {
    
  }

  @Test
  public void verifyPostUserEventsHosting() {
    
  }

  @Test
  public void verifyPostUserEventsParticipating() {
    
  }

  @Test
  public void verifyPostUserEventsVolunteering() {
    
  }

  @Test
  public void verifyPostUserEventsInvalidParameter() {
    
  }
}

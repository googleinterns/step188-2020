package com.google.sps;

import com.google.sps.data.Event;
import com.google.sps.data.User;
import com.google.sps.servlets.BlobHandlerEventServlet;
import com.google.sps.servlets.BlobUrlServlet;
import com.google.sps.utilities.CommonUtils;
import com.google.sps.utilities.SpannerClient;
import com.google.sps.utilities.SpannerTasks;
import com.google.sps.utilities.SpannerTestTasks;
import com.google.sps.utilities.TestUtils;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import javax.servlet.ServletContextEvent;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mockito;
import org.springframework.mock.web.MockServletContext;

/** */
@RunWith(JUnit4.class)
public class BlobHandlerEventTest {
  private static final String EVENT_TYPE = "event";
  private static final String EVENT_ID = "event-id";
  private static final String INVALID_ID = "invalid-id";
  private static final String IMAGE_URL = "image-url.com";
  private static final User HOST = TestUtils.newUser();
  private static final Event EVENT = TestUtils.newEventWithHost(HOST).toBuilder().setId(EVENT_ID).build();
  private static final HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
  private static final HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
  private static final StringWriter stringWriter = new StringWriter();
  private static final PrintWriter printWriter = new PrintWriter(stringWriter);

  @BeforeClass
  public static void setUp() throws Exception {
    // Mock a request to trigger the SpannerClient setup to run
    MockServletContext mockServletContext = new MockServletContext();
    new SpannerClient().contextInitialized(new ServletContextEvent(mockServletContext));
    SpannerTestTasks.setup();
    Mockito.when(response.getWriter()).thenReturn(printWriter);
  }

  @AfterClass
  public static void tearDown() throws Exception {
    SpannerTestTasks.cleanup();
  }

  @After
  public void resetMocks() {
    Mockito.reset(request);
    stringWriter.getBuffer().setLength(0);
  }

  @Test
  public void testGetImageUrl() throws Exception {
    SpannerTasks.insertOrUpdateUser(HOST);
    SpannerTasks.insertorUpdateEvent(
        EVENT.toBuilder().setImageUrl(IMAGE_URL).build());
    Mockito.doReturn(EVENT_ID).when(request).getParameter("event-id");

    new BlobHandlerEventServlet().doGet(request, response);
 
    Assert.assertFalse(stringWriter.toString().trim().isEmpty());
  }
}

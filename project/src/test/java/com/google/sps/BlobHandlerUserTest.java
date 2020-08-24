package com.google.sps;

import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.appengine.tools.development.testing.LocalUserServiceTestConfig;
import com.google.sps.data.User;
import com.google.sps.servlets.BlobHandlerUserServlet;
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
public class BlobHandlerUserTest {
  private static final String PICTURE_TYPE_PARAM = "picture-type";
  private static final String PROFILE_TYPE = "profile";
  private static final String EMAIL = "test@example.com";
  private static final String INVALID_EMAIL = "invalid@example.com";
  private static final String DOMAIN = "example.com";
  private static final String IMAGE_URL = "image-url.com";
  private static final User USER = TestUtils.newUserWithEmail(EMAIL);
  private static final LocalServiceTestHelper authenticationHelper =
      new LocalServiceTestHelper(new LocalUserServiceTestConfig());
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
    authenticationHelper.setUp();
    Mockito.when(response.getWriter()).thenReturn(printWriter);
  }

  @AfterClass
  public static void tearDown() throws Exception {
    SpannerTestTasks.cleanup();
    authenticationHelper.tearDown();
  }

  @After
  public void resetMocks() {
    Mockito.reset(request);
    stringWriter.getBuffer().setLength(0);
  }

  @Test
  public void testGetImageUrl() throws Exception {
    SpannerTasks.insertOrUpdateUser(
        USER.toBuilder().setImageUrl(IMAGE_URL).build());
    setAuthenticationHelper(EMAIL);

    new BlobHandlerUserServlet().doGet(request, response);
 
    Assert.assertFalse(stringWriter.toString().trim().isEmpty());
  }

  @Test
  public void testGetUploadUserUrl() throws Exception {
    SpannerTasks.insertOrUpdateUser(
        USER.toBuilder().setImageUrl(IMAGE_URL).build());
    setAuthenticationHelper(EMAIL);
    Mockito.doReturn(PROFILE_TYPE).when(request).getParameter(PICTURE_TYPE_PARAM);

    new BlobUrlServlet().doGet(request, response);

    Assert.assertFalse(stringWriter.toString().trim().isEmpty());
  }

  @Test
  public void testBlobstoreInvalidUser() throws Exception {
    setAuthenticationHelper(INVALID_EMAIL);
    Mockito.doReturn(PROFILE_TYPE).when(request).getParameter(PICTURE_TYPE_PARAM);

    new BlobUrlServlet().doGet(request, response);

    Mockito.verify(response)
        .sendError(HttpServletResponse.SC_BAD_REQUEST, "Error with getting current user: does not exist");
  }

  private static void setAuthenticationHelper(String email) {
    authenticationHelper
        .setEnvIsLoggedIn(true)
        .setEnvEmail(email)
        .setEnvAuthDomain(DOMAIN);
  }
}

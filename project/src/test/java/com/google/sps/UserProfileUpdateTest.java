package com.google.sps;

import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.appengine.tools.development.testing.LocalUserServiceTestConfig;
import com.google.sps.data.User;
import com.google.sps.servlets.UserProfileUpdateServlet;
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

/** Unit tests for DatabaseWrapper functionality related to Event class. */
@RunWith(JUnit4.class)
public class UserProfileUpdateTest {
  private static final String EMAIL = "test@example.com";
  private static final String DOMAIN = "example.com";
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
  public void flushWriter() {
    Mockito.reset(request);
    stringWriter.getBuffer().setLength(0);
  }

  @Test
  public void testGetUpdatedUser() throws Exception {
    SpannerTasks.insertOrUpdateUser(USER);
    setAuthenticationHelper();

    new UserProfileUpdateServlet().doGet(request, response);
 
    Assert.assertEquals(
        CommonUtils.convertToJson(USER), stringWriter.toString().trim());
  }

  @Test
  public void testPostUpdatedUser() throws Exception {
    Mockito.when(request.getParameter("name")).thenReturn(USER.getName());
    Mockito.doReturn(USER.getInterests().toString()).when(request).getParameter("interests");
    Mockito.doReturn(USER.getSkills().toString()).when(request).getParameter("skills");
    setAuthenticationHelper();

    new UserProfileUpdateServlet().doPost(request, response);

    Mockito.verify(response).sendRedirect("/profile.html");
  }

  private static void setAuthenticationHelper() {
    authenticationHelper
        .setEnvIsLoggedIn(true)
        .setEnvEmail(EMAIL)
        .setEnvAuthDomain(DOMAIN);
  }
}


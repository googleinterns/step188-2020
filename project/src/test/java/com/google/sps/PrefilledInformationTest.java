package com.google.sps;

import com.google.sps.servlets.PrefilledInformationServlet;
import com.google.sps.utilities.CommonUtils;
import com.google.sps.utilities.PrefilledInformationConstants;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.After;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mockito;

/** */
@RunWith(JUnit4.class)
public final class PrefilledInformationTest {
  private static final String REQUEST_CATEGORY = "category";
  private static final String INTERESTS = "interests";
  private static final String SKILLS = "skills";

  private static final HttpServletRequest request = Mockito.mock(HttpServletRequest.class);       
  private static final HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
  private static final StringWriter stringWriter = new StringWriter();
  private static final PrintWriter writer = new PrintWriter(stringWriter);

  @BeforeClass
  public static void setUp() throws Exception {
    Mockito.when(response.getWriter()).thenReturn(writer);
  }

  @After
  public void flushWriter() {
    Mockito.reset(request);
    stringWriter.getBuffer().setLength(0);
  }

  @Test
  public void requestInterests() throws IOException {
    // Verify that returned interests is the provided interest constant
    Mockito.when(request.getParameter(REQUEST_CATEGORY)).thenReturn(INTERESTS);
    new PrefilledInformationServlet().doGet(request, response);
    Assert.assertEquals(
        CommonUtils.convertToJson(PrefilledInformationConstants.INTERESTS).trim(),
        stringWriter.toString().trim());
  }

  @Test
  public void requestSkills() throws IOException {
    // Verify that returned skills is the provided skills constant
    Mockito.when(request.getParameter(REQUEST_CATEGORY)).thenReturn(SKILLS);
    new PrefilledInformationServlet().doGet(request, response);
    Assert.assertEquals(
        CommonUtils.convertToJson(PrefilledInformationConstants.SKILLS).trim(),
        stringWriter.toString().trim());
  }
}

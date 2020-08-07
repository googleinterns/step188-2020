package com.google.sps;

import com.google.sps.servlets.PrefilledInformationServlet;
import com.google.sps.utilities.CommonUtils;
import com.google.sps.utilities.PrefilledInformationConstants;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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

  @Test
  public void requestInterests() throws IOException {
    HttpServletRequest request = Mockito.mock(HttpServletRequest.class);       
    HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
    StringWriter stringWriter = new StringWriter();
    PrintWriter writer = new PrintWriter(stringWriter);
    Mockito.when(response.getWriter()).thenReturn(writer);
    // Verify that returned interests is the provided interest constant
    Mockito.when(request.getParameter(REQUEST_CATEGORY)).thenReturn(INTERESTS);
    new PrefilledInformationServlet().doGet(request, response);
    writer.flush();
    Assert.assertEquals(CommonUtils.convertToJson(PrefilledInformationConstants.INTERESTS).trim(), stringWriter.toString().trim());
  }

  @Test
  public void requestSkills() throws IOException {
    HttpServletRequest request = Mockito.mock(HttpServletRequest.class);       
    HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
    StringWriter stringWriter = new StringWriter();
    PrintWriter writer = new PrintWriter(stringWriter);
    Mockito.when(response.getWriter()).thenReturn(writer);
    // Verify that returned skills is the provided skills constant
    Mockito.when(request.getParameter(REQUEST_CATEGORY)).thenReturn(SKILLS);
    new PrefilledInformationServlet().doGet(request, response);
    writer.flush();
    Assert.assertEquals(CommonUtils.convertToJson(PrefilledInformationConstants.SKILLS).trim(), stringWriter.toString().trim());
  }
}

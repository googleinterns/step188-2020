package com.google.sps.servlets;

import com.google.sps.utilities.CommonUtils;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * When the user submits the form, Blobstore processes the file upload and then forwards the request
 * to this servlet. This servlet can then process the request using the file URL we get from
 * Blobstore.
 */
@WebServlet("/my-form-handler")
public class FormHandlerServlet extends HttpServlet {
  private static final String VOLUNTEER_TYPE = "volunteer-type";
  private static final String VOLUNTEER_NUMBER = "volunteer-number";

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
      System.out.println(request);
    // Get input from the form for comment text.
    String volunteerType = CommonUtils.getParameter(request, VOLUNTEER_TYPE, /* DefaultValue= */ "");

    // Get text with the sentiment tag added.
    String volunteerNumber = CommonUtils.getParameter(request, VOLUNTEER_NUMBER, /* DefaultValue= */ "");

    System.out.println(volunteerType);
    System.out.println(volunteerNumber);

    // Redirect to same HTML page.
    response.sendRedirect("/index.html");
  }
}
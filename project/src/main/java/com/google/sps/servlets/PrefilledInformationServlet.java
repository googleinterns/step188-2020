package com.google.sps.servlets;

import com.google.sps.utilities.CommonUtils;
import com.google.sps.utilities.PrefilledInformationConstants;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet to get prefilled lists of information */
@WebServlet("/prefilled-information")
public class PrefilledInformationServlet extends HttpServlet {
  private static final String INTERESTS = "interests";
  private static final String SKILLS = "skills";

  /**
   * Accesses pre-built list of information
   *
   * @param request servlet request - includes a 'category' parameter specifying the information to
   *    be returned
   * @param response servlet response
   * @throws IOException if Input/Output error occurs
   */
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String category = request.getParameter("category");
    List<String> information = new ArrayList<>();
    if (category.equals(INTERESTS)) {
      information = PrefilledInformationConstants.INTERESTS;
    } else if (category.equals(SKILLS)) {
      information = PrefilledInformationConstants.SKILLS;
    } else {
      response.sendError(HttpServletResponse.SC_NOT_FOUND);
    }
    response.setContentType("application/json;");
    response.getWriter().println(CommonUtils.convertToJson(information));
  }
}

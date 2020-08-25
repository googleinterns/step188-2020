package com.google.sps.servlets;

import com.google.sps.store.SearchStore;
import com.google.sps.utilities.CommonUtils;
import com.google.sps.utilities.KeywordHelper;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet that handles search queries and addition of event keywords to index. */
@WebServlet("/search-data")
public class SearchDataServlet extends HttpServlet {
  private static final String KEYWORD = "keyword";
  
  /**
   * Return search results for the keyword specified in the request.
   *
   * @param request servlet request
   * @param response servlet response
   * @throws IOException if Input/Output error occurs
   */
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String keyword = request.getParameter(KEYWORD);
    if (keyword == null) {
      response.sendError(HttpServletResponse.SC_BAD_REQUEST, String.format("No keyword specified."));
      return;
    }

    response.setContentType("application/json;");
    response.getWriter().println(CommonUtils.convertToJson(new SearchStore(KeywordHelper.getInstance()).getSearchResults(keyword)));
  }
}

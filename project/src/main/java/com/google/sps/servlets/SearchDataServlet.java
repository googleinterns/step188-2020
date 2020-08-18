package com.google.sps.servlets;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.sps.utilities.CommonUtils;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet that handles search queries and addition of event keywords to index. */
@WebServlet("/search-data")
public class SearchDataServlet extends HttpServlet {
  private ListMultimap<String, String> keywordToEventIds = ArrayListMultimap.create();
  private static final String KEYWORD = "keyword";
  private static final String EVENT_ID = "event-id";
  private static final String DESCRIPTION = "description";
  
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
    response.getWriter().println(CommonUtils.convertToJson(keywordToEventIds.get(keyword)));
  }

  /**
   * Add keywords in the description specified to the index with mapping to the event id.
   *
   * @param request servlet request
   * @param response servlet response
   * @throws IOException if Input/Output error occurs
   */
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String eventId = request.getParameter(EVENT_ID);
    String description = CommonUtils.getParameter(request, DESCRIPTION, "");
    if (eventId == null) {
      response.sendError(HttpServletResponse.SC_BAD_REQUEST, String.format("No event ID specified."));
      return;
    }

    String[] keywordsInDescription = description.split("[^a-zA-Z0-9']+");
    addKeywordsToIndex(eventId, keywordsInDescription);

    response.sendRedirect("/events-feed.html");
  }

  /**
    * Add keywords to the index with mapping to given event ID.
    */
  private void addKeywordsToIndex(String eventId, String[] keywords) {
    for (String keyword : keywords) {
      keywordToEventIds.put(keyword, eventId);
    }
  }
}

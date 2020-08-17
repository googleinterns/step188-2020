// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps.servlets;

import com.google.common.collect.ListMultimap;
import com.google.common.collect.ArrayListMultimap;
import com.google.sps.utilities.CommonUtils;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet that handles comments data. */
@WebServlet("/searchData")
public class SearchDataServlet extends HttpServlet {
  private ListMultimap<String, String> keywordToEventIds = ArrayListMultimap.create();
  private static final String KEYWORD = "keyword";
  private static final String EVENT_ID = "event-id";
  private static final String DESCRIPTION = "description";

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String keyword = request.getParameter(KEYWORD);

    if (keyword == null) {
        
    }

    response.setContentType("application/json;");
    response.getWriter().println(CommonUtils.convertToJson(keywordToEventIds.get(keyword)));
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String eventId = request.getParameter(EVENT_ID);
    String description = CommonUtils.getParameter(request, DESCRIPTION, "");

    String[] keywordsInDescription = description.split("[^a-zA-Z0-9']+");
    addWordsToIndex(eventId, keywordsInDescription);

    response.sendRedirect("/events-feed.html");
  }

  private void addWordsToIndex(String eventId, String[] keywords) {
    for (String keyword : keywords) {
      keywordToEventIds.put(keyword, eventId);
    }
  }
}
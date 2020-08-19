package com.google.sps.servlets;

import com.google.cloud.Date;
import com.google.gson.Gson;
import com.google.sps.data.Event;
import com.google.sps.data.User;
import com.google.sps.utilities.CommonUtils;
import com.google.sps.utilities.SpannerTasks;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/get-filtered-events")
public class FilteredEventServlet extends HttpServlet {
  /** Returns events as specified by interest filters */
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String labelParams = request.getParameter("labelParams");
    String[] labelParamsArr = labelParams.split("-");
    Set<Event> events = SpannerTasks.getFilteredEvents(labelParamsArr);

    if (!events.isPresent()) {
      response.sendError(
          HttpServletResponse.SC_NOT_FOUND,
          String.format("No events found with labels %s", labelParams));
    } else {
      response.setContentType("application/json;");
      response.getWriter().println(CommonUtils.convertToJson(events));
    }
  }
}

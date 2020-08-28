package com.google.sps.servlets;

import com.google.cloud.Date;
import com.google.cloud.language.v1.ClassificationCategory;
import com.google.cloud.language.v1.ClassifyTextResponse;
import com.google.cloud.language.v1.ClassifyTextRequest;
import com.google.cloud.language.v1.Document;
import com.google.cloud.language.v1.Document.Type;
import com.google.cloud.language.v1.LanguageServiceClient;
import com.google.gson.Gson;
import com.google.sps.data.Event;
import com.google.sps.data.User;
import com.google.sps.store.SearchStore;
import com.google.sps.utilities.CommonUtils;
import com.google.sps.utilities.KeywordHelper;
import com.google.sps.utilities.NlpProcessing;
import com.google.sps.utilities.PrefilledInformationConstants;
import com.google.sps.utilities.SpannerTasks;
import java.io.IOException;
import java.lang.StringBuilder;
import java.util.ArrayList;
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
import javax.servlet.ServletException;

@WebServlet("/create-event")
public class EventCreationServlet extends HttpServlet {
  private SearchStore searchStore;

  /**
   * Set up state for handling keyword helper requests. This method is only called when running in a
   * server, not when running in a test.
   */
  @Override
  public void init() throws ServletException {
    super.init();
    setSearchStore(new SearchStore(KeywordHelper.getInstance()));
  }

  /**
   * Sets the KeywordHelper used by this servlet. This function provides a common setup method for
   * use by the test framework or the servlet's init() function.
   */
  public void setSearchStore(SearchStore searchStore) {
    this.searchStore = searchStore;
  }

  /** Returns event details from database */
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String eventId = request.getParameter("eventId");
    Optional<Event> eventOptional = SpannerTasks.getEventById(eventId);

    // If event DNE, sends 404 ERR to frontend
    if (eventOptional.isPresent()) {
      Event event = eventOptional.get();
      response.setContentType("text/html;");
      response.getWriter().println(new Gson().toJson(event));
    } else {
      response.sendError(
          HttpServletResponse.SC_NOT_FOUND,
          String.format("No events found with event ID %s", eventId));
    }
  }

  /** Posts newly created event to database with NLP suggested labels and redirects to page with created event details*/
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String eventId = request.getParameter("eventId");
    String name = request.getParameter("name");
    String[] parsedDate = request.getParameter("date").split("-");
    Date date =
        Date.fromYearMonthDay(
            /*Year=*/ Integer.parseInt(parsedDate[0]),
            /*Month=*/ Integer.parseInt(parsedDate[1]),
            /*Day=*/ Integer.parseInt(parsedDate[2]));
    String time = request.getParameter("time");
    String description = request.getParameter("description");
    String location = request.getParameter("location");
    String text = new StringBuilder().append(name).append(" ").append(description).toString();

    // Add user inputted and NLP suggested labels together
    Set<String> labels = new HashSet<>();
    labels.addAll(new HashSet<>(getNlpSuggestedFilters(text, new ArrayList<String>())));
    labels.addAll(new HashSet<>(splitAsList(request.getParameter("interests"))));

    User host = SpannerTasks.getLoggedInUser().get();
    Event event = new Event.Builder(name, description, labels, location, date, time, host).build();
    if (eventId == null) {
      SpannerTasks.insertorUpdateEvent(event);
    } else {
      event = event.toBuilder().setId(eventId).build();
      SpannerTasks.insertorUpdateEvent(event);
      SpannerTasks.deleteIndexEntriesByEventId(eventId);
    }
    searchStore.addEventToIndex(event.getId(), name, description);

    String redirectUrl = "/event-details.html?eventId=" + event.getId();
    response.sendRedirect(redirectUrl);
    // Event in database
    response.getWriter().println(CommonUtils.convertToJson(SpannerTasks.getEventById(event.getId()).get().toBuilder().build()));
  }

  /* Calls to get NLP Suggested filters if applicable (20 words or more)
   * @param text: String of text that includes event name and description
   * @return categoryNames: returns selected names of labels that NLP API suggests for text if available, else empty list
   */
  private ArrayList<String> getNlpSuggestedFilters(String text, ArrayList<String> categoryNames) throws IOException{
    if (text.trim().split("\\s+").length > 20) {
      NlpProcessing nlp = new NlpProcessing();
      categoryNames = nlp.getNlp(text);
    }
    return categoryNames;
  }

  private static List<String> splitAsList(String values) {
    return Arrays.asList(values.split("\\s*,\\s*"));
  }
}

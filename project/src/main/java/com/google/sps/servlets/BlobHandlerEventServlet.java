package com.google.sps.servlets;

import com.google.appengine.api.blobstore.BlobInfo;
import com.google.appengine.api.blobstore.BlobInfoFactory;
import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.images.ImagesService;
import com.google.appengine.api.images.ImagesServiceFactory;
import com.google.appengine.api.images.ServingUrlOptions;
import com.google.sps.data.Event;
import com.google.sps.utilities.CommonUtils;
import com.google.sps.utilities.SpannerTasks;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * When the user submits the form, Blobstore processes the file upload and then forwards the request
 * to this servlet. This servlet can then process the request using the file URL we get from
 * Blobstore.
 */
@WebServlet("/event-blob-handler")
public class BlobHandlerEventServlet extends HttpServlet {
  private static final String EVENT_PICTURE = "event-picture";

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    Optional<Event> eventOptional = SpannerTasks.getEventById(request.getParameter("event-id"));
    if (eventOptional.isPresent()) {
      Event event = eventOptional.get();
      response.getWriter().println(event.getImageUrl());
    } else {
      response.sendError(
          HttpServletResponse.SC_BAD_REQUEST, "Error with getting event: does not exist");
    }
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String eventId = request.getParameter("event-id");
    Optional<Event> eventOptional = SpannerTasks.getEventById(eventId);
    if (eventOptional.isPresent()) {
      Event event = eventOptional.get();
      String blobKeyString = CommonUtils.getUploadedBlobKeyString(request, EVENT_PICTURE);
      SpannerTasks.insertorUpdateEvent(event.toBuilder().setImageUrl(blobKeyString).build());
    } else {
      response.sendError(
          HttpServletResponse.SC_BAD_REQUEST, "Error with getting event: does not exist");
    }
    response.sendRedirect(String.format("/event-details.html?eventId=%s", eventId));
  }
}

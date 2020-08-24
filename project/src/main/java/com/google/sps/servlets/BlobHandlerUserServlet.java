package com.google.sps.servlets;

import com.google.appengine.api.blobstore.BlobInfo;
import com.google.appengine.api.blobstore.BlobInfoFactory;
import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.images.ImagesService;
import com.google.appengine.api.images.ImagesServiceFactory;
import com.google.appengine.api.images.ServingUrlOptions;
import com.google.sps.data.User;
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
@WebServlet("/profile-blob-handler")
public class BlobHandlerUserServlet extends HttpServlet {
  private static final String PROFILE_PICTURE = "profile-picture";

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    Optional<User> userOptional = SpannerTasks.getLoggedInUser();
    if (userOptional.isPresent()) {
      User user = userOptional.get();
      response.getWriter().println(user.getImageUrl());
    } else {
      response.sendError(
          HttpServletResponse.SC_BAD_REQUEST, "Error with getting current user: does not exist");
    }
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    Optional<User> userOptional = SpannerTasks.getLoggedInUser();
    if (userOptional.isPresent()) {
      User user = userOptional.get();
      String blobKeyString = CommonUtils.getUploadedBlobKeyString(request, PROFILE_PICTURE);
      SpannerTasks.insertOrUpdateUser(user.toBuilder().setImageUrl(blobKeyString).build());
    } else {
      response.sendError(
          HttpServletResponse.SC_BAD_REQUEST, "Error with getting current user: does not exist");
    }
    response.sendRedirect("/profile-edit.html");
  }
}

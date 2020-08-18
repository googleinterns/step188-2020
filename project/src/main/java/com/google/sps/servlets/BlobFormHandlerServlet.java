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
@WebServlet("/blob-handler")
public class BlobFormHandlerServlet extends HttpServlet {
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
      String imageUrl = getUploadedFileUrl(request);
      SpannerTasks.insertOrUpdateUser(user.toBuilder().setImageUrl(imageUrl).build());
    } else {
      response.sendError(
          HttpServletResponse.SC_BAD_REQUEST, "Error with getting current user: does not exist");
    }
    response.sendRedirect("/profile-edit.html");
  }

  /** Returns a URL that points to the uploaded file, or null if the user didn't upload a file. */
  private static String getUploadedFileUrl(HttpServletRequest request) {
    BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
    Map<String, List<BlobKey>> blobs = blobstoreService.getUploads(request);
    List<BlobKey> blobKeys = blobs.get(PROFILE_PICTURE);

    // User submitted form without selecting a file, so we can't get a URL. (dev server)
    if (blobKeys == null || blobKeys.isEmpty()) {
      return null;
    }

    BlobKey blobKey = blobKeys.get(0);

    // User submitted form without selecting a file, so we can't get a URL. (live server)
    BlobInfo blobInfo = new BlobInfoFactory().loadBlobInfo(blobKey);
    if (blobInfo.getSize() == 0) {
      blobstoreService.delete(blobKey);
      return null;
    }
    return getUrlFromBlobKey(blobKey);
  }

  private static String getUrlFromBlobKey(BlobKey blobKey) {
    ImagesService imagesService = ImagesServiceFactory.getImagesService();
    ServingUrlOptions options = ServingUrlOptions.Builder.withBlobKey(blobKey);
    String servingUrl = imagesService.getServingUrl(options);
    try {
      URL url = new URL(servingUrl);
      return url.getPath();
    } catch (MalformedURLException e) {
      return servingUrl;
    }
  }
}

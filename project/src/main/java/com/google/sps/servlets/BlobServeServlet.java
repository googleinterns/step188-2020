package com.google.sps.servlets;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/blob-serve")
public class BlobServeServlet extends HttpServlet {
  private static final String BLOB_KEY = "key";

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String blobKeyValue = request.getParameter(BLOB_KEY);
    if (!blobKeyValue.isEmpty()) {
      BlobstoreServiceFactory.getBlobstoreService().serve(new BlobKey(blobKeyValue), response);
    } else {
      response.sendError(
          HttpServletResponse.SC_BAD_REQUEST, "Error with serving image: blob key does not exist");
    }
  }
}

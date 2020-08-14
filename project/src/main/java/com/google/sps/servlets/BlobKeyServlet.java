package com.google.sps.servlets;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** */
@WebServlet("/blob-key")
public class BlobKeyServlet extends HttpServlet {
  private static final String BLOB_KEY = "blob-key";

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    BlobstoreServiceFactory
        .getBlobstoreService()
        .serve(new BlobKey(request.getParameter(BLOB_KEY)), response);
  }
}
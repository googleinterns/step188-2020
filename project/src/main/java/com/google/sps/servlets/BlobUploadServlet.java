package com.google.sps.servlets;

import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** */
@WebServlet("/blob-upload")
public class BlobUploadServlet extends HttpServlet {
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    response.setContentType("text/html");
    response
        .getWriter()
        .println(BlobstoreServiceFactory.getBlobstoreService().createUploadUrl("/my-form-handler"));
  }
}
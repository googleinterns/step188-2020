package com.google.sps.servlets;

import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** */
@WebServlet("/blob-url")
public class BlobUrlServlet extends HttpServlet {
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String pictureType = request.getParameter("picture-type");
    String servletUrl = String.format("/%s-blob-handler", pictureType);
    if (pictureType.equals("event")) {
      servletUrl += "?event-id=" + request.getParameter("event-id");
    } else if (pictureType == null) {
      response.sendError(
          HttpServletResponse.SC_BAD_REQUEST, "Error with getting image: pictureType does not exist");
    }
    String uploadUrl = BlobstoreServiceFactory.getBlobstoreService().createUploadUrl(servletUrl);
    response.setContentType("text/html");
    response.getWriter().println(uploadUrl);
  }
}

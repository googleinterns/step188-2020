package com.google.sps.utilities;

import com.google.appengine.api.blobstore.BlobInfo;
import com.google.appengine.api.blobstore.BlobInfoFactory;
import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.images.ImagesService;
import com.google.appengine.api.images.ImagesServiceFactory;
import com.google.appengine.api.images.ServingUrlOptions;
import com.google.gson.Gson;
import java.util.HashSet;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.servlet.http.HttpServletRequest;

public class CommonUtils {
  public static String convertToJson(Object object) {
    return new Gson().toJson(object);
  }

  /** Splits string into list based on delimitors */
  public static List<String> splitAsList(String values) {
    return Arrays.asList(values.split("\\s*,\\s*"));
  }

  /** Returns a JsonArray containing each element provided */
  public static JsonArray createJsonArray(Set<String> elements) {
    JsonArrayBuilder builder = Json.createArrayBuilder();
    for (String element : elements) {
      builder.add(element);
    }
    return builder.build();
  }

  /**
   * Get the parameter values if exists in the request, else return the defaultValue.
   *
   * @param request the servlet request
   * @param name the name of the parameter requested
   * @param defaultValue the default value to return if the parameter requested does not exist in
   *     the request
   * @return the value for parameter in the request or the default value if request does not have
   *     the parameter
   */
  public static String getParameter(HttpServletRequest request, String name, String defaultValue) {
    String value = request.getParameter(name);
    return (value == null || value.isEmpty()) ? defaultValue : value;
  }

  /**
   * Get the set of non-empty parameter values if parameter exists in the request, else return empty
   * set.
   *
   * @param request the servlet request
   * @param name the name of the parameter requested
   * @return the nonempty values for the parameter in the request or empty set if the request not
   *     have the parameter
   */
  public static Set<String> getParameterValues(HttpServletRequest request, String name) {
    String[] values = request.getParameterValues(name);
    if (values == null) {
      return new HashSet<String>();
    }
    Set<String> nonEmptyValues = new HashSet<String>();
    for (String value : values) {
      if (!value.isEmpty()) {
        nonEmptyValues.add(value);
      }
    }
    return nonEmptyValues;
  }


  /** Returns a URL that points to the uploaded file, or null if the user didn't upload a file. */
  public static String getUploadedBlobKeyString(HttpServletRequest request, String uploadType) {
    BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
    Map<String, List<BlobKey>> blobs = blobstoreService.getUploads(request);
    List<BlobKey> blobKeys = blobs.get(uploadType);

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
    return blobKey.getKeyString();
  }
}

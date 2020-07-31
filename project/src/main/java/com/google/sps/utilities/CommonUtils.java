package com.google.sps.utilities;

import com.google.gson.Gson;
import java.util.HashSet;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;

public class CommonUtils {
  public static String convertToJson(Object object) {
    return new Gson().toJson(object);
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
   * @return the nonempty values for the parameter in the request or empty set if the request
   *     not have the parameter
   */
  public static Set<String> getParameterValues(HttpServletRequest request, String name) {
    String[] values = request.getParameterValues(name);
    Set<String> nonEmptyValues = new HashSet<String>();
    for (String value : values) {
      if (!value.isEmpty()) {
        nonEmptyValues.add(value);
      }
    }
    return nonEmptyValues;
  }
}

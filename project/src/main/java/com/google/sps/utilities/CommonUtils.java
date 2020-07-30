package com.google.sps.utilities;

import com.google.gson.Gson;
import java.util.HashSet;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;

public class CommonUtils {
  public static String convertToJson(Object object) {
    return new Gson().toJson(object);
  }

  public static String getParameter(HttpServletRequest request, String name, String defaultValue) {
    String value = request.getParameter(name);
    return (value == null || value.isEmpty()) ? defaultValue : value;
  }

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

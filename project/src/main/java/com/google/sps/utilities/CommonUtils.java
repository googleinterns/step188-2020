package com.google.sps.utilities;

import com.google.gson.Gson;
import com.google.sps.utilities.CommonUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
      if (!value.isEmpty())
        nonEmptyValues.add(value);
    }
    return nonEmptyValues;
  }
}

package com.google.sps.utilities;

import com.google.gson.Gson;

public class CommonUtils {
  public static String convertToJson(Object object) {
    return new Gson().toJson(object);
  }
}

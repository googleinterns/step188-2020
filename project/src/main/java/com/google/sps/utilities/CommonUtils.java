package com.google.sps.utilities;

import com.google.gson.Gson;
import java.util.Set;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;

public class CommonUtils {
  public static String convertToJson(Object object) {
    return new Gson().toJson(object);
  }

  public static JsonArray createJsonArray(Set<String> elements) {
    JsonArrayBuilder builder = Json.createArrayBuilder();
    for (String element : elements) {
      builder.add(element);
    }
    return builder.build();
  }
}

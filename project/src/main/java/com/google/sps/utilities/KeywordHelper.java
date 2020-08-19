package com.google.sps.utilities;

import com.google.cloud.language.v1.AnalyzeEntitiesRequest;
import com.google.cloud.language.v1.AnalyzeEntitiesResponse;
import com.google.cloud.language.v1.Document;
import com.google.cloud.language.v1.Document.Type;
import com.google.cloud.language.v1.EncodingType;
import com.google.cloud.language.v1.Entity;
import com.google.cloud.language.v1.LanguageServiceClient;
import com.google.sps.data.Keyword;
import java.io.IOException;
import java.util.ArrayList;

/* Class that abstracts away getting keywords for specific content to enable testing. */
public class KeywordHelper {
  
  /** Singleton instance of KeywordHelper. */
  private static KeywordHelper instance;

  /** Returns the singleton instance of KeywordHelper. */
  public static KeywordHelper getInstance() {
    if (instance == null) {
      instance = new KeywordHelper();
    }
    return instance;
  }

  private String content;

  public void setContent(String content) {
    this.content = content;
  }

  /**
    * Finds the keywords in the current instance's content.
    * @return list of Keyword objects representing the keywords and their relevances
    *       for the current instances's content.
    */
  public ArrayList<Keyword> getKeywords() throws IOException {
    ArrayList<Keyword> keywords = new ArrayList<Keyword>();
    try (LanguageServiceClient language = LanguageServiceClient.create()) {
      Document doc = Document.newBuilder().setContent(content).setType(Type.PLAIN_TEXT).build();
      AnalyzeEntitiesRequest request =
          AnalyzeEntitiesRequest.newBuilder()
              .setDocument(doc)
              .setEncodingType(EncodingType.UTF16)
              .build();

      AnalyzeEntitiesResponse response = language.analyzeEntities(request);
      for (Entity entity : response.getEntitiesList()) {
        keywords.add(
            new Keyword(entity.getName(), entity.getSalience()));
      }
    }
    return keywords;
  }
}

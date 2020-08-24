package com.google.sps.utilities;

import com.google.cloud.language.v1.AnalyzeEntitiesRequest;
import com.google.cloud.language.v1.AnalyzeEntitiesResponse;
import com.google.cloud.language.v1.AnalyzeSyntaxRequest;
import com.google.cloud.language.v1.AnalyzeSyntaxResponse;
import com.google.cloud.language.v1.Document;
import com.google.cloud.language.v1.Document.Type;
import com.google.cloud.language.v1.EncodingType;
import com.google.cloud.language.v1.Entity;
import com.google.cloud.language.v1.LanguageServiceClient;
import com.google.cloud.language.v1.Token;
import com.google.sps.data.Keyword;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

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
   *
   * @return list of Keyword objects representing the keywords and their relevances for the current
   *     instances's content.
   */
  public List<Keyword> getKeywords() {
    List<Keyword> keywords = new ArrayList<Keyword>();
    try (LanguageServiceClient language = LanguageServiceClient.create()) {
      Document doc = Document.newBuilder().setContent(content).setType(Type.PLAIN_TEXT).build();
      AnalyzeEntitiesRequest request =
          AnalyzeEntitiesRequest.newBuilder()
              .setDocument(doc)
              .setEncodingType(EncodingType.UTF16)
              .build();

      AnalyzeEntitiesResponse response = language.analyzeEntities(request);
      for (Entity entity : response.getEntitiesList()) {
        keywords.add(new Keyword(entity.getName(), entity.getSalience()));
      }
    } catch (IOException e) {
      System.err.println("IO Exception due to NLP library.");
    }
    return keywords;
  }

  /**
   * Finds the tokens with their basic forms in the current instance's content.
   *
   * @return mapping of each token in current instance's content to its basic form.
   */
  public Map<String, String> getTokensWithBasicForm() {
    Map<String, String> tokenToBasicForm = new HashMap<String, String>();
    try (LanguageServiceClient language = LanguageServiceClient.create()) {
      Document doc = Document.newBuilder().setContent(content).setType(Type.PLAIN_TEXT).build();
      AnalyzeSyntaxRequest request =
          AnalyzeSyntaxRequest.newBuilder()
              .setDocument(doc)
              .setEncodingType(EncodingType.UTF16)
              .build();
      AnalyzeSyntaxResponse response = language.analyzeSyntax(request);
      for (Token token : response.getTokensList()) {
        tokenToBasicForm.put(token.getText().getContent(), token.getLemma());
      }
    } catch (IOException e) {
      System.err.println("IO Exception due to NLP library.");
    }
    return tokenToBasicForm;
  }
}

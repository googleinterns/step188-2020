package com.google.sps.utilities;

import com.google.cloud.language.v1.ClassificationCategory;
import com.google.cloud.language.v1.ClassifyTextResponse;
import com.google.cloud.language.v1.ClassifyTextRequest;
import com.google.cloud.language.v1.Document;
import com.google.cloud.language.v1.Document.Type;
import com.google.cloud.language.v1.LanguageServiceClient;
import com.google.sps.utilities.PrefilledInformationConstants;
import java.io.IOException;
import java.util.ArrayList;

/* Class containing NLP API Processing Results */
public class NlpProcessing {
/*
 * @param text: String of text that includes event name and description
 * @return categoryNames: returns selected names of labels that NLP API suggests for text
*/
  public ArrayList<String> getNlp(String text) throws IOException {
    ArrayList<String> categoryNames = new ArrayList<String>();

    // Use Gcloud NLP API to predict labels based on user inputted event name and description
    try (LanguageServiceClient language = LanguageServiceClient.create()) {     
      Document doc = Document.newBuilder().setContent(text).setType(Type.PLAIN_TEXT).build();
      ClassifyTextRequest req = ClassifyTextRequest.newBuilder().setDocument(doc).build();
      // Detect categories in the given text
      ClassifyTextResponse res = language.classifyText(req);
        for (ClassificationCategory category : res.getCategoriesList()) {
          String categoryName = category.getName().split("/")[1];
            if (category.getConfidence() >= 0.5 && PrefilledInformationConstants.INTERESTS.contains(categoryName) ) {
              categoryNames.add(categoryName);
            }
        }
    } 
   return categoryNames;
  }
}

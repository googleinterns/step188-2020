package com.google.sps.utilities;

import com.google.sps.data.Event;
import com.google.sps.data.User;
import com.google.sps.data.VolunteeringOpportunity;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map; 
import java.util.Optional;
import java.util.Scanner;
import java.util.Set;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import org.javatuples.Pair; 

/** Class used to determine whether labels (and thus events) are related and how 
  * directMatchEvents: Events where the labels are direct String matches to user labels
  * similarLabels: Events where labels are related matches to user labels (category/subcategory relationship)
  */
public class GetLabelCategories {
  //txt file is based on NLP API categories: https://cloud.google.com/natural-language/docs/categories 
  private static final String LABEL_TEXT = 
    "src/main/java/com/google/sps/utilities/LabelCategories.txt";
  private static final HashMap<String, String> subcategoryToCategoryMapping = 
    createSubcategoryToCategoryLabelMapping();
  private static final HashMap<String, HashSet<String>> categorytoSubcategoryMapping =
    createCategorytoSubcategoryLabelMapping();
  //indexes based on format of category nesting in LABEL_TEXT file
  private static final int CATEGORY_INDEX = 1;
  private static final int FIRST_SUBCATEGORY_INDEX = 2;

  /** 
   * Gets a pair of two sets that hold how events are related based on labels
   * @param relevantEvents All events on discovery page
   * @param user Current logged in user
   * @return directMatchEvents as Pair[0], similarLabels as Pair[1]
   */
  public Pair<Set<Pair<Event, Integer>>, Set<Pair<Event, Integer>>> getEventRelevancy(Set<Event> relevantEvents, User user) {
    Set<Pair<Event, Integer>> directMatchEvents = new HashSet<Pair<Event, Integer>>();
    Set<Pair<Event, Integer>> similarLabelEvents = new HashSet<Pair<Event, Integer>>(); 
    Set<String> interestsAndSkills = new HashSet<>(user.getInterests());
    interestsAndSkills.addAll(user.getSkills());

    // Find matches between user labels and event labels
    for (Event event: relevantEvents) {
      Set<String> labelsAndSkills = event.getLabels();
      for (VolunteeringOpportunity opportunity : event.getOpportunities()) {
        labelsAndSkills.addAll(opportunity.getRequiredSkills());
      }
      checkDirectMatches(directMatchEvents, event, interestsAndSkills, labelsAndSkills );
      checkSimilarMatches(similarLabelEvents, event, interestsAndSkills, labelsAndSkills );
    }

    return new Pair<>(directMatchEvents, similarLabelEvents);
  }

  /** If direct matches exist, add to directMatchEvents */
  private void checkDirectMatches(
      Set<Pair<Event, Integer>> directMatchEvents, Event event,
      Set<String>  interestsAndSkills, Set<String>  labelsAndSkills) {
    Optional<Pair<Event, Integer>> directMatchEventsOptional = 
    getDirectMatchEvents(event, interestsAndSkills, labelsAndSkills);
    if (directMatchEventsOptional.isPresent()) {
      directMatchEvents.add(directMatchEventsOptional.get());
    }
  }

/** 
 * @return
 * Event: if labels are direct String matches to user labels, Integer: number of matching labels
 * Empty if no direct matches
 */
  private Optional<Pair<Event, Integer>> getDirectMatchEvents(
      Event event, Set<String> userLabels, Set<String> eventLabels) {
    HashSet<String> directMatchEvents = new HashSet<String>(userLabels);
    directMatchEvents.retainAll(eventLabels);
    if (!directMatchEvents.isEmpty()) {
      return Optional.of(new Pair<Event, Integer>(event, directMatchEvents.size()));
    }
    return Optional.empty();
  }

  /** Checks to see if similar matches exist, if so, add to directMatchEvents */
  private void checkSimilarMatches(
    Set<Pair<Event, Integer>> similarLabelEvents, Event event,
    Set<String>  interestsAndSkills, Set<String>  labelsAndSkills) {
    Optional<Pair<Event, Integer>> similarLabelEventsOptional = 
      getsimilarLabelEvents(event, interestsAndSkills, labelsAndSkills);
    if (similarLabelEventsOptional.isPresent()) {
      similarLabelEvents.add(similarLabelEventsOptional.get());
    }
  }

/** 
 * @return
 * Event: if labels are similar to user labels, Integer: number of matching labels
 * Empty if no category/subcategory relation
 */
  private Optional<Pair<Event, Integer>> getsimilarLabelEvents(
    Event event, Set<String> userLabels, Set<String> eventLabels) {
    Pair<Event, Integer> similarEvents = new Pair<Event, Integer>(null, 0);
    for (String label: userLabels) {
      if (eventLabels.contains(label)) { // If direct match
        continue;
      }
      else if (categorytoSubcategoryMapping.containsKey(label)) { // If label is category
        similarEvents = addLabelInSubcategory(label, event,  similarEvents, eventLabels);
      }
      else if (subcategoryToCategoryMapping.containsKey(label)) { // If label is subcategory type
        String category = subcategoryToCategoryMapping.get(label);
        similarEvents = addLabelInCategory(category, event,  similarEvents, eventLabels);
      }
    }
    if (similarEvents.getValue1().equals(0)){
      return Optional.empty();
    }
    return Optional.of(similarEvents);
  }

  /** Add event if it is in larger category type */
  private Pair<Event, Integer> addLabelInCategory(String category, Event event,
    Pair<Event, Integer> similarEvents,Set<String> eventLabels){
    // Add event if it is larger category type
    // Eg. If label = basketball, add Sports events also
    if (eventLabels.contains(category)) {
      similarEvents = new Pair<Event, Integer>(event, similarEvents.getValue1() + 1);
    }
    // Add event if in same larger category
    // Eg. If label = basketball, also add baseball 
    for (String eventLabel: eventLabels) {
      if (subcategoryToCategoryMapping.containsKey(eventLabel) &&
      subcategoryToCategoryMapping.get(eventLabel).equals(category)) {
        similarEvents = new Pair<Event, Integer>(event, similarEvents.getValue1() + 1);
        }
      }
    return similarEvents;
  }

  /** Add events if in label subcategory */
  private Pair<Event, Integer> addLabelInSubcategory(String label, Event event,
    Pair<Event, Integer>  similarEvents,Set<String> eventLabels) {
    HashSet<String> sameCategoryLabels = new HashSet<String>(categorytoSubcategoryMapping.get(label));
    sameCategoryLabels.retainAll(eventLabels);
    if (!sameCategoryLabels.isEmpty()) {
      return new Pair<Event, Integer>(event, similarEvents.getValue1() + sameCategoryLabels.size());
    }
    return similarEvents;
  }

  /** 
    * Creates valueToKeyMap with Key: Subcategory, Value: Category 
    * based on text file
    */
  private static HashMap<String, String> createSubcategoryToCategoryLabelMapping()  {
    HashMap<String, String> valueToKeyMap = new HashMap<String, String>();
    try {
      Scanner textFile = new Scanner(new File(LABEL_TEXT));
      while(textFile.hasNextLine()) {
        String[] tokens = textFile.nextLine().split("/");
        for (int i = FIRST_SUBCATEGORY_INDEX; i < tokens.length; i++) {
          valueToKeyMap.put(tokens[i], tokens[CATEGORY_INDEX]);
        }
      }
    }
    catch (FileNotFoundException e) {
      System.out.println("File not found");
    }
    return valueToKeyMap;
  }

  /** Creates valueToKeyMap with Key: Category, Value: Set(Subcategories) 
    * based on text file
    */
  private static HashMap<String, HashSet<String>> createCategorytoSubcategoryLabelMapping() {
    HashMap<String, HashSet<String>> valueToKeyMap = new HashMap<String, HashSet<String>>();
    try {
      Scanner textFile = new Scanner(new File(LABEL_TEXT));
      while(textFile.hasNextLine()) {
        String[] tokens = textFile.nextLine().split("/");
        if (valueToKeyMap.get(tokens[CATEGORY_INDEX]) !=  null) {
          HashSet<String> arraylist = valueToKeyMap.get(tokens[1]);
          for (int i = FIRST_SUBCATEGORY_INDEX; i < tokens.length; i++) {
            arraylist.add(tokens[i]);
          }
        valueToKeyMap.put(tokens[CATEGORY_INDEX], arraylist);
        } else {
          HashSet<String> arraylist = new HashSet<String>();
          for (int i = FIRST_SUBCATEGORY_INDEX; i < tokens.length; i++) {
            arraylist.add(tokens[i]);   
          }
        valueToKeyMap.put(tokens[CATEGORY_INDEX], arraylist);
        }
      }
    } catch (FileNotFoundException e) {
      System.out.println("File not found");
    }
    return valueToKeyMap;
  }
}

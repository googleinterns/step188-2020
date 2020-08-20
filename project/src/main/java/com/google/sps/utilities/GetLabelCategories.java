package com.google.sps.utilities;
import com.google.sps.data.Event;
import com.google.sps.data.User;
import com.google.sps.data.VolunteeringOpportunity;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.HashMap;
import java.util.HashSet;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.Map; 
import java.util.HashMap;
import org.javatuples.Pair; 
import java.util.Optional;

/**
take in Set<Event> relevantEvents = SpannerTasks.getAllEvents() and user
return events that are directMatchEvents, and similarMatchEvents

- find events that match exactly to "basketball" => Set<Event> directMatchEvents
- similarLabels = bidimap.getSiblings("basketball")       // returns ["soccer", "football"]
- use bidimap to also find events that match exactly to similarLabels => Set<Event> similarMatchEvents

then

- I take directMatchEvents and similarMatchEvents and apply an algo to score and rank them collectively

*/
/** Class used to determine whether labels (and thus events) are related */
public class GetLabelCategories {

  public static final String LABEL_TEXT = "src/main/java/com/google/sps/utilities/LabelCategories.txt";
  public static final HashMap<String, String> subcategoryToCategoryMapping = createSubcategoryToCategoryLabelMapping();
  public static final HashMap<String, HashSet<String>> categorytoSubcategoryMapping = createCategorytoSubcategoryLabelMapping();

//hold all of User event interests-> /Sports, /Birds --> all events under sports and /birds
//iterate events and get interests. for interest in interests, see if in User interests()


// all events under pets 'the big category of /birds'
//add to similarLabels -> if in userlabel in categorytoSubcategoryMapping, add event to similar labels if label is 
//value of categorytoSubcategoryMapping
//

  /** 
   * @param relevantEvents All events on discovery page (in spanner)
   * @param user Current logged in user
   * @return List<Set<String>> where Set directMatchEvents = List[0], Set similarLabels = List[1]
   * Set<Pair<Event, int>> = event, int = how many filters matched
   */
  public ArrayList<Set<Pair<Event, Integer>>> getEventRelevancy(Set<Event> relevantEvents, User user) {
    Set<Pair<Event, Integer>> directMatchEvents = new HashSet<Pair<Event, Integer>>();
    Set<Pair<Event, Integer>> similarLabelEvents = new HashSet<Pair<Event, Integer>>(); 
    Set<String> interestsAndSkills = user.getInterests();
    interestsAndSkills.addAll(user.getSkills());

    for (Event event: relevantEvents) {
      Set<String> labelsAndSkills = event.getLabels();
      for (VolunteeringOpportunity opportunity : event.getOpportunities()) {
        labelsAndSkills.addAll(opportunity.getRequiredSkills());
      }

    Optional<Pair<Event, Integer>> directMatchEventsOptional = getDirectMatchEvents(event, interestsAndSkills, labelsAndSkills );
    if (directMatchEventsOptional.isPresent()) {
        directMatchEvents.add(directMatchEventsOptional.get());
    }

    //similarLabelEvents.add(getsimilarLabelEvents(event, interestsAndSkills, labelsAndSkills));
    }
    ArrayList<Set<Pair<Event, Integer>>> list = new ArrayList<Set<Pair<Event, Integer>>>();
    list.add(directMatchEvents);
    list.add(similarLabelEvents);
    return list;
  }

  // MAKE AN OPTIONAL
  private Optional<Pair<Event, Integer>> getDirectMatchEvents(Event event, Set<String> userLabels, Set<String> eventLabels) {
      HashSet<String> directMatchEvents = new HashSet<String>(userLabels);
      directMatchEvents.retainAll(eventLabels);

      if (!directMatchEvents.isEmpty()) {
          return  Optional.of(new Pair<Event, Integer>(event, directMatchEvents.size()));
      }
      return Optional.empty();
  }

// all events under pets 'the big category of /birds'
//add to similarLabels -> if in userlabel in categorytoSubcategoryMapping, add event to similar labels if label is 
//value of categorytoSubcategoryMapping
  private Pair<Event, Integer> getsimilarLabelEvents(Event event, Set<String> userLabels, Set<String> eventLabels) {
      Pair<Event, Integer> result = new Pair<Event, Integer>(null, 0);
      for (String label: userLabels) {
          if (categorytoSubcategoryMapping.containsKey(label)) {
               HashSet<String> sameCategoryLabels = new HashSet<String>(categorytoSubcategoryMapping.get(label));
                sameCategoryLabels.retainAll(eventLabels);
                      if (!sameCategoryLabels.isEmpty()) {
                result = new Pair<Event, Integer>(event, result.getValue1() + sameCategoryLabels.size());
                      }
                //result is pair[1] + 1
          }
          else if(subcategoryToCategoryMapping.containsKey(label)) {
              String category = subcategoryToCategoryMapping.get(label);
            if(eventLabels.contains(category) ) {
                result = new Pair<Event, Integer>(event, result.getValue1() + 1);
            }
          }

      }

    return result;  
  }

  /** Creates Map<String, String> with Key: Subcategory, Value: Category */
  private static HashMap<String, String> createSubcategoryToCategoryLabelMapping()  {
    HashMap<String, String> valueToKeyMap = new HashMap<String, String>();
    try {
    Scanner textFile = new Scanner(new File(LABEL_TEXT));
      while(textFile.hasNextLine()) {
        String[] tokens = textFile.nextLine().split("/");
		for (int i = 2; i < tokens.length; i++) {
		  valueToKeyMap.put(tokens[i], tokens[1]);
		}
      }
    }
    catch( FileNotFoundException e) {
      System.out.println("File not found");
    }
    return valueToKeyMap;
  }

  /** Creates Map<String, HashSet<String>>  with Key: Category, Value: Set(Subcategories) */
  private static HashMap<String, HashSet<String>> createCategorytoSubcategoryLabelMapping()  {
    HashMap<String, HashSet<String>> valueToKeyMap = new HashMap<String, HashSet<String>>();
    try {
    Scanner textFile = new Scanner(new File(LABEL_TEXT));

    while(textFile.hasNextLine()) {
      String[] tokens = textFile.nextLine().split("/");
      if ( valueToKeyMap.get(tokens[1]) !=  null) {
        HashSet<String> arraylist = valueToKeyMap.get(tokens[1]);
          for (int i = 2; i < tokens.length; i++) {
            arraylist.add(tokens[i]);  
		  }
        valueToKeyMap.put(tokens[1], arraylist);
      } else {
        HashSet<String> arraylist = new HashSet<String>();
		for (int i = 2; i < tokens.length; i++) {
          arraylist.add(tokens[i]);   
		}
        valueToKeyMap.put(tokens[1], arraylist);
      }
    }
    } catch( FileNotFoundException e) { System.out.println("File not found"); }
    return valueToKeyMap;
    }
}

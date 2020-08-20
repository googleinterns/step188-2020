package com.google.sps.utilities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.HashSet;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.Map; 
import java.util.HashMap; 


/**
take in Set<Event> relevantEvents = SpannerTasks.getAllEvents() and user
return events that are directMatchEvents, and similarMatchEvents

- find events that match exactly to "basketball" => Set<Event> directMatchEvents
- similarLabels = bidimap.getSiblings("basketball")       // returns ["soccer", "football"]
- use bidimap to also find events that match exactly to similarLabels => Set<Event> similarMatchEvents

then

- I take directMatchEvents and similarMatchEvents and apply an algo to score and rank them collectively

*/

public class GetLabelCategories {

    public static final String LABEL_TEXT = "src/main/java/com/google/sps/utilities/LabelCategories.txt";
    public static final HashMap<String, String> labelValueMapping  =  createLabelValueMapping();


    public static HashMap<String, String> createLabelValueMapping()  {
            HashMap<String, String> valueToKeyMap = new HashMap<String, String>();
        try {


    Scanner textFile = new Scanner(new File(LABEL_TEXT));

    while(textFile.hasNextLine()) {
        String[] tokens = textFile.nextLine().split("/");
		for (int i = 2; i < tokens.length; i++) {
		    valueToKeyMap.put(tokens[i], tokens[1]);
		}
    }

        } catch( FileNotFoundException e) {
            System.out.println("File not found");
        }
    return valueToKeyMap;
    }


    public static Map<String, HashSet<String>> createLabelKeyMapping() throws FileNotFoundException {
    Map<String, HashSet<String>> valueToKeyMap = new HashMap<String, HashSet<String>>();
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
    return valueToKeyMap;
    }

}
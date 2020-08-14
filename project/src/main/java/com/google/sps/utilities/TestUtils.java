package com.google.sps.utilities;
 
import com.google.sps.data.VolunteeringOpportunity;
import com.google.sps.data.OpportunitySignup;
import com.google.sps.data.User;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/* Class containing utilities for testing. */
public class TestUtils {
  private static final String EVENT_ID = "0883de79-17d7-49a3-a866-dbd5135062a8";
  private static final String NAME = "Performer";
  private static final int NUMBER_OF_SPOTS = 240;
  private static final String VOLUNTEER_EMAIL = "volunteer@gmail.com";
  private static final String USER_NAME = "Bob Smith";
  private static final Set<String> INTERESTS =
      Collections.unmodifiableSet(new HashSet<>(Arrays.asList("Conservation", "Food")));
  private static final Set<String> SKILLS =
      Collections.unmodifiableSet(new HashSet<>(Arrays.asList("Cooking")));
 
  public static VolunteeringOpportunity newVolunteeringOpportunity() {
    return new VolunteeringOpportunity.Builder(EVENT_ID, NAME, NUMBER_OF_SPOTS).build();
  }
 
  public static OpportunitySignup newOpportunitySignup(String opportunityId) {
    return new OpportunitySignup.Builder(opportunityId, VOLUNTEER_EMAIL).build();
  }

  public static User newUser(String email) {
    return new User.Builder(USER_NAME, email).setInterests(INTERESTS).setSkills(SKILLS).build();
  }
 
  public static String newRandomId() {
    return UUID.randomUUID().toString();
  }
}

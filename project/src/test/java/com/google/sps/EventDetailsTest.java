package com.google.sps;
import com.google.cloud.Date;
import com.google.sps.data.Event;
import com.google.sps.data.User;
import com.google.sps.data.VolunteeringOpportunity;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public final class EventDetailsTest {
  private static final String NAME = "Bob Smith";
  private static final String EMAIL = "bobsmith@example.com";
  private static final Set<String> INTERESTS = new HashSet<>(Arrays.asList("Conservation", "Food"));
  private static final Set<String> SKILLS =
      Collections.unmodifiableSet(new HashSet<>(Arrays.asList("Cooking")));
  User USER1 =
      new User.Builder(NAME, EMAIL)
          .setInterests(INTERESTS)
          .setSkills(SKILLS)
          .setEventsHosting(EVENTS_HOSTING)
          .setEventsParticipating(EVENTS_PARTICIPATING)
          .setEventsVolunteering(EVENTS_VOLUNTEERING)
          .build();
  @Test
  public void verifyEventDetails() {
    Event event1 =
        new Event.Builder("Weekly Meal Prep: Angel Food Cake", 
            "In this Meal Prep Seminar, we will be teaching you how to make a delicious cake!", 
            new HashSet<>(Arrays.asList("Tech", "Work")), 
            "Online", 
            DATE, 
            TIME, 
            USER1)
            .setOpportunities(OPPORTUNITIES)
            .setAttendees(Collections.unmodifiableSet(new HashSet<>(Arrays.asList(USER1))))
            .build();
    Event event2 =
        new Event.Builder("Chef SHOWDOWN", 
            "Come pit your skills against Manhattan's best home chefs!", 
            new HashSet<>(Arrays.asList("Food", "Cooking")), 
            "Online", 
            DATE, 
            TIME, 
            USER1)
            .setOpportunities(OPPORTUNITIES)
            .setAttendees(Collections.unmodifiableSet(new HashSet<>(Arrays.asList(USER1))))
            .build();
    Event event3 =
        new Event.Builder("Chess tournaments", 
            "Gather all the nerds in your life for the checkmate of a lifetime", 
            new HashSet<>(Arrays.asList("Chess", "Tournaments")), 
            "Online", 
            DATE, 
            TIME, 
            USER1)
            .setOpportunities(OPPORTUNITIES)
            .setAttendees(Collections.unmodifiableSet(new HashSet<>(Arrays.asList(USER1))))
            .build();
    
    SpannerTasks.insertOrUpdateUser(USER1);
    SpannerTasks.insertOrUpdateUser(USER2);
    SpannerTasks.insertOrUpdateUser(USER3);
    SpannerTasks.insertorUpdateEvent(event1);
    SpannerTasks.insertorUpdateEvent(event2);
    SpannerTasks.insertorUpdateEvent(event3);
  }
}

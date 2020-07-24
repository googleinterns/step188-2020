package com.google.sps;

import com.google.sps.data.Event;
import com.google.sps.data.User;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/** */
@RunWith(JUnit4.class)
public final class UserTest {
  private static final String NAME = "Bob Smith";
  private static final String EMAIL = "bobsmith@example.com";
  private static final Set<String> INTERESTS = new HashSet<>(Arrays.asList("Conservation", "Food"));
  private static final Set<String> NEW_INTERESTS =
      new HashSet<>(Arrays.asList("Conservation", "Food", "Music"));
  private static final Set<String> SKILLS = new HashSet<>(Arrays.asList("Cooking"));
  private static final String NEW_INTEREST = "Music";

  // The following fields will be instantiated with meaningful Events after the Events class is
  // written to follow the Builder pattern.
  private static final Set<Event> EVENTS_HOSTING = Collections.emptySet();
  private static final Set<Event> EVENTS_PARTICIPATING = Collections.emptySet();
  private static final Set<Event> EVENTS_VOLUNTEERING = Collections.emptySet();

  @Test
  public void buildInstanceWithAllFields() {
    // Create a User with all fields set, and verify that all fields are correctly set
    User user =
        new User.Builder(NAME, EMAIL)
            .setInterests(INTERESTS)
            .setSkills(SKILLS)
            .setEventsHosting(EVENTS_HOSTING)
            .setEventsParticipating(EVENTS_PARTICIPATING)
            .setEventsVolunteering(EVENTS_VOLUNTEERING)
            .build();

    Assert.assertEquals(NAME, user.getName());
    Assert.assertEquals(EMAIL, user.getEmail());
    Assert.assertEquals(INTERESTS, user.getInterests());
    Assert.assertEquals(SKILLS, user.getSkills());
    Assert.assertEquals(EVENTS_HOSTING, user.getEventsHosting());
    Assert.assertEquals(EVENTS_PARTICIPATING, user.getEventsParticipating());
    Assert.assertEquals(EVENTS_VOLUNTEERING, user.getEventsVolunteering());
  }

  @Test
  public void transferInstanceToBuilderWithAllFields() {
    // Create a User with all fields set, and check that its fields can be correctly transferred to
    // its Builder.
    User expectedUser =
        new User.Builder(NAME, EMAIL)
            .setInterests(INTERESTS)
            .setSkills(SKILLS)
            .setEventsHosting(EVENTS_HOSTING)
            .setEventsParticipating(EVENTS_PARTICIPATING)
            .setEventsVolunteering(EVENTS_VOLUNTEERING)
            .build();

    User actualUser = expectedUser.toBuilder().build();
    Assert.assertTrue(EqualsBuilder.reflectionEquals(expectedUser, actualUser));
  }

  @Test
  public void addToExistingInterests() {
    // Add a new interest to a User's existing set of interests.
    User user = new User.Builder(NAME, EMAIL).setInterests(INTERESTS).build();
    user = user.toBuilder().addInterest(NEW_INTEREST).build();

    Assert.assertEquals(NEW_INTERESTS, user.getInterests());
  }
}

package com.google.sps;

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

/** */
@RunWith(JUnit4.class)
public final class UserTest {
  private static final String NAME = "Bob Smith";
  private static final String EMAIL = "bobsmith@example.com";
  private static final Set<String> INTERESTS = Collections.unmodifiableSet(new HashSet<>(Arrays.asList("Conservation", "Food")));
  private static final Set<String> SKILLS = Collections.unmodifiableSet(new HashSet<>(Arrays.asList("Cooking")));
  private static final String INTEREST_TO_ADD = "Music";
  private static final String SKILL_TO_ADD = "Juggling";

  // The following fields will be instantiated with meaningful Events after the Events class is written to follow the Builder pattern.
  private static final Set<Event> EVENTS_HOSTING = Collections.emptySet();
  private static final Set<Event> EVENTS_PARTICIPATING = Collections.emptySet();
  private static final Set<Event> EVENTS_VOLUNTEERING = Collections.emptySet();

  @Test
  public void getNameAfterBuild() {
    // Ensures that user's name is properly set
    User user = new User.Builder(NAME, EMAIL).build();
    String actual = user.getName();
    String expected = NAME;

    Assert.assertEquals(expected, actual);
  }

  @Test
  public void getEmailAfterBuild() {
    // Ensures that user's email is properly set
    User user = new User.Builder(NAME, EMAIL).build();
    String actual = user.getEmail();
    String expected = EMAIL;

    Assert.assertEquals(expected, actual);
  }

  @Test
  public void getInterestsAfterBuild() {
    // Ensures that user's interests are properly set
    User user = new User.Builder(NAME, EMAIL).interests(INTERESTS).build();
    Set<String> actual = user.getInterests();
    Set<String> expected = INTERESTS;

    Assert.assertEquals(expected, actual);
  }

  @Test
  public void getSkillsAfterBuild() {
    // Ensures that user's skills are properly set
    User user = new User.Builder(NAME, EMAIL).skills(SKILLS).build();
    Set<String> actual = user.getSkills();
    Set<String> expected = SKILLS;

    Assert.assertEquals(expected, actual);
  }

  @Test
  public void getEventsHostingAfterBuild() {
    // Ensures that user's events as host are properly set
    User user = new User.Builder(NAME, EMAIL).eventsHosting(EVENTS_HOSTING).build();
    Set<Event> actual = user.getEventsHosting();
    Set<Event> expected = EVENTS_HOSTING;

    Assert.assertEquals(expected, actual);
  }

  @Test
  public void getEventsParticipatingAfterBuild() {
    // Ensures that user's events as participant are properly set
    User user = new User.Builder(NAME, EMAIL).eventsParticipating(EVENTS_PARTICIPATING).build();
    Set<Event> actual = user.getEventsParticipating();
    Set<Event> expected = EVENTS_PARTICIPATING;

    Assert.assertEquals(expected, actual);
  }


  @Test
  public void getEventsVolunteeringAfterBuild() {
    // Ensures that user's events as volunteer are properly set
    User user = new User.Builder(NAME, EMAIL).eventsVolunteering(EVENTS_VOLUNTEERING).build();
    Set<Event> actual = user.getEventsVolunteering();
    Set<Event> expected = EVENTS_VOLUNTEERING;

    Assert.assertEquals(expected, actual);
  }

  @Test
  public void addInterestWithBuilder() {
    // Have a user with no interests initially, add an interest, and check that it was added properly
    User user = new User.Builder(NAME, EMAIL).build();

    Set<String> newInterests = user.getInterests();
    newInterests.add(INTEREST_TO_ADD);

    User.Builder newUserBuilder = user.toBuilder();
    User userWithInterest = newUserBuilder.interests(newInterests).build();
    newUserBuilder.mergeFrom(userWithInterest);

    user = newUserBuilder.build();

    Set<String> actual = user.getInterests();
    Set<String> expected = new HashSet<>(Arrays.asList(INTEREST_TO_ADD));

    Assert.assertEquals(expected, actual);
  }

  @Test
  public void addSkillWithBuilder() {
    // Have a user with no skills initially, add a skill, and check that it was added properly
    User user = new User.Builder(NAME, EMAIL).build();

    Set<String> newSkills = user.getSkills();
    newSkills.add(SKILL_TO_ADD);

    User.Builder newUserBuilder = user.toBuilder();
    User userWithSkill = newUserBuilder.skills(newSkills).build();
    newUserBuilder.mergeFrom(userWithSkill);

    user = newUserBuilder.build();

    Set<String> actual = user.getSkills();
    Set<String> expected = new HashSet<>(Arrays.asList(SKILL_TO_ADD));

    Assert.assertEquals(expected, actual);
  }

  @Test
  @Ignore
  public void addEventsHostingWithBuilder() {
    // NOTE: Will be written after Event class is written in Builder pattern
  }

  @Test
  @Ignore
  public void addEventsParticipatingWithBuilder() {
    // NOTE: Will be written after Event class is written in Builder pattern
  }

  @Test
  @Ignore
  public void addEventsVolunteeringWithBuilder() {
    // NOTE: Will be written after Event class is written in Builder pattern
  }
}

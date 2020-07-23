package com.google.sps;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.sps.data.VolunteeringOpportunity;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/** */
@RunWith(JUnit4.class)
public final class VolunteerOpportunityTest {
  private static VolunteeringOpportunity opportunity;
  private static VolunteeringOpportunity opportunityWithSkills;
  private static final String NAME = "Performer";
  private static final int NUMBER_OF_SPOTS = 240;
  public static final Set<String> SKILLS = ImmutableSet.of("Playing an instrument", "Performing");
  public static final Set<String> NEW_SKILLS = ImmutableSet.of("Singing", "Performing");

  @Before
  public void setUp() {
    opportunity = new VolunteeringOpportunity.Builder(NAME, NUMBER_OF_SPOTS).build();
    opportunityWithSkills =
        new VolunteeringOpportunity.Builder(NAME, NUMBER_OF_SPOTS).requiredSkills(SKILLS).build();
  }

  @Test
  public void getNameAfterBuild() {
    String actual = opportunity.getName();
    String expected = NAME;

    Assert.assertEquals(expected, actual);
  }

  @Test
  public void getNumSpotsAfterBuild() {
    int actual = opportunity.getNumSpotsLeft();
    int expected = NUMBER_OF_SPOTS;

    Assert.assertEquals(expected, actual);
  }

  @Test
  public void getSkillsAfterBuild() {
    Set<String> actual = opportunityWithSkills.getRequiredSkills();
    Set<String> expected = SKILLS;

    Assert.assertEquals(expected, actual);
  }

  @Test
  public void setSkills() {
    VolunteeringOpportunity changedOpportunity =
        opportunityWithSkills.toBuilder().requiredSkills(NEW_SKILLS).build();

    Set<String> actual = changedOpportunity.getRequiredSkills();
    Set<String> expected = NEW_SKILLS;

    Assert.assertEquals(expected, actual);
  }

  @Test
  public void mergeFromUsingSkills() {
    VolunteeringOpportunity.Builder opportunityBuilder = opportunityWithSkills.toBuilder();
    VolunteeringOpportunity changedOpportunity =
        opportunityWithSkills.toBuilder().requiredSkills(NEW_SKILLS).build();
    opportunityBuilder.mergeFrom(changedOpportunity);
    opportunityWithSkills = opportunityBuilder.build();

    Set<String> actual = opportunityWithSkills.getRequiredSkills();
    Set<String> expected = NEW_SKILLS;

    Assert.assertEquals(expected, actual);
  }
}
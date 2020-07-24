package com.google.sps;

import com.google.common.collect.ImmutableSet;
import com.google.sps.data.VolunteeringOpportunity;
import java.util.Set;
import java.util.HashSet;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/** */
@RunWith(JUnit4.class)
public final class VolunteerOpportunityTest {
  private static final String NAME = "Performer";
  private static final int NUMBER_OF_SPOTS = 240;
  private static final Set<String> SKILLS = ImmutableSet.of("Playing an instrument", "Performing");
  public static final Set<String> NEW_SKILLS = ImmutableSet.of("Singing", "Performing");
  public static final Set<String> SKILLS_WITH_DANCING = ImmutableSet.of("Playing an instrument", "Performing", "Dancing");
  public static final String DANCING = "Dancing";

  @Before
  public void setUp() {
  }

  @Test
  public void getAttributesAfterBuild() {
    VolunteeringOpportunity opportunity =
        new VolunteeringOpportunity.Builder(NAME, NUMBER_OF_SPOTS).setRequiredSkills(SKILLS).build();

    String actualName = opportunity.getName();
    String expectedName = NAME;
    int actualNumSpotsLeft = opportunity.getNumSpotsLeft();
    int expectedNumSpotsLeft = NUMBER_OF_SPOTS;
    Set<String> actualSkills = opportunity.getRequiredSkills();
    Set<String> expectedSkills = SKILLS;

    Assert.assertEquals(actualName, expectedName);
    Assert.assertEquals(actualNumSpotsLeft, expectedNumSpotsLeft);
    Assert.assertEquals(actualSkills, expectedSkills);
  }

  @Test
  public void getAttributesWithToBuild() {
    VolunteeringOpportunity opportunity =
        new VolunteeringOpportunity.Builder(NAME, NUMBER_OF_SPOTS).setRequiredSkills(SKILLS).build();
    VolunteeringOpportunity copyOfOpportunity = opportunity.toBuilder().build();
    
    String actualName = copyOfOpportunity.getName();
    String expectedName = NAME;
    int actualNumSpotsLeft = copyOfOpportunity.getNumSpotsLeft();
    int expectedNumSpotsLeft = NUMBER_OF_SPOTS;
    Set<String> actualSkills = copyOfOpportunity.getRequiredSkills();
    Set<String> expectedSkills = SKILLS;

    Assert.assertEquals(actualName, expectedName);
    Assert.assertEquals(actualNumSpotsLeft, expectedNumSpotsLeft);
    Assert.assertEquals(actualSkills,expectedSkills);
  }

  @Test
  public void addSkillsWithBuild() {
    Set<String> skills = new HashSet<String>();
    skills.add("Playing an instrument");
    skills.add("Performing");
    VolunteeringOpportunity opportunityWithSkills =
        new VolunteeringOpportunity.Builder(NAME, NUMBER_OF_SPOTS).setRequiredSkills(skills).build();

    VolunteeringOpportunity changedOpportunity =
        opportunityWithSkills.toBuilder().addRequiredSkill(DANCING).build();

    Set<String> actual = changedOpportunity.getRequiredSkills();
    Set<String> expected = SKILLS_WITH_DANCING;

    Assert.assertEquals(expected, actual);
  }

  @Test
  public void removeSkillsWithBuild() {
     Set<String> skills = new HashSet<String>();
    skills.add("Playing an instrument");
    skills.add("Performing");
    VolunteeringOpportunity opportunityWithSkills =
        new VolunteeringOpportunity.Builder(NAME, NUMBER_OF_SPOTS).setRequiredSkills(skills).build();
    VolunteeringOpportunity changedOpportunity =
        opportunityWithSkills.toBuilder().removeRequiredSkill(DANCING).build();

    Set<String> actual = changedOpportunity.getRequiredSkills();
    Set<String> expected = SKILLS;

    Assert.assertEquals(expected, actual);
  }
}

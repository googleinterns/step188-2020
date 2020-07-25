package com.google.sps;

import com.google.common.collect.ImmutableSet;
import com.google.sps.data.VolunteeringOpportunity;
import java.util.HashSet;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/** */
@RunWith(JUnit4.class)
public final class VolunteerOpportunityTest {
  private static final String NAME = "Performer";
  private static final int NUMBER_OF_SPOTS = 240;
  private static final String PLAYING_AN_INSTRUMENT = "Playing an instrument";
  private static final String PERFORMING = "Performing";
  private static final String DANCING = "Dancing";
  private static final Set<String> SKILLS = ImmutableSet.of(PLAYING_AN_INSTRUMENT, PERFORMING);
  private static final Set<String> SKILLS_WITH_DANCING =
      ImmutableSet.of(PLAYING_AN_INSTRUMENT, PERFORMING, DANCING);

  @Test
  public void createInstanceUsingBuilder() {
    VolunteeringOpportunity opportunity =
        new VolunteeringOpportunity.Builder(NAME, NUMBER_OF_SPOTS)
            .setRequiredSkills(SKILLS)
            .build();

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
  public void getBuilderFromInstance() {
    VolunteeringOpportunity opportunity =
        new VolunteeringOpportunity.Builder(NAME, NUMBER_OF_SPOTS)
            .setRequiredSkills(SKILLS)
            .build();
    VolunteeringOpportunity copyOfOpportunity = opportunity.toBuilder().build();

    String actualName = copyOfOpportunity.getName();
    String expectedName = NAME;
    int actualNumSpotsLeft = copyOfOpportunity.getNumSpotsLeft();
    int expectedNumSpotsLeft = NUMBER_OF_SPOTS;
    Set<String> actualSkills = copyOfOpportunity.getRequiredSkills();
    Set<String> expectedSkills = SKILLS;

    Assert.assertEquals(actualName, expectedName);
    Assert.assertEquals(actualNumSpotsLeft, expectedNumSpotsLeft);
    Assert.assertEquals(actualSkills, expectedSkills);
  }

  @Test
  public void addSkillsWithBuild() {
    Set<String> skills = new HashSet<String>();
    skills.add(PLAYING_AN_INSTRUMENT);
    skills.add(PERFORMING);
    VolunteeringOpportunity opportunityWithSkills =
        new VolunteeringOpportunity.Builder(NAME, NUMBER_OF_SPOTS)
            .setRequiredSkills(skills)
            .build();
    VolunteeringOpportunity changedOpportunity =
        opportunityWithSkills.toBuilder().addRequiredSkill(DANCING).build();

    Set<String> actual = changedOpportunity.getRequiredSkills();
    Set<String> expected = SKILLS_WITH_DANCING;

    Assert.assertEquals(expected, actual);
  }

  @Test
  public void removeSkillsWithBuild() {
    Set<String> skills = new HashSet<String>();
    skills.add(PLAYING_AN_INSTRUMENT);
    skills.add(PERFORMING);
    skills.add(DANCING);
    VolunteeringOpportunity opportunityWithSkills =
        new VolunteeringOpportunity.Builder(NAME, NUMBER_OF_SPOTS)
            .setRequiredSkills(skills)
            .build();
    VolunteeringOpportunity changedOpportunity =
        opportunityWithSkills.toBuilder().removeRequiredSkill(DANCING).build();

    Set<String> actual = changedOpportunity.getRequiredSkills();
    Set<String> expected = SKILLS;

    Assert.assertEquals(expected, actual);
  }
}

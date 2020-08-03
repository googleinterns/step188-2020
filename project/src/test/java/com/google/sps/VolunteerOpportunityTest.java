package com.google.sps;

import com.google.common.collect.ImmutableSet;
import com.google.sps.data.VolunteeringOpportunity;
import java.util.HashSet;
import java.util.Set;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

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
  private static final String EVENT_ID = "0883de79-17d7-49a3-a866-dbd5135062a8";

  @Test
  public void createInstanceUsingBuilder() {
    VolunteeringOpportunity opportunity =
        new VolunteeringOpportunity.Builder(EVENT_ID, NAME, NUMBER_OF_SPOTS)
            .setRequiredSkills(SKILLS)
            .build();

    Assert.assertEquals(NAME, opportunity.getName());
    Assert.assertEquals(NUMBER_OF_SPOTS, opportunity.getNumSpotsLeft());
    Assert.assertEquals(SKILLS, opportunity.getRequiredSkills());
  }

  @Test
  public void getBuilderFromInstance() {
    VolunteeringOpportunity opportunity =
        new VolunteeringOpportunity.Builder(EVENT_ID, NAME, NUMBER_OF_SPOTS)
            .setRequiredSkills(SKILLS)
            .build();
    VolunteeringOpportunity copyOfOpportunity = opportunity.toBuilder().build();

    Assert.assertTrue(EqualsBuilder.reflectionEquals(opportunity, copyOfOpportunity));
  }

  @Test
  public void addSkillsWithBuild() {
    Set<String> skills = new HashSet<String>();
    skills.add(PLAYING_AN_INSTRUMENT);
    skills.add(PERFORMING);
    VolunteeringOpportunity opportunityWithSkills =
        new VolunteeringOpportunity.Builder(EVENT_ID, NAME, NUMBER_OF_SPOTS)
            .setRequiredSkills(skills)
            .build();
    VolunteeringOpportunity changedOpportunity =
        opportunityWithSkills.toBuilder().addRequiredSkill(DANCING).build();

    MatcherAssert.assertThat(
        changedOpportunity.getRequiredSkills(),
        CoreMatchers.hasItems(PLAYING_AN_INSTRUMENT, PERFORMING, DANCING));
  }

  @Test
  public void removeSkillsWithBuild() {
    Set<String> skills = new HashSet<String>();
    skills.add(PLAYING_AN_INSTRUMENT);
    skills.add(PERFORMING);
    skills.add(DANCING);
    VolunteeringOpportunity opportunityWithSkills =
        new VolunteeringOpportunity.Builder(EVENT_ID, NAME, NUMBER_OF_SPOTS)
            .setRequiredSkills(skills)
            .build();
    VolunteeringOpportunity changedOpportunity =
        opportunityWithSkills.toBuilder().removeRequiredSkill(DANCING).build();

    Assert.assertEquals(SKILLS, changedOpportunity.getRequiredSkills());
  }
}

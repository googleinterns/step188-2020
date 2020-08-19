package com.google.sps;

import com.google.cloud.Date;
import com.google.sps.data.Event;
import com.google.sps.data.User;
import com.google.sps.data.VolunteeringOpportunity;
import com.google.sps.utilities.EventRanker;
import com.google.sps.utilities.TestUtils;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/** Tests the ranking algorithm for event discovery */
@RunWith(JUnit4.class)
public final class EventRankerTest {
  private static final String CONSERVATION = "conservation";
  private static final String FOOD = "food";
  private static final String MUSIC = "music";
  private static final String SEWING = "sewing";
  private static final Set<String> INTERESTS_CONSERVATION_FOOD =
      new HashSet<>(Arrays.asList(CONSERVATION, FOOD));
  private static final Set<String> SKILLS_MUSIC = new HashSet<>(Arrays.asList(MUSIC));
  private static Event EVENT_CONSERVATION_FOOD_MUSIC;
  private static Event EVENT_FOOD_MUSIC;
  private static Event EVENT_CONSERVATION_MUSIC;
  private static Event EVENT_FOOD;
  private static Event EVENT_SEWING;
  private static User USER_CONSERVATION_FOOD_MUSIC;
  private static User USER_NO_INTERESTS_OR_SKILLS;
  private static VolunteeringOpportunity OPPORTUNITY_MUSIC;

  @BeforeClass
  public static void setUp() throws Exception {
    int currentYear = new java.util.Date().getYear();
    EVENT_CONSERVATION_FOOD_MUSIC =
        TestUtils.newEvent().toBuilder()
            .setLabels(new HashSet<>(Arrays.asList(CONSERVATION, FOOD, MUSIC)))
            .build();
    EVENT_FOOD_MUSIC =
        TestUtils.newEvent().toBuilder()
            .setLabels(new HashSet<>(Arrays.asList(FOOD, MUSIC)))
            .setDate(Date.fromYearMonthDay(currentYear + 1, 1, 1))
            .build();
    EVENT_CONSERVATION_MUSIC =
        TestUtils.newEvent().toBuilder()
            .setLabels(new HashSet<>(Arrays.asList(CONSERVATION, MUSIC)))
            .setDate(Date.fromYearMonthDay(currentYear + 2, 1, 1))
            .build();
    EVENT_FOOD =
        TestUtils.newEvent().toBuilder().setLabels(new HashSet<>(Arrays.asList(FOOD))).build();
    EVENT_SEWING =
        TestUtils.newEvent().toBuilder().setLabels(new HashSet<>(Arrays.asList(SEWING))).build();
    USER_CONSERVATION_FOOD_MUSIC =
        TestUtils.newUser().toBuilder()
            .setInterests(INTERESTS_CONSERVATION_FOOD)
            .setSkills(SKILLS_MUSIC)
            .build();
    USER_NO_INTERESTS_OR_SKILLS =
        TestUtils.newUser().toBuilder().setInterests(new HashSet<>()).setSkills(new HashSet<>()).build();
    OPPORTUNITY_MUSIC =
        new VolunteeringOpportunity.Builder(EVENT_FOOD_MUSIC.getId(), "", 1).build();
    EVENT_FOOD_MUSIC = EVENT_FOOD_MUSIC.toBuilder().addOpportunity(OPPORTUNITY_MUSIC).build();
  }

  @Test
  public void testRankingEmptyEvents() throws IOException {
    Assert.assertEquals(
        new ArrayList<Event>(),
        EventRanker.rankEvents(USER_CONSERVATION_FOOD_MUSIC, new HashSet<Event>()));
  }

  @Test
  public void testRankingUntiedEvents() throws IOException {
    Set<Event> eventsToRank =
        new HashSet<>(
            Arrays.asList(
                EVENT_FOOD, EVENT_SEWING, EVENT_CONSERVATION_FOOD_MUSIC, EVENT_FOOD_MUSIC));
    List<Event> expectedEventRanking =
        Arrays.asList(EVENT_CONSERVATION_FOOD_MUSIC, EVENT_FOOD_MUSIC, EVENT_FOOD, EVENT_SEWING);

    List<Event> actualEventRanking =
        EventRanker.rankEvents(USER_CONSERVATION_FOOD_MUSIC, eventsToRank);

    Assert.assertEquals(expectedEventRanking, actualEventRanking);
  }

  @Test
  public void testRankingTiedEvents() throws IOException {
    Set<Event> eventsToRank =
        new HashSet<>(
            Arrays.asList(
                EVENT_CONSERVATION_MUSIC,
                EVENT_FOOD,
                EVENT_SEWING,
                EVENT_CONSERVATION_FOOD_MUSIC,
                EVENT_FOOD_MUSIC));
    List<Event> expectedEventRanking =
        Arrays.asList(
            EVENT_CONSERVATION_FOOD_MUSIC,
            EVENT_FOOD_MUSIC,
            EVENT_CONSERVATION_MUSIC,
            EVENT_FOOD,
            EVENT_SEWING);

    List<Event> actualEventRanking =
        EventRanker.rankEvents(USER_CONSERVATION_FOOD_MUSIC, eventsToRank);

    Assert.assertEquals(expectedEventRanking, actualEventRanking);
  }

  // @Test
  // public void testRankingNoInterestsOrSkillsRankByDate() throws IOException {
  //   Event EVENT_EARLIEST = TestUtils.newEvent().toBuilder().mergeFrom(EVENT_FOOD)
  // }
}

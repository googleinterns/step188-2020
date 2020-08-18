package com.google.sps;

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
public final class EventRankingTest {
  private static final EventRanker eventRanker = new EventRanker();
  private static final String EMAIL = "test@example.com";
  private static final String CONSERVATION = "conservation";
  private static final String FOOD = "food";
  private static final String MUSIC = "music";
  private static final String SEWING = "sewing";
  private static final Set<String> INTERESTS_CONSERVATION_FOOD =
      new HashSet<>(Arrays.asList(CONSERVATION, FOOD));
  private static final Set<String> SKILLS_MUSIC =
      new HashSet<>(Arrays.asList(MUSIC));
  private static Event EVENT_CONSERVATION_FOOD_MUSIC;
  private static Event EVENT_FOOD_MUSIC;
  private static Event EVENT_CONSERVATION_MUSIC;
  private static Event EVENT_FOOD;
  private static Event EVENT_SEWING;
  private static User USER_CONSERVATION_FOOD_MUSIC;
  private static VolunteeringOpportunity OPPORTUNITY_MUSIC;

  @BeforeClass
  public static void setUp() throws Exception {
    EVENT_CONSERVATION_FOOD_MUSIC = TestUtils
        .newEvent()
        .toBuilder()
        .setLabels(new HashSet<>(Arrays.asList(CONSERVATION, FOOD, MUSIC)))
        .build();
    EVENT_FOOD_MUSIC = TestUtils
        .newEvent()
        .toBuilder()
        .setLabels(new HashSet<>(Arrays.asList(FOOD, MUSIC)))
        .build();
    EVENT_CONSERVATION_MUSIC = TestUtils
        .newEvent()
        .toBuilder()
        .setLabels(new HashSet<>(Arrays.asList(CONSERVATION, MUSIC)))
        .build();
    EVENT_FOOD = TestUtils
        .newEvent()
        .toBuilder()
        .setLabels(new HashSet<>(Arrays.asList(FOOD)))
        .build();
    EVENT_SEWING = TestUtils
        .newEvent()
        .toBuilder()
        .setLabels(new HashSet<>(Arrays.asList(SEWING)))
        .build();
    USER_CONSERVATION_FOOD_MUSIC = TestUtils
        .newUser(EMAIL)
        .toBuilder()
        .setInterests(INTERESTS_CONSERVATION_FOOD)
        .setSkills(SKILLS_MUSIC)
        .build();
    OPPORTUNITY_MUSIC = new VolunteeringOpportunity.Builder(EVENT_FOOD_MUSIC.getId(), "", 1);
    EVENT_FOOD_MUSIC = EVENT_FOOD_MUSIC.toBuilder().addVolunteeringOpportunity(OPPORTUNITY_MUSIC);
  }

  @Test
  public void testRankingEmptyEvents() throws IOException {
    Assert.assertEqual(new ArrayList<Event>(), eventRanker.rankEvents(new HashSet<Event>()));
  }

  @Test
  public void testRankingUntiedEvents() throws IOException {
    Set<Event> eventsToRank = new HashSet<>(
        Arrays.asList(EVENT_FOOD, EVENT_SEWING, EVENT_CONSERVATION_FOOD_MUSIC, EVENT_FOOD_MUSIC));
    List<Event> expectedEventRanking =
        Arrays.asList(EVENT_CONSERVATION_FOOD_MUSIC, EVENT_FOOD_MUSIC, EVENT_FOOD, EVENT_SEWING);

    List<Event> actualEventRanking = eventRanker.rankEvents(USER_CONSERVATION_FOOD_MUSIC, eventsToRank);

    Assert.assertEquals(expectedEventRanking, actualEventRanking);
  }

  @Test
  public void testRankingTiedEvents() throws IOException {
    Set<Event> eventsToRank = new HashSet<>(
        Arrays.asList(EVENT_CONSERVATION_MUSIC, EVENT_FOOD, EVENT_SEWING, EVENT_CONSERVATION_FOOD_MUSIC, EVENT_FOOD_MUSIC));
    List<Event> expectedEventRanking =
        Arrays.asList(EVENT_CONSERVATION_FOOD_MUSIC, EVENT_FOOD_MUSIC, EVENT_CONSERVATION_MUSIC, EVENT_FOOD, EVENT_SEWING);

    List<Event> actualEventRanking = eventRanker.rankEvents(USER_CONSERVATION_FOOD_MUSIC, eventsToRank);

    Assert.assertEquals(expectedEventRanking, actualEventRanking);
  }
}

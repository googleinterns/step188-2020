package com.google.sps;

import com.google.cloud.Date;
import com.google.sps.data.Event;
import com.google.sps.data.User;
import com.google.sps.data.VolunteeringOpportunity;
import com.google.sps.utilities.CommonUtils;
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
  private static User USER;
  private static VolunteeringOpportunity OPPORTUNITY_MUSIC;
  private static String NAME = "Bob Smith";
  private static String EMAIL = "test@example.com";
  private static String INVALID_EMAIL = "invalid@example.com";

  @BeforeClass
  public static void setUp() throws Exception {
    setUpEventsAndUsers();
  }

  @Test
  public void testRankingEmptyEvents() throws IOException {
    Assert.assertEquals(
        new ArrayList<Event>(),
        EventRanker.rankEvents(USER, new HashSet<Event>()));
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
    Event EVENT_TIED_EARLIER =
        advanceEventByYears(
            TestUtils.newEvent().toBuilder().mergeFrom(EVENT_FOOD_MUSIC).build(),
            1);
    Event EVENT_TIED_LATER =
        advanceEventByYears(
            TestUtils.newEvent().toBuilder().mergeFrom(EVENT_CONSERVATION_MUSIC).build(),
            2);
    Set<Event> eventsToRank =
        new HashSet<>(
            Arrays.asList(
                EVENT_TIED_LATER,
                EVENT_FOOD,
                EVENT_SEWING,
                EVENT_CONSERVATION_FOOD_MUSIC,
                EVENT_TIED_EARLIER));
    List<Event> expectedEventRanking =
        Arrays.asList(
            EVENT_CONSERVATION_FOOD_MUSIC,
            EVENT_TIED_EARLIER,
            EVENT_TIED_LATER,
            EVENT_FOOD,
            EVENT_SEWING);

    List<Event> actualEventRanking =
        EventRanker.rankEvents(USER_CONSERVATION_FOOD_MUSIC, eventsToRank);

    Assert.assertEquals(expectedEventRanking, actualEventRanking);
  }

  @Test
  public void testRankingNoInterestsOrSkillsRankByDate() throws IOException {
    Event EVENT_ONE_YEAR_FUTURE = advanceEventByYears(TestUtils.newEvent(), 1);
    Event EVENT_TWO_YEARS_FUTURE = advanceEventByYears(TestUtils.newEvent(), 2);
    Event EVENT_THREE_YEARS_FUTURE = advanceEventByYears(TestUtils.newEvent(), 3);
    Set<Event> eventsToRank =
        new HashSet<>(
            Arrays.asList(
                EVENT_TWO_YEARS_FUTURE,
                EVENT_THREE_YEARS_FUTURE,
                EVENT_ONE_YEAR_FUTURE));
    List<Event> expectedEventRanking =
        Arrays.asList(
            EVENT_ONE_YEAR_FUTURE,
            EVENT_TWO_YEARS_FUTURE,
            EVENT_THREE_YEARS_FUTURE);
    
    List<Event> actualEventRanking =
        EventRanker.rankEvents(USER_NO_INTERESTS_OR_SKILLS, eventsToRank);
    
    Assert.assertEquals(expectedEventRanking, actualEventRanking);
  }

  private static Event advanceEventByYears(Event event, int years) {
    Date date = event.getDate();
    return event
        .toBuilder()
        .setDate(
            Date.fromYearMonthDay(
                date.getYear() + years,
                date.getMonth() + 1,
                date.getDayOfMonth()))
        .build();
  }

  private static void setUpEventsAndUsers() {
    int currentYear = new java.util.Date().getYear();
    USER_CONSERVATION_FOOD_MUSIC =
        new User.Builder(NAME, EMAIL)
            .setInterests(INTERESTS_CONSERVATION_FOOD)
            .setSkills(SKILLS_MUSIC)
            .build();
    EVENT_CONSERVATION_FOOD_MUSIC =
        TestUtils.newEvent().toBuilder()
            .setHost(USER_CONSERVATION_FOOD_MUSIC)
            .setLabels(new HashSet<>(Arrays.asList(CONSERVATION, FOOD, MUSIC)))
            .build();
    EVENT_FOOD_MUSIC =
        TestUtils.newEvent().toBuilder()
            .setHost(USER_CONSERVATION_FOOD_MUSIC)
            .setLabels(new HashSet<>(Arrays.asList(FOOD, MUSIC)))
            .setDate(Date.fromYearMonthDay(currentYear + 1, 1, 1))
            .build();
    EVENT_CONSERVATION_MUSIC =
        TestUtils.newEvent().toBuilder()
            .setHost(USER_CONSERVATION_FOOD_MUSIC)
            .setLabels(new HashSet<>(Arrays.asList(CONSERVATION, MUSIC)))
            .setDate(Date.fromYearMonthDay(currentYear + 2, 1, 1))
            .build();
    EVENT_FOOD =
        TestUtils.newEvent().toBuilder()
            .setHost(USER_CONSERVATION_FOOD_MUSIC)
            .setLabels(new HashSet<>(Arrays.asList(FOOD)))
            .build();
    EVENT_SEWING =
        TestUtils.newEvent().toBuilder()
            .setHost(USER_CONSERVATION_FOOD_MUSIC)
            .setLabels(new HashSet<>(Arrays.asList(SEWING)))
            .build();
    OPPORTUNITY_MUSIC =
        TestUtils.newVolunteeringOpportunityWithEventId(EVENT_FOOD_MUSIC.getId());
    EVENT_FOOD_MUSIC = EVENT_FOOD_MUSIC.toBuilder().addOpportunity(OPPORTUNITY_MUSIC).build();
  }
}

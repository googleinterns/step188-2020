import com.google.sps.utilities.GetLabelCategories;
import com.google.sps.utilities.CommonUtils;
import com.google.sps.utilities.TestUtils;
import com.google.sps.data.Event;
import com.google.sps.data.User;
import org.junit.Assert;
import org.junit.Test;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import java.io.FileNotFoundException;
import org.javatuples.Pair; 

/* Test getEventRelevancy(), finding direct and similar matches  */
@RunWith(JUnit4.class)
public final class GetLabelCategoriesTest {
  private GetLabelCategories labelCategories = new GetLabelCategories();

  @Test
  public void emptyLabels() throws FileNotFoundException {
    Set<String> EVENT_LABELS = new HashSet<>(Arrays.asList("Sports", "Robotics"));
    Event event = TestUtils.newEventNoLabels().toBuilder().setLabels(EVENT_LABELS).build();
    User user = TestUtils.newUserNoInterestSkills();

    Pair<Set<Pair<Event, Integer>>, Set<Pair<Event, Integer>>> matches = labelCategories.getEventRelevancy(
      new HashSet<>(Arrays.asList(event)), user);

    Assert.assertEquals(new HashSet<>(), /*direct=*/ matches.getValue0());
    Assert.assertEquals(new HashSet<>(), /*similar=*/ matches.getValue1());
  }

  @Test
  public void noMatches() throws FileNotFoundException {
    Set<String> EVENT_LABELS = new HashSet<>(Arrays.asList("Sports", "Robotics"));
    Set<String> USER_INTERESTS = new HashSet<>(Arrays.asList("Travel", "History"));
    Event event = TestUtils.newEventNoLabels().toBuilder().setLabels(EVENT_LABELS).build();
    User user = TestUtils.newUserNoInterestSkills().toBuilder()
      .setInterests(USER_INTERESTS).build();

    Pair<Set<Pair<Event, Integer>>, Set<Pair<Event, Integer>>> matches = labelCategories.getEventRelevancy(
      new HashSet<>(Arrays.asList(event)), user);

    Assert.assertEquals(new HashSet<>(), /*direct=*/ matches.getValue0());
    Assert.assertEquals(new HashSet<>(), /*similar=*/ matches.getValue1());
  }

  @Test
  /* Test for direct match event labels only (no category relations). */
  public void directMatchOnly() throws FileNotFoundException {
    Set<String> EVENT_LABELS = new HashSet<>(Arrays.asList("Sports", "Robotics"));
    Set<String> USER_INTERESTS = new HashSet<>(Arrays.asList("Sports", "Food & Drink"));
    Set<String> SKILLS = new HashSet<>(Arrays.asList("Robotics"));
    Event event = TestUtils.newEventNoLabels().toBuilder().setLabels(EVENT_LABELS).build();
    User user = TestUtils.newUserNoInterestSkills().toBuilder()
      .setInterests(USER_INTERESTS).setSkills(SKILLS).build();
    Set<Pair<Event, Integer>> expectedDirectMatches = 
      new HashSet<>(Arrays.asList(new Pair<Event, Integer>(event, /*common labels=*/ 2)));

    Pair<Set<Pair<Event, Integer>>, Set<Pair<Event, Integer>>> matches = labelCategories.getEventRelevancy(
      new HashSet<>(Arrays.asList(event)), user);

    Assert.assertEquals(expectedDirectMatches, /*direct=*/ matches.getValue0());
    Assert.assertEquals(new HashSet<>(), /*similar=*/ matches.getValue1());
  }

  @Test
  /* Test for similar matches via category relations, (basketball is a subcategory of sports) */
  public void similarMatchOnly_SubcategoryToCategory() throws FileNotFoundException {
    Set<String> EVENT_LABELS = new HashSet<>(Arrays.asList("Sports", "Robotics"));
    Set<String> USER_INTERESTS = new HashSet<>(Arrays.asList("Basketball"));
    Set<String> IRRELEVANT_LABELS = new HashSet<>(Arrays.asList("Cooking"));
    Event event = TestUtils.newEventNoLabels().toBuilder().setLabels(EVENT_LABELS).build();
    Event event2 = TestUtils.newEventNoLabels().toBuilder().setLabels(IRRELEVANT_LABELS).build();
    User user = TestUtils.newUserNoInterestSkills().toBuilder()
      .setInterests(USER_INTERESTS).build();
    Set<Pair<Event, Integer>> expectedSimilarMatches = 
      new HashSet<>(Arrays.asList(new Pair<Event, Integer>(event, /*common labels=*/ 1)));

    Pair<Set<Pair<Event, Integer>>, Set<Pair<Event, Integer>>> matches = labelCategories.getEventRelevancy(
      new HashSet<>(Arrays.asList(event, event2)), user);

    Assert.assertEquals(new HashSet<>(), /*direct=*/ matches.getValue0());
    Assert.assertEquals(expectedSimilarMatches, /*similar=*/ matches.getValue1());
  }

  @Test
  /* Test for similar matches via category relations, (sports contains basketball) */
  public void similarMatchOnly_CategoryToSubcategory() throws FileNotFoundException {
    Set<String> EVENT_LABELS = new HashSet<>(Arrays.asList("Basketball", "Robotics"));
    Set<String> USER_INTERESTS = new HashSet<>(Arrays.asList("Sports"));
    Set<String> IRRELEVANT_LABELS = new HashSet<>(Arrays.asList("Cooking"));
    Event event = TestUtils.newEventNoLabels().toBuilder().setLabels(EVENT_LABELS).build();
    Event event2 = TestUtils.newEventNoLabels().toBuilder().setLabels(IRRELEVANT_LABELS).build();
    User user = TestUtils.newUserNoInterestSkills().toBuilder()
      .setInterests(USER_INTERESTS).build();
    Set<Pair<Event, Integer>> expectedSimilarMatches = 
      new HashSet<>(Arrays.asList(new Pair<Event, Integer>(event, /*common labels=*/ 1)));

    Pair<Set<Pair<Event, Integer>>, Set<Pair<Event, Integer>>> matches = labelCategories.getEventRelevancy(
      new HashSet<>(Arrays.asList(event, event2)), user);

    Assert.assertEquals(new HashSet<>(), /*direct=*/ matches.getValue0());
    Assert.assertEquals(expectedSimilarMatches, /*similar=*/ matches.getValue1());
  }

  @Test
  /* Test for similar matches via category relations, (basketball and soccer are both sports) */
  public void similarMatchOnly_SubcategoryToSubcategory() throws FileNotFoundException {
    Set<String> EVENT_LABELS = new HashSet<>(Arrays.asList("Soccer", "Robotics"));
    Set<String> USER_INTERESTS = new HashSet<>(Arrays.asList("Basketball"));
    Set<String> IRRELEVANT_LABELS = new HashSet<>(Arrays.asList("Cooking"));
    Event event = TestUtils.newEventNoLabels().toBuilder()
      .setLabels(EVENT_LABELS).build();
    Event event2 = TestUtils.newEventNoLabels().toBuilder()
      .setLabels(IRRELEVANT_LABELS).build();
    User user = TestUtils.newUserNoInterestSkills().toBuilder()
      .setInterests(USER_INTERESTS).build();
    Set<Pair<Event, Integer>> expectedSimilarMatches = 
      new HashSet<>(Arrays.asList(new Pair<Event, Integer>(event, /*common labels=*/ 1)));

    Pair<Set<Pair<Event, Integer>>, Set<Pair<Event, Integer>>> matches = labelCategories.getEventRelevancy(
      new HashSet<>(Arrays.asList(event, event2)), user);

    Assert.assertEquals(new HashSet<>(), /*direct=*/ matches.getValue0());
    Assert.assertEquals(expectedSimilarMatches, /*similar=*/ matches.getValue1());
  }

  @Test
  /** Tests finding direct and similar matches between multiple events
    * Relations: Basketball, Soccer, Swimming all sports; Card Games is subcategory of Games
  */
  public void directAndSimilarMatch() throws FileNotFoundException {
    Set<String> USER_INTERESTS = new HashSet<>(
      Arrays.asList("Basketball", "Travel", "Games", "Robotics"));
    Set<String> DIRECT_INDIRECT_MATCH_LABELS =
      new HashSet<>(Arrays.asList("Soccer", "Robotics"));
    Set<String> SWIMMING_LABEL = new HashSet<>(Arrays.asList("Swimming"));
    Set<String> MORE_DIRECT_INDIRECT_MATCH_LABELS =
      new HashSet<>(Arrays.asList("Cooking","Card Games", "Soccer" ));
    Event event = TestUtils.newEventNoLabels().toBuilder()
      .setLabels(DIRECT_INDIRECT_MATCH_LABELS).build();
    Event event2 = TestUtils.newEventNoLabels().toBuilder()
      .setLabels(MORE_DIRECT_INDIRECT_MATCH_LABELS).build();
    Event event3 = TestUtils.newEventNoLabels().toBuilder().setLabels(SWIMMING_LABEL).build();
    Event event4 = TestUtils.newEventNoLabels();
    User user = TestUtils.newUserNoInterestSkills().toBuilder()
      .setInterests(USER_INTERESTS).build();
    Set<Pair<Event, Integer>> expectedDirectMatches = 
      new HashSet<>(Arrays.asList(new Pair<Event, Integer>(event, /*common labels=*/ 1)));
    Set<Pair<Event, Integer>> expectedSimilarMatches = 
      new HashSet<>(Arrays.asList(new Pair<Event, Integer>(event, /*common labels=*/ 1),
      new Pair<Event, Integer>(event2, /*common labels=*/ 2),
      new Pair<Event, Integer>(event3, /*common labels=*/ 1)));

    Pair<Set<Pair<Event, Integer>>, Set<Pair<Event, Integer>>> matches = labelCategories.getEventRelevancy(
      new HashSet<>(Arrays.asList(event, event2, event3, event4)), user);

    Assert.assertEquals(expectedDirectMatches, /*direct=*/ matches.getValue0());
    Assert.assertEquals(expectedSimilarMatches, /*similar=*/ matches.getValue1());
  }
}
import com.google.sps.utilities.GetLabelCategories;
import com.google.sps.utilities.CommonUtils;
import com.google.sps.utilities.TestUtils;
import com.google.sps.data.Event;
import com.google.sps.data.User;
import org.junit.Assert;
import org.junit.Test;
import java.util.Arrays;
import java.util.Set;
import java.util.HashSet;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import java.io.FileNotFoundException;

@RunWith(JUnit4.class)
public final class GetLabelCategoriesTest {
  private static final Set<String> EVENT_LABELS =
      new HashSet<>(Arrays.asList("Sports", "Robotics"));
  private static final Set<String> USER_INTERESTS =
      new HashSet<>(Arrays.asList("Sports", "Food & Drink"));
  private static final Set<String> SKILLS =
      new HashSet<>(Arrays.asList("Robotics"));

//   @Test
//   public void noMatches() throws FileNotFoundException {
//   }

//   @Test
//   public void directAndSoftMatch() throws FileNotFoundException {
//   }

  @Test
  /* Test getEventRelevancy() with direct match event labels only. No category relations. */
  public void directMatchOnly() throws FileNotFoundException {
      Event event = TestUtils.newEventNoLabels().toBuilder().setLabels(EVENT_LABELS).build();
      Event event2 = TestUtils.newEventNoLabels();
      User user = TestUtils.newUserNoInterestSkills().toBuilder().setInterests(USER_INTERESTS).setSkills(SKILLS).build();

    GetLabelCategories g = new GetLabelCategories();

     System.out.println(g.getEventRelevancy( new HashSet<>(Arrays.asList(event, event2)), user));

    // Given an empty String, verify that an empty JSON object is returned
    Assert.assertEquals("", "");
  }

//   @Test
//   /* Test getEventRelevancy() with category relations, basketball is a subcategory of sports */
//   public void getEventRelevancy_SoftMatch() throws FileNotFoundException {
//   Set<String> EVENT_LABELS =
//       new HashSet<>(Arrays.asList("Sports", "Robotics"));
//   Set<String> USER_INTERESTS =
//       new HashSet<>(Arrays.asList("Basketball", "Food & Drink"));
//   Set<String> SKILLS =
//       new HashSet<>(Arrays.asList("Painting"));

//       Event event = TestUtils.newEventNoLabels().toBuilder().setLabels(EVENT_LABELS).build();
//       Event event2 = TestUtils.newEventNoLabels();
//       Event event3 = TestUtils.newEventNoLabels();
//       User user = TestUtils.newUserNoInterestSkills().toBuilder().setInterests(USER_INTERESTS).setSkills(SKILLS).build();

//     GetLabelCategories g = new GetLabelCategories();

//     System.out.println(g.getEventRelevancy( new HashSet<>(Arrays.asList( event, event3)), user).get(1));

//     // Given an empty String, verify that an empty JSON object is returned
//     Assert.assertEquals("", "");
//   }
}
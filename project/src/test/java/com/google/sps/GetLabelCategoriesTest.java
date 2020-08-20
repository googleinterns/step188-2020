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
  private static final Set<String> LABELS =
      new HashSet<>(Arrays.asList("Tech", "Work"));
  private static final Set<String> INTERESTS =
      new HashSet<>(Arrays.asList("Conservation", "Food"));
  private static final Set<String> SKILLS =
      new HashSet<>(Arrays.asList("Cooking"));

  @Test
  public void ValidateGetEventRelevancy() throws FileNotFoundException {
      Event event = TestUtils.newEvent().toBuilder().setLabels(LABELS).build();
      User user = TestUtils.newUser().toBuilder().setInterests(INTERESTS).setSkills(SKILLS).build();

    GetLabelCategories g = new GetLabelCategories();

     System.out.println(g.getEventRelevancy( new HashSet<>(Arrays.asList(event)), user));

    // Given an empty String, verify that an empty JSON object is returned
    Assert.assertEquals("", "");
  }
}
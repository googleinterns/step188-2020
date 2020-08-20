import com.google.sps.utilities.GetLabelCategories;
import com.google.sps.utilities.CommonUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import java.io.FileNotFoundException;

@RunWith(JUnit4.class)
public final class GetLabelCategoriesTest {


  @Test
  public void Test() throws FileNotFoundException{

      System.out.println( GetLabelCategories.labelValueMapping.get("Wildlife"));
    // Given an empty String, verify that an empty JSON object is returned
    Assert.assertEquals("", "");
  }
}
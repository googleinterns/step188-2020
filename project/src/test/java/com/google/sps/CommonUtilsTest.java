import com.google.sps.utilities.CommonUtils;
import java.util.HashSet;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.mock.web.MockHttpServletRequest;

/** */
@RunWith(JUnit4.class)
public final class CommonUtilsTest extends Mockito {
  private static final String NAME = "Name";
  private static final String NAME_VALUE = "Bob";
  private static final String JOE = "Joe";
  private static final String JAMES = "James";
  private static final String[] NONEMPTY_VALUES = {JOE, JAMES};
  private static final String[] SOME_EMPTY_VALUES = {JOE, ""};
  private static final String[] EMPTY_VALUES = {"", ""};

  @Test
  public void getParameterWithValueReturnValue() {
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.setParameter(NAME, NAME_VALUE);

    String actualParameterValue = CommonUtils.getParameter(request, NAME, StringUtils.EMPTY);

    Assert.assertEquals(NAME_VALUE, actualParameterValue);
  }

  @Test
  public void getParameterWithoutValueReturnDefaultValue() {
    MockHttpServletRequest request = new MockHttpServletRequest();

    String actualParameterValue = CommonUtils.getParameter(request, NAME, StringUtils.EMPTY);

    Assert.assertEquals(StringUtils.EMPTY, actualParameterValue);
  }

  @Test
  public void getParameterWithNonemptyValuesReturnValues() {
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.setParameter(NAME, NONEMPTY_VALUES);

    Set<String> actualParameterValues = CommonUtils.getParameterValues(request, NAME);

    MatcherAssert.assertThat(actualParameterValues, Matchers.contains(JOE, JAMES));
  }

  @Test
  public void getParameterWithFewEmptyValuesReturnNonemptyValues() {
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.setParameter(NAME, SOME_EMPTY_VALUES);

    Set<String> actualParameterValues = CommonUtils.getParameterValues(request, NAME);

    MatcherAssert.assertThat(actualParameterValues, Matchers.contains(JOE));
  }

  @Test
  public void getParameterWithAllEmptyValuesReturnEmptySet() {
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.setParameter(NAME, EMPTY_VALUES);

    Set<String> actualParameterValues = CommonUtils.getParameterValues(request, NAME);

    Assert.assertTrue(actualParameterValues.isEmpty());
  }
}

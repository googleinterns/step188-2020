import com.google.sps.utilities.CommonUtils;
import org.apache.commons.lang3.StringUtils;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.springframework.mock.web.MockHttpServletRequest;

@RunWith(JUnit4.class)
public final class CommonUtilsTest {
  private static final String NAME = "Name";
  private static final String NAME_VALUE = "Bob";
  private static final String JOE = "Joe";
  private static final String JAMES = "James";

  @Test
  public void getParameterWithValueReturnValue() {
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.setParameter(NAME, NAME_VALUE);

    Assert.assertEquals(NAME_VALUE, CommonUtils.getParameter(request, NAME, StringUtils.EMPTY));
  }

  @Test
  public void getParameterWithoutValueReturnDefaultValue() {
    MockHttpServletRequest request = new MockHttpServletRequest();

    Assert.assertEquals(
        StringUtils.EMPTY, CommonUtils.getParameter(request, NAME, StringUtils.EMPTY));
  }

  @Test
  public void getParameterWithNonemptyValuesReturnValues() {
    MockHttpServletRequest request = new MockHttpServletRequest();
    String[] nameValues = {JOE, JAMES};
    request.setParameter(NAME, nameValues);

    MatcherAssert.assertThat(
        CommonUtils.getParameterValues(request, NAME), Matchers.contains(JOE, JAMES));
  }

  @Test
  public void getParameterWithFewEmptyValuesReturnNonemptyValues() {
    MockHttpServletRequest request = new MockHttpServletRequest();
    String[] nameValues = {JOE, ""};
    request.setParameter(NAME, nameValues);

    MatcherAssert.assertThat(CommonUtils.getParameterValues(request, NAME), Matchers.contains(JOE));
  }

  @Test
  public void getParameterWithAllEmptyValuesReturnEmptySet() {
    MockHttpServletRequest request = new MockHttpServletRequest();
    String[] nameValues = {"", ""};
    request.setParameter(NAME, nameValues);

    Assert.assertTrue(CommonUtils.getParameterValues(request, NAME).isEmpty());
  }
}

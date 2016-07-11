package io.github.stephenc.require;

import org.junit.Test;

import static io.github.stephenc.require.Require.*;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

/**
 * @author Stephen Connolly
 */
public class RequireTest {

    @Test(expected = RequirementViolatedException.class)
    public void errors() {
        Require.error();
    }

    @Test
    public void errorWithNoMessageToString() {
        try {
            Require.error();
        } catch (RequirementViolatedException e) {
            assertThat(e.toString(), is("io.github.stephenc.require.RequirementViolatedException: "));
        }
    }

    @Test
    public void failWithMessageToString() {
        try {
            Require.error("woops!");
        } catch (RequirementViolatedException e) {
            assertThat(e.toString(), is("io.github.stephenc.require.RequirementViolatedException: woops!"));
        }
    }

    @Test
    public void assertThatIncludesDescriptionOfTestedValueInErrorMessage() {
        String expected = "expected";
        String actual = "actual";

        String expectedMessage = "identifier\nExpected: \"expected\"\n     but: was \"actual\"";

        try {
            requireThat("identifier", actual, equalTo(expected));
        } catch (RequirementViolatedException e) {
            assertThat(e.getMessage(), is(expectedMessage));
        }
    }

    @Test
    public void assertThatIncludesAdvancedMismatch() {
        String expectedMessage =
                "identifier\nExpected: is an instance of java.lang.Integer\n     but: \"actual\" is a java.lang.String";

        try {
            requireThat("identifier", "actual", is(instanceOf(Integer.class)));
        } catch (RequirementViolatedException e) {
            assertThat(e.getMessage(), is(expectedMessage));
        }
    }

    @Test
    public void assertThatDescriptionCanBeElided() {
        String expected = "expected";
        String actual = "actual";

        String expectedMessage = "\nExpected: \"expected\"\n     but: was \"actual\"";

        try {
            requireThat(actual, equalTo(expected));
        } catch (RequirementViolatedException e) {
            assertThat(e.getMessage(), is(expectedMessage));
        }
    }

}

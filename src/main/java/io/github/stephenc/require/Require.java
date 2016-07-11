/*
 * The MIT License
 *
 * Copyright (c) 2016, Stephen Connolly
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package io.github.stephenc.require;

import org.hamcrest.Matcher;

import static org.hamcrest.CoreMatchers.is;

/**
 * A set of methods useful for stating requirement conditions in which the test is valid. There are four outcomes of any
 * test in JUnit:
 * <ul>
 * <li>PASSED</li>
 * <li>FAILED</li>
 * <li>ERROR</li>
 * <li>SKIPPED</li>
 * </ul>
 * The PASS/FAIL distinction is the most obvious, however the FAIL/ERROR, FAIL/SKIP and ERROR/SKIP distinctions are not.
 * <p>
 * A test should be SKIPPED when external conditions mean that the test cannot be executed. So:
 * <ul>
 * <li>
 * if a test is written assuming it has a connection to a specific vendor's database
 * </li>
 * <li>
 * if a test is written assuming the underlying operating system is/is not Windows
 * </li>
 * <li>
 * etc
 * </li>
 * </ul>
 * These are all external (to the module under test) conditions. It would be wrong for the test to declare itself as
 * broken. It should just declare itself skipped.
 * <p>
 * A test should ERROR when internal conditions mean that the test cannot be executed. So:
 * <ul>
 * <li>
 * There is (or should be) another test that verifies that the home page has a login link. This test is verifying
 * that the user can login. So the test requires that the home page has a login link in order for the test to be valid.
 * </li>
 * </ul>
 * The critical discriminator is that we are making assumptions about the module under test. If we refactored the
 * app layout, there may no longer be a login link on the home page and thus we either have removed the
 * {@code homePageHasLoginLink} test or refactored it to verify the new behaviour
 * ({@code clickingSignupAlsoDisplaysLoginLink}). If we use an {@code Assume.assumeThat(homePage,has(loginLink))} in
 * the {@code usernamePasswordLoginWorks} our test would be skipped and there is no alert. If we use an
 * {@code Assert.assertThat(homePage,has(loginLink))} then the test would be failed, so there is an alert
 * <strong>but</strong> we have made diagnosing regressions in the login link harder as there are now a bunch of
 * tests failing such as {@code registeredUsersCanSeeSavedCarts()} and it is hard to determine where to start looking.
 * What we actually want to do in this case is have the test record as an ERROR. This both lets us know that something
 * is wrong while letting us focus first on fixing the failures.
 */
public class Require {
    /**
     * Analogue of {@code Assert.fail()} for where a requirement is not met and the test should abort immediately.
     */
    public static void error() {
        throw new RequirementViolatedException(null);
    }

    /**
     * Analogue of {@code Assert.fail(String)} for where a requirement is not met and the test should abort immediately.
     *
     * @param message the description of the error.
     */
    public static void error(String message) {
        throw new RequirementViolatedException(message);
    }

    /**
     * Analogue of {@code Assert.assertThat(T,Matcher<T>)} and {@code Assume.assumeThat(T,Matcher<T>)}.
     *
     * @param observed    the observed value.
     * @param expectation the expectation.
     * @param <T>         type of matcher.
     */
    public static <T> void requireThat(T observed, Matcher<T> expectation) {
        if (!expectation.matches(observed)) {
            throw new RequirementViolatedException(observed, expectation);
        }
    }

    /**
     * Analogue of {@code Assert.assertThat(String,T,Matcher<T>)} and {@code Assume.assumeThat(String,T,Matcher<T>)}.
     *
     * @param description the description.
     * @param observed    the observed value.
     * @param expectation the expectation.
     * @param <T>         type of matcher.
     */
    public static <T> void requireThat(String description, T observed, Matcher<T> expectation) {
        if (!expectation.matches(observed)) {
            throw new RequirementViolatedException(description, observed, expectation);
        }
    }

    /**
     * Analogue of {@code Assert.assertTrue(boolean)} and {@code Assume.assumeTrue(boolean)}.
     *
     * @param observed the observed value.
     */
    public static void requireTrue(boolean observed) {
        requireThat(observed, is(true));
    }

    /**
     * Analogue of {@code Assert.assertTrue(String,boolean)} and {@code Assume.assumeTrue(String,boolean)}.
     *
     * @param description the description.
     * @param observed    the observed value.
     */
    public static void requireTrue(String description, boolean observed) {
        requireThat(description, observed, is(true));
    }

    /**
     * Analogue of {@code Assert.assertFalse(boolean)} and {@code Assume.assumeFalse(boolean)}.
     *
     * @param observed the observed value.
     */
    public static void requireFalse(boolean observed) {
        requireThat(observed, is(false));
    }

    /**
     * Analogue of {@code Assert.assertFalse(String,boolean)} and {@code Assume.assumeFalse(String,boolean)}.
     *
     * @param description the description.
     * @param observed    the observed value.
     */
    public static void requireFalse(String description, boolean observed) {
        requireThat(description, observed, is(false));
    }
}

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

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.SelfDescribing;
import org.hamcrest.StringDescription;

/**
 * An exception class used to implement <i>requirements</i> (state in which a given test  is valid/invalid and should
 * or should not be executed). A test for which a requirement fails should generate a test case error.
 */
public class RequirementViolatedException extends RuntimeException implements SelfDescribing {
    /**
     * Standardize serialization.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Flag to indicate that we have an {@link #observed} (because the observed value may be {@code null}.
     */
    private final boolean haveObserved;
    /**
     * The observed value.
     */
    private final Object observed;
    /**
     * The expectation.
     */
    private final Matcher<?> expectation;

    /**
     * A requirement exception with an observed value and a {@link Matcher} describing the expectation that failed.
     *
     * @param observed    the observed value.
     * @param expectation the expectation.
     */
    public <T> RequirementViolatedException(T observed, Matcher<T> expectation) {
        this(null, observed, expectation);
    }

    /**
     * A requirement exception with a message, observed value and a {@link Matcher} describing the expectation that
     * failed.
     *
     * @param description the description of the requirement.
     * @param observed    the observed value.
     * @param expectation the expectation.
     */
    public <T> RequirementViolatedException(String description, T observed, Matcher<T> expectation) {
        super(description, observed instanceof Throwable ? (Throwable) observed : null);
        haveObserved = true;
        this.observed = observed;
        this.expectation = expectation;
    }

    /**
     * A described requirement exception without either observed value or expected matcher.
     *
     * @param description the description.
     */
    public RequirementViolatedException(String description) {
        super(description);
        haveObserved = false;
        this.observed = null;
        this.expectation = null;
    }

    /**
     * A requirement exception with description and cause.
     *
     * @param description the description.
     * @param cause       the cause
     */
    public RequirementViolatedException(String description, Throwable cause) {
        this(description, cause, null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getMessage() {
        return StringDescription.asString(this);
    }

    /**
     * {@inheritDoc}
     */
    public void describeTo(Description description) {
        String message = super.getMessage();
        if (haveObserved) {
            if (message != null) {
                description.appendText(message);
            }
            if (expectation != null) {
                description.appendText("\nExpected: ").appendDescriptionOf(expectation);
                description.appendText("\n     but: ");
                expectation.describeMismatch(observed, description);
            } else {
                description.appendText("Observed: ").appendValue(observed);
            }
        } else if (message != null) {
            description.appendText(message);
        }
    }
}

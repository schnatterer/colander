/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2017 Johannes Schnatterer
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.junit;

import java.util.Optional;

/**
 * Some convenience junit asserts when using java 8 lambda expressions.
 */
@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public class AssertLambda extends Assert{
    /**
     * Protect constructor since it is a static only class
     */
    protected AssertLambda() {
    }

    /**
     * Asserts that an {@link Optional} is empty. If it is not, an {@link AssertionError}
     * is thrown with the given message.
     *
     * @param message the identifying message for the {@link AssertionError} (<code>null</code>
     * okay)
     * @param optional Object to check or <code>null</code>
     */
    static public void assertEmpty(String message, Optional<?> optional) {
        assertNotNull(message, optional);
        if (optional.isPresent()) {
            failNotEmpty(message, optional);
        }
    }

    static private void failNotEmpty(String message, Optional<?> actual) {
        String formatted = "";
        if (message != null) {
            formatted = message + " ";
        }
        fail(formatted + "expected empty, but was:<" + actual + ">");
    }

    /**
     * Asserts the value of an {@link Optional}. Fails the test if empty.
     *
     * @param message         the identifying message for the {@link AssertionError} (<code>null</code> okay)
     * @param expected        the expected object
     * @param actual          the object to compare to <code>expected</code>
     * @param assertIfPresent assert method that is called if actual isPresent()
     * @param <T>             type of the parameters to compare.
     */
    public static <T> void assertOptional(String message, T expected, Optional<T> actual,
                                          AssertMethod<T> assertIfPresent) {
        if (actual.isPresent()) {
            assertIfPresent.accept(message, expected, actual.get());
        } else {
            fail(Assert.format(message, expected, Optional.empty()));
        }
    }

    /**
     * A method call to a typical {@link Assert} method, like {@link Assert#assertEquals(String, Object, Object)}.
     *
     * @param <U> type of the parameters to compare.
     */
    @FunctionalInterface
    public interface AssertMethod<U> {
        /**
         * Performs this operation on the given arguments.
         *
         * @param message  the identifying message for the {@link AssertionError} (<code>null</code> okay)
         * @param expected the expected object
         * @param actual   the object to compare to <code>expected</code>
         */
        void accept(String message, U expected, U actual);
    }
}

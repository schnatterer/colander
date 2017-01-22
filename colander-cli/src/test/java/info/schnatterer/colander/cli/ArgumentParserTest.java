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
package info.schnatterer.colander.cli;

import info.schnatterer.colander.cli.ArgumentParser.ArgumentException;
import org.hamcrest.junit.ExpectedException;
import org.junit.Rule;
import org.junit.Test;
import uk.org.lidalia.slf4jtest.LoggingEvent;
import uk.org.lidalia.slf4jtest.TestLogger;
import uk.org.lidalia.slf4jtest.TestLoggerFactory;
import uk.org.lidalia.slf4jtest.TestLoggerFactoryResetRule;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.greaterThan;
import static org.junit.Assert.*;

public class ArgumentParserTest {
    private static final String PROGRAM_NAME = "progr";

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    /** Logger of class under test. */
    private static final TestLogger LOG = TestLoggerFactory.getTestLogger(ArgumentParser.class);

    /** Rest logger before each test. **/
    @Rule
    public TestLoggerFactoryResetRule testLoggerFactoryResetRule = new TestLoggerFactoryResetRule();

    @Test
    public void read() throws Exception {
        Arguments args = read("input", "output");

        assertEquals("Input file", "input", args.getInputFile());
        assertEquals("Output file", "output", args.getOutputFile());
    }

    @Test
    public void readInputOnly() throws Exception {
        Arguments args = read("input");

        assertEquals("Input file", "input", args.getInputFile());
        assertNull("Output file", args.getOutputFile());
    }

    @Test
    public void readNoMainArgs() throws Exception {
        expectedException.expect(ArgumentException.class);
        expectedException.expectMessage("Main parameters");
        read("");
    }

    @Test
    public void readHelp() throws Exception {
        assertTrue("Unexpected return on read()", read("input", "output", "--help").isHelp());
        assertThat("Unexpected log message", getLogEvent(0).getMessage(), containsString("Usage"));
        assertThat("Unexpected log message", getLogEvent(0).getMessage(), containsString(PROGRAM_NAME));
    }

    private Arguments read(String ... argv) {
        return ArgumentParser.read(argv, PROGRAM_NAME);
    }

    /**
     * @return the logging event at <code>index</code>. Fails if not enough logging events present.
     */
    private LoggingEvent getLogEvent(int index) {
        assertThat("Unexpected number of Log messages", LOG.getLoggingEvents().size(), greaterThan(index));
        return LOG.getLoggingEvents().get(index);
    }
}

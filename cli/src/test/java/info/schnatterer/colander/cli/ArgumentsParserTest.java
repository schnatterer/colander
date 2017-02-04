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

import info.schnatterer.colander.cli.ArgumentsParser.ArgumentException;
import org.hamcrest.junit.ExpectedException;
import org.junit.Rule;
import org.junit.Test;
import uk.org.lidalia.slf4jtest.LoggingEvent;
import uk.org.lidalia.slf4jtest.TestLogger;
import uk.org.lidalia.slf4jtest.TestLoggerFactory;
import uk.org.lidalia.slf4jtest.TestLoggerFactoryResetRule;

import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

public class ArgumentsParserTest {
    private static final String PROGRAM_NAME = "progr";

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    /** Logger of class under test. */
    private static final TestLogger LOG = TestLoggerFactory.getTestLogger(ArgumentsParser.class);

    /** Rest logger before each test. **/
    @Rule
    public TestLoggerFactoryResetRule testLoggerFactoryResetRule = new TestLoggerFactoryResetRule();

    @Test
    public void read() throws Exception {
        Arguments args = read("input", "output");

        assertEquals("Input file", "input", args.getInputFile());
        assertEquals("Output file", "output", args.getOutputFile());

        assertFalse("Help", args.isHelp());
        assertFalse("Remove duplicates", args.isRemoveDuplicates());
        assertFalse("Remove Empty", args.isRemoveEmpty());
        assertTrue("Replace in summary", args.getReplaceInSummary().isEmpty());
        assertTrue("Remove summary contains", args.getRemoveSummaryContains().isEmpty());
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
    public void readReplaceInSummary() throws Exception {
        Map<String, String> replaceInSummary =
            read("--replace-summary a=b", "--replace-summary", "\"\\r(?!\\n)=\\r\\n\"", "input", "output")
                .getReplaceInSummary();
        assertThat(replaceInSummary, hasEntry("a", "b"));
        assertThat(replaceInSummary, hasEntry("\\r(?!\\n)", "\\r\\n"));
        assertEquals("Unexpected amount of replace arguments", 2, replaceInSummary.size());
    }

    @Test
    public void readReplaceInDescription() throws Exception {
        Map<String, String> replaceInDescription =
            read("--replace-description a=b", "--replace-description", "\"\\r(?!\\n)=\\r\\n\"", "input", "output")
                .getReplaceInDescription();
        assertThat(replaceInDescription, hasEntry("a", "b"));
        assertThat(replaceInDescription, hasEntry("\\r(?!\\n)", "\\r\\n"));
        assertEquals("Unexpected amount of replace arguments", 2, replaceInDescription.size());
    }

    @Test
    public void readRemoveSummaryContains() throws Exception {
        List<String> removeSummaryContainsMultiple =
            read("--remove-summary", "a", "--remove-summary", "\"b c\"", "input", "output").getRemoveSummaryContains();
        List<String> removeSummaryContainsCommaSyntax =
            read("--remove-summary", "\"a,b c\"", "input", "output").getRemoveSummaryContains();
        assertThat(removeSummaryContainsMultiple, contains("a", "b c"));
        assertEquals("Unexpected amount of replace arguments", 2, removeSummaryContainsMultiple.size());
        assertEquals("Multiple parameter syntax and comma syntax are not the same", removeSummaryContainsCommaSyntax, removeSummaryContainsMultiple);
    }

    @Test
    public void readRemoveDuplicates() {
        Arguments read = read("--remove-duplicate-events", "input", "output");
        assertTrue("Remove duplicates", read.isRemoveDuplicates());
    }

    @Test
    public void readRemoveEmpty() {
        assertTrue("Remove empty", read("--remove-empty-events", "input", "output").isRemoveEmpty());
    }

    @Test
    public void readHelp() throws Exception {
        assertTrue("Unexpected return on read()", read("input", "output", "--help").isHelp());
        assertThat("Unexpected log message", getLogEvent(0).getMessage(), containsString("Usage"));
        assertThat("Unexpected log message", getLogEvent(0).getMessage(), containsString(PROGRAM_NAME));
    }

    private Arguments read(String... argv) {
        return ArgumentsParser.read(argv, PROGRAM_NAME);
    }

    /**
     * @return the logging event at <code>index</code>. Fails if not enough logging events present.
     */
    private LoggingEvent getLogEvent(int index) {
        assertThat("Unexpected number of Log messages", LOG.getLoggingEvents().size(), greaterThan(index));
        return LOG.getLoggingEvents().get(index);
    }
}

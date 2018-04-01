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

import com.thekua.spikes.LogbackCapturingAppender;
import info.schnatterer.colander.cli.ArgumentsParser.ArgumentException;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ArgumentsParserTest {
    private static final String PROGRAM_NAME = "progr";

    /** Logger of class under test. */
    private final LogbackCapturingAppender log = LogbackCapturingAppender.weaveInto(ArgumentsParser.LOG);

    @Test
    void read() throws Exception {
        Arguments args = read("input", "output");

        assertEquals("Input file", "input", args.getInputFile());
        assertEquals("Output file", "output", args.getOutputFile());

        assertFalse("Help", args.isHelp());
        assertFalse("Remove duplicates", args.isRemoveDuplicateEvents());
        assertFalse("Remove Empty", args.isRemoveEmptyEvents());
        assertTrue("Replace in summary", args.getReplaceInSummary().isEmpty());
        assertTrue("Remove summary contains", args.getRemoveSummaryContains().isEmpty());
    }

    @Test
    void readInputOnly() throws Exception {
        Arguments args = read("input");
        assertEquals("Input file", "input", args.getInputFile());
        assertNull("Output file", args.getOutputFile());
    }

    @Test
    void readNoMainArgs() throws Exception {
        ArgumentException actualException = assertThrows(ArgumentException.class,() -> read(""));

        assertThat(actualException.getMessage(), containsString("Main parameters"));
    }

    @Test
    void readReplaceInSummary() throws Exception {
        Map<String, String> replaceInSummary =
            read("--replace-summary a=b", "--replace-summary", "\"\\r(?!\\n)=\\r\\n\"", "input", "output")
                .getReplaceInSummary();
        assertThat(replaceInSummary, hasEntry("a", "b"));
        assertThat(replaceInSummary, hasEntry("\\r(?!\\n)", "\\r\\n"));
        assertEquals("Unexpected amount of replace arguments", 2, replaceInSummary.size());
    }

    @Test
    void readReplaceInDescription() throws Exception {
        Map<String, String> replaceInDescription =
            read("--replace-description a=b", "--replace-description", "\"\\r(?!\\n)=\\r\\n\"", "input", "output")
                .getReplaceInDescription();
        assertThat(replaceInDescription, hasEntry("a", "b"));
        assertThat(replaceInDescription, hasEntry("\\r(?!\\n)", "\\r\\n"));
        assertEquals("Unexpected amount of replace arguments", 2, replaceInDescription.size());
    }

    @Test
    void readRemoveSummaryContains() throws Exception {
        List<String> removeSummaryContainsMultiple =
            read("--remove-summary", "a", "--remove-summary", "\"b c\"", "input", "output").getRemoveSummaryContains();
        List<String> removeSummaryContainsCommaSyntax =
            read("--remove-summary", "\"a,b c\"", "input", "output").getRemoveSummaryContains();
        assertThat(removeSummaryContainsMultiple, contains("a", "b c"));
        assertEquals("Unexpected amount of replace arguments", 2, removeSummaryContainsMultiple.size());
        assertEquals("Multiple parameter syntax and comma syntax are not the same", removeSummaryContainsCommaSyntax, removeSummaryContainsMultiple);
    }

    @Test
    void readRemoveDescriptionContains() throws Exception {
        List<String> removeDescriptionContainsMultiple =
            read("--remove-description", "a", "--remove-description", "\"b c\"", "input", "output").getRemoveDescriptionContains();
        List<String> removeDescriptionContainsCommaSyntax =
            read("--remove-description", "\"a,b c\"", "input", "output").getRemoveDescriptionContains();
        assertThat(removeDescriptionContainsMultiple, contains("a", "b c"));
        assertEquals("Unexpected amount of replace arguments", 2, removeDescriptionContainsMultiple.size());
        assertEquals("Multiple parameter syntax and comma syntax are not the same", removeDescriptionContainsCommaSyntax, removeDescriptionContainsMultiple);
    }

    @Test
    void readRemoveDuplicates() {
        Arguments read = read("--remove-duplicate-events", "input", "output");
        assertTrue("Remove duplicates", read.isRemoveDuplicateEvents());
    }

    @Test
    void readRemoveEmpty() {
        assertTrue("Remove empty", read("--remove-empty-events", "input", "output").isRemoveEmptyEvents());
    }

    @Test
    void readHelp() throws Exception {
        assertTrue("Unexpected return on read()", read("input", "output", "--help").isHelp());
        assertThat("Unexpected log message", getLogEvent(0), containsString("Usage"));
        assertThat("Unexpected log message", getLogEvent(0), containsString(PROGRAM_NAME));
    }

    private Arguments read(String... argv) {
        return ArgumentsParser.read(argv, PROGRAM_NAME);
    }

    /**
     * @return the logging event at <code>index</code>. Fails if not enough logging events present.
     */
    private String getLogEvent(int index) {
        assertThat("Unexpected number of Log messages", log.getCapturedLogMessages().size(), greaterThan(index));
        return log.getCapturedLogMessages().get(index);
    }
}

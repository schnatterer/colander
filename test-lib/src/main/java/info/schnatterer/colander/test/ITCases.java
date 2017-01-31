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
package info.schnatterer.colander.test;

import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.component.VEvent;
import org.junit.rules.TemporaryFolder;

import java.io.*;
import java.nio.file.Files;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;

/**
 * Constants used for testing in all modules.
 */
public class ITCases {
    private static final String ICS_FILE = "ColanderIT.ics";

    private ITCases() {}

    /**
     * @return the absolute file path to test ICS file
     * @param folder
     */
    public static String getFilePathTestIcs(TemporaryFolder folder) throws IOException {
        return getFilePathTestIcs(ICS_FILE, folder);
    }

    /**
     * Verifies that a parsed ICS is as expected.
     */
    @SuppressWarnings("squid:S1160") // This is a test-lib. Don't try to win a trophy for its design.
    public static void verifyParsedIcs(String outputPath) throws IOException, ParserException {
        Calendar cal = new CalendarBuilder().build(new FileInputStream(outputPath));
        List<VEvent> events = cal.getComponents("VEVENT").stream()
            .map(component -> (VEvent) component)
            .collect(Collectors.toList());
        assertEquals("Number of components", 2, events.size());
        findEventBySummary(events, "Duplicate");
        VEvent replacedEvent = findEventBySummary(events, "Replace");
        assertEquals("Replaced event description", "FirstLine\nSecondLine\nThirdLine\n",
            replacedEvent.getDescription().getValue());
        assertEquals("Replaced event summary", "Replace!",
            replacedEvent.getSummary().getValue());
    }

    /**
     * Visible for testing
     */
    static String getFilePathTestIcs(String path, TemporaryFolder folder) throws IOException {
        InputStream testIcsFileStream = ITCases.class.getClassLoader().getResourceAsStream(path);
        if (testIcsFileStream == null) {
            throw new AssertionError("Test ICS file not found");
        }

        // Write ics file to temporary folder, to also work when this module is a jar dependecy
        File newIcsFile = folder.newFile();
        Files.write(newIcsFile.toPath(), read(testIcsFileStream).getBytes("UTF-8"));
        return newIcsFile.getAbsolutePath();
    }

    private static VEvent findEventBySummary(List<VEvent> events, String summaryContains) {
        List<VEvent> filteredEvents = events.stream().filter(even -> even.getSummary().getValue().contains(summaryContains))
            .collect(Collectors.toList());
        assertEquals("Expected number of events with summary: " + summaryContains, 1,  filteredEvents.size());
        return filteredEvents.get(0);
    }

    private static String read(InputStream input) throws IOException {
        try (BufferedReader buffer = new BufferedReader(new InputStreamReader(input))) {
            return buffer.lines().collect(Collectors.joining("\n"));
        }
    }
}

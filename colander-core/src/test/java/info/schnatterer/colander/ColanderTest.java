/**
 * The MIT License (MIT)
 * <p>
 * Copyright (c) 2017 Johannes Schnatterer
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package info.schnatterer.colander;

import info.schnatterer.colander.Colander.ColanderBuilder;
import net.fortuna.ical4j.model.Calendar;
import org.junit.Test;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ColanderTest {
    private String expectedFilePath = "file";
    private FilterChain filterChain = mock(FilterChain.class);
    private Calendar cal = mock(Calendar.class);
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern(Colander.ColanderResult.DATE_TIME_FORMAT_FILE_NAME);

    @Test
    public void toss() throws Exception {
        assertEquals(expectedFilePath, Colander.toss(expectedFilePath).filePath);
    }

    @Test
    public void removeDuplicates() throws Exception {
        ColanderBuilder colanderBuilder = Colander.toss(expectedFilePath).removeDuplicates();
        assertThat(colanderBuilder.filters, hasItem(isA(DuplicateFilter.class)));
    }

    @Test
    public void removeEmptyEvents() throws Exception {
        ColanderBuilder colanderBuilder = Colander.toss(expectedFilePath).removeEmptyEvents();
        assertThat(colanderBuilder.filters, hasItem(isA(EmptyEventRemovalFilter.class)));
    }

    @Test
    public void replaceInSummary() throws Exception {
        ColanderBuilder colanderBuilder = Colander.toss(expectedFilePath).replaceInSummary("a", "b");
        List<ReplaceSummaryFilter> replaceFilters = getFiltersByClass(colanderBuilder, ReplaceSummaryFilter.class);
        assertEquals("Unexpected amount of filters found", 1, replaceFilters.size());
        replaceFilters.forEach(
            filter -> {
                assertEquals("Unexpected regex", "a", filter.getRegex());
                assertEquals("Unexpected stringToReplaceInSummary", "b", filter.getStringToReplaceInSummary());
            }
        );
    }

    @Test
    public void removeSummaryContains() throws Exception {
        ColanderBuilder colanderBuilder = Colander.toss(expectedFilePath).removeSummaryContains("str");
        List<SummaryEventRemoverFilter> removeSummaryFilters = getFiltersByClass(colanderBuilder, SummaryEventRemoverFilter.class);
        assertEquals("Unexpected amount of filters found", 1, removeSummaryFilters.size());
        removeSummaryFilters.forEach(
            filter -> assertEquals("Unexpected summaryContainsString", "str", filter.getSummaryContainsString())
        );
    }

    @Test
    public void maintainsFilterOrder() {
        ColanderBuilder colanderBuilder = Colander.toss(expectedFilePath)
            .removeSummaryContains("str")
            .replaceInSummary("a", "b")
            .removeEmptyEvents()
            .removeDuplicates();
        Iterator<VEventFilter> filters = colanderBuilder.filters.iterator();
        assertTrue("Unexpected order", filters.next() instanceof SummaryEventRemoverFilter);
        assertTrue("Unexpected order", filters.next() instanceof ReplaceSummaryFilter);
        assertTrue("Unexpected order", filters.next() instanceof EmptyEventRemovalFilter);
        assertTrue("Unexpected order", filters.next() instanceof DuplicateFilter);
    }


    @Test
    public void rinseToCalendar() throws Exception {
        ColanderBuilder builder = new ColanderBuilderForTest(expectedFilePath);

        when(filterChain.run(any(Calendar.class))).thenReturn(cal);

        assertSame(cal, builder.rinse().toCalendar());
    }

    @Test
    public void rinseToFile() throws Exception {
        ColanderResultForTest colanderResult = new ColanderResultForTest("dontcare", cal);
        String expectedPath = "expectedPath";

        colanderResult.toFile(expectedPath);

        assertSame(expectedPath, colanderResult.writtenPath);
        assertSame(cal, colanderResult.writtenCal);
    }

    @Test
    public void rinseToFilePathNull() throws Exception {
        ColanderResultForTest colanderResult = new ColanderResultForTest("a/b.someEnding", cal);

        colanderResult.toFile(null);
        LocalDateTime dateBefore = createComparableDateNow(LocalDateTime.now().format(formatter), formatter);
        assertThat(colanderResult.writtenPath, startsWith("a/b"));
        assertThat(colanderResult.writtenPath, endsWith(".someEnding"));

        verifyDateInNewFileName(colanderResult.writtenPath, dateBefore, "\\.someEnding");
    }

    @Test
    public void rinseToFilePathNullInputPathNoFileExtension() throws Exception {
        ColanderResultForTest colanderResult = new ColanderResultForTest("a/b", cal);

        colanderResult.toFile(null);
        LocalDateTime dateBefore = createComparableDateNow(LocalDateTime.now().format(formatter), formatter);
        assertThat(colanderResult.writtenPath, startsWith("a/b"));

        verifyDateInNewFileName(colanderResult.writtenPath, dateBefore, "");
    }

    private <T extends VEventFilter> List<T> getFiltersByClass(ColanderBuilder colanderBuilder, Class<T> clazz) {
        return colanderBuilder.filters.stream()
            .filter(clazz::isInstance)
            .map(clazz::cast)
            .collect(Collectors.toList());
    }


    private LocalDateTime createComparableDateNow(String format, DateTimeFormatter formatter) {
        return LocalDateTime.parse(format, formatter);
    }

    private void verifyDateInNewFileName(String writtenPath, LocalDateTime dateBefore, String extension) {
        Pattern pattern = Pattern.compile("a/b-(.*)" + extension);
        Matcher matcher = pattern.matcher(writtenPath);
        assertTrue("Date not found in new file name", matcher.find());
        LocalDateTime newFileNameDate =
            LocalDateTime.parse(matcher.group(1), formatter);
        assertTrue("Date in new file name is unexpected. Expected equal or later than " + dateBefore + ", but was " + newFileNameDate,
            newFileNameDate.isAfter(dateBefore) || newFileNameDate.isEqual(dateBefore));
    }

    private class ColanderBuilderForTest extends ColanderBuilder {
        ColanderBuilderForTest(String filePath) {
            super(filePath);
        }

        @Override
        Calendar read(String filePath) throws IOException {
            return cal;
        }

        @Override
        FilterChain createFilterChain() {
            return filterChain;
        }
    }

    private class ColanderResultForTest extends Colander.ColanderResult {
        Calendar writtenCal;
        String writtenPath;

        ColanderResultForTest(String filePath, Calendar result) {
            super(filePath, result);
        }

        @Override
        void write(Calendar result, String path) throws IOException {
            writtenCal = result;
            writtenPath = path;
        }
    }

}

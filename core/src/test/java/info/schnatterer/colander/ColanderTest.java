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
package info.schnatterer.colander;

import info.schnatterer.colander.Colander.ColanderBuilder;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.component.VEvent;
import org.junit.Test;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.isA;
import static org.junit.AssertLambda.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ColanderTest {
    private String expectedFilePath = "file";
    private FilterChain filterChain = mock(FilterChain.class);
    private Calendar cal = mock(Calendar.class);

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
    public void filter() throws Exception {
        ColanderBuilder colanderBuilder = Colander.toss(expectedFilePath).filter(event -> Optional.empty());
        List<VEventFilter> allFilters = getFiltersByClass(colanderBuilder, VEventFilter.class);
        assertEquals("Unexpected amount of filters found", 1, allFilters.size());
        allFilters.forEach(
            filter -> {
                VEvent event = mock(VEvent.class);
                assertEmpty("Filter returned unexpected value.", filter.apply(event));
            }
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

    private <T extends VEventFilter> List<T> getFiltersByClass(ColanderBuilder colanderBuilder, Class<T> clazz) {
        return colanderBuilder.filters.stream()
            .filter(clazz::isInstance)
            .map(clazz::cast)
            .collect(Collectors.toList());
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
        void write(Calendar result, String path, String inputPath) throws IOException {
            writtenCal = result;
            writtenPath = path;
        }
    }

}

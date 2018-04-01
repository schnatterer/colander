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
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.component.VEvent;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.*;

class ColanderTest {
    private String expectedFilePath = "file";
    private FilterChain filterChain = mock(FilterChain.class);
    private Calendar cal = mock(Calendar.class);

    @Test
    void toss() throws Exception {
        assertEquals(expectedFilePath, Colander.toss(expectedFilePath).filePath);
    }

    @Test
    void removeDuplicates() throws Exception {
        ColanderBuilder colanderBuilder = Colander.toss(expectedFilePath).removeDuplicateEvents();
        assertThat(colanderBuilder.filters).first().isOfAnyClassIn(RemoveDuplicateEventFilter.class);
    }

    @Test
    void removeEmptyEvents() throws Exception {
        ColanderBuilder colanderBuilder = Colander.toss(expectedFilePath).removeEmptyEvents();
        assertThat(colanderBuilder.filters).first().isOfAnyClassIn(RemoveEmptyEventFilter.class);
    }

    @Test
    void replaceInProperty() throws Exception {
        String expectedProperty = "some property name";
        String expectedRegex = "a";
        String expectedReplacement = "b";
        replaceInProperty(expectedProperty, expectedRegex, expectedReplacement,
            colanderBuilder ->colanderBuilder.replaceInProperty(expectedProperty, expectedRegex, expectedReplacement));
    }

    @Test
    void replaceInSummary() throws Exception {
        String expectedProperty =  Property.SUMMARY;
        String expectedRegex = "a";
        String expectedReplacement = "b";
        replaceInProperty(expectedProperty, expectedRegex, expectedReplacement,
            colanderBuilder ->colanderBuilder.replaceInSummary(expectedRegex, expectedReplacement));
    }

    @Test
    void replaceInDescription() throws Exception {
        String expectedProperty =  Property.DESCRIPTION;
        String expectedRegex = "a";
        String expectedReplacement = "b";
        replaceInProperty(expectedProperty, expectedRegex, expectedReplacement,
            colanderBuilder ->colanderBuilder.replaceInDescription(expectedRegex, expectedReplacement));
    }

    @Test
    void removePropertyContains() throws Exception {
        String expectedProperty = "some property name";
        String expectedString = "str";
        removePropertyContains(expectedProperty, expectedString,
            colanderBuilder -> colanderBuilder.removePropertyContains(expectedProperty, expectedString));
    }

    @Test
    void removeSummaryContains() throws Exception {
        String expectedProperty = Property.SUMMARY;
        String expectedString = "str";
        removePropertyContains(expectedProperty, expectedString,
            colanderBuilder -> colanderBuilder.removeSummaryContains(expectedString));
    }

    @Test
    void removeDescriptionContains() throws Exception {
        String expectedProperty = Property.DESCRIPTION;
        String expectedString = "str";
        removePropertyContains(expectedProperty, expectedString,
            colanderBuilder -> colanderBuilder.removeDescriptionContains(expectedString));
    }

    @Test
    void filter() throws Exception {
        ColanderBuilder colanderBuilder = Colander.toss(expectedFilePath).filter(event -> Optional.empty());
        List<ColanderFilter> allFilters = getFiltersByClass(colanderBuilder, ColanderFilter.class);
        assertEquals(1, allFilters.size(), "Unexpected amount of filters found");
        allFilters.forEach(
            filter -> {
                VEvent event = mock(VEvent.class);
                assertThat(filter.apply(event)).isEmpty();
            }
        );
    }

    @Test
    void maintainsFilterOrder() {
        ColanderBuilder colanderBuilder = Colander.toss(expectedFilePath)
            .removeSummaryContains("str")
            .replaceInSummary("a", "b")
            .removeEmptyEvents()
            .removeDuplicateEvents();
        Iterator<ColanderFilter> filters = colanderBuilder.filters.iterator();
        assertTrue(filters.next() instanceof RemoveFilter, "Unexpected order");
        assertTrue(filters.next() instanceof ReplaceFilter, "Unexpected order");
        assertTrue(filters.next() instanceof RemoveEmptyEventFilter, "Unexpected order");
        assertTrue(filters.next() instanceof RemoveDuplicateEventFilter, "Unexpected order");
    }


    @Test
    void rinseToCalendar() throws Exception {
        ColanderBuilder builder = new ColanderBuilderForTest(expectedFilePath);

        when(filterChain.run(any(Calendar.class))).thenReturn(cal);

        assertSame(cal, builder.rinse().toCalendar());
    }

    @Test
    void rinseToFile() throws Exception {
        ColanderResultForTest colanderResult = new ColanderResultForTest("dontcare", cal);
        String expectedPath = "expectedPath";

        colanderResult.toFile(expectedPath);

        assertSame(expectedPath, colanderResult.writtenPath);
        assertSame(cal, colanderResult.writtenCal);
    }

    private <T extends ColanderFilter> List<T> getFiltersByClass(ColanderBuilder colanderBuilder, Class<T> clazz) {
        return colanderBuilder.filters.stream()
            .filter(clazz::isInstance)
            .map(clazz::cast)
            .collect(Collectors.toList());
    }

    private void replaceInProperty(String expectedProperty, String expectedRegex, String expectedReplacement, Consumer<ColanderBuilder> consumer) {
        ColanderBuilder colanderBuilder = Colander.toss(expectedFilePath);
        consumer.accept(colanderBuilder);
        List<ReplaceFilter> replaceFilters = getFiltersByClass(colanderBuilder, ReplaceFilter.class);
        assertEquals(1, replaceFilters.size(), "Unexpected amount of filters found");
        replaceFilters.forEach(
            filter -> {
                assertEquals(expectedProperty, filter.getPropertyName(), "Unexpected property");
                assertEquals(expectedRegex, filter.getRegex(), "Unexpected regex");
                assertEquals(expectedReplacement, filter.getStringToReplace(), "Unexpected stringToReplaceInSummary");
            }
        );
    }

    private void removePropertyContains(String expectedProperty,String expectedString, Consumer<ColanderBuilder> consumer) {
        ColanderBuilder colanderBuilder = Colander.toss(expectedFilePath);
        consumer.accept(colanderBuilder);
        List<RemoveFilter> removeFilters = getFiltersByClass(colanderBuilder, RemoveFilter.class);
        assertEquals(1, removeFilters.size(), "Unexpected amount of filters found");
        removeFilters.forEach(
            filter -> {
                assertEquals(expectedProperty, filter.getPropertyName(), "Unexpected property");
                assertEquals(expectedString, filter.getPropertyContainsString(), "Unexpected summaryContainsString");
            }
        );
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

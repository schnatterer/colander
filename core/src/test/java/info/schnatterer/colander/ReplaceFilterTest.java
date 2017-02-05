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

import net.fortuna.ical4j.model.Date;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.Description;
import net.fortuna.ical4j.model.property.DtStart;
import org.hamcrest.junit.ExpectedException;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.text.ParseException;

import static org.junit.AssertLambda.assertOptional;
import static org.mockito.Mockito.*;

public class ReplaceFilterTest {
    private Date expectedDate = new Date();

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void filterChangesOnMatch() throws Exception {
        ReplaceFilter filter = new ReplaceFilter("h.*llo", "hullo", Property.SUMMARY);
        VEvent event = new VEvent(expectedDate, "hallo icaltools");
        VEvent expectedEvent = new VEvent(expectedDate, "hullo icaltools");
        assertOptional("Unexpected filtering result", expectedEvent, filter.apply(event), Assert::assertEquals);
    }

    @Test
    public void filterIgnoresWhenNoMatch() throws Exception {
        ReplaceFilter filter = new ReplaceFilter("hallo", "hullo", Property.SUMMARY);
        VEvent event = new VEvent(new Date(), "hullo icaltools");
        assertOptional("Unexpected filtering result", event, filter.apply(event), Assert::assertSame);
    }

    @Test
    public void filterDescription() throws Exception {
        ReplaceFilter filter = new ReplaceFilter("h.*llo", "hullo", Property.DESCRIPTION);
        VEvent event = createVEvent(expectedDate, "hallo icaltools");
        VEvent expectedEvent = createVEvent(expectedDate, "hullo icaltools");
        assertOptional("Unexpected filtering result", expectedEvent, filter.apply(event), Assert::assertEquals);
    }

    @Test
    public void filterPropertyDoesNotExist() throws Exception {
        ReplaceFilter filter = new ReplaceFilter("hallo", "hullo", Property.DESCRIPTION);
        VEvent event = new VEvent(expectedDate, "hullo icaltools");
        // Unfiltered
        assertOptional("Unexpected filtering result", event, filter.apply(event), Assert::assertEquals);
    }
    @Test
    public void filterPropertyDoesHaveValue() throws Exception {
        ReplaceFilter filter = new ReplaceFilter("hallo", "hullo", Property.SUMMARY);
        VEvent event = new VEvent(expectedDate, null);
        // Unfiltered
        assertOptional("Unexpected filtering result", event, filter.apply(event), Assert::assertEquals);
    }

    @Test
    public void filterIOException() throws Exception {
        testException(new IOException("mocked Message"));
    }

    @Test
    public void filterURISyntaxException() throws Exception {
        testException(new URISyntaxException("uri", "mocked Message"));
    }

    @Test
    public void filterParseException() throws Exception {
        testException(new ParseException("mocked Message", 42));
    }

    private VEvent createVEvent(Date startDate, String description) throws IOException, URISyntaxException, ParseException {
        VEvent event = new VEvent();
        event.getProperties().add(new DtStart(startDate));
        event.getProperties().add(new Description(description));
        return event;
    }

    private void testException(Exception exception) throws IOException, URISyntaxException, ParseException {
        String expectedMessage = exception.getMessage();
        ReplaceFilter filter = spy(new ReplaceFilter("", "", Property.SUMMARY));
        doThrow(exception).when(filter).replace(any());

        expectedException.expect(ColanderParserException.class);
        expectedException.expectMessage(expectedMessage);

        filter.apply(new VEvent(false));
    }
}

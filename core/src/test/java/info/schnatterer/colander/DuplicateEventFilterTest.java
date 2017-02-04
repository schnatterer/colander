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
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.DtEnd;
import net.fortuna.ical4j.model.property.Summary;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneId;

import static org.junit.AssertLambda.assertEmpty;
import static org.junit.AssertLambda.assertOptional;

public class DuplicateEventFilterTest {
    private DuplicateEventFilter filter = new DuplicateEventFilter();

    private LocalDateTime startDate = LocalDateTime.of(2012, Month.DECEMBER, 12, 13, 0);
    private LocalDateTime endDate = LocalDateTime.of(2012, Month.DECEMBER, 12, 23, 59);
    private LocalDateTime differentDate = LocalDateTime.of(2016, Month.JANUARY, 15, 12, 5);

    private VEvent event = createVEvent("Sum", startDate, endDate);

    @Before
    public void before() {
        assertOptional("First call on apply", event, filter.apply(event), Assert::assertSame);
    }

    @Test
    public void filterSameEvent() throws Exception {
        VEvent sameEvent = event;

        assertEmpty("Second call on apply", filter.apply(event));
        assertEmpty("Call on apply with same event instance", filter.apply(sameEvent));
    }

    @Test
    public void filterEqualEvent() throws Exception {
        VEvent equalEvent =
            new VEvent(event.getStartDate().getDate(), event.getEndDate().getDate(), event.getSummary().getValue());

        assertEmpty("Call on apply with other event instance, that equals", filter.apply(equalEvent));
    }

    @Test
    public void filterDifferentSummary() throws Exception {
        VEvent differentSummary = createVEvent("DifferentSummary", startDate, endDate);

        assertOptional("Call on apply with other event instance, different summary", differentSummary,
            filter.apply(differentSummary), Assert::assertEquals);
    }

    @Test
    public void filterDifferentStartDate() throws Exception {
        VEvent differentStartDate = createVEvent("Different Start Date", differentDate, endDate);

        assertOptional("Call on apply with other event instance, different start date", differentStartDate,
            filter.apply(differentStartDate), Assert::assertEquals);
    }

    @Test
    public void filterDifferentEndDate() throws Exception {
        VEvent differentEndDate = createVEvent("Different End Date", startDate, differentDate);

        assertOptional("Call on apply with other event instance, different end date", differentEndDate,
            filter.apply(differentEndDate), Assert::assertEquals);
    }

    @Test
    public void filterEndDateNull() throws Exception {
        VEvent endDateNull = new VEvent(toDate(startDate),"end date null");

        assertOptional("Call on apply with other event instance, end date null", endDateNull,
            filter.apply(endDateNull), Assert::assertEquals);
    }

    @Test
    public void filterStartDateNull() throws Exception {
        VEvent startDateNull = new VEvent();
        startDateNull.getProperties().add(new DtEnd(toDate(endDate)));
        startDateNull.getProperties().add(new Summary("start date null"));

        assertOptional("Call on apply with other event instance, start date null", startDateNull,
            filter.apply(startDateNull), Assert::assertEquals);
    }

    private VEvent createVEvent(String sum, LocalDateTime startDate, LocalDateTime endDate) {
        return new VEvent(toDate(startDate), toDate(endDate), sum);
    }

    private Date toDate(LocalDateTime of) {
        return new Date(java.util.Date.from(of.atZone(ZoneId.systemDefault()).toInstant()));
    }
}

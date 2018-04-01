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
import net.fortuna.ical4j.model.property.Description;
import net.fortuna.ical4j.model.property.DtEnd;
import net.fortuna.ical4j.model.property.Summary;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneId;

import static org.assertj.core.api.Assertions.assertThat;


public class RemoveDuplicateEventFilterTest {
    private RemoveDuplicateEventFilter filter = new RemoveDuplicateEventFilter();

    private LocalDateTime startDate = LocalDateTime.of(2012, Month.DECEMBER, 12, 13, 0);
    private LocalDateTime endDate = LocalDateTime.of(2012, Month.DECEMBER, 12, 23, 59);
    private LocalDateTime differentDate = LocalDateTime.of(2016, Month.JANUARY, 15, 12, 5);

    private VEvent event = createVEvent("Sum", "descr", startDate, endDate);

    @Before
    public void before() {
        // Initialize with one event
        assertThat(filter.apply(event)).hasValueSatisfying(actual -> assertThat(actual).isSameAs(event));
    }

    @Test
    public void filterSameEvent() throws Exception {
        VEvent sameEvent = event;

        assertThat(filter.apply(event)).isEmpty();
        assertThat(filter.apply(sameEvent)).isEmpty();
    }

    @Test
    public void filterEqualEvent() throws Exception {
        VEvent equalEvent =
            new VEvent(event.getStartDate().getDate(), event.getEndDate().getDate(), event.getSummary().getValue());
        equalEvent.getProperties().add(event.getDescription());

        assertThat(filter.apply(equalEvent)).isEmpty();
    }

    @Test
    public void filterDifferentSummary() throws Exception {
        VEvent differentSummary = createVEvent("DifferentSummary", "descr", startDate, endDate);

        assertThat(filter.apply(differentSummary)).hasValue(differentSummary);
    }

    @Test
    public void filterDifferentDescription() throws Exception {
        VEvent differentSummary = createVEvent("Sum", "DifferentDescr", startDate, endDate);

        assertThat(filter.apply(differentSummary)).hasValue(differentSummary);
    }

    @Test
    public void filterDifferentStartDate() throws Exception {
        VEvent differentStartDate = createVEvent("Sum", "descr", differentDate, endDate);

        assertThat(filter.apply(differentStartDate)).hasValue(differentStartDate);
    }

    @Test
    public void filterDifferentEndDate() throws Exception {
        VEvent differentEndDate = createVEvent("Sum", "descr", startDate, differentDate);

        assertThat(filter.apply(differentEndDate)).hasValue(differentEndDate);
    }

    @Test
    public void filterEndDateNull() throws Exception {
        VEvent endDateNull = new VEvent(toDate(startDate),"end date null");

        assertThat(filter.apply(endDateNull)).hasValue(endDateNull);
    }

    @Test
    public void filterStartDateNull() throws Exception {
        VEvent startDateNull = new VEvent();
        startDateNull.getProperties().add(new DtEnd(toDate(endDate)));
        startDateNull.getProperties().add(new Summary("start date null"));

        assertThat(filter.apply(startDateNull)).hasValue(startDateNull);
    }

    private VEvent createVEvent(String sum, String descr, LocalDateTime startDate, LocalDateTime endDate) {
        VEvent event = new VEvent(toDate(startDate), toDate(endDate), sum);
        event.getProperties().add(new Description(descr));
        return event;
    }

    private Date toDate(LocalDateTime of) {
        return new Date(java.util.Date.from(of.atZone(ZoneId.systemDefault()).toInstant()));
    }
}

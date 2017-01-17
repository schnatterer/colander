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
import org.junit.Assert;
import org.junit.Test;

import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneId;

import static org.junit.AssertLambda.assertEmpty;
import static org.junit.AssertLambda.assertOptional;

public class DuplicateFilterTest {
    DuplicateFilter filter = new DuplicateFilter();

    @Test
    @SuppressWarnings("UnnecessaryLocalVariable") // sameEvent makes this more readable
    public void filter() throws Exception {
        LocalDateTime startDate = LocalDateTime.of(2012, Month.DECEMBER, 12, 13, 0);
        LocalDateTime endDate = LocalDateTime.of(2012, Month.DECEMBER, 12, 23, 59);
        LocalDateTime differentDate = LocalDateTime.of(2016, Month.JANUARY, 15, 12, 5);

        VEvent event = createVEvent("Sum", startDate, endDate);
        VEvent sameEvent = event;
        VEvent equalEvent =
            new VEvent(event.getStartDate().getDate(), event.getEndDate().getDate(), event.getSummary().getValue());
        VEvent differentSummary = createVEvent("DifferentSummary", startDate, endDate);
        VEvent differentStartDate = createVEvent("DifferentSummary", differentDate, endDate);
        VEvent differentEndDate = createVEvent("DifferentSummary", startDate, differentDate);

        assertOptional("First call on apply", event, filter.apply(event), Assert::assertSame);
        assertEmpty("Second call on apply", filter.apply(event));
        assertEmpty("Call on apply with same event instance", filter.apply(sameEvent));
        assertEmpty("Call on apply with other event instance, that equals", filter.apply(equalEvent));
        assertOptional("Call on apply with other event instance, different summary", differentSummary,
                       filter.apply(differentSummary), Assert::assertEquals);
        assertOptional("Call on apply with other event instance, different start date", differentStartDate,
                       filter.apply(differentStartDate), Assert::assertEquals);
        assertOptional("Call on apply with other event instance, different end date", differentEndDate,
                       filter.apply(differentEndDate), Assert::assertEquals);
    }

    private VEvent createVEvent(String sum, LocalDateTime startDate, LocalDateTime endDate) {
        return new VEvent(toDate(startDate), toDate(endDate), sum);
    }

    private Date toDate(LocalDateTime of) {
        return new Date(java.util.Date.from(of.atZone(ZoneId.systemDefault()).toInstant()));
    }
}

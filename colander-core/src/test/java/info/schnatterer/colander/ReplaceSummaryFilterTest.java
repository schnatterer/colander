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

import static org.junit.AssertLambda.assertOptional;

public class ReplaceSummaryFilterTest {

    @Test
    public void filterChangesOnMatch() throws Exception {
        ReplaceSummaryFilter filter = new ReplaceSummaryFilter("h.*llo", "hullo");
        VEvent event = new VEvent(new Date(), "hallo icaltools");
        VEvent expectedEvent = new VEvent(new Date(), "hullo icaltools");
        assertOptional("Unexpected filtering result", expectedEvent, filter.apply(event), Assert::assertEquals);
    }

    @Test
    public void filterIgnoresWhenNoMatch() throws Exception {
        ReplaceSummaryFilter filter = new ReplaceSummaryFilter("hallo", "hullo");
        VEvent event = new VEvent(new Date(), "hullo icaltools");
        assertOptional("Unexpected filtering result", event, filter.apply(event), Assert::assertSame);
    }
}

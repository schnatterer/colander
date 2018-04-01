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
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertSame;

class RemoveEmptyEventFilterTest {
    private RemoveEmptyEventFilter filter = new RemoveEmptyEventFilter();

    @Test
    void applyMatch() throws Exception {
        VEvent event = new VEvent(false);
        assertThat(filter.apply(event)).isEmpty();
    }

    @Test
    void applyMatchEmptyStrings() throws Exception {
        VEvent event = new VEvent(new Date(), "");
        event.getProperties().add(new Description(""));
        assertThat(filter.apply(event)).isEmpty();
    }

    @Test
    void applyMatchNull() throws Exception {
        VEvent event = new VEvent(new Date(), null);
        event.getProperties().add(new Description(null));
        assertThat(filter.apply(event)).isEmpty();
    }

    @Test
    void applyDescriptionEmpty() throws Exception {
        VEvent event = new VEvent(new Date(), "sumry");
        assertSame("Unexpected filtering result", event, filter.apply(event).orElse(null));
    }

    @Test
    void applySummaryEmpty() throws Exception {
        VEvent event = new VEvent(false);
        event.getProperties().add(new Description("descr"));
        assertSame("Unexpected filtering result", event, filter.apply(event).orElse(null));
    }

    @Test
    void applySummaryNull() throws Exception {
        VEvent event = new VEvent(new Date(), null);
        event.getProperties().add(new Description("desc"));
        assertSame("Unexpected filtering result", event, filter.apply(event).orElse(null));
    }

    @Test
    void applyDescriptionNull() throws Exception {
        VEvent event = new VEvent(new Date(), "sumry");
        event.getProperties().add(new Description(null));
        assertSame("Unexpected filtering result", event, filter.apply(event).orElse(null));
    }
}

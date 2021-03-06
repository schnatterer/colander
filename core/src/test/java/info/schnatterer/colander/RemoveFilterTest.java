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
import net.fortuna.ical4j.model.property.Summary;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class RemoveFilterTest {

    @Test
    public void applyMatch() throws Exception {
        RemoveFilter filter = new RemoveFilter("hallo", Property.SUMMARY);
        VEvent event = new VEvent(new Date(), "hallo icaltools");
        assertThat(filter.apply(event)).isEmpty();
    }

    @Test
    public void applyNoMatch() throws Exception {
        RemoveFilter filter = new RemoveFilter("hallo", Property.SUMMARY);
        VEvent event = new VEvent(new Date(), "hullo icaltools");
        assertThat(filter.apply(event)).hasValueSatisfying(actual -> assertThat(actual).isSameAs(event));
    }

    @Test
    public void filterSummaryDoesNotExist() throws Exception {
        RemoveFilter filter = new RemoveFilter("hallo", Property.SUMMARY);
        VEvent event = new VEvent();
        assertThat(filter.apply(event)).hasValueSatisfying(actual -> assertThat(actual).isSameAs(event));
    }

    @Test
    public void filterSummaryDoesHaveValue() throws Exception {
        RemoveFilter filter = new RemoveFilter("hallo", Property.SUMMARY);
        VEvent event = new VEvent();
        event.getProperties().add(new Summary(null));
        assertThat(filter.apply(event)).hasValueSatisfying(actual -> assertThat(actual).isSameAs(event));
    }

}

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

import net.fortuna.ical4j.model.component.CalendarComponent;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.Summary;
import org.junit.Assert;
import org.junit.Test;

import java.util.Optional;

public class PropertiesTest {
    CalendarComponent calendarComponent = new VEvent();


    @Test
    public void getSummary() throws Exception {
        Summary expectedSummary = new Summary("value");
        calendarComponent.getProperties().add(expectedSummary);
        Assert.assertEquals(expectedSummary, Properties.getSummary(calendarComponent).orElse(null));
    }

    @Test
    public void getSummaryNoSummary() throws Exception {
        Assert.assertEquals(Optional.empty(), Properties.getSummary(calendarComponent));
    }

    @Test
    public void getSummaryValue() throws Exception {
        String expectedValue = "val";
        calendarComponent.getProperties().add(new Summary(expectedValue));
        Assert.assertEquals(expectedValue, Properties.getSummaryValue(calendarComponent).orElse(null));
    }

    @Test
    public void getSummaryValueNoSummary() throws Exception {
        Assert.assertEquals(Optional.empty(), Properties.getSummaryValue(calendarComponent));
    }

    @Test
    public void getSummaryValueNoSummaryValue() throws Exception {
        Summary expectedSummary = new Summary(null);
        calendarComponent.getProperties().add(expectedSummary);
        Assert.assertEquals(Optional.empty(), Properties.getSummaryValue(calendarComponent));
    }
}

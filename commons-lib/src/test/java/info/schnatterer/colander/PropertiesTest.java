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
import net.fortuna.ical4j.model.property.Description;
import net.fortuna.ical4j.model.property.Summary;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PropertiesTest {
    private CalendarComponent calendarComponent = new VEvent();

    @Test
    void getProperty() throws Exception {
        Summary expectedSummary = new Summary("value");
        calendarComponent.getProperties().add(expectedSummary);
        assertEquals(expectedSummary, Properties.getSummary(calendarComponent).orElse(null));
    }

    @Test
    void getPropertyNoSummary() throws Exception {
        assertEquals(Optional.empty(), Properties.getSummary(calendarComponent));
    }

    @Test
    void getPropertyValue() throws Exception {
        String expectedValue = "val";
        calendarComponent.getProperties().add(new Summary(expectedValue));
        assertEquals(expectedValue, Properties.getSummaryValue(calendarComponent).orElse(null));
    }

    @Test
    void getPropertyValueNoSummary() throws Exception {
        assertEquals(Optional.empty(), Properties.getSummaryValue(calendarComponent));
    }

    @Test
    void getPropertyValueNoSummaryValue() throws Exception {
        Summary expectedSummary = new Summary(null);
        calendarComponent.getProperties().add(expectedSummary);
        assertEquals(Optional.empty(), Properties.getSummaryValue(calendarComponent));
    }

    @Test
    void getDescription() throws Exception {
        Description expectedDescription = new Description("value");
        calendarComponent.getProperties().add(expectedDescription);
        assertEquals(expectedDescription, Properties.getDescription(calendarComponent).orElse(null));
    }

    @Test
    void getDescriptionValue() throws Exception {
        String expectedValue = "val";
        calendarComponent.getProperties().add(new Description(expectedValue));
        assertEquals(expectedValue, Properties.getDescriptionValue(calendarComponent).orElse(null));
    }
}

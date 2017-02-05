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

import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.component.CalendarComponent;

import java.util.Optional;

/**
 * Conveniently provides {@link Property}s in a {@code null}-safe way.
 */
public class Properties {

    public static Optional<Property> getSummary(CalendarComponent component) {
        return getProperty(component, Property.SUMMARY);
    }

    public static Optional<String> getSummaryValue(CalendarComponent component) {
        return getPropertyValue(component, Property.SUMMARY);
    }

    public static Optional<Property> getDescription(CalendarComponent component) {
        return getProperty(component, Property.DESCRIPTION);
    }

    public static Optional<String> getDescriptionValue(CalendarComponent component) {
        return getPropertyValue(component, Property.DESCRIPTION);
    }

    public static Optional<Property> getProperty(CalendarComponent component, String propertyName) {
        return Optional.ofNullable(component.getProperty(propertyName));
    }

    public static Optional<String> getPropertyValue(CalendarComponent component, String propertyName) {
        return getProperty(component, propertyName)
            .map(property -> Optional.ofNullable(property.getValue()))
            .orElse(Optional.empty());
    }
}

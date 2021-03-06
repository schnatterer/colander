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

import java.io.IOException;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.Optional;

/**
 * Replaces regex in a {@link Property} of a calender component.
 */
public class ReplaceFilter implements ColanderFilter {

    private final String stringToReplace;
    private final String regex;
    private final String propertyName;

    /**
     * @param regex regex to match
     * @param stringToReplace regex to replace matching regex
     * @param propertyName the event property to replace
     */
    public ReplaceFilter(String regex, String stringToReplace, String propertyName) {
        this.regex = regex;
        this.stringToReplace = stringToReplace;
        this.propertyName = propertyName;
    }

    @Override
    public Optional<CalendarComponent> apply(CalendarComponent component) {
        try {
            replace(component.getProperty(propertyName));
        } catch (IOException | URISyntaxException | ParseException e) {
            throw new ColanderParserException(e);
        }
        return Optional.of(component);
    }

    /**
     * Visible for testing.
     */
    void replace(Property property) throws IOException, URISyntaxException, ParseException {
        if (property == null) {
            return;
        }
        String value = property.getValue();
        if (value != null) {
            property.setValue(value.replaceAll(regex, stringToReplace));
        }
    }

    public String getRegex() { return regex; }

    public String getStringToReplace() { return stringToReplace; }

    public String getPropertyName() { return propertyName; }
}

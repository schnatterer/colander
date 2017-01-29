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

import net.fortuna.ical4j.model.component.VEvent;

import java.util.Optional;

/**
 * Replaces regex in summary of an event.
 */
class ReplaceSummaryFilter implements VEventFilter {

    private final String stringToReplaceInSummary;
    private final String regex;

    /**
     * @param regex regex to match
     * @param stringToReplaceInSummary regex to replace matching regex
     */
    public ReplaceSummaryFilter(String regex, String stringToReplaceInSummary) {
        this.regex = regex;
        this.stringToReplaceInSummary = stringToReplaceInSummary;
    }

    @Override
    public Optional<VEvent> apply(VEvent event) {
        String value = event.getSummary().getValue();
        event.getSummary().setValue(value.replaceAll(regex, stringToReplaceInSummary));
        return Optional.of(event);
    }

    public String getRegex() { return regex; }

    public String getStringToReplaceInSummary() { return stringToReplaceInSummary; }
}

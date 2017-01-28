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
 * Removes event, when its summary contains a specific string.
 */
class SummaryEventRemoverFilter implements VEventFilter {
    private String summaryContainsString;

    /**
     * @param summaryContainsString remove summary when it contains this string
     */
    public SummaryEventRemoverFilter(String summaryContainsString) {
        this.summaryContainsString = summaryContainsString;
    }

    @Override
    public Optional<VEvent> apply(VEvent event) {
        if (event.getSummary().getValue().contains(summaryContainsString)) {
            return Optional.empty();
        } else {
            return Optional.of(event);
        }
    }

    public String getSummaryContainsString() { return summaryContainsString; }
}

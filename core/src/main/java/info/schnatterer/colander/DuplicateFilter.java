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
import net.fortuna.ical4j.model.property.DtEnd;
import net.fortuna.ical4j.model.property.DtStart;
import net.fortuna.ical4j.model.property.Summary;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * Removes all events that have the same summary, start and end date.
 */
class DuplicateFilter implements VEventFilter {

    private Set<VEvent> filteredEvents = new HashSet<>();

    @Override
    public Optional<VEvent> apply(VEvent event) {
        ComparisonVEvent comparisonVEvent = new ComparisonVEvent(event);

        if (filteredEvents.contains(comparisonVEvent)) {
            return Optional.empty();
        } else {
            filteredEvents.add(comparisonVEvent);
            return Optional.of(event);
        }
    }

    /**
     * Class that specifies the attributes of a {@link VEvent} that are compared when looking for "duplicates".
     */
    private static class ComparisonVEvent extends VEvent {
        private Summary summary;
        private DtStart startDate;
        private DtEnd endDate;

        ComparisonVEvent(VEvent eventToCompare) {
            this.summary = eventToCompare.getSummary();
            this.startDate = eventToCompare.getStartDate();
            this.endDate = eventToCompare.getEndDate();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o)
                return true;
            if (o == null || getClass() != o.getClass())
                return false;
            if (!super.equals(o))
                return false;

            ComparisonVEvent that = (ComparisonVEvent) o;

            if (summary != null ? !summary.equals(that.summary) : that.summary != null)
                return false;
            if (startDate != null ? !startDate.equals(that.startDate) : that.startDate != null)
                return false;
            return endDate != null ? endDate.equals(that.endDate) : that.endDate == null;
        }

        @Override
        public int hashCode() {
            int result = super.hashCode();
            result = 31 * result + (summary != null ? summary.hashCode() : 0);
            result = 31 * result + (startDate != null ? startDate.hashCode() : 0);
            result = 31 * result + (endDate != null ? endDate.hashCode() : 0);
            return result;
        }
    }
}

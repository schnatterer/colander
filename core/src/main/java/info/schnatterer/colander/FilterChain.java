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

import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.ComponentList;
import net.fortuna.ical4j.model.component.CalendarComponent;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.util.CompatibilityHints;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

/**
 * Brings together multiple {@link VEventFilter}s and applies them to all events of an iCal file.
 */
class FilterChain {
    static {
        CompatibilityHints.setHintEnabled(CompatibilityHints.KEY_RELAXED_VALIDATION, true);
    }

    private static final Logger LOG = LoggerFactory.getLogger(FilterChain.class);

    private final List<VEventFilter> filters;

    public FilterChain(List<VEventFilter> filters) {
        this.filters = filters;
    }

    /**
     * Applies all filters of the chain to an iCal file.
     *
     * @param cal the iCal to parse
     * @return the modified iCal, never {@code null}
     */
    Calendar run(Calendar cal) {
        LOG.info("Start processing. Please wait...");
        // Create empty output calendar with same properties
        Calendar calOut = new Calendar(cal.getProperties(), new ComponentList<>());

        ComponentList<CalendarComponent> allComponents = cal.getComponents();
        for (CalendarComponent component : allComponents) {
            if ("VEVENT".equals(component.getName())) {
                filterEvent((VEvent) component).ifPresent(calOut.getComponents()::add);
            } else {
                // Just pipe other components through
                calOut.getComponents().add(component);
            }
        }
        // TODO count amount of filtered classes per apply (using BaseFilter class?)
        LOG.info("Number of records processed: {}", allComponents.size());
        LOG.info("Number of records in new calendar: {}", calOut.getComponents().size());
        LOG.info("Number of records deleted: {}", allComponents.size() - calOut.getComponents().size());

        return calOut;
    }

    /**
     * Visible for testing
     */
    @SuppressWarnings("WeakerAccess")
    protected Optional<VEvent> filterEvent(VEvent event) {
        VEvent filteredEvent = event;
        for (VEventFilter filter : filters) {
            Optional<VEvent> returnedEvent = filter.apply(filteredEvent);
            if (returnedEvent.isPresent()) {
                filteredEvent = returnedEvent.get();
            } else {
                LOG.debug("Filter {} deleted originalEvent {}", filter.getClass().getSimpleName(), event.getSummary());
                return Optional.empty();
            }
        }
        return Optional.of(filteredEvent);
    }
}

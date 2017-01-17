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

import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.data.CalendarOutputter;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.ComponentList;
import net.fortuna.ical4j.model.component.CalendarComponent;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.util.CompatibilityHints;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

/**
 * Brings together multiple {@link VEventFilter}s and applies them to all events of an iCal file.
 */
public class FilterChain {
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
     * @param inputFile the iCal file to parse
     * @param outputFile the file to write the modified iCal file to
     */
    public void run(File inputFile, File outputFile) throws IOException {
        // Reading the file and creating the calendar
        CalendarBuilder builder = new CalendarBuilder();

        LOG.info("Reading calendar file...");

        Calendar cal;
        try {
            cal = builder.build(new FileInputStream(inputFile));
        } catch (ParserException e) {
            throw new IOException(e);
        }

        Calendar outputCal = run(cal);
        write(outputCal, outputFile);
    }

    /**
     * Visible for testing
     */
    @SuppressWarnings("WeakerAccess")
    protected Calendar run(Calendar cal) {
        LOG.info("Start processing. Please wait...");
        // Create empty output calendar with same properties
        Calendar calOut = new Calendar(cal.getProperties(), new ComponentList<>());

        ComponentList<CalendarComponent> events = cal.getComponents("VEVENT");
        for (CalendarComponent calendarComponent : events) {
            filterEvent((VEvent) calendarComponent).ifPresent(calOut.getComponents()::add);
        }
        // TODO count amount of filtered classes per apply (using BaseFilter class?)
        LOG.info("Number of records processed: {}", events.size());
        LOG.info("Number of records in new calendar: {}", calOut.getComponents().size());
        LOG.info("Number of records deleted: {}", events.size() - calOut.getComponents().size());

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
                LOG.debug("Filter {} deleted originalEvent {}", filter, event);
                return Optional.empty();
            }
        }
        return Optional.of(filteredEvent);
    }

    private void write(Calendar call, File outputFile) {
        // write new calendar
        try (FileOutputStream fileOutputStream = new FileOutputStream(outputFile)) {
            CalendarOutputter calendarOutputter = new CalendarOutputter();
            calendarOutputter.output(call, fileOutputStream);
        } catch (FileNotFoundException e) {
            LOG.error("Unable to open file", e);
        } catch (IOException e) {
            LOG.error("Error writing new calendar file", e);
        }
    }
}

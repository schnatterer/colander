/**
 * The MIT License (MIT)
 * <p>
 * Copyright (c) 2017 Johannes Schnatterer
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * <p>
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
import net.fortuna.ical4j.validate.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

/**
 * Handles in and output of calenders conveniently.
 */
class ColanderIO {
    private static final Logger LOG = LoggerFactory.getLogger(ColanderIO.class);

    /**
     * Creates calendar object from an ical file from a create a calender object
     *
     * @param filePath the path to the ical file
     * @return an object representing the ical file
     * @throws FileNotFoundException   if the file does not exist, is a directory rather than a regular file, or for
     *                                 some other reason cannot be opened forreading.
     * @throws IOException             where an error occurs reading data from the specified stream
     * @throws ColanderParserException where an error occurs parsing data from the stream
     */
    Calendar read(String filePath) throws IOException {
        return read(new FileInputStream(filePath));
    }

    /**
     * Creates calendar object from an ical stream from a create a calender object
     *
     * @param input a stream containg the ical file
     * @return an object representing the ical file
     * @throws FileNotFoundException   if the file does not exist, is a directory rather than a regular file, or for
     *                                 some other reason cannot be opened forreading.
     * @throws IOException             where an error occurs reading data from the specified stream
     * @throws ColanderParserException where an error occurs parsing data from the stream
     */
    Calendar read(InputStream input) throws IOException {
        LOG.info("Reading calendar file...");

        try {
            return createCalenderBuilder().build(input);
        } catch (ParserException e) {
            throw new ColanderParserException(e);
        }
    }

    /**
     * Writes a calender object to a file.
     *
     * @param cal        the iCal to write
     * @param outputFile the file to write the modified iCal file to
     * @throws FileNotFoundException   if the file exists but is a directory
     *                                 rather than a regular file, does not exist but cannot
     *                                 be created, or cannot be opened for any other reason
     * @throws IOException             thrown when unable to write to output stream
     * @throws ColanderParserException where calendar validation fails
     */
    void write(Calendar cal, String outputFile) throws IOException {
        // write new calendar
        try (OutputStream outputStream = createOutputStream(outputFile)) {
            CalendarOutputter calendarOutputter = createCalendarOutputter();
            try {
                calendarOutputter.output(cal, outputStream);
            } catch (ValidationException e) {
                throw new ColanderParserException(e);
            }
        }
    }

    /**
     * Visible for testing
     */
    CalendarBuilder createCalenderBuilder() { return  new CalendarBuilder(); }

    /**
     * Visible for testing
     */
    CalendarOutputter createCalendarOutputter() { return new CalendarOutputter(); }

    /**
     * Visible for testing
     */
    OutputStream createOutputStream(String outputFile) throws FileNotFoundException {
        return new FileOutputStream(outputFile);
    }
}

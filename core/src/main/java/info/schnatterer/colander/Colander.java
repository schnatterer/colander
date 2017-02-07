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
import net.fortuna.ical4j.model.Property;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Public Interface of colander.
 */
public class Colander {
    Colander() {}

    /**
     * Puts a calender into colander.
     *
     * @param filePath path to the ical file.
     * @return a new instance of the {@link ColanderBuilder}
     */
    public static ColanderBuilder toss(String filePath) {
        return new ColanderBuilder(filePath);
    }

    /**
     * Builder that allows configuring colander's filters fluently. Use {@link #rinse()} to apply.
     */
    public static class ColanderBuilder {
        List<ColanderFilter> filters = new ArrayList<>();
        final String filePath;

        ColanderBuilder(String filePath) {
            this.filePath = filePath;
        }

        /**
         * Remove event when summary, description, start date or end date are the same in another event.
         *
         * @return a reference to this object.
         */
        public ColanderBuilder removeDuplicateEvents() {
            filters.add(new RemoveDuplicateEventFilter());
            return this;
        }

        /**
         * Removes event when it has
         * <ul>
         *     <li>no summary and </li>
         *     <li>no description.</li>
         * </ul>
         *
         * @return a reference to this object.
         */
        public ColanderBuilder removeEmptyEvents() {
            filters.add(new RemoveEmptyEventFilter());
            return this;
        }

        /**
         * Removes a calender component, when one of its properties contains a specific string.
         *
         * @param propertyName          the event property to search
         * @param propertyContainsString remove component when it's property contains this string
         * @return a reference to this object.
         */
        public ColanderBuilder removePropertyContains(String propertyName, String propertyContainsString) {
            filters.add(new RemoveFilter(propertyContainsString, propertyName));
            return this;
        }

        /**
         * Removes a calender component, when its summary contains a specific string.
         *
         * @param summaryContainsString remove when summary contains this string
         * @return a reference to this object.
         */
        public ColanderBuilder removeSummaryContains(String summaryContainsString) {
            return removePropertyContains(Property.SUMMARY, summaryContainsString);
        }

        /**
         * Removes a calender component, when its summary contains a specific string.
         *
         * @param descriptionContainsString remove when summary contains this string
         * @return a reference to this object.
         */
        public ColanderBuilder removeDescriptionContains(String descriptionContainsString) {
            return removePropertyContains(Property.DESCRIPTION, descriptionContainsString);
        }

        /**
         * Replaces regex in a calender component's property (e.g. summary, description, ..)
         *
         * @param propertyName property to search
         * @param regex                    regex to match
         * @param stringToReplaceInSummary regex to replace matching regex
         * @return a reference to this object.
         */
        public ColanderBuilder replaceInProperty(String propertyName, String regex, String stringToReplaceInSummary) {
            filters.add(new ReplaceFilter(regex, stringToReplaceInSummary, propertyName));
            return this;
        }

        /**
         * Replaces regex in summary of a calender component.
         *
         * @param regex                    regex to match
         * @param stringToReplaceInSummary regex to replace matching regex
         * @return a reference to this object.
         */
        public ColanderBuilder replaceInSummary(String regex, String stringToReplaceInSummary) {
            return replaceInProperty(Property.SUMMARY, regex, stringToReplaceInSummary);
        }

        /**
         * Replaces regex in description of an event.
         *
         * @param regex                    regex to match
         * @param stringToReplaceInSummary regex to replace matching regex
         * @return a reference to this object.
         */
        public ColanderBuilder replaceInDescription(String regex, String stringToReplaceInSummary) {
            return replaceInProperty(Property.DESCRIPTION, regex, stringToReplaceInSummary);
        }

        /**
         * Adds a custom filter to colander.
         *
         * @param filter the event filter
         * @return a reference to this object.
         */
        public ColanderBuilder filter(ColanderFilter filter) {
            filters.add(filter);
            return this;
        }

        /**
         * Rinses colander's input, i.e. applies the filters to.
         * Terminates {@link ColanderBuilder} and returns a {@link ColanderResult} that allows further processing.
         *
         * @return a wrapper around the result that allows for further processing
         * @throws java.io.FileNotFoundException  if the file does not exist, is a directory rather than a regular file, or for
         *                                 some other reason cannot be opened forreading.
         * @throws IOException             where an error occurs reading data from the specified stream
         * @throws ColanderParserException where an error occurs parsing data from the stream
         */
        public ColanderResult rinse() throws IOException {
            return new ColanderResult(filePath, createFilterChain().run(read(filePath)));
        }

        /**
         * Visible for testing.
         *
         * @return the calender at inputFilePath
         */
        Calendar read(String filePath) throws IOException {
            return new ColanderIO().read(filePath);
        }

        /**
         * Visible for testing.
         *
         * @return a filter chain containing the configured filters.
         */
        FilterChain createFilterChain() {
            return new FilterChain(filters);
        }


    }

    /**
     * Representation of rinsed calender, ready for further processing.
     */
    public static class ColanderResult {
        private final Calendar result;
        private final String inputFilePath;

        ColanderResult(String inputFilePath, Calendar result) {
            this.inputFilePath = inputFilePath;
            this.result = result;
        }

        /**
         * Write rinsed calender to ical file
         *
         * @param outputPath the path to the ical file. When {@code null}, a new filename is generated from
         * {@link #inputFilePath}.
         *
         * @throws java.io.FileNotFoundException   if the file exists but is a directory
         *                                 rather than a regular file, does not exist but cannot
         *                                 be created, or cannot be opened for any other reason
         * @throws IOException             thrown when unable to write to output stream
         * @throws ColanderParserException where calendar validation fails
         * @throws java.nio.file.FileAlreadyExistsException if the file exists. Colander is not going to overwrite any
         * files.
         */
        public void toFile(String outputPath) throws IOException {
            write(result, outputPath, inputFilePath);
        }

        /**
         * @return an in-memory-representation of rinsed calender.
         */
        public Calendar toCalendar() {
            return result;
        }

        /**
         * Visible for testing.
         */
        void write(Calendar result, String outputPath, String inputFilePath) throws IOException {
            new ColanderIO().write(result, outputPath, inputFilePath);
        }
    }
}

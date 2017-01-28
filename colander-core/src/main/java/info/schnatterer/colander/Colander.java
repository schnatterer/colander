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
        List<VEventFilter> filters = new ArrayList<>();
        final String filePath;

        ColanderBuilder(String filePath) {
            this.filePath = filePath;
        }

        /**
         * Removes all events that have the same summary, start and end date.
         *
         * @return a reference to this object.
         */
        public ColanderBuilder removeDuplicates() {
            filters.add(new DuplicateFilter());
            return this;
        }

        /**
         * Removes event when it either has
         * <ul>
         *     <li>no summary,</li>
         *     <li>no start date or </li>
         *     <li>no end date.</li>
         * </ul>
         *
         * @return a reference to this object.
         */
        public ColanderBuilder removeEmptyEvents() {
            filters.add(new EmptyEventRemovalFilter());
            return this;
        }

        /**
         * Removes event, when its summery contains a specific string.
         *
         * @param summaryContainsString remove summary when it contains this string
         * @return a reference to this object.
         */
        public ColanderBuilder removeSummaryContains(String summaryContainsString) {
            filters.add(new SummaryEventRemoverFilter(summaryContainsString));
            return this;
        }

        /**
         * Replaces regex in summary of an event.
         *
         * @param regex                    regex to match
         * @param stringToReplaceInSummary regex to replace matching regex
         * @return a reference to this object.
         */
        public ColanderBuilder replaceInSummary(String regex, String stringToReplaceInSummary) {
            filters.add(new ReplaceSummaryFilter(regex, stringToReplaceInSummary));
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

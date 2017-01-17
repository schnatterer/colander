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
package info.schnatterer.colander.cli;

import info.schnatterer.colander.DuplicateFilter;
import info.schnatterer.colander.EmptyEventRemovalFilter;
import info.schnatterer.colander.FilterChain;
import info.schnatterer.colander.ReplaceSummaryFilter;
import info.schnatterer.colander.SummaryEventRemoverFilter;
import info.schnatterer.colander.VEventFilter;
import net.fortuna.ical4j.model.Calendar;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * Command Line Interface for iCal Tools.
 */
//TODO Implement this using a proper CLI framework
public class ColanderCli {
    private static final Logger LOG = LoggerFactory.getLogger(ColanderCli.class);

    public static void main(String[] args) {
        String inputFile = null;
        String outputFile = null;

        if (args.length > 0) {
            if (args[0].equals("--help")) {
                printHelp();
                System.exit(0);
            } else {
                inputFile = args[0];
            }

            if (args.length > 1) {
                outputFile = args[1];
            } else {
                outputFile = inputFile + ".new";
            }
        } else {
            printHelp();
            System.exit(1);
        }

        List<VEventFilter> filters = Arrays
            .asList(new EmptyEventRemovalFilter(),
                    // Replace all single \r characters by \r\n which seems to be expected by ical
                    new ReplaceSummaryFilter("\\r(?!\\n)", "\\r\\n"),
                    new SummaryEventRemoverFilter(". Geburtstag"),
                    new SummaryEventRemoverFilter("Erstes Viertel"),
                    new SummaryEventRemoverFilter("Letztes Viertel"),
                    new SummaryEventRemoverFilter("Vollmond"),
                    new SummaryEventRemoverFilter("Neumond"),
                    new SummaryEventRemoverFilter("Totensonntag"),
                    new DuplicateFilter()
                    );
        Calendar calOut = null;
        try {
            new FilterChain(filters).run(new File(inputFile), new File(outputFile));
        } catch (IOException e) {
            LOG.error("Error parsing Calendar", e);
        }
    }

    private static void printHelp() {
        LOG.info(
            "Reads an ICal calendar file and applies filters to it." + System.lineSeparator() + System.lineSeparator()
            + "colander [source | --help] [destination]" + System.lineSeparator());
    }
}

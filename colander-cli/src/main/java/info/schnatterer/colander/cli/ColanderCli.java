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
package info.schnatterer.colander.cli;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Command Line Interface for iCal Tools.
 */
//TODO Implement this using a proper CLI framework
public class ColanderCli {
    private static final Logger LOG = LoggerFactory.getLogger(ColanderCli.class);

    public static void main(String[] args) {
        printHelp();
    }

    private static void printHelp() {
        LOG.info(
            "Reads an ICal file and applies filters to it." + System.lineSeparator() + System.lineSeparator()
                + "colander [source | --help] [destination]" + System.lineSeparator());
    }
}

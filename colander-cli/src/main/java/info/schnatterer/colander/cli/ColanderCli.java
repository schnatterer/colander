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

import info.schnatterer.colander.cli.Arguments.ParameterException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Main class of Command Line Interface for colander.
 */
class ColanderCli {
    static final String PROGRAM_NAME = "colander";
    private static final Logger LOG = LoggerFactory.getLogger(ColanderCli.class);

    /**
     * Main class should not be instantiated
     */
    ColanderCli() {
    }

    /**
     * Entry point of the application.
     *
     * @param args command line arguments
     */
    @SuppressWarnings("squid:S1166") // Message is logged, stack traces are deliberately hidden to not bloat CLI output
    public static void main(String[] args) {
    /* Parse command line arguments/parameter (command line interface) */
        Arguments cliParams = null;
        try {
            cliParams = Arguments.read(args, PROGRAM_NAME);
        } catch (ParameterException e) {
            LOG.error(e.getMessage());
            System.exit(-1);
        }

        if (!cliParams.isHelp()) {
            startColander(cliParams);
        } else {
            LOG.info(cliParams.usage(""));
        }
    }

    static void startColander(Arguments cliParams) {
        /* TODO Successfully read command line params, do something with it ...*/
        LOG.info("cliparams={}", cliParams);
    }
}

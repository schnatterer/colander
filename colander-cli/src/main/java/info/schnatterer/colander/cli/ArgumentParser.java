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

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Parser for CLI-arguments
 */
class ArgumentParser {
    private static final Logger LOG = LoggerFactory.getLogger(ArgumentParser.class);

    /** Use {@link #read(String[], String)} instead of constructor. */
    ArgumentParser() {
    }

    /**
     * Reads the command line parameters and prints error messages when something went wrong.
     *
     * @param argv arguments passed via CLI
     * @return an instance of {@link Arguments}
     * @throws ArgumentException on syntax error
     */
    public static Arguments read(String[] argv, String programName) {
        Arguments arguments = new Arguments();
        JCommander commander = new JCommander(arguments);
        try {
            commander.setProgramName(programName);
            commander.parse(argv);
        } catch (ParameterException e) {
            // Print error and usage
            String usage = createUsage(e.getMessage() + System.lineSeparator(), commander);
            LOG.error(usage);
            // Rethrow, so the main application knows something went wrong
            throw new ArgumentException(usage, e);
        }

        if (arguments.isHelp()) {
            LOG.info(createUsage("", commander));
        }

        return arguments;
    }

    /**
     * Creates a usage string to be displayed on console.
     *
     * @param prefix written before usage
     * @return the usage string
     */
    private static String createUsage(String prefix, JCommander commander) {
        StringBuilder usage = new StringBuilder(prefix);
        commander.usage(usage, "  ");
        return usage.toString();
    }


    /**
     * Exception thrown when parameter syntax is invalid.
     */
    static class ArgumentException extends RuntimeException {
        /**
         * Constructs a new runtime exception with the specified detail message and cause.
         * <p>Note that the detail message associated with {@code cause} is <i>not</i> automatically incorporated in
         * this runtime exception's detail message.
         *
         * @param message the detail message (which is saved for later retrieval by the {@link #getMessage()} method).
         * @param cause   the cause (which is saved for later retrieval by the {@link #getCause()} method).
         *                (A <tt>null</tt> value is permitted, and indicates that the cause is nonexistent or unknown.)
         */
        ArgumentException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}

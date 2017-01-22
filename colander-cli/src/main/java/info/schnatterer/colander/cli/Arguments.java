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
import com.beust.jcommander.Parameter;

import java.util.ArrayList;
import java.util.List;

/**
 * Parser for CLI-arguments
 */
class Arguments {
    /** Using the {@link JCommander} framework to parse parameters. */
    private JCommander commander = null;

    /** List of unnamed arguments.*/
    @Parameter(required = true, description = "<input.ics> [<output.ics]>")
    private List<String> mainArguments = new ArrayList<>();

    @Parameter(names = "--help", help = true, description = "(optional) Show this message")
    private boolean help;

    /** Use {@link #read(String[], String)} instead of constructor. */
    Arguments() {}

    /**
     * Reads the command line parameters.
     *
     * @param argv arguments passed via CLI
     * @return an instance of {@link Arguments}, never {@code null}
     * @throws ParameterException on syntax error
     */
    static Arguments read(String[] argv, @SuppressWarnings("SameParameterValue") String programName) {
        Arguments cliParams = new Arguments();
        try {
            cliParams.commander = new JCommander(cliParams);
            cliParams.commander.setProgramName(programName);
            cliParams.commander.parse(argv);
        } catch (com.beust.jcommander.ParameterException e) {
            // Rethrow, so the main application knows something went wrong
            throw new ParameterException(cliParams.usage(e.getMessage() + System.lineSeparator()), e);
        }

        return cliParams;
    }

    /**
     * @return input file name. Never {@code null}.
     */
    public String getInputFile() {
        return mainArguments.get(0);
    }

    /**
     * @return output file name. Can be {@code null}!
     */
    public String getOutputFile() {
        String outputFile = null;
        if (mainArguments.size() > 1) {
            outputFile = mainArguments.get(1);
        }
        return outputFile;
    }

    /**
     * @return {@code true} when help paramter was passed. Otherwise {@code false}
     */
    public boolean isHelp() {
        return help;
    }

    /**
     * @param prefix written before usage
     * @return the usage string
     */
    public String usage(String prefix) {
        StringBuilder usage = new StringBuilder(prefix);
        commander.usage(usage, "  ");
        return usage.toString();
    }

    @Override
    public String toString() {
        return "Arguments{" +
            "help=" + help +
            ", mainArguments=" + mainArguments +
            '}';
    }

    /**
     * Exception thrown when parameter syntax is invalid.
     */
    static class ParameterException extends RuntimeException {
        /**
         * Constructs a new runtime exception with the specified detail message and cause.
         * <p>Note that the detail message associated with {@code cause} is <i>not</i> automatically incorporated in
         * this runtime exception's detail message.
         *
         * @param  message the detail message (which is saved for later retrieval by the {@link #getMessage()} method).
         * @param  cause the cause (which is saved for later retrieval by the {@link #getCause()} method).
         *               (A <tt>null</tt> value is permitted, and indicates that the cause is nonexistent or unknown.)
         */
        ParameterException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}

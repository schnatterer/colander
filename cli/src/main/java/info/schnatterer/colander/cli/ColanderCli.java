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

import de.triology.versionname.VersionNames;
import info.schnatterer.colander.Colander;
import info.schnatterer.colander.cli.ArgumentsParser.ArgumentException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Main class of Command Line Interface for colander.
 */
class ColanderCli {
    private static final String PROGRAM_NAME = "colander";
    private static final Logger LOG = LoggerFactory.getLogger(ColanderCli.class);

    /**
     * Main class should not be instantiated directly, only via {@link #main(String[])}.
     * Visible for testing.
     */
    ColanderCli() {
    }

    /**
     * Entry point of the application
     *
     * @param args arguments passed via CLI
     */
    public static void main(String[] args) {
        System.exit(new ColanderCli().execute(args).getExitStatus());
    }

    /**
     * Parses {@code args} and starts {@link Colander}.
     *
     * @param args comand line args to parse.
     * @return an exit status to be returned to CLI.
     */
    @SuppressWarnings({"squid:S1166", // Exceptions are logged in ArgumentsParser by contract.
        "squid:S2629" // Log statements are used for console output
    })
    ExitStatus execute(String[] args) {
        LOG.info(createProgramNameWithVersion());
        Arguments cliParams;
        try {
            cliParams = ArgumentsParser.read(args, PROGRAM_NAME);
        } catch (ArgumentException e) {
            return ExitStatus.ERROR_ARGS;
        }

        if (!cliParams.isHelp()) {
            return startColander(cliParams);
        }
        return ExitStatus.SUCCESS;
    }

    private String createProgramNameWithVersion() {
        String programName = PROGRAM_NAME;
        String versionNameFromManifest = VersionNames.getVersionNameFromManifest();
        if (!versionNameFromManifest.isEmpty()) {
            programName = programName + " " + versionNameFromManifest;
        }
        return programName;
    }

    /**
     * Converts an {@link Arguments} object to a {@link Colander} and rinses.
     *
     * @param args comand line args to parse.
     * @return an exit status to be returned to CLI.
     */
    ExitStatus startColander(Arguments args) {
        LOG.debug("CLI arguments={}", args);
        Colander.ColanderBuilder colander = createColanderBuilder(args.getInputFile());
        if (args.isRemoveDuplicateEvents()) {
            colander.removeDuplicateEvents();
        }
        if (args.isRemoveEmptyEvents()) {
            colander.removeEmptyEvents();
        }
        args.getReplaceInSummary().forEach(colander::replaceInSummary);
        args.getReplaceInDescription().forEach(colander::replaceInDescription);
        args.getRemoveSummaryContains().forEach(colander::removeSummaryContains);
        args.getRemoveDescriptionContains().forEach(colander::removeDescriptionContains);

        try {
            colander.rinse().toFile(args.getOutputFile());
        } catch (Exception e) {
            LOG.error("Error while parsing or writing calender: " + e.getMessage(), e);
            return ExitStatus.ERROR_PARSING;
        }
        return ExitStatus.SUCCESS;
    }

    /**
     * Visible for testing
     */
    Colander.ColanderBuilder createColanderBuilder(String inputFile) {
        return Colander.toss(inputFile);
    }
}

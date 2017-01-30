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

import info.schnatterer.colander.test.ITCases;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.ExpectedSystemExit;
import org.junit.rules.TemporaryFolder;

import java.io.File;

import static org.junit.Assert.assertTrue;

/**
 * Integration tests that tests colander CLI end-to-end, from ics file to ics file, using
 * {@link ColanderCli#main(String[])} method and exit codes.
 */
public class ColanderCliITCase {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Rule
    public final ExpectedSystemExit exit = ExpectedSystemExit.none();

    @Test
    public void endToEnd() throws Exception {
        String outputPath = folder.getRoot().toString() + "/out.ics";
        exit.expectSystemExitWithStatus(0);
        exit.checkAssertionAfterwards(() -> {
            assertTrue("Output not written", new File(outputPath).exists());
            ITCases.verifyParsedIcs(outputPath);
        });
        execute(
            "--remove-duplicates",
            "--remove-empty",
            "--remove-summary", "Remove me",
            "--replace-summary \\r(?!\\n)=\\r\\n",
            ITCases.getFilePathTestIcs(folder),
            outputPath
        );
    }

    @Test
    public void endToEndParsingArgs() throws Exception {
        exit.expectSystemExitWithStatus(1);
        execute("--wtf");
    }

    @Test
    public void endToEndExceptionParsing() throws Exception {
        exit.expectSystemExitWithStatus(2);
        // Try to overwrite input file
        execute(ITCases.getFilePathTestIcs(folder), ITCases.getFilePathTestIcs(folder));
    }

    private void execute(String... args) { ColanderCli.main(args); }
}

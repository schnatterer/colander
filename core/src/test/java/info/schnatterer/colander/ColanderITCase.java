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

import info.schnatterer.colander.test.ITCases;
import net.fortuna.ical4j.model.Property;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.util.Optional;

import static org.junit.Assert.assertTrue;

/**
 * Integration tests that tests colander end-to-end, from ics file to ics file.
 */
public class ColanderITCase {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Test
    public void endToEnd() throws Exception {
        String outputPath = folder.getRoot().toString() + "/out.ics";
        String inputPath = ITCases.getFilePathTestIcs(folder);
        Colander.toss(inputPath)
            .removeDuplicateEvents()
            .removeEmptyEvents()
            .removePropertyContains(Property.SUMMARY, "Remove me")
            // Generic replace in property
            .replaceInProperty(Property.DESCRIPTION, "L.ne", "Line")
            // Convenience: replace in property summary
            .replaceInSummary("Replace", "Replace!")
            // NOP filter
            .filter(Optional::of)
            .rinse()
            .toFile(outputPath);
        assertTrue("Output not written", new File(outputPath).exists());
        ITCases.verifyParsedIcs(inputPath, outputPath);
    }
}

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
import org.hamcrest.junit.ExpectedException;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.*;

public class ArgumentsTest {
    private static final String PROGRAM_NAME = "progr";

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void read() throws Exception {
        Arguments args = read("input", "output");

        assertEquals("Input file", "input", args.getInputFile());
        assertEquals("Output file", "output", args.getOutputFile());
    }

    @Test
    public void readInputOnly() throws Exception {
        Arguments args = read("input");

        assertEquals("Input file", "input", args.getInputFile());
        assertNull("Output file", args.getOutputFile());
    }

    @Test
    public void readNoMainArgs() throws Exception {
        expectedException.expect(ParameterException.class);
        expectedException.expectMessage("Main parameters");
        read("");
    }

    @Test
    public void readHelp() throws Exception {
        assertTrue("Unexpected return on read()", read("input", "output", "--help").isHelp());
    }

    private Arguments read(String ... argv) {
        return Arguments.read(argv, PROGRAM_NAME);
    }
}

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

import info.schnatterer.colander.Colander;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ColanderCliTest {

    @Spy
    private ColanderCli cli;

    @Mock
    private Colander.ColanderBuilder builder;

    @Mock
    private Colander.ColanderResult result;

    @Mock
    private Arguments args;

    @Before
    public void before() throws Exception {
        doReturn(builder).when(cli).createColanderBuilder(any());
        when(builder.rinse()).thenReturn(result);
    }

    @Test
    public void execute() throws Exception {
        assertEquals("Exit status", ExitStatus.SUCCESS, execute("a/b.ical"));
    }

    @Test
    public void executeHelp() throws Exception {
        assertEquals("Exit status", ExitStatus.SUCCESS, execute("--help"));
        verifyZeroInteractions(builder, result);
    }

    @Test
    public void executeErrorArgs() throws Exception {
        assertEquals("Exit status", ExitStatus.ERROR_ARGS, execute("--wtf"));
    }

    @Test
    public void executeErrorParsing() throws Exception {
        when(builder.rinse()).thenThrow(new RuntimeException("Mocked exception"));
        assertEquals("Exit status", ExitStatus.ERROR_PARSING, execute("a/b.ical"));
    }

    @Test
    public void startColander() throws Exception {
        String expectedInput = "in";
        String expectedOutput = "out";
        when(args.getInputFile()).thenReturn(expectedInput);
        when(args.getOutputFile()).thenReturn(expectedOutput);
        when(args.isRemoveDuplicates()).thenReturn(true);
        when(args.isRemoveEmpty()).thenReturn(true);
        when(args.getRemoveSummaryContains()).thenReturn(Arrays.asList("y", "z"));
        when(args.getReplaceInSummary()).thenReturn(new HashMap<String, String>() {{
                put("a", "b");
                put("c", "d");
            }});

        assertEquals("Exit status", ExitStatus.SUCCESS, cli.startColander(args));

        verify(cli).createColanderBuilder(expectedInput);
        verify(builder).removeDuplicates();
        verify(builder).removeEmptyEvents();
        verify(builder).replaceInSummary("a", "b");
        verify(builder).replaceInSummary("c", "d");
        verify(builder).removeSummaryContains("y");
        verify(builder).removeSummaryContains("z");
        verify(result).toFile(expectedOutput);
    }

    @Test
    public void startColanderEmptyArgs() throws Exception {
        assertEquals("Exit status", ExitStatus.SUCCESS, cli.startColander(args));

        verify(cli).createColanderBuilder(null);
        verify(builder, never()).removeDuplicates();
        verify(builder, never()).removeEmptyEvents();
        verify(builder, never()).replaceInSummary(anyString(), anyString());
        verify(builder, never()).removeSummaryContains(anyString());
        verify(result).toFile(null);
    }

    private ExitStatus execute(String... args) {
        return cli.execute(args);
    }

}

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

import com.beust.jcommander.Parameter;

import java.util.ArrayList;
import java.util.List;

/**
 * Value object that holds the arguments passed to CLI.
 */
public class Arguments {

    /** List of unnamed arguments.*/
    @Parameter(required = true, description = "<input.ics> [<output.ics]>")
    private List<String> mainArguments = new ArrayList<>();

    @Parameter(names = "--help", help = true, description = "(optional) Show this message")
    private boolean help;

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

    @Override
    public String toString() {
        return "Arguments{" +
            "help=" + help +
            ", mainArguments=" + mainArguments +
            '}';
    }


}

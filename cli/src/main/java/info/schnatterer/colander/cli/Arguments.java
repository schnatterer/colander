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

import com.beust.jcommander.DynamicParameter;
import com.beust.jcommander.Parameter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Value object that holds the arguments passed to CLI.
 */
// JCommander involves a lot of annotation magic, which leads to a lot of warnings which don't really apply here
@SuppressWarnings({"unused", "MismatchedQueryAndUpdateOfCollection", "FieldCanBeLocal"})
public class Arguments {

    /** List of unnamed arguments.*/
    @Parameter(required = true, description = "<input.ics> [<output.ics]>")
    private List<String> mainArguments = new ArrayList<>();

    @DynamicParameter(names = "--replace-summary", description = "Replace in summary (regex)")
    private Map<String, String> replaceInSummary = new HashMap<>();

    @Parameter(names = "--remove-summary", description = "Remove when summary contains expression")
    private List<String> removeSummaryContains = new ArrayList<>();

    @Parameter(names = "--remove-duplicates", description = "Remove when summary, start date or end date are empty")
    private boolean removeDuplicates = false;

    @Parameter(names = "--remove-empty", description = "Remove when summary, start date or end date appear multiple times")
    private boolean removeEmpty = false;

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
     * @return pairs of regexes to be replaced by each other within the summary. Replace key by value. Never {@code null}.
     */
    public Map<String, String> getReplaceInSummary() { return replaceInSummary; }

    /**
     * @return the terms that when contained in summary, lead to removal.
     */
    public List<String> getRemoveSummaryContains() { return removeSummaryContains; }

    /**
     * @return {@code true} when duplicates should be removed. Otherwise {@code false}.
     */
    public boolean isRemoveDuplicates() { return removeDuplicates; }

    /**
     * @return {@code true} when empty events should be removed. Otherwise {@code false}.
     */
    public boolean isRemoveEmpty() { return removeEmpty; }

    /**
     * @return {@code true} when help argument was passed. Otherwise {@code false}.
     */
    public boolean isHelp() { return help;  }

    @Override
    public String toString() {
        return "Arguments{" +
            "mainArguments=" + mainArguments +
            ", replaceInSummary=" + replaceInSummary +
            ", removeSummaryContains=" + removeSummaryContains +
            ", removeDuplicates=" + removeDuplicates +
            ", removeEmpty=" + removeEmpty +
            ", help=" + help +
            '}';
    }
}
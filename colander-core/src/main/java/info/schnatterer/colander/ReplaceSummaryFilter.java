package info.schnatterer.colander;

import net.fortuna.ical4j.model.component.VEvent;

import java.util.Optional;

/**
 * Replaces regex in summary of an event.
 */
public class ReplaceSummaryFilter implements VEventFilter {

    private final String stringToReplaceInSummary;
    private final String regex;

    public ReplaceSummaryFilter(String regex, String stringToReplaceInSummary) {
        this.regex = regex;
        this.stringToReplaceInSummary = stringToReplaceInSummary;
    }

    @Override
    public Optional<VEvent> apply(VEvent event) {
        String value = event.getSummary().getValue();
        event.getSummary().setValue(value.replaceAll(regex, stringToReplaceInSummary));
        return Optional.of(event);
    }

}

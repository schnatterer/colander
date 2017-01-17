package info.schnatterer.colander;

import net.fortuna.ical4j.model.component.VEvent;

import java.util.Optional;

/**
 * Removes event, when it has a specific summary.
 */
public class SummaryEventRemoverFilter implements VEventFilter {
    String summaryContainsString;

    public SummaryEventRemoverFilter(String summaryContainsString) {
        this.summaryContainsString = summaryContainsString;
    }

    @Override
    public Optional<VEvent> apply(VEvent event) {
        if (event.getSummary().getValue().contains(summaryContainsString)) {
            return Optional.empty();
        } else {
            return Optional.of(event);
        }
    }
}

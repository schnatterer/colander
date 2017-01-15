package info.schnatterer.icaltools;

import net.fortuna.ical4j.model.component.VEvent;

import java.util.Optional;

/**
 * Interface for filters that mutate or delete events in a filter chain.
 */
@FunctionalInterface
public interface VEventFilter {
    /**
     * Filters an event.
     *
     * @param event subject to be filtered
     * @return an event to be passed to next apply.
     */
    Optional<VEvent> apply(VEvent event);
}

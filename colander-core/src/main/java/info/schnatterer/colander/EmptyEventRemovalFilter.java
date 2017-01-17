package info.schnatterer.colander;

import net.fortuna.ical4j.model.component.VEvent;

import java.util.Optional;

/**
 * Removes event when it either has
 * <ul>
 *     <li>no summary,</li>
 *     <li>no start date or </li>
 *     <li>no end date.</li>
 * </ul>
 */
public class EmptyEventRemovalFilter implements VEventFilter {

    @Override
    public Optional<VEvent> apply(VEvent event) {
        if (event.getSummary() == null || event.getStartDate() == null || event.getEndDate() == null) {
            return Optional.empty();
        } else {
            return Optional.of(event);
        }
    }
}

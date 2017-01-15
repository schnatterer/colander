package info.schnatterer.icaltools;

import net.fortuna.ical4j.model.Date;
import net.fortuna.ical4j.model.component.VEvent;
import org.junit.Assert;
import org.junit.Test;

import static org.junit.AssertLambda.assertOptional;

public class ReplaceSummaryFilterTest {

    @Test
    public void filterChangesOnMatch() throws Exception {
        ReplaceSummaryFilter filter = new ReplaceSummaryFilter("h.*llo", "hullo");
        VEvent event = new VEvent(new Date(), "hallo icaltools");
        VEvent expectedEvent = new VEvent(new Date(), "hullo icaltools");
        assertOptional("Unexpected filtering result", expectedEvent, filter.apply(event), Assert::assertEquals);
    }

    @Test
    public void filterIgnoresWhenNoMatch() throws Exception {
        ReplaceSummaryFilter filter = new ReplaceSummaryFilter("hallo", "hullo");
        VEvent event = new VEvent(new Date(), "hullo icaltools");
        assertOptional("Unexpected filtering result", event, filter.apply(event), Assert::assertSame);
    }
}

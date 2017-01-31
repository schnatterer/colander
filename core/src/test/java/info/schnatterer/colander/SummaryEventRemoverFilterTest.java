package info.schnatterer.colander;

import net.fortuna.ical4j.model.Date;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.Summary;
import org.junit.Assert;
import org.junit.Test;

import static org.junit.AssertLambda.assertEmpty;
import static org.junit.AssertLambda.assertOptional;

public class SummaryEventRemoverFilterTest {

    @Test
    public void applyMatch() throws Exception {
        SummaryEventRemoverFilter filter = new SummaryEventRemoverFilter("hallo");
        VEvent event = new VEvent(new Date(), "hallo icaltools");
        assertEmpty("Unexpected filtering result", filter.apply(event));
    }

    @Test
    public void applyNoMatch() throws Exception {
        SummaryEventRemoverFilter filter = new SummaryEventRemoverFilter("hallo");
        VEvent event = new VEvent(new Date(), "hullo icaltools");
        assertOptional("Unexpected filtering result", event, filter.apply(event), Assert::assertSame);
    }

    @Test
    public void filterSummaryDoesNotExist() throws Exception {
        SummaryEventRemoverFilter filter = new SummaryEventRemoverFilter("hallo");
        VEvent event = new VEvent();
        assertOptional("Unexpected filtering result", event, filter.apply(event), Assert::assertSame);
    }

    @Test
    public void filterSummaryDoesHaveValue() throws Exception {
        SummaryEventRemoverFilter filter = new SummaryEventRemoverFilter("hallo");
        VEvent event = new VEvent();
        event.getProperties().add(new Summary(null));
        assertOptional("Unexpected filtering result", event, filter.apply(event), Assert::assertSame);
    }

}

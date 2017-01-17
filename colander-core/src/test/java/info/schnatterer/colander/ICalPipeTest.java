package info.schnatterer.colander;

import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.ComponentList;
import net.fortuna.ical4j.model.Date;
import net.fortuna.ical4j.model.component.CalendarComponent;
import net.fortuna.ical4j.model.component.VEvent;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.AssertLambda.assertEmpty;
import static org.junit.AssertLambda.assertOptional;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ICalPipeTest {

    private VEventFilter passThroughFilter1 = mock(VEventFilter.class);
    private VEventFilter passThroughFilter2 = mock(VEventFilter.class);
    private VEvent inputEvent = mock(VEvent.class);

    @Before
    public void setUp() {
        when(passThroughFilter1.apply(any(VEvent.class))).thenAnswer(new PassThroughAnswer());
        when(passThroughFilter2.apply(any(VEvent.class))).thenAnswer(new PassThroughAnswer());
    }

    @Test
    public void testParse() {
        FilterChain pipe = new FilterChain(Arrays.asList(passThroughFilter1, passThroughFilter2));

        VEvent event1 = new VEvent(new Date(), "event1");
        VEvent event2 = new VEvent(new Date(), "event2");
        Calendar inputCalender = new Calendar(new ComponentList<CalendarComponent>() {{
            add(event1);
            add(event2);
        }});

        Calendar outputCalendar = pipe.run(inputCalender);
        verify(passThroughFilter1).apply(event1);
        verify(passThroughFilter1).apply(event2);
        verify(passThroughFilter2).apply(event1);
        verify(passThroughFilter2).apply(event2);
        assertTrue("Event 1 not in output calender", outputCalendar.getComponents().contains(event1));
        assertTrue("Event 2 not in output calender", outputCalendar.getComponents().contains(event2));
    }

    @Test
    public void testParseDelete() {

        VEvent event1 = new VEvent(new Date(), "event1");
        VEvent event2 = new VEvent(new Date(), "event2");
        Calendar inputCalender = new Calendar(new ComponentList<CalendarComponent>() {{
            add(event1);
            add(event2);
        }});

        VEventFilter deleteEventFilter = mock(VEventFilter.class);
        when(deleteEventFilter.apply(any(VEvent.class))).thenAnswer(new PassThroughAnswer());
        when(deleteEventFilter.apply(event1)).thenReturn(Optional.empty());
        FilterChain pipe = new FilterChain(Arrays.asList(passThroughFilter1, deleteEventFilter, passThroughFilter2));

        Calendar outputCalendar = pipe.run(inputCalender);
        verify(passThroughFilter1).apply(event1);
        verify(passThroughFilter1).apply(event2);
        verify(deleteEventFilter).apply(event1);
        verify(deleteEventFilter).apply(event2);
        verify(passThroughFilter2, never()).apply(event1);
        verify(passThroughFilter2).apply(event2);
        assertFalse("Event 1 in output calender", outputCalendar.getComponents().contains(event1));
        assertTrue("Event 2 not in output calender", outputCalendar.getComponents().contains(event2));
    }

    @Test
    public void testFilterEvent() {
        FilterChain pipe = new FilterChain(Arrays.asList(passThroughFilter1, passThroughFilter2));

        Optional<VEvent> actualFilteredEvent = pipe.filterEvent(inputEvent);
        verify(passThroughFilter1).apply(inputEvent);
        verify(passThroughFilter2).apply(inputEvent);
        assertOptional("Unexpected filtered event", inputEvent, actualFilteredEvent, Assert::assertEquals);
    }

    @Test
    public void testFilterEventDelete() {
        VEventFilter filter2 = mock(VEventFilter.class);
        FilterChain pipe = new FilterChain(Arrays.asList(passThroughFilter1, filter2, passThroughFilter2));

        Optional<VEvent> vEvent = pipe.filterEvent(inputEvent);
        verify(passThroughFilter1).apply(inputEvent);
        verify(filter2).apply(inputEvent);
        verify(passThroughFilter2, never()).apply(inputEvent);
        assertEmpty("Event not deleted", vEvent);
    }

    private static class PassThroughAnswer implements Answer<Optional<VEvent>> {
        @Override
        public Optional<VEvent> answer(InvocationOnMock invocationOnMock) throws Throwable {
            return Optional.of((VEvent) invocationOnMock.getArguments()[0]);
        }
    }
}

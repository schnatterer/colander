package info.schnatterer.colander;

import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.data.CalendarOutputter;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.validate.ValidationException;
import org.hamcrest.junit.ExpectedException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import java.io.*;

import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ColanderIOTest {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();
    @Mock
    private CalendarBuilder builder;
    @Mock
    private CalendarOutputter outputter;
    private OutputStream outStream = new ByteArrayOutputStream();

    private ColanderIO io = new ColanderIOForTest();

    @Test
    public void read() throws Exception {
        Calendar expectedCalendar = mock(Calendar.class);
        InputStream stream = mock(InputStream.class);
        when(builder.build(stream)).thenReturn(expectedCalendar);

        Calendar actualCalender = io.read(stream);
        assertSame("Unexpected calendar returned", expectedCalendar, actualCalender);
    }

    @Test
    public void readException() throws Exception {
        when(builder.build(any(InputStream.class))).thenThrow(ParserException.class);

        expectedException.expect(ColanderParserException.class);
        io.read(mock(InputStream.class));
    }

    @Test
    public void write() throws Exception {
        Calendar expectedCalendar = mock(Calendar.class);
        String expectedFile = "expectedFile";

        io.write(expectedCalendar, expectedFile);
        //calendarOutputter.output is final and cant be mocked. So assert something else
        assertNotEquals("write() did not write anything", 0,
            ((ByteArrayOutputStream) outStream).toByteArray().length);
    }

    @Test
    public void writeValidationException() throws Exception {
        Calendar expectedCalendar = mock(Calendar.class);
        String expectedFile = "expectedFile";
        outStream = mock(OutputStream.class, new ThrowValidationExceptionOnEachMethodCall());
        expectedException.expect(ColanderParserException.class);

        // Call method under test
        io.write(expectedCalendar, expectedFile);
        System.out.println(mockingDetails(outStream).getInvocations());
        verify(outStream, atLeastOnce()).close();
    }

    /**
     * Answer that makes mock throw an {@link ValidationException} on each method call.
     */
    private static class ThrowValidationExceptionOnEachMethodCall implements Answer {
        public Object answer(InvocationOnMock invocation) throws Throwable {
            throw new ValidationException("Mocked Exception");
        }
    }

    private class ColanderIOForTest extends ColanderIO {
        @Override
        CalendarBuilder createCalenderBuilder() { return builder; }

        @Override
        CalendarOutputter createCalendarOutputter() { return outputter; }

        @Override
        OutputStream createOutputStream(String outputFile) throws FileNotFoundException {
            return outStream;
        }
    }

}

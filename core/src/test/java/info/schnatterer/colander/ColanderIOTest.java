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
package info.schnatterer.colander;

import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.data.CalendarOutputter;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.validate.ValidationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.FileAlreadyExistsException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class ColanderIOTest {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(ColanderIO.DATE_TIME_FORMAT_FILE_NAME);

    @Mock
    private CalendarBuilder builder;
    @Mock
    private CalendarOutputter outputter;
    private OutputStream outStream = new ByteArrayOutputStream();

    private ColanderIO io = new ColanderIOForTest();
    private String outputPath;

    @Test
    void read() throws Exception {
        Calendar expectedCalendar = mock(Calendar.class);
        InputStream stream = mock(InputStream.class);
        when(builder.build(stream)).thenReturn(expectedCalendar);

        Calendar actualCalender = io.read(stream);
        assertSame(expectedCalendar, actualCalender, "Unexpected calendar returned");
    }

    @Test
    void readException() throws Exception {
        ParserException expectedException = new ParserException("mocked exception message", 42);
        when(builder.build(any(InputStream.class))).thenThrow(expectedException);

        ColanderParserException actualException = assertThrows(ColanderParserException.class,
            () -> io.read(mock(InputStream.class)));

        assertEquals(expectedException.getMessage(), actualException.getMessage());
    }

    @Test
    void write() throws Exception {
        String expectedFile = "expectedFile";

        io.write(mock(Calendar.class), expectedFile, null);
        //calendarOutputter.output is final and cant be mocked. So assert something else
        assertNotEquals(0, ((ByteArrayOutputStream) outStream).toByteArray().length, "write() did not write anything");
    }

    @Test
    void writeValidationException() throws Exception {
        String expectedFile = "expectedFile";
        String expectedMessage = "mocked exception message";
        outStream = mock(OutputStream.class, new ThrowValidationExceptionOnEachMethodCall(expectedMessage));

        ColanderParserException actualException = assertThrows(ColanderParserException.class,
            () -> io.write(mock(Calendar.class), expectedFile, null));

        assertEquals(expectedMessage, actualException.getMessage());
        System.out.println(mockingDetails(outStream).getInvocations());
        verify(outStream, atLeastOnce()).close();
    }

    @Test
    void writePathNull() throws Exception {
        LocalDateTime dateBefore = createComparableDateNow(LocalDateTime.now().format(formatter), formatter);
        io.write(mock(Calendar.class), null, "a/b.someEnding");

        assertThat(outputPath).startsWith("a/b");
        assertThat(outputPath).endsWith(".someEnding");

        verifyDateInNewFileName(outputPath, dateBefore, "\\.someEnding");
    }

    @Test
    void writePathNullInputPathNoFileExtension() throws Exception {
        Calendar expectedCalendar = mock(Calendar.class);
        LocalDateTime dateBefore = createComparableDateNow(LocalDateTime.now().format(formatter), formatter);

        io.write(expectedCalendar, null, "a/b");

        assertThat(outputPath).startsWith("a/b");

        verifyDateInNewFileName(outputPath, dateBefore, "");
    }

    @Test
    void writeFileExists() throws Exception {

        assertThrows(FileAlreadyExistsException.class,
            () -> io.write(mock(Calendar.class),
                // Just use this classes file to emulate an existing file
                createPathToClassFile(),
                null));
    }

    @Test
    void writeFileAllArgumentsNull() throws Exception {
        ColanderParserException actualException = assertThrows(ColanderParserException.class,
            () -> io.write(mock(Calendar.class), null, null));

        assertThat(actualException.getMessage()).contains("Both input and output file paths are null");
    }

    /**
     * Answer that makes mock throw an {@link ValidationException} on each method call.
     */
    private static class ThrowValidationExceptionOnEachMethodCall implements Answer {
        String message;

        ThrowValidationExceptionOnEachMethodCall(String message) {
            this.message = message;
        }

        public Object answer(InvocationOnMock invocation) throws Throwable {
            throw new ValidationException(message);
        }
    }

    private String createPathToClassFile() {
        return getClass().getProtectionDomain().getCodeSource().getLocation().getPath()
            + getClass().getName().replace(".", "/") + ".class";
    }

    private LocalDateTime createComparableDateNow(String format, DateTimeFormatter formatter) {
        return LocalDateTime.parse(format, formatter);
    }

    private void verifyDateInNewFileName(String writtenPath, LocalDateTime dateBefore, String extension) {
        Pattern pattern = Pattern.compile("a/b-(.*)" + extension);
        Matcher matcher = pattern.matcher(writtenPath);
        assertTrue(matcher.find(), "Date not found in new file name");
        LocalDateTime newFileNameDate =
            LocalDateTime.parse(matcher.group(1), formatter);
        assertTrue(newFileNameDate.isAfter(dateBefore) || newFileNameDate.isEqual(dateBefore),
            "Date in new file name is unexpected. Expected equal or later than " + dateBefore + ", but was " + newFileNameDate);
    }

    private class ColanderIOForTest extends ColanderIO {
        @Override
        CalendarBuilder createCalenderBuilder() {
            return builder;
        }

        @Override
        CalendarOutputter createCalendarOutputter() {
            return outputter;
        }

        @Override
        OutputStream createOutputStream(String outputFile) throws FileNotFoundException {
            ColanderIOTest.this.outputPath = outputFile;
            return outStream;
        }
    }

}

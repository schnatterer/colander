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

import net.fortuna.ical4j.model.component.CalendarComponent;

import java.lang.reflect.ParameterizedType;
import java.util.Optional;

/**
 * Base class for filters, that filter only specific
 */
public abstract class TypedColanderFilter<T extends CalendarComponent> implements ColanderFilter {


    /**
     * Template method for concrete classes. Same as {@link #apply(CalendarComponent)}, but casted to the calender
     * component type that this filter applies to.
     *
     * @param concreteComponent concrete calender component type that this filter applies to.
     * @return a calendar component to be passed to next filter or {@link Optional#empty()} if the component should be
     * removed.
     * @throws ColanderParserException if anything goes wrong
     */
    protected abstract Optional<CalendarComponent> applyTyped(T concreteComponent);


    @Override
    public Optional<CalendarComponent> apply(CalendarComponent abstractComponent) {
        Class<T> filteredComponentType = getFilteredComponentType();
        if (filteredComponentType.isInstance(abstractComponent)) {
            return applyTyped(filteredComponentType.cast(abstractComponent));
        } else {
            // Don't filter
            return Optional.of(abstractComponent);
        }
    }


    @SuppressWarnings("unchecked") // Due to type erasure, there seem to be no good options of getting the generic type.
    private Class<T> getFilteredComponentType() {
        // Note that this only works for complex type hierarchies. Let's just not create these!
        return (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }
}

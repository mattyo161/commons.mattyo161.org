/*
 * EventList.java
 *
 * Created on March 31, 2004, 12:45 AM
 */
package org.mattyo161.commons.cal;

import java.util.*;
import java.text.*;
/**
 * EventList is a container for <code>Event</code> objects. It keeps all events sorted by date and can
 * retrieve a range of events, as well as allow you to iterate through the Events in the list
 * @author  mattyo1
 * @see Event
 */
public class EventList {
    private TreeSet eventSet = new TreeSet();
    
    /** Creates a new instance of EventList */
    public EventList() {
    }
    
    /**
     * Adds an <code>Event</code> to the <code>EventList</code> duplicates are ignored
     * @param Event the Event to add to the EventList
     * @see Event
     */
    public void add(Event event) {
        eventSet.add(event);
    }
    
    /**
     * Retrieves a range from events from the given startDate to the given endDate, and the dates are
     * inclusive, that is the retuned EventList will include events from both the startDate and the endDate.
     * The comparison for the range takes into account time so you can grab events from 12:00 to 5:00 PM if you want
     * @param startDate the date to start grabbing Events from the list
     * @param endDate the date to end grabbing Events from the list
     * @return An EventList containing all the events that matched the date range.
     * @see Event
     */
    public EventList getRange(Calendar startDate, Calendar endDate) {
        long startMillis = startDate.getTimeInMillis();
        long endMillis = endDate.getTimeInMillis();
        EventList subEvents = new EventList();
        for (Iterator i = eventSet.iterator(); i.hasNext(); ) {
            Event currEvent = (Event)i.next();
            if (currEvent.getEventCalendar().getTimeInMillis() >= startMillis &&
                    currEvent.getEventCalendar().getTimeInMillis() <= endMillis) {
                subEvents.add(currEvent);
            } else if (currEvent.getEventCalendar().getTimeInMillis() <= endMillis) {
                break;
            }
        }
        return subEvents;
    }
    
    /**
     * Retrieves a range from events from the given startDate to the end of the list, and the startDate is
     * inclusive.
     * @param startDate the date to start grabbing Events from the list
     * @return An EventList containing all the events that matched from the startDate on.
     * @see Event
     */
    public EventList getRange(Calendar startDate) {
        long startMillis = startDate.getTimeInMillis();
        EventList subEvents = new EventList();
        for (Iterator i = eventSet.iterator(); i.hasNext(); ) {
            Event currEvent = (Event)i.next();
            if (currEvent.getEventCalendar().getTimeInMillis() >= startMillis) {
                subEvents.add(currEvent);
            }
        }
        return subEvents;
    }
    
    /**
     * Returns an iterator over the elements in this EventList. The elements are returned in 
     * order by <code>eventTime</code>, <code>eventName</code>, <code>eventDetail</code>.
     * @return an iterator over the elements in the EventList
     */
    public Iterator iterator() {
        return eventSet.iterator();
    }
    
    /**
     * Returns the <code>EventList</code> object as a string this would mostly be used for debugging purposes.
     * @return <code>String</code> representation of the object
     */
    public String toString() {
        StringBuffer retValue = new StringBuffer();
        retValue.append("[");
        int itemCount = 1;
        for (Iterator i = this.eventSet.iterator(); i.hasNext(); itemCount++) {
            if (itemCount == 1) {
                retValue.append(i.next().toString());
            } else {
                retValue.append(",\n").append(i.next().toString());
            }
        }
        retValue.append("]");
        return retValue.toString();
    }
}

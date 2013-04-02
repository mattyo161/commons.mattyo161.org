/*
 * Event.java
 *
 * Created on March 30, 2004, 11:34 PM
 */
package org.mattyo161.commons.cal;

import java.util.*;
import java.text.*;

/**
 * <code>Event</code> is a Class that is designed to handle general events for a Calendar. In a typical
 * calendar view you see a title for the event <code>EventName</code>, a time for the event <code>EventTime</code>
 * and the detail or description for the event <code>EventDetail</code>. These 3 basic elements are what
 * the user needs to understand what this Event is. There is also an <code>EventType</code> and 
 * <code>EventId</code> for reference to an object in a database, or some other repository.<br />
 * In its current incarnation <code>Event</code> is meant as a basic reference to a more in depth object
 * to make the representation of information in a calendar form much easer to handle.
 *
 * @author mattyo1
 * @version 1.0
 */
public class Event implements Comparable {
    private int eventType;
    private String eventId;
    private String eventName;
    private Date eventTime = new Date();  
    private String eventDetail;
    
    
    /** Creates a new instance of Event */
    public Event() {
    }
    
    /**
     * Creates a new instance of Event and sets the properties to the passed parameters.
     * @param eventType an <code>int</code> representing the type for this event in repository
     * @param eventId an <code>int</code> representing the id for this event in repository
     * @param eventTime a <code>java.lang.Date</code> representing the date and time the event is for
     * @param eventName a <code>String</code> for the name of the event to dispaly to hte user.
     * @param eventDetail a <code>String</code> with additional information to provide to the user
     */
    public Event(int eventType, String eventId, Date eventTime, String eventName, String eventDetail) {
        setEvent(eventType, eventId, new Cal(eventTime), eventName, eventDetail);
    }

    /**
     * Creates a new instance of Event and sets the properties to the passed parameters.
     * @param eventType an <code>int</code> representing the type for this event in repository
     * @param eventId an <code>int</code> representing the id for this event in repository
     * @param eventTime a <code>java.lang.Calendar</code> representing the date and time the event is for
     * @param eventName a <code>String</code> for the name of the event to dispaly to hte user.
     * @param eventDetail a <code>String</code> with additional information to provide to the user
     */
    public Event(int eventType, String eventId, Calendar eventTime, String eventName, String eventDetail) {
        setEvent(eventType, eventId, new Cal(eventTime), eventName, eventDetail);
    }

    /**
     * Creates a new instance of Event and sets the properties to the passed parameters.
     * @param eventType an <code>int</code> representing the type for this event in repository
     * @param eventId an <code>int</code> representing the id for this event in repository
     * @param eventTime a <code>org.mattyo161.commons.cal.Cal</code> representing the date and time the event is for
     * @param eventName a <code>String</code> for the name of the event to dispaly to hte user.
     * @param eventDetail a <code>String</code> with additional information to provide to the user
     */
    public Event(int eventType, String eventId, Cal eventTime, String eventName, String eventDetail) {
        setEvent(eventType, eventId, eventTime, eventName, eventDetail);
    }

    /**
     * Creates a new instance of Event and sets the properties to the passed parameters.
     * @param eventType an <code>int</code> representing the type for this event in repository
     * @param eventId an <code>int</code> representing the id for this event in repository
     * @param eventTime a <code>java.sql.Date</code> representing the date and time the event is for
     * @param eventName a <code>String</code> for the name of the event to dispaly to hte user.
     * @param eventDetail a <code>String</code> with additional information to provide to the user
     */
    public Event(int eventType, String eventId, java.sql.Date eventTime, String eventName, String eventDetail) {
        setEvent(eventType, eventId, new Cal(eventTime), eventName, eventDetail);
    }

    /**
     * Creates a new instance of Event and sets the properties to the passed parameters.
     * @param eventType an <code>int</code> representing the type for this event in repository
     * @param eventId an <code>int</code> representing the id for this event in repository
     * @param eventTime a <code>java.sql.Time</code> representing the date and time the event is for
     * @param eventName a <code>String</code> for the name of the event to dispaly to hte user.
     * @param eventDetail a <code>String</code> with additional information to provide to the user
     */
    public Event(int eventType, String eventId, java.sql.Time eventTime, String eventName, String eventDetail) {
        setEvent(eventType, eventId, new Cal(eventTime), eventName, eventDetail);
    }

    /**
     * Creates a new instance of Event and sets the properties to the passed parameters.
     * @param eventType an <code>int</code> representing the type for this event in repository
     * @param eventId an <code>int</code> representing the id for this event in repository
     * @param eventTime a <code>java.sql.Timestamp</code> representing the date and time the event is for
     * @param eventName a <code>String</code> for the name of the event to dispaly to hte user.
     * @param eventDetail a <code>String</code> with additional information to provide to the user
     */
    public Event(int eventType, String eventId, java.sql.Timestamp eventTime, String eventName, String eventDetail) {
        setEvent(eventType, eventId, new Cal(eventTime), eventName, eventDetail);
    }

    /**
     * Alters the current <code>Event</event> by setting the properties to the passed parameters.
     * @param eventType an <code>int</code> representing the type for this event in repository
     * @param eventId an <code>int</code> representing the id for this event in repository
     * @param eventTime a <code>org.mattyo161.commons.cal.Cal</code> representing the date and time the event is for
     * @param eventName a <code>String</code> for the name of the event to dispaly to hte user.
     * @param eventDetail a <code>String</code> with additional information to provide to the user
     */
    private void setEvent(int eventType, String eventId, Cal eventTime, String eventName, String eventDetail) {
        this.eventType = eventType;
        this.eventId = eventId;
        this.eventTime = eventTime.getTime();
        this.eventName = eventName;
        this.eventDetail = eventDetail;
    }
    
    /**
     * Creates a new instance of Event and sets the properties to the passed parameters.
     * @param eventId an <code>int</code> representing the id for this event in repository
     * @param eventTime a <code>java.sql.Timestamp</code> representing the date and time the event is for
     * @param eventName a <code>String</code> for the name of the event to dispaly to hte user.
     * @param eventDetail a <code>String</code> with additional information to provide to the user
     * @depricated
     */
    public Event(String eventId, Calendar eventTime, String eventName, String eventDetail) {
        this.eventType = 0;
        this.eventId = eventId;
        this.eventTime = eventTime.getTime();
        this.eventName = eventName;
        this.eventDetail = eventDetail;
    }
    
    /**
     * Returns the <code>Event</code> object as a string. Mostly used for debugging purposes, it simply
     * displays the properties in the following form:
     * <code>[<EventType, <EventId>, "<EventTime>", "<EventName>", "<EventDetail>"]</code>
     * @return <code>String</code> representation of the object
     */
    public String toString() {
        String retValue;
        
        retValue = "[" + this.eventType + 
                   "," + this.eventId + 
                   ",\"" + this.eventTime.toString() + "\"" +
                   ",\"" + this.eventName + "\"" + 
                   ",\"" + this.eventDetail + "\"" +
                   "]";
        return retValue;
    }
    
    /**
     * Compares this event to the specified object. The result is <code>true</code>
     * if and only if the argument is not <code>null</code> and
     * is an <code>Event</code> object that represents the same Event as
     * this object.
     * @param obj object to compare to
     * @throws ClassCastException If the <code>Object</code> parameter does not an <code>Evnet</code>
     * @return <code>true</code> if the objets are the same; <code>false</code> otherwise
     */    
    public boolean equals(Object obj) throws ClassCastException {
        boolean retValue = false;
        if (obj != null) {
            if (getClass() == obj.getClass()) {
                Event objEvent = (Event) obj;
                retValue = (this.eventType == objEvent.getEventType()) &&
                           this.eventId.equals(objEvent.getEventId()) &&
                           this.eventTime.equals(objEvent.getEventTime()) &&
                           this.eventName.equals(objEvent.getEventName()) &&
                           this.eventDetail.equals(objEvent.getEventDetail());
            } else {
                throw new ClassCastException("An Ojbect of Class " + getClass().getName() + " cannot be compared to an Object of Class " + obj.getClass().getName());
            }
        }
        return retValue;
    }
    
    /**
     * Compares this event to the specified object. The result is <code>true</code>
     * if and only if the argument is not <code>null</code> and
     * is an <code>Event</code> object that represents the same Event as
     * this object.
     * @param Event objEvent to compare to
     * @return <code>true</code> if the objets are the same; <code>false</code> otherwise
     */    
    public boolean equals(Event objEvent) {
        boolean retValue = false;
        if (objEvent != null) {
            retValue = (this.eventType == objEvent.getEventType()) &&
                       (this.eventId == objEvent.getEventId()) &&
                       this.eventTime.equals(objEvent.getEventTime()) &&
                       this.eventName.equals(objEvent.getEventName()) &&
                       this.eventDetail.equals(objEvent.getEventDetail());
        }
        return retValue;
    }
    
    /**
     * Override hashCode. Generates the hash code for the Event object
     */
    public int hashCode() {
        int retValue = 0;
        retValue += this.eventType;
        retValue += this.eventId.hashCode() / 3;
        if (this.eventTime != null) retValue += this.eventTime.hashCode() / 3;
        if (this.eventName != null) retValue += this.eventName.hashCode() / 3;
        if (this.eventDetail != null) retValue += this.eventDetail.hashCode() / 3;
        
        retValue = super.hashCode();
        return retValue;
    }
    
    /**
     * Compares this event to the specified object. The object must be an <code>Event</code> ojbect
     * if not a <code>ClassCastException</code> is thrown. 
     * @param obj object to compare to
     * @throws ClassCastException If the <code>Object</code> parameter does not implement the <code>Calendar</code> interface
     * @return the value 0 if the two objects are equal;<br />
     * a value less than 0 if the argument is a <code>Calendar</code> less then this <code>Calendar</code>;<br />
     * and a value greater than 0 if the argument is a <code>Calendar</code> less then this <code>Calendar</code>.
     */    
    public int compareTo(Object obj) throws ClassCastException {
        int retValue = -1;
        if (obj != null) {
            if (getClass() == obj.getClass()) {
                Event objEvent = (Event) obj;
                retValue = this.eventTime.compareTo(objEvent.getEventTime());
                if (retValue == 0) retValue = this.eventName.compareTo(objEvent.getEventName());
                if (retValue == 0) retValue = this.eventDetail.compareTo(objEvent.getEventDetail());
                if (retValue == 0) retValue = objEvent.getEventType() - this.eventType;
                if (retValue == 0) retValue = this.eventId.compareTo(objEvent.getEventId());
            } else {
                throw new ClassCastException("An Ojbect of Class " + getClass().getName() + " cannot be compared to an Object of Class " + obj.getClass().getName());
            }
        }
        return retValue;
    }

    /**
     * Compares this event to the specified Event.
     * @param objEvent object to compare to
     * @throws ClassCastException If the <code>Object</code> parameter does not implement the <code>Calendar</code> interface
     * @return the value 0 if the two objects are equal;<br />
     * a value less than 0 if the argument is a <code>Calendar</code> less then this <code>Calendar</code>;<br />
     * and a value greater than 0 if the argument is a <code>Calendar</code> less then this <code>Calendar</code>.
     */    
    public int compareTo(Event objEvent) {
        int retValue = -1;
        if (objEvent != null) {
            retValue = this.eventTime.compareTo(objEvent.getEventTime());
            if (retValue == 0) retValue = this.eventName.compareTo(objEvent.getEventName());
            if (retValue == 0) retValue = this.eventDetail.compareTo(objEvent.getEventDetail());
            if (retValue == 0) retValue = objEvent.getEventType() - this.eventType;
            if (retValue == 0) retValue = this.eventId.compareTo(objEvent.getEventId());
        }
        return retValue;
    }

    /** Getter for property eventType.
     * @return Value of property eventType.
     *
     */
    public int getEventType() {
        return eventType;
    }
    
    /** Setter for property eventType.
     * @param eventType New value of property eventType.
     *
     */
    public void setEventType(int eventType) {
        this.eventType = eventType;
    }
    /**
     * Getter for property eventTime.
     * @return Value of property eventTime.
     */
    public Date getEventTime() {
        return (Date) this.eventTime.clone();
    }
    
    /**
     * Getter for property eventTime.
     * @return Value of property eventTime as a <code>Calendar</code>
     */
    public Calendar getEventCalendar() {
        Calendar retValue = Calendar.getInstance();
        retValue.setTime(this.eventTime);
        return retValue;
    }
    
    /**
     * Getter for property eventTime.
     * @return Value of property eventTime as a <code>Cal</code>
     */
    public Cal getEventCal() {
        Cal retValue = new Cal(this.eventTime);
        return retValue;
    }
    
    /**
     * Setter for property eventTime.
     * @param eventTime New value of property eventTime.
     */
    public void setEventTime(java.util.Calendar eventTime) {
        this.eventTime = eventTime.getTime();
    }
    
    /**
     * Setter for property eventTime, using a <code>java.util.Date</code> object.
     * @param eventTime New value of property eventTime.
     */
    public void setEventTime(java.util.Date eventTime) {
        this.eventTime = (Date)eventTime.clone();
    }
    
    /**
     * Setter for property eventTime, using a <code>org.mattyo161.commons.cal.Cal</code> object.
     * @param eventTime New value of property eventTime.
     */
    public void setEventTime(Cal eventTime) {
        this.eventTime = eventTime.getTime();
    }
    
    /**
     * Setter for property eventTime, using a <code>java.sql.Date</code> object.
     * @param eventTime New value of property eventTime.
     */
    public void setEventTime(java.sql.Date eventTime) {
        this.eventTime = new Cal(eventTime).getTime();
    }
    
    /**
     * Setter for property eventTime, using a <code>java.sql.Time</code> object.
     * @param eventTime New value of property eventTime.
     */
    public void setEventTime(java.sql.Time eventTime) {
        this.eventTime = new Cal(eventTime).getTime();
    }

    /**
     * Setter for property eventTime, using a <code>java.sql.Timestamp</code> object.
     * @param eventTime New value of property eventTime.
     */
    public void setEventTime(java.sql.Timestamp eventTime) {
        this.eventTime = new Cal(eventTime).getTime();
    }
    
    /**
     * Getter for property eventName.
     * @return Value of property eventName.
     */
    public java.lang.String getEventName() {
        return eventName;
    }
    
    /**
     * Setter for property eventName.
     * @param eventName New value of property eventName.
     */
    public void setEventName(java.lang.String eventName) {
        this.eventName = eventName;
    }
    
    /**
     * Getter for property eventId.
     * @return Value of property eventId.
     */
    public String getEventId() {
        return eventId;
    }
    
    /**
     * Setter for property eventId.
     * @param eventId New value of property eventId.
     */
    public void setEventId(String eventId) {
        this.eventId = eventId;
    }
    
    /**
     * Getter for property eventDetail.
     * @return Value of property eventDetail.
     */
    public java.lang.String getEventDetail() {
        return eventDetail;
    }
    
    /**
     * Setter for property eventDetail.
     * @param eventDetail New value of property eventDetail.
     */
    public void setEventDetail(java.lang.String eventDetail) {
        this.eventDetail = eventDetail;
    }
       
}

package ru.shemplo.fitness.turnstile.db;

import java.util.Date;

public class Event {

    private String objectClass;

    private int objectId;

    private String propertyName;

    private String propertyAction;

    private String propertyValue;

    private Date date;

    public Event(String objectClass, int objectId, String propertyName, String propertyAction, String propertyValue, Date date) {
        this.objectClass = objectClass;
        this.objectId = objectId;
        this.propertyName = propertyName;
        this.propertyAction = propertyAction;
        this.propertyValue = propertyValue;
        this.date = null;
    }

    public String getObjectClass() {
        return objectClass;
    }

    public int getObjectId() {
        return objectId;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public String getPropertyAction() {
        return propertyAction;
    }

    public String getPropertyValue() {
        return propertyValue;
    }

    public Date getDate() {
        return date;
    }

    @Override
    public String toString() {
        return "Event{" +
                "objectClass='" + objectClass + '\'' +
                ", objectId=" + objectId +
                ", propertyName='" + propertyName + '\'' +
                ", propertyAction='" + propertyAction + '\'' +
                ", propertyValue='" + propertyValue + '\'' +
                ", date=" + date +
                '}';
    }
}

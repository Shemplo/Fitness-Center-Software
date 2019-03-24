package ru.shemplo.fitness.entities;

import java.util.Date;

public interface Updatable {
    
    public void setLastTimeUpdated (Date dateTime);
    
    public Date getLastTimeUpdated ();
    
}

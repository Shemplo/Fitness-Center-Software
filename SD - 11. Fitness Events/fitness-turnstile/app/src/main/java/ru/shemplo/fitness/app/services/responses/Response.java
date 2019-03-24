package ru.shemplo.fitness.app.services.responses;

public interface Response <T, E extends Exception> {

    public T getResult ();

    default public boolean hasResult () {
        return getResult () != null;
    }

    public E getException ();

    default public boolean hasException () {
        return getException () != null;
    }

}

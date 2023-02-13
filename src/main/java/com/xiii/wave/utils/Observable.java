package com.xiii.wave.utils;

import java.util.HashSet;
import java.util.Set;

/*
 * Credits to Frequency, taken from their Observable class, including their BoundingBox class.
 */

public final class Observable<T> {

    private final Set<ChangeObserver<T>> observers = new HashSet<>();
    private T value;

    public Observable(final T initValue) {
        this.value = initValue;
    }

    public T get() {
        return value;
    }

    public void set(final T value) {

        final T oldValue = this.value;
        this.value = value;
        observers.forEach(it -> it.handle(oldValue, value));

    }


    public ChangeObserver<T> observe(final ChangeObserver<T> onChange) {

        observers.add(onChange);

        return onChange;

    }

    public void unobserve(final ChangeObserver<T> onChange) {
        observers.remove(onChange);
    }

    @FunctionalInterface
    public interface ChangeObserver<T> {

        void handle(final T from, final T to);

    }
}

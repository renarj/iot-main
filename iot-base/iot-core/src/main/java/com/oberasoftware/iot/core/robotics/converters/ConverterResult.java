package com.oberasoftware.iot.core.robotics.converters;

import java.util.ArrayList;
import java.util.List;

public class ConverterResult<T> {
    private final List<T> results = new ArrayList<>();

    public ConverterResult(T result) {
        this.results.add(result);
    }

    public ConverterResult(List<T> results) {
        this.results.addAll(results);
    }

    public boolean isCollection() {
        return this.results.size() > 1;
    }

    public boolean isEmpty() {
        return this.results.isEmpty();
    }

    public T getResult() {
        if(!isEmpty()) {
            return this.results.get(0);
        } else {
            return null;
        }
    }

    public List<T> getResults() {
        return this.results;
    }
}

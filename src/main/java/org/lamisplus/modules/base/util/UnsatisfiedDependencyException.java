package org.lamisplus.modules.base.util;

public class UnsatisfiedDependencyException extends RuntimeException {
    public UnsatisfiedDependencyException(String message) {
        super(message);
    }
}

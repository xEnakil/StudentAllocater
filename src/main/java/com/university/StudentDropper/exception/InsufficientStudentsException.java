package com.university.StudentDropper.exception;

public class InsufficientStudentsException  extends RuntimeException{
    public InsufficientStudentsException(String message) {
        super(message);
    }
}

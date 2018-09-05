package com.etnetera.hr.controller.exception;

public class FrameworkNotFoundException extends Throwable {
    private Long id;

    public FrameworkNotFoundException(Long id) {
        this.id = id;
    }

    @Override
    public String getMessage() {
        return "Framework " + id + " not found.";
    }
}

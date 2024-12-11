package com.web.config;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class MessageException extends RuntimeException {

    private int errorCode;

    private String errorMessage;

    public MessageException(Throwable throwable) {
        super(throwable);
    }

    public MessageException(String msg, Throwable throwable) {
        super(msg, throwable);
    }

    public MessageException(String msg) {
        super(msg);
    }

    public MessageException(String message, int errorCode) {
        super();
        this.errorCode = errorCode;
        this.errorMessage = message;
    }


    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    @Override
    public void setStackTrace(StackTraceElement[] stackTrace) {
        System.out.println("");
    }

    @JsonIgnore
    @Override
    public StackTraceElement[] getStackTrace() {
        return super.getStackTrace();
    }

    @Override
    public String toString() {
        return this.errorCode + " : " + this.getErrorMessage();
    }
}


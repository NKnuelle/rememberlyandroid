package de.rememberly.rememberlyandroidapp.model;

public class HttpResponse {
    public HttpResponse() {

    }
    public HttpResponse(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    private String message;
}

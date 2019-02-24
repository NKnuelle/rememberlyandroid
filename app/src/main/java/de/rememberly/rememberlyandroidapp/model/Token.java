package de.rememberly.rememberlyandroidapp.model;

public class Token extends HttpResponse {

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    private String token;
}

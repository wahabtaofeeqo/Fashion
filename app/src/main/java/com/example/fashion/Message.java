package com.example.fashion;

public class Message {

    private String email;
    private String sub;
    private String message;

    public Message(String email, String sub, String message) {
        this.email = email;
        this.sub = sub;
        this.message = message;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSub() {
        return sub;
    }

    public void setSub(String sub) {
        this.sub = sub;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

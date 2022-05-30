package com.example.android.callingapp.DataObjects;

import androidx.annotation.Nullable;

public class Message {
    private String message;
    private String author;
    private String timeStamp;

    public Message(){}

    @Override
    public boolean equals(@Nullable Object obj) {
        Message msg=(Message) obj;

        return super.equals(obj);
    }

    public Message(String message, String author, String timeStamp) {
        this.message = message;
        this.author = author;
        this.timeStamp = timeStamp;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTimeStamp() {
        return timeStamp;
    }
}

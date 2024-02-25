package com.example.myapplication;

public class User {
    private int id;
    private String name;

    private String AI;
    private String email;

    // Constructor
    public User(int id, String AI, String email) {
        this.AI = AI;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return AI;
    }

    public void setName(String AI) {
        this.AI = AI;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "You: bow\nAI: " + AI;
    }
}

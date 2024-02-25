package com.example.myapplication;

public class Data {
    private String Input = "";
    private String Output = "";

    // Constructor
    public Data(String Input, String Output) {
        this.Input = Input;
        this.Output = Output;
    }

    @Override
    public String toString() {
        return "\nAI:" + Output;
    }
}

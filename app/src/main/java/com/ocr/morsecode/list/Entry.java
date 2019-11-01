package com.ocr.morsecode.list;

import androidx.annotation.NonNull;

import java.io.Serializable;

public class Entry implements Serializable {

    private int id;
    private String inputText;
    private String outputText;

    public Entry() {
    }

    Entry(String inputText, String outputText) {
        super();
        this.inputText = inputText;
        this.outputText = outputText;
    }

    @NonNull
    @Override
    public String toString() {
        return "Entry: [id=" + id + ", inputText=" + inputText + ", outputText=" + outputText + " ]";
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    String getInputText() {
        return inputText;
    }

    String getOutputText() {
        return outputText;
    }
}

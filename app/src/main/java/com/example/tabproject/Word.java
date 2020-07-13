package com.example.tabproject;

public class Word {
    private String k_word;
    private String e_word;

    public Word(String k_word, String e_word) {
        this.k_word = k_word;
        this.e_word = e_word;
    }

    public String getK_word() {
        return k_word;
    }

    public void setK_word(String k_word) {
        this.k_word = k_word;
    }

    public String getE_word() {
        return e_word;
    }

    public void setE_word(String e_word) {
        this.e_word = e_word;
    }
}
package com.example.tabproject;

public class WordList {
    private String wordlist;
    private int wordlist_id;

    public WordList(String wordlist, int wordlist_id) {
        this.wordlist = wordlist;
        this.wordlist_id = wordlist_id;
    }

    public int getWordlist_id() {
        return wordlist_id;
    }

    public void setWordlist_id(int wordlist_id) {
        this.wordlist_id = wordlist_id;
    }

    public String getWordlist() {
        return wordlist;
    }

    public void setWordlist(String wordlist) {
        this.wordlist = wordlist;
    }
}

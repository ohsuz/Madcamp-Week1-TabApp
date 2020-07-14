package com.example.tabproject;

public class WordList {
    private String wordlist;
    private String lan;
    private int wordlist_id;

    public WordList(String wordlist, String lan, int wordlist_id) {
        this.wordlist = wordlist;
        this.lan = lan;
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

    public String getLan() {
        return lan;
    }

    public void setLan(String lan) {
        this.lan = lan;
    }
}

package com.example.quotology;

public class QuotesData {
    String name, quote, key;


    public QuotesData(){

    }

    public QuotesData(String name, String quote, String key) {
        this.name = name;
        this.quote = quote;
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getQuote() {
        return quote;
    }

    public void setQuote(String quote) {
        this.quote = quote;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}

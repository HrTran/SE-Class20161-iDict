package com.example.hembit.idict.Model;

/**
 * Created by hembit on 20/01/2017.
 */

public class History {
    private String word;
    private int access_time;

    public History(String word, int access_time) {
        this.word= word;
        this.access_time= access_time;
    }

    public String getWord() { return word; }

    public void setWord(String word) { this.word = word; }

    public int getAccess_time() { return access_time; }

    public void setAccess_time(int access_time) { this.access_time = access_time; }
}

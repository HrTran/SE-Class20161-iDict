package com.example.hembit.idict.Model;

/**
 * Created by hembit on 23/12/2016.
 */

public class Word {
    private int wordId;
    private String word_pronounce;
    private String word_meaning;
    private String word_text;

    public Word(int wordId, String word_pronounce,String word_meaning ,String word_text) {
        this.wordId= wordId;
        this.word_pronounce= word_pronounce;
        this.word_meaning= word_meaning;
        this.word_text= word_text;
    }

    public int getWordIdId() { return wordId; }

    public void setWordIdId(int wordId) { this.wordId = wordId; }

    public String getWord_pronounce() { return word_pronounce; }

    public void setWord_pronounce(String word_pronounce) { this.word_pronounce = word_pronounce; }

    public String getWord_meaning() { return word_meaning; }

    public void setWord_meaning(String word_meaning) { this.word_meaning = word_meaning; }

    public String getWord_text() { return word_text; }

    public void setWord_text(String word_text) { this.word_text = word_text; }
}

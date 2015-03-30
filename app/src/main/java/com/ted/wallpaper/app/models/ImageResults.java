package com.ted.wallpaper.app.models;

import java.util.ArrayList;

public class ImageResults {

    private ArrayList<Image> results;

    public ImageResults() {
    }

    public ImageResults(ArrayList<Image> results) {
        this.results = results;
    }

    public ArrayList<Image> getResults() {
        return results;
    }

    public void setResults(ArrayList<Image> results) {
        this.results = results;
    }
}

package com.example.rohit.musicapp;

public class SongObject {

    private String mFileName;
    private String mAbsolutePath;

    SongObject(String fileName, String absolutePath) {
        this.mFileName = fileName;
        this.mAbsolutePath =absolutePath;
    }

    public String getmFileName() {
        return mFileName;
    }

    public void setmFileName(String mFileName) {
        this.mFileName = mFileName;
    }

    public String getmAbsolutePath() {
        return mAbsolutePath;
    }

    public void setmAbsolutePath(String mAbsolutePath) {
        this.mAbsolutePath = mAbsolutePath;
    }
}

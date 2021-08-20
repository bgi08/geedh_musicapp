package com.example.vennamusic;

import android.net.Uri;

public class MusicList {
    String title,artist,duration;
    boolean isplaying;
    Uri musicfile;

    public MusicList(String title, String artist, String duration, boolean isplaying,Uri musicfile) {
        this.title = title;
        this.artist = artist;
        this.duration = duration;
        this.isplaying = isplaying;
        this.musicfile=musicfile;
    }

    public String getTitle() {
        return title;
    }

    public String getArtist() {
        return artist;
    }

    public String getDuration() {
        return duration;
    }

    public boolean Isplaying() {
        return isplaying;
    }

    public Uri getMusicfile() {
        return musicfile;
    }

    public void setIsplaying(boolean isplaying) {
        this.isplaying = isplaying;
    }
}

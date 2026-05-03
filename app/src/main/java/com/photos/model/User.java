package com.photos.model;

import java.util.ArrayList;
import java.util.List;
import java.io.Serializable;

public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    private String username;
    private List<Album> albums;

    public User(String username) {
        this.username = username;
        this.albums   = new ArrayList<>();
    }

    public String      getUsername() { return username; }
    public List<Album> getAlbums()   { return albums; }

    public void addAlbum(Album album) {
        if (!albums.contains(album)) albums.add(album);
    }

    public void removeAlbum(Album album) {
        albums.remove(album);
    }

    public Album getAlbumByName(String name) {
        for (Album a : albums) {
            if (a.getName().equalsIgnoreCase(name)) return a;
        }
        return null;
    }

    @Override
    public String toString() { return username; }
}

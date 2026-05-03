package com.photos.model;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.io.Serializable;

public class Album implements Serializable {
    private static final long serialVersionUID = 1L;

    private String name;
    private List<Photo> photos;

    public Album(String name) {
        this.name = name;
        this.photos = new ArrayList<>();
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public List<Photo> getPhotos() { return photos; }

    public void addPhoto(Photo photo) {
        if (!photos.contains(photo)) photos.add(photo);
    }

    public void removePhoto(Photo photo) {
        photos.remove(photo);
    }

    public Calendar getEarliestDate() {
        if (photos.isEmpty()) return null;
        Calendar earliest = (Calendar) photos.get(0).getDateTaken().clone();
        for (Photo p : photos) {
            if (p.getDateTaken().before(earliest))
                earliest = (Calendar) p.getDateTaken().clone();
        }
        return earliest;
    }

    public Calendar getLatestDate() {
        if (photos.isEmpty()) return null;
        Calendar latest = (Calendar) photos.get(0).getDateTaken().clone();
        for (Photo p : photos) {
            if (p.getDateTaken().after(latest))
                latest = (Calendar) p.getDateTaken().clone();
        }
        return latest;
    }

    public int getPhotoCount() { return photos.size(); }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Album)) return false;
        Album other = (Album) o;
        return name.equalsIgnoreCase(other.name);
    }

    @Override
    public String toString() { return name; }
}

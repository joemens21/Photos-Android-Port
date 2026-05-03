package com.photos.model;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.io.Serializable;

public class Photo implements Serializable {
    private static final long serialVersionUID = 1L;

    private String filePath;
    private String caption;
    private Calendar dateTaken;
    private List<Tag> tags;

    public Photo(String filePath, String caption, Calendar dateTaken) {
        this.filePath  = filePath;
        this.caption   = caption;
        this.dateTaken = (Calendar) dateTaken.clone();
        this.dateTaken.set(Calendar.MILLISECOND, 0);
        this.tags      = new ArrayList<>();
    }

    public String   getFilePath()  { return filePath; }
    public String   getCaption()   { return caption; }
    public void     setCaption(String caption) { this.caption = caption; }
    public Calendar getDateTaken() { return (Calendar) dateTaken.clone(); }
    public List<Tag> getTags()     { return tags; }

    /** Returns just the filename part of the path (used as display name). */
    public String getFileName() {
        int slash = filePath.lastIndexOf('/');
        return slash >= 0 ? filePath.substring(slash + 1) : filePath;
    }

    public void addTag(Tag tag) {
        if (!tags.contains(tag)) tags.add(tag);
    }

    public void removeTag(Tag tag) {
        tags.remove(tag);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Photo)) return false;
        Photo other = (Photo) o;
        return filePath.equals(other.filePath);
    }

    @Override
    public int hashCode() { return filePath.hashCode(); }
}

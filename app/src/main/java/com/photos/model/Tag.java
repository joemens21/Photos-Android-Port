package com.photos.model;

import java.util.Objects;
import java.io.Serializable;

public class Tag implements Serializable {
    private static final long serialVersionUID = 1L;

    public static final String TYPE_PERSON   = "person";
    public static final String TYPE_LOCATION = "location";

    private String type;
    private String value;

    public Tag(String type, String value) {
        this.type  = type;
        this.value = value;
    }

    public String getType()  { return type; }
    public String getValue() { return value; }

    @Override
    public String toString() { return type + "=" + value; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Tag)) return false;
        Tag tag = (Tag) o;
        return type.equalsIgnoreCase(tag.type) &&
               value.equalsIgnoreCase(tag.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type.toLowerCase(), value.toLowerCase());
    }
}

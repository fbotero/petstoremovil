package com.mocatto.dto;

import java.io.Serializable;

/**
 * Created by froilan.ruiz on 7/1/2016.
 */
public class Data implements Serializable {
    private String id;
    private String name;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Data{" +
                "id='" + id + '\'' +
                ", description='" + name + '\'' +
                '}';
    }
}

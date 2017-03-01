package com.mocatto.dto;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * Created by froilan.ruiz on 6/7/2016.
 */
public class Topic implements Serializable {
    private Integer id;
    private String title;
    private String description;
    private String author;
    private Timestamp date;
    private Integer likes;
    private Integer specieId;

    public Topic(Integer id,String title, String description, String author, Timestamp date, Integer likes, Integer specieId) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.author = author;
        this.date = date;
        this.likes = likes;
        this.specieId = specieId;
    }

    public Topic() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public Timestamp getDate() {
        return date;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }

    public Integer getLikes() {
        return likes;
    }

    public void setLikes(Integer likes) {
        this.likes = likes;
    }

    public Integer getSpecieId() {
        return specieId;
    }

    public void setSpecieId(Integer specieId) {
        this.specieId = specieId;
    }
}

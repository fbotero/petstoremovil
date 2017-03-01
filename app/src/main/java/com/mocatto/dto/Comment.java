package com.mocatto.dto;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by froilan.ruiz on 6/7/2016.
 */
public class Comment implements Serializable {
    private Integer id;
    private String name;
    private Date date;
    private Integer topic;
    private Integer likes;


    public Comment(Integer id, String name, Date date, Integer topic, Integer likes) {
        this.id = id;
        this.name = name;
        this.date = date;
        this.topic = topic;
        this.likes = likes;
    }

    public Comment() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Integer getTopic() {
        return topic;
    }

    public void setTopic(Integer topic) {
        this.topic = topic;
    }

    public Integer getLikes() {
        return likes;
    }

    public void setLikes(Integer likes) {
        this.likes = likes;
    }
}
